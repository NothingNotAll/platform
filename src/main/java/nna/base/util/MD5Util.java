package nna.base.util;
/**
 * Created by NNA-SHUAI on 2017/4/27.
 */

import sun.misc.BASE64Encoder;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * MD5 Enocde
 *
 * @author
 * @create 2017-04-27 9:50
 **/

public class MD5Util {
    private static MessageDigest md5;
    private static BASE64Encoder base64en;
    static{
        try {
            md5 = MessageDigest.getInstance("MD5");
            base64en = new BASE64Encoder();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }
    public static String getMD5Str(String str) throws UnsupportedEncodingException {
        return base64en.encode(str.getBytes("UTF-8"));
    }
}
