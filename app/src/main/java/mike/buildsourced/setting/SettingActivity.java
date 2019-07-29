package mike.buildsourced.setting;

import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.TextAppearanceSpan;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;

import mike.buildsourced.BaseActivity;
import mike.buildsourced.R;
import mike.buildsourced.common.GlobInfo;
import mike.buildsourced.common.MDToast;
import mike.buildsourced.common.database.DBManager;
import mike.buildsourced.common.database.UpdateHandler;
import mike.buildsourced.common.network.APIManager;
import mike.buildsourced.common.network.response.APIResponse;
import mike.buildsourced.common.network.ApiCallBack;
import mike.buildsourced.common.network.response.LoginResponse;
import mike.buildsourced.common.network.response.UpdateSinceResponse;

public class SettingActivity extends BaseActivity implements View.OnClickListener{

    TextView txtHideCredentials,txtCredential,txtError;
    EditText txtEmail,txtPasswd;
    ImageView ivUseApp,ivUseBrowser;
    TextView txtUseApp,txtUseBrowser;
    LinearLayout viewUseApp, viewUseBrowser,viewCredential;
    Boolean bCollapse = false;
    Boolean bPrevAppMode;
    Button btnUpdate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        initClass();
    }

    void initClass(){
        initUI();
        if(GlobInfo.getIsSignedIn()){
            txtEmail.setText(GlobInfo.getUserEmail());
            txtPasswd.setText(GlobInfo.getUserPasswd());
            bCollapse = true;
            collapseCredential();
        }
        else{
            btnUpdate.setText("Login");
            txtHideCredentials.setVisibility(View.GONE);
            txtCredential.setText("Credential Information");
        }
        bPrevAppMode = GlobInfo.getIsUseApp();
        changeUseMethod(GlobInfo.getIsUseApp());

    }

    void setActionBar(boolean aSigend){
        TextView _txtTitle = (TextView)findViewById(R.id.toolbar_txt_title);
        TextView _txtClose = (TextView)findViewById(R.id.toolbar_txt_close);
        LinearLayout _backLayout = (LinearLayout)findViewById(R.id.toolbar_btn_back);
        _backLayout.setVisibility(View.GONE);

        if(!aSigend){
            _txtTitle.setText("Login");
            _txtClose.setVisibility(View.GONE);
        }
        else{
            _txtTitle.setText("Settings");
            ImageView _ivBadge = (ImageView)findViewById(R.id.toolbar_iv_badge);
            _ivBadge.setVisibility(View.VISIBLE);
            TextDrawable drawable = TextDrawable.builder()
                    .buildRound(String.format("%d", DBManager.getAllPendingCount()), ContextCompat.getColor(this,R.color.AppGreen));
            _ivBadge.setImageDrawable(drawable);
            _txtClose.setVisibility(View.VISIBLE);
            _txtClose.setOnClickListener(this);
        }
    }

    void initUI(){
        setActionBar(GlobInfo.getIsSignedIn());
        btnUpdate = (Button)findViewById(R.id.activity_setting_btn_login);
        btnUpdate.setOnClickListener(this);

        txtHideCredentials = (TextView)findViewById(R.id.activity_setting_txt_hidecredential);
        txtHideCredentials.setOnClickListener(this);

        View _txtClose = findViewById(R.id.toolbar_txt_close);
        _txtClose.setOnClickListener(this);

        ivUseApp = (ImageView)findViewById(R.id.activity_setting_iv_useapp);
        ivUseBrowser = (ImageView)findViewById(R.id.activity_setting_iv_usebrowser);
        txtUseApp = (TextView)findViewById(R.id.activity_setting_txt_useapp);
        txtUseBrowser = (TextView)findViewById(R.id.activity_setting_txt_usebrowser);
        viewUseApp = (LinearLayout)findViewById(R.id.activity_setting_layout_app);
        viewUseBrowser = (LinearLayout)findViewById(R.id.activity_setting_layout_browser);
        viewUseApp.setOnClickListener(this);
        viewUseBrowser.setOnClickListener(this);
        viewCredential = (LinearLayout)findViewById(R.id.activity_setting_credentials);
        txtCredential = (TextView)findViewById(R.id.activity_setting_txt_updatecredential);
        String _strMed = "Use App";
        String _strSmall = "\n" +
                "\nFor faster Inventory\n             Offline mode";
        SpannableString _spanMed = new SpannableString(_strMed);
        _spanMed.setSpan(new TextAppearanceSpan(this,R.style.MediumDescription),0,_strMed.length(),0);
        SpannableString _spanSmall = new SpannableString(_strSmall);
        _spanSmall.setSpan(new TextAppearanceSpan(this,R.style.SmallDescription),0,_strSmall.length(),0);
        txtUseApp.append(_spanMed);
        txtUseApp.append(_spanSmall);

        _strMed = "Use Browser";
        _strSmall = "\n" +
                "\nFull Functionality\n         Online mode";
        _spanMed = new SpannableString(_strMed);
        _spanMed.setSpan(new TextAppearanceSpan(this,R.style.MediumDescription),0,_strMed.length(),0);
        _spanSmall = new SpannableString(_strSmall);
        _spanSmall.setSpan(new TextAppearanceSpan(this,R.style.SmallDescription),0,_strSmall.length(),0);
        txtUseBrowser.append(_spanMed);
        txtUseBrowser.append(_spanSmall);

        txtEmail = (EditText)findViewById(R.id.activity_setting_edit_username);
        txtPasswd = (EditText)findViewById(R.id.activity_setting_edit_password);
        txtError = (TextView) findViewById(R.id.activity_setting_txt_error);
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.activity_setting_btn_login){
            onTapUpdate();
        }
        else if(v.getId() == R.id.toolbar_txt_close){
            onClose();
        }
        else if(v.getId() == R.id.activity_setting_layout_app){
            changeUseMethod(true);
            GlobInfo.changeAppMode(true);
        }
        else if(v.getId() == R.id.activity_setting_layout_browser){
            changeUseMethod(false);
            GlobInfo.changeAppMode(false);
        }
        else if(v.getId() == R.id.activity_setting_txt_hidecredential){
            bCollapse = !bCollapse;
            collapseCredential();
        }
    }

    void onClose(){
        if(bPrevAppMode != GlobInfo.getIsUseApp())
            setResult(1);
        else
            setResult(0);
        hudProgress.dismiss();
        finish();
    }

    @Override
    public void onBackPressed() {
        if(bPrevAppMode != GlobInfo.getIsUseApp())
            setResult(1);
        else
            setResult(0);
        super.onBackPressed();

    }

    void onTapUpdate() {
        if (txtEmail.getText().length() == 0 || txtPasswd.getText().length() == 0){
            txtError.setVisibility(View.VISIBLE);
            txtError.setText("Please input your email and password.");
            return;
        }
        txtError.setVisibility(View.GONE);
        hudProgress.showWithStatus("Syncing");
        final SettingActivity _safeSelf = this;
        APIManager.reqLogin(txtEmail.getText().toString(), txtPasswd.getText().toString(), new ApiCallBack() {
            @Override
            public void onSuccess(APIResponse response) {
                LoginResponse _response = (LoginResponse)response;
                //hudProgress.dismiss();
                GlobInfo.setUserEmail(_safeSelf.txtEmail.getText().toString());
                GlobInfo.setUserPasswd(_safeSelf.txtPasswd.getText().toString());
                MDToast.makeSuccessText(_safeSelf,"Login Success! Syncing data...");
                hudProgress.showInfoWithStatus("Syncing");

                APIManager.reqInitialUpdate(new ApiCallBack() {
                    @Override
                    public void onSuccess(APIResponse response) {
                        GlobInfo.isFirstLoaded = false;
                        DBManager.initDB();
                        DBManager.syncData((UpdateSinceResponse) response, new UpdateHandler() {
                            @Override
                            public void onCompletion(Boolean bSuccess) {
                                hudProgress.dismiss();
                                GlobInfo.gotoMainView(_safeSelf);
                                GlobInfo.setIsSignedIn(true);
                                finish();
                            }
                        });

                    }

                    @Override
                    public void onFailure(String error) {
                        hudProgress.dismiss();
                        MDToast.makeFailedText(_safeSelf,error);
                    }
                });

            }

            @Override
            public void onFailure(String error) {
                hudProgress.dismiss();
                //_safeSelf.view.isUserInteractionEnabled = true;
                _safeSelf.txtError.setVisibility(View.VISIBLE);
                _safeSelf.txtError.setText(error);
            }
        });

    }

    void changeUseMethod(Boolean bApp){
        int _nAppColor,_nBrowserColor;
        if(bApp){
            _nAppColor = ContextCompat.getColor(this,R.color.AppBlue);
            _nBrowserColor = Color.WHITE;
            viewUseApp.setBackgroundColor(_nAppColor);
            viewUseBrowser.setBackgroundColor(Color.WHITE);
            ivUseApp.setImageResource(R.drawable.icon_app_white);
            ivUseBrowser.setImageResource(R.drawable.icon_browser_blue);
        }
        else{
            _nBrowserColor = ContextCompat.getColor(this,R.color.AppBlue);
            _nAppColor = Color.WHITE;
            viewUseBrowser.setBackgroundColor(_nBrowserColor);
            viewUseApp.setBackgroundColor(Color.WHITE);
            ivUseApp.setImageResource(R.drawable.icon_app_blue);
            ivUseBrowser.setImageResource(R.drawable.icon_browser_white);
        }

        txtUseApp.setTextColor(_nBrowserColor);
        txtUseBrowser.setTextColor(_nAppColor);
        GlobInfo.notifyAppModeChanged();

    }
    void collapseCredential(){
        if(bCollapse){
            txtHideCredentials.setText("▼");
            viewCredential.setVisibility(View.GONE);
        }
        else{
            txtHideCredentials.setText("▲");
            viewCredential.setVisibility(View.VISIBLE);
        }

    }
}
