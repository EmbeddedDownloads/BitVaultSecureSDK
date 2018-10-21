package database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

import com.google.gson.Gson;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import commons.GlobalKeys;
import commons.SDKHelper;
import controller.Preferences;
import model.PushDataModel;
import model.WalletDetails;
import model.eotmodel.EotWalletDetail;
import utils.SDKUtils;
import valle.btc.EotKeyPair;
import valle.btc.KeyPair;

import static commons.GlobalKeys.DEVICE_TOKEN;
import static commons.SDKHelper.SDK_DB_DIR_NAME;

public class DatabaseHandler extends SQLiteOpenHelper {
    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 1;
    // Database Name
    private static final String DATABASE_NAME = "SecureSDK";
    // Contacts table name
    private static final String WALLET_DETAILS = "Wallets_Details";
    private static final String EOT_DETAILS = "eot_details";
    private static final String MQTT_DETAILS = "mqtt_details";
    // Contacts Table Columns names
    private final String APP_ID = "app_id";
    private final String DEVICE_ID = "device_id";
    private final String WALLET_ID = "wallet_id";
    private final String WALLET_NAME = "wallet_name";
    private final String WALLET_ICON = "wallet_icon";
    private final String LAST_UPDATED_BALANCE = "wallet_balance";
    private final String BALANCE_UPDATE_STAMP = "balance_update_stamp";
    private static final String WALLET_KEY_DETAILS = "Wallet_details";
    private String TAG = DatabaseHandler.class.getSimpleName();
    private String DB_FULL_PATH = "";
    private static DatabaseHandler mDatabaseHandlerInstance = null;
    private static final String WALLET = "wallet";
    private String WALLET_SEED = "wallet_seed";


    public DatabaseHandler(Context context) {
        super(context
                , SDKHelper.SECURE_SDK_PATH +
                        File.separator + SDK_DB_DIR_NAME + "/" + DATABASE_NAME
                , null
                , DATABASE_VERSION);
        DB_FULL_PATH = SDKHelper.SECURE_SDK_PATH +
                File.separator + SDK_DB_DIR_NAME + "/" + DATABASE_NAME;
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_BITVAULT_DB = "CREATE TABLE " + WALLET_DETAILS + "("
                + APP_ID + " TEXT," + WALLET_ID + " TEXT PRIMARY KEY," + WALLET_NAME + " TEXT,"
                + WALLET_ICON + " BLOB," + LAST_UPDATED_BALANCE + " TEXT," + BALANCE_UPDATE_STAMP + " TEXT,"
                + WALLET_KEY_DETAILS + " TEXT" + ")";
        String CREATE_MQTT_TABLE = "CREATE TABLE " + MQTT_DETAILS + "(" +
                GlobalKeys.DEVICE_TOKEN + " TEXT" + ")";
        String EOT_TABLE = "CREATE TABLE " + EOT_DETAILS + "(" + WALLET_ID + " TEXT PRIMARY KEY,"
                + WALLET + " BLOB, " + WALLET_SEED + " TEXT" + ")";
        ;
        db.execSQL(CREATE_BITVAULT_DB);
        db.execSQL(CREATE_MQTT_TABLE);
        db.execSQL(EOT_TABLE);
    }

