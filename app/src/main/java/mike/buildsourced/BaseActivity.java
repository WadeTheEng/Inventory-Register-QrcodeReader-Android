package mike.buildsourced;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.bigkoo.svprogresshud.SVProgressHUD;

import mike.buildsourced.common.ContextSingleton;

/**
 * Created by user1 on 6/23/2017.
 */

public class BaseActivity extends AppCompatActivity {
    int onStartCount = 0;
    public SVProgressHUD hudProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        hudProgress =  new SVProgressHUD(this);
        onStartCount = 1;
        if (savedInstanceState == null) // 1st time
        {
            this.overridePendingTransition(R.anim.anim_slide_in_left,
                    R.anim.anim_slide_out_left);
        } else // already created so reverse animation
        {
            onStartCount = 2;
        }
    }

    @Override
    protected void onStart() {
        // TODO Auto-generated method stub
        super.onStart();
        if (onStartCount > 1) {
            this.overridePendingTransition(R.anim.anim_slide_in_right,
                    R.anim.anim_slide_out_right);

        } else if (onStartCount == 1) {
            onStartCount++;
        }

    }
}
