package securityencryption;

/**
 * Created by Vinod Singh on 5/5/2017.
 */

import android.os.AsyncTask;
import android.os.Environment;

import org.spongycastle.util.encoders.Base64;
import org.spongycastle.util.encoders.Hex;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.security.Key;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Security;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import commons.SDKConstants;
import commons.SDKHelper;
import iclasses.DecryptDataCallback;
import iclasses.EncryptDataCallBack;
import iclasses.mediavaultcallback.MediaVaultDecryptDataCallback;
import iclasses.mediavaultcallback.MediaVaultEncryptDataCallBack;
import model.DataToPBCModel;
import model.MatchTransactionModel;
import model.MediaVaultBlockModel;
import model.MediaVaultDataToPBCModel;
import utils.SDKUtils;

import static securityencryption.EncryptDecryptHelper.asymDecryption;
import static securityencryption.EncryptDecryptHelper.asymEncryption;


public class EncryptDecryptData {

    /**
     * @param args
     */
    final private static String BTCprivKey = "18E14A7B6A307F426A94F8114701E7C8E774E7F9A47E2C2035DB29A206321725";

    static {
        Security.insertProviderAt(new org.spongycastle.jce.provider.BouncyCastleProvider(), 1);
    }


    String rootPath = Environment.getExternalStorageDirectory()
            .getAbsolutePath() + SDKHelper.FILE_PATH;
    /**
     * This async class object is used to process encryption
     */
    private AsyncTask<Void, Void, DataToPBCModel> generateEncryptionTask = null;
    private AsyncTask<Void, Void, String> generateDecryptionTask = null;
    private AsyncTask<Void, Void, MediaVaultDataToPBCModel> generateMediaEncryptionTask = null;
    private EncryptDataCallBack mEncryptDataCallBack = null;
    private DecryptDataCallback mDecryptDataCallback = null;

