package model;

/**
 * Created by ${e} on 8/23/2017.
 */

public class PushDataModel {
    private String DeviceToken= "";
    private String AppId = "";

    public String getDeviceToken() {
        return DeviceToken;
    }

    public void setDeviceToken(String deviceToken) {
        DeviceToken = deviceToken;
    }

    public String getAppId() {
        return AppId;
    }

    public void setAppId(String appId) {
        AppId = appId;
    }
}
