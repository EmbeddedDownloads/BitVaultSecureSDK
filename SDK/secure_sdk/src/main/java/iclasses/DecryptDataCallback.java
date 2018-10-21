package iclasses;

import model.MatchTransactionModel;

/**
 * Created by linchpin on 29/5/17.
 */

public interface DecryptDataCallback {
    void decryptedMessage(String decryptMessage, MatchTransactionModel matchTransactionModel);
}