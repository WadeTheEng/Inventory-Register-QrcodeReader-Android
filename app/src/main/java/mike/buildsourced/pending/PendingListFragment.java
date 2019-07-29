package mike.buildsourced.pending;

import android.content.Context;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import mike.buildsourced.BaseFragment;
import mike.buildsourced.R;
import mike.buildsourced.common.database.DBManager;
import mike.buildsourced.common.database.model.Asset;
import mike.buildsourced.common.database.model.Pending;
import mike.buildsourced.pending.detail.PendingDetailFragment;

public class PendingListFragment extends BaseFragment {

    PendingAdapter pendingAdapter;
    ListView lvContent;
    List<Pending> arrPendings;

    public PendingListFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View _viewMe =  inflater.inflate(R.layout.fragment_pending_list, container, false);
        ImageView _ivAny = (ImageView)_viewMe.findViewById(R.id.fragment_pending_list_iv_current_pending);
        String _strPendingCnt = String.format("%d", DBManager.getAllPendingCount());
        TextDrawable drawable = TextDrawable.builder()
                .buildRound(_strPendingCnt, ContextCompat.getColor(getActivity(),R.color.AppBlue));
        _ivAny.setImageDrawable(drawable);

        lvContent = (ListView)_viewMe.findViewById(R.id.fragment_pending_list_listview);
        arrPendings = DBManager.getAllPending();
        pendingAdapter = new PendingAdapter(getActivity(),arrPendings);
        lvContent.setAdapter(pendingAdapter);
        lvContent.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Pending _aPending = arrPendings.get(position);
                PendingDetailFragment _fragment = new PendingDetailFragment();
                _fragment.setPending(_aPending);
                gotoFragment(_fragment);
            }
        });

        return _viewMe;
    }

    @Override
    public void onTapTitle() {
        super.onTapTitle();
        LayoutInflater layoutInflater = (LayoutInflater)getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        // inflate the custom popup layout
        final View inflatedView = layoutInflater.inflate(R.layout.popup_totalpending, null,false);
        // find the ListView in the popup layout
        ListView listView = (ListView)inflatedView.findViewById(R.id.popup_totalpending_list);
        // get device size
        Display display = getActivity().getWindowManager().getDefaultDisplay();
        final Point size = new Point();
        display.getSize(size);
        int mDeviceHeight = size.y;

        // fill the data to the list items
        List<Asset> _arrAssets = DBManager.getAllAssetsHavePending();
        TotalPendingAdapter _adapter = new TotalPendingAdapter(getActivity(),_arrAssets);
        listView.setAdapter(_adapter);

        // set height depends on the device size
        PopupWindow popWindow = new PopupWindow(inflatedView, size.x - 100,mDeviceHeight/3, true );
        // set a background drawable with rounders corners

        popWindow.setBackgroundDrawable(ContextCompat.getDrawable(getActivity(),R.drawable.maintnc_shape));

        // make it focusable to show the keyboard to enter in `EditText`
        popWindow.setFocusable(true);
        // make it outside touchable to dismiss the popup window
        popWindow.setOutsideTouchable(true);

        // show the popup at bottom of the screen and set some margin at bottom ie,
        popWindow.showAtLocation(getView(), Gravity.TOP, 0,150);

        View container = (View) popWindow.getContentView().getParent();
        WindowManager wm = (WindowManager)getActivity().getSystemService(Context.WINDOW_SERVICE);
        WindowManager.LayoutParams p = (WindowManager.LayoutParams) container.getLayoutParams();
        p.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        p.dimAmount = 0.3f;
        wm.updateViewLayout(container, p);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setCustomTitle(getView(),"Total Pending▼");
        showBackButton(view);
        showSettingItems(view);
    }

    @Override
    public void reloadData() {
        super.reloadData();
        arrPendings = DBManager.getAllPending();
        pendingAdapter = new PendingAdapter(getActivity(),arrPendings);
        lvContent.setAdapter(pendingAdapter);
        pendingAdapter.notifyDataSetChanged();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    public class TotalPendingAdapter extends BaseAdapter {

        private Context mContext;
        private LayoutInflater inflater;
        private List<Asset> arrAssetItems;

        public TotalPendingAdapter(Context context, List<Asset> aItems){
            this.mContext = context;
            this.arrAssetItems = aItems;
            inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return arrAssetItems.size() + 1;
        }

        @Override
        public Object getItem(int location) {
            return arrAssetItems.get(location);
        }

        @Override
        public long getItemId(int position){
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent){
            if(convertView == null){
                convertView = inflater.inflate(R.layout.totalpending_list_low,null);
            }

            String _strTitle,_strPendingCnt;
            if(position == 0)
            {
                _strPendingCnt = String.format("%d", DBManager.getAllPendingCount());
                _strTitle = "Total Pending";
            }
            else{
                final Asset _data = arrAssetItems.get(position - 1);
                _strPendingCnt = String.format("%d", _data.getPendingCount());
                _strTitle = _data.assetName;
            }

            TextView _txtTitle = (TextView)convertView.findViewById(R.id.totalpending_list_txt_title);
            _txtTitle.setText(_strTitle);

            ImageView _ivAny = (ImageView)convertView.findViewById(R.id.totalpending_list_iv_count);
            TextDrawable drawable = TextDrawable.builder()
                    .buildRound(_strPendingCnt, ContextCompat.getColor(getActivity(),R.color.AppBlue));
            _ivAny.setImageDrawable(drawable);
            return convertView;
        }


    }
}
