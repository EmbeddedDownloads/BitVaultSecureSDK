package iclasses;

import java.io.Serializable;

/**
 * Created by Deepak on 5/1/2017.
 */

public interface TransactionBuilder extends Serializable {
    public void RequestedTransaction(String mTransaction);

    public void TransactionId(String mTxId);

    public void TransactionFailed(String error);
}
