package model;

/**
 * Created by Vinod Singh on 1/6/17.
 */

public class MatchTransactionModel {
    private String txId;
    private String hashTxId;
    private String url;

    private String timestamp;

    private String pbcId;


    private String appId;
    private String crc;

    private String receiver;

    private String tag;

    private String sessionKey;

    private String senderAddress;
    private String webServerKey;

    public MatchTransactionModel(String txId,String hashTxId,String url,String crc,String timestamp,String pbcId,String appId,String receiver,
    String tag,String sessionKey,String sender,String webServerKey){
        this.txId=txId;
        this.hashTxId=hashTxId;
        this.url=url;
        this.timestamp = timestamp;
        this.crc = crc;
        this.pbcId = pbcId;
        this.appId = appId;
        this.receiver = receiver;
        this.tag = tag;
        this.sessionKey = sessionKey;
        this.senderAddress = sender;
        this.webServerKey = webServerKey;
    }


    public String getWebServerKey() {
        return webServerKey;
    }

    public void setWebServerKey(String webServerKey) {
        this.webServerKey = webServerKey;
    }
    public String getSenderAddress() {
        return senderAddress;
    }

    public void setSenderAddress(String senderAddress) {
        this.senderAddress = senderAddress;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getPbcId() {
        return pbcId;
    }

    public void setPbcId(String pbcId) {
        this.pbcId = pbcId;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getCrc() {
        return crc;
    }

    public void setCrc(String crc) {
        this.crc = crc;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getSessionKey() {
        return sessionKey;
    }

    public void setSessionKey(String sessionKey) {
        this.sessionKey = sessionKey;
    }


    public String getTxId() {
        return txId;
    }

    public void setTxId(String txId) {
        this.txId = txId;
    }

    public String getHashTxId() {
        return hashTxId;
    }

    public void setHashTxId(String hashTxId) {
        this.hashTxId = hashTxId;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

}
