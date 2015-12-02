package com.applozic.mobicomkit.api;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.applozic.mobicomkit.api.account.user.MobiComUserPreference;
import com.applozic.mobicomkit.api.conversation.MessageIntentService;
import com.applozic.mobicomkit.api.conversation.MobiComMessageService;
import com.applozic.mobicomkit.broadcast.BroadcastService;
import com.applozic.mobicomkit.feed.MqttMessageResponse;
import com.applozic.mobicommons.commons.core.utils.Utils;
import com.applozic.mobicommons.json.GsonUtils;
import com.applozic.mobicommons.people.contact.Contact;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import static com.applozic.mobicomkit.api.MobiComKitConstants.APPLICATION_KEY_META_DATA;

/**
 * Created by sunil on 26/11/15.
 */
public class ApplozicMqttService implements MqttCallback {


    private static final String STATUS = "status";
    private static final String MESSAGE_RECEIVED = "MESSAGE_RECEIVED";
    private static final String MESSAGE_DELIVERED = "MESSAGE_DELIVERED";
    private static final String MQTT_URL = "tcp://apps.applozic.com";
    private static final String MQTT_PORT = "1883";
    private static final String TAG = "ApplozicMqttService";
    private static final String TYPINGTOPIC = "typing-";
    private static ApplozicMqttService applozicMqttService;
    private MqttClient client;
    private MemoryPersistence memoryPersistence;
    private Context context;


    private ApplozicMqttService(Context context) {
        this.context = context;
        memoryPersistence = new MemoryPersistence();
    }


    public static ApplozicMqttService getInstance(Context context) {

        if (applozicMqttService == null) {
            applozicMqttService = new ApplozicMqttService(context);
        }
        return applozicMqttService;
    }

