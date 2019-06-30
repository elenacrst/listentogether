package com.elena.listentogether.ui.custom.expandablelayout;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.NestedScrollView;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.animation.DecelerateInterpolator;
import android.widget.LinearLayout;

import com.elena.listentogether.utils.ViewUtils;


/**
 * The parent layout provides expand/collapse animation for it's the second components.
 */
@SuppressWarnings({
        "PMD.GodClass",
        "PMD.TooManyMethods",
        "PMD.CyclomaticComplexity",
        "PMD.ModifiedCyclomaticComplexity",
        "PMD.StdCyclomaticComplexity",
})
public class ExpandableLinearLayout extends ForegroundLinearLayout {
    private static final int MAX_ANIMATION_DURATION = 500;
    private static final float ANIMATION_RATE = 0.75f;

    private static final String SUPER_STATE_KEY = "superState";
    private static final String IS_EXPANDED_KEY = "isExpanded";
    private static final int BUNDLE_STATE_CAPACITY = 3;

    private boolean mIsExpandable;
    private boolean mIsExpanded;
    private ValueAnimator mAnimator;

    private View mHeaderView;
    private View mExpandedContentView;
    private OnExpandListener mListener;
    private ExpandBadge mFlippingImageButton;

    /**
     * Callback to be called when an {@link ExpandableLinearLayout} has changed its state.
     */
    public interface OnExpandListener {
        /**
         * Called when an {@link ExpandableLinearLayout} has changed its state.
         *
         * @param isExpanded the state
         */
        void onExpandableStateChanged(boolean isExpanded);
    }

    /**
     * An interface for a view that can be set as the expanding/collapsing icon view with special animation.
     */
    public interface ExpandBadge {
        /**
         * Sets state of the button.
         *
         * @param isExpanded If {@code true} the badge should be expanded, {@code false} otherwise
         * @param animate    If {@code true} the badge uses animation
         */
        void setExpanded(final boolean isExpanded, final boolean animate);

        /**
         * Set the enabled state of this view.
         *
         * @param visibility One of {@link #VISIBLE}, {@link #INVISIBLE}, or {@link #GONE}.
         */
        void setVisibility(int visibility);

        /**
         * Sets animation duration of flipping button.
         *
         * @param duration duration of animation
         */
        void setAnimationDuration(long duration);

        /**
         * Sets animation interpolator.
         *
         * @param interpolator animation {@link TimeInterpolator}
         */
        void setInterpolator(TimeInterpolator interpolator);
    }

    /**
     * Simple constructor to use when creating a view from code.
     *
     * @param context The Context the view is running in, through which it can access the current theme, resources, etc
     */
    public ExpandableLinearLayout(final Context context) {
        super(context);
        this.init();
    }

