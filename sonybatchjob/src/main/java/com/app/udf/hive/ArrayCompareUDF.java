package com.app.udf.hive;

import org.apache.hadoop.hive.ql.exec.UDF;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ArrayCompareUDF extends UDF {
    /**
     * obtain array difference set when test data use .
     * @param oneArray a array
     * @param twoArray a array
     * @param func function identifier
     * @return
     */
    public List<String> evaluate(List<String> oneArray, List<String> twoArray, int func) throws Exception {
        if (oneArray==null||twoArray==null){
            throw new Exception("The array parameter must be no null!");
        }

        Set<String> set = new HashSet<String>(oneArray.size()>twoArray.size() ? oneArray : twoArray);
        for (String i : oneArray.size()>twoArray.size() ? twoArray : oneArray)
        {
            if (set.contains(i))
            {
                set.remove(i);
            } else
            {
                set.add(i);
            }
        }
        return new ArrayList<String>(set);
    }

    /**
     * array difference if same is false otherwise is true.
     * @param oneArray a array
     * @param twoArray a array
     * @return
     */
    public boolean evaluate(List<String> oneArray, List<String> twoArray) throws Exception {
        if (oneArray==null||twoArray==null){
            throw new Exception("the array must be no null!");
        }

        Set<String> set = new HashSet<String>(oneArray.size()>twoArray.size() ? oneArray : twoArray);
        for (String i : oneArray.size()>twoArray.size() ? twoArray : oneArray)
        {
            if (!set.contains(i))
            {
                return true;
            }
        }
        return false;
    }
}
