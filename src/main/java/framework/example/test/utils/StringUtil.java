package framework.example.test.utils;

public class StringUtil {

    public static String lowerFirstCase(String className) {
        char[] chars = className.toCharArray();
        chars[0] += 32;
        return String.valueOf(chars);
    }
    
    public static boolean isNotBlank(String val) {
        int strLen;
        if (val == null || (strLen = val.length()) == 0) {
            return false;
        }
        for (int i = 0; i < strLen; i++) {
            if (Character.isWhitespace(val.charAt(i))) {
                return false;
            }
        }
        return true;
    }
}
