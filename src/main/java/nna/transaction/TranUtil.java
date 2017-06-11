package nna.transaction;

import nna.Marco;
import nna.base.bean.dbbean.PlatformSql;
import nna.base.init.NNAServiceInit0;
import nna.base.log.Log;
import nna.base.protocol.dispatch.AppUtil;
import nna.base.util.BuildSQL;
import nna.enums.DBSQLConValType;

import java.sql.*;
import java.util.HashMap;

/**
 * @author NNA-SHUAI
 * @create 2017-06-09 11:24
 **/

public class TranUtil {

    public static String arrayToString(String[] strs,String spit){
        int size=strs.length;
        StringBuilder stringBuilder=new StringBuilder("");
        for(int index=0;index < size;index++){
            if(stringBuilder.equals("")){
                stringBuilder.append(strs[index]);
            }else{
                stringBuilder.append(","+strs[index]);
            }
        }
        return stringBuilder.toString();
    }

    public static ResultSet[] initRS(PreparedStatement[] psts) throws SQLException {
        int count=psts.length;
        ResultSet[] rs=new ResultSet[count];
        for(int index=0;index<count;index++){
            rs[index]=psts[index].executeQuery();
        }
        return rs;
    }

    public static Integer[] getPageParam(Integer totalLines,
                                            Integer currentPage,
                                            Integer pageSize,
                                            Integer pageFlag) throws SQLException{
        Integer totalPage=totalLines/pageSize;
        if(totalPage==0){
            if(totalLines > 0){
                totalPage=1;
            }
        }else{
            if(totalLines%pageSize>0){
                totalPage+=1;
            }
        }
        AppUtil.putTemp(Marco.TOTALPAGE, new String[]{totalPage.toString()});
        Integer begin=(currentPage-1)*pageSize;
        Integer end=(currentPage)*pageSize;
        return new Integer[]{begin,end,pageFlag};
    }

    public static void setRsReverse(Integer totalPage,
                                       Integer currentPage,
                                       Integer pageFlag,
                                       ResultSet rs) throws SQLException {
        boolean isNeed=false;
        switch (pageFlag) {
            case 0://首页
            case 1://末页
            case 2://上一页
                Integer goalPage=currentPage-1;
                Integer middle=totalPage/2;
                isNeed= goalPage>middle;
            case 3://下一页
                Integer nextPage=currentPage+1;
                Integer middle1=totalPage/2;
                isNeed= nextPage>middle1;
            case 4://当前页
                Integer middle11=totalPage/2;
                isNeed= currentPage>middle11;
            default:
                break;
        }
        if(isNeed){
            rs.setFetchDirection(ResultSet.FETCH_REVERSE);
        }
    }


    public void close(ResultSet rs) throws SQLException {
        if(rs!=null){
            rs.close();
        }
    }

    public void close(Statement st) throws SQLException {
        if(st!=null){
            st.close();
        }
    }

    public void close(Connection connection) throws SQLException {
        if(connection!=null){
            connection.close();
        }
    }

    public void close(Connection connection, Statement[] sts) throws SQLException {
        int size=sts.length;
        Statement statement;
        for(int index=0;index < size; index++){
            try{
                statement=sts[index];
                close(statement);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        close(connection);
    }

    public void close(ResultSet[] rss) throws SQLException {
        int length=rss.length;
        ResultSet rs;
        for(int index=0;index < length;index++){
            rs=rss[index];
            try{
                if(rs!=null){
                    close(rs);
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    public ResultSet setPstParaAndExePst(PreparedStatement pst, String[] conNms, DBSQLConValType[] dbsqlConValTypes, HashMap<String,String[]> conMap) throws SQLException {
        setPstParameter(pst,conNms,dbsqlConValTypes,conMap,0);
        return pst.executeQuery();
    }

    public void setPstParameter(PreparedStatement pst,String[] conNms,DBSQLConValType[] dbsqlConValTypes,HashMap<String,String[]> conMap,int valIndex) throws SQLException {
        int parameterCount=conNms.length;
        String tempNm;
        DBSQLConValType tempValType;
        String[] conVal;
        String conValue;
        for(int index=0;index < parameterCount;index++){
            tempNm=conNms[index];
            conVal=conMap.get(tempNm);
            conValue=conVal[valIndex];
            tempValType=dbsqlConValTypes[index];
            switch (tempValType){
                case INTEGER:
                    pst.setInt(index,Integer.valueOf(conValue));
                    break;
                case BOOLEAN:
                    pst.setBoolean(index,Boolean.valueOf(conValue));;
                    break;
                case STRING:
                    pst.setString(index,conValue);;
                    break;
            }
        }
    }
}
