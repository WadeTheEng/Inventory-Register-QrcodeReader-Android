package mike.buildsourced.common.network.response;

import com.google.gson.annotations.SerializedName;

/**
 * Created by user1 on 6/29/2017.
 */

public class ErrorResponse {
    @SerializedName("detail")
    public String strErrorDetail;
}
