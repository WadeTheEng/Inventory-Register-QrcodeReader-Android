package mike.buildsourced.main;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.Intent;
import android.graphics.PointF;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.os.ResultReceiver;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dlazaro66.qrcodereaderview.QRCodeReaderView;
import com.github.pwittchen.swipe.library.Swipe;
import com.github.pwittchen.swipe.library.SwipeListener;
import mike.buildsourced.BaseFragment;
import mike.buildsourced.R;
import mike.buildsourced.common.Constants;
import mike.buildsourced.common.ContextSingleton;
import mike.buildsourced.common.GlobInfo;
import mike.buildsourced.common.MDToast;
import mike.buildsourced.common.database.DBManager;
import mike.buildsourced.common.database.SyncManager;
import mike.buildsourced.common.database.UpdateHandler;
import mike.buildsourced.common.database.model.Asset;
import mike.buildsourced.common.network.APIManager;
import mike.buildsourced.common.network.ApiCallBack;
import mike.buildsourced.common.network.response.APIResponse;
import mike.buildsourced.common.network.response.TokenResponse;
import mike.buildsourced.common.location.FetchAddressIntentService;
import mike.buildsourced.common.location.GPSTracker;
import mike.buildsourced.main.cameraview.TakePhotoFragment;

public class ScanFragment extends BaseFragment implements
        View.OnClickListener,TakePhotoFragment.OnUpdatePhotoListener,
        MaintncFragment.OnUpdateMaintncListener,QRCodeReaderView.OnQRCodeReadListener {

    public static QRCodeReaderView qrCodeReaderView;
    FrameLayout mViewLayout;
    LinearLayout viewDetail;
    View viewContainer;
    ImageView ivSwipeButton;
    double timeinMS;
    String strPrevCode = "";
    GPSTracker gpsTracker;
    String strAddress ="";
    AddressResultReceiver addressReceiver  = new AddressResultReceiver(new Handler());
    Asset curAsset;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 12;
    private Swipe swipe;
    Boolean bShowDetail;
    public static Boolean bEnableScan; // if the view is in Scan View?

    public ScanFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.

     * @return A new instance of fragment ScanFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ScanFragment newInstance() {
        ScanFragment fragment = new ScanFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        gpsTracker = new GPSTracker(getActivity());
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        swipe = new Swipe();
        swipe.setListener(new SwipeListener() {
            @Override public void onSwipingLeft(final MotionEvent event) {
            }
            @Override public void onSwipedLeft(final MotionEvent event) {
            }
            @Override public void onSwipingRight(final MotionEvent event) {
            }
            @Override public void onSwipedRight(final MotionEvent event) {
            }
            @Override public void onSwipingUp(final MotionEvent event) {
            }
            @Override public void onSwipingDown(final MotionEvent event) {
            }

            @Override public void onSwipedUp(final MotionEvent event) {
                slideUpDown(viewDetail,false);
            }
            @Override public void onSwipedDown(final MotionEvent event) {
                slideUpDown(viewDetail,true);
            }


        });
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View _viewMe =  inflater.inflate(R.layout.fragment_scan, container, false);
        qrCodeReaderView = (QRCodeReaderView)_viewMe.findViewById(R.id.qrdecoderview);
        qrCodeReaderView.setAutofocusInterval(2000L);
        qrCodeReaderView.setOnQRCodeReadListener(this);
        qrCodeReaderView.setBackCamera();
        qrCodeReaderView.startCamera();

        viewDetail = (LinearLayout)_viewMe.findViewById(R.id.fragment_scan_view_detail);
        viewDetail.setVisibility(View.GONE);
        bShowDetail = false;
        Button _btn = (Button)viewDetail.findViewById(R.id.fragment_scan_btn_camera);
        _btn.setOnClickListener(this);
        _btn = (Button)viewDetail.findViewById(R.id.fragment_scan_btn_wrench);
        _btn.setOnClickListener(this);
        _btn = (Button)viewDetail.findViewById(R.id.fragment_scan_btn_detail);
        _btn.setOnClickListener(this);
        _btn = (Button)viewDetail.findViewById(R.id.fragment_scan_btn_close);
        _btn.setOnClickListener(this);

        ivSwipeButton = (ImageView) viewDetail.findViewById(R.id.fragment_scan_iv_swipebtn);
        viewContainer = _viewMe;

        //debug
        /*new Handler().postDelayed(new Runnable() {
            public void run() {
                //GlobInfo.gotoWebHandler("http://www.google.com");
                postDelyedScan();

            }
        }, 5000);*/

        viewDetail.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                swipe.dispatchTouchEvent(event);
                return true;
            }
        });

        return _viewMe;
    }

    @Override
    public void onBackButton() {
        super.onBackButton();

    }

    void postDelyedScan(){
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                handleCode("thisisprodtest");
                //showScannedDetail("");
            }
        });

    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setCustomTitle(getView(),"BuildSourced");
        showPendingButton(view);
        showSettingItems(view);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onDetach() {
        super.onDetach();

    }

    @Override
    public void onResume() {
        super.onResume();
        bEnableScan = true;
        if(bShowDetail){
            viewDetail.setVisibility(View.VISIBLE);
        }
        timeinMS = System.currentTimeMillis();
    }

    @Override
    public void onPause() {
        super.onPause();
        bEnableScan = false;
        if(bShowDetail){
            viewDetail.setVisibility(View.GONE);
        }
    }

    public static void setEnableScan(Boolean aEnable){
        bEnableScan = aEnable;
        if(qrCodeReaderView != null){
            if(bEnableScan){
                qrCodeReaderView.startCamera();
            }
            else{
                qrCodeReaderView.stopCamera();
            }
        }
    }

    boolean isEnableScan(){
        if(bEnableScan){
            return true;
        }
        return false;
    }
    // Called when a QR is decoded
    // "text" : the text encoded in QR
    // "points" : points where QR control points are placed
    @Override public void onQRCodeRead(String text, PointF[] points) {
        //Mike - test to create a photo
        if(System.currentTimeMillis() - timeinMS > 3000 && isEnableScan()){
            timeinMS = System.currentTimeMillis();
            handleCode(text);
        }
    }

    void handleCode(String codes) {

        if(GlobInfo.isSync){
            return;
        }

        //guard strPrevCode.compare(codes) != .orderedSame else{ return}
        strAddress ="";
        strPrevCode = codes;

        /*let locString = "lat: "  + String(format:"%f",(myLocation?.coordinate.latitude)!) +
                " long: " + String(format:"%f",(myLocation?.coordinate.longitude)!) +
                " altitude: " + String(format:"%f",(myLocation?.altitude)!) + "\n"
        // getAddress(latitude: latitude, longitude: longitude)
        curPlaceMark = nil
        getPlacemarkFromLocation(location: myLocation!)*/
        if(!gpsTracker.canGetLocation()){
            gpsTracker.showSettingsAlert();
        }
        startIntentService();//get address
        String _qrUrl  = codes;
        //if the code is a QR code, launch the URL in a browser
        if(_qrUrl.contains("http")) {
            _qrUrl = _qrUrl.replace("http","https");

        } else {
            //if the code is a UPC code, then prepend our FQDN and then launch
            _qrUrl = "https://bsqr1.com/" + codes;
        }

        if(GlobInfo.getIsUseApp()){

            Uri _url = Uri.parse(_qrUrl);
            final String _strRealCodes = _url.getLastPathSegment();

            //print(_strRealCodes)
            if(GlobInfo.isOnline()){
                hudProgress.showWithStatus("Syncing");
                APIManager.reqGetAssetWithTokenId(_strRealCodes, new ApiCallBack() {
                    @Override
                    public void onSuccess(APIResponse response) {
                        hudProgress.dismiss();
                        TokenResponse _tokenResponse = (TokenResponse)response;
                        if(_tokenResponse.asset_detail != null){
                            DBManager.updateAsset(_tokenResponse.asset_detail);
                            showScannedDetail(_strRealCodes);
                        }
                        else{
                            showScannedDetail(_strRealCodes);
                        }
                    }

                    @Override
                    public void onFailure(String error) {
                        hudProgress.dismiss();
                        String _strErrorMessage = "Can't load asset data!";
                        if(error != null)
                            _strErrorMessage = _strErrorMessage + " Reason:" + error;
                        MDToast.makeFailedText(ContextSingleton.getContext(),_strErrorMessage);
                    }
                });
            }
            else{
                showScannedDetail(_strRealCodes);
            }

        }
        else{
            //let _vcSafari = SFSafariViewController(url: _url!)
            //self.present(_vcSafari, animated: true, completion: nil)
            GlobInfo.gotoWebHandler(_qrUrl);
        }

    }

    protected void startIntentService() {
        Intent intent = new Intent(getActivity(), FetchAddressIntentService.class);
        intent.putExtra(Constants.RECEIVER, addressReceiver);
        intent.putExtra(Constants.LOCATION_DATA_EXTRA, gpsTracker.getLocation());
        getActivity().startService(intent);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        if(v.getId() == R.id.fragment_scan_btn_camera){
            onTapCapture();
        }
        else if(v.getId() == R.id.fragment_scan_btn_wrench){
            onTapWrench();
        }
        else if(v.getId() == R.id.fragment_scan_btn_detail){
            onTapSeeMore();
        }
        else if(v.getId() == R.id.fragment_scan_btn_close){
            slideUpDown(viewDetail,true,true);
        }
    }

    void onTapCapture(){
       // mScannerView.stopCamera();
        TakePhotoFragment _fragment = new TakePhotoFragment();
        _fragment.setUpdatePhotoListener(this);
        gotoFragment(_fragment);
    }

    @Override
    public void onUpdatePhoto(String aImgPath) {
        hudProgress.showWithStatus("Syncing");
        SyncManager.addPhoto(aImgPath, curAsset, new UpdateHandler() {
            @Override
            public void onCompletion(Boolean bSuccess) {
                hudProgress.dismiss();
                if(bSuccess){
                    MDToast.makeSuccessText(getActivity(),"Image Update Successful");
                }
                else{
                    MDToast.makeFailedText(getActivity(),"Image added to pending updates");
                }
            }
        });
    }

    void onTapWrench(){
        MaintncFragment _fragment = new MaintncFragment();
        _fragment.setUpdateMaintncListener(this);
        _fragment.setAsset(curAsset);
        gotoFragment(_fragment);
    }

    @Override
    public void onUpdateFlag(Boolean flag) {
        hudProgress.showWithStatus("Syncing");
        SyncManager.updateMaintncFlag(flag, curAsset, new UpdateHandler() {
            @Override
            public void onCompletion(Boolean bSuccess) {
                hudProgress.dismiss();
                if(bSuccess){
                    MDToast.makeSuccessText(getActivity(),"Maintenance Update Successful");
                }
                else{
                    MDToast.makeFailedText(getActivity(),"Maintenance Update added to pending updates");
                }
            }
        });
    }

    @Override
    public void onUpdateNotes(String notes) {
        hudProgress.showWithStatus("Syncing");
        SyncManager.updateMaintncNotes(notes, curAsset, new UpdateHandler() {
            @Override
            public void onCompletion(Boolean bSuccess) {
                hudProgress.dismiss();
                if(bSuccess){
                    MDToast.makeSuccessText(getActivity(),"Maintenance Update Successful");
                }
                else{
                    MDToast.makeFailedText(getActivity(),"Maintenance Update added to pending updates");
                }
            }
        });
    }

    void onTapSeeMore() {
        ItemDetailFragment _fragment = new ItemDetailFragment();
        _fragment.setAsset(curAsset);
        gotoFragment(_fragment);
    }
    void showScannedDetail(String aCodes){
        DBManager.getAll();
        curAsset = DBManager.getAsset(aCodes);
        if(curAsset != null){
            TextView _txtName = (TextView)viewContainer.findViewById(R.id.fragment_scan_txt_title);
            TextView _txtAddress = (TextView)viewContainer.findViewById(R.id.fragment_scan_txt_address);
            _txtName.setText(curAsset.assetName);
            String  _latLng = String.format("Lat:%f, Lng:%f",gpsTracker.getLatitude(),gpsTracker.getLongitude());
            if(strAddress.isEmpty()){
                strAddress = _latLng;
            }
            else{
                strAddress = strAddress + "\n" + _latLng;
            }
            _txtAddress.setText(strAddress);
            //"Approximate Address: 2045 Lincoln Highway, Edison, NJ\nLat:40.5242, Lng:-74.389"
            ivSwipeButton.setVisibility(View.GONE);
            //location update
            SyncManager.updateLat(gpsTracker.getLatitude(), curAsset);
            SyncManager.updateLng(gpsTracker.getLongitude(), curAsset);
            slideFromRight(viewDetail);
        }
        else{
            MDToast.makeFailedText(getActivity(), "Can't find the assets.");
        }

    }

    private void slideFromRight(final View childView){
        childView.setTranslationY(0);
        childView.setTranslationX(viewContainer.getWidth());
        childView.animate()
                .translationXBy(-viewContainer.getWidth())
                .setDuration(500)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        childView.setVisibility(View.VISIBLE);
                        bShowDetail = true;
                    }
                });
    }
    private void slideUpDown(final View childView,final Boolean bDown) {
        slideUpDown(childView,bDown,false);
    }

    private void slideUpDown(final View childView,final Boolean bDown,final Boolean bHide){
        //float _tabheight = getResources().getDimensionPixelSize(R.dimen.tab_height);
        if(!bDown && ivSwipeButton.getVisibility() == View.GONE)
            return;
        if(bDown && ivSwipeButton.getVisibility() == View.VISIBLE)
            return;

        int _nDelta = 0;
        _nDelta = childView.getHeight();
        if(!bDown){
            _nDelta = -1 * (childView.getHeight() - ivSwipeButton.getHeight());
        }

        childView.animate()
                .translationYBy(_nDelta)
                .setDuration(500)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        childView.setVisibility(View.VISIBLE);
                        bShowDetail = true;
                    }
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        if(bDown)
                            ivSwipeButton.setVisibility(View.VISIBLE);
                        else
                            ivSwipeButton.setVisibility(View.GONE);
                        if(bHide) {
                            childView.setVisibility(View.GONE);
                            bShowDetail = false;
                        }
                    }

                });
    }

    class AddressResultReceiver extends ResultReceiver {
        public AddressResultReceiver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {

            // Display the address string
            // or an error message sent from the intent service.
            strAddress = resultData.getString(Constants.RESULT_DATA_KEY);

            // Show a toast message if an address was found.
            if (resultCode == Constants.SUCCESS_RESULT) {
                //showToast(getString(R.string.address_found));
            }

        }
    }


}
