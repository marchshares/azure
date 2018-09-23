package com.bars.orders;

import com.google.common.collect.Sets;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class Utils {

    public static Stream<Object> jsonArrayToStream(JSONArray arr) {
        return StreamSupport.stream(arr.spliterator(), false);
    }


    public static boolean isContainsSubName(String name, String subName) {
        if (name == null) {
            return false;
        }

        return Sets.newHashSet(name.split(" ")).contains(subName);
    }

    public static boolean isStringEquals(String str1, String str2) {
        return StringUtils.equals(safeTrim(str1).toLowerCase(), safeTrim(str2).toLowerCase());
    }

    public static String safeTrim(String str) {
        return str == null ? "" : str.trim();
    }

    public static String toString(JSONObject option) {
        return option.toString(1);
    }
}
