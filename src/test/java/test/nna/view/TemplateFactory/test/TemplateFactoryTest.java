package test.nna.view.TemplateFactory.test;

import junit.framework.TestCase;
import nna.base.util.view.Template;
import nna.base.util.view.View;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;

/**
 * ${description}
 *
 * @author NNA-SHUAI
 * @create 2017-2017/5/23-13:15
 */
public class TemplateFactoryTest extends TestCase {
    public void setUp() throws Exception {
        super.setUp();
    }

    public void testTemplateFactory() throws Exception {

    }

    public void testGetBegin() throws Exception{
        File file=new File("C:\\Users\\NNA-SHUAI\\IdeaProjects\\platformv3\\src\\main\\webapp\\login.html");
        FileInputStream fileInputStream=new FileInputStream(file);
        InputStreamReader fileReader=new InputStreamReader(fileInputStream,"utf-8");
        BufferedReader reader=new BufferedReader(fileReader);
//        System.out.println(TemplateFactory.getBegin(reader));
    }

    public void testGetEnd() throws Exception{
        File file=new File("C:\\Users\\NNA-SHUAI\\IdeaProjects\\platformv3\\src\\main\\webapp\\login.html");
        FileInputStream fileInputStream=new FileInputStream(file);
        InputStreamReader fileReader=new InputStreamReader(fileInputStream,"utf-8");
        BufferedReader reader=new BufferedReader(fileReader);
//        System.out.println(TemplateFactory.getEnd(reader));
    }

    public void testBuildTemplate() throws Exception{
        File file=new File("C:\\Users\\NNA-SHUAI\\IdeaProjects\\platformv3\\src\\main\\webapp\\login.html");
        FileInputStream fileInputStream=new FileInputStream(file);
        InputStreamReader fileReader=new InputStreamReader(fileInputStream,"utf-8");
        BufferedReader reader=new BufferedReader(fileReader);
//        Template template=Template.buildTemplate(reader);
//        for(int index=0;index < template.getStrs().length;index++){
//            System.out.println(template.getStrs()[index]);
//        }
//        for(int index=0;index < template.getViews().length;index++){
//            View view=template.getViews()[index];
//            for(int i=0;i < view.getViews().length;i++){
//                System.out.println(view.getViews()[i]);
//            }
//            for(int j=0;j < view.getRenderNms().length;j++){
//                System.out.println(view.getRenderNms()[j]);
//            }
//        }
    }

    public void testGetTemplate() throws Exception{
        Template.getTemplate("C:\\Users\\NNA-SHUAI\\IdeaProjects\\platformv3\\src\\main\\webapp\\login.html","UTF-8");
    }

    public void tearDown() throws Exception {

    }

}