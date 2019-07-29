package mike.buildsourced;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.bigkoo.svprogresshud.SVProgressHUD;

import java.util.List;

import mike.buildsourced.common.Constants;
import mike.buildsourced.common.GlobInfo;
import mike.buildsourced.common.database.DBManager;
import mike.buildsourced.main.MaintncFragment;
import mike.buildsourced.main.Tab1Fragment;
import mike.buildsourced.pending.PendingListFragment;
import mike.buildsourced.setting.SettingActivity;

/**
 * Created by user1 on 6/16/2017.
 */

public class BaseFragment extends BackableFragment implements View.OnClickListener {

    TextView lbTitle;
    ImageView viewBadge;
    boolean bShowSetting = false;
    boolean bShowPending = false;
    public SVProgressHUD hudProgress;
    public Tab1Fragment mainFragment;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        hudProgress =  new SVProgressHUD(getActivity());
        registerBrodcastReceiver();
    }

    public void registerBrodcastReceiver(){
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mReceiverWifiAndAppMode,
                new IntentFilter(GlobInfo.Notify_WIFION));
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mReceiverWifiAndAppMode,
                new IntentFilter(GlobInfo.Notify_WIFIOFF));
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mReceiverWifiAndAppMode,
                new IntentFilter(GlobInfo.Notify_APPMODECHANGED));
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mReceiverPendingChanged,
                new IntentFilter(GlobInfo.Notify_PENDINGCHANGED));
    }

    private BroadcastReceiver mReceiverWifiAndAppMode = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(bShowSetting){
                showSettingItems(getView());
            }
        }
    };

    private BroadcastReceiver mReceiverPendingChanged = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(bShowPending){
                TextDrawable drawable = TextDrawable.builder()
                        .buildRound(String.format("%d", DBManager.getAllPendingCount()), ContextCompat.getColor(getActivity(),R.color.AppGreen));
                viewBadge.setImageDrawable(drawable);
                reloadData();
            }
        }
    };
    public void reloadData(){}

    public void showSettingItems(View view) {
        bShowSetting = true;
        TextView _txtClose = (TextView)view.findViewById(R.id.toolbar_txt_close);
        _txtClose.setVisibility(View.GONE);
        ImageView _ivAppMode = (ImageView)view.findViewById(R.id.toolbar_iv_app);
        ImageView _ivSetting = (ImageView)view.findViewById(R.id.toolbar_iv_setting);
        _ivSetting.setVisibility(View.VISIBLE);
        _ivAppMode.setVisibility(View.VISIBLE);
        _ivSetting.setOnClickListener(this);
        String _strHighSuffix = "d";
        if(GlobInfo.isOnline()){
            _strHighSuffix = "h";
        }
        if(GlobInfo.getIsUseApp()){
            _strHighSuffix = "icon_app_" + _strHighSuffix;
        }
        else{
            _strHighSuffix = "icon_browser_" + _strHighSuffix;
        }
        _ivAppMode.setImageResource(Constants.getImageId(getActivity(),_strHighSuffix));

    }

    public void setCustomTitle(View view,String aTitle){
        lbTitle = (TextView)view.findViewById(R.id.toolbar_txt_title);
        if(lbTitle != null){
            lbTitle.setText(aTitle);
        }
        lbTitle.setOnClickListener(this);
    }

    public void showPendingButton(View view){
        bShowPending = true;
        LinearLayout _backLayout = (LinearLayout)view.findViewById(R.id.toolbar_btn_back);
        _backLayout.setVisibility(View.GONE);
        viewBadge = (ImageView)view.findViewById(R.id.toolbar_iv_badge);
        viewBadge.setVisibility(View.VISIBLE);

        TextDrawable drawable = TextDrawable.builder()
                .buildRound(String.format("%d", DBManager.getAllPendingCount()), ContextCompat.getColor(getActivity(),R.color.AppGreen));
        viewBadge.setImageDrawable(drawable);
        viewBadge.setOnClickListener(this);
    }

    public void showBackButton(View view) {
        LinearLayout _backLayout = (LinearLayout)view.findViewById(R.id.toolbar_btn_back);
        _backLayout.setVisibility(View.VISIBLE);
        _backLayout.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.toolbar_iv_badge){
            onTapBadge();
        }
        else if(v.getId() == R.id.toolbar_iv_setting){
            onTapSetting();
        }
        else if(v.getId() == R.id.toolbar_btn_back){
            onBackButton();
        }
        else if(v.getId() == R.id.toolbar_txt_title){
            onTapTitle();
        }
    }

    @Override
    public void onBackButton() {
        if(mainFragment != null)
            mainFragment.popFragment();
        getActivity().onBackPressed();
    }

    void onTapSetting(){
        Intent _intentSetting = new Intent(getActivity(), SettingActivity.class);
        //startActivityForResult(_intentSetting);
        startActivityForResult(_intentSetting,0);
    }

    void onTapBadge(){
        gotoFragment(new PendingListFragment());
    }

    public void onTapTitle(){

    }

    public void gotoFragment(BaseFragment aFragment){
        if(mainFragment != null)
            mainFragment.pushFragment(aFragment);
        aFragment.mainFragment = mainFragment;
        FragmentTransaction _ft = getActivity().getSupportFragmentManager().beginTransaction(); //getChildFragmentManager().beginTransaction(); //getActivity().getSupportFragmentManager().beginTransaction();
        _ft.setCustomAnimations(R.anim.anim_slide_in_left, R.anim.anim_slide_out_left,R.anim.anim_slide_in_right,R.anim.anim_slide_out_right);
        _ft.add(R.id.fragment_tab1_mainframe,aFragment).addToBackStack("");
        _ft.commit();
    }

    @Override
    public void onDestroy() {
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mReceiverPendingChanged);
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mReceiverWifiAndAppMode);
        super.onDestroy();
    }
}
