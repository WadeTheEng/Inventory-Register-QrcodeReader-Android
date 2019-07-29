package mike.buildsourced.common.database;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.zxing.BarcodeFormat;

import org.litepal.crud.DataSupport;
import org.litepal.tablemanager.Connector;
import org.litepal.tablemanager.model.ColumnModel;
import org.litepal.tablemanager.model.TableModel;
import org.litepal.util.DBUtility;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import mike.buildsourced.common.GlobInfo;
import mike.buildsourced.common.database.model.Asset;
import mike.buildsourced.common.database.model.ImageUrl;
import mike.buildsourced.common.database.model.Pending;
import mike.buildsourced.common.database.model.TrackingCode;
import mike.buildsourced.common.network.response.AssetResponse;
import mike.buildsourced.common.network.response.UpdateSinceResponse;
import mike.buildsourced.common.ContextSingleton;

/**
 * Created by user1 on 6/24/2017.
 */

public class DBManager {

    public static void initDB(){
        DataSupport.deleteAll(Asset.class);
    }

    public static void syncData(final UpdateSinceResponse data, final UpdateHandler aHandler){
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                List<Integer> _deletedSince = data.deleted_since;
                int _nCount = _deletedSince.size();
                for(int i = 0; i <_nCount; i++){
                    int _delID = _deletedSince.get(i).intValue();
                    deleteAsset(_delID);
                }

                _nCount = data.assets.size();
                List<AssetResponse> _assets = data.assets;
                for(int i = 0; i < _nCount; i++){
                    createAsset(_assets.get(i));
                }

                Date _lastSyncDate = new Date();
                try {
                    DateFormat _formater = new SimpleDateFormat("yyyy'-'MM'-'dd'T'HH':'mm':'ss'.'SSS'Z'");
                    _lastSyncDate = _formater.parse(data.last_updated_at);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                GlobInfo.setLastSynced(_lastSyncDate);

                ContextSingleton.getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        aHandler.onCompletion(true);
                        //showScannedDetail("");
                    }
                });
            }
        });

    }

    public static void getAll(){
        /*List<Asset> _assets = DataSupport.findAll(Asset.class);
        int count = _assets.size();
        count ++;*/
        TableModel tableMode = DBUtility.findPragmaTableInfo("TrackingCode", Connector.getDatabase());
        List<ColumnModel> columnModelList = tableMode.getColumnModels();
        int count = columnModelList.size();
    }

    public static Asset getAssetWithId(int aAssetId){
        List<Asset> _assets = DataSupport.where("assetId = ?", String.valueOf(aAssetId)).find(Asset.class);
        if(_assets.size() > 0){
            return _assets.get(0);
        }
        return null;
    }

    public static Asset getAsset(String aTokenId){

        List<TrackingCode> _tracking = DataSupport.where("guidValue = ?",aTokenId).find(TrackingCode.class);
        if(_tracking.size() > 0){
            TrackingCode _trackcode =_tracking.get(0);
            return getAssetWithId(_trackcode.parent_id) ;
        }
        return null;
    }

    public static void deleteAsset(int aDelId){
        List<Asset> _assets = DataSupport.where("assetId = ?", String.valueOf(aDelId)).find(Asset.class);
        if(_assets.size() > 0){
            _assets.get(0).delete();
        }
    }

    public static void updateAsset(AssetResponse aAssetResponse){
        int _id = aAssetResponse.id;// aAsset["id"] as! NSNumber
        //eleteAsset(aDelId:_id)
        //createAsset(aAsset: aAsset)
        Asset _asset = getAssetWithId(_id);
        if(_asset != null)
            updateAssetValue(aAssetResponse,_asset);
        else
            createAsset(aAssetResponse);
    }

    public static void updateAssetValue(AssetResponse aAssetResponse,Asset aAsset){
        aAsset.assetId = aAssetResponse.id;
        aAsset.assetName = aAssetResponse.name;
        aAsset.assetDescription = aAssetResponse.description;
        aAsset.mileage = aAssetResponse.mileage;
        aAsset.latitude = aAssetResponse.latitude;
        aAsset.longitude = aAssetResponse.longitude;
        aAsset.lastNotesEntry = aAssetResponse.last_notes_entry;
        aAsset.assetNumber = aAssetResponse.asset_number;
        aAsset.rental = aAssetResponse.rental;
        aAsset.publicFlag = aAssetResponse.bPublic;
        aAsset.maintenanceFlag = aAssetResponse.maintenance_flag;
        aAsset.clientContactInformation = aAssetResponse.client_contact_information;
        deleteImageUrls(aAsset.assetId);
        if(aAssetResponse.image_urls.size() > 0){
            for(int j = 0 ; j < aAssetResponse.image_urls.size(); j++){
                ImageUrl _imgUrl = new ImageUrl();
                _imgUrl.strUrl = aAssetResponse.image_urls.get(j);
                _imgUrl.parent_id = aAsset.assetId;
                _imgUrl.save();
                //aAsset.imageUrls.add(_imgUrl);
            }
        }

        aAsset.projectId = aAssetResponse.project_id;

        if(aAsset.assetName == null){
            aAsset.assetName = "";
        }

        if(aAsset.assetAddress == null){
            aAsset.assetAddress = "";
        }

        if(aAsset.assetDescription == null){
            aAsset.assetDescription = "";
        }

        if(aAsset.assetNumber == null){
            aAsset.assetNumber = "";
        }

        if(aAsset.lastNotesEntry == null){
            aAsset.lastNotesEntry = "";
        }
        deleteTrackings(aAsset.assetId);
        for(int j = 0 ; j < aAssetResponse.tracking_codes.size(); j++){
            TrackingCode _tracking = new TrackingCode();
            _tracking.guidValue = aAssetResponse.tracking_codes.get(j);
            _tracking.parent_id = aAsset.assetId;
            //Log.d("Tracking id: %s",_tracking.guidValue);
            _tracking.save();
            //aAsset.trackingCodes.add(_tracking);
        }
        aAsset.save();
    }

    public static void deleteImageUrls(int parentId){
        DataSupport.deleteAll(ImageUrl.class,"parent_id = ?",String.valueOf(parentId));
    }

    public static void deleteTrackings(int parentId){
        DataSupport.deleteAll(TrackingCode.class,"parent_id = ?",String.valueOf(parentId));
    }


    public static void createAsset(AssetResponse aAssetResponse){

        Asset _objAsset = new Asset();
        _objAsset.save();
        updateAssetValue(aAssetResponse,_objAsset);

    }

    public static void deletePending(Pending aPending){
        aPending.delete();
        Intent _intentNotify = new Intent(GlobInfo.Notify_PENDINGCHANGED);
        LocalBroadcastManager.getInstance(ContextSingleton.getContext()).sendBroadcast(_intentNotify);
        //NotificationCenter.default.post(name:.Notify_PENDINGCHANGED, object: nil)
        ///notify
    }

    public static void addLngPending(double lng, Asset aAsset){
        List<Pending> _pendings = DataSupport.where("pendKind = ? And parent_id = ?",String.valueOf(1),String.valueOf(aAsset.assetId))
                .find(Pending.class);

        if(_pendings.size() > 0){
            _pendings.get(0).delete();
        }

        Pending _objPending = new Pending();
        _objPending.pendKind = 1;
        _objPending.longitude = String.valueOf(lng);
        _objPending.pendDate = new Date(System.currentTimeMillis());
        _objPending.parent_id = aAsset.assetId;
        _objPending.save();
        notifyPendingChanged();
    }

    public static void addLatPending(double lat, Asset aAsset)
    {
        List<Pending> _pendings = DataSupport.where("pendKind = ? And parent_id = ?",String.valueOf(0),String.valueOf(aAsset.assetId))
                .find(Pending.class);

        if(_pendings.size() > 0){
            _pendings.get(0).delete();
        }

        Pending _objPending = new Pending();
        _objPending.pendKind = 0;
        _objPending.latitude = String.valueOf(lat);
        _objPending.pendDate = new Date(System.currentTimeMillis());
        _objPending.parent_id = aAsset.assetId;
        _objPending.save();
        notifyPendingChanged();
    }


    public static void  addMaintanceFlag(Boolean flag, Asset aAsset){

        List<Pending> _pendings = DataSupport.where("pendKind = ? And parent_id = ?",String.valueOf(3),String.valueOf(aAsset.assetId))
                .find(Pending.class);

        if(_pendings.size() > 0){
            _pendings.get(0).delete();
        }

        Pending _objPending = new Pending();
        _objPending.pendKind = 3;
        _objPending.maintenanceFlag = flag;
        _objPending.pendDate = new Date(System.currentTimeMillis());
        _objPending.parent_id = aAsset.assetId;
        _objPending.save();
        notifyPendingChanged();
    }

    public static void  addMaintanceNote(String notes, Asset aAsset){

        List<Pending> _pendings = DataSupport.where("pendKind = ? And parent_id = ?",String.valueOf(4),String.valueOf(aAsset.assetId))
                .find(Pending.class);

        if(_pendings.size() > 0){
            _pendings.get(0).delete();
        }

        Pending _objPending = new Pending();
        _objPending.pendKind = 4;
        _objPending.maintenanceNotes = notes;
        _objPending.pendDate = new Date(System.currentTimeMillis());
        _objPending.parent_id = aAsset.assetId;
        _objPending.save();
        notifyPendingChanged();
    }

    public static void notifyPendingChanged(){
        Intent _intentNotify = new Intent(GlobInfo.Notify_PENDINGCHANGED);
        LocalBroadcastManager.getInstance(ContextSingleton.getContext()).sendBroadcast(_intentNotify);
    }

    public static Pending  addPhotoPath(String pPath,Asset aAsset){

        List<Pending> _pendings = DataSupport.where("pendKind = ? And parent_id = ?",String.valueOf(5),String.valueOf(aAsset.assetId))
                .find(Pending.class);

        if(_pendings.size() > 0){
            _pendings.get(0).delete();
        }

        Pending _objPending = new Pending();
        _objPending.pendKind = 5;
        _objPending.photoPath = pPath;
        _objPending.pendDate = new Date(System.currentTimeMillis());
        _objPending.parent_id = aAsset.assetId;
        _objPending.save();
        notifyPendingChanged();
        return _objPending;
    }

    public static void addPhotoPathWithSuccess(String pPath, Asset aAsset){
        //aAsset.imageUrls.add(pPath);
        ImageUrl _imgUrl = new ImageUrl();
        _imgUrl.strUrl = pPath;
        _imgUrl.parent_id = aAsset.assetId;
        _imgUrl.save();
        aAsset.save();
    }

    public static int getAllPendingCount(){
        return DataSupport.count(Pending.class);
    }

    public static List<Pending> getAllPending(){
        return DataSupport.order("pendDate DESC").find(Pending.class);
    }

    public static List<Asset> getAllAssetsHavePending(){

        List<Pending> _arrPending = getAllPending();
        List<Asset> _arrAsset =  new ArrayList<Asset>();
        List<Integer> _arrAssetIds = new ArrayList<>();
        for(int i = 0; i < _arrPending.size(); i++){
            Pending _pending = _arrPending.get(i);
            Asset _parentAsset = getAssetWithId(_pending.parent_id);
            if(!_arrAssetIds.contains(_parentAsset.assetId)){
                _arrAsset.add(_parentAsset);
                _arrAssetIds.add(_parentAsset.assetId);
            }
        }
        return _arrAsset;
    }


}
