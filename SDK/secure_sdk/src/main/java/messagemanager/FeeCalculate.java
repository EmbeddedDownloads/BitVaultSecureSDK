package messagemanager;

import model.MessageFeeModel;

/**
 * Created by Vinod Singh on 26/5/17.
 */

public class FeeCalculate {

    /***
     * This method is used to calculate bitcoin amount needed to send message & return callback
     *
     * @param  charSize,mediaSize,message_Priority,jsonString
     * @return
     */
    public double calculateFee(int charSize, long mediaSize, MessageFeeModel messageFeeModel){
        double messageFee = 0;
        if(messageFeeModel != null){
          /*  messageFee = messageFee + calculateCharFee(charSize,messageFeeModel);
            messageFee = messageFee + calculateMediaSize(mediaSize,messageFeeModel);*/
            if(charSize <= 0  && mediaSize <= 0){
                messageFee = 0l;
            }
            else{
                messageFee = Double.parseDouble(messageFeeModel.getFees().getMsg_char_fee());
            }

            return messageFee;
        }
        else{
            return 0l;
        }
    }
    // method to calculate fee as per char length
    private double calculateCharFee(int charSize, MessageFeeModel messageFeeModel){
        double charFee = 0;
        if(charSize <= 0){
            charFee = 0;
        }
        else {
            charFee = Double.parseDouble(messageFeeModel.getFees().getMsg_char_fee());
        }

        return charFee;
    }

    // method to calculate fee as per media file size
    private double calculateMediaSize(long mediaSize, MessageFeeModel messageFeeModel){
        double mediaSizeFee = 0;
        double sizeInMb = mediaSize/1024.0;
        if(sizeInMb <= 0 ){
            mediaSizeFee = 0;
        }
        else if(sizeInMb > 0 && sizeInMb <= 1){
            mediaSizeFee = Double.parseDouble(messageFeeModel.getFees().getMedia_fee().get_2MB());
        }
        else if(sizeInMb > 0 && sizeInMb <= 2){
            mediaSizeFee = Double.parseDouble(messageFeeModel.getFees().getMedia_fee().get_4MB());
        }
        else if(sizeInMb > 0 && sizeInMb <= 3){
            mediaSizeFee = Double.parseDouble(messageFeeModel.getFees().getMedia_fee().get_6MB());
        }
        else if(sizeInMb > 0 && sizeInMb <= 4) {
            mediaSizeFee = Double.parseDouble(messageFeeModel.getFees().getMedia_fee().get_8MB());
        }
        else if(sizeInMb > 0 && sizeInMb <= 5){
            mediaSizeFee = Double.parseDouble(messageFeeModel.getFees().getMedia_fee().get_10MB());
        }
        return mediaSizeFee;
    }
}
