package mike.buildsourced.pending.detail;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.kyleduo.switchbutton.SwitchButton;
import com.squareup.picasso.Picasso;

import mike.buildsourced.BaseFragment;
import mike.buildsourced.R;
import mike.buildsourced.common.database.DBManager;
import mike.buildsourced.common.database.model.Asset;
import mike.buildsourced.common.database.model.Pending;

public class PendingDetailFragment extends BaseFragment {

    private Pending curPending;

    public PendingDetailFragment() {
        // Required empty public constructor
    }

    public void setPending(Pending aPending){curPending = aPending;}


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View _viewMe = inflater.inflate(R.layout.fragment_pending_detail, container, false);
        TextView _txtAny = (TextView)_viewMe.findViewById(R.id.fragment_pending_detail_txt_title);
        _txtAny.setText(curPending.getPendingname());
        _txtAny = (TextView)_viewMe.findViewById(R.id.fragment_pending_detail_txt_date);
        _txtAny.setText(curPending.getPendDate());

        Asset _parentAsset = DBManager.getAssetWithId(curPending.parent_id);
        _txtAny = (TextView)_viewMe.findViewById(R.id.fragment_pending_detail_txt_assetname);
        _txtAny.setText(_parentAsset.assetName);

        _txtAny = (TextView)_viewMe.findViewById(R.id.fragment_pending_detail_txt_assetid);
        _txtAny.setText(String.format("Asset ID: %d",_parentAsset.assetId));

        switch (curPending.pendKind) {
            case 0:case 1:
            {
                View _viewAny = _viewMe.findViewById(R.id.fragment_pending_detail_view_latlng);
                _viewAny.setVisibility(View.VISIBLE);
                _txtAny = (TextView)_viewMe.findViewById(R.id.fragment_pending_detail_txt_latlng);
                _txtAny.setText(String.format("Lat:%f, Lng:%f",_parentAsset.latitude,_parentAsset.longitude));

            }
                break;
            case 3:case 4:
            {
                View _viewAny = _viewMe.findViewById(R.id.fragment_pending_detail_view_maintnc);
                _viewAny.setVisibility(View.VISIBLE);
                SwitchButton _switch = (SwitchButton)_viewMe.findViewById(R.id.fragment_pending_detail_switch_maintnc_required);
                _switch.setChecked(_parentAsset.maintenanceFlag);
                _switch.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        return true;
                    }
                });
                _txtAny = (TextView)_viewMe.findViewById(R.id.fragment_pending_detail_txt_maintnc_notes);
                _txtAny.setText(_parentAsset.lastNotesEntry);
            }
                break;
            default:
            {
                View _viewAny = _viewMe.findViewById(R.id.fragment_pending_detail_view_image);
                _viewAny.setVisibility(View.VISIBLE);
                ImageView _ivImage = (ImageView)_viewMe.findViewById(R.id.fragment_pending_detail_iv_image);
                String _strPhotoPath = "file://" + curPending.photoPath;
                Picasso.with(getActivity())
                        .load(_strPhotoPath)
                        .into(_ivImage);
            }
        }

        return _viewMe;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setCustomTitle(getView(),"Pending");
        showBackButton(view);
        showSettingItems(view);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onDetach() {
        super.onDetach();
        curPending = null;
    }

}
