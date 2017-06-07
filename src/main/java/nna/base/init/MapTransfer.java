package nna.base.init;

import nna.base.bean.Clone;
import nna.base.util.ObjectUtil;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import static nna.transaction.AbstractTransaction.getBean;

/**
 * @author NNA-SHUAI
 * @create 2017-05-24 22:31
 **/




 class MapTransfer<T>{

     MapTransfer(){}

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
        }
        rs.close();
        pst.close();
        return map;
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
            map.put((String)method.invoke(clone),clone);
        }
        rs.close();
        pst.close();
        return map;
    }

}
