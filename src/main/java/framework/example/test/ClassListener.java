package framework.example.test;

import java.io.File;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;


public class ClassListener implements ServletContextListener{
	
	private static String PATH = "framework.example.test.controller";
	
	//監聽關閉時執行
	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		System.out.println("關閉");
	}

	// 監聽啟動時執行
	@Override
	public void contextInitialized(ServletContextEvent sce) {
		System.out.println("contextListener#start");
		ServletContext sct = sce.getServletContext();
//		List<String> inRestController = new ArrayList<>();
		try {
			String realPath = getRealPath(PATH, sce);
			List<String> fullPaths = getFullPaths(realPath, PATH);
			List<Class> classList = convertPath2Clzz(fullPaths);
			for(Class clz : classList) {
				System.out.println("class=> " + clz);
				Annotation[] ano = (Annotation[]) clz.getAnnotations();
				for (Annotation annotation : ano) {
					System.out.println(annotation.annotationType().getName());
					if(annotation.annotationType().getName().endsWith("MyRestController")) {
//						inRestController.add(clz.getName());
						sct.setAttribute("inRestController", clz.getName());
						System.out.println("成功=> " + sct.getAttribute("inRestController"));
					}else {
						System.out.println("查無指定註解");
					}
				}
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public String getRealPath(String testPackage,ServletContextEvent sce) {
		ServletContext servletContext = sce.getServletContext();
		String realPath = servletContext.getRealPath("/");
		realPath += "WEB-INF/classes/";
		realPath += testPackage.replaceAll("\\.", "\\/");
		return realPath;
	}
	
	public List<String> getFullPaths(String realPath,String testPackage){
		List<String> fullPaths = new ArrayList<>();
		getFullPathsRecursion(testPackage, realPath, fullPaths);
		return fullPaths;
	}
	
	public void getFullPathsRecursion(String testPackage,String realPath, List<String> fullPaths) {
		File file = new File(realPath);
		File[] list = file.listFiles();
		for (File fileName : list) {
			String s = fileName.getName();
			//如果是目錄
			if(fileName.isDirectory()) {
				getFullPathsRecursion(testPackage + "." + s, realPath + "/" + s, fullPaths);
			}else {
				int i = s.indexOf(".class");
				s = s.substring(0,i);
				fullPaths.add(testPackage + "." + s);
			}
		}
	}
	
	public List<Class> convertPath2Clzz(List<String> fullPaths){
		System.out.println("fullPaths => " + fullPaths);
		List<Class> classList = new ArrayList<>();
		for (String path : fullPaths) {
			try {
				Class clazz = Class.forName(path);
				classList.add(clazz);
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} 
		}
		return classList;
	}

}
