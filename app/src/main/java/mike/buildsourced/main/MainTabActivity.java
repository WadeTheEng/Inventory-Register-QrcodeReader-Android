package mike.buildsourced.main;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import mike.buildsourced.BaseActivity;
import mike.buildsourced.R;
import mike.buildsourced.common.ContextSingleton;
import mike.buildsourced.common.GlobInfo;
import mike.buildsourced.help.HelpFragment;
import mike.buildsourced.webhome.WebViewFragment;

public class MainTabActivity extends BaseActivity implements View.OnClickListener{

    public static int TAB0 = 0;
    public static int TAB1 = 1;
    public static int TAB2 = 2;

    int nSelectedTab = -1;
    String strShouldOpenUrl = "";
    Tab1Fragment fragTab1;
    ViewPagerAdapter pageAdapter;
    ViewPager viewPager;

    private class ViewPagerAdapter extends FragmentPagerAdapter{

        List<Fragment> arrFragments = new ArrayList<>();
        public ViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        public void setTabMode(){

            if(arrFragments.size() == 0){
                if(GlobInfo.getIsUseApp()){
                    arrFragments.add(new Tab1Fragment());
                    arrFragments.add(new HelpFragment());
                }
                else{
                    arrFragments.add(new Tab1Fragment());
                    arrFragments.add(new WebViewFragment());
                    arrFragments.add(new HelpFragment());
                }
            }
            else{
                if(GlobInfo.getIsUseApp()){
                    if(arrFragments.size() == 3){
                        arrFragments.remove(1);
                    }
                }
                else{
                    if(arrFragments.size() == 2){
                        arrFragments.add(1,new WebViewFragment());
                    }
                }
            }
        }

        @Override
        public Fragment getItem(int position) {
            return arrFragments.get(position);
        }

        @Override
        public int getCount() {
            return arrFragments.size();
        }


    }


