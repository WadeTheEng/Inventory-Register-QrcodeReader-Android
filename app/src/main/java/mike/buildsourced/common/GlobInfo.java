package mike.buildsourced.common;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.content.LocalBroadcastManager;

import java.util.Date;

import mike.buildsourced.common.database.DBManager;
import mike.buildsourced.common.database.SyncManager;
import mike.buildsourced.common.database.UpdateHandler;
import mike.buildsourced.common.network.APIManager;
import mike.buildsourced.common.network.ApiCallBack;
import mike.buildsourced.common.network.response.APIResponse;
import mike.buildsourced.common.network.response.UpdateSinceResponse;
import mike.buildsourced.main.MainTabActivity;
import mike.buildsourced.setting.SettingActivity;

/**
 * Created by user1 on 6/23/2017.
 */

public class GlobInfo extends BroadcastReceiver{

    static final public String URL_Server = "http://www.buildsourced.com";
    static final public String URL_APIServer = "http://api.buildsourced.com/api/v1/";

    static final public String Notify_WIFION = "Notify_WIFION";
    static final public String Notify_WIFIOFF = "Notify_WIFIOFF";
    static final public String Notify_APPMODECHANGED = "Notify_APPMODECHANGED";
    static final public String Notify_PENDINGCHANGED = "Notify_PENDINGCHANGED";

    public static boolean isSync = false;
    public static boolean isFirstLoaded = true;
    public static final String keyAuthKey = "AuthKey";
    public static final String keyUserEmail = "UserEmail";
    public static final String keyUserPasswd = "UserPasswd";
    public static final String keyUseMethod = "UseMethod";
    public static final String keyLastSynced = "LastSynced";
    public static final String keySignIn = "SignIn";
    public static MainTabActivity mainTabActivity;

    public static boolean isOnline(){
        Context aContext = ContextSingleton.getContext();
        if(NetworkUtil.getConnectivityStatus(aContext) == NetworkUtil.TYPE_NOT_CONNECTED){
            return false;
        }
        return true;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        //MDToast.makeText(context,NetworkUtil.getConnectivityStatusString(context),2);
        if(NetworkUtil.getConnectivityStatus(context) == NetworkUtil.TYPE_NOT_CONNECTED){
            Intent _intentNotify = new Intent(GlobInfo.Notify_WIFIOFF);
            LocalBroadcastManager.getInstance(context).sendBroadcast(_intentNotify);
            GlobInfo.loadInitiAssets();
        }
        else{
            Intent _intentNotify = new Intent(GlobInfo.Notify_WIFION);
            LocalBroadcastManager.getInstance(context).sendBroadcast(_intentNotify);
        }
    }

    public static void loadInitiAssets(){
        if(GlobInfo.getIsSignedIn()){
            SyncManager.updateAllPendings();

            APIManager.reqUpdatedSince(Constants.dateString(GlobInfo.getLastSynced()), new ApiCallBack() {
                @Override
                public void onSuccess(APIResponse response) {
                    GlobInfo.isSync = true;
                    mainTabActivity.showSync();
                    DBManager.syncData((UpdateSinceResponse) response, new UpdateHandler() {
                        @Override
                        public void onCompletion(Boolean bSuccess) {
                            mainTabActivity.dismissSync();
                        }
                    });
                    GlobInfo.isSync = false;
                    GlobInfo.isFirstLoaded = false;

                }

                @Override
                public void onFailure(String error) {

                }
            });
        }
    }

    public static String getAuthKey(){
        Context aContext = ContextSingleton.getContext();
        SharedPreferences preferences = aContext.getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
        return preferences.getString(keyAuthKey,"");
    }

    public static void setAuthKey(String aKey){
        Context aContext = ContextSingleton.getContext();
        SharedPreferences preferences = aContext.getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(keyAuthKey, aKey);
        editor.commit();
    }

    public static String getUserEmail(){
        Context aContext = ContextSingleton.getContext();
        SharedPreferences preferences = aContext.getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
        return preferences.getString(keyUserEmail,"");
    }

    public static void setUserEmail(String aKey){
        Context aContext = ContextSingleton.getContext();
        SharedPreferences preferences = aContext.getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(keyUserEmail, aKey);
        editor.commit();
    }