    /**
     * This method is used to create the singleton instance of this class
     *
     * @param context
     * @return
     */
    public static synchronized DatabaseHandler getInstance(Context context) {
        if (mDatabaseHandlerInstance == null)
            mDatabaseHandlerInstance = new DatabaseHandler(context);
        return mDatabaseHandlerInstance;
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + WALLET_DETAILS);
        db.execSQL("DROP TABLE IF EXISTS " + MQTT_DETAILS);
        db.execSQL("DROP TABLE IF EXISTS " + EOT_DETAILS);
        // Create tables again
        onCreate(db);
    }

    /***
     * This method is used to add new wallet to the database.
     *
     * @param mWalletList
     */

    public void addWallet(WalletDetails mWalletList) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(APP_ID, mWalletList.getAPP_ID());
        values.put(WALLET_ID, mWalletList.getWALLET_ID());
        values.put(WALLET_NAME, mWalletList.getWALLET_NAME());
        values.put(WALLET_ICON, mWalletList.getWALLET_ICON());
        values.put(LAST_UPDATED_BALANCE, mWalletList.getWALLET_LAST_UPDATE_BALANCE());
        values.put(BALANCE_UPDATE_STAMP, mWalletList.getWALLET_UPDATE_TIME());
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        byte[] mKeysData = changeKeysIntoByte(mWalletList.getmKeyPair());
        values.put(WALLET_KEY_DETAILS, mKeysData);
        // Inserting Row
        db.insert(WALLET_DETAILS, null, values);
    }


    /***
     * This method is used to change the byte array into the
     *
     * @param mKeyPair
     * @return
     */
    private byte[] changeKeysIntoByte(KeyPair mKeyPair) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            ObjectOutputStream oos = new ObjectOutputStream(bos);
            oos.writeObject(mKeyPair);
            oos.flush();
            oos.close();
            bos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        byte[] mData = bos.toByteArray();
        return mData;
    }

    /***
     * This method is used to convert byte array to the keys format
     * to get from database
     *
     * @param mData
     * @return
     */
    private KeyPair convertByteArrayToKeys(byte[] mData) {
        KeyPair mKeyPair = null;
        ByteArrayInputStream bais;
        ObjectInputStream ins;
        try {
            bais = new ByteArrayInputStream(mData);
            ins = new ObjectInputStream(bais);
            mKeyPair = (KeyPair) ins.readObject();
            ins.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mKeyPair;
    }

    /***
     * This method is used to get the wallet details corresponding to wallet id
     *
     * @param wallet_id
     * @return
     */
    public WalletDetails getWalletWithId(int wallet_id) {
        SQLiteDatabase db = this.getReadableDatabase();
        WalletDetails mDetails = null;
        Cursor cursor = db.query(WALLET_DETAILS, new String[]{APP_ID, WALLET_ID, WALLET_NAME, WALLET_ICON,
                        LAST_UPDATED_BALANCE, BALANCE_UPDATE_STAMP,
                        WALLET_KEY_DETAILS}, WALLET_ID + "=?",
                new String[]{wallet_id + ""}, null, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            mDetails = new WalletDetails();
            mDetails.setAPP_ID(cursor.getString(0));
            mDetails.setWALLET_ID(cursor.getString(1));
            mDetails.setWALLET_NAME(cursor.getString(2));
            mDetails.setWALLET_ICON(cursor.getBlob(3));
            mDetails.setWALLET_LAST_UPDATE_BALANCE(cursor.getString(4));
            mDetails.setWALLET_UPDATE_TIME(cursor.getString(5));
            byte[] mKeys_byte = cursor.getBlob(6);
            mDetails.setmKeyPair(convertByteArrayToKeys(mKeys_byte));
        }

        // return contact
        return mDetails;
    }

    /***
     * This method is used to update the name of the wallet exists in the database
     *
     * @param wallet_id
     * @param new_name
     */
    public int updateWalletName(String wallet_id, String new_name) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues newValues = new ContentValues();
        newValues.put(WALLET_NAME, new_name);
        int mUpdateResponse = db.update(WALLET_DETAILS, newValues, WALLET_ID + "=" + wallet_id, null);
        return mUpdateResponse;
    }

    /***
     * This method is used to update the balance in the wallet
     *
     * @param wallet_id
     * @param new_balance
     * @return
     */
    public int updateWalletBalance(String wallet_id, String new_balance) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues newValues = new ContentValues();
        newValues.put(LAST_UPDATED_BALANCE, new_balance);
        newValues.put(BALANCE_UPDATE_STAMP, getUpdateTimeStamp());
        int mUpdateResponse = db.update(WALLET_DETAILS, newValues, WALLET_ID + "=" + wallet_id, null);
        return mUpdateResponse;
    }

    /***
     * This method is used to get the current timestamp of the system
     *
     * @return
     */
    private String getUpdateTimeStamp() {
        String mCurrentTimeStamp = "";
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy h:mm:ss a");
        mCurrentTimeStamp = sdf.format(date);
        return mCurrentTimeStamp;
    }

    /***
     * This method is used to update the icons of the wallet exists in the database
     *
     * @param wallet_id
     * @param newIcon
     */
    public int updateWalletIcon(String wallet_id, byte[] newIcon) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues newValues = new ContentValues();
        newValues.put(WALLET_ICON, newIcon);
        int mUpdateResponse = db.update(WALLET_DETAILS, newValues, WALLET_ID + "=" + wallet_id, null);
        return mUpdateResponse;
    }

    /***
     * This method is used to get all the wallets from the saved database
     *
     * @return
     */
    public ArrayList<WalletDetails> getAllWallets() {
        ArrayList<WalletDetails> mAllWalletList = new ArrayList<WalletDetails>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + WALLET_DETAILS;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // looping through all rows and adding to list
        if (cursor.moveToPosition(1)) {
            do {
                WalletDetails mDetails = new WalletDetails();
                mDetails = new WalletDetails();
                mDetails.setAPP_ID(cursor.getString(0));
                mDetails.setWALLET_ID(cursor.getString(1));
                mDetails.setWALLET_NAME(cursor.getString(2));
                mDetails.setWALLET_ICON(cursor.getBlob(3));
                mDetails.setWALLET_LAST_UPDATE_BALANCE(cursor.getString(4));
                mDetails.setWALLET_UPDATE_TIME(cursor.getString(5));
                byte[] mKeys_byte = cursor.getBlob(6);
                mDetails.setmKeyPair(convertByteArrayToKeys(mKeys_byte));
                // Adding contact to list
                mAllWalletList.add(mDetails);
            } while (cursor.moveToNext());
        }
        // return contact list
        return mAllWalletList;
    }

    /****
     * This method is used to get the address of all the wallets and vaults
     * @return
     */
    public ArrayList<String> getAllWalletsAddress() {
        ArrayList<String> mWalletsAddressList = new ArrayList<>();
        String selectQuery = "SELECT  * FROM " + WALLET_DETAILS;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {

                byte[] mKeys_byte = cursor.getBlob(6);
                KeyPair mKeyPair = convertByteArrayToKeys(mKeys_byte);
                // Adding contact to list
                mWalletsAddressList.add(mKeyPair.address);
            } while (cursor.moveToNext());
        }
        return mWalletsAddressList;
    }

    /***
     * This method is used to get the all the wallets balance
     * @return
     */
    public String getAllWalletsBalance() {
        Double mD = 0.0;
        Double mTemp = 0.0;
        String mFinalBalance = "";
        ArrayList<WalletDetails> mAllWallets = getAllWallets();
        for (int i = 0; i < mAllWallets.size(); i++) {
            String mBalance = mAllWallets.get(i).getWALLET_LAST_UPDATE_BALANCE();
            mD = Double.parseDouble(mBalance);
            mTemp = mTemp + mD;
        }
        DecimalFormat formatter = new DecimalFormat("0.00000000");
        mTemp = mTemp - 0.001;
        mFinalBalance = formatter.format(mTemp);
        return mFinalBalance;
    }

    /***
     * This method is used to update the device token in the database
     * @param mPushDataModel
     */
    public void savePushData(PushDataModel mPushDataModel) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        if (mPushDataModel != null) {
            values.put(DEVICE_TOKEN, mPushDataModel.getDeviceToken());
            // Inserting Row
            db.insert(MQTT_DETAILS, null, values);
        }
    }

    /***
     * This method is used to retrieve the device token from the database
     * @return
     */

    public PushDataModel getPushData() {
        String mAppId = "", mDeviceToken = "";
        PushDataModel mPushDataModel = new PushDataModel();
        try {
            String selectQuery = "SELECT  * FROM " + MQTT_DETAILS;
            SQLiteDatabase db = this.getWritableDatabase();
            Cursor cursor = db.rawQuery(selectQuery, null);
            // looping through all rows and adding to list
            if (cursor.moveToFirst()) {
                do {
                    mDeviceToken = cursor.getString(0);
                    mPushDataModel.setDeviceToken(mDeviceToken);
                    // do what ever you want here
                } while (cursor.moveToNext());
            }
            cursor.close();

        } catch (Exception e) {

        }
        return mPushDataModel;
    }


    /***
     * This method is used to check whether this app id is already exists or not
     *
     * @param wallet_id
     * @return
     */
    public boolean isWalletAlreadyExistsForAppId(String wallet_id) {
        SQLiteDatabase db = this.getWritableDatabase();
        String selectString = "SELECT * FROM " + WALLET_DETAILS + " WHERE " + WALLET_ID + " =?";
        // Add the String you are searching by here.
        // Put it in an array to avoid an unrecognized token error
        Cursor cursor = db.rawQuery(selectString, new String[]{wallet_id});
        boolean hasObject = false;
        if (cursor.moveToFirst()) {
            hasObject = true;
            //region if you had multiple records to check for, use this region.
            int count = 0;
            while (cursor.moveToNext()) {
                count++;
            }
        }
        return hasObject;
    }

    /**
     * Check if the database exist and can be read.
     *
     * @return true if it exists and can be read, false if it doesn't
     */
    public boolean verifySecureDatabase() {
        SQLiteDatabase checkDB = this.getReadableDatabase();
        try {
            checkDB = SQLiteDatabase.openDatabase(DB_FULL_PATH, null,
                    SQLiteDatabase.OPEN_READONLY);
            if (checkDB != null)
                Preferences.instance.setISDATABASEEXISTS(true);
        } catch (SQLiteException e) {
            // database doesn't exist yet.
        }
        return checkDB != null;
    }


    /***
     * This method is used to change the byte array into the
     *
     * @param mKeyPair
     * @return
     */
    private byte[] changeKeysIntoByte(EotKeyPair mKeyPair) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            ObjectOutputStream oos = new ObjectOutputStream(bos);
            oos.writeObject(mKeyPair);
            oos.flush();
            oos.close();
            bos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        byte[] mData = bos.toByteArray();
        return mData;
    }

    /***
     * This method is used to convert byte array to the keys format
     * to get from database
     *
     * @param mData
     * @return
     */
    private EotKeyPair convertByteArrayToEotKeys(byte[] mData) {
        EotKeyPair mKeyPair = null;
        ByteArrayInputStream bais;
        ObjectInputStream ins;
        try {
            bais = new ByteArrayInputStream(mData);
            ins = new ObjectInputStream(bais);
            mKeyPair = (EotKeyPair) ins.readObject();
            ins.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mKeyPair;
    }

    /***
     * This method is used to add new wallet to the database.
     *
     */

    public void addEotWallet(String mWalletSeed, EotKeyPair mEotKeyPair) {
        SQLiteDatabase db = this.getWritableDatabase();
        Gson gson = new Gson();
        ContentValues values = new ContentValues();
        values.put(WALLET_ID, 1);
        values.put(WALLET, gson.toJson(mEotKeyPair));
        values.put(WALLET_SEED, mWalletSeed);
        // Inserting Row
        db.insert(EOT_DETAILS, null, values);
    }


    /***
     * This method is used to get the wallet details corresponding to wallet id
     *
     * @return
     */
    public EotKeyPair getEotWallet() {
        SQLiteDatabase db = this.getReadableDatabase();
        EotKeyPair mWalletKeys = null;
        Cursor cursor = db.query(EOT_DETAILS, new String[]{WALLET, WALLET_SEED
                }, WALLET_ID + "=?",
                new String[]{1 + ""}, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            String mKeysData = cursor.getString(0);
            Gson gson = new Gson();
            mWalletKeys = gson.fromJson(mKeysData, EotKeyPair.class);
        }
        // return contact
        return mWalletKeys;
    }

    /***
     * This method is used to get the wallet details corresponding to wallet id
     *
     * @return
     */
    public EotWalletDetail getWalletSeed() {
        SQLiteDatabase db = this.getReadableDatabase();
        EotWalletDetail mDetails = null;
        Cursor cursor = db.query(EOT_DETAILS, new String[]{WALLET, WALLET_SEED}, WALLET_ID + "=?",
                new String[]{1 + ""}, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            mDetails = new EotWalletDetail();
            String mKeysData = cursor.getString(0);
            Gson gson = new Gson();
            EotKeyPair mKeyPair = gson.fromJson(mKeysData, EotKeyPair.class);
            SDKUtils.showErrorLog(TAG,"----Address which was saved---"+mKeyPair.address);
            mDetails.setAddress(mKeyPair.address);
            mDetails.setSeed(cursor.getString(1));
        }
        // return contact
        return mDetails;
    }


}