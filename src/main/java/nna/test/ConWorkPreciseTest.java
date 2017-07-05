package nna.test;

import java.io.*;

/**
 *
 * Created by NNA-SHUAI on 2017/7/4.
 */
public class ConWorkPreciseTest {
    public static void main(String[] args) throws IOException {
        test("C:\\LOG\\20170705\\TEST-CON-LOG\\");
    }
    static void test(String path) throws IOException {
        File file=new File(path);
        File[] logs=file.listFiles();
        for(File log:logs){
            if(log.isDirectory()){
                test(log.getAbsolutePath());
            }else{
                System.out.println(log.getAbsolutePath());
                testLog(log.getAbsolutePath());
            }
        }
    }
    static void testLog(String logPath) throws IOException {
        BufferedReader br=new BufferedReader(new InputStreamReader(new FileInputStream(logPath)));
        String line;
        Integer index=0;
        Integer index2=1;
        while((line=br.readLine())!=null){
            if(index%2!=0){
                String line2=line.substring(line.lastIndexOf("]")+1);
                if(!Integer.valueOf(line2.trim()).equals(index2)){
                    System.out.println(line2+"-"+index2+"-"+(Integer.valueOf(line2.trim()).equals(index2)));
                }
                index2+=1;
            }
            index++;
        }
        br.close();
    }
}
