package nna.base.bean.dbbean;

/**
 * @author NNA-SHUAI
 * @create 2017-06-11 13:01
 **/

public class PlatformProtocol {
    private int entryId;
    private String entryClass;
    private String entryMethod;
    private int entryPort;

    public int getEntryId() {
        return entryId;
    }

    public void setEntryId(int entryId) {
        this.entryId = entryId;
    }

    public String getEntryClass() {
        return entryClass;
    }

    public void setEntryClass(String entryClass) {
        this.entryClass = entryClass;
    }

    public String getEntryMethod() {
        return entryMethod;
    }

    public void setEntryMethod(String entryMethod) {
        this.entryMethod = entryMethod;
    }

    public int getEntryPort() {
        return entryPort;
    }

    public void setEntryPort(int entryPort) {
        this.entryPort = entryPort;
    }
}
