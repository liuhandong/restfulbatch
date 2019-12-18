package com.app.udf.hive;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.hive.ql.exec.UDFArgumentTypeException;
import org.apache.hadoop.hive.ql.metadata.HiveException;
import org.apache.hadoop.hive.ql.parse.SemanticException;
import org.apache.hadoop.hive.ql.udf.generic.AbstractGenericUDAFResolver;
import org.apache.hadoop.hive.ql.udf.generic.GenericUDAFEvaluator;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.PrimitiveObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.PrimitiveObjectInspectorFactory;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.PrimitiveObjectInspectorUtils;
import org.apache.hadoop.hive.serde2.typeinfo.PrimitiveTypeInfo;
import org.apache.hadoop.hive.serde2.typeinfo.TypeInfo;
import org.apache.hadoop.io.Text;

/**
 * a UDAF class for
 * concat multi rows parameter to string
 * after sql group by
 */
public class SQLRegxStatementUDAF extends AbstractGenericUDAFResolver {

    static final Log LOG = LogFactory.getLog(SQLRegxStatementUDAF.class.getName());

    static final String CONST_DELIMITER = "#@@#";
    static final String CONST_LNLIMITER = "#!!#";

    @Override
    public GenericUDAFEvaluator getEvaluator(TypeInfo[] parameters)
            throws SemanticException {
        if (parameters.length != 5) {
            throw new UDFArgumentTypeException(parameters.length - 1,
                    "Exactly one argument is expected.");
        }

        if (parameters[0].getCategory() != ObjectInspector.Category.PRIMITIVE) {
            throw new UDFArgumentTypeException(0,
                    "Only primitive type arguments are accepted but "
                            + parameters[0].getTypeName() + " is passed.");
        }
        for(int i=0;i<parameters.length;i++)
        if(((PrimitiveTypeInfo)parameters[i]).getPrimitiveCategory() != PrimitiveObjectInspector.PrimitiveCategory.STRING) {
            throw new UDFArgumentTypeException(i,"The parameter must be string.");
        }
        switch (((PrimitiveTypeInfo) parameters[0]).getPrimitiveCategory()) {
            case BYTE:
            case SHORT:
            case INT:
            case LONG:
            case FLOAT:
            case DOUBLE:
            case STRING:
            case TIMESTAMP:
                return new ConcatUDAFEvaluator();
            case BOOLEAN:
            default:
                throw new UDFArgumentTypeException(0,
                        "Only numeric or string type arguments are accepted but "
                                + parameters[0].getTypeName() + " is passed.");
        }
    }

    /**
     * a class for
     * concat parameter to a string
     * as `1#@@#1#@@#item_name#@@#regex#@@#.*(test2|valid0).*`
     */
    public static class ConcatUDAFEvaluator extends GenericUDAFEvaluator {

        private PrimitiveObjectInspector stringinputOI;
        private PrimitiveObjectInspector partialAggOI;
        private Text result;

        /**
         * Determine the data format of input and output parameters of each stage objectinspectors
         * @param mode
         * @param parameters
         * @return
         * @throws HiveException
         */
        @Override
        public ObjectInspector init(Mode mode, ObjectInspector[] parameters)
                throws HiveException {
            super.init(mode, parameters);
            if(mode == Mode.PARTIAL1 || mode == Mode.COMPLETE) {
                // init input
                stringinputOI = (PrimitiveObjectInspector) parameters[0];
            }
            if(mode == Mode.PARTIAL2 || mode == Mode.FINAL){
                partialAggOI = (PrimitiveObjectInspector)parameters[0];
            }
            result = new Text("");
            return PrimitiveObjectInspectorFactory.writableStringObjectInspector;
        }

        static class ConcatAgg extends AbstractAggregationBuffer {
            StringBuilder line = new StringBuilder("");
        }

        @Override
        public ConcatAgg getNewAggregationBuffer() throws HiveException {
            ConcatAgg result = new ConcatAgg();
            reset(result);
            return result;
        }

        @Override
        public void reset(AggregationBuffer agg) throws HiveException {
            ConcatAgg myagg = (ConcatAgg) agg;
            myagg.line.delete(0, myagg.line.length());
        }

        boolean warned = false;

        /**
         * In the map phase, the column data from the input SQL is processed iteratively
         * @param agg
         * @param parameters
         * @throws HiveException
         */
        @Override
        public void iterate(AggregationBuffer agg, Object[] parameters) throws HiveException {
            ConcatAgg myagg = (ConcatAgg) agg;
            String value = getConcatString(parameters);
            if (myagg.line.length() == 0) {
                myagg.line.append(value);
            } else {
                myagg.line.append(CONST_LNLIMITER + value);
            }
        }

        /**
         * concat parameters to a string
         * as `1#@@#1#@@#item_name#@@#regex#@@#.*(test2|valid0).*`
         * @param parameters
         * @return
         */
        private String getConcatString(Object[] parameters) {

            Object p0 = parameters[0];
            Object p1 = parameters[1];
            Object p2 = parameters[2];
            Object p3 = parameters[3];
            Object p4 = parameters[4];

            String conditionGroupNo = PrimitiveObjectInspectorUtils.getString(p0, stringinputOI);
            String orConditionGroupNo = PrimitiveObjectInspectorUtils.getString(p1, stringinputOI);
            String itemName = PrimitiveObjectInspectorUtils.getString(p2, stringinputOI);
            String decisionMethod = PrimitiveObjectInspectorUtils.getString(p3, stringinputOI);
            String decisionValue = PrimitiveObjectInspectorUtils.getString(p4, stringinputOI);

            // concat string for
            if (conditionGroupNo == null) {
                conditionGroupNo = "0";
            }
            if (orConditionGroupNo == null) {
                orConditionGroupNo = "0";
            }

            StringBuffer value = new StringBuffer();
            value.append(conditionGroupNo);
            value.append(CONST_DELIMITER);
            value.append(orConditionGroupNo);
            value.append(CONST_DELIMITER);
            value.append(itemName);
            value.append(CONST_DELIMITER);
            value.append(decisionMethod);
            value.append(CONST_DELIMITER);
            value.append(decisionValue);
            return value.toString();
        }

        /**
         * After map and combiner are finished, some data aggregation results are obtained
         * @param agg
         * @return
         * @throws HiveException
         */
        @Override
        public Object terminatePartial(AggregationBuffer agg) throws HiveException {
            ConcatAgg myagg = (ConcatAgg) agg;
            result.set(myagg.line.toString());

            return result;
        }

        /**
         * The result of combiner merging map, and the result of reducer merging mapper or combiner.
         * @param agg
         * @param partial
         * @throws HiveException
         */
        @Override
        public void merge(AggregationBuffer agg, Object partial) throws HiveException {
            if (partial != null) {
               ConcatAgg myagg = (ConcatAgg) agg;
               String v = PrimitiveObjectInspectorUtils.getString(partial, partialAggOI);
               if (myagg.line.length() == 0) {
                   myagg.line.append(v);
               }else {
                   myagg.line.append(CONST_LNLIMITER + v);
               }
            }
        }

        /**
         * Reducer stage, output the final result
         * @param agg
         * @return
         * @throws HiveException
         */
        @Override
        public Object terminate(AggregationBuffer agg) throws HiveException {
            ConcatAgg myagg = (ConcatAgg) agg;
            result.set(myagg.line.toString());
            return result;
        }
    }
}
