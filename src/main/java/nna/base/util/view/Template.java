package nna.base.util.view;

import nna.base.bean.Clone;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * template
 * @author NNA-SHUAI
 * @create 2017-05-14 21:40
 **/

public class Template extends Clone{
    private String[] strs;
    private View[] views;

    public String render(Map<String,String[]> map){
        StringBuilder stringBuilder=new StringBuilder("");
        int count=strs.length;
        String str;
        View view;
        for(int index=0,index2=0;index < count;index++){
            str=strs[index];
            if(str==null){
                view=views[index2++];
                str=view.toString(map);
            }
            stringBuilder.append(str);
        }
        return stringBuilder.toString();
    }

    public Template Clone(){
        Template template=(Template) super.clone();
        template.setStrs(strs.clone());
        template.setViews(views.clone());
        return template;
    }

    public static Template getTemplate(String templatePath,String encode) throws IOException {
        return TemplateFactory.getTemplate(templatePath,encode);
    }

    public void setStrs(String[] strs) {
        this.strs = strs;
    }

    public void setViews(View[] views) {
        this.views = views;
    }
    public String[] getStrs() {
        return strs;
    }

    public View[] getViews() {
        return views;
    }
}
