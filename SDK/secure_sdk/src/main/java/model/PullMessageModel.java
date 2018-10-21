package model;

/**
 * Created by Vinod Singh on 25/5/17.
 */

public class PullMessageModel {
    private String message;

    private String status;

    private ResultSet[] resultSet;

    public String getMessage ()
    {
        return message;
    }

    public void setMessage (String message)
    {
        this.message = message;
    }

    public String getStatus ()
    {
        return status;
    }

    public void setStatus (String status)
    {
        this.status = status;
    }

    public ResultSet[] getResultSet ()
    {
        return resultSet;
    }

    public void setResultSet (ResultSet[] resultSet)
    {
        this.resultSet = resultSet;
    }

    public class ResultSet {
        private String timestamp;

        private String pbcId;

        private String fileId;

        private String appId;

        private String transactionId;

        private String crc;

        private String receiver;

        private String tag;

        private String sessionKey;
        private String sender;
        private String webServerKey;
        public String getWebServerKey() {
            return webServerKey;
        }

        public void setWebServerKey(String webServerKey) {
            this.webServerKey = webServerKey;
        }



        public String getSender() {
            return sender;
        }

        public void setSender(String sender) {
            this.sender = sender;
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

        public String getFileId() {
            return fileId;
        }

        public void setFileId(String fileId) {
            this.fileId = fileId;
        }

        public String getAppId() {
            return appId;
        }

        public void setAppId(String appId) {
            this.appId = appId;
        }

        public String getTransactionId() {
            return transactionId;
        }

        public void setTransactionId(String transactionId) {
            this.transactionId = transactionId;
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
    }
}
