package ai.resola.chatapp.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Build;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowInsets;
import android.widget.FrameLayout;

import ai.resola.chatapp.Utils;
import ai.resola.chatapp.component.ClippingImageView;
import ai.resola.chatapp.component.SizeNotifierFrameLayoutPhoto;

/**
 * Created by dotuan on 2017/03/28.
 */

public class PhotoViewer {


    private Activity parentActivity;

    private FrameLayoutDrawer containerView;
    private FrameLayout windowView;
    private ClippingImageView animatingImageView;
    private Object lastInsets;

    private Paint blackPaint = new Paint();

    private static volatile PhotoViewer Instance = null;

    public static PhotoViewer getInstance() {
        PhotoViewer localInstance = Instance;
        if (localInstance == null) {
            synchronized (PhotoViewer.class) {
                localInstance = Instance;
                if (localInstance == null) {
                    Instance = localInstance = new PhotoViewer();
                }
            }
        }
        return localInstance;
    }

    private class FrameLayoutDrawer extends SizeNotifierFrameLayoutPhoto {

        private Paint paint = new Paint();

        public FrameLayoutDrawer(Context context) {
            super(context);
            setWillNotDraw(false);
            paint.setColor(0x33000000);
        }

        @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            /*int widthSize = View.MeasureSpec.getSize(widthMeasureSpec);
            int heightSize = View.MeasureSpec.getSize(heightMeasureSpec);

            setMeasuredDimension(widthSize, heightSize);

            measureChildWithMargins(captionEditText, widthMeasureSpec, 0, heightMeasureSpec, 0);
            int inputFieldHeight = captionEditText.getMeasuredHeight();

            int childCount = getChildCount();
            for (int i = 0; i < childCount; i++) {
                View child = getChildAt(i);
                if (child.getVisibility() == GONE || child == captionEditText) {
                    continue;
                }
                if (captionEditText.isPopupView(child)) {
                    if (Utils.isInMultiwindow) {
                        if (Utils.isTablet()) {
                            child.measure(View.MeasureSpec.makeMeasureSpec(widthSize, View.MeasureSpec.EXACTLY), View.MeasureSpec.makeMeasureSpec(Math.min(Utils.dp(320), heightSize - inputFieldHeight - Utils.statusBarHeight), View.MeasureSpec.EXACTLY));
                        } else {
                            child.measure(View.MeasureSpec.makeMeasureSpec(widthSize, View.MeasureSpec.EXACTLY), View.MeasureSpec.makeMeasureSpec(heightSize - inputFieldHeight - Utils.statusBarHeight, View.MeasureSpec.EXACTLY));
                        }
                    } else {
                        child.measure(View.MeasureSpec.makeMeasureSpec(widthSize, View.MeasureSpec.EXACTLY), View.MeasureSpec.makeMeasureSpec(child.getLayoutParams().height, View.MeasureSpec.EXACTLY));
                    }
                } else {
                    measureChildWithMargins(child, widthMeasureSpec, 0, heightMeasureSpec, 0);
                }
            }*/
        }

        @Override
        protected void onLayout(boolean changed, int l, int t, int r, int b) {
            final int count = getChildCount();
            //int paddingBottom = getKeyboardHeight() <= Utils.dp(20) && !Utils.isInMultiwindow ? captionEditText.getEmojiPadding() : 0;
            int paddingBottom = 0;
            for (int i = 0; i < count; i++) {
                final View child = getChildAt(i);
                if (child.getVisibility() == GONE) {
                    continue;
                }
                final FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) child.getLayoutParams();

                final int width = child.getMeasuredWidth();
                final int height = child.getMeasuredHeight();

                int childLeft;
                int childTop;

                int gravity = lp.gravity;
                if (gravity == -1) {
                    gravity = Gravity.TOP | Gravity.LEFT;
                }

                final int absoluteGravity = gravity & Gravity.HORIZONTAL_GRAVITY_MASK;
                final int verticalGravity = gravity & Gravity.VERTICAL_GRAVITY_MASK;

                switch (absoluteGravity & Gravity.HORIZONTAL_GRAVITY_MASK) {
                    case Gravity.CENTER_HORIZONTAL:
                        childLeft = (r - l - width) / 2 + lp.leftMargin - lp.rightMargin;
                        break;
                    case Gravity.RIGHT:
                        childLeft = r - width - lp.rightMargin;
                        break;
                    case Gravity.LEFT:
                    default:
                        childLeft = lp.leftMargin;
                }

                switch (verticalGravity) {
                    case Gravity.TOP:
                        childTop = lp.topMargin;
                        break;
                    case Gravity.CENTER_VERTICAL:
                        childTop = ((b - paddingBottom) - t - height) / 2 + lp.topMargin - lp.bottomMargin;
                        break;
                    case Gravity.BOTTOM:
                        childTop = ((b - paddingBottom) - t) - height - lp.bottomMargin;
                        break;
                    default:
                        childTop = lp.topMargin;
                }
                /*
                if (child == mentionListView) {
                    if (!captionEditText.isPopupShowing() && !captionEditText.isKeyboardVisible() && captionEditText.getEmojiPadding() == 0) {
                        childTop += Utils.dp(400);
                    } else {
                        childTop -= captionEditText.getMeasuredHeight();
                    }
                } else if (child == captionEditText) {
                    if (!captionEditText.isPopupShowing() && !captionEditText.isKeyboardVisible() && captionEditText.getEmojiPadding() == 0) {
                        childTop += Utils.dp(400);
                    }
                } else if (child == pickerView || child == captionTextViewNew || child == captionTextViewOld) {
                    if (captionEditText.isPopupShowing() || captionEditText.isKeyboardVisible()) {
                        childTop += Utils.dp(400);
                    }
                } else if (captionEditText.isPopupView(child)) {
                    if (Utils.isInMultiwindow) {
                        childTop = captionEditText.getTop() - child.getMeasuredHeight() + Utils.dp(1);
                    } else {
                        childTop = captionEditText.getBottom();
                    }
                }
                */
                child.layout(childLeft, childTop, childLeft + width, childTop + height);
            }

