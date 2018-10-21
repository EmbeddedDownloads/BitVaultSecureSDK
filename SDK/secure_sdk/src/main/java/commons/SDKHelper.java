package commons;

import android.os.Environment;

import datamover.DownloadData;
import datamover.UploadData;
import messagemanager.FeeDescriptor;
import securityencryption.EncryptDecryptData;
import utils.SDKUtils;

/**********************************************************************
 * VVDN Technologies
 * All rights reserved.
 * This software is the confidential and proprietary information of
 * VVDN Technologies. ("Confidential Information"). You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with VVDN.
 ********************************************************************/
public interface SDKHelper {

    String HTTPS_EXT = "http://";
    String EOT_DOMAIN = HTTPS_EXT + "178.62.30.50:3622";
    String EOT_EXPLORER = EOT_DOMAIN + "/api/addr/";

    // Main Blockchain url
    String EOT_TRAN = "178.62.30.50:3622/api/txs?address=";
    String EOT_TRANSACTION_HISTORY_URL = HTTPS_EXT + EOT_TRAN;
    String EOT_TRANSACTION_PAGE = "&pageNum=0";

    //EOT explorer url's
    String EOT_TX_BROADCAST_URL = "http://178.62.30.50:3622/api/tx/send";


    String TAG_MESSAGE_FEE = FeeDescriptor.class.getSimpleName();
    String EOT_TO_USD = "https://api.coinmarketcap.com/v1/ticker/eot-token/";
    String KEY_DATA = "data";
    /**
     * This tag variable is used to keep the track of the wallet api web service call
     */
    String TAG_WALLET_UNSPENT_COUNT = "wallet_unspent_count";
    String TAG_FETCH_WALLET_BALANCE = "wallet_balance_callbck";
    String TAG_CURRENCY_CONVERTOR = "currency_convertor";
    String TAG_WALLET_ADDRESS_PROPERTIES = "wallet_address_properties";
    String TAG_TRANSACTION_HISTORY_HANDLER = "transactions_history_handler";
    String TAG_SEND_TRANS = "send_trans";
    /**
     * This variable tracks the timeout call of the web api calling
     */
    int WEBCALL_TIMEOUT = 1000 * 25;
    /**
     * This variable is used to track the retries count to the web service api
     */
    int RETRY_COUNT = 1;
    String DB_NAME = "securesdk.db";
    String KEY_TX_ID = "txid";
    String KEY_SATOSHIS = "satoshis";
    String KEY_ADDRESS = "address";
    String KEY_CONFIRMATIONS = "confirmations";
    String KEY_VOUT = "vout";
    String WALLET_NAME = "Wallet";
    String VAULT_NAME = "Vault";
    String USD_TO_BTC = "https://blockchain.info/tobtc?currency=USD&value=1";
    String INSIGHT_SERVER_DOMAIN_NAME = "146.148.123.155";
    String INSIGHT_API_EXT = "/insight-api/";
    String INSIGHT_API_ADDRESS = "addr/";
    String INSIGHT_TO = "/utxo";
    String TAG_USD_TO_BTC = "usdtobtc";
    String TAG_BTC_TO_USD = "btctousd";
    int ZERO = 0;
    int ONE = 1;
    int FIVE = 6;
    String ZERO_ZERO = "0.0";

    String URL_NODE1 = "http://35.161.247.187:8080/PrivateBlockChain/apis/sendMessage";
    String URL_NODE2 = "http://34.212.202.45:8080/PrivateBlockChain/apis/sendMessage";
    String URL_NODE3 = "http://52.37.159.5:8080/PrivateBlockChain/apis/sendMessage";

    String URL_RECEIVE_DATA_NOTIFICATION_NODE1 = "http://35.161.247.187:8080/PrivateBlockChain/apis/block/getMessage";
    String URL_RECEIVE_DATA_NOTIFICATION_NODE2 = "http://34.212.202.45:8080/PrivateBlockChain/apis/block/getMessage";
    String URL_RECEIVE_DATA_NOTIFICATION_NODE3 = "http://52.37.159.5:8080/PrivateBlockChain/apis/block/getMessage";


    String URL_RECEIVE_DATA_PULL_NODE1 = "http://35.161.247.187:8080/PrivateBlockChain/apis/block/getBlocks";
    String URL_RECEIVE_DATA_PULL_NODE2 = "http://34.212.202.45:8080/PrivateBlockChain/apis/block/getBlocks";
    String URL_RECEIVE_DATA_PULL_NODE3 = "http://52.37.159.5:8080/PrivateBlockChain/apis/block/getBlocks";

    String URL_GET_FILE_NODE1 = "http://35.161.247.187:8080/PrivateBlockChain/apis/block/getFile?fileId=";
    String URL_GET_FILE_NODE2 = "http://34.212.202.45:8080/PrivateBlockChain/apis/block/getFile?fileId=";
    String URL_GET_FILE_NODE3 = "http://52.37.159.5:8080/PrivateBlockChain/apis/block/getFile?fileId=";

