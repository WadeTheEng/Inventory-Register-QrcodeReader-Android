package mike.buildsourced.common.network.response;

import com.google.gson.annotations.SerializedName;

/**
 * Created by user1 on 6/24/2017.
 */

public class TokenResponse extends APIResponse {

    @SerializedName("asset_detail")
    public AssetResponse asset_detail;

}
