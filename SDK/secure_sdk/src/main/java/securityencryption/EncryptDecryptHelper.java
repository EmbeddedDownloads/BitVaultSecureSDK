package securityencryption;

import org.spongycastle.jce.spec.IESParameterSpec;
import org.spongycastle.util.encoders.Hex;

import java.math.BigInteger;
import java.security.AlgorithmParameters;
import java.security.Key;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.ECGenParameterSpec;
import java.security.spec.ECParameterSpec;
import java.security.spec.ECPoint;
import java.security.spec.ECPrivateKeySpec;
import java.security.spec.ECPublicKeySpec;
import java.security.spec.KeySpec;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by Vinod Singh on 26/5/17.
 */

public class EncryptDecryptHelper {

    final private static String ASYMALGORITHM = "ECIES";
    final private static String ECurve = "secp256k1";
    private final static byte[] derivation = Hex.decode("404122232425262728292a2b2c2d2e2f");
    private final static byte[] encoding = Hex.decode("303132333435363738393a3b3c3d3e3f");
    private final static int MACKeySize = 128;


    final public static String SYMALGORITHM 	= "AES/CBC/PKCS5Padding";
    final private static int ITERATION_COUNT = 256;
    final private static int KEY_LENGTH = 256;
    final private static byte[] SALT = {
            (byte) 0xA9, (byte) 0x9B, (byte) 0xC8, (byte) 0x32,
            (byte) 0x56, (byte) 0x35, (byte) 0xE3, (byte) 0x03
    };

    // IV - 16 bytes static
    final private static byte[] ivBytes = {
            (byte) 0xA9, (byte) 0x9B, (byte) 0xC8, (byte) 0x32,
            (byte) 0x56, (byte) 0x35, (byte) 0xE3, (byte) 0x03,
            (byte) 0xA9, (byte) 0x9B, (byte) 0xC8, (byte) 0x32,
            (byte) 0x56, (byte) 0x35, (byte) 0xE3, (byte) 0x03
    };

    public static AlgorithmParameterSpec ivSpec = new IvParameterSpec(ivBytes);

    public final static int ENC_BUFFER_LEN = 2097152 ; // 1048576 = 1 MB ; 2097152 = 2 MB
    public final static int DEC_BUFFER_LEN = ENC_BUFFER_LEN + 32;
    /**
     * Get EC Public Key from BTC public key
     *
     * @param pubKeyHex
     * @return
     * @throws Exception
     */
    public static PublicKey getBTCPublicKey(String pubKeyHex) throws Exception {
        byte[] keyRaw = Hex.decode(pubKeyHex);
        BigInteger xInt = new BigInteger(1, Arrays.copyOfRange(keyRaw, 1, 33));
        BigInteger yInt = new BigInteger(1, Arrays.copyOfRange(keyRaw, 33, 65));
        ECPoint pubPoint = new ECPoint(xInt, yInt);
        AlgorithmParameters parameters = AlgorithmParameters.getInstance("EC", "SC");
        parameters.init(new ECGenParameterSpec(ECurve));
        ECParameterSpec ecParameters = (ECParameterSpec) parameters.getParameterSpec(ECParameterSpec.class);
        ECPublicKeySpec pubSpec = new ECPublicKeySpec(pubPoint, ecParameters);
        KeyFactory kf = KeyFactory.getInstance("EC", "SC");
        PublicKey key = kf.generatePublic(pubSpec);
        return key;
    }

    /**
     * Get EC Private Key from BTC private key
     *
     * @param privKeyHex
     * @return
     * @throws Exception
     */
    public static PrivateKey getBTCPrivateKey(String privKeyHex) throws Exception {
        byte[] keyRaw = Hex.decode(privKeyHex);
        BigInteger privInt = new BigInteger(1, keyRaw);
        AlgorithmParameters parameters = AlgorithmParameters.getInstance("EC", "SC");
        parameters.init(new ECGenParameterSpec(ECurve));
        ECParameterSpec ecParameters = (ECParameterSpec) parameters.getParameterSpec(ECParameterSpec.class);
        ECPrivateKeySpec privSpec = new ECPrivateKeySpec(privInt, ecParameters);
        KeyFactory kf = KeyFactory.getInstance("EC", "SC");
        PrivateKey key = kf.generatePrivate(privSpec);
        return key;
    }


    /**
     * Convert String to Session Key - AES
     *
     * @param StrKey
     * @return
     */
    public static Key convertStringToKey(String StrKey) {
        byte[] keyRaw = Hex.decode(StrKey);
        Key key = new SecretKeySpec(keyRaw, 0, keyRaw.length, "AES");
        return key;
    }

    /**
     * Symmetric Encryption - AES
     *
     * @param input
     * @param length
     * @param SymKey
     * @return
     * @throws Exception
     */
    public static byte[][] symEncryption(byte[] input, int length, Key SymKey) throws Exception {
        Cipher ecipher = Cipher.getInstance(SYMALGORITHM, "SC");
        ecipher.init(Cipher.ENCRYPT_MODE, SymKey, ivSpec);
        byte[] cipherText = new byte[ecipher.getOutputSize(length)];
        int ctLength = ecipher.update(input, 0, length, cipherText, 0);
        ctLength += ecipher.doFinal(cipherText, ctLength);
        byte[][] result = new byte[2][];
        result[0] = cipherText;
        result[1] = BigInteger.valueOf(ctLength).toByteArray();
        return result;
    }

