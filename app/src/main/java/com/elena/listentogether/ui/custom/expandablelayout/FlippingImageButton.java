package com.elena.listentogether.ui.custom.expandablelayout;

import android.animation.ObjectAnimator;
import android.animation.TimeInterpolator;
import android.content.Context;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.widget.AppCompatImageButton;
import android.util.AttributeSet;

import com.elena.listentogether.R;

/**
 * Displays a button with an image (instead of text) that can be pressed or clicked by the user with back and forth rotate animation.
 */
public class FlippingImageButton extends AppCompatImageButton implements ExpandableLinearLayout.ExpandBadge {
    private static final float START_DEGREES_VALUE = 0f;
    private static final float END_DEGREES_VALUE = -180f;
    private static final String KEY_SUPER_STATE = "superState";
    private static final String KEY_STATE_IS_EXPANDED = "mIsExpanded";

    private ObjectAnimator mFlipAnimator;
    private boolean mIsExpanded;

    /**
     * Simple constructor to use when creating a view from code.
     *
     * @param context The Context the view is running in, through which it can access the current theme, resources, etc
     */
    public FlippingImageButton(final Context context) {
        super(context);
        this.init();
    }

    /**
     * Constructor that is called when inflating a view from XML.
     *
     * @param context The Context the view is running in, through which it can access the current theme, resources, etc
     * @param attrs   The attributes of the XML tag that is inflating the view
     */
    public FlippingImageButton(final Context context, final AttributeSet attrs) {
        super(context, attrs);
        this.init();
    }

    /**
     * Perform inflation from XML and apply a class-specific base style from a theme attribute.
     *
     * @param context      The Context the view is running in, through which it can access the current theme, resources, etc
     * @param attrs        The attributes of the XML tag that is inflating the view
     * @param defStyleAttr An attribute in the current theme that contains a reference to a style resource that supplies default values for the view.
     *                     Can be 0 to not look for defaults
     */
    public FlippingImageButton(final Context context, final AttributeSet attrs, final int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.init();
    }

    @Override
    public final void setExpanded(final boolean isExpanded, final boolean animate) {
        if (mIsExpanded != isExpanded) {
            if (animate) {
                this.turnAnimation(isExpanded);
            } else {
                setRotation(isExpanded ? END_DEGREES_VALUE : START_DEGREES_VALUE);
            }
            mIsExpanded = isExpanded;
            setColorFilter(getResources().getColor(isExpanded ? android.R.color.white : R.color.blueIndicator));
        }
    }

    @Override
    public void setAnimationDuration(final long duration) {
        if (null == mFlipAnimator) {
            return;
        }
        mFlipAnimator.setDuration(duration);
    }

    @Override
    public void setInterpolator(final TimeInterpolator interpolator) {
        if (null == mFlipAnimator) {
            return;
        }
        mFlipAnimator.setInterpolator(interpolator);
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        final Bundle bundle = new Bundle(2);
        bundle.putParcelable(KEY_SUPER_STATE, super.onSaveInstanceState());
        bundle.putBoolean(KEY_STATE_IS_EXPANDED, mIsExpanded);
        return bundle;
    }

    @Override
    protected void onRestoreInstanceState(final Parcelable state) {
        if (state instanceof Bundle) {
            final Bundle bundle = (Bundle) state;
            this.setExpanded(bundle.getBoolean(KEY_STATE_IS_EXPANDED), false);
            super.onRestoreInstanceState(bundle.getParcelable(KEY_SUPER_STATE));
        } else {
            super.onRestoreInstanceState(state);
        }
    }

    private void init() {
        mFlipAnimator = ObjectAnimator.ofFloat(this, "rotation", 0, 0);
        mFlipAnimator.setDuration(getResources().getInteger(android.R.integer.config_mediumAnimTime));
    }

    private void turnAnimation(final boolean isExpanded) {
        if (!mFlipAnimator.isRunning()) {
            if (isExpanded) {
                mFlipAnimator.setFloatValues(END_DEGREES_VALUE, START_DEGREES_VALUE);
            } else {
                mFlipAnimator.setFloatValues(START_DEGREES_VALUE, END_DEGREES_VALUE);
            }
        }
        mFlipAnimator.reverse();
    }
}