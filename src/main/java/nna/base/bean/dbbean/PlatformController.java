package nna.base.bean.dbbean;

import nna.base.bean.Clone;

import java.sql.Timestamp;

/**
 * for db table of controller
 * @author NNA-SHUAI
 * @create 2017-05-13 16:14
 **/

public class PlatformController extends Clone {
    private static final Long serialVersionUID=15L;

    private Integer id;
    private boolean status;
    private String renderClass;
    private String renderMethod;
    private String renderPage;
    private Timestamp createTimestamp;
    private Timestamp updateTimestamp;
    private String controllerDesc;

    public PlatformController(){

    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getRenderClass() {
        return renderClass;
    }

    public void setRenderClass(String renderClass) {
        this.renderClass = renderClass;
    }

    public String getRenderPage() {
        return renderPage;
    }

    public void setRenderPage(String renderPage) {
        this.renderPage = renderPage;
    }

    public String getRenderMethod() {
        return renderMethod;
    }

    public void setRenderMethod(String renderMethod) {
        this.renderMethod = renderMethod;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public Timestamp getCreateTimestamp() {
        return createTimestamp;
    }

    public void setCreateTimestamp(Timestamp createTimestamp) {
        this.createTimestamp = createTimestamp;
    }

    public Timestamp getUpdateTimestamp() {
        return updateTimestamp;
    }

    public void setUpdateTimestamp(Timestamp updateTimestamp) {
        this.updateTimestamp = updateTimestamp;
    }

    public String getControllerDesc() {
        return controllerDesc;
    }

    public void setControllerDesc(String controllerDesc) {
        this.controllerDesc = controllerDesc;
    }

}
