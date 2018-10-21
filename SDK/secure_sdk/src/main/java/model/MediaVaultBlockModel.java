package model;

/**
 * Created by linchpin on 20/9/17.
 */

public class MediaVaultBlockModel {
    private String message;

    private String status;

    private ResultSet resultSet;

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

    public ResultSet getResultSet ()
    {
        return resultSet;
    }

    public void setResultSet (ResultSet resultSet)
    {
        this.resultSet = resultSet;
    }
    public class ResultSet {
        private String id;

        private String timestamp;

        private String pbcId;

        private String fileId;

        private String webServerKey;

        private String appId;

        private String crc;

        private String walletAddress;

        private String tag;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
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

        public String getWebServerKey() {
            return webServerKey;
        }

        public void setWebServerKey(String webServerKey) {
            this.webServerKey = webServerKey;
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

        public String getWalletAddress() {
            return walletAddress;
        }

        public void setWalletAddress(String walletAddress) {
            this.walletAddress = walletAddress;
        }

        public String getTag() {
            return tag;
        }

        public void setTag(String tag) {
            this.tag = tag;
        }
    }
}



