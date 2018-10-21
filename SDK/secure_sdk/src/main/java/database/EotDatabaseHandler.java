//package database;
//
//import android.content.ContentValues;
//import android.content.Context;
//import android.database.Cursor;
//import android.database.sqlite.SQLiteDatabase;
//import android.database.sqlite.SQLiteException;
//import android.database.sqlite.SQLiteOpenHelper;
//
//import java.io.ByteArrayInputStream;
//import java.io.ByteArrayOutputStream;
//import java.io.File;
//import java.io.ObjectInputStream;
//import java.io.ObjectOutputStream;
//
//import commons.GlobalKeys;
//import commons.SDKHelper;
//import controller.Preferences;
//import model.PushDataModel;
//import model.eotmodel.EotWalletDetail;
//import utils.SDKUtils;
//import valle.btc.EotKeyPair;
//
//import static commons.GlobalKeys.DEVICE_TOKEN;
//import static commons.SDKHelper.SDK_DB_DIR_NAME;
//import static commons.SDKHelper.WALLET_NAME;
//
//public class EotDatabaseHandler extends SQLiteOpenHelper {
//    // All Static variables
//    // Database Version
//    private static final int DATABASE_VERSION = 1;
//    // Database Name
//    private static final String DATABASE_NAME = "SecureSDK";
//    // Contacts table name
//    private static final String WALLET_DETAILS = "EotWallet_Details";
//    private static final String MQTT_DETAILS = "eot_mqtt_details";
//    // Contacts Table Columns names
//    private static final String WALLET_ADDRESS = "";
//    private static final String WALLET = "wallet";
//    private static final String PUBLIC_KEY = "";
//    private static final byte[] WALLET_PUBLIC_KEY = null;
//    private static final String WALLET_KEY_DETAILS = "Wallet_details";
//    private String TAG = EotDatabaseHandler.class.getSimpleName();
//    private String DB_FULL_PATH = "";
//    private static EotDatabaseHandler mDatabaseHandlerInstance = null;
//    private String WALLET_ID = "wallet_id";
//    private String WALLET_SEED = "wallet_seed";
//
//
//    public EotDatabaseHandler(Context context) {
//        super(context
//                , SDKHelper.SECURE_SDK_PATH +
//                        File.separator + SDK_DB_DIR_NAME + "/" + DATABASE_NAME
//                , null
//                , DATABASE_VERSION);
//        DB_FULL_PATH = SDKHelper.SECURE_SDK_PATH +
//                File.separator + SDK_DB_DIR_NAME + "/" + DATABASE_NAME;
//        SDKUtils.showLog(TAG, "-----Database Created-----");
//    }
//
//    // Creating Tables
//    @Override
//    public void onCreate(SQLiteDatabase db) {
//        SDKUtils.showLog(TAG, "-----onCreate(SQLiteDatabase db)-----");
//        String CREATE_CONTACTS_TABLE = "CREATE TABLE " + WALLET_DETAILS + "(" + WALLET_ID + " TEXT PRIMARY KEY,"
//                + WALLET + " BLOB, " + WALLET_SEED + " TEXT" + ")";
//        String CREATE_MQTT_TABLE = "CREATE TABLE " + MQTT_DETAILS + "(" +
//                GlobalKeys.DEVICE_TOKEN + " TEXT" + ")";
//        ;
//        db.execSQL(CREATE_CONTACTS_TABLE);
//        db.execSQL(CREATE_MQTT_TABLE);
//    }
//
//    /**
//     * This method is used to create the singleton instance of this class
//     *
//     * @param context
//     * @return
//     */
//    public static synchronized EotDatabaseHandler getInstance(Context context) {
//        if (mDatabaseHandlerInstance == null)
//            mDatabaseHandlerInstance = new EotDatabaseHandler(context);
//        return mDatabaseHandlerInstance;
//    }
//
//    // Upgrading database
//    @Override
//    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
//        // Drop older table if existed
//        db.execSQL("DROP TABLE IF EXISTS " + WALLET_DETAILS);
//        db.execSQL("DROP TABLE IF EXISTS " + MQTT_DETAILS);
//        // Create tables again
//        onCreate(db);
//    }
//
//    /***
//     * This method is used to change the byte array into the
//     *
//     * @param mKeyPair
//     * @return
//     */
//    private byte[] changeKeysIntoByte(EotKeyPair mKeyPair) {
//        ByteArrayOutputStream bos = new ByteArrayOutputStream();
//        try {
//            ObjectOutputStream oos = new ObjectOutputStream(bos);
//            oos.writeObject(mKeyPair);
//            oos.flush();
//            oos.close();
//            bos.close();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        byte[] mData = bos.toByteArray();
//        return mData;
//    }
//
//    /***
//     * This method is used to convert byte array to the keys format
//     * to get from database
//     *
//     * @param mData
//     * @return
//     */
//    private EotKeyPair convertByteArrayToKeys(byte[] mData) {
//        EotKeyPair mKeyPair = null;
//        ByteArrayInputStream bais;
//        ObjectInputStream ins;
//        try {
//            bais = new ByteArrayInputStream(mData);
//            ins = new ObjectInputStream(bais);
//            mKeyPair = (EotKeyPair) ins.readObject();
//            ins.close();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return mKeyPair;
//    }
//
//    /***
//     * This method is used to add new wallet to the database.
//     *
//     */
//
//    public void addEotWallet(String mWalletSeed, EotKeyPair mEotKeyPair) {
//        SQLiteDatabase db = this.getWritableDatabase();
//        ContentValues values = new ContentValues();
//        values.put(WALLET_ID, 1);
//        byte[] mKeyPair = changeKeysIntoByte(mEotKeyPair);
//        values.put(WALLET, mKeyPair);
//        values.put(WALLET_SEED,mWalletSeed);
//        // Inserting Row
//        db.insert(WALLET_DETAILS, null, values);
//    }
//
//
//    /***
//     * This method is used to get the wallet details corresponding to wallet id
//     *
//     * @return
//     */
//    public EotKeyPair getEotWallet() {
//        SQLiteDatabase db = this.getReadableDatabase();
//        EotKeyPair mWalletKeys = null;
//        Cursor cursor = db.query(WALLET_DETAILS, new String[]{WALLET, WALLET_SEED
//                }, WALLET_ID + "=?",
//                new String[]{1 + ""}, null, null, null);
//        if (cursor != null && cursor.moveToFirst()) {
//            mWalletKeys = convertByteArrayToKeys(cursor.getBlob(1));
//        }
//        // return contact
//        return mWalletKeys;
//    }
//
//    /***
//     * This method is used to get the wallet details corresponding to wallet id
//     *
//     * @return
//     */
//    public EotWalletDetail getWalletSeed() {
//        SQLiteDatabase db = this.getReadableDatabase();
//        EotWalletDetail mDetails = null;
//        Cursor cursor = db.query(WALLET_DETAILS, new String[]{WALLET, WALLET_SEED}, WALLET_ID + "=?",
//                new String[]{1 + ""}, null, null, null);
//        if (cursor != null && cursor.moveToFirst()) {
//            mDetails = new EotWalletDetail();
//            mDetails.setAddress(cursor.getString(1));
//            mDetails.setSeed(cursor.getString(2));
//        }
//        // return contact
//        return mDetails;
//    }
//
//
//    /***
//     * This method is used to update the name of the wallet exists in the database
//     *
//     * @param new_name
//     */
//    private int updateWalletName(String new_name) {
//        SQLiteDatabase db = this.getWritableDatabase();
//        ContentValues newValues = new ContentValues();
//        newValues.put(WALLET_NAME, new_name);
//        int mUpdateResponse = db.update(WALLET_DETAILS, newValues, WALLET_ID + "=" + 1, null);
//        return mUpdateResponse;
//    }
//
//
//    /***
//     * This method is used to update the device token in the database
//     * @param mPushDataModel
//     */
//    private void savePushData(PushDataModel mPushDataModel) {
//        SQLiteDatabase db = this.getWritableDatabase();
//        ContentValues values = new ContentValues();
//        if (mPushDataModel != null) {
//            values.put(DEVICE_TOKEN, mPushDataModel.getDeviceToken());
//            // Inserting Row
//            db.insert(MQTT_DETAILS, null, values);
//        }
//    }
//
//    /***
//     * This method is used to retrieve the device token from the database
//     * @return
//     */
//
//    private PushDataModel getPushData() {
//        String mAppId = "", mDeviceToken = "";
//        PushDataModel mPushDataModel = new PushDataModel();
//        try {
//            String selectQuery = "SELECT  * FROM " + MQTT_DETAILS;
//            SQLiteDatabase db = this.getWritableDatabase();
//            Cursor cursor = db.rawQuery(selectQuery, null);
//            // looping through all rows and adding to list
//            if (cursor.moveToFirst()) {
//                do {
//                    mDeviceToken = cursor.getString(0);
//                    mPushDataModel.setDeviceToken(mDeviceToken);
//                    // do what ever you want here
//                } while (cursor.moveToNext());
//            }
//            cursor.close();
//
//        } catch (Exception e) {
//
//        }
//        return mPushDataModel;
//    }
//
//
//    /***
//     * This method is used to check whether this app id is already exists or not
//     *
//     * @param wallet_id
//     * @return
//     */
//    private boolean isWalletAlreadyExistsForAppId(String wallet_id) {
//        SQLiteDatabase db = this.getWritableDatabase();
//        String selectString = "SELECT * FROM " + WALLET_DETAILS + " WHERE " + WALLET_ID + " =?";
//        // Add the String you are searching by here.
//        // Put it in an array to avoid an unrecognized token error
//        Cursor cursor = db.rawQuery(selectString, new String[]{wallet_id});
//        boolean hasObject = false;
//        if (cursor.moveToFirst()) {
//            hasObject = true;
//            //region if you had multiple records to check for, use this region.
//            int count = 0;
//            while (cursor.moveToNext()) {
//                count++;
//            }
//        }
//        return hasObject;
//    }
//
//    /**
//     * Check if the database exist and can be read.
//     *
//     * @return true if it exists and can be read, false if it doesn't
//     */
//    public boolean verifySecureDatabase() {
//        SQLiteDatabase checkDB = this.getReadableDatabase();
//        try {
//            checkDB = SQLiteDatabase.openDatabase(DB_FULL_PATH, null,
//                    SQLiteDatabase.OPEN_READONLY);
//            if (checkDB != null)
//                Preferences.instance.setISDATABASEEXISTS(true);
//        } catch (SQLiteException e) {
//            // database doesn't exist yet.
//        }
//        return checkDB != null;
//    }
//
//
//}