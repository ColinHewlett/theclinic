/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package colinhewlettsolutions.client.controller;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.SecureRandom;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.util.Arrays;
import java.util.Base64;
/**
 *
 * @author colin
 */
public class PasswordUtils {
    private static final int ITERATIONS = 10000;
    private static final int KEY_LENGTH = 256;
    private static final SecureRandom RANDOM = new SecureRandom();
    
    private static String salt = null;
    public static String generateSalt() {
        byte[] theSalt = new byte[16];
        RANDOM.nextBytes(theSalt);
        salt = Base64.getEncoder().encodeToString(theSalt);
        return salt;
    }
    
    private static String _salt = null;
    public static void setSalt(String value){
        _salt = value;
    }
    public static String getSalt(){
        return _salt;
    }

    /*
    private static byte[] salt = null;
    public static void setSalt(byte[] value){
        salt = value;
    }
    public static byte[] getSalt(){
        return salt;
    }
*/
    
    private static String hashedPassword = null;
    public static void setHashedPassword(String value){
        hashedPassword = value;
    }
    public static String getHashedPassword(){
        return hashedPassword;
    }
    
    public static byte[] generateSaltBytes() {
        byte[] theSalt = new byte[16];
        RANDOM.nextBytes(theSalt);
        return theSalt;
    }

    public static byte[] hashPasswordBytes(String password, byte[] salt) throws NoSuchAlgorithmException, InvalidKeySpecException {
        PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt, ITERATIONS, KEY_LENGTH);
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA512");
        return keyFactory.generateSecret(spec).getEncoded();
    }
    
    public static String hashPassword(String password, String salt) throws NoSuchAlgorithmException, InvalidKeySpecException {
        PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), Base64.getDecoder().decode(salt), ITERATIONS, KEY_LENGTH);
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA512");
        byte[] hash = keyFactory.generateSecret(spec).getEncoded();
        return Base64.getEncoder().encodeToString(hash);
    }
    
    /*
    public static byte[] hashPassword(String password, byte[] salt) throws NoSuchAlgorithmException, InvalidKeySpecException {
        PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt, ITERATIONS, KEY_LENGTH);
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA512");
        return keyFactory.generateSecret(spec).getEncoded();
    }
*/

    // Compare two byte arrays for equality
    public static boolean isPasswordCorrect(String password, String storedPassword, String storedSalt) throws NoSuchAlgorithmException, InvalidKeySpecException {
        String hashedPassword = hashPassword(password, storedSalt);
        return hashedPassword.equals(storedPassword);
        //return Arrays.equals(hashedPassword, expectedHash);
    }

    public static boolean isSaltCorrect(String storedSalt){
        return getSalt().equals(storedSalt);
        //return Arrays.equals(getSalt(), expectedSalt);
    }
    
    public static boolean isHashedPasswordCorrect(String storedHashedPassword){
        return getHashedPassword().equals(storedHashedPassword);
        //return Arrays.equals(getHashedPassword(), expectedHashedPassword);
    }
}
