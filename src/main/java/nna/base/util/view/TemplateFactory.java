package nna.base.util.view;

import nna.base.util.CharUtil;

import java.io.*;
import java.util.ArrayList;

/**
 * @author NNA-SHUAI
 * @create 2017-05-23 13:14
 **/

 class TemplateFactory {

    private static final int[] nextHeader= CharUtil.nexts("<!--");
    private static final int[] nextTail= CharUtil.nexts("-->");

    private TemplateFactory(){}

     static Template getTemplate(String templatePath,String encode) throws IOException {
        assert templatePath!=null;
        assert !templatePath.trim().equals("");
        File file=new File(templatePath);
        FileInputStream fileInputStream=new FileInputStream(file);
        InputStreamReader fileReader=new InputStreamReader(fileInputStream,encode);
        BufferedReader reader=new BufferedReader(fileReader);
        return buildTemplate(reader);
    }

     static Template buildTemplate(BufferedReader reader) throws IOException {
        Template template=new Template();
        ArrayList<String> strs=new ArrayList<String>();
        ArrayList<View> views=new ArrayList<View>();
        boolean isBegin=true;
        while(true){
            if(isBegin){
                String str=getBegin(reader);
                if(strs.equals("")){
                    break;
                }else {
                    strs.add(str);
                    isBegin=false;
                }
            }else{
                String str=getEnd(reader);
                if(str.equals("")){
                    break;
                }else{
                    views.add(ViewParser.getView(str));
                    strs.add(null);
                }
                isBegin=true;
            }
        }
        template.setStrs(strs.toArray(new String[0]));
        template.setViews(views.toArray(new View[0]));
        return template;
    }

     static String getEnd(BufferedReader reader) throws IOException {
        StringBuilder temp=new StringBuilder("");
        int index=0;
        String begin="-->";
        char[] chars=new char[1];
        if(reader.read(chars)==-1){
            return temp.toString();
        }else {
            temp.append(chars);
        }
        while(true){
            if(begin.charAt(index)==chars[0]){
                if(index==2){
                    return temp.substring(0,temp.toString().length()-3);
                }else {
                    index++;
                    if(reader.read(chars)==-1){
                        break;
                    }else
                        temp.append(chars);
                }
            }else {
                if(index==0){
                    if(reader.read(chars)==-1){
                        break;
                    }else
                        temp.append(chars);
                }
                index=nextTail[index];
            }
        }
        return temp.toString();
    }

     static String getBegin(BufferedReader reader) throws IOException {
        StringBuilder temp=new StringBuilder("");
        int index=0;
        String begin="<!--";
        char[] chars=new char[1];
        if(reader.read(chars)==-1){
            return temp.toString();
        }else{
            temp.append(chars);
        }
        while(true){
            if(begin.charAt(index)==chars[0]){
                if(index==3){
                    return temp.substring(0,temp.toString().length()-4);
                }else {
                    index++;
                    if(reader.read(chars)==-1){
                        break;
                    }else{
                        temp.append(chars);
                    }
                }
            }else {
                if(index==0){
                    if(reader.read(chars)==-1){
                        break;
                    }else {
                        index=nextHeader[index];
                        temp.append(chars);
                        continue;
                    }
                }else{
                    index=nextHeader[index];
                }
            }
        }
        return temp.toString();
    }

}
