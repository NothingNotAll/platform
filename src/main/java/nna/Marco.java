package nna;


import nna.enums.DBType;

public interface Marco {
    DBType dbType=DBType.mysql;
    String DRIVER="com.mysql.jdbc.Driver";
    String URL="jdbc:mysql://localhost:3306/platformv2";
    String ACCOUNT="root";
    String PASSWORD="123";
    boolean IS_DISTRIBUTED=false;

    String INIT_TABLENAME_PLATFORMCLAZZ="PLATFORM_CLAZZ",
            INIT_TABLENAME_PLATFORMLOG="PLATFORM_LOG",
            INIT_TABLENAME_PLATFROMTRANSACTION="PLATFORM_TRANSACTION",
            INIT_TABLENAME_PLATFORMSQL="PLATFORM_SQL",
            CLAZZ_COUNT="SELECT COUNT(*) FROM PLATFORM_CLAZZ",
            MYSQL_SELECT_TABLE_COLS="SELECT " +
                    "GROUP_CONCAT(COLUMN_NAME) " +
                    "from " +
                    "information_schema.`COLUMNS` where TABLE_NAME=?",
            MYSQL_SELECT_PRVIMARY_KEY="SELECT\n" +
                    "\tt.TABLE_NAME,\n" +
                    "\tt.CONSTRAINT_TYPE,\n" +
                    "\tc.COLUMN_NAME,\n" +
                    "\tc.ORDINAL_POSITION\n" +
                    "FROM\n" +
                    "\tINFORMATION_SCHEMA.TABLE_CONSTRAINTS AS t,\n" +
                    "\tINFORMATION_SCHEMA.KEY_COLUMN_USAGE AS c\n" +
                    "WHERE\n" +
                    "\tt.TABLE_NAME = c.TABLE_NAME\n" +
                    "AND t.TABLE_SCHEMA = 'PLATFORMV2'\n" +
                    "AND t.CONSTRAINT_TYPE = 'PRIMARY KEY'\n" +
                    "AND T.TABLE_NAME = ?;";

    ReturnMessage BLANK=new ReturnMessage();
    ReturnMessage SUCCESS=new ReturnMessage();
    ReturnMessage nullException=new ReturnMessage("null exception",000001);
    ReturnMessage indexOutofException=new ReturnMessage("index out of exception",000002);
    ReturnMessage notExist=new ReturnMessage("不存在",000003);

    String SUCESS="S";
    String FAIL="F";
    int SUCCESSCODE=000000;
    int EXCEPTIONFAILCODE=111111;

	String TOTALPAGE="1totalPage";
	String TOTALCOUNT="1count";
	String CURRENT_PAGE="1currentPage";
	String PAGE_SIZE="1pageSize";
	String PAGE_FLAG="1pageFlag";
	String PAGE_BEGIN="1begin";
    String PAGE_END="1end";

    // for performance of orm db to bean , as general we use the serializable id as the bean id;
    int PLATFORM_APP=0;
    int PLATFORM_PROXY=1;
    int PLATFORM_RESOURCE=2;
    int PLATFORM_ROLE=3;
    int PLATFORM_ROLE_RESOURCE=4;
    int PLATFORM_SERVICE=5;
    int PLATFORM_SERVICE_TRANSACTION=6;
    int PLATFORM_SQL=7;
    int PLATFORM_TRANSACTION=8;
    int PLATFORM_USER=9;
    int PLATFORM_USER_ROLE=10;
    int PLATFORM_CLUSTER=11;
    int PLATFORM_DEVICE=12;
    int PLATFORM_CLUSTER_DEVICE=13;
    int PLATFORM_CONTROLLER=14;
    int PLATFORM_DB=15;
    int PLATFORM_DEVELOP_VERSION=16;
    int PLATFORM_ENTRY=17;
    int PLATFORM_LOG=18;
    int PLATFORM_COLUMN=19;
    int PLATFORM_CLAZZ=20;
    int PLATFORM_SESSION=21;


    //JAVA_VALUE_TYPE FOR ORM performance
    int JAVA_INT=0;
    int JAVA_STRING=1;
    int JAVA_BOOLEAN=2;
    int JAVA_LONG=3;
    int JAVA_DOUBLE=4;
    int JAVA_TIMESTAMP=5;
    int PLATFORM_ENUM_DBOPERTYPE=6;
    int PLATFORM_ENUM_DBSQLCONVALTYPE=7;
    int PLATFORM_ENUM_DBTRANLVLTYPE=8;
    int PLATFORM_ENUM_DBTRANPPGTYPE=9;
    int PLATFORM_ENUM_DEVELOPTYPE=10;
    int PLATFORM_ENUM_DBTYPE=11;
    int PLATFORM_ENUM_JAVAVALUETYPE=12;
    int PLATFORM_ENUM_PROXYTYPE=13;
    int PLATFORM_ENUM_RESOURCETYPE=14;
    int PLATFORM_ENUM_USERROLE=15;
    int PLATFORM_ENUM_CLUSTERTYPE=16;
    int PLATFORM_ENUM_DEVICETYPE=17;
    int PLATFORM_ENUM_SESSIONTYPE=18;
    int PLATFORM_ENUM_PRIVETYPE=19;
    int JAVA_FLOAT = 20;
    int JAVA_INTEGER =21 ;
    int PLATFORM_ENUM_SERVICEMETHODTYPE=22;

    //total confMeta
    int LOGIN=1;
    int FREE=2;
    int LOGIN_PASSWORD_ERROR = 3;

    //global transaction name;
    String TRAN_SEL_ALL_ROLE="selAllRole";
    String TRAN_SEL_USER_INFO="selUserInfo";
    String TRAN_NAME="TRANSACTION_NAME";
    String ARRAY_COUNT="count";
    String HEAD_ENTRY_CODE = "/head/entryCode";
    String HEAD_ENTRY_SESSION_NM = "";

    String LOG_TIME_DIR = "HH-mm-ss-SSS";
    String YYYYMMDD_DIR = "yyyyMMdd";
}