    /**
     * Method used to encrypt data for BitVault to BitVault and Desktop to Desktop case
     *
     * @param tag,receiverAddress
     * @param BTCpubKey
     * @param txId
     * @param fileLocation,appId,pbcId
     * @return
     */
    public void encryptData(final String tag, final String receiverAddress, final String BTCpubKey, final String txId,
                            final String fileLocation, final String pbcId, final String appId,final String senderAddress,
                            final String msgCase, final String webserverkey, EncryptDataCallBack encryptDataCallBack) {
        this.mEncryptDataCallBack = encryptDataCallBack;
        SDKUtils.showLog(SDKHelper.ENCRYPTION_START,"" + System.currentTimeMillis());
        generateEncryptionTask = new AsyncTask<Void, Void, DataToPBCModel>() {
            @Override
            protected DataToPBCModel doInBackground(Void... params) {
                // Generate Symmetric Keys from TXID
                SecretKey SymKey = null;
                try {
                    if(msgCase != null && msgCase.equalsIgnoreCase(SDKHelper.BITVAULT_TO_DESKTOP)){
                        SymKey =  EncryptDecryptHelper.getAESKey(txId);
                    }else{
                        SymKey = EncryptDecryptHelper.genSymmetricKey(txId);
                    }

                    SDKUtils.showLog(SDKHelper.TAG_ENCRYPTION, SymKey.toString());

                    // Get Bitcoin Keys from receiver address
                    PublicKey PubKey = null;
                    PubKey = EncryptDecryptHelper.getBTCPublicKey(BTCpubKey);
                    SDKUtils.showLog(SDKHelper.TAG_ENCRYPTION, PubKey.toString());

                    // generate random session key
                    Key SessionKey = null;
                    SessionKey = EncryptDecryptHelper.genSessionKey();
                    byte[] EncSessionKey = SessionKey.getEncoded();
                    String strSessionkey = Hex.toHexString(EncSessionKey);


                    // ENCRYPTION -------------------------------------
                    try{
                        byte[] buffer = new byte[EncryptDecryptHelper.ENC_BUFFER_LEN];

                        File f = new File (fileLocation);
                        InputStream is = new FileInputStream(f);
                        BufferedInputStream inStream = new BufferedInputStream(is);

                        File file =new File(rootPath);
                        if (!file.exists())
                            file.mkdirs();
                        File secureFile = new File(file, SDKHelper.FILE_NAME);

                        OutputStream os = new FileOutputStream(secureFile);

                        Cipher ecipher1 = Cipher.getInstance( EncryptDecryptHelper.SYMALGORITHM, "SC");
                        ecipher1.init(Cipher.ENCRYPT_MODE, SymKey, EncryptDecryptHelper.ivSpec);
                        Cipher ecipher2 = Cipher.getInstance( EncryptDecryptHelper.SYMALGORITHM, "SC");
                        ecipher2.init(Cipher.ENCRYPT_MODE, SessionKey, EncryptDecryptHelper.ivSpec);

                        SDKUtils.showLog("Encrypting the File..." + "\n" ,"InProgress");

                        int bytesRead = inStream.read(buffer);

                        while ( bytesRead != -1){

                            // Encrypt buffer
                            byte[][] bWrite = EncryptDecryptHelper.EncryptSendingBuffer ( buffer, bytesRead, ecipher1, ecipher2 );

                            int len = (new BigInteger(bWrite[1]).intValue());

                            SDKUtils.showLog("Enc length","" +len);
                            os.write( bWrite[0], 0, len );
                            bytesRead = inStream.read(buffer);
                        }
                        inStream.close();
                        os.flush();
                        os.close();
                        SDKUtils.showLog("File Encrypted\n","Success");

                    }catch(IOException e){
                        SDKUtils.showLog("FileEncryption_Exception\n","IO Exception");
                    }

                    // Encrypt session key with public key
                    byte[] kcipher = asymEncryption(EncSessionKey, PubKey);
                    String EncryptedSessionKey = Base64.toBase64String(kcipher);

                    // Generate CRC mechanism
        /* 0 -> TAG: SecureMessage = 01
         * 1 -> TXID Hash
         * 2 -> Receiver Wallet Address
         * 3 -> Encrypted Message
         * 4 -> Encrypted Session Key
         * 5 -> pbcId
         * 6 -> appId
         * 7 -> timeStamp
         * 8 -> CRC of 0 to 6
         * 9 -> CRC of 0 to 3
         */

                    InputStream inputMessage = new FileInputStream(rootPath + SDKHelper.FILE_NAME);

                    long timeStamp = System.currentTimeMillis();
                    String EncryptMsgforCrc = tag + "|$$|" + Base64.toBase64String(DataHelper.hashGenerate(txId)) + "|$$|" + receiverAddress +
                            "|$$|" + DataHelper.calculateHash(inputMessage, SDKConstants.SHA_256)
                            + "|$$|" + EncryptedSessionKey + "|$$|" + pbcId + "|$$|" + appId + "|$$|" + timeStamp + "|$$|" + senderAddress + "|$$|" + webserverkey;

                    String mCRC = null;
                    mCRC = DataHelper.getCRC(EncryptMsgforCrc);
                    SDKUtils.showLog(SDKHelper.TAG_ENCRYPTION, mCRC);

                    DataToPBCModel dataToPBCModel = new DataToPBCModel();
                    dataToPBCModel.setTag(tag);
                    dataToPBCModel.setHashTXId(Base64.toBase64String(DataHelper.hashGenerate(txId)));
                    dataToPBCModel.setReceiverWalletAdress(receiverAddress);
                    dataToPBCModel.setCrc(mCRC);
                    dataToPBCModel.setFilepath(rootPath + SDKHelper.FILE_NAME);
                    dataToPBCModel.setEncryptedSessionKey(EncryptedSessionKey);
                    dataToPBCModel.setPbcId(pbcId);
                    dataToPBCModel.setAppId(appId);
                    dataToPBCModel.setTimeStamp(timeStamp);
                    dataToPBCModel.setTxId(txId);
                    dataToPBCModel.setSenderAddress(senderAddress);
                    dataToPBCModel.setWebServerKey(webserverkey);
                    if(msgCase != null && msgCase.equalsIgnoreCase(SDKHelper.BITVAULT_TO_DESKTOP)){
                        dataToPBCModel.setSessionKey(strSessionkey);
                    }else{
                        dataToPBCModel.setSessionKey("");
                    }

                    return dataToPBCModel;
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            protected void onPostExecute(DataToPBCModel dataToPBCModel) {
                super.onPostExecute(dataToPBCModel);
                mEncryptDataCallBack.encryptDataResponse(dataToPBCModel, msgCase);
            }
        }.execute();
    }

    /**
     * Method used to decrypt data for BitVault to BitVault and Desktop to Desktop case
     *
     * @param txId
     * @param EncryptedSessionKey
     * @param decryptedInput
     * @param matchTransactionModel
     * @return
     */

    public void decryptData(final String txId, final String EncryptedSessionKey, final InputStream decryptedInput, DecryptDataCallback decryptDataCallback, final MatchTransactionModel matchTransactionModel) {
        this.mDecryptDataCallback = decryptDataCallback;
        SDKUtils.showLog(SDKHelper.DECRYPTION_START, "" + System.currentTimeMillis());
        generateDecryptionTask = new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                try {
                    File file =new File(rootPath);
                    if (!file.exists())
                        file.mkdirs();
                    String receivedFilePath = rootPath + System.currentTimeMillis() + SDKHelper.RECEIVED_FILE_NAME;
                    PrivateKey PrivKey = EncryptDecryptHelper.getBTCPrivateKey(BTCprivKey);

                    // Decrypt session key
                    byte[] dSessionKey = asymDecryption(Base64.decode(EncryptedSessionKey), PrivKey);
                    Key RcvSessionKey = new SecretKeySpec(dSessionKey, 0, dSessionKey.length, "AES");

                    // get TxId to decrypt message
                    SecretKey RegenSymKey = EncryptDecryptHelper.genSymmetricKey(txId);


                        byte[] buffer = new byte[EncryptDecryptHelper.DEC_BUFFER_LEN];
                        BufferedInputStream inStream = new BufferedInputStream(decryptedInput);

                        OutputStream os = new FileOutputStream(receivedFilePath);

                        Cipher dcipher1 = Cipher.getInstance( EncryptDecryptHelper.SYMALGORITHM, "SC");
                        dcipher1.init( Cipher.DECRYPT_MODE, RcvSessionKey, EncryptDecryptHelper.ivSpec );
                        Cipher dcipher2 = Cipher.getInstance( EncryptDecryptHelper.SYMALGORITHM, "SC");
                        dcipher2.init( Cipher.DECRYPT_MODE, RegenSymKey, EncryptDecryptHelper.ivSpec );


                    SDKUtils.showLog("Decrypting the File...","InProgress");

                        int bytesRead = inStream.read(buffer);

                        while ( bytesRead != -1){

                            // Decrypt buffer
                            byte[][] bWrite = EncryptDecryptHelper.DecryptRcvBuffer ( buffer, bytesRead, dcipher1, dcipher2  );

                            int len = (new BigInteger(bWrite[1]).intValue());
                            SDKUtils.showLog("*******" ,"" +len);

                            os.write( bWrite[0], 0, len );
                            bytesRead = inStream.read(buffer);
                        }

                        inStream.close();
                        os.flush();
                        os.close();
                        SDKUtils.showLog("File Decrypted\n","done");

                        return receivedFilePath;
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }

            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                mDecryptDataCallback.decryptedMessage(s, matchTransactionModel);
            }
        }.execute();
    }

