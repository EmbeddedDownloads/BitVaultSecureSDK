package securityencryption;

import org.spongycastle.util.Strings;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.zip.CRC32;
import java.util.zip.Checksum;

/**
 * Created by Vinod Singh on 5/5/17.
 */

public class DataHelper {
    /***
     * Generate CRC
     *
     * @param text
     * @return
     * @throws Exception
     */
    public static String getCRC(String text) throws Exception {
        Checksum checksum = new CRC32();
        byte[] checksumbyte = Strings.toByteArray(text);
        checksum.update(checksumbyte, 0, checksumbyte.length);
        long CRCofMessageTxID = checksum.getValue();
        return Long.toHexString(CRCofMessageTxID);
    }


    /**
     * Hashes generation of byte  type of data
     *
     * @param txId
     * @return
     */
    public static byte[] hashGenerate(byte[] txId) {
        MessageDigest digest = null;
        try {
            digest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        byte[] TxIDHash = digest.digest(txId);
        return TxIDHash;
    }

    /**
     * Hashes generation of String type of data
     *
     * @param txId
     * @return
     */
    public static byte[] hashGenerate(String txId) {
        MessageDigest digest = null;
        try {
            digest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        byte[] TxIDHash = digest.digest(txId.getBytes(StandardCharsets.UTF_8));
        return TxIDHash;
    }

    /**
     * Hashes generation of Encrypt Message
     *
     * @param encryptMessage
     * @return
     */
    public static String hashGenrateForMessage(String encryptMessage) {
        MessageDigest digest = null;
        try {
            digest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        byte[] TxIDHash = digest.digest(encryptMessage.getBytes(StandardCharsets.UTF_8));
        return convertByteArrayToHexString(TxIDHash);
    }


    /**
     * Convert Generated Hash bytes into Hex String
     *
     * @param arrayBytes
     * @return
     */
    private static String convertByteArrayToHexString(byte[] arrayBytes) {
        StringBuffer stringBuffer = new StringBuffer();
        for (int i = 0; i < arrayBytes.length; i++) {
            stringBuffer.append(Integer.toString((arrayBytes[i] & 0xff) + 0x100, 16)
                    .substring(1));
        }
        return stringBuffer.toString();
    }

    /***
     * This method is used to make file from the input stream
     *
     * @param inputStream
     * @return
     */
    public byte[] createFileFromInputStream(InputStream inputStream) {
        try {
            byte[] filecontent = new byte[inputStream.available()];
            inputStream.read(filecontent, 0, inputStream.available());
            return filecontent;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /***
     * This method is used to calculate dataHashfrom the input stream
     *
     * @param is
     * @return
     */
    public static String calculateHash(InputStream is, final String algoName) throws Exception {
        final byte[] buffer = new byte[8192];
        final MessageDigest md = MessageDigest.getInstance(algoName);


        final DigestInputStream dis = new DigestInputStream(is, md);
        try {
            while (dis.read(buffer) != -1) {
                ;
            }
        } finally {
            dis.close();
            is.close();
        }
        return convertByteArrayToHexString(md.digest());

    }

    /***
     * This method is used to calculate dataHashfrom the byte array
     *
     * @param content
     * @return
     */
    public static String calculateHash(final byte[] content, final String algoName) throws Exception {

        InputStream is = new ByteArrayInputStream(content);

        final byte[] buffer = new byte[8192];
        final MessageDigest md = MessageDigest.getInstance(algoName);


        final DigestInputStream dis = new DigestInputStream(is, md);
        try {
            while (dis.read(buffer) != -1) {
                ;
            }
        } finally {
            dis.close();
            is.close();
        }
        return convertByteArrayToHexString(md.digest());

    }
}
