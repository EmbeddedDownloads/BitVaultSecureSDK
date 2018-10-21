package model.eotmodel;

/**
 * Created by vvdn on 11/20/2017.
 */

public class EotWalletDetail {
    private String address = "";
    private byte[] public_key = null;
    private String seed="";

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public byte[] getPublic_key() {
        return public_key;
    }

    public void setPublic_key(byte public_key[]) {
        this.public_key = public_key;
    }

    public String getSeed() {
        return seed;
    }

    public void setSeed(String seed) {
        this.seed = seed;
    }
}
