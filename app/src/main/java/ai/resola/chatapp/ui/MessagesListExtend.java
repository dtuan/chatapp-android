package ai.resola.chatapp.ui;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;

import com.stfalcon.chatkit.messages.MessagesList;

/**
 * Created by dotuan on 2017/03/24.
 */

public class MessagesListExtend extends MessagesList {
    public MessagesListExtend(Context context) {
        super(context);
    }

    public MessagesListExtend(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public MessagesListExtend(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }


    // By pass error for design view, this function should not be call
    @Override
    public void setAdapter(Adapter adapter) {
        //super.setAdapter(adapter);
        // DO NOTHING
    }
}
