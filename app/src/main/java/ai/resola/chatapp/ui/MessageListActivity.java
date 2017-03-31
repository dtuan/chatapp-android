package ai.resola.chatapp.ui;

import android.app.Activity;
import android.app.Service;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.LatLng;
import com.squareup.picasso.Picasso;
import com.stfalcon.chatkit.commons.ImageLoader;
import com.stfalcon.chatkit.commons.models.IMessage;
import com.stfalcon.chatkit.messages.MessageInput;
import com.stfalcon.chatkit.messages.MessagesList;
import com.stfalcon.chatkit.messages.MessagesListAdapter;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import ai.resola.chatapp.Browser;
import ai.resola.chatapp.CONST;
import ai.resola.chatapp.R;
import ai.resola.chatapp.Utils;
import ai.resola.chatapp.api.RebotAPI;
import ai.resola.chatapp.externals.SoftKeyboard;
import ai.resola.chatapp.model.ChatResponse;
import ai.resola.chatapp.model.Message;
import ai.resola.chatapp.model.User;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static ai.resola.chatapp.R.id.messagesList;

public class MessageListActivity extends AppCompatActivity
        implements MessagesListAdapter.SelectionListener, MessagesListAdapter.OnMessageClickListener, MessageViewHolderDelegate  {
    public static  String TAG = "MessageListActivity";

    private ViewGroup mainContainer;

    private MessagesList messagesListView;
    private MyMessagesListAdapter adapter;
    private MessageInput inputView;

    private LinearLayout repliesContainer;
    private ArrayList<Button> repliesButtons;

    private User localUser;
    private User botUser;

    private SoftKeyboard softKeyboard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_list);

        localUser = getIntent().getParcelableExtra("USER");
        botUser = new User(CONST.BOT_USER_ID, CONST.BOT_USER_ID, CONST.BOT_USER_NAME, CONST.BOT_USER_IMAGE);
        repliesButtons = new ArrayList<>();

        mainContainer = (ViewGroup) findViewById(R.id.activity_message_list);
        repliesContainer = (LinearLayout) findViewById(R.id.replies);

        messagesListView = (MessagesList) findViewById(messagesList);
        initMessagesAdapter();

        inputView = (MessageInput) findViewById(R.id.input);
        inputView.setInputListener(new MessageInput.InputListener() {
            @Override
            public boolean onSubmit(CharSequence input) {
                return processInput(input.toString());
            }
        });

        addKeyboardListener();

        // Default text
        inputView.getInputEditText().setText("地図");
    }

    private void addKeyboardListener() {
        InputMethodManager im = (InputMethodManager) getSystemService(Service.INPUT_METHOD_SERVICE);
        softKeyboard = new SoftKeyboard(mainContainer, im);
        softKeyboard.setSoftKeyboardCallback(new SoftKeyboard.SoftKeyboardChanged() {
            @Override
            public void onSoftKeyboardHide() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        for(Button btn: repliesButtons) {
                            repliesContainer.addView(btn);
                        }
                    }
                });
            }

            @Override
            public void onSoftKeyboardShow() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        repliesContainer.removeAllViews();
                    }
                });
            }
        });
    }

    @Override
    public void onSelectionChanged(int count) {
        Log.v(TAG, "onSelectionChanged: " + count);
    }

    public static void open(Activity activity, User user) {
        Intent intent = new Intent(activity, MessageListActivity.class);
        intent.putExtra("USER", user);
        activity.startActivity(intent);
    }

    private void initMessagesAdapter() {
        ImageLoader imageLoader = new ImageLoader() {
            @Override
            public void loadImage(ImageView imageView, String url) {
                Picasso.with(MessageListActivity.this).load(url).into(imageView);
            }
        };

        MessagesListAdapter.HoldersConfig holdersConfig = new MessagesListAdapter.HoldersConfig();
        holdersConfig.setIncoming(MyIncomingMessageViewHolder.class, R.layout.item_custom_incoming_message);
        holdersConfig.setOutcoming(MyOutgoingMessageViewController.class, R.layout.item_custom_outcoming_message);

        adapter = new MyMessagesListAdapter(localUser.getId(), holdersConfig, imageLoader);
        adapter.setOnMessageClickListener(this);
        adapter.setOnMessageLongClickListener(new MessagesListAdapter.OnMessageLongClickListener<Message>() {
            @Override
            public void onMessageLongClick(Message message) {
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("REBOT_CHAT", message.getText());
                clipboard.setPrimaryClip(clip);
                Toast.makeText(MessageListActivity.this,
                        "Copied", Toast.LENGTH_SHORT).show();
            }
        });
        adapter.setHolderDelegate(MessageListActivity.this);

        //adapter.enableSelectionMode(this);

        adapter.setLoadMoreListener(new MessagesListAdapter.OnLoadMoreListener() {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                /*if (totalItemsCount < 50) {
                    loadMessages();
                }*/
                Log.v(TAG, "onLoadMore: " + page + ", count: " + totalItemsCount);
            }
        });

        messagesListView.setAdapter(adapter);
    }

    private boolean processInput(String text) {
        Utils.hideKeyboard(inputView);

        adapter.addToStart(createMessage(text, localUser), true);
        sendChatMessage(text);

        return true;
    }

    private Message createMessage(String text, User user) {
        return new Message(text, Message.Type.Text, null, getDate(), user);
    }

    private Date getDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, -1);
        return  calendar.getTime();
    }

    private void sendChatMessage(String message) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(CONST.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RebotAPI rebotAPI = retrofit.create(RebotAPI.class);

        Call<ChatResponse> call = rebotAPI.retrieveChatResponse(CONST.APP_ID, localUser.getUniqueId(), message);

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
                Toast.makeText(MessageListActivity.this, t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void downloadComplete(ChatResponse chatResponse) {
        Log.v("### DOWNLOAD COMPLETE", chatResponse.toString());
        for(String response: chatResponse.getResponse()) {
            List<Message> messages = processResponse(response);
            for(Message msg: messages) {
                adapter.addToStart(msg, true);
            }
        }
        setupReplies(chatResponse.getReply());
    }

    public List<Message> processResponse(String html) {
        // Replace original line break to avoid stripped when parse html
        String myHtml = html.replaceAll(CONST.LINE_BREAK, CONST.MARK_LINE_BREAK);

        String TAG = "### PARSE ###";
        Log.v("######################", "\n"+myHtml+"\n######################");
        Document doc = Jsoup.parse(myHtml);
        Element body = doc.body();

        if (body == null) {
            return null;
        }

        // Wrap text node to tag "text" because body.children not contain text nodes and
        // remove node just has line break
        List<TextNode> textNodes = body.textNodes();
        for(TextNode tn: textNodes) {
            String str = tn.text();
            str = str.replace(CONST.MARK_LINE_BREAK, "");
            if (str.isEmpty()) {
                tn.remove();
            } else {
                tn.wrap("<text></text>");
            }
        }


        List<Message> messages = new ArrayList<>();
        Elements elements = body.children();
        for(Element element: elements) {
            messages.add(Message.create(element, getDate(), botUser));
        }

        return messages;
    }

    public void setupReplies(String[] replies) {
        repliesButtons.clear();
        repliesContainer.removeAllViews();

        if (replies == null || replies.length == 0) {
            return;
        }

        for(String reply: replies) {
            Button btn = createButton(repliesContainer, reply);
            repliesButtons.add(btn);
        }
    }

    private Button createButton(ViewGroup parent, String title) {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );

        Button btn = new Button(MessageListActivity.this);
        btn.setLayoutParams(params);
        btn.setText(title);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                processSelectReply(v);
            }
        });

        parent.addView(btn);
        return btn;
    }

    private void processSelectReply(View v) {
        Button btn = (Button) v;
        processInput(btn.getText().toString());
        setupReplies(null);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        softKeyboard.unRegisterSoftKeyboardCallback();
    }

    @Override
    public void onMessageClick(IMessage iMessage) {
        Log.v("MessageList", "### onMessageClick");
        //Log.d("MessageList", Log.getStackTraceString(new Exception()));
    }

    @Override
    public void didPressedUrl(String url) {
        Log.d(TAG, "didPressedUrl");
        Browser.openUrl(MessageListActivity.this, url, true);
    }

    @Override
    public void didPressedMap(MessagesListAdapter.BaseMessageViewHolder holder) {
        Log.d(TAG, "didPressedMap");
        Message message = ((MyIncomingMessageViewHolder)holder).getMessage();
        try {
            LatLng latLng = message.getMedia().getLocation();
            String strGeo = String.format("geo:0,0?q=%f,%f(%s)", latLng.latitude, latLng.longitude, message.getText());
            Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                    Uri.parse(strGeo));
            startActivity(intent);
        } catch (Exception e) {
            Log.e("MLA", e.toString());
        }
    }

    @Override
    public void didPressedVideo(MessagesListAdapter.BaseMessageViewHolder holder) {
        Log.d(TAG, "didPressedVideo");
        Message message = ((MyIncomingMessageViewHolder)holder).getMessage();
        String videoUrl = message.getMedia().videoUrl;
        Browser.openUrl(this, videoUrl, true);
    }

    @Override
    public void didPressedImage(MessagesListAdapter.BaseMessageViewHolder holder) {
        Log.d(TAG, "didPressedImage");
    }
}