    private MqttClient connect() {
        String userId = MobiComUserPreference.getInstance(context).getUserId();
        if (client == null) {
            try {
                client = new MqttClient(MQTT_URL + ":" + MQTT_PORT, userId, memoryPersistence);
                if (client != null) {
                    MqttConnectOptions options = new MqttConnectOptions();
                    options.setWill(STATUS, (MobiComUserPreference.getInstance(context).getSuUserKeyString() + "," + "0").getBytes(), 0, true);
                    client.setCallback(ApplozicMqttService.this);

                    if (!client.isConnected()) {
                        client.connect(options);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return client;
    }

    public synchronized void connectPublish(final String userKeyString, final String status) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    final MqttClient client = connect();
                    if (client == null || !client.isConnected()) {
                        return;
                    }
                    MqttMessage message = new MqttMessage();
                    message.setRetained(false);
                    message.setPayload((userKeyString + "," + status).getBytes());
                    Log.i(TAG, "UserKeyString, status:" + userKeyString + ", " + status);
                    message.setQos(0);
                    client.publish(STATUS, message);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }).start();

    }

    public synchronized  void subscribe() {
        connect();
        subscribeToConversation();
        subscribeToTypingTopic();
    }

    public synchronized  void unSubscribe() {
        unSubscribeToConversation();
        unSubscribeToTypingTopic();
    }

    public synchronized void subscribeToConversation() {

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String userKeyString = MobiComUserPreference.getInstance(context).getSuUserKeyString();
                    connectPublish(userKeyString, "1");
                    if (client != null && client.isConnected()) {
                        client.subscribe(userKeyString, 0);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }

    public synchronized  void unSubscribeToConversation() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (client == null || !client.isConnected()) {
                        return;
                    }
                    String userKeyString = MobiComUserPreference.getInstance(context).getSuUserKeyString();
                    client.unsubscribe(userKeyString);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void disconnectPublish(String userKey, String status) {
        try {
            connectPublish(userKey, status);
            if (!MobiComUserPreference.getInstance(context).isLoggedIn()) {
                disconnect();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void disconnect() {
        if (client != null) {
            try {
                client.disconnect();
            } catch (MqttException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void connectionLost(Throwable throwable) {
        BroadcastService.sendMQTTDisconnected(context, BroadcastService.INTENT_ACTIONS.MQTT_DISCONNECTED.toString());
    }

    @Override
    public void messageArrived(String s, MqttMessage mqttMessage) throws Exception {
        Log.i(TAG, "Received MQTT message: " + new String(mqttMessage.getPayload()));
        try {
            if (!TextUtils.isEmpty(s) && s.startsWith(TYPINGTOPIC)) {
                String typingResponse[] = mqttMessage.toString().split(",");
                String applicationId = typingResponse[0];
                String userId = typingResponse[1];
                String isTypingStatus = typingResponse[2];
                BroadcastService.sendUpdateTypingBroadcast(context, BroadcastService.INTENT_ACTIONS.UPDATE_TYPING_STATUS.toString(), applicationId, userId, isTypingStatus);
            } else {
                final MqttMessageResponse mqttMessageResponse = (MqttMessageResponse) GsonUtils.getObjectFromJson(mqttMessage.toString(), MqttMessageResponse.class);
                if (mqttMessageResponse != null) {
                    final MobiComMessageService messageService = new MobiComMessageService(context, MessageIntentService.class);
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            Log.i(TAG, "MQTT message calling ");
                            if (MESSAGE_RECEIVED.equals(mqttMessageResponse.getType())) {
                                messageService.syncMessages();
                            }
                            if (MESSAGE_DELIVERED.equals(mqttMessageResponse.getType())) {
                                String splitKeyString[] = (mqttMessageResponse.getMessage()).split(",");
                                String keyString = splitKeyString[0];
                                String userId = splitKeyString[1];
                                messageService.updateDeliveryStatus(keyString);
                            }
                        }
                    }).start();

                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public synchronized void publishTopic(final String statusMessage, final String applicationId, final String status, final String loggedInUserId, final String userId, final String userKeyString) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    final MqttClient client = connect();
                    if (client == null || !client.isConnected()) {
                        return;
                    }
                    MqttMessage message = new MqttMessage();
                    message.setRetained(false);
                    message.setPayload((applicationId + "," + loggedInUserId + "," + status).getBytes());
                    message.setQos(0);
                    client.publish(statusMessage + "-" + applicationId + "-" + userId, message);
                    Log.i(TAG, "Published " + new String(message.getPayload()) + " to topic: " + statusMessage + "-" + applicationId + "-" + userId);
                } catch (MqttException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public synchronized void subscribeToTypingTopic() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    final MqttClient client = connect();
                    if (client == null || !client.isConnected()) {
                        return;
                    }
                    MobiComUserPreference mobiComUserPreference = MobiComUserPreference.getInstance(context);
                    client.subscribe("typing-" + Utils.getMetaDataValue(context, APPLICATION_KEY_META_DATA) + "-" + mobiComUserPreference.getUserId(), 0);
                    Log.i(TAG, "Subscribed to topic: " + "typing-" + Utils.getMetaDataValue(context, APPLICATION_KEY_META_DATA) + "-" + mobiComUserPreference.getUserId());
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }).start();

    }

    public void unSubscribeToTypingTopic() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (client == null || !client.isConnected()) {
                        return;
                    }
                    MobiComUserPreference mobiComUserPreference = MobiComUserPreference.getInstance(context);
                    client.unsubscribe("typing-" + Utils.getMetaDataValue(context, APPLICATION_KEY_META_DATA) + "-" + mobiComUserPreference.getUserId());
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }).start();
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {

    }

    public void typingStarted(Contact contact) {
        MobiComUserPreference mobiComUserPreference = MobiComUserPreference.getInstance(context);
        publishTopic("typing", getApplicationId(contact), "1", mobiComUserPreference.getUserId(), contact.getUserId(), mobiComUserPreference.getSuUserKeyString());
    }

    public void typingStopped(Contact contact) {
        MobiComUserPreference mobiComUserPreference = MobiComUserPreference.getInstance(context);
        publishTopic("typing", getApplicationId(contact), "0", mobiComUserPreference.getUserId(), contact.getUserId(), mobiComUserPreference.getSuUserKeyString());
    }

    public String getApplicationId(Contact contact) {
        String applicationId = contact.getApplicationId();
        if (TextUtils.isEmpty(applicationId)) {
            applicationId = Utils.getMetaDataValue(context, APPLICATION_KEY_META_DATA);
        }
        return applicationId;
    }

}


