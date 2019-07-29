package mike.buildsourced.pending;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import mike.buildsourced.R;
import mike.buildsourced.common.database.model.Pending;

/**
 * Created by user1 on 7/5/2017.
 */

public class PendingAdapter extends BaseAdapter {

    private Context mContext;
    private LayoutInflater inflater;
    private List<Pending> arrPendingItems;
    public int nSelectedMenu = 0;

    public PendingAdapter(Context context, List<Pending> aendingItems){
        this.mContext = context;
        this.arrPendingItems = aendingItems;
        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return arrPendingItems.size();
    }

    @Override
    public Object getItem(int location) {
        return arrPendingItems.get(location);
    }

    @Override
    public long getItemId(int position){
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        if(convertView == null){
            convertView = inflater.inflate(R.layout.pending_list_low,null);
        }

        final Pending _data = arrPendingItems.get(position);
        TextView _txtTitle = (TextView)convertView.findViewById(R.id.pending_list_txt_title);
        TextView _txtDate = (TextView)convertView.findViewById(R.id.pending_list_txt_date);
        _txtTitle.setText(_data.getPendingname());
        _txtDate.setText(_data.getPendDate());
        return convertView;
    }


}