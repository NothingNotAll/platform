package nna.base.proxy;

import net.sf.cglib.proxy.Enhancer;
import nna.base.bean.dbbean.PlatformProxy;
import nna.base.util.orm.ObjectUtil;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;

public class ProxyFactory {
	private static Enhancer enhancer = new Enhancer();

	public static Object buildProxyObject(Object beenProxyObject,Method beenBeenProxyMethod,ProxyService proxyService) throws NoSuchMethodException, SecurityException {
		proxyService.setBeenProxyObject(beenProxyObject);
		proxyService.setBeenProxyMethod(beenBeenProxyMethod);
		@SuppressWarnings("rawtypes")
		Class beenProxyInterface=getInterface(beenProxyObject, beenBeenProxyMethod);
		if (beenProxyInterface==null) {
			return  getProxy(beenProxyObject.getClass(),proxyService);
		}
		return Proxy.newProxyInstance(ProxyFactory.class.getClassLoader(), new Class[]{beenProxyInterface}, proxyService);
	}

	private static Object getProxy(@SuppressWarnings("rawtypes") Class clazz,ProxyService proxyService){
		  enhancer.setSuperclass(clazz);
		  enhancer.setCallback(proxyService);
		  return enhancer.create();
	}

	@SuppressWarnings("rawtypes")
	private static Class getInterface(Object object,Method method){
		Class goalClass=null;
		Class[] interfaces=getInterfaces(object.getClass());
		for(Class temp:interfaces){
			Method[] methods=temp.getMethods();
			if (methods==null) {
				continue;
			}
			for(Method method2:methods){
				if (method2.getName()==method.getName()) {
					return temp;
				}
			}
		}
		return goalClass;
	}

  	@SuppressWarnings("rawtypes")
	private static Class[] getInterfaces(Class temp){
  		Class[] interfaces=temp.getInterfaces();
  		Class tempClass=temp.getSuperclass();
  		if (tempClass!=null) {
  			Class[] temps=getInterfaces(tempClass);
  			if (temps!=null&&temps.length>0) {
  	  			Class[] classess=new Class[interfaces.length+temps.length];
  	  			System.arraycopy(interfaces, 0, classess,0,interfaces.length);
  	  			System.arraycopy(temps, 0, classess, interfaces.length, classess.length);
  	  			interfaces=classess;
			}
  		}
		return interfaces;
  	}

  	private static void reduce(ProxyService proxyService,Logic logic,PlatformProxy v2){
		Logic[] logics=null;
		int length;
		Logic[] tempLogics=null;
		switch (v2.getProxyType()) {
		case Before:
			logics=proxyService.getbLogics();
			if(logics==null){
				proxyService.setbLogics(new Logic[]{logic});
			}else{
				length=logics.length;
				tempLogics=new Logic[length+1];
				System.arraycopy(logics, 0, tempLogics, 0, length);
				tempLogics[length]=logic;
				proxyService.setbLogics(tempLogics);
			}
			break;
		case After:
			logics=proxyService.getaLogics();
			if(logics==null){
				proxyService.setaLogics(new Logic[]{logic});
			}else{
				length=logics.length;
				tempLogics=new Logic[length+1];
				System.arraycopy(logics, 0, tempLogics, 0, length);
				tempLogics[length]=logic;
				proxyService.setaLogics(tempLogics);
			}
			break;
		case Exception:
			logics=proxyService.geteLogics();
			if(logics==null){
				proxyService.seteLogics(new Logic[]{logic});
			}else{
				length=logics.length;
				tempLogics=new Logic[length+1];
				System.arraycopy(logics, 0, tempLogics, 0, length);
				tempLogics[length]=logic;
				proxyService.seteLogics(tempLogics);
			}
			break;
		case Final:
			logics=proxyService.getfLogics();
			if(logics==null){
				proxyService.setfLogics(new Logic[]{logic});
			}else{
				length=logics.length;
				tempLogics=new Logic[length+1];
				System.arraycopy(logics, 0, tempLogics, 0, length);
				tempLogics[length]=logic;
				proxyService.setfLogics(tempLogics);
			}
			break;

		default:
			break;
		}
  	}

	public static HashMap<String, ProxyService> getClassMethodProxyServiceConfig(LinkedList<PlatformProxy> lists){
		HashMap<String, ProxyService> proxyServiceConfig=new HashMap<String, ProxyService>();
		Iterator<PlatformProxy> iterator=lists.iterator();
		while(iterator.hasNext()){
			PlatformProxy v2=iterator.next();
			String className=v2.getBeenproxyClassRegex();
			String methodName=v2.getBeenproxyMethodRegex();
			String key=className+"/"+methodName;
			ProxyService proxyService = null;
			if(!proxyServiceConfig.containsKey(key)){
				proxyService=new ProxyService();
				String proxyClassName=v2.getProxyClass();
				String proxyMethodName=v2.getProxyMethod();
				@SuppressWarnings("rawtypes")
				Class logicClass;
				try {
					logicClass = Class.forName(proxyClassName);
					Object logicClassObject=logicClass.newInstance();
					Method method=ObjectUtil.loadMethodFromObjectAndMethodName(logicClassObject, proxyMethodName);
					Logic logic=new Logic(className, methodName, logicClass.newInstance(),method);
					reduce(proxyService, logic, v2);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}else{
				proxyService=proxyServiceConfig.get(key);
				String proxyClassName=v2.getProxyClass();
				String proxyMethodName=v2.getProxyMethod();
				try {
					@SuppressWarnings("rawtypes")
					Class logicClass = Class.forName(proxyClassName);
					Object logicClassObject=logicClass.newInstance();
					Method method= ObjectUtil.loadMethodFromObjectAndMethodName(logicClassObject, proxyMethodName);
					Logic logic=new Logic(className, methodName, logicClass.newInstance(),method);
					reduce(proxyService, logic, v2);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			proxyServiceConfig.put(key, proxyService);
		}
		return proxyServiceConfig;
	}



	public static Object[] getProxy(Map<String, ProxyService> proxyServiceHashMap, String className, String methodName){
		if(className==null||methodName==null||className.trim().equals("")||methodName.trim().equals("")){
			return null;
		}
		Object[] objectAndMethod=new Object[2];
		Object beenProxyObject= null;
		Method beenProxyMethod=null;
		try {
			beenProxyObject = ObjectUtil.loadObjectFromClassName(className);
			beenProxyMethod=ObjectUtil.loadMethodFromObjectAndMethodName(beenProxyObject,methodName);
		} catch (Exception e) {
			e.printStackTrace();
		}
		ProxyService proxyService=proxyServiceHashMap.get(className);
		if(proxyService!=null){
			try {
				beenProxyObject = ProxyFactory.buildProxyObject(beenProxyObject, beenProxyMethod,proxyService);
				beenProxyMethod=ObjectUtil.loadMethodFromObjectAndMethodName(beenProxyObject, beenProxyMethod.getName());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		objectAndMethod[0]=beenProxyObject;
		objectAndMethod[1]=beenProxyMethod;
		return objectAndMethod;
	}

	public static void main(String[] args) throws NoSuchMethodException, SecurityException {
		
	}
}
