package framework.example.test.listener;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import framework.example.test.annotation.MyRequestMapping;
import framework.example.test.annotation.MyRestController;

@WebListener
public class LeoWebListener implements ServletContextListener{

    /**
     * 根目錄
     */
    public static final String ROOT_DIRECTORY_PATH = "framework.example.test";
    /**
     * 路徑存放區
     */
    public static final List<String> CLASS_PATH = new ArrayList<>();
    /**
     * 存放有註解的class
     */
    public static final Map<String,Method> METHOD_MAP = new HashMap<>();
    
    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        // 掃描根目錄
        scanPackage(ROOT_DIRECTORY_PATH);
        // 掃描annotation
        scanAnnotation();
    }
    
    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        
    }


    public void scanPackage(String rootDirectoryPath) {
        // 將路徑轉換成
        URL url = getClass().getClassLoader().getResource("/" + rootDirectoryPath.replaceAll("\\.", "/"));
        
        String urlStr = url.getFile();
        
        File file = new File(urlStr);
        String[] files = file.list();
        
        for (String path : files) {
            File filePath = new File(urlStr + path);
            
            if (filePath.isDirectory()) {
                scanPackage(rootDirectoryPath + "." +  path);
            }else {
                CLASS_PATH.add(rootDirectoryPath + "." + path);
            }
        }
    }
    
    public void scanAnnotation() {
        CLASS_PATH.forEach(path -> {
            try {
                Class<?> clazz = Class.forName(path.replace(".class", ""));
                if (clazz.isAnnotationPresent(MyRestController.class)) {
                    MyRequestMapping reqMapping = clazz.getAnnotation(MyRequestMapping.class);
                    String clazzPath = "";
                    if(reqMapping != null) {
                        clazzPath = reqMapping.value();
                    }

                    Method[] method = clazz.getDeclaredMethods();
                    for (Method mt : method) {
                        if (mt.isAnnotationPresent(MyRequestMapping.class)) {
                            MyRequestMapping myReq = mt.getAnnotation(MyRequestMapping.class);
                            String myRequsetStr = myReq.value();
                            METHOD_MAP.put(clazzPath + myRequsetStr, mt);
                        }
                    }
                }
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        });
    }
    
}