    /**
     * Method used to encrypt data for Media Vault file case
     *
     * @param tag,receiverAddress
     * @param BTCpubKey
     * @param txId
     * @param fileLocation,appId,pbcId
     * @return
     */
    public void encryptMedia(final String tag, final String receiverAddress, final String BTCpubKey, final String txId,
                            final String fileLocation, final String pbcId, final String appId, final String senderAddress,
                            final String webserverkey,
                            final MediaVaultEncryptDataCallBack encryptDataCallBack) {
        SDKUtils.showLog(SDKHelper.ENCRYPTION_START, "" + System.currentTimeMillis());
        generateMediaEncryptionTask = new AsyncTask<Void, Void, MediaVaultDataToPBCModel>() {
            @Override
            protected MediaVaultDataToPBCModel doInBackground(Void... params) {
                try {
                    // Get Bitcoin Keys from receiver address
                    PublicKey PubKey = null;
                    PubKey = EncryptDecryptHelper.getBTCPublicKey(BTCpubKey);
                    SDKUtils.showLog(SDKHelper.TAG_ENCRYPTION, PubKey.toString());

                    // generate random session key
                    Key fileKey = null;
                    fileKey = EncryptDecryptHelper.genSessionKey();
                    byte[] EncFileKey = fileKey.getEncoded();

                    //concatenate txid and sessionKeyInString
                    String fileEncryptionKey = Hex.toHexString(EncFileKey) + txId;
                    //Generate double hash of fileEncryptionKeyInString
                    String doubleHashOfEncryptionKey = Hex.toHexString(DataHelper.hashGenerate(DataHelper.hashGenerate
                            (Hex.decode(fileEncryptionKey))));
                    //Get in Key format of doubleHashOfEncryptionKey
                    SecretKey SymEncryptionKey = new SecretKeySpec(Hex.decode(doubleHashOfEncryptionKey), "AES");

                    // ENCRYPTION -------------------------------------
                    try{
                        byte[] buffer = new byte[EncryptDecryptHelper.ENC_BUFFER_LEN];

                        File f = new File (fileLocation);
                        InputStream is = new FileInputStream(f);
                        BufferedInputStream inStream = new BufferedInputStream(is);

                        File file =new File(rootPath);
                        if (!file.exists())
                            file.mkdirs();
                        File secureFile = new File(file, SDKHelper.MEDIA_VAULT_FILE_NAME);

                        OutputStream os = new FileOutputStream(secureFile);

                        Cipher ecipher1 = Cipher.getInstance( EncryptDecryptHelper.SYMALGORITHM, "SC");
                        ecipher1.init(Cipher.ENCRYPT_MODE, SymEncryptionKey, EncryptDecryptHelper.ivSpec);

                        SDKUtils.showLog("Encrypting the media File..." + "\n" ,"InProgress");

                        int bytesRead = inStream.read(buffer);

                        while ( bytesRead != -1){

                            // Encrypt buffer
                            byte[][] bWrite = EncryptDecryptHelper.symEncryption( buffer, bytesRead, ecipher1 );


                            int len = (new BigInteger(bWrite[1]).intValue());

                            SDKUtils.showLog("Enc length","" +len);
                            os.write( bWrite[0], 0, len );
                            bytesRead = inStream.read(buffer);
                        }
                        inStream.close();
                        os.flush();
                        os.close();
                        SDKUtils.showLog("Media Encrypted\n","Success");

                    }catch(IOException e){
                        SDKUtils.showLog("MediaEncryption_Exception\n","IO Exception");
                    }

                    // Encrypt file key with public key
                    byte[] kcipherFileKey = EncryptDecryptHelper.asymEncryption(EncFileKey, PubKey);
                    //Convert kcipherFileKey into string
                    String encryptedFileKey = Base64.toBase64String(kcipherFileKey);

                    // Encrypt txid key with public key
                    byte[] kcipherTxid = EncryptDecryptHelper.asymEncryption(Hex.decode(txId), PubKey);
                    //Convert kcipherTxid into string
                    String encryptedTxid = Base64.toBase64String(kcipherTxid);

                    //Generate double hash of encryptedFileKey for EncryptedUniqueFileId
                    String EncryptedUniqueFileId = Base64.toBase64String(DataHelper.hashGenerate(DataHelper.hashGenerate
                            ((encryptedFileKey))));


                    // Convert Encrypted file into string....
                    InputStream inputMessage = new FileInputStream(rootPath + SDKHelper.MEDIA_VAULT_FILE_NAME);
                    final String fileHash =  DataHelper.calculateHash(inputMessage, SDKConstants.SHA_256);
                    // Generate CRC mechanism
                    long timeStamp = System.currentTimeMillis();
                    String EncryptMsgforCrc = tag + "|$$|" + EncryptedUniqueFileId
                            + "|$$|" + receiverAddress
                            + "|$$|" + fileHash
                            + "|$$|" + pbcId
                            + "|$$|" + appId
                            + "|$$|" + timeStamp
                            + "|$$|" + webserverkey;
                    SDKUtils.showErrorLog("EncryptMsgforCrc..",EncryptMsgforCrc);

                    String mCRC = null;
                    mCRC = DataHelper.getCRC(EncryptMsgforCrc);
                    SDKUtils.showLog(SDKHelper.TAG_ENCRYPTION, mCRC);


                    MediaVaultDataToPBCModel dataToPBCModel = new MediaVaultDataToPBCModel();
                    dataToPBCModel.setTag(tag);
                    dataToPBCModel.setEncryptedTXId(encryptedTxid);
                    dataToPBCModel.setWalletAddress(receiverAddress);
                    dataToPBCModel.setCrc(mCRC);
                    dataToPBCModel.setFilepath(rootPath + SDKHelper.MEDIA_VAULT_FILE_NAME);
                    dataToPBCModel.setEncryptedFileKey(encryptedFileKey);
                    dataToPBCModel.setEncryptedUniqueFileId(EncryptedUniqueFileId);
                    dataToPBCModel.setPbcId(pbcId);
                    dataToPBCModel.setAppId(appId);
                    dataToPBCModel.setTimeStamp(timeStamp);
                    dataToPBCModel.setTxId(txId);
                    dataToPBCModel.setWebServerKey(webserverkey);
                    return dataToPBCModel;
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            protected void onPostExecute(MediaVaultDataToPBCModel dataToPBCModel) {
                super.onPostExecute(dataToPBCModel);
                SDKUtils.showLog(SDKHelper.ENCRYPTION_END, "" + System.currentTimeMillis());
                encryptDataCallBack.encryptMediaResponse(dataToPBCModel);
            }
        }.execute();
    }

    /**
     * Method used to decrypt data for media vault file case
     *
     * @param fileKey
     * @param decryptedInput
     * @param txId
     * @param matchTransactionModel
     * @return
     */
    public void decryptMediaFile(final String txId, final String fileKey, final InputStream decryptedInput,
                                 final MediaVaultDecryptDataCallback decryptDataCallback,
                                 final MediaVaultBlockModel matchTransactionModel) {
        SDKUtils.showLog(SDKHelper.DECRYPTION_START, "" + System.currentTimeMillis());
        generateDecryptionTask = new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                try {
                    File file =new File(rootPath);
                    if (!file.exists())
                        file.mkdirs();
                    String receivedFilePath = rootPath + System.currentTimeMillis() + SDKHelper.RECEIVED_MEDIA_FILE_NAME;
                    PrivateKey PrivKey = EncryptDecryptHelper.getBTCPrivateKey(BTCprivKey);

                    // Decrypt file key
                    byte[] DecryptedFileKey = EncryptDecryptHelper.asymDecryption(Base64.decode(fileKey), PrivKey);

                    // Decrypt TxId
                    byte[] DecryptedTxId = EncryptDecryptHelper.asymDecryption(Base64.decode(txId), PrivKey);

                    //concatenate DecryptedFileKey and DecryptedTxId
                    String fileEncryptionKey = Hex.toHexString(DecryptedFileKey) + Hex.toHexString(DecryptedTxId);
                    //Generate double hash of fileEncryptionKeyInString
                    String doubleHashOfEncryptionKey = Hex.toHexString(DataHelper.hashGenerate(DataHelper.hashGenerate
                            (Hex.decode(fileEncryptionKey))));


                    //Get in Key format of doubleHashOfEncryptionKey
                    SecretKey SymEncryptionKey = new SecretKeySpec(Hex.decode(doubleHashOfEncryptionKey), "AES");


                    byte[] buffer = new byte[EncryptDecryptHelper.DEC_BUFFER_LEN];
                    BufferedInputStream inStream = new BufferedInputStream(decryptedInput);

                    OutputStream os = new FileOutputStream(receivedFilePath);

                    Cipher dcipher1 = Cipher.getInstance( EncryptDecryptHelper.SYMALGORITHM, "SC");
                    dcipher1.init( Cipher.DECRYPT_MODE, SymEncryptionKey, EncryptDecryptHelper.ivSpec );

                    SDKUtils.showLog("Decrypting the File...","InProgress");

                    int bytesRead = inStream.read(buffer);
                    byte[][] bWrite = null;
                    while ( bytesRead != -1){

                        // Decrypt buffer
                        bWrite = EncryptDecryptHelper.symDecryptionMedia (buffer, bytesRead, dcipher1);
                        int len = (new BigInteger(bWrite[1]).intValue());

                        SDKUtils.showLog("*******",""+len);
                        os.write( bWrite[0], 0, len );
                        bytesRead = inStream.read(buffer);
                    }
                    byte[] output = dcipher1.doFinal();
                    os.write( output, 0, output.length );

                    inStream.close();
                    os.flush();
                    os.close();
                    SDKUtils.showLog("File Decrypted\n","done");

                    SDKUtils.showLog(SDKHelper.DECRYPTION_END, "" + System.currentTimeMillis());
                    return receivedFilePath;
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }

            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                decryptDataCallback.decryptedMediaVaultFile(s, matchTransactionModel);
            }
        }.execute();
    }
}

