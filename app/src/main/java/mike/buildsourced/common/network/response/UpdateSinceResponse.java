package mike.buildsourced.common.network.response;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by user1 on 6/24/2017.
 */

public class UpdateSinceResponse extends APIResponse {

    @SerializedName("last_updated_at")
    public String last_updated_at;

    @SerializedName("deleted_since")
    public List<Integer> deleted_since;

    @SerializedName("assets")
    public List<AssetResponse> assets;
}
