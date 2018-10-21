package iclasses;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttMessage;

/**
 * Created by ${e} on 7/6/2017.
 */

public interface MQTTManagerCallback{
    public void MQTTConnectionSuccess(IMqttToken asyncActionToken);

    public void MQTTConnectionFailure(IMqttToken asyncActionToken, Throwable exception);

    public void MQTTConnectionLost(Throwable cause);

    public void MQTTMessageArrived(MqttMessage message);

    public void MQTTMessageDelivered(IMqttDeliveryToken token);

    public void MQTTtopicSubscribed(IMqttToken asyncActionToken);

    public void MQTTtopicSubscribedFailed(IMqttToken asyncActionToken,
                                          Throwable exception);
}
