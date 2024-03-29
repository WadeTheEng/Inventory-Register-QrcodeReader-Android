package mike.buildsourced.common;

import android.content.Context;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import mike.buildsourced.R;

/**
 * Created by user1 on 6/16/2017.
 */

public class MDToast {


    public static final int TYPE_SUCCESS = 0;
    public static final int TYPE_ERROR = 1;

    public static int LENGTH_LONG = Toast.LENGTH_LONG;
    public static int LENGTH_SHORT = Toast.LENGTH_SHORT;

    public static void makeText(Context context, String message, int duration) {
        makeText(context, message, duration, TYPE_SUCCESS);
    }

    public static void makeSuccessText(Context context, String message)
    {
        makeText(context,message,3000,TYPE_SUCCESS);
    }

    public static void makeFailedText(Context context, String message)
    {
        makeText(context,message,3000,TYPE_ERROR);
    }

    public static void makeText(Context context, String message, int duration, int type) {
        LinearLayout layout = new LinearLayout(context);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.custom_toast_container, null);
        layout.addView(view);

        LinearLayout customToastContainer = (LinearLayout) layout.findViewById(R.id.custom_toast_container);
        ImageView icon = (ImageView) layout.findViewById(R.id.icon);
        TextView text = (TextView) layout.findViewById(R.id.text);

        switch (type) {
            case TYPE_SUCCESS:
                //icon.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_check_circle_white_18dp));
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    customToastContainer.setBackground(ContextCompat.getDrawable(context, R.drawable.custom_toast_success_background));
                } else customToastContainer.setBackgroundColor(ContextCompat.getColor(context, R.color.AppBlue));
                break;
            case TYPE_ERROR:
                //icon.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_error_white_18dp));
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    customToastContainer.setBackground(ContextCompat.getDrawable(context, R.drawable.custom_toast_error_background));
                } else customToastContainer.setBackgroundColor(ContextCompat.getColor(context, R.color.AppRed));
                break;
        }
        text.setText(message);

        Toast toast = new Toast(context);
        toast.setDuration(duration);
        toast.setView(layout);
        toast.show();
    }

}
