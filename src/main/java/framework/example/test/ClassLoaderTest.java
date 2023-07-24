package framework.example.test;

import java.net.URL;

public class ClassLoaderTest {

	public static void main(String[]args) {
		try {
			Class cls = Class.forName("ClassLoaderTest");
			ClassLoader clsLoader = cls.getClassLoader();
			System.out.println("loader =>" + clsLoader.getClass());
			URL url = clsLoader.getSystemResource(".class");
			System.out.println("url => " + url);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
}
