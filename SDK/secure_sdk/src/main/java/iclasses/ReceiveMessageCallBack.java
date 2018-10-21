package iclasses;

/**
 * Created by Vinod Singh on 18/5/17.
 */

public interface ReceiveMessageCallBack {
    void receiveMessageCallback(String status, String message, String messageList,
                                String senderAdress, String tag, String txId);
}
