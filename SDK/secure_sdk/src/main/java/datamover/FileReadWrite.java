package datamover;

import android.os.Environment;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;

import commons.SDKConstants;
import commons.SDKHelper;

/**
 * Created by Vinod Singh on 12/5/17.
 */

public class FileReadWrite {

    /***
     * This method is used to write data onto the file send to PBC
     *
     * @param text
     * @param location
     * @return
     */
    public static boolean writeToFile(byte[] text, String location,String fileName, int bufSize) {

        try {
            File root = new File(location);
            if (!root.exists()) {
                root.mkdirs();
            }
            File mFile = new File(location + fileName);

            if (mFile.exists()) {
                mFile.delete();
            }
            mFile.createNewFile();
            byte[] bWrite = text;
            FileWriter writer = new FileWriter(mFile);
            BufferedWriter bufferedWriter = new BufferedWriter(writer, bufSize);
            for (int x = 0; x < bWrite.length; x++) {
                bufferedWriter.write(bWrite[x]);   // writes the bytes
            }
            bufferedWriter.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    /***
     * This method is used to write data onto the file to maintain logs
     *
     * @param text
     * @param location
     * @return
     */

    public static boolean writeToFileall(String text, String location) {
        location = Environment.getExternalStorageDirectory()
                .getAbsolutePath() +location;
        text = text + "....................................................\n\n\n";
        try {
            File root = new File(location);
            if (!root.exists()) {
                root.mkdirs();
            }
            File mFile = new File(location + SDKHelper.FILE_NAME_LOG);

            if (!mFile.exists()) {
                mFile.createNewFile();
            }

            byte[] bWrite = text.getBytes();
            FileWriter writer = new FileWriter(mFile);
            BufferedWriter bufferedWriter = new BufferedWriter(writer, SDKConstants.WRITE_FILE_BUFFER_SIZE);
            for (int x = 0; x < bWrite.length; x++) {
                bufferedWriter.write(bWrite[x]);   // writes the bytes
            }
            bufferedWriter.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    /***
     * This method is used to read data from the file and access into the sdk
     *
     * @param location
     * @return
     */
    public static byte[] readFromFile(String location) {
        byte[] buffer = new byte[SDKConstants.READ_FILE_BUFFER_SIZE];
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int bytesRead;
        try {
            File f = new File(location);
            InputStream is = new BufferedInputStream(new FileInputStream(f));
            while ((bytesRead = is.read(buffer)) != -1) {
                baos.write(buffer, 0, bytesRead);
            }
            is.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

        return baos.toByteArray();
    }

}