    private BroadcastReceiver mReceiverAppMode = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //setTabMode();
        }
    };

    public void showSync(){
        hudProgress.showWithStatus("Syncing");
    }

    public void dismissSync(){
        hudProgress.dismissImmediately();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ContextSingleton.setContext(this);
        GlobInfo.mainTabActivity = this;
        setContentView(R.layout.activity_main_tab);
        LinearLayout _layoutTab =(LinearLayout)findViewById(R.id.main_tab_tab0);
        _layoutTab.setOnClickListener(this);
        _layoutTab =(LinearLayout)findViewById(R.id.main_tab_tab1);
        _layoutTab.setOnClickListener(this);
        _layoutTab =(LinearLayout)findViewById(R.id.main_tab_tab2);
        _layoutTab.setOnClickListener(this);
        LocalBroadcastManager.getInstance(this).registerReceiver(mReceiverAppMode,
                new IntentFilter(GlobInfo.Notify_APPMODECHANGED));
        viewPager = (ViewPager) findViewById(R.id.main_tab_viewpager);
        pageAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(pageAdapter);
        viewPager.setOffscreenPageLimit(3);
        setTabMode();
        viewPager.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                return false;
            }


        });
        if(GlobInfo.isFirstLoaded){
            GlobInfo.loadInitiAssets();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == 1) {
            finish();
            GlobInfo.gotoMainView(ContextSingleton.getContext());
            //setTabMode();
        }
    }

    @Override
    public void onClick(View v) {

        if(v.getId() == R.id.main_tab_tab0){
            gotoTab(TAB0);
        }

        if(v.getId() == R.id.main_tab_tab1){
            gotoTab(TAB1);
        }

        if(v.getId() == R.id.main_tab_tab2){
            gotoTab(TAB2);
        }

    }

    void setTabMode(){
        pageAdapter.setTabMode();
        pageAdapter.notifyDataSetChanged();
        if(GlobInfo.getIsUseApp()){
            LinearLayout _layoutTab =(LinearLayout)findViewById(R.id.main_tab_tab1);
            _layoutTab.setVisibility(View.GONE);
            gotoTab(TAB0);
        }
        else{
            LinearLayout _layoutTab =(LinearLayout)findViewById(R.id.main_tab_tab1);
            _layoutTab.setVisibility(View.VISIBLE);
            gotoTab(TAB1);
        }

    }

    public void handleUrl(String strUrl){
        strShouldOpenUrl = strUrl;
        gotoTab(TAB1);
    }

    public void gotoTab(int index){

        if(index == nSelectedTab) {
            if(index == 1){//debug
                WebViewFragment webviewFragment = (WebViewFragment)pageAdapter.getItem(1);
                if(strShouldOpenUrl != "") {
                    webviewFragment.handleUrl(strShouldOpenUrl);
                    strShouldOpenUrl = "";
                }
            }
            return;
        }

        nSelectedTab = index;

        Fragment _fragment;
        View _viewTab;
        ImageView _ivTabIcon;
        TextView _txtTabName;
        FragmentManager _fragmentManager = getSupportFragmentManager();
        int _arrTabViewsIds[] = {R.id.main_tab_tab0,R.id.main_tab_tab1,R.id.main_tab_tab2};
        int _arrTabNameIds[] = {R.id.main_tab_tab0_text,R.id.main_tab_tab1_text,R.id.main_tab_tab2_text};
        int _arrTabIconIds[] = {R.id.main_tab_tab0_icon,R.id.main_tab_tab1_icon,R.id.main_tab_tab2_icon};
        int _arrTabIconImages[] = {R.drawable.icon_scan_d,R.drawable.icon_home_d,R.drawable.icon_help_d};
        for(int i = 0; i < 3; i++){
            _viewTab = findViewById(_arrTabViewsIds[i]);
            _txtTabName = (TextView)findViewById(_arrTabNameIds[i]);
            _ivTabIcon = (ImageView)findViewById(_arrTabIconIds[i]);
            if(_viewTab != null){
                _viewTab.setBackgroundColor(Color.WHITE);
                _txtTabName.setTextColor(ContextCompat.getColor(this,R.color.AppBlue));
                _ivTabIcon.setImageResource(_arrTabIconImages[i]);
            }
        }
        //ScanFragment.bEnableScan = false;
        ScanFragment.setEnableScan(false);
        switch(index){
            case 0: //Tab0
                /*if(fragTab1 == null){
                    fragTab1 = new Tab1Fragment();
                }
                _fragmentManager.beginTransaction().replace(R.id.container,fragTab1).commit();*/
                Tab1Fragment _tab1Fragment = (Tab1Fragment)pageAdapter.getItem(0);
                if(_tab1Fragment.isScanViewAtTop()){
                    ScanFragment.setEnableScan(true);
                }

                viewPager.setCurrentItem(0);
                _viewTab = findViewById(R.id.main_tab_tab0);
                _ivTabIcon = (ImageView)findViewById(R.id.main_tab_tab0_icon);
                _ivTabIcon.setImageResource(R.drawable.icon_scan_h);
                _txtTabName = (TextView)findViewById(R.id.main_tab_tab0_text);
                break;
            case 1://tab1
                WebViewFragment webviewFragment = (WebViewFragment)pageAdapter.getItem(1);
                if(strShouldOpenUrl != "") {
                    webviewFragment.handleUrl(strShouldOpenUrl);
                    strShouldOpenUrl = "";
                }

                viewPager.setCurrentItem(1);
                _viewTab = findViewById(R.id.main_tab_tab1);
                _ivTabIcon = (ImageView)findViewById(R.id.main_tab_tab1_icon);
                _ivTabIcon.setImageResource(R.drawable.icon_home_h);
                _txtTabName = (TextView)findViewById(R.id.main_tab_tab1_text);
                break;
            case 2://tab2
                /*_fragmentManager.beginTransaction().replace(R.id.container,new HelpFragment()).commit();*/
                if(GlobInfo.getIsUseApp())
                    viewPager.setCurrentItem(1);
                else
                    viewPager.setCurrentItem(2);
                _viewTab = findViewById(R.id.main_tab_tab2);
                _ivTabIcon = (ImageView)findViewById(R.id.main_tab_tab2_icon);
                _ivTabIcon.setImageResource(R.drawable.icon_help_h);
                _txtTabName = (TextView)findViewById(R.id.main_tab_tab2_text);
                break;
            default:
                _viewTab = findViewById(R.id.main_tab_tab0);
                _ivTabIcon = (ImageView)findViewById(R.id.main_tab_tab0_icon);
                _txtTabName = (TextView)findViewById(R.id.main_tab_tab1_text);
        }

        _viewTab.setBackgroundColor(ContextCompat.getColor(this,R.color.AppBlue));
        _txtTabName.setTextColor(Color.WHITE);

    }

    /*
    public void gotoFragment(Fragment aFragment){
        FragmentTransaction _ft = getSupportFragmentManager().beginTransaction();
        _ft.setCustomAnimations(R.anim.anim_slide_in_left, R.anim.anim_slide_out_left,R.anim.anim_slide_in_right,R.anim.anim_slide_out_right);
        _ft.replace(R.id.container,aFragment).addToBackStack("");
        _ft.commit();
    }*/

    @Override
    public void onDestroy() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mReceiverAppMode);
        super.onDestroy();
    }



}
