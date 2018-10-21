package iclasses.mediavaultcallback;

import model.MediaVaultBlockModel;

/**
 * Created by vvdn on 12/09/2017
 */

public interface MediaVaultDecryptDataCallback {
    void decryptedMediaVaultFile(String decryptMessage, MediaVaultBlockModel matchTransactionModel);
}
