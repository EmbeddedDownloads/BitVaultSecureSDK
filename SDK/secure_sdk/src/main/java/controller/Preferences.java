package controller;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;


public class Preferences {
    public static Preferences instance = new Preferences();
    private final String APPLICATION_SHARED_PREFERENCE_CG = "SecureSDK";
    private SharedPreferences preferences;
    private String KEY_PAIR_DATA = "key_pair";
    private String TAG = Preferences.class.getSimpleName();
    private Intent intent;
    private String WALLET_ADDRESS = "address";
    private String PRIVATE_KEY = "private_key";
    private String PUBLIC_KEY = "public_key";
    private String MQTT_CLIENT_ID = "client_id";
    private String DEVICE_ID = "device_id";
    private String SEED = "seed";
    private String APP_KEY = "app_key";
    private String EOT_CURRENT_BAL = "eot_bal";
    private boolean ISDATABASEEXISTS = false;
    private String DATABASE_EXISTS = "isdatabaseexists";
    private String WALLET = "wallet";
    private String WALLET_OBJ = "wallet_obj";
    private String IS_FIRST = "isFirstLaunch";

    public Preferences() {
        preferences = SDKControl.mInstance.getSharedPreferences(
                APPLICATION_SHARED_PREFERENCE_CG, Context.MODE_PRIVATE);
    }

    /**
     * Clear all the preference details of the user
     */
    public void clearPreferences() {
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.commit();
    }

    /***
     * This method is used to save data in local preferences
     *
     * @param context,text,PREFS_NAME,PREFS_KEY
     * @return
     *
     */
    public void saveData(Context context, String text, String PREFS_NAME, String PREFS_KEY) {
        SharedPreferences settings;
        SharedPreferences.Editor editor;
        settings = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        editor = settings.edit();

        editor.putString(PREFS_KEY, text);

        editor.commit();
    }

    /***
     * This method is used to get data from local preferences
     *
     * @param context,PREFS_NAME,PREFS_KEY
     * @return
     *
     */
    public String getData(Context context, String PREFS_NAME, String PREFS_KEY) {
        SharedPreferences settings;
        String text;
        settings = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        text = settings.getString(PREFS_KEY, "");
        return text;
    }

    /**
     * This method is used to save the client id of the client application
     * to receiver mqtt notifications
     *
     * @param mqtt_client_id
     */
    public void saveClientId(String mqtt_client_id) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(MQTT_CLIENT_ID, mqtt_client_id);
        editor.commit();
    }

    /**
     * Get the login password back to the user and set it to already filled details
     *
     * @return
     */
    public String getClientId() {
        String savedDeviceId = preferences.getString(MQTT_CLIENT_ID, "");
        return savedDeviceId;
    }

    /**
     * This method is used to save the client id of the client application
     * to receiver mqtt notifications
     *
     * @param mDeviceId
     */
    public void saveDeviceId(String mDeviceId) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(DEVICE_ID, mDeviceId);
        editor.commit();

    }

    /**
     * Get the login password back to the user and set it to already filled details
     *
     * @return
     */
    public String getDeviceId() {
        String savedDeviceId = preferences.getString(DEVICE_ID, "");
        return savedDeviceId;
    }

    public boolean ISDATABASEEXISTS() {
        boolean isDatabaseExists = preferences.getBoolean(DATABASE_EXISTS, false);
        return isDatabaseExists;
    }

    public void setISDATABASEEXISTS(boolean ISDATABASEEXISTS) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(DATABASE_EXISTS, ISDATABASEEXISTS);
        editor.commit();
    }

//    /**
//     * get Wallet
//     *
//     * @return
//     */
//    public EotKeyPair getWallet() {
//        Gson gson = new Gson();
//        String json = preferences.getString(WALLET_OBJ, "");
//        EotKeyPair mWalletkey = gson.fromJson(json, EotKeyPair.class);
//        return mWalletkey;
//    }
//
//    /**
//     * Save Wallet
//     *
//     * @param mWallet
//     */
//    public void setWallet(EotKeyPair mWallet) {
//        SharedPreferences.Editor editor = preferences.edit();
//        Gson gson = new Gson();
//        String json = gson.toJson(mWallet);
//        editor.putString(WALLET_OBJ, json);
//        editor.commit();
//    }
//
//    /**
//     * This method is used to save the client id of the client application
//     * to receiver mqtt notifications
//     *
//     * @param mDeviceId
//     */
//    public void storeSeed(String mDeviceId) {
//        SharedPreferences.Editor editor = preferences.edit();
//        editor.putString(SEED, mDeviceId);
//        editor.commit();
//
//    }
//
//    /**
//     * Get the login password back to the user and set it to already filled details
//     *
//     * @return
//     */
//    public String getSeed() {
//        String savedDeviceId = preferences.getString(SEED, "");
//        return savedDeviceId;
//    }
//
//    /**
//     * This method is used to save the client id of the client application
//     * to receiver mqtt notifications
//     *
//     * @param mDeviceId
//     */
//    public void storeEotCurrentBalance(String mDeviceId) {
//        SharedPreferences.Editor editor = preferences.edit();
//        editor.putString(EOT_CURRENT_BAL, mDeviceId);
//        editor.commit();
//
//    }
//
//    /**
//     * Get the login password back to the user and set it to already filled details
//     *
//     * @return
//     */
//    public String getEotCurrentBalance() {
//        String savedDeviceId = preferences.getString(EOT_CURRENT_BAL, "");
//        return savedDeviceId;
//    }

    /**
     * This method is used to save the client id of the client application
     * to receiver mqtt notifications
     *
     * @param appKey
     */
    public void setAppKey(String appKey) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(APP_KEY, appKey);
        editor.commit();

    }

    /**
     * Get the login password back to the user and set it to already filled details
     *
     * @return
     */
    public String getAppKey() {
        String appKey = preferences.getString(APP_KEY, "");
        return appKey;
    }


    /**
     * This method is used to save the client id of the client application
     * to receiver mqtt notifications
     *
     * @param isFirst
     */
    public void isFirstLaunch(boolean isFirst) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(IS_FIRST, isFirst);
        editor.commit();

    }

    /**
     * Get the login password back to the user and set it to already filled details
     *
     * @return
     */
    public boolean getIsFirstLaunch() {
        boolean isFirst = preferences.getBoolean(IS_FIRST, true);
        return isFirst;
    }
}

