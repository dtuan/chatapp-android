package ai.resola.chatapp.ui;

import com.stfalcon.chatkit.commons.ImageLoader;
import com.stfalcon.chatkit.commons.ViewHolder;
import com.stfalcon.chatkit.messages.MessagesListAdapter;

import ai.resola.chatapp.model.Message;

/**
 * Created by dotuan on 2017/03/31.
 */

public class MyMessagesListAdapter extends MessagesListAdapter<Message> {

    private MessageViewHolderDelegate holderDelegate;

    public MyMessagesListAdapter(String senderId, HoldersConfig holders, ImageLoader imageLoader) {
        super(senderId, holders, imageLoader);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        if (holder instanceof MyIncomingMessageViewHolder) {
            ((MyIncomingMessageViewHolder) holder).setDelegate(holderDelegate);
        }
    }

    public void setHolderDelegate(MessageViewHolderDelegate messageViewHolderDelegate) {
        holderDelegate = messageViewHolderDelegate;
    }
}
