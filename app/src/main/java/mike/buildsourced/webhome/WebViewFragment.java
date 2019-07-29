package mike.buildsourced.webhome;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.SslErrorHandler;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import mike.buildsourced.BaseFragment;
import mike.buildsourced.R;
import mike.buildsourced.common.Constants;
import mike.buildsourced.common.GlobInfo;
import mike.buildsourced.common.MDToast;


public class WebViewFragment extends BaseFragment {
    String strURL = "firstTimeThrough";
    WebView webView;

    public WebViewFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View _viewMe =  inflater.inflate(R.layout.fragment_web_view, container, false);

        WebView _webView = (WebView)_viewMe.findViewById(R.id.fragment_webview_webcontent);
        _webView.getSettings().setJavaScriptEnabled(true);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {

            _webView.getSettings().setAllowFileAccess(true);
            _webView.getSettings().setAllowContentAccess(true);
            _webView.getSettings().setAllowFileAccessFromFileURLs(true);
            _webView.getSettings().setAllowUniversalAccessFromFileURLs(true);
        }
        _webView.getSettings().setLoadWithOverviewMode(true);
        _webView.getSettings().setUseWideViewPort(true);

        MyWebClient _client =  new MyWebClient();
        _client.wvFragment = this;
        _webView.setWebViewClient(_client);
        _webView.setWebChromeClient(new WebChromeClient());
        webView = _webView;
        loadContent();
        return _viewMe;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setCustomTitle(getView(),"Home");
        showSettingItems(view);

    }

    public void loadContent(){
        if(!strURL.contains("http")) {
            webView.loadUrl(GlobInfo.URL_Server);//debug
            //loadMyUrl(GlobInfo.URL_Server);
        } else if (strURL.contains("buildsourced.com")) {
            return;
        }
        else {
            String ALLOWED_URI_CHARS = "@#&=*+-_.,:!?()/~'%";
            strURL =  Uri.encode(strURL, ALLOWED_URI_CHARS);
            webView.stopLoading();//debug
            webView.loadUrl(strURL);//debug
            //loadMyUrl(strURL);
        }
    }


    public void handleUrl(String aUrl){
        //SVProgressHUD.dismiss()
        strURL = aUrl;
        loadContent();
    }

    private class MyWebClient extends WebViewClient
    {

        WebViewFragment wvFragment;

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            // TODO Auto-generated method stub
            wvFragment.hudProgress.show();
            super.onPageStarted(view, url, favicon);
        }

        @Override
        public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
            super.onReceivedError(view, request, error);
            wvFragment.hudProgress.dismiss();
            MDToast.makeFailedText(GlobInfo.mainTabActivity,error.toString());
            webView.loadUrl(strURL);
        }
/*
        @Override
        public void onReceivedHttpError(WebView view, WebResourceRequest request, WebResourceResponse errorResponse) {
            super.onReceivedHttpError(view, request, errorResponse);
            wvFragment.hudProgress.dismiss();
            MDToast.makeFailedText(GlobInfo.mainTabActivity,errorResponse.toString());
        }

        @Override
        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
            super.onReceivedSslError(view, handler, error);
            wvFragment.hudProgress.dismiss();
            MDToast.makeFailedText(GlobInfo.mainTabActivity,error.toString());
        }*/

        /*
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            // TODO Auto-generated method stub

            if(url == "https://www.buildsourced.com/users/sign_in") {

                //first grab the user name, pwd and the Remember Me checkbox
                String username;
                String password;
                String checked ;
                view.evaluateJavascript("document.getElementById('user_email').value", this);
                view.evaluateJavascript("document.getElementById('user_password').value", this);
                view.evaluateJavascript("document.getElementById('user_remember_me').checked",this);

                print("username: " + username!)
                print("password: " + password!)
                print("checked: " + checked!)

                //now if the user has checked the Remember me box, we want to save their information
                //if((checked!.contains("true")) && username!.contains("@") && !password!.isEmpty) {
                if((checked!.contains("true"))) {
                    print("we'll want to save these parameters " + username! + " password: " + password!)

                    // Setting user name, password and checkbox settings to safe store them
                    let defaults = UserDefaults.standard

                    defaults.set(username, forKey: keyUserEmail)
                    defaults.set(password, forKey: keyUserPasswd)
                    defaults.set("true", forKey: "checkbox")


                } else {
                    // Setting user name, password and checkbox settings to safe store them
                    let defaults = UserDefaults.standard
                    //this function gets called twice - once when entering the page and once upon leaving
                    //the first time through the username and password will already be empty
                    //we don't want to remove valid info in the safe store on the first time through
                    if(!(username?.isEmpty)! && !(password?.isEmpty)!) {
                        defaults.set("", forKey: keyUserEmail)
                        defaults.set("", forKey: keyUserPasswd)
                        defaults.set("false", forKey: "checkbox")
                    }
                    print("we'll not want to save these parameters")
                }
            }
            return true;
        }*/
        /*
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {

            // let urlScheme = request.url?.scheme
            //print("in webView: " + (request.url.absoluteString)!);
            String _strAbsUrl =  request.getUrl().toString();
            return super.shouldOverrideUrlLoading(view, request);
        }*/

        @Override
        public void onPageFinished(WebView view, String url) {
            // TODO Auto-generated method stub
            super.onPageFinished(view, url);
            wvFragment.hudProgress.dismiss();
            strURL = url;
            //wvFragment.handleUrl(url);
            if(url.compareTo("https://www.buildsourced.com/users/sign_in") == 0 ) {
                //first figure out whether the "Remember Me" flag was set last time
                SharedPreferences preferences = getActivity().getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                //editor.putBoolean(keySignIn, aValue);
                //grab the stored checkbox
                String checkbox = preferences.getString("checkbox","");

                //does the checkbox key exist?
                if(isKeyPresentInUserDefaults("checkbox")) {
                    //if the checkbox was turned on, then read in the variables from safe store and set checkbox
                    if(checkbox.contains("true")) {
                        String savedUsername = GlobInfo.getUserEmail();
                        String savedPassword = GlobInfo.getUserPasswd();

                        if(isKeyPresentInUserDefaults(GlobInfo.keyUserEmail) && isKeyPresentInUserDefaults(GlobInfo.keyUserPasswd)) {
                            String fillForm = String.format("document.getElementById('user_email').value = '%s';document.getElementById('user_password').value = '%s';",savedUsername,savedPassword);
                            view.evaluateJavascript(fillForm, new ValueCallback<String>() {
                                @Override
                                public void onReceiveValue(String value) {

                                }
                            });

                            view.evaluateJavascript("document.getElementById('user_remember_me').checked = true;", new ValueCallback<String>() {
                                @Override
                                public void onReceiveValue(String value) {

                                }
                            });

                        }
                    } else { //if the check box is turned off, set fields to blank and checkbox to false
                        String fillForm = String.format("document.getElementById('user_email').value = '';document.getElementById('user_password').value = '';");

                        view.evaluateJavascript(fillForm, new ValueCallback<String>() {
                            @Override
                            public void onReceiveValue(String value) {

                            }
                        });

                        view.evaluateJavascript("document.getElementById('user_remember_me').checked = false;", new ValueCallback<String>() {
                            @Override
                            public void onReceiveValue(String value) {

                            }
                        });
                    }//if the user did not save uname and pwd
                }//if checkbox is empty
            }//if webview request matches buildsourced.com

        }

        Boolean isKeyPresentInUserDefaults(String key){
            SharedPreferences preferences = getActivity().getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
            return preferences.getString(key,"").compareTo("") != 0;
        }

    }



    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

}
