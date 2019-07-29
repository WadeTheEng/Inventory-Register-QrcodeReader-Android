package mike.buildsourced.common.network.response;

import com.google.gson.annotations.SerializedName;

/**
 * Created by user1 on 6/23/2017.
 */

public class LoginResponse extends APIResponse {

    @SerializedName("token")
    public String token;

}
