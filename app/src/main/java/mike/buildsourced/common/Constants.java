package mike.buildsourced.common;

import android.content.Context;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by user1 on 6/16/2017.
 */

public class Constants {
    public static final int SUCCESS_RESULT = 0;
    public static final int FAILURE_RESULT = 1;
    public static final String PACKAGE_NAME =
            "mike.buildsourced";
    public static final String RECEIVER = PACKAGE_NAME + ".RECEIVER";
    public static final String RESULT_DATA_KEY = PACKAGE_NAME +
            ".RESULT_DATA_KEY";
    public static final String LOCATION_DATA_EXTRA = PACKAGE_NAME +
            ".LOCATION_DATA_EXTRA";



    static public int getImageId(Context context, String aStrName){
        int drawableResourceId = context.getResources().getIdentifier(aStrName, "drawable", context.getPackageName());
        return drawableResourceId;
    }

    static public Date dateFromString(String strDate){
        Date _retDate = new Date();
        try {
            DateFormat _formater = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            _retDate = _formater.parse(strDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return _retDate;
    }

    static public String dateString(Date date){
        DateFormat _formater = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return _formater.format(date);
    }

    static public String datePendingStyleString(Date date){
        DateFormat _formater = new SimpleDateFormat("MM/dd/yy HH:mm a");

        return _formater.format(date);
    }
}
