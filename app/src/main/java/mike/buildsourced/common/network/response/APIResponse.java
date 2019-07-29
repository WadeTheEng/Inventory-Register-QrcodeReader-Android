package mike.buildsourced.common.network.response;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by user1 on 6/23/2017.
 */

public class APIResponse {

    @SerializedName("code")
    public int code;

    @SerializedName("message")
    public String message;

    @SerializedName("errors")
    public List<ErrorResponse> errors;

}
