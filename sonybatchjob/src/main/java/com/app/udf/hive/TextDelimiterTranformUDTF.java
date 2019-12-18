package com.app.udf.hive;

import org.apache.hadoop.hive.ql.exec.Description;
import org.apache.hadoop.hive.ql.exec.UDFArgumentException;
import org.apache.hadoop.hive.ql.metadata.HiveException;
import org.apache.hadoop.hive.ql.udf.generic.GenericUDTF;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspectorFactory;
import org.apache.hadoop.hive.serde2.objectinspector.PrimitiveObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.StructObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.PrimitiveObjectInspectorFactory;

import java.util.ArrayList;
import java.util.List;


@Description(
        name = "TextDelimiterTranformUDTF",
        value = "_FUNC_(arg1, arg2, ... argN) - A short description for the function",
        extended = "This is more detail about the function, such as syntax,  examples."
)
public class TextDelimiterTranformUDTF extends GenericUDTF {

    private PrimitiveObjectInspector stringOI = null;
    private String delimiter = "\\|";
    /**
     * This method will be called exactly once per instance.
     * It performs any custom initialization logic we need.
     * It is also responsible for verifying the input types and
     * specifying the output types.
     */
    @Override
    public StructObjectInspector initialize(ObjectInspector[] objectInspectors) throws UDFArgumentException {
        //Check number of arguments.
        if (!(objectInspectors.length == 1||objectInspectors.length ==2)) {
            throw new UDFArgumentException("The UDTF should take exactly one argument");
        }

        if(objectInspectors.length ==2){
            delimiter = (String)stringOI.getPrimitiveJavaObject((PrimitiveObjectInspector)objectInspectors[1]);
            //validate delimiter
            switch (delimiter){
                case "\\|":
                case ";":
                case ",":
                case ":":
                default:
                    throw new UDFArgumentException("The UDTF should take exactly one argument");
            }
        }

        /*
        * Check that the input ObjectInspector[] array contains a
        * single PrimitiveObjectInspector of the Primitive type,
        * such as String.
         */
        if (objectInspectors[0].getCategory() != ObjectInspector.Category.PRIMITIVE
                &&
                ((PrimitiveObjectInspector) objectInspectors[0]).getPrimitiveCategory() !=
                        PrimitiveObjectInspector.PrimitiveCategory.STRING) {
            throw new UDFArgumentException("The UDTF should take a string as a parameter");
        }


        stringOI = (PrimitiveObjectInspector) objectInspectors[0];
        /*
         * Define the expected output for this function, including
         * each alias and types for the aliases.
         */
        List<String> fieldNames = new ArrayList<String>(1);
        List<ObjectInspector> fieldOIs = new ArrayList<ObjectInspector>(1);
        fieldNames.add("text_value");
        fieldOIs.add(PrimitiveObjectInspectorFactory.javaStringObjectInspector);

        //Set up the output schema.
        return  ObjectInspectorFactory.getStandardStructObjectInspector(fieldNames,
                        fieldOIs);
    }

    /**
     * This method is called once per input row and generates
     * output. The "forward" method is used (instead of
     * "return") in order to specify the output from the function.
     */
    @Override
    public void process(Object[] record) throws HiveException {
        /*
         * We may need to convert the object to a primitive type
         * before implementing customized logic.
         */
        final String recStr = (String)stringOI.getPrimitiveJavaObject(record[0]);
        //emit newly created structs after applying customized logic.
        String[] recs = recStr.split(delimiter);
        for(String rs : recs){
            forward(new Object[]{rs});
        }

    }

    /**
     * This method is for any cleanup that is necessary before
     * returning from the UDTF. Since the output stream has
     * already been closed at this point, this method cannot
     * emit more rows.
     */
    @Override
    public void close() throws HiveException {
        //Do nothing.
    }
}
