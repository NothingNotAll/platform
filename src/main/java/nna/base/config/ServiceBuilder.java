package nna.base.config;
/**
 * Created by NNA-SHUAI on 2017/4/24.
 */

import org.apache.poi.hssf.usermodel.HSSFRow;

import java.io.IOException;

/**
 * Configuration the service with xls
 *
 * @author
 * @create 2017-04-24 20:57
 **/

public interface ServiceBuilder {
    public ServiceBuilder buildXLXCfgFile(String xlsFileName) throws IOException;

    public ServiceBuilder buildCfgApp();

    public ServiceBuilder buildController();

    public ServiceBuilder buildService();

    public ServiceBuilder buildRequest();

    public ServiceBuilder buildResponse();

    public ServiceBuilder buildBusinessAction();

    public ServiceBuilder buildBBMap();

    public ServiceBuilder buildDBSQL();

    public ServiceBuilder buildCreator();

    public ServiceBuilder buildUpdator();

    public ServiceBuilder buildSource();

    public ServiceBuilder buildTmeplateMethod(String sheetName);

    public ServiceConfig getServiceConfig();

    public ServiceConfig buildDefaultCfg(String cfgXlSFileName) throws IOException;

    public interface RowMapper<T>{
        public T mapper(HSSFRow row);
    }
}
