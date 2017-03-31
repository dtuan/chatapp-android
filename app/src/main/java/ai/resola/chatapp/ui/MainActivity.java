package ai.resola.chatapp.ui;

import android.animation.ValueAnimator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import ai.resola.chatapp.Browser;
import ai.resola.chatapp.CONST;
import ai.resola.chatapp.R;
import ai.resola.chatapp.Utils;
import ai.resola.chatapp.model.User;
import ai.resola.chatapp.ui.MessageListActivity;

import android.provider.Settings.Secure;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.youtube.player.YouTubeApiServiceUtil;
import com.google.android.youtube.player.YouTubeInitializationResult;


public class MainActivity extends AppCompatActivity {

    private String androidId;
    LottieAnimationView animationView;
    Button startChatButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Utils.checkDisplaySize(this, getResources().getConfiguration());
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            Utils.statusBarHeight = getResources().getDimensionPixelSize(resourceId);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        androidId = Secure.getString(getApplicationContext().getContentResolver(), Secure.ANDROID_ID);
        Log.v("androidId", androidId);

        startChatButton = (Button) findViewById(R.id.btnOpenChat);
        startChatButton.setVisibility(View.INVISIBLE);

        startAnimation();

        checkYouTubeService();
    }

    private void checkYouTubeService() {
        //Check for any issues
        final YouTubeInitializationResult result = YouTubeApiServiceUtil.isYouTubeApiServiceAvailable(this);


        if (result != YouTubeInitializationResult.SUCCESS)
        {
            //If there are any issues we can show an error dialog.
            result.getErrorDialog(this, 0).show();
        }
        else
        {
            Toast.makeText(MainActivity.this, "Youtube is ok", Toast.LENGTH_SHORT).show();
        }
    }

    private void startAnimation() {
        animationView = (LottieAnimationView) findViewById(R.id.animation_view);
        animationView.setAnimation("favourite_app_icon.json");
        animationView.loop(false);

        animationView.addAnimatorUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                if (animation.getAnimatedFraction() >= 1) {
                    ((ViewGroup)animationView.getParent()).removeView(animationView);
                    startChatButton.setVisibility(View.VISIBLE);
                }
            }
        });

        animationView.playAnimation();
    }

    public void onClickOpenChat(View view) {
        User user = new User(androidId, CONST.LOCAL_USER_ID, "Current User", CONST.LOCAL_USER_IMAGE);
        MessageListActivity.open(this, user);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Browser.bindCustomTabsService(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        Browser.unbindCustomTabsService(this);
    }

    // DEV
    private int dev_currentTextIndex = 0;
    private String dev_getSendText() {
        String[] texts = new String[] {"動画", "リンク", "地図", "画像"};
        String text = texts[dev_currentTextIndex];
        dev_currentTextIndex++;
        if (dev_currentTextIndex >= texts.length) {
            dev_currentTextIndex = 0;
        }
        return text;
    }


}
