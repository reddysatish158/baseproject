import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

import org.apache.commons.codec.binary.Base64;

/**
* Class which provides methods for encrypting and decrypting
* strings using a DES encryption algorithm.
* Strings can be encrypted and then are returned translated
* into a Base64 Ascii String.
*
* @author Tim Archer 11/11/03
* @version $Revision: 1.2 $
*/
public class Des {

    private Cipher encryptCipher = null;
    private Cipher decryptCipher = null;

    /**
* Construct a new object which can be utilized to encrypt
* and decrypt strings using the specified key
* with a DES encryption algorithm.
*
* @param key The secret key used in the crypto operations.
* @throws Exception If an error occurs.
*
*/
    public Des(SecretKey key) throws Exception {
        encryptCipher = Cipher.getInstance("DES");
        decryptCipher = Cipher.getInstance("DES");
        encryptCipher.init(Cipher.ENCRYPT_MODE, key);
        decryptCipher.init(Cipher.DECRYPT_MODE, key);
    }

    /**
* Encrypt a string using DES encryption, and return the encrypted
* string as a base64 encoded string.
* @param unencryptedString The string to encrypt.
* @return String The DES encrypted and base 64 encoded string.
* @throws Exception If an error occurs.
*/
    public String encryptBase64 (String unencryptedString) throws Exception {
        // Encode the string into bytes using utf-8
        byte[] unencryptedByteArray = unencryptedString.getBytes("UTF8");

        // Encrypt
        byte[] encryptedBytes = encryptCipher.doFinal(unencryptedByteArray);

        /*// Encode bytes to base64 to get a string
byte [] encodedBytes = Base64.encodeBase64(encryptedBytes);*/

        return new String(encryptedBytes);
    }
 
    /**
* Decrypt a base64 encoded, DES encrypted string and return
* the unencrypted string.
* @param encryptedString The base64 encoded string to decrypt.
* @return String The decrypted string.
* @throws Exception If an error occurs.
*/
    public String decryptBase64 (String encryptedString) throws Exception {
        // Encode bytes to base64 to get a string
        byte [] decodedBytes = Base64.decodeBase64(encryptedString.getBytes());

        // Decrypt
        byte[] unencryptedByteArray = decryptCipher.doFinal(decodedBytes);

        // Decode using utf-8
        return new String(unencryptedByteArray, "UTF8");
    }

    /**
* Main unit test method.
* @param args Command line arguments.
*
*/
    public static void main(String args[]) {
        try {
            //Generate the secret key
            String password = "ABCDEFGHABCDEFGH";
            byte[] b=password.getBytes();
            
            DESKeySpec key = new DESKeySpec(password.getBytes());
           // SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
             
            SecretKey myDesKey =SecretKeyFactory.getInstance("DES").generateSecret(key);
            Des des=new Des(myDesKey);
            Cipher desCipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
            
            desCipher.init(Cipher.ENCRYPT_MODE, myDesKey);
            //Instantiate the encrypter/decrypter
            //CryptString crypt = new CryptString(keyFactory.generateSecret(key));
            byte[] text = "4b09ef27a17948ed25b2b7103bda8a3a".getBytes();
            String unencryptedString ="000100010000F0F0af2f31009a8b6b5e5f7d64060f1c9ba1";//000100010000F0F0
            String encryptedString = des.encryptBase64(unencryptedString);
            System.out.println("Encrypted String:"+encryptedString);
            byte[] textEncrypted = desCipher.doFinal(text);
            //Decrypt the string
        /* String unencryptedString1 = des.decryptBase64(encryptedString);
System.out.println("UnEncrypted String:"+unencryptedString1);*/
         // System.out.println(textEncrypted.);
            
            
       // char[] b1= Hex.encodeHex(encryptedString.getBytes(encryptedString));
         // byte[] encryptedArray=encryptedString.getBytes();
  StringBuffer buffer=new StringBuffer();
            for(byte i:textEncrypted){
             buffer.append(Integer.toHexString(i));
             //buffer.append(String.format("%02x", i&0xff));
            }
            System.out.println(buffer);
            int temp;
            String result = "";
            for (int i = 0; i < textEncrypted.length; ++i) {
                temp = textEncrypted[i] & 0xFF;
                if (temp < 16) {
                    result += "0";
                }
                result += Integer.toHexString(temp);
              }
              System.out.println(result.toUpperCase());
      /*String bytArrayToHex(byte[] a) {
StringBuilder sb = new StringBuilder();
for(byte b: a)
sb.append(String.format("%02x", b&0xff));
return sb.toString();
}*/
       // System.out.println("Encrypted String-"+buffer);
         // System.out.println("Encrypted String in char "+b1);
        } catch (Exception e) {
            System.err.println("Error:"+e.toString());
        }
    }
}

   
