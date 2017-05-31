package nna.base.bean.dbbean;

import nna.base.bean.Clone;
import nna.enums.ClusterType;

/**
 * @author NNA-SHUAI
 * @create 2017-05-19 9:29
 **/

public class PlatformCluster extends Clone{
    private static final Long serialVersionUID=12L;

    private int clusterId;
    private ClusterType clusterType;
    private int appId;
    private String clusterEn;
    private String clusterCh;
    private String clusterDesc;

    public int getClusterId() {
        return clusterId;
    }

    public void setClusterId(int clusterId) {
        this.clusterId = clusterId;
    }

    public ClusterType getClusterType() {
        return clusterType;
    }

    public void setClusterType(ClusterType clusterType) {
        this.clusterType = clusterType;
    }

    public String getClusterEn() {
        return clusterEn;
    }

    public void setClusterEn(String clusterEn) {
        this.clusterEn = clusterEn;
    }

    public String getClusterCh() {
        return clusterCh;
    }

    public void setClusterCh(String clusterCh) {
        this.clusterCh = clusterCh;
    }

    public String getClusterDesc() {
        return clusterDesc;
    }

    public void setClusterDesc(String clusterDesc) {
        this.clusterDesc = clusterDesc;
    }

    public int getAppId() {
        return appId;
    }

    public void setAppId(int appId) {
        this.appId = appId;
    }
}
