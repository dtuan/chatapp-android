package ai.resola.chatapp.ui;

import android.view.View;

import com.stfalcon.chatkit.messages.MessagesListAdapter;

import ai.resola.chatapp.model.Message;

/**
 * Created by dotuan on 2017/03/28.
 */

public class MyOutgoingMessageViewController extends
        MessagesListAdapter.OutcomingMessageViewHolder<Message> {
    public MyOutgoingMessageViewController(View itemView) {
        super(itemView);
    }
}
