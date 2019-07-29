package mike.buildsourced.common.database.model;

import org.litepal.annotation.Column;
import org.litepal.crud.DataSupport;
import org.litepal.util.Const;

import java.util.Date;

import mike.buildsourced.common.Constants;
import mike.buildsourced.common.GlobInfo;
import mike.buildsourced.common.database.DBManager;
import mike.buildsourced.common.network.APIManager;
import mike.buildsourced.common.network.ApiCallBack;
import mike.buildsourced.common.network.response.APIResponse;

/**
 * Created by user1 on 6/26/2017.
 */

public class Pending extends DataSupport implements ApiCallBack{
    public String latitude;
    public String longitude;
    public Boolean maintenanceFlag;
    public Date pendDate;
    public int pendKind; //0:lat 1: lng 3:Flag 4: notes 5: photo
    public String photoPath;
    public String maintenanceNotes;
    public int parent_id;



    public String getPendingname(){
        switch(pendKind){
            case 0:
                return "Lat Update";
            case 1:
                return "Lng Update";
            case 3:
                return "Maintenance Update";
            case 4:
                return "Maintenance Update";
            default:
                return "Image Added";
        }
    }

    public String getPendDate(){
        return Constants.datePendingStyleString(pendDate);
    }

    public void sendData(){
        switch(pendKind){
            case 0:
                APIManager.reqPostWithAssetsId(parent_id, "current_lat", latitude,this);
                break;
            case 1:
                APIManager.reqPostWithAssetsId(parent_id, "current_lng", longitude,this);
                break;
            case 3:
                APIManager.reqPostWithAssetsId(parent_id, "maintenance", maintenanceFlag.toString(),this);
                break;
            case 4:
                APIManager.reqPostWithAssetsId(parent_id, "notes", maintenanceNotes,this);
                break;
            default:
                APIManager.reqUploadPhoto(parent_id,photoPath,this);
        }
    }

    @Override
    public void onSuccess(APIResponse response) {
        if(response.message.compareTo("SUCCESS") == 0 ){
            DBManager.deletePending(this);
        }
    }

    @Override
    public void onFailure(String error) {
        if(GlobInfo.isOnline()){
            sendData();
        }
    }

}
