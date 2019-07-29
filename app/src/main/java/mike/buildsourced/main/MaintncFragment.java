package mike.buildsourced.main;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.kyleduo.switchbutton.SwitchButton;

import mike.buildsourced.BaseFragment;
import mike.buildsourced.R;
import mike.buildsourced.common.database.model.Asset;

public class MaintncFragment extends BaseFragment {

    private OnUpdateMaintncListener mListener;
    private Asset curAsset;

    SwitchButton swMaintnceFlag;
    EditText txtNotes;

    public void setUpdateMaintncListener(OnUpdateMaintncListener listener){
        mListener = listener;
    }
    public void setAsset(Asset aAsset){curAsset = aAsset;}

    public MaintncFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE|WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View _viewMe = inflater.inflate(R.layout.fragment_maintnc, container, false);
        TextView _txtTitle = (TextView)_viewMe.findViewById(R.id.fragment_maintnc_txt_title);
        _txtTitle.setText(curAsset.assetName);
        swMaintnceFlag = (SwitchButton)_viewMe.findViewById(R.id.fragment_maintnc_switch_maintnc_required);
        txtNotes = (EditText)_viewMe.findViewById(R.id.fragment_maintnc_edit_maintnc_notes);
        swMaintnceFlag.setChecked(curAsset.maintenanceFlag);
        txtNotes.setText(curAsset.lastNotesEntry);
        View _btnUpdate = _viewMe.findViewById(R.id.fragment_maintnc_btn_update);
        _btnUpdate.setOnClickListener(this);
        View _btnCancel = _viewMe.findViewById(R.id.fragment_maintnc_btn_cancel);
        _btnCancel.setOnClickListener(this);

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
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        if(v.getId() == R.id.fragment_maintnc_btn_update){
            onTapUpdate();
        }
        else if(v.getId() == R.id.fragment_maintnc_btn_cancel){
            onTapCancel();
        }
    }

   void onTapUpdate() {
        if(swMaintnceFlag.isChecked() != curAsset.maintenanceFlag){
            mListener.onUpdateFlag(swMaintnceFlag.isChecked());
        }
        String _strNotes = txtNotes.getText().toString();
        if(curAsset.lastNotesEntry.compareTo(_strNotes) != 0){
            mListener.onUpdateNotes(_strNotes);
        }
       onBackButton();
    }

   void onTapCancel() {
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

    public interface OnUpdateMaintncListener {
        // TODO: Update argument type and name
        void onUpdateFlag(Boolean flag);
        void onUpdateNotes(String notes);
    }

}
