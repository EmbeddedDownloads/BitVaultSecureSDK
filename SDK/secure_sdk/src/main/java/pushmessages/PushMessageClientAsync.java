package pushmessages;
/*******************************************************************************
 * Copyright (c) 2009, 2014 IBM Corp.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v1.0 which accompany this distribution.
 *
 * The Eclipse Public License is available at
 *    http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 *   http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *    Dave Locke - initial API and implementation and/or initial documentation
 */

//package org.eclipse.paho.sample.mqttv3app;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttAsyncClient;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Timestamp;
import java.util.ArrayList;

import commons.GlobalKeys;
import commons.SDKConstants;
import commons.SDKHelper;
import controller.Preferences;
import database.DatabaseHandler;
import iclasses.MQTTManagerCallback;
import model.PushDataModel;
import utils.NetworkUtil;
import utils.SDKUtils;


public class PushMessageClientAsync implements MqttCallback {

    private DatabaseHandler mDatabaseHandler = null;
    // Default settings:
    private String deviceToken = null;
    private boolean registrationStatus;
    private boolean quietMode = false;
    private int qos = 1;
    private String clientId = "HelloBitVault";
    private String subRespTopic = deviceToken;
    private String password = null;
    private String userName = null;
    private String message = null;
    private MqttConnectOptions conOpt;
    private static MqttAsyncClient client;
    private Context mContext = null;
    private MQTTManagerCallback mMqttManagerCallback = null;
    private static PushMessageClientAsync pushMessageClientAsyncInstance = null;
    private String TAG = PushMessageClientAsync.class.getSimpleName();
    private boolean isApplicationRegistered = false;
    private static PushDataModel mPushDataModel = null;

    /**
     * Constructor
     */
    public PushMessageClientAsync(Context mContext) {
        this.mContext = mContext;
        if (mContext != null)
            mDatabaseHandler = new DatabaseHandler(mContext);
        else
            SDKUtils.showErrorLog(TAG, "---Context is null---");
        deviceToken = SDKUtils.getDeviceKey();
        saveAppKeyForApplications();
        mPushDataModel = new PushDataModel();
        getClientId();
        connectToPushServer(SDKHelper.PUSH_MESSAGES_REG, clientId, null, null);
//        initializeNetworkVerifier();
    }

    /***
     * This method is used to save the app key of the client application
     */
    private void saveAppKeyForApplications() {
        String mAppKey = SDKUtils.getApplicationKey();
        if (Preferences.instance.getIsFirstLaunch()) {
            Preferences.instance.isFirstLaunch(false);
            Preferences.instance.setAppKey(mAppKey);
        }
    }

    /***
     * This method is used to get the instance of this class to access the methods
     * @return
     */
    public static PushMessageClientAsync getPushMessageClientAsyncInstance(Context mContext) {
        if (pushMessageClientAsyncInstance == null)
            pushMessageClientAsyncInstance = new PushMessageClientAsync(mContext);
        return pushMessageClientAsyncInstance;
    }


    /***
     * This method is used to create the client id of the mqtt device
     */
    public String getClientId() {
        if (Preferences.instance.getClientId().isEmpty()) {
            clientId = MqttClient.generateClientId();
            Preferences.instance.saveClientId(clientId);
        } else {
            clientId = Preferences.instance.getClientId();
        }
        return clientId;
    }

    /***
     * This method is used to connect the user with the MQTT server
     */
    private void tryToConnectWithMQTTServer() {
        if (NetworkUtil.getConnectivityStatus(mContext)) {
            if (!SDKConstants.isMQTTRegistered) {
                connectToPushServer(SDKHelper.PUSH_MESSAGES_REG, clientId, null, null);
                SDKConstants.isMQTTRegistered = true;
            }
        }
    }

    /**
     * check device registration
     *
     * @return success/failure
     */
    public void connectToPushServer(String registration_url, String client_id,
                                    String user_name, String pwd) {
        subRespTopic = deviceToken;
        try {
            // Initialize the client
            conOpt = new MqttConnectOptions();
            conOpt.setCleanSession(false);
            initClientAsync(registration_url, client_id, user_name, pwd);
            // connect client
            doConnect();
        } catch (MqttException me) {
            // Display full details of any exception that occurs
            me.printStackTrace();
        } catch (Throwable th) {
            th.printStackTrace();
        }
    }

    /**
     * Initializes the client
     *
     * @throws MqttException
     */
    public void initClientAsync(String mConnectionUrl, String clId, String mUserName,
                                String mUserPwd) throws MqttException {
        // set clean session
        conOpt.setCleanSession(false);
        // Construct the object that contains connection parameters
        if (password != null) {
            conOpt.setPassword(this.password.toCharArray());
        }
        if (userName != null) {
            conOpt.setUserName(this.userName);
        }
        initializeClient(mConnectionUrl, clId);
    }

