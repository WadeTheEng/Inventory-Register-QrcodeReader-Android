package mike.buildsourced.common.network.response;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by user1 on 6/24/2017.
 */

public class AssetResponse {
    @SerializedName("id")
    public int id;
    @SerializedName("name")
    public String name;
    @SerializedName("description")
    public String description;
    @SerializedName("mileage")
    public int mileage;
    @SerializedName("latitude")
    public double latitude;
    @SerializedName("longitude")
    public double longitude;
    @SerializedName("last_notes_entry")
    public String last_notes_entry;
    @SerializedName("asset_number")
    public String asset_number;
    @SerializedName("rental")
    public Boolean rental;
    @SerializedName("public")
    public Boolean bPublic;
    @SerializedName("maintenance_flag")
    public Boolean maintenance_flag;
    @SerializedName("client_contact_information")
    public String client_contact_information;
    @SerializedName("image_urls")
    public List<String> image_urls;
    @SerializedName("tracking_codes")
    public List<String> tracking_codes;
    @SerializedName("project_id")
    public int project_id;



}
