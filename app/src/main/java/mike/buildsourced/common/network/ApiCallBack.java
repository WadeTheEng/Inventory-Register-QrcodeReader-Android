package mike.buildsourced.common.network;

import mike.buildsourced.common.network.response.APIResponse;

/**
 * Created by user1 on 6/23/2017.
 */

public interface ApiCallBack {

    void onSuccess(APIResponse response);
    void onFailure(String error);
}
