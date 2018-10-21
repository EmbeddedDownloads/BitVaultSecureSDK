package iclasses;

/**
 * Created by Vinod Singh on 15/5/17.
 */

public interface SendMessageCallback {
    void sendMessageCallback(String status, String message, String txId, String messageType,String sessionKey);
}
