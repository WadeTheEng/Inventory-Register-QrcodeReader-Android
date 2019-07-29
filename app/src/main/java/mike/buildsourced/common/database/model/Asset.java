package mike.buildsourced.common.database.model;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by user1 on 6/26/2017.
 */

public class Asset extends DataSupport {

    public String assetAddress;
    public String assetDescription;
    public int assetId;
    public String assetName;
    public String assetNumber;
    public String clientContactInformation;
    //public List<ImageUrl> imageurls = new ArrayList<ImageUrl>();
    public String lastNotesEntry;
    public Double latitude;
    public Double longitude;
    public Boolean maintenanceFlag;
    public int mileage;
    public int projectId;
    public Boolean publicFlag;
    public Boolean rental;
    //public List<Pending> pendings = new ArrayList<Pending>();
    //public List<TrackingCode> trackingcodes = new ArrayList<TrackingCode>();

    public List<ImageUrl> getImageUrls(){
        List<ImageUrl> _arrUrls = DataSupport.where("parent_id = ?",String.valueOf(assetId))
                .find(ImageUrl.class);
        return _arrUrls;
    }

    public int getPendingCount(){
        List<Pending> _pendings = DataSupport.where("parent_id = ?",String.valueOf(assetId))
                .find(Pending.class);
        return _pendings.size();
    }

    public List<Pending> getPendingArray(){
        List<Pending> _pendings = DataSupport.where("parent_id = ?",String.valueOf(assetId))
                .find(Pending.class);
        Collections.sort(_pendings, new Comparator<Pending>() {
            @Override
            public int compare(Pending o1, Pending o2) {
                return o1.pendDate.compareTo(o2.pendDate);
            }
        });
        return _pendings;
    }
}
