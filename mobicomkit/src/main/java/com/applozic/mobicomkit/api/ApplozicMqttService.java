package com.applozic.mobicomkit.api;

import android.content.Context;
import android.util.Log;

import com.applozic.mobicomkit.api.account.user.MobiComUserPreference;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

/**
 * Created by sunil on 26/11/15.
 */
public class ApplozicMqttService implements MqttCallback {


    private static String MQTT_URL = "tcp://apps.applozic.com";
    private static String MQTT_PORT = "1883";
    private static String TAG = "ApplozicMqttService";
    private static ApplozicMqttService applozicMqttService;
    private MqttClient client;
    private MemoryPersistence memoryPersistence;
    private Context context;
    private static final String STATUS = "status";


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


    public MqttClient connect() {
        String userId = MobiComUserPreference.getInstance(context).getUserId();
        if (client == null) {
            try {
                client = new MqttClient(MQTT_URL + ":" + MQTT_PORT, userId, memoryPersistence);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return client;
    }

    public synchronized void connectPublish(String userKeyString, String status) {
        final MqttClient client = connect();
        try {
            MqttConnectOptions options = new MqttConnectOptions();
            options.setWill(STATUS, (userKeyString + "," + status).getBytes(), 0, true);
            client.setCallback(this);
            if (!client.isConnected()) {
                client.connect(options);
            }
            MqttMessage message = new MqttMessage();
            message.setRetained(false);
            message.setPayload((userKeyString + "," + status).getBytes());
            Log.i(TAG, "UserKeyString,status:" + userKeyString + ", " + status);
            message.setQos(0);
            client.publish(STATUS, message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void disconnect(String userKey, String status) {

        try {
            connectPublish(userKey, status);
            if (client != null) {
                client.disconnect();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void connectionLost(Throwable throwable) {

    }

    @Override
    public void messageArrived(String s, MqttMessage mqttMessage) throws Exception {

    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {

    }
}


