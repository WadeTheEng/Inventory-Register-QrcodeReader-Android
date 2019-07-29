package mike.buildsourced.common.database;

import java.util.List;

import mike.buildsourced.common.GlobInfo;
import mike.buildsourced.common.MDToast;
import mike.buildsourced.common.database.model.Asset;
import mike.buildsourced.common.database.model.Pending;
import mike.buildsourced.common.network.APIManager;
import mike.buildsourced.common.network.ApiCallBack;
import mike.buildsourced.common.network.response.APIResponse;

/**
 * Created by user1 on 6/26/2017.
 */


//typealias UpdateHandler = (_ success: Bool) -> Void

public class SyncManager {

    public static void updateAllPendings(){

        List<Pending> _arrAllPendings = DBManager.getAllPending();
        int _nCount = _arrAllPendings.size();
        for(int i = 0; i < _nCount; i++){
            Pending _pending = _arrAllPendings.get(i);
            if(GlobInfo.isOnline()){
                _pending.sendData();
            }
        }
    }

    public static void updateLat(final Double lat, final Asset aAsset){
        aAsset.longitude = lat;
        aAsset.save();
        if(GlobInfo.isOnline()){
            APIManager.reqPostWithAssetsId(aAsset.assetId, "current_lat", String.format("%f", lat), new ApiCallBack() {
                @Override
                public void onSuccess(APIResponse response) {
                    if(response.message.compareTo("SUCCESS") != 0){
                        DBManager.addLatPending(lat, aAsset);
                    }
                }

                @Override
                public void onFailure(String error) {
                    DBManager.addLatPending(lat, aAsset);
                }
            });
        }
        else{
            DBManager.addLatPending(lat, aAsset);
        }
    }

    public static void updateLng(final Double lng, final Asset aAsset){
        aAsset.longitude = lng;
        aAsset.save();
        if(GlobInfo.isOnline()){
            APIManager.reqPostWithAssetsId(aAsset.assetId, "current_lng", String.format("%f", lng), new ApiCallBack() {
                @Override
                public void onSuccess(APIResponse response) {
                    if(response.message.compareTo("SUCCESS") != 0){
                        DBManager.addLngPending(lng, aAsset);
                    }
                }

                @Override
                public void onFailure(String error) {
                    DBManager.addLngPending(lng, aAsset);
                }
            });
        }
        else{
            DBManager.addLngPending(lng, aAsset);
        }

    }

    public static void updateMaintncFlag(final Boolean flag,final Asset aAsset,final UpdateHandler handler){
        aAsset.maintenanceFlag = flag;
        aAsset.save();
        if(GlobInfo.isOnline()){
            APIManager.reqPostWithAssetsId(aAsset.assetId, "maintenance", flag.toString(), new ApiCallBack() {
                @Override
                public void onSuccess(APIResponse response) {
                    if(response.message.compareTo("SUCCESS") != 0){
                        handler.onCompletion(false);
                        DBManager.addMaintanceFlag(flag,aAsset);
                    }
                    else
                        handler.onCompletion(true);
                }

                @Override
                public void onFailure(String error) {
                    handler.onCompletion(false);
                    DBManager.addMaintanceFlag(flag,aAsset);
                }
            });
        }
        else{
            DBManager.addMaintanceFlag(flag,aAsset);
            handler.onCompletion(false);
        }

    }

    public static void updateMaintncNotes(final String notes, final Asset aAsset,final UpdateHandler handler){
        aAsset.lastNotesEntry = notes;
        aAsset.save();
        if(GlobInfo.isOnline()){
            APIManager.reqPostWithAssetsId(aAsset.assetId, "notes", notes, new ApiCallBack() {
                @Override
                public void onSuccess(APIResponse response) {
                    if(response.message.compareTo("SUCCESS") != 0){
                        handler.onCompletion(false);
                        DBManager.addMaintanceNote(notes, aAsset);
                    }
                    else
                        handler.onCompletion(true);
                }

                @Override
                public void onFailure(String error) {
                    handler.onCompletion(false);
                    DBManager.addMaintanceNote(notes, aAsset);
                }
            });
        }
        else{
            DBManager.addMaintanceNote(notes, aAsset);
            handler.onCompletion(false);
        }

    }

    public static void addPhoto(final String aImagePath ,final Asset aAsset,final UpdateHandler handler){

        if(GlobInfo.isOnline()){
            APIManager.reqUploadPhoto(aAsset.assetId, aImagePath, new ApiCallBack() {
                @Override
                public void onSuccess(APIResponse response) {
                    handler.onCompletion(true);
                    DBManager.addPhotoPathWithSuccess(aImagePath, aAsset);
                }

                @Override
                public void onFailure(String error) {
                    handler.onCompletion(false);
                    Pending _objPending = DBManager.addPhotoPath(aImagePath, aAsset);
                    _objPending.sendData();
                }
            });

        }
        else{
            DBManager.addPhotoPath(aImagePath, aAsset);
            handler.onCompletion(false);
        }

    }
}