    String URL_RECEIVE_ACK_1 = "http://35.161.247.187:8080/PrivateBlockChain/apis/block/acknowledge/";
    String URL_RECEIVE_ACK_2 = "http://34.212.202.45:8080/PrivateBlockChain/apis/block/acknowledge/";
    String URL_RECEIVE_ACK_3 = "http://52.37.159.5:8080/PrivateBlockChain/apis/block/acknowledge/";
    String MQTT_TOPIC_TO_SUBSCRIBE = SDKUtils.getDeviceKey();
    String MQTT_DEVICE_REG = "bitvault/device-register";
    String MQTT_APPLICATION_REG = "bitvault/application-register";
    String DEVICE_ID = "device_id";
    String WALLET_ADDR = "wallet_addr";
    String TEST_NET_URL = "testnet.";
    String MAIN_BLOCK_CHAIN = "";
    // Test net get balance API
    String TESTNET_URL_BLOCK_CHAIN_EXPLORER = "https://" + TEST_NET_URL + "blockexplorer.com/api/addr/";
    String TESTNET_URL_BIT_PAY_EXPLORER = "https://" + TEST_NET_URL + "blockexplorer.com/api/addr/";
    String TESTNET_URL_BIT_PAY_EXPLORERS = "https://" + TEST_NET_URL + "blockexplorer.com/api/addrs/";
    String TESTNET_URL_TRANSACTION_HISTORY = "https://" + TEST_NET_URL + "blockexplorer.com/api/addrs/";
    String TESTNET_URL_PUSH_TRANSACTION_URL = "https://" + TEST_NET_URL + "blockexplorer.com/api/tx/send";

    // Main Blockchain url
    String MAIN_BLOCK_CHAIN_EXPLORER = "https://" + "blockexplorer.com/api/addr/";
    String MAIN_BIT_PAY_EXPLORER = "https://" + "blockexplorer.com/api/addr/";
    String MAIN_BIT_PAY_EXPLORERS = "https://" + "blockexplorer.com/api/addrs/";
    String MAIN_TRANSACTION_HISTORY = "https://" + "blockexplorer.com/api/addrs/";
    String MAIN_PUSH_TRANSACTION_URL = "https://" + "blockexplorer.com/api/tx/send";


    String LOW = "low";
    String MEDIUM = "medium";
    String HIGH = "high";
    String OK = "ok";
    String GOT_MESSAGE = "Get message fee successfully";
    String APPID = "appId";
    String CLOUD_URL = "cloud/sdk/api/fee";
    String TX_HISTORY_NO_OF_PAGES = "/txs?from=0&to=50";
    String TRANSACTION_ID = "transactionId";
    String CRC = "crc";
    String DATA_TAG = "tag";
    String TAG_SECURE_MESSAGING = "secure messaging";
    String TAG_RECEIVER = "receiverAddress";
    String TAG_PBC_ID = "pbcId";
    String TAG_TIMESTAMP = "timestamp";
    String TAG_SESSION_KEY = "sessionKey";
    String TAG_FILE = "file";
    String TAG_STATUS = "status";
    String TAG_MESSAGE = "message";
    String KEY_BUNDLE_DATA="bundle_data";
    String TAG_CAP_OK = "OK";
    String RAW_TX = "rawtx";
    int TEST_NET_ADDRESS = 0x6F;
    int TEST_NET_PRIVATE_KEY = 0xEF;
    int MAIN_PRIVATE_KEY = 0x80;
    int MAIN_ADDRESS = 0;
    int EOT_ADDRESS = 0x21;
    String FILE_NAME = "SecureMessage.txt";
    String UNABLE_PROCESS_FEE = "Sorry unable to get message fee";
    String SEND_MESSAGE_VOLLEY_ERROR = "Sorry unable to send Message";
    String TAG_CAP_FAILED = "Failed";
    int SESSION_TIMER = 900000; // 15 minutes
    //    int SESSION_TIMER = 10000; // 2o second
    int SESSION_SECONDS = 1000;
    int DUMMY_TIMER = 10000;
    String TAG_AMOUNT = "amount";
    String TAG_DOWNLOAD_DATA = DownloadData.class.getSimpleName();
    String RECEIVE_MESSAGE = "Receive message successfully";
    String SEND_MESSAGE_SUCCESS = "Send message successfully";
    String RECEIVE_NO_MESSAGE = "No New Message Available";
    String TAG_SENDER = "senderAddress";
    String BLOCK_DELETE_SUCCESS = "Block data deleted successfully.";
    String TAG_SUCCESS = "success";
    String SECURE_SDK_PATH = Environment.getExternalStorageDirectory() + "/" + "BitVault";
    String SDK_DB_DIR_NAME = "SecureSDK";

    String FILE_NAME_LOG = "pbcLogs.txt";

    String PREFERENCE_RECEIVE_NAME = "receiveSession";
    String PREFERENCE_RECEIVE_KEY = "receive_process";
    String PREFERENCES_CHECK = "receiveInProcess";

    String ENCRYPTION_START = "EncryptionStart";
    String ENCRYPTION_END = "EncryptionEnd";
    String DECRYPTION_START = "DecryptionStart";
    String DECRYPTION_END = "DecryptionEnd";
    String MQTT_CALLBACK = "mqtt_callback";
    String PUSH_MESSAGES_REG = "tcp://34.209.234.181:1883";

