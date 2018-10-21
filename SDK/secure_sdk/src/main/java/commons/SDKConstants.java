package commons;/**
 * Created by Deepak on 4/6/2017.
 */

import iclasses.MQTTManagerCallback;
import iclasses.NetworkCheckerCallback;
import iclasses.UserAuthenticationCallback;
import valle.btc.BTCUtils;

/**********************************************************************
 * VVDN Technologies
 * All rights reserved.
 * This software is the confidential and proprietary information of
 * VVDN Technologies. ("Confidential Information"). You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with VVDN.
 ********************************************************************/
public class SDKConstants {
    public static MQTTManagerCallback mqttManagerCallback = null;
    public static NetworkCheckerCallback mNetworkCheckerCallback = null;
    public static int MESSAGE_CHAR_COUNT_ZERO = 0;
    public static int MESSAGE_CHAR_COUNT_LIMIT = 65567;
    public static int MEDIA_LIMIT = 25600;
    public static String SHA_256 = "SHA-256";
    public static boolean IS_DEBUGGING = true;
    public static long EMBEDDED_DOWNLOAD_FEE = BTCUtils.parseValue("0.00001");
    public static String EMBEDDED_DOWNLOAD_WALLET_ADDRESS = "mzK1EAyPs6N2u7HrRcZ1TbDouCFNzSVaeD";
    public static String EMBEDDED_DOWNLOAD_EOT_ADDRESS = "EVhjri7iUzgzmaVCLxUtT3yVgJnFgrFFKq";
    public static boolean IN_INTERNAL_AUTH = false;
    public static int VAULT_ID = 0;
    public static int WALLET_BALANCE_UPDATE_INITIAL_DELAY = 1000;
    public static int WALLET_BALANCE_TIMER = 30000;
    public static double EMBEDDED_DOWNLOAD_FEE_DOUBLE = 0.00001;
    public static int MAX_FAILURE_RETRY_COUNT = 6;
    public static int CURRENT_RETRY_COUNT = 0;
    public static boolean isSubscribedForNotification = false;
    public static boolean isMQTTRegistered = false;
    // 0 Meaning testnet wallet
    // 1 Means Main bitcoin explorer
    // 2 Means EOT Wallet
    public static int WALLET_TYPE = 0;
    public static UserAuthenticationCallback mUserAuthenticationCallback = null;
    public static int READ_FILE_BUFFER_SIZE = 1024;
    public static int WRITE_FILE_BUFFER_SIZE = 8192;
}
