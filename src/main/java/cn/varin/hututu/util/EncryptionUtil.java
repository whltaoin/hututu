package cn.varin.hututu.util;

import cn.hutool.crypto.digest.BCrypt;

/**
 * 加密工具类
 * 使用：BCrypt
 */
public class EncryptionUtil {
    /**
     * 加密
     * @param password
     * @return
     */
    public static String getCiphertext(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }

    /**
     * 对比密码
     * @param plaintext 明文
     * @param ciphertext 密文
     * @return
     */
    public static boolean checkPlaintext(String plaintext, String ciphertext) {
        return BCrypt.checkpw(plaintext, ciphertext);
    }

//    public static void main(String[] args) {
//        String s = getCiphertext("123456");
//        System.out.println(checkPlaintext("123456", s));
//
//    }


}