    private void initializeClient(String mConnectionUrl, String clId) {
        try {
            // Construct the MqttClient instance
            client = new MqttAsyncClient(mConnectionUrl, clId, null);
            // Set this wrapper as the callback handler
            client.setCallback(this);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }


    /**
     * disconnect mqtt client
     */
    public void disconnectClient() {
        Disconnector disc = new Disconnector();
        disc.doDisconnect();
    }

    /***
     * This method is used to register all the wallets to the mqtt server
     */
    private void registerDevice() {
        JSONObject mJsonObject = new JSONObject();
        JSONArray mJsonArray = new JSONArray();
        String mTemp = "";
        try {
            mJsonObject.put(SDKHelper.DEVICE_ID, SDKUtils.getDeviceKey());
            ArrayList<String> mWallletsAddress = new DatabaseHandler(mContext).getAllWalletsAddress();
            if (mWallletsAddress != null && !mWallletsAddress.isEmpty()) {
                int sizeOfWallets = mWallletsAddress.size();
                for (int i = 0; i < sizeOfWallets; i++) {
                    mJsonArray.put(mWallletsAddress.get(i));
                }
                mJsonObject.put(SDKHelper.WALLET_ADDR, mJsonArray);
                String payload = mJsonObject.toString();
                try {
                    publish(SDKHelper.MQTT_DEVICE_REG, payload.getBytes());
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                }
            } else {
                // No wallets exists in the database
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Publish / send a message to an MQTT server
     *
     * @param payload the set of bytes to send to the MQTT server
     * @throws MqttException
     */
    public void publish(String mUrl, byte[] payload) throws Throwable {
        // when a notification is received that an MQTT action has completed
        SDKConstants.isMQTTRegistered = true;
        Publisher pub = new Publisher();
        pub.doPublish(mUrl, qos, payload);
    }

    /**
     * Subscribe to a topic on an MQTT server
     *
     * @param topicName to subscribe to (can be wild carded)
     * @param qos       the maximum quality of service to receive messages at for this
     *                  subscription
     * @throws MqttException
     */
    public void subscribe(String topicName, int qos) throws Throwable {
        // Subscribe using a non-blocking subscribe
        Subscriber sub = new Subscriber();
        sub.doSubscribe(topicName, qos);
    }

    /**
     * Unsubscribe to a topic on an MQTT server
     *
     * @param topicName to subscribe to (can be wild carded)
     * @throws MqttException
     */
    public void unsubscribe(String topicName) throws Throwable {
        // Subscribe using a non-blocking subscribe
        Unsubscriber unsub = new Unsubscriber();
        unsub.doUnsubscribe(topicName);
    }


    /****************************************************************/
    /* Methods to implement the MqttCallback interface */
    /****************************************************************/

    /**
     * @see MqttCallback#connectionLost(Throwable)
     */
    public void connectionLost(Throwable cause) {
        // Called when the connection to the server has been lost.
        // An application may choose to implement reconnection
        // logic at this point. This sample simply exits.
        if (mMqttManagerCallback != null) {
            mMqttManagerCallback.MQTTConnectionLost(cause);
        }
    }

    /**
     * @see MqttCallback#deliveryComplete(IMqttDeliveryToken)
     */
    public void deliveryComplete(IMqttDeliveryToken token) {

        // note that token.getTopics() returns an array so we convert to a
        // string
        // before printing it on the console
        if (mMqttManagerCallback != null) {
            mMqttManagerCallback.MQTTMessageDelivered(token);
        }
    }

    /**
     * @see MqttCallback#messageArrived(String, MqttMessage)
     */
    public void messageArrived(String topic, MqttMessage message) throws MqttException {
        // Called when a message arrives from the server that matches any
        // subscription made by the client
        try {
            String msg = new String(message.getPayload());
            JSONObject jsonObject = new JSONObject(msg);
            if (jsonObject.has(GlobalKeys.STATUS)) {
                boolean mStatus = jsonObject.getBoolean(GlobalKeys.STATUS);
                if (mStatus) {
                    JSONObject mToken = new JSONObject(jsonObject.getString(GlobalKeys.DATA));
                    registerApplication(mToken); // Register the application
                } else if (!mStatus) {
                    mPushDataModel = mDatabaseHandler.getPushData();
                    String token = mPushDataModel.getDeviceToken();
                    if (!token.equalsIgnoreCase("") && !isApplicationRegistered) {
                        isApplicationRegistered = true;
                        JSONObject mJsonObject1 = new JSONObject();
                        mJsonObject1.put(GlobalKeys.DEVICE_TOKEN, token);
                        registerApplication(mJsonObject1);
                    }
                }
            } else if (mMqttManagerCallback != null) {
                mMqttManagerCallback.MQTTMessageArrived(message);
            }
            Bundle mBundle = new Bundle();
            mBundle.putString(SDKHelper.TAG_MESSAGE, message.toString());
            Intent i = new Intent();
            i.setAction(SDKHelper.NOTIFICATION_BROADCAST_KEY);
            i.putExtra(SDKHelper.KEY_BUNDLE_DATA, mBundle);
            if (mContext != null)
                mContext.sendBroadcast(i);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /***
     * This method is used to register the application on the mqtt server
     * @param mToken
     */
    public void registerApplication(JSONObject mToken) {
        try {
            String appId = "";
            if (mToken.has(GlobalKeys.DEVICE_TOKEN)) {
                String mDeviceToken = mToken.getString(GlobalKeys.DEVICE_TOKEN);
                appId = Preferences.instance.getAppKey();
                if (mPushDataModel != null) {
                    mPushDataModel.setDeviceToken(mDeviceToken);
                } else {
                    mPushDataModel = new PushDataModel();
                    mPushDataModel.setDeviceToken(mDeviceToken);
                }
                JSONObject mJsonData = new JSONObject();
                mJsonData.put(GlobalKeys.APPLICATION_KEY, appId);
                mJsonData.put(GlobalKeys.DEVICE_TOKEN, mDeviceToken);
                if (mDatabaseHandler != null && !appId.equalsIgnoreCase("")) {
                    mDatabaseHandler.savePushData(mPushDataModel);
                }
                publish(SDKHelper.MQTT_APPLICATION_REG, mJsonData.toString().getBytes());
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    /**
     * Connect in a non-blocking way and then sit back and wait to be notified
     * that the action has completed.
     */
    public void doConnect() {
        // Connect to the server
        // Get a token and setup an asynchronous listener on the token which
        // will be notified once the connect completes
        IMqttActionListener conListener = new IMqttActionListener() {
            public void onSuccess(IMqttToken asyncActionToken) {
                try {
                    subscribe(subRespTopic, qos);
                    SDKConstants.isSubscribedForNotification = true;
                    registerDevice();
                    if (mMqttManagerCallback != null) {
                        mMqttManagerCallback.MQTTConnectionSuccess(asyncActionToken);
                    }
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                }
            }

            public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                if (mMqttManagerCallback != null) {
                    mMqttManagerCallback.MQTTConnectionFailure(asyncActionToken, exception);
                }
            }
        };

        try {
            // Connect using a non-blocking connect
            conOpt.setKeepAliveInterval(5000);
            client.connect(conOpt, null, conListener);
        } catch (MqttException e) {
            // If though it is a non-blocking connect an exception can be
            // thrown if validation of parms fails or other checks such
            // as already connected fail.
        }
    }

    /**
     * Publish in a non-blocking way and then sit back and wait to be notified
     * that the action has completed.
     */
    public class Publisher {
        public void doPublish(String topicName, int qos, byte[] payload) {
            // Send / publish a message to the server
            // Get a token and setup an asynchronous listener on the token which
            // will be notified once the message has been delivered
            MqttMessage message = new MqttMessage(payload);
            message.setQos(qos);

            String time = new Timestamp(System.currentTimeMillis()).toString();

            // Setup a listener object to be notified when the publish
            // completes.
            //
            IMqttActionListener pubListener = new IMqttActionListener() {
                public void onSuccess(IMqttToken asyncActionToken) {
                }

                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                }
            };

            try {
                // Publish the message
                if (client != null && client.isConnected()) {
                    client.publish(topicName, message, null, pubListener);
                } else {
                    getClientId();
                    initializeClient(SDKHelper.PUSH_MESSAGES_REG, clientId);
                    client.publish(topicName, message, null, pubListener);
                }

            } catch (MqttException e) {
            }
        }
    }

    /**
     * Subscribe in a non-blocking way and then sit back and wait to be notified
     * that the action has completed.
     */
    public class Subscriber {
        public void doSubscribe(String topicName, int qos) {
            // Make a subscription
            // Get a token and setup an asynchronous listener on the token which
            // will be notified once the subscription is in place.

            IMqttActionListener subListener = new IMqttActionListener() {
                public void onSuccess(IMqttToken asyncActionToken) {
                    if (mMqttManagerCallback != null) {
                        mMqttManagerCallback.MQTTtopicSubscribed(asyncActionToken);
                    }
                }

                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    if (mMqttManagerCallback != null) {
                        mMqttManagerCallback.MQTTtopicSubscribedFailed(asyncActionToken, exception);
                    }
                }
            };

            try {
                client.subscribe(topicName, qos, null, subListener);
            } catch (MqttException e) {
            }
        }
    }

    /**
     * Unsubscribe in a non-blocking way and then sit back and wait to be
     * notified that the action has completed.
     */
    public class Unsubscriber {
        public void doUnsubscribe(String topicName) {
            // Make a unsubscription
            // Get a token and setup an asynchronous listener on the token which
            // will be notified once the subscription is in place.

            IMqttActionListener unsubListener = new IMqttActionListener() {
                public void onSuccess(IMqttToken asyncActionToken) {
                }

                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                }
            };

            try {
                client.unsubscribe(topicName, null, unsubListener);
            } catch (MqttException e) {
            }
        }
    }

    /**
     * Disconnect in a non-blocking way and then sit back and wait to be
     * notified that the action has completed.
     */
    public class Disconnector {
        public void doDisconnect() {
            // Disconnect the client

            IMqttActionListener discListener = new IMqttActionListener() {
                public void onSuccess(IMqttToken asyncActionToken) {
                }

                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                }


            };

            try {
                client.disconnect(null, discListener);
            } catch (MqttException e) {
            }
        }
    }
}