    String TAG_WEB_SERVER_KEY = "webServerKey";
    String WEB_SERVER_VALUE = "4370ca92-0004-421c-9195-6v07cf2ddgcf";
    String NOTIFICATION_BROADCAST_KEY = "com.embedded.download.intent.action.Notification";

    String TAG_NO_MESSAGE = "No Message";
    String NO_NEWMESSAGE_AVAILABLE = "No New Message Available to receive";
    String TAG_ENCRYPTION = EncryptDecryptData.class.getSimpleName();
    String TAG_UPLOAD_DATA = UploadData.class.getSimpleName();
    String RECEIVED_FILE_NAME = "ReceivedSecureMessage.txt";

    String DESKTOP_TO_DESKTOP = "Desktop to Desktop";
    String BITVAULT_TO_DESKTOP = "BItVault to Desktop";
    String BITVAULT_TO_BITVAULT = "BItVault to BItVault";
    String MEDIA_VAULT = "mediaVault";

    // for media vault constant

    String BASE_URL_NODE1 = "http://54.202.214.48:8080/PrivateBlockChain/apis";
    String BASE_URL_NODE2 = "http://54.149.134.186:8080/PrivateBlockChain/apis";
    String BASE_URL_NODE3 = "http://54.187.58.177:8080/PrivateBlockChain/apis";

    String RECEIVER_ADDRESS = "mgJS12VDsDFjTPQrALG4ec38ajExZCv4mC";
    String MEDIA_VAULT_TAG = "media_vault";
    String MEDIA_VAULT_FILE_PATH = "/home/vvdn/Downloads/mediavault/encrypted/";
    String MEDIA_VAULT_FILE_ID = "id";
    String MEDIA_VAULT_WALLET_ADDRESS = "walletAddress";
    String MEDIA_VAULT_FILE_NAME = "mediavault.txt";
    String SEND_MEDIA_FILE_SUCCESS = "File Upload successfully";
    // PBC NODE ADDRESS
    //For NODE1
    String UPLOAD_MEDIA_NODE1 = BASE_URL_NODE1 + "/uploadMedia";
    String RECEIVE_MEDIA_NODE1 = BASE_URL_NODE1 + "/block/getMedia";
    String DELETE_MEDIA_NODE1 = BASE_URL_NODE1 + "/block/deleteMedia";
    String MEDIA_STATUS_NODE1 = BASE_URL_NODE1 + "/block/mediaStatus";
    String GET_MEDIA_FILE_NODE1 = BASE_URL_NODE1 + "/block/getFile?fileId=";

    //For NODE2
    String UPLOAD_MEDIA_NODE2 = BASE_URL_NODE2 + "/uploadMedia";
    String RECEIVE_MEDIA_NODE2 = BASE_URL_NODE2 + "/block/getMedia";
    String DELETE_MEDIA_NODE2 = BASE_URL_NODE2 + "/block/deleteMedia";
    String MEDIA_STATUS_NODE2 = BASE_URL_NODE2 + "/block/mediaStatus";
    String GET_MEDIA_FILE_NODE2 = BASE_URL_NODE2 + "/block/getFile?fileId=";

    //For NODE3
    String UPLOAD_MEDIA_NODE3 = BASE_URL_NODE3 + "/uploadMedia";
    String RECEIVE_MEDIA_NODE3 = BASE_URL_NODE3 + "/block/getMedia";
    String DELETE_MEDIA_NODE3 = BASE_URL_NODE3 + "/block/deleteMedia";
    String MEDIA_STATUS_NODE3 = BASE_URL_NODE3 + "/block/mediaStatus";
    String GET_MEDIA_FILE_NODE3 = BASE_URL_NODE3 + "/block/getFile?fileId=";
    String PUB_KEY = "walletPublicKey";
    String MEDIAVAULT_SIGNNATURE = "signature";
    String SECURE_MESSAGE_PKG = "com.app.securemessaging";
    String MEDIA_VAULT_PKG = "com.bitvault.mediavault";
    String BASE_URL_APPSTORE = "http://52.10.154.132:8080/cloud/rest/api/v1/mobile/mediavault";
    String DELETE_URL_APPSTORE = "http://52.10.154.132:8080/cloud/rest/api/v1/mobile/mediavault/delete";
    String MEDIADVAULT_ENCFILE_KEY = "encFileKey";
    String MEDIADVAULT_ENCTXNID = "encTxnID";
    String MEDIADVAULT_FILEANME = "filename";
    String MEDIADVAULT_TYPE = "type";
    String MEDIADVAULT_WALLET_ADDRESSES = "walletAddresses=";
    String MEDIAVAULT_GETFILE_SUCCESS = "File deatails fetched successfully";
    String RECEIVED_MEDIA_FILE_NAME = "ReceivedMediaVault.txt";
    String TAG_RESULT_SET = "resultSet";
    String TAG_RESULT = "result";
    String FILE_PATH = "/home/vvdn/Downloads/encrypted/";
}
