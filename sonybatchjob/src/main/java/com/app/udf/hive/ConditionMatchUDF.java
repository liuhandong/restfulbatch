package com.app.udf.hive;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hive.ql.exec.UDF;
import org.apache.hadoop.hive.ql.io.orc.OrcFile;
import org.apache.hadoop.hive.ql.io.orc.Reader;
import org.apache.hadoop.hive.ql.io.orc.RecordReader;
import org.apache.hadoop.hive.serde2.objectinspector.StructObjectInspector;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.IOException;
import java.net.URI;
import java.util.*;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class ConditionMatchUDF extends UDF {

    static final Log LOG = LogFactory.getLog(ConditionMatchUDF.class.getName());

    static final String CONST_DELIMITER = "#@@#";
    static final String CONST_LNLIMITER = "#!!#";
    static final String CONST_REGXFLG = "regex";
    static final String CONST_L_PARENTHESES = "(";
    static final String CONST_R_PARENTHESES = ")";

    static final String CONST_AND = " && ";
    static final String CONST_OR = " || ";

    /**
     *  Obtain orc row data.
     * @param tableFilePath  dictionary table path
     * @param ident ident any integer for ident function,
     *              1 is self brand dictionary;
     *              2 is competition brand dictionary;
     *              3 is selling form dictionary;
     * @return
     */
    private List<DictionaryMaster> getRowDataByOrcFile(String tableFilePath,Integer ident){

        List<DictionaryMaster> list = new ArrayList<>();
        try {
            Path folder = new Path(tableFilePath);
            Configuration conf = new Configuration();
            FileSystem fs = FileSystem.get(URI.create(tableFilePath), conf);
            List<Path> paths = getFilesUnderFolder(fs,folder,null);
            for(Path path:paths){
                FileSystem fsin = FileSystem.get(URI.create(path.getName()), conf);
                Reader reader = OrcFile.createReader(fsin, path);
                StructObjectInspector inspector = (StructObjectInspector) reader.getObjectInspector();
                RecordReader records = reader.rows();
                Object row = null;
                while (records.hasNext()) {
                    row = records.next(row);
                    DictionaryMaster dictionaryMaster = new DictionaryMaster();
                    List<Object> row_lst = inspector.getStructFieldsDataAsList(row);
                    /**
                     *  match field
                     *  blacekFlg(#%%#)
                     *  conditionGroupNo(#@@#)orConditionGroupNo(#@@#)itemName(#@@#)decisionMethod(#@@#)decisionValue
                     *      0
                     *      0                      1                       2              3                   4
                     */
                    switch (ident){
                        case 1:
                            if(row_lst.get(0)!=null) {
                                dictionaryMaster.setBrandLayerId(row_lst.get(0).toString());
                            }
                            if(row_lst.get(1)!=null){
                                dictionaryMaster.setInclusion(row_lst.get(1).toString());
                            }
                            if(row_lst.get(2)!=null){
                                dictionaryMaster.setExclusion(row_lst.get(2).toString());
                            }
                            if(row_lst.get(3)!=null){
                                dictionaryMaster.setContractBrandId(row_lst.get(3).toString());
                            }
                            break;
                        case 2:
                            if(row_lst.get(0)!=null) {
                                dictionaryMaster.setProductGroupId( row_lst.get(0).toString());
                            }
                            if(row_lst.get(1)!=null){
                                dictionaryMaster.setProductBrandId(row_lst.get(1).toString());
                            }
                            if(row_lst.get(2)!=null){
                                dictionaryMaster.setInclusion(row_lst.get(2).toString());
                            }
                            if(row_lst.get(3)!=null){
                                dictionaryMaster.setExclusion(row_lst.get(3).toString());
                            }
                            break;
                        case 3:
                            if(row_lst.get(0)!=null) {
                                dictionaryMaster.setSellingFormCode(row_lst.get(0).toString());
                            }
                            if(row_lst.get(1)!=null){
                                dictionaryMaster.setInclusion(row_lst.get(1).toString());
                            }
                            if(row_lst.get(2)!=null){
                                dictionaryMaster.setExclusion(row_lst.get(2).toString());
                            }
                            break;
                    }
                    list.add(dictionaryMaster);
                }
            }
        }catch (IOException e){
            LOG.error(""+e.getMessage());
        }
        return list;
    }

    List<DictionaryMaster> list = null;

    /**
     * UDF function for dynamic validation data
     * @param fields column title name of table
     * @param fieldValues  column value of table
     * @param tableFilePath get path by shell as `show create table DN001_TM005`
     *  CREATE TABLE `dn001_tm005`(
     *   `brand_layer_id` string,
     *    ……
     *  LOCATION
     *   'hdfs://master:9000/opt/tools/hive/warehouse/dn001_tm005'
     * @param ident any integer for ident function,
     *              1 is self brand dictionary;
     *              2 is competition brand dictionary;
     * @return
     * @throws IOException
     */
    public List<Integer> evaluate(ArrayList<String> fields, ArrayList<String> fieldValues, String tableFilePath, Integer ident) throws Exception {
        //assert fields.size() == fieldValues.size();

        List<Integer> rlist = new ArrayList<>();
        Configuration conf = new Configuration();
        Path file_in = new Path(tableFilePath);

        if(list==null) {
            list = getRowDataByOrcFile(tableFilePath, ident);
        }
        for(DictionaryMaster dict:list) {
            String inclusion = dict.getInclusion();
            if (dict.getBrandLayerId() == null || inclusion == null) {
                rlist.add(null);
                rlist.add(null);
                rlist.add(0);
                return rlist;
            }
            //String[] multiregx = inclusion.split(CONST_MULTIREGX);
            String[] inclusions = inclusion.split(CONST_LNLIMITER);
            int index = 2;
            if (ident.equals(2)) {
                if (inclusions.length > 0) {
                    //LOG.info("-------[" + dict.getInclusion() + "][" + inclusions[0] + "][" + inclusions[0].split(CONST_DELIMITER).length + "]");
                    if (inclusions[0].split(CONST_DELIMITER).length != 5
                            || dict.getExclusion() != null && dict.getExclusion().split(CONST_DELIMITER).length != 5) {
                        throw new Exception("The data must be the form as " +
                                "`1#@@#1#@@#itemname#@@#regex#@@#B#\n" +
                                "!!(line delimiter)\n" + //line delimiter
                                "#2#@@#0#@@#itemname#@@#regex#@@#E` for inclusion or exclusion");
                    }
                }
            }else {
                if (inclusions.length > 0) {
                    //LOG.info("-------[" + dict.getInclusion() + "][" + inclusions[0] + "][" + inclusions[0].split(CONST_DELIMITER).length + "]");
                    if (inclusions[0].split(CONST_DELIMITER).length != 5
                            || dict.getExclusion() != null && dict.getExclusion().split(CONST_DELIMITER).length != 5) {
                        throw new Exception("The data must be the form as " +
                                "`1#@@#1#@@#itemname#@@#regex#@@#B#\n" +
                                "!!(line delimiter)\n" + //line delimiter
                                "#2#@@#0#@@#itemname#@@#regex#@@#E` for inclusion or exclusion");
                    }
                }
            }
            // only once begin TODO
            String key = inclusion.split(CONST_DELIMITER)[index];

            int place = fields.indexOf(key);
            if(place<0){
                LOG.error("-------There isn't matched key as `" + key + "` in " + fields.toString() +" .");
                rlist.add(null);
                rlist.add(null);
                rlist.add(0);
                return rlist;
            }
            // only once end

            String value = fieldValues.get(place);

            // first match if express is true,then return false directly.
            if(dict.getExclusion()!=null && getResult(express(value,dict.getExclusion()))) {
                if(ident.equals(1)){
                    rlist.add(Integer.valueOf(dict.getBrandLayerId()));
                    rlist.add(Integer.valueOf(dict.getContractBrandId()));
                }if(ident.equals(2)){
                    rlist.add(Integer.valueOf(dict.getProductGroupId()));
                    rlist.add(Integer.valueOf(dict.getProductBrandId()));
                }
                rlist.add(0);
                return rlist;
            }
            // wait for complete except conditional
            if(getResult(express(value,inclusion))) {
                if(ident.equals(1)){
                    rlist.add(Integer.valueOf(dict.getBrandLayerId()));
                    rlist.add(Integer.valueOf(dict.getContractBrandId()));
                }if(ident.equals(2)){
                    rlist.add(Integer.valueOf(dict.getProductGroupId()));
                    rlist.add(Integer.valueOf(dict.getProductBrandId()));
                }
                rlist.add(1);
                return rlist;
            }
        }

        rlist.add(null);
        rlist.add(null);
        rlist.add(0);
        return rlist;
    }

    /**
     * regular match for string
     * @param s be matched string
     * @param regex regular match express
     * @param extractIndex match position
     * @return
     */
    private static String regexfind(String s, String regex, Integer extractIndex) {
        String lastRegex = null;
        Pattern p = null;
        if (s == null || regex == null) {
            return null;
        }
        if (!regex.equals(lastRegex) || p == null) {
            lastRegex = regex;
            p = Pattern.compile(regex);
        }
        Matcher m = p.matcher(s);
        if (m.find()) {
            MatchResult mr = m.toMatchResult();
            return mr.group(extractIndex);
        }
        return null;
    }

    /**
     * execute express speed by js syntax
     * @param evalValue express be executed
     * @return
     */
    private boolean getResult(String evalValue) {
        ScriptEngineManager manager = new ScriptEngineManager();
        ScriptEngine engine = manager.getEngineByName("javascript");
        try {
            engine.eval("var result = eval("+evalValue+");");
            return (boolean)engine.get("result");
        } catch (ScriptException e) {
            LOG.error("The express has error!" + e.getMessage() + "\n" +
                    "               ["+evalValue+"]");
            return false;
        }
    }

    /**
     *
     * @param fields
     * @param fieldValues
     * @param tableFilePath
     * @return
     * @throws IOException
     */
    public Integer evaluate(ArrayList<String> fields, ArrayList<String> fieldValues, String tableFilePath) throws Exception {
        assert (fields.size() != fieldValues.size());

        Configuration conf = new Configuration();
        Path file_in = new Path(tableFilePath);
        if(list==null) {
            // 3 is selling form code setting;
            list = getRowDataByOrcFile(tableFilePath, 3);
        }

        String sellingFormCode = null;
        StringBuffer stb = new StringBuffer();

        for(DictionaryMaster dict:list) {
            sellingFormCode = dict.getSellingFormCode();
            if (dict.getBrandLayerId() == null) {
                return null;
            }
            String includeStr = dict.getInclusion();
            String excludeStr = dict.getExclusion();
            if(dict.getInclusion() == null) {
                return null;
            }
            String[] inclusions = dict.getInclusion().split(CONST_LNLIMITER);
            if (dict.getInclusion() != null && inclusions.length > 0) {
                LOG.info("-------[" + dict.getInclusion() + "][" + inclusions[0] + "][" + inclusions[0].split(CONST_DELIMITER).length + "]");
                if (inclusions[0].split(CONST_DELIMITER).length != 5
                        || dict.getExclusion() != null && dict.getExclusion().split(CONST_DELIMITER).length != 5) {
                    throw new Exception("The data must be the form as " +
                            "`1#@@#1#@@#itemname#@@#regex#@@#B#\n" +
                            "!!(line delimiter)\n" + //line delimiter
                            "#2#@@#0#@@#itemname#@@#regex#@@#E` for inclusion or exclusion");
                }
            }
            String key = includeStr.split(CONST_DELIMITER)[2];
            int place = fields.indexOf(key);
            String value = fieldValues.get(place);
            // first match if express is true,then return false directly.
            if(excludeStr!=null && getResult(express(value,excludeStr))) {
                return null;
            }
            // handle after complete except conditional
            if(includeStr!=null && getResult(express(value,includeStr))) {
                return Integer.valueOf(sellingFormCode);
            }
        }
        return null;
    }

    /**
     * such so
     * function parameter regxstr is
     * `1#@@#1#@@#itemname#@@#regex#@@#B#!!#2#@@#0#@@#itemname#@@#regex#@@#E#!!#1#@@#1#@@#itemname
     * #@@#regex#@@#A#!!#1#@@#0#@@#itemname#@@#regex#@@#C#!!#2#@@#0#@@#itemname#@@#regex#@@#D`
     * convert result as `( true  or  false ) and ( true  and  false )`
     * @param regxstr need parse string
     * @return
     */
    static String express(String value, String regxstr) {

        StringBuffer result = new StringBuffer();

        if(regxstr!=null) {

            //String[] multiregx = regxstr.split(CONST_MULTIREGX);
            Map<String, Map<String,List<ConditionReg>>> map = regexMap(regxstr);

            Set<String> regxset = map.keySet();
            int groupCount = 0;
            for(String key: regxset) {
                Map<String,List<ConditionReg>> ormap= map.get(key);
                int orConnt = 0;
                Set<String> orset = ormap.keySet();
                for(String orkey:ormap.keySet()) {

                    List<ConditionReg> list = ormap.get(orkey);
                    //Add left parentheses if the item list size greater than 1
                    if(list.size()>1)
                        result.append(CONST_L_PARENTHESES);
                    int c = 0;
                    for(ConditionReg conditionReg:list){
                        if(conditionReg.getDecisionMethod().equals(CONST_REGXFLG)) {
                            // regular express match value
                            result.append(regexfind(value, conditionReg.getDecisionValue(), 1) != null ? true : false);
                        }else{
                            result.append(conditionReg.getItemName());
                            result.append(conditionReg.getDecisionMethod());
                            result.append(conditionReg.getDecisionValue());
                        }
                        if(list.size()-1 != c) {
                            if(!conditionReg.getOrConditionGroupNo().equals("0")) {
                                result.append(CONST_OR);
                            }else {
                                result.append(CONST_AND);
                            }
                        }
                        c = c + 1;
                    }
                    // Add right parentheses if the item list size greater than 1
                    if(list.size()>1) {
                        result.append(CONST_R_PARENTHESES);
                    }
                    if(orset.size()-1 != orConnt) {
                        result.append(CONST_AND);
                    }
                    orConnt = orConnt+1;
                }
                if(regxset.size()-1 != groupCount) {
                    result.append(CONST_OR);
                }
                groupCount = groupCount+1;
                //result.append(CONST_R_PARENTHESES);
            }
        }
        return result.toString();
    }

    /**
     * Convert the regxstr to the object ConditionReg, the regxstr as `1#@@#1#@@#itemname#@@#regex#@@#B#`
     * put the object ConditionReg in a map table
     * <ConditionGroupNo,Map<OrConditionGroupNo,List<ConditionReg>>>
     * @param regxstr
     * @return
     */
    private static Map<String, Map<String,List<ConditionReg>>> regexMap(String regxstr ) {
        Map<String, Map<String,List<ConditionReg>>> map = new HashMap();
        String[] regxs = regxstr.split(CONST_LNLIMITER);
        for(String re:regxs){
            ConditionReg conditionReg = new ConditionReg();
            String[] res = re.split(CONST_DELIMITER);

            conditionReg.setConditionGroupNo(res[0]);
            conditionReg.setOrConditionGroupNo(res[1]);
            conditionReg.setItemName(res[2]);
            conditionReg.setDecisionMethod(res[3]);
            conditionReg.setDecisionValue(res[4]);

            if(map.get(conditionReg.getConditionGroupNo())!=null) {
                if(map.get(conditionReg.getConditionGroupNo()).get(conditionReg.getOrConditionGroupNo())!=null){
                    map.get(conditionReg.getConditionGroupNo()).get(conditionReg.getOrConditionGroupNo()).add(conditionReg);
                }else {
                    List<ConditionReg> orConditionGroupNoList = new ArrayList();
                    orConditionGroupNoList.add(conditionReg);
                    map.get(conditionReg.getConditionGroupNo()).put(conditionReg.getOrConditionGroupNo(),orConditionGroupNoList);
                }
            }else {
                Map<String,List<ConditionReg>> conditionGroupNoMap = new HashMap();
                List<ConditionReg> orConditionGroupNoList = new ArrayList();
                orConditionGroupNoList.add(conditionReg);
                conditionGroupNoMap.put(conditionReg.getOrConditionGroupNo(),orConditionGroupNoList);
                map.put(conditionReg.getConditionGroupNo(),conditionGroupNoMap);
            }
        }
        return map;
    }

    /**
     * Obtain all file that pattern match name of a file in a folder(not contain subfolder)
     * @param fs
     * @param folderPath
     * @param pattern regex for pattern match name of a file
     * @return
     * @throws IOException
     */
    public static List<Path> getFilesUnderFolder(FileSystem fs, Path folderPath, String pattern) throws IOException {
        List<Path> paths = new ArrayList<Path>();
        if (fs.exists(folderPath)) {
            FileStatus[] fileStatus = fs.listStatus(folderPath);
            for (int i = 0; i < fileStatus.length; i++) {
                FileStatus fileStatu = fileStatus[i];
                if (fileStatu.isFile()) {//only file
                    Path oneFilePath = fileStatu.getPath();
                    //obtain any files for it's size great than zero
                    if(fileStatu.getLen()>0){
                        if (pattern == null) {
                            paths.add(oneFilePath);
                        } else {
                            if (oneFilePath.getName().contains(pattern)) {
                                paths.add(oneFilePath);
                            }
                        }
                    }
                }
            }
        }
        return paths;
    }

    /**
     *
     */
    private static class DictionaryMaster{
        String sellingFormCode;
        String brandLayerId;
        String inclusion;
        String exclusion;
        String contractBrandId;
        String layerLevelNo;
        String productGroupId;
        String productBrandId;

        public String getSellingFormCode() {
            return sellingFormCode;
        }

        public void setSellingFormCode(String sellingFormCode) {
            this.sellingFormCode = sellingFormCode;
        }

        public String getBrandLayerId() {
            return brandLayerId;
        }

        public void setBrandLayerId(String brandLayerId) {
            this.brandLayerId = brandLayerId;
        }

        public String getInclusion() {
            return inclusion;
        }

        public void setInclusion(String inclusion) {
            this.inclusion = inclusion;
        }

        public String getExclusion() {
            return exclusion;
        }

        public void setExclusion(String exclusion) {
            this.exclusion = exclusion;
        }

        public String getContractBrandId() {
            return contractBrandId;
        }

        public void setContractBrandId(String contractBrandId) {
            this.contractBrandId = contractBrandId;
        }

        public String getLayerLevelNo() {
            return layerLevelNo;
        }

        public void setLayerLevelNo(String layerLevelNo) {
            this.layerLevelNo = layerLevelNo;
        }

        public String getProductGroupId() { return productGroupId;  }

        public void setProductGroupId(String productGroupId) {
            this.productGroupId = productGroupId;
        }

        public String getProductBrandId() {
            return productBrandId;
        }

        public void setProductBrandId(String productBrandId) {
            this.productBrandId = productBrandId;
        }
    }

    /**
     *
     */
    public static class ConditionReg {
        String conditionGroupNo;
        String orConditionGroupNo;
        String itemName;
        String decisionMethod;
        String decisionValue;

        public String getItemName() {
            return itemName;
        }

        public void setItemName(String itemName) {
            this.itemName = itemName;
        }

        public String getDecisionValue() {
            return decisionValue;
        }

        public void setDecisionValue(String decisionValue) {
            this.decisionValue = decisionValue;
        }

        public String getDecisionMethod() {
            return decisionMethod;
        }

        public void setDecisionMethod(String decisionMethod) {
            this.decisionMethod = decisionMethod;
        }

        public void setOrConditionGroupNo(String orConditionGroupNo) {
            this.orConditionGroupNo = orConditionGroupNo;
        }

        public void setConditionGroupNo(String conditionGroupNo) {
            this.conditionGroupNo = conditionGroupNo;
        }

        public String getConditionGroupNo() {
            return conditionGroupNo;
        }

        public String getOrConditionGroupNo() {
            return orConditionGroupNo;
        }
    }
}
