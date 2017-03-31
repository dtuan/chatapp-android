package ai.resola.chatapp.component;

import android.content.Context;
import android.graphics.Rect;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;

import ai.resola.chatapp.Utils;

/**
 * Created by dotuan on 2017/03/28.
 */

public class SizeNotifierFrameLayoutPhoto extends FrameLayout {
    private Rect rect = new Rect();
    private int keyboardHeight;
    private SizeNotifierFrameLayoutPhotoDelegate delegate;
    private WindowManager windowManager;
    private boolean withoutWindow;

    public interface SizeNotifierFrameLayoutPhotoDelegate {
        void onSizeChanged(int keyboardHeight, boolean isWidthGreater);
    }

    public SizeNotifierFrameLayoutPhoto(Context context) {
        super(context);
    }

    public void setDelegate(SizeNotifierFrameLayoutPhotoDelegate sizeNotifierFrameLayoutPhotoDelegate) {
        delegate = sizeNotifierFrameLayoutPhotoDelegate;
    }

    public void setWithoutWindow(boolean value) {
        withoutWindow = value;
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        notifyHeightChanged();
    }

    public int getKeyboardHeight() {
        View rootView = getRootView();
        getWindowVisibleDisplayFrame(rect);
        if (withoutWindow) {
            int usableViewHeight = rootView.getHeight() - (rect.top != 0 ? Utils.statusBarHeight : 0) - Utils.getViewInset(rootView);
            return usableViewHeight - (rect.bottom - rect.top);
        } else {
            int usableViewHeight = rootView.getHeight() - Utils.getViewInset(rootView);
            int top = rect.top;
            int size = Utils.displaySize.y - top - usableViewHeight;
            if (size <= Utils.dp(10)) {
                size = 0;
            }
            return size;
        }
    }

    public void notifyHeightChanged() {
        if (delegate != null) {
            keyboardHeight = getKeyboardHeight();
            final boolean isWidthGreater = Utils.displaySize.x > Utils.displaySize.y;
            post(new Runnable() {
                @Override
                public void run() {
                    if (delegate != null) {
                        delegate.onSizeChanged(keyboardHeight, isWidthGreater);
                    }
                }
            });
        }
    }
}