    /***
     * Symmetric Decryption - AES
     *
     * @param cipherText
     * @param length
     * @param SymKey
     * @return
     * @throws Exception
     */
    public static byte[][] symDecryption(byte[] cipherText, int length, Key SymKey) throws Exception {
        Cipher dcipher = Cipher.getInstance(SYMALGORITHM, "SC");
        dcipher.init(Cipher.DECRYPT_MODE, SymKey, ivSpec);
        byte[] plainText = new byte[dcipher.getOutputSize(length)];
        int ptLength = dcipher.update(cipherText, 0, length, plainText, 0);
        ptLength += dcipher.doFinal(plainText, ptLength);
        byte[][] result = new byte[2][];
        result[0] = plainText;
        result[1] = BigInteger.valueOf(ptLength).toByteArray();
        return result;
    }



    /**
     * Asymmetric Encryption - ECIES
     *
     * @param input
     * @param PubKey
     * @return
     * @throws Exception
     */
    public static byte[] asymEncryption(byte[] input, Key PubKey) throws Exception {
        IESParameterSpec params = new IESParameterSpec(derivation, encoding, MACKeySize);
        Cipher ecipher = Cipher.getInstance(ASYMALGORITHM, "SC");
        ecipher.init(Cipher.ENCRYPT_MODE, PubKey, params);
        byte[] cipherText = ecipher.doFinal(input, 0, input.length);
        return cipherText;
    }

    /**
     * Asymmetric Decryption - ECIES
     *
     * @param cipherText
     * @param PrivKey
     * @return
     * @throws Exception
     */
    public static byte[] asymDecryption(byte[] cipherText, Key PrivKey) throws Exception {
        IESParameterSpec params = new IESParameterSpec(derivation, encoding, MACKeySize);
        Cipher dcipher = Cipher.getInstance(ASYMALGORITHM, "SC");
        dcipher.init(Cipher.DECRYPT_MODE, PrivKey, params);
        byte[] plainText = dcipher.doFinal(cipherText);
        return plainText;
    }




    /* --------------------------- METHODS ------------------------ */
	/*
	 Generate Symmetric Keys
     * @return
     * @throws Exception
	 */
    public static SecretKey genSymmetricKey ( String Password ) throws Exception {

        SecretKeyFactory efactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        KeySpec espec = new PBEKeySpec( Password.toCharArray(), SALT, ITERATION_COUNT, KEY_LENGTH );
        SecretKey etmp = efactory.generateSecret(espec);
        SecretKey SymKey = new SecretKeySpec(etmp.getEncoded(), "AES");
        return SymKey;
    }

    /**
     * Generate Session Key - AES
     *
     * @return
     * @throws Exception
     */
    public static Key genSessionKey() throws Exception {
        KeyGenerator SessionGenerator = KeyGenerator.getInstance("AES", "SC");
        SessionGenerator.init(256);
        Key SessionKey = SessionGenerator.generateKey();
        return SessionKey;
    }

    /*
    Symmetric Encryption - AES
     */
    public static byte[][] symEncryption ( byte[] input, int length, Cipher ecipher) throws Exception {

        byte[] cipherText = new byte[ecipher.getOutputSize(length)];
        int ctLength = ecipher.update(input, 0, length, cipherText, 0);
        ctLength += ecipher.doFinal(cipherText, ctLength);

        byte[][] result = new byte[2][];
        result[0] = cipherText;
        result[1] = BigInteger.valueOf(ctLength).toByteArray();
        return result;
    }

    /*
    Symmetric Decryption - AES
    */
    public static byte[][] symDecryption ( byte[] cipherText, int length, Cipher dcipher ) throws Exception {

        byte[] plainText = new byte[dcipher.getOutputSize(length)];
        int ptLength = dcipher.update( cipherText, 0, length, plainText, 0);
        ptLength += dcipher.doFinal(plainText, ptLength);

        byte[][] result = new byte[2][];
        result[0] = plainText;
        result[1] = BigInteger.valueOf(ptLength).toByteArray();
        return result;
    }

    /*
  Symmetric Decryption - AES
  */
    public static byte[][] symDecryptionMedia ( byte[] cipherText, int length, Cipher dcipher ) throws Exception {

        byte[] plainText = new byte[dcipher.getOutputSize(length)];
        int ptLength = dcipher.update( cipherText, 0, length, plainText, 0);


        byte[][] result = new byte[2][];
        result[0] = plainText;
        result[1] = BigInteger.valueOf(ptLength).toByteArray();
        return result;
    }
    /*
     Encrypt of files in buffer
     */
    public static byte[][] EncryptSendingBuffer ( byte[] input, int length, Cipher ecipher1, Cipher ecipher2 ) throws Exception {

        byte[][] mcipher = symEncryption( input, length, ecipher1);
        mcipher = symEncryption( mcipher[0], new BigInteger(mcipher[1]).intValue(), ecipher2);
        mcipher[0] = Arrays.copyOfRange(mcipher[0],0, new BigInteger(mcipher[1]).intValue() );

        return mcipher;
    }

    /*
    Decrypt of files in buffer
     */
    public static byte[][] DecryptRcvBuffer ( byte[] input, int length, Cipher dcipher1, Cipher dcipher2  ) throws Exception {

        byte[][] mplain = symDecryption( input, length, dcipher1 );
        mplain = symDecryption( mplain[0], new BigInteger(mplain[1]).intValue(), dcipher2 );
        mplain[0] = Arrays.copyOfRange(mplain[0],0, new BigInteger(mplain[1]).intValue() );

        return mplain;
    }

    /**
     * Gets AES Key
     * */
    public static SecretKey getAESKey(String key) throws Exception {
        SecretKey symKey = null;
        byte[] input = Hex.decode(key);
        symKey = new SecretKeySpec(input, "AES");
        return symKey;
    }

}
