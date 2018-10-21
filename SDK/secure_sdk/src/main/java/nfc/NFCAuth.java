package nfc;

import android.app.Activity;
import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;

import utils.SDKUtils;

/**
 * Created by vvdn on 11/3/2017.
 */

public class NFCAuth extends Activity {
    private String TAG = NFCAuth.class.getSimpleName();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    protected void onResume() {
        super.onResume();
//        NfcAdapter nfcAdapter = NfcAdapter.getDefaultAdapter(this);
//        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
//        nfcAdapter.enableForegroundDispatch(this, pendingIntent, null, null);
//        ScheduledExecutorService exec = Executors.newSingleThreadScheduledExecutor();
//        exec.scheduleAtFixedRate(new Runnable() {
//            @Override
//            public void run() {
//                // do stuff
//                handleIntent(getIntent());
//            }
//        }, 5, 5, TimeUnit.SECONDS);
        handleIntent(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleIntent(intent);

    }

    @Override
    protected void onPause() {
        super.onPause();
        NfcAdapter nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        nfcAdapter.disableForegroundDispatch(this);
    }

    private void handleIntent(Intent intent) {
        NfcAdapter.getDefaultAdapter(this).disableForegroundDispatch(this);
        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(intent.getAction())) {
            Parcelable[] rawMsgs = intent.getParcelableArrayExtra(
                    NfcAdapter.EXTRA_NDEF_MESSAGES);
            // only one message sent during the beam
            NdefMessage msg = (NdefMessage) rawMsgs[0];
            displayByteArray(msg.getRecords()[0].getPayload());
            try {
                writeToFile(getString(msg.getRecords()[0]));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            //Toast.makeText(this, "msg:" + new String(msg.getRecords()[0].getPayload()), Toast.LENGTH_SHORT).show();
        } else {
            SDKUtils.showLog(TAG, "---Intent is null----");
        }
    }
    private String displayByteArray(byte[] bytes) {
        String res="";
        StringBuilder builder = new StringBuilder().append("[");
        for (int i = 0; i < bytes.length; i++) {
            res+=(char)bytes[i];
        }
        SDKUtils.showLog(TAG,"----The Data in the card----"+res);
        return res;
    }


    private String getString(NdefRecord record) throws UnsupportedEncodingException {
        byte[] payload = record.getPayload();

        // Get the Text Encoding
        String textEncoding = ((payload[0] & 128) == 0) ? "UTF-8" : "UTF-16";

        // Get the Language Code
        int languageCodeLength = payload[0] & 0063;

        // String languageCode = new String(payload, 1, languageCodeLength, "US-ASCII");
        // e.g. "en"

        // Get the Text
        return new String(payload, languageCodeLength + 1, payload.length - languageCodeLength - 1, textEncoding);
    }

    private void writeToFile(String data) {
        // Get the directory for the user's public pictures directory.
        File path = new File(this.getFilesDir(), "config_nfc_test");

        // Make sure the path directory exists.
        if (!path.exists()) {
            // Make it, if it doesn't exit
            path.mkdirs();
        }
        final File file = new File(path, "config.txt");

        // Save your stream, don't forget to flush() it before closing it.

        try {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            FileOutputStream fOut = new FileOutputStream(file);
            OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);
            myOutWriter.append(data);

            myOutWriter.close();

            fOut.flush();
            fOut.close();
        } catch (IOException e) {
        }
    }
}
