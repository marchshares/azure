package com.bars.orders;

import com.google.common.collect.Sets;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class Utils {
    public static final Pattern PLACE_HOLDERS_PATTERN = Pattern.compile("\\$\\{[^}]*}");

    public static Stream<Object> jsonArrayToStream(JSONArray arr) {
        return StreamSupport.stream(arr.spliterator(), false);
    }

    public static String extTrimLower(String str) {
        return extTrim(str).toLowerCase();
    }

    public static String extTrim(String str) {
        return str
                .replaceAll("\\(.*\\)", "")
                .replaceAll("\\s{2,}", " ")
                .trim();
    }

    public static boolean isContainsSubName(String name, String subName) {
        if (name == null || subName == null) {
            return false;
        }
        String subNameLower = subName.toLowerCase();

        return Sets.newHashSet(name.split(" "))
                .stream()
                .map(String::toLowerCase)
                .anyMatch(subNameLower::equals);

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

    public static void checkGood(String value, String name){
        if (!StringUtils.isNotEmpty(value)) {
            throw new RuntimeException("Check failed: " + name + "=" + value);
        }
    }

    public static void checkPlaceHolders(String msgText) {
        Matcher matcher = PLACE_HOLDERS_PATTERN.matcher(msgText);
        if (matcher.find()) {
            throw new RuntimeException("Found unresolved placeholder " + matcher.group() + " in " + msgText);
        }
    }
}
