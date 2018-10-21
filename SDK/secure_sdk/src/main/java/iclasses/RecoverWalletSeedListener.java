package iclasses;

import android.graphics.Bitmap;

/**
 * Created by vvdn on 9/22/2017.
 */

public interface RecoverWalletSeedListener {
    public void walletSeedSuccess(String walletSeed, Bitmap seedQrcode);
    public void walletSeedFailed(String failed);
}
