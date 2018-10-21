package model.eotmodel;

/**
 * Created by vvdn on 11/1/2017.
 */

public class EotPriceConverterModel {
    private String usdPrice = "";
    private String btcPrice = "";
    private String lastUpdate = "";

    public String getUsdPrice() {
        return usdPrice;
    }

    public void setUsdPrice(String usdPrice) {
        this.usdPrice = usdPrice;
    }

    public String getBtcPrice() {
        return btcPrice;
    }

    public void setBtcPrice(String btcPrice) {
        this.btcPrice = btcPrice;
    }

    public String getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(String lastUpdate) {
        this.lastUpdate = lastUpdate;
    }
}
