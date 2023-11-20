package framework.example.test.utils;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.file.FileSystems;
import java.util.Properties;

public class ConfigPropUtil {
    
    private static final String Resources_Path = "/Users/huangweixin/git/Custom-ControllerAndORM/src/main/resources/application.properties";
    
    public static String getPropertiesData(String rootPath, String key) {
        String val = "";
        try {
            InputStream in = new BufferedInputStream(new FileInputStream(Resources_Path));
            Properties p = new Properties();
            p.load(in);
            val = p.getProperty(key);
            System.out.println("val => " + val);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return val;
    }
    
}
