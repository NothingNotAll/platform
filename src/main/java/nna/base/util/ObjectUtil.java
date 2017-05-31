package nna.base.util;

import nna.Marco;
import nna.base.bean.Clone;
import nna.base.bean.dbbean.PlatformLog;
import nna.enums.JavaValueType;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/*
 * Used as reflection util of object
 *  
 * */
public class ObjectUtil {

  	public static Method loadMethodFromObjectAndMethodName(Object object,String methodName) throws NoSuchMethodException, SecurityException{
  		Class clazz=object.getClass();
		return loadMethodFromObjectAndMethodName(clazz,methodName);
  	}

    public static Method loadMethodFromObjectAndMethodName(Class clazz,String methodName) throws NoSuchMethodException, SecurityException{
        Method[] methods=clazz.getMethods();
        int count=methods.length;
        Method method=null;
        for(int index=0;index < count;index++){
            method=methods[index];
            if (method.getName().equals(methodName)) {
                return method ;
            }
        }
        if(method==null){
            throw new NullPointerException();
        }
        return method;
    }

    public static ObjectFactory buildObjectFactory(String className) throws NoSuchMethodException, ClassNotFoundException, InstantiationException, IllegalAccessException {
            Class clazz=Class.forName(className);
            return buildObjectFactory(clazz);
    }

	public static ObjectFactory buildObjectFactory(Class clazz) throws NoSuchMethodException, IllegalAccessException, InstantiationException {
  	    Field[] fields=clazz.getDeclaredFields();
        int size=fields.length;
        Field field;
        String fieldName ;
        String methodName;
        Method method ;
        ObjectFactory objectFactory =new ObjectFactory();
        Clone clone = (Clone) clazz.newInstance();
        objectFactory.setClone(clone);
        objectFactory.setFieldsCount(size);
        objectFactory.setSetMethods(new Method[size]);
        objectFactory.setGetMethods(new Method[size]);
        objectFactory.setFieldName(new String[size]);
        objectFactory.setJavaValueTypesV2(new int[size]);
        objectFactory.setNullJsonIndexes(new int[size]);
        objectFactory.setJsons(new String[size*2+1]);
        int i=0;
        for(int index=0;index < size;index++){
            field=fields[index];
            fieldName=field.getName();

            //for bean to json Str
            String unitJson="";
            if(index==0){
                unitJson="{\r\n\""+fieldName+"\":\"";
            }else{
                if(index>0&&index < size-1){
                    unitJson="\",\""+fieldName+"\":\"";
                }else{
                    if(index==size-1){
                        unitJson+="\"}";
                    }
                }
            }
            objectFactory.getJsons()[index]=unitJson;
            objectFactory.getJsons()[index+1]=null;
            objectFactory.getNullJsonIndexes()[i++]=index+1;

            //for ResultSet to Bean
            JavaValueType javaValueType=getFieldType(field);
            if(javaValueType.toString().equals("BOOLEAN")){
                if(fieldName.startsWith("is")){
                    methodName="set"+fieldName.substring(2);
                }else{
                    methodName="set"+fieldName.substring(0,1).toUpperCase()+fieldName.substring(1);
                }
            }else{
                methodName="set"+fieldName.substring(0,1).toUpperCase()+fieldName.substring(1);
            }
            method=loadMethodFromObjectAndMethodName(clazz,methodName);

            int javaValueTypeV2=getFileTypeV2(javaValueType);
            objectFactory.getJavaValueTypesV2()[index]=javaValueTypeV2;
            objectFactory.getSetMethods()[index]=method;

            //for bean to pst Parameter;
            if(javaValueType.toString().equals("BOOLEAN")){
                if(fieldName.startsWith("is")){
                    methodName="is"+fieldName.substring(1,2).toUpperCase()+fieldName.substring(1);
                }else{
                    methodName="is"+fieldName.substring(0,1).toUpperCase()+fieldName.substring(1);
                }
            }else{
                methodName="get"+fieldName.substring(0,1).toUpperCase()+fieldName.substring(1);
            }
            method=loadMethodFromObjectAndMethodName(clazz,methodName);
            objectFactory.getGetMethods()[index]=method;
        }
        return objectFactory;
    }

    private static int getFileTypeV2(JavaValueType javaValueType) {
        switch (javaValueType){
            case CLUSTERTYPE:
                return Marco.PLATFORM_ENUM_CLUSTERTYPE;
            case DEVICETYPE:
                return Marco.PLATFORM_ENUM_DEVICETYPE;
            case DBTYPE:
                return Marco.PLATFORM_ENUM_DBTYPE;
            case USERROLE:
                return Marco.PLATFORM_ENUM_USERROLE;
            case PROXYTYPE:
                return Marco.PLATFORM_ENUM_PROXYTYPE;
            case DBOPERTYPE:
                return Marco.PLATFORM_ENUM_DBOPERTYPE;
            case DEVELOPTYPE:
                return Marco.PLATFORM_ENUM_DEVELOPTYPE;
            case RESOURCETYPE:
                return Marco.PLATFORM_ENUM_RESOURCETYPE;
            case DBTRANLVLTYPE:
                return Marco.PLATFORM_ENUM_DBTRANLVLTYPE;
            case DBTRANPPGTYPE:
                return Marco.PLATFORM_ENUM_DBTRANPPGTYPE;
            case JAVAVALUETYPE:
                return Marco.PLATFORM_ENUM_JAVAVALUETYPE;
            case DBSQLCONVALTYPE:
                return Marco.PLATFORM_ENUM_DBSQLCONVALTYPE;
            case BOOLEAN:
                return Marco.JAVA_BOOLEAN;
            case STRING:
                return Marco.JAVA_STRING;
            case DOUBLE:
                return Marco.JAVA_DOUBLE;
            case TIMESTAMP:
                return Marco.JAVA_TIMESTAMP;
            case INT:
                return Marco.JAVA_INT;
            case LONG:
                return Marco.JAVA_LONG;
            case PRIVETYPE:
                return Marco.PLATFORM_ENUM_PRIVETYPE;
            case SESSIONTYPE:
                return Marco.PLATFORM_ENUM_SESSIONTYPE;
            case FLOAT:
                return Marco.JAVA_FLOAT;

        }
        return -1;
    }

    public void buildBean(){
  	    //根据配置生成 Object 对象 首先进行简单对象的生成，然后进行 复杂对象的生成 这是一个递进的过程
        //1：构造器参数配置 构造器参数配置：参数类型；参数值；
        //2：成员字段变量的生成。

        //判断这个类是否有代理配置如果有代理配置 则加载代理配置，生成代理对象

    }

    private static JavaValueType getFieldType(Field field) {
  	    String typeName=field.getType().getName();
  	    int dotIndex=typeName.lastIndexOf(".");
  	    String javaValueType=dotIndex==-1?typeName:typeName.substring(dotIndex+1);
  	    javaValueType=javaValueType.toUpperCase();
        return JavaValueType.valueOf(javaValueType);
    }

    public static Object loadObjectFromClassName(String className) throws InstantiationException, IllegalAccessException, ClassNotFoundException{
		return Class.forName(className).newInstance();
	}
	public static void main(String[] args){
        try {
            buildObjectFactory(PlatformLog.class);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
    }
}