    public static String getUserPasswd(){
        Context aContext = ContextSingleton.getContext();
        SharedPreferences preferences = aContext.getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
        return preferences.getString(keyUserPasswd,"");
    }

    public static void setUserPasswd(String aKey){
        Context aContext = ContextSingleton.getContext();
        SharedPreferences preferences = aContext.getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(keyUserPasswd, aKey);
        editor.commit();
    }

    public static boolean getIsSignedIn(){
        Context aContext = ContextSingleton.getContext();
        SharedPreferences preferences = aContext.getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
        return preferences.getBoolean(keySignIn,false);
    }

    public static void setIsSignedIn(Boolean aValue){
        Context aContext = ContextSingleton.getContext();
        SharedPreferences preferences = aContext.getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(keySignIn, aValue);
        editor.commit();
    }

    public static boolean getIsUseApp(){
        Context aContext = ContextSingleton.getContext();
        SharedPreferences preferences = aContext.getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
        return preferences.getBoolean(keyUseMethod,false);
    }

    public static void setIsUseApp(Boolean aValue){
        Context aContext = ContextSingleton.getContext();
        SharedPreferences preferences = aContext.getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(keyUseMethod, aValue);
        editor.commit();
    }

    public static Date getLastSynced(){
        Context aContext = ContextSingleton.getContext();
        SharedPreferences preferences = aContext.getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
        long _nTimeValue = preferences.getLong(keyLastSynced, 0);
        if(_nTimeValue == 0)
        {
            return Constants.dateFromString("2000-01-01 00:00:00");
        }
        return new Date(_nTimeValue);
    }

    public static void setLastSynced(Date aValue){
        Context aContext = ContextSingleton.getContext();
        SharedPreferences preferences = aContext.getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putLong(keyLastSynced, aValue.getTime());
        editor.commit();
    }

    static public void globalInit(){
        initReachability();
    }

    static public void initReachability(){

    }

    static public void gotoLogin(Context context){
        Intent _intentSetting = new Intent(context, SettingActivity.class);
        context.startActivity(_intentSetting);
    }

    static public void gotoMainView(Context context){
        Intent _intentMain = new Intent(context, MainTabActivity.class);
        context.startActivity(_intentMain);
    }



    static public void  changeAppMode(Boolean aUseApp){
        if(GlobInfo.getIsSignedIn()){
            GlobInfo.setIsUseApp(aUseApp);
            GlobInfo.notifyAppModeChanged();
        }
        else{
            GlobInfo.setIsUseApp(aUseApp);
        }
    }

    static public void gotoWebHandler(String aUrl){
        if(!getIsUseApp()){
            Context _main = ContextSingleton.getContext();
            if(_main instanceof MainTabActivity){
                MainTabActivity _mainTab = (MainTabActivity)_main;
                _mainTab.handleUrl(aUrl);
            }
        }
    }

    static public void startApp(Context context){

        GlobInfo.globalInit();
        if(!GlobInfo.getIsSignedIn())
        {
            GlobInfo.gotoLogin(context);
        }
        else{
            gotoMainView(context);
        }

    }

    public static void notifyAppModeChanged(){
        Intent _intentNotify = new Intent(GlobInfo.Notify_APPMODECHANGED);
        LocalBroadcastManager.getInstance(ContextSingleton.getContext()).sendBroadcast(_intentNotify);
    }

    private static class NetworkUtil {

        public static int TYPE_WIFI = 1;
        public static int TYPE_MOBILE = 2;
        public static int TYPE_NOT_CONNECTED = 0;


        public static int getConnectivityStatus(Context context) {
            ConnectivityManager cm = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);

            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            if (null != activeNetwork) {
                if(activeNetwork.getType() == ConnectivityManager.TYPE_WIFI)
                    return TYPE_WIFI;

                if(activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE)
                    return TYPE_MOBILE;
            }
            return TYPE_NOT_CONNECTED;
        }

        public static String getConnectivityStatusString(Context context) {
            int conn = NetworkUtil.getConnectivityStatus(context);
            String status = null;
            if (conn == NetworkUtil.TYPE_WIFI) {
                status = "Wifi enabled";
            } else if (conn == NetworkUtil.TYPE_MOBILE) {
                status = "Mobile data enabled";
            } else if (conn == NetworkUtil.TYPE_NOT_CONNECTED) {
                status = "Not connected to Internet";
            }
            return status;
        }
    }
}
