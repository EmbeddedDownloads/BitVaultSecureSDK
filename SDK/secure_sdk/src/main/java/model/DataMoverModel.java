package model;

/**
 * Created by Vinod Singh on 5/5/17.
 */

public class DataMoverModel {
    // get values from app end
    private String receiverAddress="";
    private String message_tag="";
    private String messageFiles="";
    private String PbcId="";

    public String getMessageFiles() {
        return messageFiles;
    }

    public void setMessageFiles(String messageFiles) {
        this.messageFiles = messageFiles;
    }

    public String getReceiverAddress() {
        return receiverAddress;
    }

    public void setReceiverAddress(String receiverAddress) {
        this.receiverAddress = receiverAddress;
    }

    public String getMessage_tag() {
        return message_tag;
    }

    public void setMessage_tag(String message_tag) {
        this.message_tag = message_tag;
    }

    public String getPbcId() {
        return PbcId;
    }

    public void setPbcId(String pbcId) {
        PbcId = pbcId;
    }

}
