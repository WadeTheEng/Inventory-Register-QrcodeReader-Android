package mike.buildsourced.main.cameraview;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import com.bigkoo.svprogresshud.SVProgressHUD;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import mike.buildsourced.BaseFragment;
import mike.buildsourced.R;
import mike.buildsourced.common.MDToast;
import mike.buildsourced.main.ScanFragment;


public class TakePhotoFragment extends BaseFragment {
    // TODO: Rename parameter arguments, choose names that match

    private OnUpdatePhotoListener mListener;
    String strImagePath;
    View viewCamera, viewPhotoPreview;
    public SVProgressHUD hudProgress;

    public TakePhotoFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    public void setUpdatePhotoListener(OnUpdatePhotoListener listener){
        mListener = listener;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View _viewMe = inflater.inflate(R.layout.fragment_take_photo, container, false);

        viewCamera = _viewMe.findViewById(R.id.fragment_takephoto_takephotoview);
        viewPhotoPreview = _viewMe.findViewById(R.id.fragment_takephoto_savepreview);

        Button captureButton = (Button) _viewMe.findViewById(R.id.fragment_takephoto_btn_takephoto);
        captureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final long startTime = System.currentTimeMillis();
                String _strPath = getOutputMediaFilePath();
                File pictureFile = new File(_strPath);
                if (pictureFile == null) {
                    return;
                }
                ByteArrayOutputStream baos  =ScanFragment.qrCodeReaderView.getImage();

                Bitmap bitmap = BitmapFactory.decodeByteArray(baos.toByteArray(),0, baos.size());
                int screenWidth = 800;//getResources().getDisplayMetrics().widthPixels;
                int screenHeight = 800;//getResources().getDisplayMetrics().heightPixels;
                Bitmap bm;
                int _width = bitmap.getWidth();
                int _height = bitmap.getHeight();
                if(_width > _height){
                    double _fScale = 800.0 / (double)_width;
                    screenHeight = (int)(_height * _fScale);
                    screenWidth = 800;
                }
                else{
                    double _fScale = 800.0 / (double)_height;
                    screenWidth = (int)(_width * _fScale);
                    screenHeight = 800;
                }

                if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                    // Notice that width and height are reversed
                    Bitmap scaled = Bitmap.createScaledBitmap(bitmap,screenWidth ,screenHeight , true);
                    int w = scaled.getWidth();
                    int h = scaled.getHeight();
                    // Setting post rotate to 90
                    Matrix mtx = new Matrix();
                    mtx.postRotate(90);
                    // Rotating Bitmap
                    bm = Bitmap.createBitmap(scaled, 0, 0, w, h, mtx, true);
                }else{// LANDSCAPE MODE
                    //No need to reverse width and height
                    Bitmap scaled = Bitmap.createScaledBitmap(bitmap, screenWidth,screenHeight , true);
                    bm=scaled;
                }

                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bm.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                byte[] byteArray = stream.toByteArray();

                try {
                    FileOutputStream fos = new FileOutputStream(pictureFile);
                    fos.write(byteArray);
                    fos.close();
                    showTakedPicture(_strPath);
                } catch (FileNotFoundException e) {
                    MDToast.makeFailedText(getActivity(), e.getMessage());
                } catch (IOException e) {
                    MDToast.makeFailedText(getActivity(), e.getMessage());
                }
            }
        });

        Button _btnAddImage = (Button)_viewMe.findViewById(R.id.fragment_takephoto_btn_addimage);
        Button _btnRetake = (Button)_viewMe.findViewById(R.id.fragment_takephoto_btn_retake);
        _btnAddImage.setOnClickListener(this);
        _btnRetake.setOnClickListener(this);
        hudProgress =  new SVProgressHUD(getActivity());

        return _viewMe;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setCustomTitle(getView(),"Camera");
        showBackButton(view);
        showSettingItems(view);
    }

    @Override
    public void onBackButton() {
        super.onBackButton();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onPause() {
        //mCamera.stopPreview();
        //mCamera.release();
        super.onPause();
    }

    private static String getOutputMediaFilePath() {
        File mediaStorageDir = new File(
                Environment
                        .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                "MyCameraApp");
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d("BuildSourced", "failed to create directory");
                return null;
            }
        }
        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss")
                .format(new Date());
        File mediaFile;
        String _strPath = mediaStorageDir.getPath() + File.separator
                + "IMG_" + timeStamp + ".jpg";
        return _strPath;

    }

    void showTakedPicture(String aFilePath){
        viewCamera.setVisibility(View.GONE);
        viewPhotoPreview.setVisibility(View.VISIBLE);
        ImageView _image = (ImageView)viewPhotoPreview.findViewById(R.id.fragment_takephoto_iv_preview);
        Picasso.with(getActivity()).load(new File(aFilePath)).into(_image);
        strImagePath = aFilePath;
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        if(v.getId() == R.id.fragment_takephoto_btn_addimage){
            uploadPhoto();

        }
        else if(v.getId() == R.id.fragment_takephoto_btn_retake){
            //mCamera.startPreview();
            viewCamera.setVisibility(View.VISIBLE);
            viewPhotoPreview.setVisibility(View.GONE);
        }
    }

    public void uploadPhoto(){
        mListener.onUpdatePhoto(strImagePath);
        onBackButton();
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnUpdatePhotoListener {
        // TODO: Update argument type and name
        void onUpdatePhoto(String aImgPath);
    }
}
