package nna.base.util;


import nna.Marco;
import nna.base.cache.NNAServiceInit0;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class BuildSQL {

	
	private BuildSQL(){};

	public static String buildPageSQL(String sql){
		switch (Marco.dbType) {
			case mysql:
				sql+=" LIMIT ?,?";
				break;
			case oracle:
				break;
			default:
				break;
		}
		return sql;
	}
	
	public static String insert(String columns,String tableName) {
		int columnCount=columns.split("[,]").length;
		columnCount=columnCount==0?1:columnCount;
		String insertSQL="INSERT INTO "+tableName+" ("+columns+") VALUES(";
		int finalIndex=columnCount-1;
		if (finalIndex==0) {
			insertSQL+="?";
		}else{
			for (int i = 0; i < finalIndex; i++) {
				insertSQL+="?,";
			}
			insertSQL+="?)";
		}
		return insertSQL;
	}
	
	public static String selectNoCondition(String[] columns,String tableName) {
		String selectSqlStr="SELECT ";
		int finalIndex=columns.length-1;
		if (finalIndex == 0) {
			selectSqlStr+=columns[0];
		}else {
			for (int i = 0; i < finalIndex; i++) {
				selectSqlStr+=columns[i];
				selectSqlStr+=",";
			}
			selectSqlStr+=columns[finalIndex];
		}
		selectSqlStr+=" FROM ";
		selectSqlStr+=tableName;
		return selectSqlStr;
	}

	public static String selectWithCondition(String[] columns,String tableName,String[] conditionColumns,String[] comparsisonSymbols,String[] logicSymbols) {
		String selectSqlStr=selectNoCondition(columns,tableName);
		return withConditions(selectSqlStr, conditionColumns, comparsisonSymbols,logicSymbols);
	}

	public static String selectWithCondition(String[] columns,String tableName,String[] conditionColumns) {
		String selectSqlStr=selectNoCondition(columns,tableName);
		return withConditions(selectSqlStr, conditionColumns);
	}

	public static String updateNoCondition(String[] updateColumns,String tableName) {
		String updateSqlStr=" UPDATE "+tableName+" SET ";
		int finalIndex=updateColumns.length-1;
		if (finalIndex == 0) {
			updateSqlStr+="= ";
			updateSqlStr+="?";
		}else {
			for (int i = 0; i < finalIndex; i++) {
				updateSqlStr+=updateColumns[i];
				updateSqlStr+="= ";
				updateSqlStr+="?";
				updateSqlStr+=",";
			}
			updateSqlStr+=updateColumns[finalIndex];
			updateSqlStr+="=";
			updateSqlStr+="?";
		}
		return updateSqlStr;
	}

	public static String updateWithCondition(String[] updateColumns,String[] conditionColumns,String[] comparisonSymbols,String[] logicSymbols,String tableName) {
		String updateSqlStr=updateNoCondition(updateColumns, tableName);
		return withConditions(updateSqlStr, conditionColumns,comparisonSymbols,logicSymbols);
	}

	public static String deleteNoCondition(String tableName) {
		String deleteSqlStr="DELETE FROM ";
		deleteSqlStr+=tableName;
		return deleteSqlStr;
	}

	public static String deleteWithCondition(String tableName,String[] conditionColumns,String[] comparsisonSymbols,String[] symbols){
		String deleteSqlStr=deleteNoCondition(tableName);
		return withConditions(deleteSqlStr, conditionColumns, comparsisonSymbols,symbols);
	}

	public static String deleteWithCondition(String tableName,String[] conditionColumns){
		String deleteSqlStr=deleteNoCondition(tableName);
		return withConditions(deleteSqlStr, conditionColumns);
	}

	public static String withConditions(String sqlStr,String[] conditionColumns) {

		sqlStr+="  WHERE ";
		int finalIndex=conditionColumns.length-1;
		if (finalIndex == 0) {
			sqlStr+=" ";
			sqlStr+=conditionColumns[0];
			sqlStr+=" = ";
			sqlStr+="?";
		}else {
			for (int i = 0; i < finalIndex; i++) {
				sqlStr+=" ";
				sqlStr+=conditionColumns[i];
				sqlStr+=" = ";
				sqlStr+="?";
				sqlStr+=" AND ";
			}
			sqlStr+=" ";
			sqlStr+=conditionColumns[finalIndex];
			sqlStr+=" = ";
			sqlStr+="?";
		}
		return sqlStr;
	
	}

	public static String withConditions(String sqlStr,String[] conditionColumns,String[] comparsionSymbols,String[] symbols) {
		sqlStr+="  WHERE ";
		int finalIndex=conditionColumns.length-1;
		if (finalIndex == 0) {
			sqlStr+=" ";
			sqlStr+=conditionColumns[0];
			sqlStr+=comparsionSymbols[0];
			sqlStr+="?";
		}else {
			for (int i = 0; i < finalIndex; i++) {
				sqlStr+=" ";
				sqlStr+=conditionColumns[i];
				sqlStr+=comparsionSymbols[i];
				sqlStr+=" ? ";
				sqlStr+=symbols[i];
			}
			sqlStr+=" ";
			sqlStr+=conditionColumns[finalIndex];
			sqlStr+=comparsionSymbols[finalIndex];
			sqlStr+=" ? ";
		}
		return sqlStr;
	}
	public static String getTableCols(Connection con,String tableName) throws SQLException {
	    tableName=tableName.trim();
        PreparedStatement pst = null;
	    switch (Marco.dbType){
            case mysql:
                pst=con.prepareStatement(Marco.MYSQL_SELECT_TABLE_COLS);
                break;
            case oracle:
                ;
                break;
        }
        pst.setString(1,tableName);
        ResultSet rs=pst.executeQuery();
        rs.next();
        String colNames=rs.getString(1);
        rs.close();
        pst.close();
        return colNames;
    }

    public static  String getJavaColFromDBCol(String dbCols){
	    if(dbCols==null){
	        return "";
        }
        String[] colName=dbCols.split("[,]");
        StringBuilder javaCols=new StringBuilder("");
        for(int index=0;index < colName.length;index++){
            String temp=colName[index];
            String[] temps=temp.split("[_]");
            StringBuilder str=new StringBuilder("");
            for(int i=0;i< temps.length;i++){
                if(!temps[i].trim().equals("")){
                    if(i==0){
                        str.append(temps[i].toLowerCase());
                    }else{
                        str.append(getUpper(temps[i]));
                    }
                }
            }
            if(javaCols.toString().equals("")){
                javaCols.append(str);
            }else{
                javaCols.append(","+str.toString());
            }
        }
        return javaCols.toString();
    }
    public static String getTablePrimaryKey(Connection con,String tableName) throws SQLException {
        PreparedStatement pst=con.prepareStatement(Marco.MYSQL_SELECT_PRVIMARY_KEY);
        pst.setString(1,tableName);
        ResultSet rs=pst.executeQuery();
        StringBuilder str=new StringBuilder("");
        while(rs.next()){
            if(str.toString().equals("")){
                str.append(rs.getString(3));
            }else{
                str.append(","+rs.getString(3));
            }
        }
        rs.close();
        pst.close();
        return str.toString();
    }

    public static String getUpper(String str){
        if(str.length()==1){
            return str.toUpperCase();
        }else{
            if(str.contains(" ")||str.contains("\t")){
                str=str.split("[\r||\n||\t]+")[0];
            }
            if(str.contains(".")){
                str=str.substring(str.indexOf(".")+1);
            }
            return str.substring(0,1).toUpperCase()+str.substring(1).toLowerCase();
        }
    }

	public static void main(String[] args) {
//		System.out.println(BuildSQL.deleteNoCondition("tableName"));
//		System.out.println(BuildSQL.deleteWithCondition("sd", new String[]{"s1v","sds"}));
//		System.out.println(BuildSQL.deleteWithCondition("sd", new String[]{"s1v","sds"}, new String[]{"=",">="}, new String[]{"or","and"}));
//		System.out.println(BuildSQL.deleteWithCondition("sd", new String[]{"s1v"}, new String[]{"=",">="}, new String[]{"or"}));
//		System.out.println(BuildSQL.selectNoCondition(new String[]{"s1","s1","sd"}, "ss"));
//		System.out.println(BuildSQL.selectWithCondition(new String[]{"s1v","sds"}, "sdfd", new String[]{"s1v","sds"}, new String[]{">","="}, new String[]{"or","sds"}));
//		System.out.println(BuildSQL.updateNoCondition(new String[]{"s1"}, new String[]{"'s1'  or 1=1 or 1=1 "}, "sfa"));
//		System.out.println(BuildSQL.updateWithCondition(new String[]{"s1","sd"}, new String[]{"s1","sd"}, new String[]{"s1"}, new String[]{"s1"}, "sfa"));
//		String[][] values={{"1","2","3"},{"4","5","6"},{"7","8","9"}};
//		System.out.println(BuildSQL.insert("APP_EN,URL,SERVICE_NAME,RENDER_CLASSNAME,RENDER_METHODNAME,STATUS,RENDER_PAGENAME,CONTROLLER_DESC", "tabless"));
//        try {
//            System.out.println(getUpper("s"));
//            System.out.println(getUpper("sls"));
//            System.out.println(getTablePrimaryKey(NNAServiceInit0.init(),"platform_sql"));
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//        System.out.println("1.33".substring("1.33".indexOf(".")+1));
        try {
            String cols=getTableCols(NNAServiceInit0.init(),"platform_APP");
			System.out.println(cols);
			String javaCols=getJavaColFromDBCol(cols);
            String[] jCols=javaCols.split("[,]");
            int count=jCols.length;
            for(int index=0;index<count;index++){
                System.out.println(jCols[index]+";");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
