package mike.buildsourced.main;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.ListViewAutoScrollHelper;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.kyleduo.switchbutton.SwitchButton;
import com.squareup.picasso.Picasso;
import com.yydcdut.sdlv.Menu;
import com.yydcdut.sdlv.MenuItem;
import com.yydcdut.sdlv.SlideAndDragListView;

import java.util.ArrayList;
import java.util.List;

import mike.buildsourced.BaseFragment;
import mike.buildsourced.R;
import mike.buildsourced.common.database.DBManager;
import mike.buildsourced.common.database.model.Asset;
import mike.buildsourced.common.database.model.ImageUrl;
import mike.buildsourced.common.database.model.Pending;
import mike.buildsourced.common.gallery.GalleryNavigator;
import mike.buildsourced.common.gallery.OneFlingGallery;
import mike.buildsourced.pending.PendingAdapter;
import mike.buildsourced.pending.detail.PendingDetailFragment;

public class ItemDetailFragment extends BaseFragment implements SlideAndDragListView.OnDragListener, SlideAndDragListView.OnSlideListener,
        SlideAndDragListView.OnMenuItemClickListener,
        SlideAndDragListView.OnItemDeleteListener,SlideAndDragListView.OnListItemClickListener {

    private Asset curAsset;
    GalleryAdapter mAdapter;

    List<ImageUrl> arrImageUrls;
    private List<Menu> mMenuList;
    SlideAndDragListView<Pending> mListView;
    PendingAdapter pendingAdapter;
    List<Pending> arrPendings;

    public ItemDetailFragment() {
        // Required empty public constructor
    }

    public void setAsset(Asset aAsset){curAsset = aAsset;}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View _viewMe = inflater.inflate(R.layout.fragment_item_detail, container, false);
        TextView _txtAny = (TextView)_viewMe.findViewById(R.id.fragment_item_detail_txt_title);
        _txtAny.setText(curAsset.assetName);
        _txtAny = (TextView)_viewMe.findViewById(R.id.fragment_item_detail_txt_asset_id);
        _txtAny.setText(String.format("Asset ID: %d",curAsset.assetId));
        _txtAny = (TextView)_viewMe.findViewById(R.id.fragment_item_detail_txt_latlng);
        _txtAny.setText(String.format("Lat:%f, Lng:%f",curAsset.latitude,curAsset.longitude));
        _txtAny = (TextView)_viewMe.findViewById(R.id.fragment_item_detail_txt_desc);
        _txtAny.setText(curAsset.assetDescription);
        SwitchButton _switch = (SwitchButton)_viewMe.findViewById(R.id.fragment_item_detail_switch_maintnc_required);
        _switch.setChecked(curAsset.maintenanceFlag);
        _switch.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
        _txtAny = (TextView)_viewMe.findViewById(R.id.fragment_item_detail_txt_maintnc_notes);
        _txtAny.setText(curAsset.lastNotesEntry);
        arrImageUrls = curAsset.getImageUrls();
        initImageGallery(_viewMe);

        ImageView _ivAny = (ImageView)_viewMe.findViewById(R.id.fragment_item_detail_iv_current_pending);
        String _strPendingCnt = String.format("%d",curAsset.getPendingCount());
        TextDrawable drawable = TextDrawable.builder()
                .buildRound(_strPendingCnt, ContextCompat.getColor(getActivity(),R.color.AppBlue));
        _ivAny.setImageDrawable(drawable);

        _ivAny = (ImageView)_viewMe.findViewById(R.id.fragment_item_detail_iv_total_pending);
        _strPendingCnt = String.format("%d",DBManager.getAllPendingCount());
        drawable = TextDrawable.builder()
                .buildRound(_strPendingCnt, ContextCompat.getColor(getActivity(),R.color.AppBlue));
        _ivAny.setImageDrawable(drawable);

        initMenu();
        arrPendings = curAsset.getPendingArray();
        pendingAdapter = new PendingAdapter(getActivity(),arrPendings);

        mListView = (SlideAndDragListView)_viewMe.findViewById(R.id.fragment_item_detail_slv);
        mListView.setMenu(mMenuList);
        mListView.setAdapter(pendingAdapter);
        mListView.setOnDragListener(this, arrPendings);
        mListView.setOnSlideListener(this);
        mListView.setOnMenuItemClickListener(this);
        mListView.setOnItemDeleteListener(this);
        mListView.setOnListItemClickListener(this);
        setListViewHeightBasedOnChildren();
        return _viewMe;
    }

    public void initMenu() {
        mMenuList = new ArrayList<>(1);
        Menu _menu;
        _menu = new Menu(true, true,0);
        _menu.addItem(new MenuItem.Builder().setWidth((int) getResources().getDimension(R.dimen.slv_item_bg_btn_width) )
                .setBackground(new ColorDrawable(getResources().getColor(R.color.red)))
                .setDirection(MenuItem.DIRECTION_LEFT)
                .setIcon(getResources().getDrawable(R.drawable.icon_trash))
                .build());
        mMenuList.add(_menu);
    }

    public void setListViewHeightBasedOnChildren() {
        ListAdapter listAdapter = mListView.getAdapter();
        if (listAdapter == null) {
            return;
        }
        int desiredWidth = View.MeasureSpec.makeMeasureSpec(mListView.getWidth(), View.MeasureSpec.AT_MOST);
        int totalHeight = 0;
        View view = null;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            view = listAdapter.getView(i, view, mListView);
            if (i == 0) {
                view.setLayoutParams(new ViewGroup.LayoutParams(desiredWidth, DrawerLayout.LayoutParams.WRAP_CONTENT));
            }
            view.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            totalHeight += view.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = mListView.getLayoutParams();
        params.height = totalHeight + (mListView.getDividerHeight() * (listAdapter.getCount() - 1));
        mListView.setLayoutParams(params);
        mListView.requestLayout();
    }

    @Override
    public void onListItemClick(View view, int i) {
        Pending _aPending = arrPendings.get(i);
        PendingDetailFragment _fragment = new PendingDetailFragment();
        _fragment.setPending(_aPending);
        gotoFragment(_fragment);

    }

    @Override
    public int onMenuItemClick(View v, int itemPosition, int buttonPosition, int direction) {
        return Menu.ITEM_DELETE_FROM_BOTTOM_TO_TOP;
    }

    @Override
    public void onItemDelete(View view, int position) {
        Pending _objPending = arrPendings.get(position);
        DBManager.deletePending(_objPending);
        arrPendings.remove(position);
        pendingAdapter.notifyDataSetChanged();

    }

    @Override
    public void onDragViewStart(int position) {

    }

    @Override
    public void onDragViewMoving(int position) {

    }

    @Override
    public void onDragViewDown(int position) {

    }

    @Override
    public void onSlideOpen(View view, View parentView, int position, int direction) {

    }

    @Override
    public void onSlideClose(View view, View parentView, int position, int direction) {

    }

    public void initImageGallery(View viewParent){
        mAdapter = new GalleryAdapter();

        OneFlingGallery gallery = (OneFlingGallery) viewParent.findViewById(R.id.fragment_item_detail_gallery);
        final GalleryNavigator navi = (GalleryNavigator) viewParent.findViewById(R.id.fragment_item_detail_navi);
        navi.setSize(arrImageUrls.size());
        navi.setVisibility(View.GONE);
        navi.setVisibility(View.VISIBLE);
        navi.invalidate();

        gallery.setAdapter(mAdapter);
        gallery.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int id,
                                       long arg3) {
                navi.setPosition(id);
                navi.invalidate();
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {

            }
        });

    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setCustomTitle(getView(),curAsset.assetName);
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
        curAsset = null;
    }

    public class GalleryAdapter extends BaseAdapter {
        private LayoutInflater inflater;

        public GalleryAdapter() {

            inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return arrImageUrls.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }


        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.item_imagegrallery, null);
                holder = new ViewHolder();
                holder.imgIntro = (ImageView) convertView.findViewById(R.id.imgItem);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            ImageUrl _imageUrl = arrImageUrls.get(position);
            String _strUrl = _imageUrl.strUrl;
            if(!_imageUrl.strUrl.contains("http")){
                _strUrl = "file://" + _imageUrl.strUrl;
            }
            Picasso.with(getActivity())
                    .load(_strUrl)
                    .into(holder.imgIntro);

            return convertView;
        }
    }

    class ViewHolder {
        public ImageView imgIntro;
    }



}