    /**
     * Constructor that is called when inflating a view from XML.
     *
     * @param context The Context the view is running in, through which it can access the current theme, resources, etc
     * @param attrs   The attributes of the XML tag that is inflating the view
     */
    public ExpandableLinearLayout(final Context context, final AttributeSet attrs) {
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
    public ExpandableLinearLayout(final Context context, final AttributeSet attrs, final int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.init();
    }

    /**
     * Perform inflation from XML and apply a class-specific base style from a theme attribute or style resource.
     *
     * @param context      The Context the view is running in, through which it can access the current theme, resources, etc
     * @param attrs        The attributes of the XML tag that is inflating the view
     * @param defStyleAttr An attribute in the current theme that contains a reference to a style resource that supplies default values for the view.
     *                     Can be 0 to not look for defaults
     * @param defStyleRes  A resource identifier of a style resource that supplies default values for the view, used only if defStyleAttr is 0 or can not be found in the theme.
     *                     Can be 0 to not look for defaults.
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public ExpandableLinearLayout(final Context context, final AttributeSet attrs, final int defStyleAttr, final int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.init();
    }

    private void init() {
        setOrientation(VERTICAL);
        setBaselineAligned(false);
        mIsExpandable = true;
    }

    /**
     * Set listener.
     *
     * @param listener the listener
     */
    public void setOnExpandListener(@Nullable final OnExpandListener listener) {
        this.mListener = listener;
    }

    @Override
    public void setEnabled(final boolean enabled) {
        super.setEnabled(enabled);
        if (mHeaderView != null) {
            mHeaderView.setEnabled(enabled);
        }
    }

    /**
     * Returns whether this {@link ExpandableLinearLayout} is expanded.
     *
     * @return {@code true} if this {@link ExpandableLinearLayout} is expanded, {@code false}  otherwise.
     */
    public final boolean isExpanded() {
        return mIsExpanded;
    }

    /**
     * Set expanded/collapsible mode for {@link ExpandableLinearLayout}.
     *
     * @param expandable If {@code true} then {@link ExpandableLinearLayout} is expandable, {@code false} otherwise.
     */
    public void setExpandable(final boolean expandable) {
        if (mIsExpandable != expandable) {
            mIsExpandable = expandable;
            this.updateExpandableMode();
            this.setExpanded(!expandable);
        }
    }

    /**
     * Perform expansion.
     */
    public final void expand() {
        if (isEnabled() && mExpandedContentView != null && !mIsExpanded) {
            this.setupAnimation();
            this.toggleBadge(true);
            mAnimator.reverse();
            mIsExpanded = true;
        }
    }

    /**
     * Perform collapse.
     */
    public final void collapse() {
        if (isEnabled() && mExpandedContentView != null && mIsExpanded) {
            this.setupAnimation();
            this.toggleBadge(false);
            mAnimator.reverse();
            mIsExpanded = false;
        }
    }

    /**
     * Sets the expanded state.
     *
     * @param expanded {@code true} if the content view should be expanded, {@code false} otherwise
     */
    public void setExpanded(final boolean expanded) {
        if (mIsExpanded != expanded) {
            if (mAnimator != null && mAnimator.isRunning()) {
                mAnimator.end();
            } else {
                if (mExpandedContentView != null) {
                    mExpandedContentView.setVisibility(expanded ? VISIBLE : GONE);
                }
                mIsExpanded = expanded;
            }
        }
        if (mFlippingImageButton != null) {
            mFlippingImageButton.setExpanded(expanded, false);
        }
    }

    private void updateExpandableMode() {
        if (mFlippingImageButton != null) {
            mFlippingImageButton.setVisibility(mIsExpandable ? VISIBLE : GONE);
        }
        if (mHeaderView != null) {
            mHeaderView.setClickable(mIsExpandable);
        }
    }

    private void toggleBadge(final boolean isExpanded) {
        if (mFlippingImageButton != null) {
            mFlippingImageButton.setExpanded(isExpanded, true);
        }
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        final Bundle bundle = new Bundle(BUNDLE_STATE_CAPACITY);
        bundle.putParcelable(SUPER_STATE_KEY, super.onSaveInstanceState());
        bundle.putBoolean(IS_EXPANDED_KEY, mIsExpanded);
        bundle.putSparseParcelableArray(ChildrenViewStateHelper.DEFAULT_CHILDREN_STATE_KEY, ChildrenViewStateHelper.newInstance(this).saveChildrenState());
        return bundle;
    }

    @Override
    protected void onRestoreInstanceState(final Parcelable state) {
        if (state instanceof Bundle) {
            final Bundle bundle = (Bundle) state;
            this.setExpanded(bundle.getBoolean(IS_EXPANDED_KEY));
            ChildrenViewStateHelper.newInstance(this).restoreChildrenState(bundle.getSparseParcelableArray(ChildrenViewStateHelper.DEFAULT_CHILDREN_STATE_KEY));
            super.onRestoreInstanceState(bundle.getParcelable(SUPER_STATE_KEY));
        } else {
            super.onRestoreInstanceState(state);
        }
    }

    @Override
    protected void dispatchSaveInstanceState(final SparseArray<Parcelable> container) {
        dispatchFreezeSelfOnly(container); //we need override this method in conjunction with using {@link ChildrenViewStateHelper}
    }

    @Override
    protected void dispatchRestoreInstanceState(final SparseArray<Parcelable> container) {
        dispatchThawSelfOnly(container); //we need override this method in conjunction with using {@link ChildrenViewStateHelper}
    }

    @Override
    public void addView(@NonNull final View child, final int index, final ViewGroup.LayoutParams params) {
        super.addView(child, index, params);
        if (index > 0) {
            final String privateMessage = "The index should be -1. index = " + index;
            throw new IllegalStateException(privateMessage);
        }
    }

    @Override
    public void onViewAdded(final View child) {
        super.onViewAdded(child);
        if (mHeaderView == null) {
            mHeaderView = child;
            if (mHeaderView.isClickable()) {
                mHeaderView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(@NonNull final View view) {
                        if (mIsExpanded) {
                            ExpandableLinearLayout.this.collapse();
                        } else {
                            ExpandableLinearLayout.this.expand();
                        }
                    }
                });
                mFlippingImageButton = ViewUtils.findViewByClass(this, ExpandBadge.class);
            } else {
                mFlippingImageButton = ViewUtils.findViewByClass(this, ExpandBadge.class);
                if (mFlippingImageButton instanceof View) {
                    ((View) mFlippingImageButton).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(@NonNull final View view) {
                            if (mIsExpanded) {
                                ExpandableLinearLayout.this.collapse();
                            } else {
                                ExpandableLinearLayout.this.expand();
                            }
                        }
                    });
                }
            }
            this.onHeaderViewUpdated();
        } else if (mExpandedContentView == null) {
            mExpandedContentView = child;
            if (!isInEditMode()) {
                mExpandedContentView.setVisibility(mIsExpanded ? VISIBLE : GONE);
            }
            this.onContentViewUpdated();
        } else {
            final String privateMessage = "There can be only two children, this view (" + child.getClass().getName() + " is excessive";
            throw new IllegalStateException(privateMessage);
        }
    }

    @Override
    public void onViewRemoved(@NonNull final View child) {
        super.onViewRemoved(child);
        if (mHeaderView == child) { // NOPMD
            mHeaderView.setOnClickListener(null);
            mHeaderView = null; // NOPMD We need a nullable reference since we have removed this child from the view hierarchy
        } else if (mExpandedContentView == child) { // NOPMD
            if (mAnimator != null) {
                mAnimator.removeAllUpdateListeners();
                mAnimator.removeAllListeners();
                mAnimator = null; // NOPMD We need a nullable reference since we have removed this child from the view hierarchy
            }
            mExpandedContentView = null; // NOPMD We need a nullable reference since we have removed this child from the view hierarchy
        }
    }

    /**
     * Called when the header view is added.
     */
    protected void onHeaderViewUpdated() {
        //nothing here
    }

    /**
     * Called when the expandable content view is added.
     */
    protected void onContentViewUpdated() {
        this.updateExpandableMode();
    }

    private void setupAnimation() {
        if (mAnimator == null || !mAnimator.isRunning()) {
            if (mAnimator == null) {
                this.createExpandAnimator();
            }
            final int measuredHeight = this.getMeasuredContentHeight();
            mAnimator.setDuration(measuredHeight > MAX_ANIMATION_DURATION ? MAX_ANIMATION_DURATION : (long) (measuredHeight * ANIMATION_RATE));
            if (mIsExpanded) {
                mAnimator.setIntValues(0, measuredHeight);
            } else {
                mAnimator.setIntValues(measuredHeight, 0);
            }

            if (mFlippingImageButton != null) {
                mFlippingImageButton.setAnimationDuration(mAnimator.getDuration());
                mFlippingImageButton.setInterpolator(mAnimator.getInterpolator());
            }
        }
    }

    private void createExpandAnimator() {
        mAnimator = new ValueAnimator();
        mAnimator.setDuration(getResources().getInteger(android.R.integer.config_longAnimTime));
        mAnimator.setInterpolator(new DecelerateInterpolator());

        final AnimatorListener animatorListener = new AnimatorListener(findViewInHierarchy(ExpandableLinearLayout.this, NestedScrollView.class));
        mAnimator.addUpdateListener(animatorListener);
        mAnimator.addListener(animatorListener);
    }

    private int getMeasuredContentHeight() {
        final int widthSpec = View.MeasureSpec.makeMeasureSpec(getMeasuredWidth() - getPaddingLeft() - getPaddingRight(), View.MeasureSpec.AT_MOST);
        final int heightSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
       // mExpandedContentView.measure(widthSpec, heightSpec);
        return mExpandedContentView.getMeasuredHeight();
    }
    //todo animation for expanding the list

    private class AnimatorListener extends AnimatorListenerAdapter implements ValueAnimator.AnimatorUpdateListener {
        final Rect mThisRect = new Rect();
        final Rect mScrollRect = new Rect();
        final NestedScrollView mScrollView;
        final int mOffset = (int) (getResources().getDisplayMetrics().scaledDensity * 20);

        AnimatorListener(@Nullable final NestedScrollView scrollView) {
            mScrollView = scrollView;
            if (mScrollView != null) {
                this.mScrollView.getGlobalVisibleRect(mScrollRect);
            }
        }


        @Override
        public void onAnimationUpdate(@NonNull final ValueAnimator valueAnimator) {
            mExpandedContentView.getLayoutParams().height = (int) valueAnimator.getAnimatedValue();
            mExpandedContentView.requestLayout();

            ExpandableLinearLayout.this.getGlobalVisibleRect(mThisRect);
            mThisRect.bottom = mThisRect.top + getHeight();

            if (mIsExpandable && null != mScrollView && mThisRect.height() < mScrollRect.height() && mScrollRect.bottom < mThisRect.bottom) {
                mScrollView.scrollBy(0, mThisRect.bottom - mScrollRect.bottom + mOffset);
            }
        }

        @Override
        public void onAnimationStart(final Animator animation) {
            if (!mIsExpanded) {
                mExpandedContentView.setVisibility(VISIBLE);
            }
        }

        @Override
        public void onAnimationEnd(@NonNull final Animator animation) {
            if (mIsExpanded) {
                mExpandedContentView.getLayoutParams().height = LinearLayout.LayoutParams.WRAP_CONTENT;
            } else {
                mExpandedContentView.setVisibility(GONE);
            }
            if (mListener != null) {
                mListener.onExpandableStateChanged(mIsExpanded);
            }
        }
    }

    /**
     * Looks for a top view with the given class. If this view is instance of the given class, return this view.
     *
     * @param clazz The clazz to search for.
     * @return a view that has the given id in the hierarchy or null
     */
    @Nullable
    public static <T> T findViewInHierarchy(@NonNull final View view, @NonNull final Class<T> clazz) {
        if (clazz.isAssignableFrom(view.getClass())) {
            return clazz.cast(view);
        } else {
            final ViewParent viewParent = view.getParent();
            if (viewParent instanceof View) {
                return findViewInHierarchy((View) viewParent, clazz);
            }
            return null;
        }
    }
}