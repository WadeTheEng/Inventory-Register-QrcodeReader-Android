package mike.buildsourced.common;

import android.app.Activity;
import android.content.Context;

/**
 * Created by user1 on 6/23/2017.
 */

public class ContextSingleton {
    private static Activity gContext;

    public static void setContext( Activity activity) {
        gContext = activity;
    }

    public static Activity getActivity() {
        return gContext;
    }

    public static Context getContext() {
        return gContext;
    }
}
