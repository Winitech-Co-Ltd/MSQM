package com.peru.ncov2019.ml.cmm.encryption;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;

import lombok.extern.apachecommons.CommonsLog;
/**
 * Clase Util para usar la función AES256 (cifrado)
 *
 */
@CommonsLog
public class AES256Util {
	
	private String iv;
    private Key keySpec;
    private String key = "00000000000000000000000000000000";
    private String vector = "0000000000000000";

    
    
    public AES256Util(String key, String vector) throws UnsupportedEncodingException {
    	this.key = key;
    	this.vector = vector;
    	
        this.iv = vector.substring(0, 16);
        byte[] keyBytes = new byte[16];
        byte[] b = key.getBytes("UTF-8");
        int len = b.length;
        if (len > keyBytes.length) {
            len = keyBytes.length;
        }
        System.arraycopy(b, 0, keyBytes, 0, len);
        SecretKeySpec keySpec = new SecretKeySpec(keyBytes, "AES");

        this.keySpec = keySpec;
    }
    
    
    
    /**
     * 
     * @throws UnsupportedEncodingException
     */
    public AES256Util() throws UnsupportedEncodingException {
        this.iv = vector.substring(0, 16);
        byte[] keyBytes = new byte[16];
        byte[] b = key.getBytes("UTF-8");
        int len = b.length;
        if (len > keyBytes.length) {
            len = keyBytes.length;
        }
        System.arraycopy(b, 0, keyBytes, 0, len);
        SecretKeySpec keySpec = new SecretKeySpec(keyBytes, "AES");

        this.keySpec = keySpec;
    }
	
    /**
     * Encriptar la cadena en AES256
     * @param str Cadena para encriptar
     * @return String Cadena encriptada
     * @throws NoSuchAlgorithmException
     * @throws GeneralSecurityException
     * @throws UnsupportedEncodingException
     */
    public String encrypt(String str) throws NoSuchAlgorithmException,
            GeneralSecurityException, UnsupportedEncodingException {
        Cipher c = Cipher.getInstance("AES/CBC/PKCS5Padding");
        c.init(Cipher.ENCRYPT_MODE, keySpec, new IvParameterSpec(iv.getBytes()));
        byte[] encrypted = c.doFinal(str.getBytes("UTF-8"));
        String enStr = Base64.encodeBase64String(encrypted);
        return enStr;
    }
	
    /**
     * Descifrar txt encriptado con AES256.
     *
     * @param str Cadena para descifrar
     * @return String Cadena descifrada
     * @throws NoSuchAlgorithmException
     * @throws GeneralSecurityException
     * @throws UnsupportedEncodingException
     */
    public String decrypt(String str) throws NoSuchAlgorithmException,
            GeneralSecurityException, UnsupportedEncodingException {
        Cipher c = Cipher.getInstance("AES/CBC/PKCS5Padding");
        c.init(Cipher.DECRYPT_MODE, keySpec, new IvParameterSpec(iv.getBytes()));
        byte[] byteStr = Base64.decodeBase64(str.getBytes());
        return new String(c.doFinal(byteStr), "UTF-8");
    }


	public static void main(String[] args) {
	
		AES256Util aes2;
		
		// Leer archivo
        BufferedReader br = null;
        StringBuffer sb = new StringBuffer(); // Variables de prueba
         try {
            br = new BufferedReader(new FileReader("now.log"));
            String line = null;
            String data1 = null;
            String temp = null;
            aes2 = new AES256Util();
    		
            while ((line = br.readLine()) != null) {
            	
            	if(line.indexOf("PARM")>=0){
            		
            		temp = line.substring(line.indexOf("PARM")+7);
            		temp = temp.substring(0,temp.indexOf("\""));
            		
            		data1 = aes2.decrypt(temp);
            		
            		line = line.replace(temp, data1);
            		
            		sb.append(line+"\r\n");
            	}
            }
 
        } catch (IOException ioe) {
            System.out.println(ioe.getMessage());
        } catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (GeneralSecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
            try { 
                if (br!=null) 
                    br.close(); 
            } catch (Exception e) {}
        }
         
         File file = new File("now.txt");
         FileWriter writer = null;
         
         try {
             // Especifique como TRUE si desea escribir sobre el contenido de archivo existente y especifique como FALSE si desea eliminar los contenidos existentes y escribir uno nuevo.
             writer = new FileWriter(file, false);
             writer.write(sb.toString());
             writer.flush();
             
             System.out.println("DONE");
         } catch(IOException e) {
             e.printStackTrace();
         } finally {
             try {
                 if(writer != null) writer.close();
             } catch(IOException e) {
                 e.printStackTrace();
             }
         }
         
	}

}