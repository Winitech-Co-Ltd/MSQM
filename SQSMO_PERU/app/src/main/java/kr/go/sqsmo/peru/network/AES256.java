package kr.go.sqsmo.peru.network;

import android.util.Base64;
import android.util.Log;

import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class AES256 {
    private static String publicKey;
    private static String publicVector;

    private static String privateKey;
    private static String privateVector;

    public static void setPublic(String key, String vector) {
        publicKey = key + key;
        publicVector = vector;
    }


    public static void setPrivate(String key, String vector) {
        privateKey = key + key;
        privateVector = vector;
    }


    public static String encrypt(String str) {
        String enStr = "";
        try {
            byte[] keyBytes = new byte[16];
            byte[] b = privateKey.getBytes("UTF-8");

            int len = b.length;
            if (len > keyBytes.length) {
                len = keyBytes.length;
            }
            System.arraycopy(b, 0, keyBytes, 0, len);
            SecretKeySpec keySpec = new SecretKeySpec(keyBytes, "AES");
            Cipher c = Cipher.getInstance("AES/CBC/PKCS5Padding");

            c.init(Cipher.ENCRYPT_MODE, keySpec, new IvParameterSpec(privateVector.substring(0, 16).getBytes()));
            byte[] encrypted = c.doFinal(str.getBytes("UTF-8"));
            enStr = Base64.encodeToString(encrypted, Base64.NO_PADDING | Base64.NO_CLOSE | Base64.NO_WRAP);
        } catch (UnsupportedEncodingException e) {
            Log.e("AES256", "UnsupportedEncodingException" + e);
        } catch (NoSuchAlgorithmException e) {
            Log.e("AES256", "NoSuchAlgorithmException" + e);
        } catch (InvalidKeyException e) {
            Log.e("AES256", "InvalidKeyException" + e);
        } catch (InvalidAlgorithmParameterException e) {
            Log.e("AES256", "InvalidAlgorithmParameterException" + e);
        } catch (NoSuchPaddingException e) {
            Log.e("AES256", "NoSuchPaddingException" + e);
        } catch (BadPaddingException e) {
            Log.e("AES256", "BadPaddingException" + e);
        } catch (IllegalBlockSizeException e) {
            Log.e("AES256", "IllegalBlockSizeException" + e);
        }
        return enStr;
    }

    /**
     * AES256으로 암호화된 txt 를 복호화한다.
     *
     * @param str 복호화할 문자열
     * @return
     * @throws NoSuchAlgorithmException
     * @throws GeneralSecurityException
     * @throws UnsupportedEncodingException
     */
    public static String decrypt(String str) throws NoSuchAlgorithmException,
            GeneralSecurityException, UnsupportedEncodingException {
        byte[] keyBytes = new byte[16];
        byte[] b = privateKey.getBytes("UTF-8");
        int len = b.length;
        if (len > keyBytes.length) {
            len = keyBytes.length;
        }
        System.arraycopy(b, 0, keyBytes, 0, len);
        SecretKeySpec keySpec = new SecretKeySpec(keyBytes, "AES");
        Cipher c = Cipher.getInstance("AES/CBC/PKCS5Padding");
        c.init(Cipher.DECRYPT_MODE, keySpec, new IvParameterSpec(privateVector.substring(0, 16).getBytes()));
        byte[] byteStr = Base64.decode(str.getBytes(), 0);
        return new String(c.doFinal(byteStr), "UTF-8");
    }

    public static String publicEncrypt(String str) {
        String enStr = "";
        try {
            byte[] keyBytes = new byte[16];
            byte[] b = publicKey.getBytes("UTF-8");
            int len = b.length;
            if (len > keyBytes.length) {
                len = keyBytes.length;
            }
            System.arraycopy(b, 0, keyBytes, 0, len);
            SecretKeySpec keySpec = new SecretKeySpec(keyBytes, "AES");
            Cipher c = Cipher.getInstance("AES/CBC/PKCS5Padding");

            c.init(Cipher.ENCRYPT_MODE, keySpec, new IvParameterSpec(publicVector.substring(0, 16).getBytes()));
            byte[] encrypted = c.doFinal(str.getBytes("UTF-8"));
            enStr = Base64.encodeToString(encrypted, Base64.NO_PADDING | Base64.NO_CLOSE | Base64.NO_WRAP);
        } catch (UnsupportedEncodingException e) {
            Log.e("AES256", "UnsupportedEncodingException" + e);
        } catch (NoSuchAlgorithmException e) {
            Log.e("AES256", "NoSuchAlgorithmException" + e);
        } catch (InvalidKeyException e) {
            Log.e("AES256", "InvalidKeyException" + e);
        } catch (InvalidAlgorithmParameterException e) {
            Log.e("AES256", "InvalidAlgorithmParameterException" + e);
        } catch (NoSuchPaddingException e) {
            Log.e("AES256", "NoSuchPaddingException" + e);
        } catch (BadPaddingException e) {
            Log.e("AES256", "BadPaddingException" + e);
        } catch (IllegalBlockSizeException e) {
            Log.e("AES256", "IllegalBlockSizeException" + e);
        }
        return enStr;
    }

    /**
     * AES256으로 암호화된 txt 를 복호화한다.
     *
     * @param str 복호화할 문자열
     * @return
     * @throws NoSuchAlgorithmException
     * @throws GeneralSecurityException
     * @throws UnsupportedEncodingException
     */
    public static String publicDecrypt(String str) throws NoSuchAlgorithmException,
            GeneralSecurityException, UnsupportedEncodingException {
        byte[] keyBytes = new byte[16];
        byte[] b = publicKey.getBytes("UTF-8");
        int len = b.length;
        if (len > keyBytes.length) {
            len = keyBytes.length;
        }
        System.arraycopy(b, 0, keyBytes, 0, len);
        SecretKeySpec keySpec = new SecretKeySpec(keyBytes, "AES");
        Cipher c = Cipher.getInstance("AES/CBC/PKCS5Padding");
        c.init(Cipher.DECRYPT_MODE, keySpec, new IvParameterSpec(publicVector.substring(0, 16).getBytes()));
        byte[] byteStr = Base64.decode(str.getBytes(), 0);
        return new String(c.doFinal(byteStr), "UTF-8");
    }

}
