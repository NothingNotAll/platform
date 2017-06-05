package nna.base.util;

import nna.Marco;
import nna.base.bean.Clone;
import nna.enums.*;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

/**
 * s
 *
 * @author NNA-SHUAI
 * @create 2017-05-16 17:24
 **/

 public class ObjectFactory{
    private static List<Object> objectOfListContainer;//init it from the config file or from the db config;
    private static HashMap<String,Object> objectOfMapContainer;//init it from the config file or from the db config;

    private Clone clone;
    private Method[] setMethods;
    private Method[] getMethods;
    private int[] javaValueTypesV2;

    //for object to Json
    private String[] jsons;
    private int[] nullJsonIndexes;

    private String[] fieldName;

    private int fieldsCount;

    public static HashMap<String, Object> getObjectOfMapContainer() {
        return objectOfMapContainer;
    }

    public static void setObjectOfMapContainer(HashMap<String, Object> objectOfMapContainer) {
        ObjectFactory.objectOfMapContainer = objectOfMapContainer;
    }

    public static List<Object> getObjectOfListContainer() {
        return objectOfListContainer;
    }

    public static void setObjectOfListContainer(List<Object> objectOfListContainer) {
        ObjectFactory.objectOfListContainer = objectOfListContainer;
    }

    public String getJSON(Object object) throws InvocationTargetException, IllegalAccessException {
        StringBuilder json=new StringBuilder("");
        Method nullSetMethod;
        String jsonUnit;
        int nullCount=nullJsonIndexes.length;
        for(int index=0;index < nullCount;index++){
            nullSetMethod=setMethods[index];
            jsonUnit=jsons[nullJsonIndexes[index]];
            if(jsonUnit==null){
                jsonUnit=nullSetMethod.invoke(object).toString();
                jsonUnit=CharUtil.stringToJson(jsonUnit,true);
            }
            json.append(jsonUnit);
        }
        return json.toString();
    }

    public void getObject(Object object,Map<String,Object> fieldValueMap) throws InvocationTargetException, IllegalAccessException {
        Method method;
        Object fieldValue;
        String temp;
        for(int index=0;index < fieldsCount;index++){
            temp=fieldName[index];
            fieldValue=fieldValueMap.get(temp);
            method=setMethods[index];
            method.invoke(object,fieldValue);
        }
    }

    public void setFieldValueV2(ResultSet rs,int index,Clone clone) throws SQLException, InvocationTargetException, IllegalAccessException {
        Method method=setMethods[index];
        int javaValueTypeV2=javaValueTypesV2[index];
        switch (javaValueTypeV2){
            case Marco.JAVA_STRING:
                method.invoke(clone,rs.getString(index));
                break;
            case Marco.JAVA_INT:
                method.invoke(clone,rs.getInt(index));
                break;
            case Marco.JAVA_LONG:
                method.invoke(clone,rs.getLong(index));
                break;
            case Marco.JAVA_DOUBLE:
                method.invoke(clone,rs.getDouble(index));
                break;
            case Marco.JAVA_BOOLEAN:
                method.invoke(clone,rs.getBoolean(index));
                break;
            case Marco.JAVA_TIMESTAMP:
                method.invoke(clone,rs.getTimestamp(index));
                break;
            case Marco.PLATFORM_ENUM_DBSQLCONVALTYPE:
                method.invoke(clone,DBSQLConValType.valueOf(rs.getString(index)));
                break;
            case Marco.PLATFORM_ENUM_JAVAVALUETYPE:
                method.invoke(clone,JavaValueType.valueOf(rs.getString(index)));
                break;
            case Marco.PLATFORM_ENUM_DBTRANPPGTYPE:
                method.invoke(clone,DBTranPpgType.valueOf(rs.getString(index)));
                break;
            case Marco.PLATFORM_ENUM_DBTRANLVLTYPE:
                method.invoke(clone,DBTranLvlType.valueOf(rs.getString(index)));
                break;
            case Marco.PLATFORM_ENUM_RESOURCETYPE:
                method.invoke(clone,ResourceType.valueOf(rs.getString(index)));
                break;
            case Marco.PLATFORM_ENUM_DEVELOPTYPE:
                method.invoke(clone,DevelopType.valueOf(rs.getString(index)));
                break;
            case Marco.PLATFORM_ENUM_DBOPERTYPE:
                method.invoke(clone, DBOperType.valueOf(rs.getString(index)));
                break;
            case Marco.PLATFORM_ENUM_PROXYTYPE:
                System.out.println(method.getName());
                method.invoke(clone,ProxyType.valueOf(rs.getString(index)));
                break;
            case Marco.PLATFORM_ENUM_USERROLE:
                method.invoke(clone, UserRole.valueOf(rs.getString(index)));
                break;
            case Marco.PLATFORM_ENUM_DBTYPE:
                method.invoke(clone, DBType.valueOf(rs.getString(index)));
                break;
            case Marco.PLATFORM_ENUM_DEVICETYPE:
                method.invoke(clone, DeviceType.valueOf(rs.getString(index)));
                break;
            case Marco.PLATFORM_ENUM_CLUSTERTYPE:
                method.invoke(clone, ClusterType.valueOf(rs.getString(index)));
                break;
            case Marco.PLATFORM_ENUM_SESSIONTYPE:
                method.invoke(clone,SessionType.valueOf(rs.getString(index)));
                break;
            case Marco.PLATFORM_ENUM_PRIVETYPE:
                method.invoke(clone,PriveType.valueOf(rs.getString(index)));
                break;
            case Marco.JAVA_FLOAT:
                method.invoke(clone,rs.getFloat(index));
                break;
            case Marco.JAVA_INTEGER:
                method.invoke(clone,(Integer)rs.getInt(index));
                break;
            case Marco.PLATFORM_ENUM_SERVICEMETHODTYPE:
                method.invoke(clone,ServiceMethodType.valueOf(rs.getString(index)));
                break;
        }
    }

    public static void main(String[] args){
        Field[] fields=ObjectFactory.class.getDeclaredFields();
        for(int index=0;index < fields.length;index++){
            System.out.println(fields[index].getType().getCanonicalName());
        }
    }

    public void setParameterV2(PreparedStatement pst, int index,Clone clone) throws SQLException {
        Object value = null;
        Method method;
        method=getMethods[index];
        int parType=javaValueTypesV2[index];
        try {
            value=method.invoke(clone);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        switch (parType){
            case Marco.JAVA_STRING:
                pst.setString(index,(String)value);
                break;
            case Marco.JAVA_INT:
                pst.setInt(index,(Integer) value);
                break;
            case Marco.JAVA_LONG:
                pst.setLong(index,(Long) value);
                break;
            case Marco.JAVA_DOUBLE:
                pst.setDouble(index,(Double) value);
                break;
            case Marco.JAVA_BOOLEAN:
                pst.setBoolean(index,(Boolean) value);
                break;
            case Marco.JAVA_TIMESTAMP:
                pst.setTimestamp(index,(Timestamp)value);
                break;
            case Marco.JAVA_FLOAT:
                pst.setFloat(index,(Float) value);
            case Marco.JAVA_INTEGER:
                pst.setInt(index,(Integer)value);
            default:
                pst.setString(index,value.toString());
        }
    }

    public static Object getBean(String keyOfBean){
        return objectOfMapContainer.get(keyOfBean);
    }

    public static Object getBean(Integer indexOfBean){
        return objectOfListContainer.get(indexOfBean);
    }

    public static Clone getBean(String keyOfBean,Object ext){
        return ((Clone)objectOfMapContainer.get(keyOfBean)).clone();
    }

    public static Clone getBean(Integer keyOfIndex,Object ext){
        return ((Clone)objectOfListContainer.get(keyOfIndex)).clone();
    }

    public Method[] getSetMethods() {
        return setMethods;
    }

    public void setSetMethods(Method[] setMethods) {
        this.setMethods = setMethods;
    }

    public Method[] getGetMethods() {
        return getMethods;
    }

    public void setGetMethods(Method[] getMethods) {
        this.getMethods = getMethods;
    }

    public Clone getClone() {
        return clone;
    }

    public void setClone(Clone clone) {
        this.clone = clone;
    }

    public int getFieldsCount() {
        return fieldsCount;
    }

    public void setFieldsCount(int fieldsCount) {
        this.fieldsCount = fieldsCount;
    }

    public String[] getJsons() {
        return jsons;
    }

    public void setJsons(String[] jsons) {
        this.jsons = jsons;
    }

    public int[] getNullJsonIndexes() {
        return nullJsonIndexes;
    }

    public void setNullJsonIndexes(int[] nullJsonIndexes) {
        this.nullJsonIndexes = nullJsonIndexes;
    }

    public void setFieldName(String[] fieldName) {
        this.fieldName = fieldName;
    }

    public String[] getFieldName() {
        return fieldName;
    }

    public int[] getJavaValueTypesV2() {
        return javaValueTypesV2;
    }

    public void setJavaValueTypesV2(int[] javaValueTypesV2) {
        this.javaValueTypesV2 = javaValueTypesV2;
    }

}
