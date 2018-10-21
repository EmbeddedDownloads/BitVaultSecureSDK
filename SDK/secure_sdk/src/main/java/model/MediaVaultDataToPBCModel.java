package model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by vvdn on 9/12/2017.
 */

public class MediaVaultDataToPBCModel implements Parcelable {
    private String tag = "";
    private String encryptedTXId = "";
    private String walletAddress = "";
    private String filepath = "";
    private String fileLocation = "";

    public String getFileLocation() {
        return fileLocation;
    }

    public void setFileLocation(String fileLocation) {
        this.fileLocation = fileLocation;
    }

    private String crc = "";
    private String encryptedFileKey = "";
    private String encryptedUniqueFileId = "";
    private String appId = "";
    private String pbcId = "";
    private long timeStamp;
    private String txId = "";
    private String webServerKey;
    private String signature;

    public String getEncryptedUniqueFileId() {
        return encryptedUniqueFileId;
    }

    public void setEncryptedUniqueFileId(String encryptedUniqueFileId) {
        this.encryptedUniqueFileId = encryptedUniqueFileId;
    }


    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public String getWebServerKey() {
        return webServerKey;
    }

    public void setWebServerKey(String webServerKey) {
        this.webServerKey = webServerKey;
    }

    public String getWalletAddress() {
        return walletAddress;
    }

    public void setWalletAddress(String walletAddress) {
        this.walletAddress = walletAddress;
    }

    public String getTxId() {
        return txId;
    }

    public void setTxId(String txId) {
        this.txId = txId;
    }

    public String getEncryptedFileKey() {
        return encryptedFileKey;
    }

    public void setEncryptedFileKey(String encryptedFileKey) {
        this.encryptedFileKey = encryptedFileKey;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getPbcId() {
        return pbcId;
    }

    public void setPbcId(String pbcId) {
        this.pbcId = pbcId;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getEncryptedTXId() {
        return encryptedTXId;
    }

    public void setEncryptedTXId(String encryptedTXId) {
        this.encryptedTXId = encryptedTXId;
    }


    public String getFilepath() {
        return filepath;
    }

    public void setFilepath(String filepath) {
        this.filepath = filepath;
    }

    public String getCrc() {
        return crc;
    }

    public void setCrc(String crc) {
        this.crc = crc;
    }

    @Override
    public String toString() {
        return "DataToPBCModel{" +
                "tag='" + tag + '\'' +
                ", encryptedTXId='" + encryptedTXId + '\'' +
                ", WalletAdress='" + walletAddress + '\'' +
                ", filepath='" + filepath + '\'' +
                ", crc='" + crc + '\'' +
                ", encryptedFileKey='" + encryptedFileKey + '\'' +
                ", appId='" + appId + '\'' +
                ", pbcId='" + pbcId + '\'' +
                ", timeStamp=" + timeStamp +
                ", txId='" + txId + '\'' +
                ", webServerKey='" + webServerKey + '\'' +
                ", fileLocation='" + fileLocation + '\'' +
                ", encryptedUniqueFileId ='" + encryptedUniqueFileId + '\'' +
                ", signature='" + signature + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.tag);
        dest.writeString(this.encryptedTXId);
        dest.writeString(this.walletAddress);
        dest.writeString(this.filepath);
        dest.writeString(this.crc);
        dest.writeString(this.encryptedFileKey);
        dest.writeString(this.appId);
        dest.writeString(this.pbcId);
        dest.writeLong(this.timeStamp);
        dest.writeString(this.txId);
        dest.writeString(this.webServerKey);
        dest.writeString(this.encryptedUniqueFileId);
        dest.writeString(this.fileLocation);
        dest.writeString(this.signature);
    }

    public MediaVaultDataToPBCModel() {
    }

    protected MediaVaultDataToPBCModel(Parcel in) {
        this.tag = in.readString();
        this.encryptedTXId = in.readString();
        this.walletAddress = in.readString();
        this.filepath = in.readString();
        this.crc = in.readString();
        this.encryptedFileKey = in.readString();
        this.appId = in.readString();
        this.pbcId = in.readString();
        this.timeStamp = in.readLong();
        this.txId = in.readString();
        this.webServerKey = in.readString();
        this.encryptedUniqueFileId = in.readString();
        this.fileLocation = in.readString();
        this.signature = in.readString();
    }

    public static final Creator<MediaVaultDataToPBCModel> CREATOR = new Creator<MediaVaultDataToPBCModel>() {
        @Override
        public MediaVaultDataToPBCModel createFromParcel(Parcel source) {
            return new MediaVaultDataToPBCModel(source);
        }

        @Override
        public MediaVaultDataToPBCModel[] newArray(int size) {
            return new MediaVaultDataToPBCModel[size];
        }
    };
}
