package com.elena.listentogether.ui.custom.expandablelayout;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.SparseArray;
import android.view.ViewGroup;

/**
 * View state helper for correct (without overlapping) saving view tree in {@link ViewGroup} with the same ids.
 * <p>
 * By default implementation of saving view state if parent {@link ViewGroup} have some views in view tree,
 * saving instance state (view state) is going with key of id (integer) and each view with the same id replaces the state of previous view.
 * <p>
 * With this helper each view saves state in independent bundle avoiding overlapping by other views with the same id.
 */
public final class ChildrenViewStateHelper {
    public static final String DEFAULT_CHILDREN_STATE_KEY = ChildrenViewStateHelper.class.getSimpleName() + ".childrenState";
    private ViewGroup mClientViewGroup;

    /**
     * Creates {@link ChildrenViewStateHelper} object.
     */
    public static ChildrenViewStateHelper newInstance(@NonNull final ViewGroup viewGroup) {
        final ChildrenViewStateHelper handler = new ChildrenViewStateHelper();
        handler.mClientViewGroup = viewGroup;
        return handler;
    }

    private ChildrenViewStateHelper() {
        //use static factory method
    }

    @NonNull
    public SparseArray<Parcelable> saveChildrenState() {
        final SparseArray<Parcelable> parentStateArray = new SparseArray<>();
        for (int childIndex = 0; childIndex < mClientViewGroup.getChildCount(); childIndex++) {
            final Bundle bundle = new Bundle();
            final SparseArray<Parcelable> childStateArray = new SparseArray<>(); //create independent SparseArray for each child (View or ViewGroup)
            mClientViewGroup.getChildAt(childIndex).saveHierarchyState(childStateArray);
            bundle.putSparseParcelableArray(DEFAULT_CHILDREN_STATE_KEY, childStateArray);
            parentStateArray.append(childIndex, bundle);
        }
        return parentStateArray;
    }



    public void restoreChildrenState(@Nullable final SparseArray<Parcelable> childrenState) {
        if (childrenState == null) {
            return;
        }
        for (int childIndex = 0; childIndex < mClientViewGroup.getChildCount(); childIndex++) {
            final Bundle bundle = (Bundle) childrenState.get(childIndex);
            final SparseArray<Parcelable> childState = bundle.getSparseParcelableArray(DEFAULT_CHILDREN_STATE_KEY);
            mClientViewGroup.getChildAt(childIndex).restoreHierarchyState(childState);
        }
    }
}
