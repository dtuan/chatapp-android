package ai.resola.chatapp.ui;

import android.app.Application;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ClickableSpan;
import android.text.style.URLSpan;
import android.text.util.Linkify;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubeThumbnailLoader;
import com.google.android.youtube.player.YouTubeThumbnailView;
import com.stfalcon.chatkit.messages.MessagesListAdapter;

import ai.resola.chatapp.ApplicationLoader;
import ai.resola.chatapp.R;
import ai.resola.chatapp.model.Message;
import ai.resola.chatapp.support.DeveloperKey;

/**
 * Created by dotuan on 2017/03/28.
 */

public class MyIncomingMessageViewHolder
        extends MessagesListAdapter.IncomingMessageViewHolder<Message>
        implements OnMapReadyCallback {

    private ImageView imageView;
    private MapView mapView;
    private GoogleMap map;
    private YouTubeThumbnailView youTubeThumbnailView;

    private Message message;
    private boolean initializedMap;

    MessageViewHolderDelegate delegate;

    private boolean isClickingLink = false;

    private class MySpan extends ClickableSpan {

        private String mUrl;

        public MySpan(String url) {

            super();
            mUrl = url;
        }

        @Override
        public void onClick(View widget) {
            isClickingLink = true;
            delegate.didPressedUrl(mUrl);
        }
    }


    public MyIncomingMessageViewHolder(View itemView) {
        super(itemView);

        imageView = (ImageView) itemView.findViewById(R.id.imageView);
        mapView = (MapView) itemView.findViewById(R.id.incomingMapView);
        youTubeThumbnailView = (YouTubeThumbnailView) itemView.findViewById(R.id.youtubeThumbnail);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                delegate.didPressedImage(MyIncomingMessageViewHolder.this);
            }
        });

        youTubeThumbnailView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                delegate.didPressedVideo(MyIncomingMessageViewHolder.this);
            }
        });

        initializeMapView();
    }

    public void setDelegate(MessageViewHolderDelegate holderDelegate) {
        delegate = holderDelegate;
    }

    public void setTextClickable() {
        CharSequence charSequence = text.getText();
        SpannableStringBuilder sp = new SpannableStringBuilder(charSequence);

        URLSpan[] spans = sp.getSpans(0, charSequence.length(), URLSpan.class);

        for (URLSpan urlSpan : spans) {
            MySpan mySpan = new MySpan(urlSpan.getURL());
            sp.setSpan(mySpan, sp.getSpanStart(urlSpan),
                    sp.getSpanEnd(urlSpan), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        }

        text.setText(sp);
    }

    public Message getMessage() {
        return message;
    }

    public void initializeMapView() {
        if (mapView != null && !initializedMap) {
            initializedMap = true;
            mapView.onCreate(null);
            mapView.onResume();
            mapView.getMapAsync(this);
        }
    }

    public void initializeYoutubeThumbnailView (final String videoId) {
        Log.d("YoutubeThumbnailView", "videoId: " + videoId);
        youTubeThumbnailView.initialize(DeveloperKey.YOUTUBE_KEY, new YouTubeThumbnailView.OnInitializedListener() {
            @Override
            public void onInitializationSuccess(YouTubeThumbnailView youTubeThumbnailView, final YouTubeThumbnailLoader youTubeThumbnailLoader) {
                youTubeThumbnailLoader.setVideo(videoId);
                youTubeThumbnailLoader.setOnThumbnailLoadedListener(new YouTubeThumbnailLoader.OnThumbnailLoadedListener() {
                    @Override
                    public void onThumbnailLoaded(YouTubeThumbnailView youTubeThumbnailView, String s) {
                        youTubeThumbnailLoader.release();
                    }

                    @Override
                    public void onThumbnailError(YouTubeThumbnailView youTubeThumbnailView, YouTubeThumbnailLoader.ErrorReason errorReason) {

                    }
                });
            }

            @Override
            public void onInitializationFailure(YouTubeThumbnailView youTubeThumbnailView, YouTubeInitializationResult youTubeInitializationResult) {

            }
        });
    }

    @Override
    public void onBind(Message message) {
        super.onBind(message);

        this.message = message;
        imageView.setVisibility(View.GONE);
        mapView.setVisibility(View.GONE);
        youTubeThumbnailView.setVisibility(View.GONE);

        setTextClickable();

        switch (message.getType()) {
            case Link:
                break;
            case Image:
                imageView.setVisibility(View.VISIBLE);
                imageLoader.loadImage(imageView, message.getMedia().imageUrl);
                break;
            case Video:
                youTubeThumbnailView.setVisibility(View.VISIBLE);
                initializeYoutubeThumbnailView(message.getMedia().getYoutubeVideoId());
                break;
            case Location:
                mapView.setVisibility(View.VISIBLE);
                if (map != null) {
                    setMapLocaton(map, message.getText(), message.getMedia().getLocation());
                }

                break;
            default: break;
        }

        this.itemView.invalidate();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        MapsInitializer.initialize(ApplicationLoader.applicationContext);

        map = googleMap;

        map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                delegate.didPressedMap(MyIncomingMessageViewHolder.this);
            }
        });

        if (message.getType() == Message.Type.Location) {
            setMapLocaton(map, message.getText(), message.getMedia().getLocation());
        }
    }

    public static void setMapLocaton(GoogleMap map, String name, LatLng location) {
        // Add a marker for this item and set the camera
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 13f));
        map.addMarker(new MarkerOptions().position(location));

        // Set the map type back to normal.
        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
    }

}