            notifyHeightChanged();
        }

        @Override
        protected void onDraw(Canvas canvas) {
            getInstance().onDraw(canvas);

            if (Build.VERSION.SDK_INT >= 21 && Utils.statusBarHeight != 0) {
                canvas.drawRect(0, 0, getMeasuredWidth(), Utils.statusBarHeight, paint);
            }
        }

        @Override
        protected boolean drawChild(Canvas canvas, View child, long drawingTime) {
            //return child != aspectRatioFrameLayout && super.drawChild(canvas, child, drawingTime);
            return true;
        }
    }

    public void setParentActivity(final Activity activity) {
        if (parentActivity == activity) {
            return;
        }

        parentActivity = activity;

    }

    private void createWindowView(final Activity activity) {
        windowView = new FrameLayout(activity) {
            @Override
            protected boolean drawChild(Canvas canvas, View child, long drawingTime) {
                boolean result = super.drawChild(canvas, child, drawingTime);
                if (Build.VERSION.SDK_INT >= 21 && child == animatingImageView && lastInsets != null) {
                    WindowInsets insets = (WindowInsets) lastInsets;
                    canvas.drawRect(0, getMeasuredHeight(), getMeasuredWidth(), getMeasuredHeight() + insets.getSystemWindowInsetBottom(), blackPaint);
                }
                return result;
            }

            @Override
            protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                int widthSize = MeasureSpec.getSize(widthMeasureSpec);
                int heightSize = MeasureSpec.getSize(heightMeasureSpec);
                if (Build.VERSION.SDK_INT >= 21 && lastInsets != null) {
                    WindowInsets insets = (WindowInsets) lastInsets;
                    /*if (Utils.incorrectDisplaySizeFix) {
                        if (heightSize > Utils.displaySize.y) {
                            heightSize = Utils.displaySize.y;
                        }
                        heightSize += Utils.statusBarHeight;
                    }*/
                    heightSize -= insets.getSystemWindowInsetBottom();
                    widthSize -= insets.getSystemWindowInsetRight();
                } else {
                    if (heightSize > Utils.displaySize.y) {
                        heightSize = Utils.displaySize.y;
                    }
                }
                setMeasuredDimension(widthSize, heightSize);
                ViewGroup.LayoutParams layoutParams = animatingImageView.getLayoutParams();
                animatingImageView.measure(MeasureSpec.makeMeasureSpec(layoutParams.width, MeasureSpec.AT_MOST), MeasureSpec.makeMeasureSpec(layoutParams.height, MeasureSpec.AT_MOST));
                containerView.measure(MeasureSpec.makeMeasureSpec(widthSize, MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(heightSize, MeasureSpec.EXACTLY));
            }
        };

    }

    @SuppressLint("NewApi")
    private void onDraw(Canvas canvas) {
        /*
        if (animationInProgress == 1 || !isVisible && animationInProgress != 2) {
            return;
        }

        float currentTranslationY;
        float currentTranslationX;
        float currentScale;
        float aty = -1;

        if (imageMoveAnimation != null) {
            if (!scroller.isFinished()) {
                scroller.abortAnimation();
            }

            float ts = scale + (animateToScale - scale) * animationValue;
            float tx = translationX + (animateToX - translationX) * animationValue;
            float ty = translationY + (animateToY - translationY) * animationValue;
            if (currentEditMode == 1) {
                photoCropView.setAnimationProgress(animationValue);
            }

            if (animateToScale == 1 && scale == 1 && translationX == 0) {
                aty = ty;
            }
            currentScale = ts;
            currentTranslationY = ty;
            currentTranslationX = tx;
            containerView.invalidate();
        } else {
            if (animationStartTime != 0) {
                translationX = animateToX;
                translationY = animateToY;
                scale = animateToScale;
                animationStartTime = 0;
                if (currentEditMode == 1) {
                    photoCropView.setAnimationProgress(1);
                }
                updateMinMax(scale);
                zoomAnimation = false;
            }
            if (!scroller.isFinished()) {
                if (scroller.computeScrollOffset()) {
                    if (scroller.getStartX() < maxX && scroller.getStartX() > minX) {
                        translationX = scroller.getCurrX();
                    }
                    if (scroller.getStartY() < maxY && scroller.getStartY() > minY) {
                        translationY = scroller.getCurrY();
                    }
                    containerView.invalidate();
                }
            }
            if (switchImageAfterAnimation != 0) {
                if (switchImageAfterAnimation == 1) {
                    setImageIndex(currentIndex + 1, false);
                } else if (switchImageAfterAnimation == 2) {
                    setImageIndex(currentIndex - 1, false);
                }
                switchImageAfterAnimation = 0;
            }
            currentScale = scale;
            currentTranslationY = translationY;
            currentTranslationX = translationX;
            if (!moving) {
                aty = translationY;
            }
        }

        if (currentEditMode == 0 && scale == 1 && aty != -1 && !zoomAnimation) {
            float maxValue = getContainerViewHeight() / 4.0f;
            backgroundDrawable.setAlpha((int) Math.max(127, 255 * (1.0f - (Math.min(Math.abs(aty), maxValue) / maxValue))));
        } else {
            backgroundDrawable.setAlpha(255);
        }

        ImageReceiver sideImage = null;
        if (currentEditMode == 0) {
            if (scale >= 1.0f && !zoomAnimation && !zooming) {
                if (currentTranslationX > maxX + AndroidUtilities.dp(5)) {
                    sideImage = leftImage;
                } else if (currentTranslationX < minX - AndroidUtilities.dp(5)) {
                    sideImage = rightImage;
                }
            }
            changingPage = sideImage != null;
        }

        if (sideImage == rightImage) {
            float tranlateX = currentTranslationX;
            float scaleDiff = 0;
            float alpha = 1;
            if (!zoomAnimation && tranlateX < minX) {
                alpha = Math.min(1.0f, (minX - tranlateX) / canvas.getWidth());
                scaleDiff = (1.0f - alpha) * 0.3f;
                tranlateX = -canvas.getWidth() - AndroidUtilities.dp(30) / 2;
            }

            if (sideImage.hasBitmapImage()) {
                canvas.save();
                canvas.translate(getContainerViewWidth() / 2, getContainerViewHeight() / 2);
                canvas.translate(canvas.getWidth() + AndroidUtilities.dp(30) / 2 + tranlateX, 0);
                canvas.scale(1.0f - scaleDiff, 1.0f - scaleDiff);
                int bitmapWidth = sideImage.getBitmapWidth();
                int bitmapHeight = sideImage.getBitmapHeight();

                float scaleX = (float) getContainerViewWidth() / (float) bitmapWidth;
                float scaleY = (float) getContainerViewHeight() / (float) bitmapHeight;
                float scale = scaleX > scaleY ? scaleY : scaleX;
                int width = (int) (bitmapWidth * scale);
                int height = (int) (bitmapHeight * scale);

                sideImage.setAlpha(alpha);
                sideImage.setImageCoords(-width / 2, -height / 2, width, height);
                sideImage.draw(canvas);
                canvas.restore();
            }

            canvas.save();
            canvas.translate(tranlateX, currentTranslationY / currentScale);
            canvas.translate((canvas.getWidth() * (scale + 1) + AndroidUtilities.dp(30)) / 2, -currentTranslationY / currentScale);
            radialProgressViews[1].setScale(1.0f - scaleDiff);
            radialProgressViews[1].setAlpha(alpha);
            radialProgressViews[1].onDraw(canvas);
            canvas.restore();
        }

        float translateX = currentTranslationX;
        float scaleDiff = 0;
        float alpha = 1;
        if (!zoomAnimation && translateX > maxX && currentEditMode == 0) {
            alpha = Math.min(1.0f, (translateX - maxX) / canvas.getWidth());
            scaleDiff = alpha * 0.3f;
            alpha = 1.0f - alpha;
            translateX = maxX;
        }
        boolean drawTextureView = Build.VERSION.SDK_INT >= 16 && aspectRatioFrameLayout != null && aspectRatioFrameLayout.getVisibility() == View.VISIBLE;
        if (centerImage.hasBitmapImage()) {
            canvas.save();
            canvas.translate(getContainerViewWidth() / 2 + getAdditionX(), getContainerViewHeight() / 2 + getAdditionY());
            canvas.translate(translateX, currentTranslationY);
            canvas.scale(currentScale - scaleDiff, currentScale - scaleDiff);

            if (currentEditMode == 1) {
                photoCropView.setBitmapParams(currentScale, translateX, currentTranslationY);
            }

            int bitmapWidth = centerImage.getBitmapWidth();
            int bitmapHeight = centerImage.getBitmapHeight();
            if (drawTextureView && textureUploaded) {
                float scale1 = bitmapWidth / (float) bitmapHeight;
                float scale2 = videoTextureView.getMeasuredWidth() / (float) videoTextureView.getMeasuredHeight();
                if (Math.abs(scale1 - scale2) > 0.01f) {
                    bitmapWidth = videoTextureView.getMeasuredWidth();
                    bitmapHeight = videoTextureView.getMeasuredHeight();
                }
            }

            float scaleX = (float) getContainerViewWidth() / (float) bitmapWidth;
            float scaleY = (float) getContainerViewHeight() / (float) bitmapHeight;
            float scale = scaleX > scaleY ? scaleY : scaleX;
            int width = (int) (bitmapWidth * scale);
            int height = (int) (bitmapHeight * scale);

            if (!drawTextureView || !textureUploaded || !videoCrossfadeStarted || videoCrossfadeAlpha != 1.0f) {
                centerImage.setAlpha(alpha);
                centerImage.setImageCoords(-width / 2, -height / 2, width, height);
                centerImage.draw(canvas);
            }
            if (drawTextureView) {
                if (!videoCrossfadeStarted && textureUploaded) {
                    videoCrossfadeStarted = true;
                    videoCrossfadeAlpha = 0.0f;
                    videoCrossfadeAlphaLastTime = System.currentTimeMillis();
                }
                canvas.translate(-width / 2, -height / 2);
                videoTextureView.setAlpha(alpha * videoCrossfadeAlpha);
                aspectRatioFrameLayout.draw(canvas);
                if (videoCrossfadeStarted && videoCrossfadeAlpha < 1.0f) {
                    long newUpdateTime = System.currentTimeMillis();
                    long dt = newUpdateTime - videoCrossfadeAlphaLastTime;
                    videoCrossfadeAlphaLastTime = newUpdateTime;
                    videoCrossfadeAlpha += dt / 300.0f;
                    containerView.invalidate();
                    if (videoCrossfadeAlpha > 1.0f) {
                        videoCrossfadeAlpha = 1.0f;
                    }
                }
            }
            canvas.restore();
        }
        if (!drawTextureView && (videoPlayerControlFrameLayout == null || videoPlayerControlFrameLayout.getVisibility() != View.VISIBLE)) {
            canvas.save();
            canvas.translate(translateX, currentTranslationY / currentScale);
            radialProgressViews[0].setScale(1.0f - scaleDiff);
            radialProgressViews[0].setAlpha(alpha);
            radialProgressViews[0].onDraw(canvas);
            canvas.restore();
        }

        if (sideImage == leftImage) {
            if (sideImage.hasBitmapImage()) {
                canvas.save();
                canvas.translate(getContainerViewWidth() / 2, getContainerViewHeight() / 2);
                canvas.translate(-(canvas.getWidth() * (scale + 1) + AndroidUtilities.dp(30)) / 2 + currentTranslationX, 0);
                int bitmapWidth = sideImage.getBitmapWidth();
                int bitmapHeight = sideImage.getBitmapHeight();

                float scaleX = (float) getContainerViewWidth() / (float) bitmapWidth;
                float scaleY = (float) getContainerViewHeight() / (float) bitmapHeight;
                float scale = scaleX > scaleY ? scaleY : scaleX;
                int width = (int) (bitmapWidth * scale);
                int height = (int) (bitmapHeight * scale);

                sideImage.setAlpha(1.0f);
                sideImage.setImageCoords(-width / 2, -height / 2, width, height);
                sideImage.draw(canvas);
                canvas.restore();
            }

            canvas.save();
            canvas.translate(currentTranslationX, currentTranslationY / currentScale);
            canvas.translate(-(canvas.getWidth() * (scale + 1) + AndroidUtilities.dp(30)) / 2, -currentTranslationY / currentScale);
            radialProgressViews[2].setScale(1.0f);
            radialProgressViews[2].setAlpha(1.0f);
            radialProgressViews[2].onDraw(canvas);
            canvas.restore();
        }*/
    }
}
