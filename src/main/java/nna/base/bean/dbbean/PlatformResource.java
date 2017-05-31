package nna.base.bean.dbbean;

import nna.base.bean.Clone;
import nna.enums.ResourceType;

/**
 * @author NNA-SHUAI
 * @create 2017-05-15 17:59
 **/

public class PlatformResource extends Clone {
    private static final Long serialVersionUID=3L;

    private int resourceId;
    private String resourceName;
    private ResourceType resourceType;
    private int resourcePk;

    public String getResourceName() {
        return resourceName;
    }

    public void setResourceName(String resourceName) {
        this.resourceName = resourceName;
    }

    public int getResourceId() {
        return resourceId;
    }

    public void setResourceId(int resourceId) {
        this.resourceId = resourceId;
    }

    public ResourceType getResourceType() {
        return resourceType;
    }

    public void setResourceType(ResourceType resourceType) {
        this.resourceType = resourceType;
    }

    public int getResourcePk() {
        return resourcePk;
    }

    public void setResourcePk(int resourcePk) {
        this.resourcePk = resourcePk;
    }
}
