package nna.base.init;

import nna.Marco;
import nna.base.bean.Clone;
import nna.base.bean.dbbean.PlatformProtocol;
import nna.base.util.orm.ObjectUtil;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

import static nna.base.util.orm.ObjectFactory.getBean;


/**
 * @author NNA-SHUAI
 * @create 2017-05-24 22:31
 **/




 class MapReduce<T>{

     MapReduce(){}

     HashMap<Integer,T> getIMap(HashMap<Integer,Clone> iMap){
        HashMap<Integer,T> map=new HashMap<Integer, T>(iMap.size());
        Iterator<Map.Entry<Integer,Clone>> iterator=iMap.entrySet().iterator();
        while(iterator.hasNext()){
            Map.Entry<Integer,Clone> entry=iterator.next();
            T t=(T)entry.getValue();
            map.put(entry.getKey(),t);
        }
        return map;
    }
     HashMap<String,T> getSMap(HashMap<String,Clone> sMap){
        HashMap<String,T> map=new HashMap<String, T>(sMap.size());
        Iterator<Map.Entry<String,Clone>> iterator=sMap.entrySet().iterator();
        while(iterator.hasNext()){
            Map.Entry<String,Clone> entry=iterator.next();
            T t=(T)entry.getValue();
            map.put(entry.getKey(),t);
        }
        return map;
    }



     static HashMap<Integer,Clone> getIMap(PreparedStatement pst, String getMethodName, int serializableId) throws SQLException, ClassNotFoundException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException {
        assert pst!=null;
        assert serializableId > 0;
        ResultSet rs=pst.executeQuery();
        Method method;
        HashMap<Integer,Clone> map=new HashMap<Integer, Clone>();
        while(rs.next()){
            Clone clone= getBean(rs,serializableId);
            method= ObjectUtil.loadMethodFromObjectAndMethodName(clone,getMethodName);
            map.put((Integer)method.invoke(clone),clone);
//            test(clone,serializableId);
        }
        rs.close();
        pst.close();
        return map;
    }
    static void test(Clone clone,int serializableId){
        if(serializableId== Marco.PLATFORM_PROTOCOL){
            System.out.println(((PlatformProtocol)clone).getProtocolType());
        }
    }

     static HashMap<String,Clone> getSMap(PreparedStatement pst,String getMethodName,int serializableId) throws SQLException, ClassNotFoundException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException {
        assert pst!=null;
        assert serializableId > 0;
        ResultSet rs=pst.executeQuery();
        Method method;
        HashMap<String,Clone> map=new HashMap<String, Clone>();
        while(rs.next()){
            Clone clone= getBean(rs,serializableId);
            method=ObjectUtil.loadMethodFromObjectAndMethodName(clone,getMethodName);
            map.put(method.invoke(clone).toString(),clone);
        }
        rs.close();
        pst.close();
        return map;
    }

    static void getList(
            List<Clone> list,
            PreparedStatement pst,
            int serializableId
    ) throws SQLException, ClassNotFoundException, InvocationTargetException, InstantiationException, IllegalAccessException {
        ResultSet rs=pst.executeQuery();
        Clone clone;
        while(rs.next()){
            clone= getBean(rs,serializableId);
            list.add(clone);
        }
        rs.close();
        pst.close();
    }

    public void getList(List<Clone> list,List<T> tList){
         Iterator<Clone> iterator=list.iterator();
         T t;
         while(iterator.hasNext()){
             t=(T)iterator.next();
             tList.add(t);
         }
    }

    public void reduceSList(List list,
                              String getMethodName,
                              Map<String,ArrayList> map) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Iterator iterator=list.iterator();
        while(iterator.hasNext()){
            T t=(T)iterator.next();
            Method method=ObjectUtil.loadMethodFromObjectAndMethodName(t,getMethodName);
            String key=method.invoke(t).toString();
            List temp=map.get(key);
            put(temp,t,key,map);
        }
    }

    public void reduceIList(List list,
                             String getMethodName,
                             Map<Integer,ArrayList> map) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Iterator iterator=list.iterator();
        while(iterator.hasNext()){
            T t=(T)iterator.next();
            Method method=ObjectUtil.loadMethodFromObjectAndMethodName(t,getMethodName);
            Integer key=(Integer)method.invoke(t);
            List temp=map.get(key);
            put(temp,t,key,map);
        }
    }

    private void put(List temp,
                     T t,
                     Object key,
                     Map map) {
        if(temp==null){
            temp=new ArrayList();
            temp.add(t);
            map.put(key,temp);
        }else{
            temp.add(t);
        }
    }

}
