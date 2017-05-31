package nna.base.bean.dbbean;

import nna.base.bean.Clone;

/**
 * for db table of service column
 * @author NNA-SHUAI
 * @create 2017-05-13 17:10
 **/

public class PlatformColumn extends Clone {
    private static final Long serialVersionUID=20L;

    private String columnId;
    private int columnNo;
    private String columnDefaultvalue;
    private String columnOutsideName;
    private String columnInnerName;
    private int columnLength;
    private boolean columnIsarray;
    private String columnFormat;
    private boolean columnIsmust;
    private String columnDesc;

    public PlatformColumn(){

    }

    public String getColumnId() {
        return columnId;
    }

    public void setColumnId(String columnId) {
        this.columnId = columnId;
    }

    public int getColumnNo() {
        return columnNo;
    }

    public void setColumnNo(int columnNo) {
        this.columnNo = columnNo;
    }

    public String getColumnOutsideName() {
        return columnOutsideName;
    }

    public void setColumnOutsideName(String columnOutsideName) {
        this.columnOutsideName = columnOutsideName;
    }

    public String getColumnInnerName() {
        return columnInnerName;
    }

    public void setColumnInnerName(String columnInnerName) {
        this.columnInnerName = columnInnerName;
    }

    public int getColumnLength() {
        return columnLength;
    }

    public void setColumnLength(int columnLength) {
        this.columnLength = columnLength;
    }

    public boolean isColumnIsarray() {
        return columnIsarray;
    }

    public void setColumnIsarray(boolean columnIsarray) {
        this.columnIsarray = columnIsarray;
    }

    public String getColumnFormat() {
        return columnFormat;
    }

    public void setColumnFormat(String columnFormat) {
        this.columnFormat = columnFormat;
    }

    public boolean isColumnIsmust() {
        return columnIsmust;
    }

    public void setColumnIsmust(boolean columnIsmust) {
        this.columnIsmust = columnIsmust;
    }

    public String getColumnDesc() {
        return columnDesc;
    }

    public void setColumnDesc(String columnDesc) {
        this.columnDesc = columnDesc;
    }

    public String getColumnDefaultvalue() {
        return columnDefaultvalue;
    }

    public void setColumnDefaultvalue(String columnDefaultvalue) {
        this.columnDefaultvalue = columnDefaultvalue;
    }
}
