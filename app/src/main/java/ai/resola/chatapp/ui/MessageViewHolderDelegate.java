package ai.resola.chatapp.ui;

import com.stfalcon.chatkit.messages.MessagesListAdapter;

/**
 * Created by dotuan on 2017/03/31.
 */

public interface MessageViewHolderDelegate {
    void didPressedUrl(String url);
    void didPressedMap(MessagesListAdapter.BaseMessageViewHolder holder);
    void didPressedVideo(MessagesListAdapter.BaseMessageViewHolder holder);
    void didPressedImage(MessagesListAdapter.BaseMessageViewHolder holder);
}
