package framework.example.test.listener;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import framework.example.test.annotation.MyAutowired;
import framework.example.test.annotation.MyComponent;
import framework.example.test.annotation.MyController;
import framework.example.test.annotation.MyRequestMapping;
import framework.example.test.annotation.MyRestController;
import framework.example.test.annotation.MyService;
import framework.example.test.annotation.MyValue;
import framework.example.test.exception.UrlException;
import framework.example.test.utils.ConfigPropUtil;
import framework.example.test.utils.StringUtil;

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
     * 路徑存放區
     */
    public static final Map<String,Object> IOC_MAP = new HashMap<>();
    /**
     * 存放有註解的class
     */
    public static final Map<String,Method> METHOD_MAP = new HashMap<>();
    
    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        // 掃描根目錄
        scanPackage(ROOT_DIRECTORY_PATH);
        // 掃描controller、service
        scanAnnotation();
        // 注入Autowired
        initAutowired();
        // 掃描MyRestContoller
        scanControllerAnnotation();
    }
    
    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        
    }

    /**
     * 掃描路徑檔案
     * @param rootDirectoryPath
     */
    private void scanPackage(String rootDirectoryPath) {
        System.out.println("scanPackage#start");
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
    
    /**
     * 掃描MyRestController、MyService
     */
    private void scanAnnotation() {
        System.out.println("scanAnnotation#start");
        CLASS_PATH.forEach(claz -> {
            try {
                Class<?> clazz = Class.forName(claz.replace(".class", ""));
                
                if (clazz.isAnnotationPresent(MyComponent.class)) {
                    IOC_MAP.put(StringUtil.lowerFirstCase(clazz.getSimpleName()), clazz.getDeclaredConstructor().newInstance());
                }else if (clazz.isAnnotationPresent(MyController.class) || clazz.isAnnotationPresent(MyRestController.class) || clazz.isAnnotationPresent(MyService.class)) {
                    IOC_MAP.put(StringUtil.lowerFirstCase(clazz.getSimpleName()), clazz.getDeclaredConstructor().newInstance());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
    
    /**
     * 掃描MyAutowired(Annotation)
     */
    private void initAutowired() {
        System.out.println("initAutowired#start");
        IOC_MAP.forEach((k,v) -> {
            try {
                Class<?> clazz = v.getClass();
                if (clazz.isAnnotationPresent(MyComponent.class) || clazz.isAnnotationPresent(MyController.class) 
                        || clazz.isAnnotationPresent(MyRestController.class) || clazz.isAnnotationPresent(MyService.class)) {
                    Field[] fields = clazz.getDeclaredFields();
                    for (Field field : fields) {
                        if (field.isAnnotationPresent(MyValue.class)) {
                            // 獲取依賴注入註解
                            MyValue myValue = field.getAnnotation(MyValue.class);
                            String key = myValue.value();
                            
                            //開啟 private權限
                            field.setAccessible(true);
                            Object val = ConfigPropUtil.getPropertiesData(ROOT_DIRECTORY_PATH, key);
                            if (val != null) {
                                field.set(v, val);
                            }
                        }
                        
                        if (field.isAnnotationPresent(MyAutowired.class)) {
                            // 獲取依賴注入註解
                            MyAutowired autowired = field.getAnnotation(MyAutowired.class);
                            String beanName = autowired.value();
                            if ("".equals(beanName)) {
                                beanName = StringUtil.lowerFirstCase(field.getType().getSimpleName());
                            }
                            //開啟 private權限
                            field.setAccessible(true);
                            Object beanClass = IOC_MAP.get(beanName);
                            if (beanClass != null) {
                                field.set(v, beanClass);
                            }
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
    
    /**
     * 掃描MyRestController(Annotation)
     */
    private void scanControllerAnnotation() {
        System.out.println("scanControllerAnnotation#start");
        IOC_MAP.forEach((k,v) -> {
            try {
                Class<?> clazz = v.getClass();
                if (clazz.isAnnotationPresent(MyRestController.class) || clazz.isAnnotationPresent(MyController.class)) {
                    // class
                    MyRequestMapping reqMapping = clazz.getAnnotation(MyRequestMapping.class);
                    String clazzPath = "";
                    if(reqMapping != null) {
                        clazzPath = reqMapping.value();
                    }
                    
                    // method
                    Method[] method = clazz.getDeclaredMethods();
                    for (Method mt : method) {
                        if (mt.isAnnotationPresent(MyRequestMapping.class)) {
                            MyRequestMapping myReq = mt.getAnnotation(MyRequestMapping.class);
                            String myRequestStr = myReq.value();
                            String path = clazzPath + myRequestStr;
                            if (METHOD_MAP.get(path) != null) {
                                throw new UrlException("UrlException :" + clazz.getSimpleName() + "-(" + myRequestStr + ") is repeated");
                            }else {
                                METHOD_MAP.put(path, mt);
                            }
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
    
}
