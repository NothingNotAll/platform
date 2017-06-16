package nna.transaction;

import nna.Marco;
import nna.base.dispatch.AppUtil;

import java.sql.*;

/**
 * @author NNA-SHUAI
 * @create 2017-06-09 11:24
 **/

public class TranUtil {

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
}
