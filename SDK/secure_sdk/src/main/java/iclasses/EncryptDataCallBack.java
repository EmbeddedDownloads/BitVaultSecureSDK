package iclasses;

import model.DataToPBCModel;

/**
 * Created by Vinod Singh on 24/5/17.
 */

public interface EncryptDataCallBack {
    void encryptDataResponse(DataToPBCModel dataToPBCModel, String msgCase);
}
