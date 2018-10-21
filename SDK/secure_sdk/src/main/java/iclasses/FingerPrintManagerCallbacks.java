package iclasses;

import android.hardware.fingerprint.FingerprintManager;

/**
 * Created by Deepak on 5/5/2017.
 */

public interface FingerPrintManagerCallbacks {
    public void onAuthenticationSucceeded(FingerprintManager.AuthenticationResult result);

    public void onAuthenticationFailed();

    public void onAuthenticationHelp(int helpMsgId, CharSequence helpString);

    public void onAuthenticationError(int errMsgId, CharSequence errString);
}
