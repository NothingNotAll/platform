package nna.base.config;/**
 * Created by NNA-SHUAI on 2017/4/24.
 */


import nna.base.bean.dbbean.*;

import java.util.ArrayList;

/**
 * the config of service
 *
 * @author
 * @create 2017-04-24 21:56
 **/

public class ServiceConfig {
    private ArrayList<PlatformController> controllers;
    private ArrayList<PlatformService> services;
    private ArrayList<PlatformColumn> request;
    private ArrayList<PlatformColumn> response;
    private ArrayList<PlatformTransaction> transactions;
    private ArrayList<PlatformServiceTransaction> serviceTransactions;
    private ArrayList<PlatformSql> sqlCfgs;
    private ArrayList<PlatformApp> apps;
    private ArrayList<PlatformDevelopVersion> creators;
    private ArrayList<PlatformDevelopVersion> updators;

    public ArrayList<PlatformController> getControllers() {
        return controllers;
    }

    public void setControllers(ArrayList<PlatformController> controllers) {
        this.controllers = controllers;
    }

    public ArrayList<PlatformService> getServices() {
        return services;
    }

    public void setServices(ArrayList<PlatformService> services) {
        this.services = services;
    }

    public ArrayList<PlatformColumn> getRequest() {
        return request;
    }

    public void setRequest(ArrayList<PlatformColumn> request) {
        this.request = request;
    }

    public ArrayList<PlatformTransaction> getTransactions() {
        return transactions;
    }

    public void setTransactions(ArrayList<PlatformTransaction> transactions) {
        this.transactions = transactions;
    }

    public ArrayList<PlatformServiceTransaction> getServiceTransactions() {
        return serviceTransactions;
    }

    public void setServiceTransactions(ArrayList<PlatformServiceTransaction> serviceTransactions) {
        this.serviceTransactions = serviceTransactions;
    }

    public ArrayList<PlatformSql> getSqlCfgs() {
        return sqlCfgs;
    }

    public void setSqlCfgs(ArrayList<PlatformSql> sqlCfgs) {
        this.sqlCfgs = sqlCfgs;
    }

    public ArrayList<PlatformApp> getApps() {
        return apps;
    }

    public void setApps(ArrayList<PlatformApp> apps) {
        this.apps = apps;
    }

    public ArrayList<PlatformDevelopVersion> getCreators() {
        return creators;
    }

    public void setCreators(ArrayList<PlatformDevelopVersion> creators) {
        this.creators = creators;
    }

    public ArrayList<PlatformDevelopVersion> getUpdators() {
        return updators;
    }

    public void setUpdators(ArrayList<PlatformDevelopVersion> updators) {
        this.updators = updators;
    }

    public ArrayList<PlatformColumn> getResponse() {
        return response;
    }

    public void setResponse(ArrayList<PlatformColumn> response) {
        this.response = response;
    }
}
