package com.dxt.seckill.utils;

import org.apache.commons.codec.digest.DigestUtils;

//两次md5加密
//用户端：PASS=MD5（明文+固定Salt）
//服务端：PASS=MD5（用户输入+随机Salt）
public class MD5Util {

    private static final String salt = "1a2b3c4d";

    public static String md5(String src){
        return DigestUtils.md5Hex(src);
    }

    public static String inputPassToFromPass(String inputPass) {
        String str =  "" + salt.charAt(0) + salt.charAt(2) + inputPass + salt.charAt(5) + salt.charAt(4);
        return md5(str);
    }

    public static String fromPassToDBPass(String fromPass, String salt){
        String str =  "" + salt.charAt(0) + salt.charAt(2) + fromPass + salt.charAt(5) + salt.charAt(4);
        return md5(str);
    }

    public static String inputPassToDBPass(String inputPass, String salt){
        String fromPass = inputPassToFromPass(inputPass);
        return fromPassToDBPass(fromPass, salt);
    }

    public static void main(String[] args) {
        System.out.println(MD5Util.inputPassToDBPass("123456","1a2b3c4d"));
    }
}
