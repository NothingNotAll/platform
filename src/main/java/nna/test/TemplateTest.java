package nna.test;

import nna.base.util.view.Template;

import java.io.IOException;

/**
 * Created by NNA-SHUAI on 2017/7/8.
 */
public class TemplateTest extends TradeReqMap{
    static {
        TradeReqMap.map.put("roleId",new String[]{"000000"});
        TradeReqMap.map.put("roleName",new String[]{"admin"});
    }
    public static void main(String[] args) throws IOException {
        Template template=Template.getTemplate("C:\\idea\\platformv3\\src\\main\\webapp\\login.html","UTF-8");
        String logintHtml=template.render(TradeReqMap.map);
        System.out.println(logintHtml);
//        System.out.println(template.toString(TradeReqMap.map));
    }
}
