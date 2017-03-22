package ai.resola.chatapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import ai.resola.chatapp.api.RebotAPI;
import ai.resola.chatapp.model.ChatResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import android.provider.Settings.Secure;


public class MainActivity extends AppCompatActivity {

    private EditText txtText;

    private String androidId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtText = (EditText) findViewById(R.id.txtText);

        androidId = Secure.getString(getApplicationContext().getContentResolver(), Secure.ANDROID_ID);
        Log.v("androidId", androidId);
    }

    public void onClickSend(View view) {
        sendChatMessage(txtText.getText().toString());
    }

    private void sendChatMessage(String message) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(CONST.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RebotAPI rebotAPI = retrofit.create(RebotAPI.class);

        Call<ChatResponse> call = rebotAPI.retrieveChatResponse(CONST.APP_ID, androidId, message);

        call.enqueue(new Callback<ChatResponse>() {
            @Override
            public void onResponse(Call<ChatResponse> call,
                                   Response<ChatResponse> response) {
                Log.v("onResponse", response.toString());
                if (response.isSuccessful()) {
                    downloadComplete(response.body());
                }
            }

            @Override
            public void onFailure(Call<ChatResponse> call, Throwable t) {
                Toast.makeText(MainActivity.this, t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void downloadComplete(ChatResponse chatResponse) {
        Log.v("DOWNLOAD COMPLETE", chatResponse.toString());
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
