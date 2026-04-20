package com.emergency.aria.databinding;
import com.emergency.aria.R;
import com.emergency.aria.BR;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.View;
@SuppressWarnings("unchecked")
public class ActivityMainBindingImpl extends ActivityMainBinding  {

    @Nullable
    private static final androidx.databinding.ViewDataBinding.IncludedLayouts sIncludes;
    @Nullable
    private static final android.util.SparseIntArray sViewsWithIds;
    static {
        sIncludes = null;
        sViewsWithIds = new android.util.SparseIntArray();
        sViewsWithIds.put(R.id.tvSyncBadge, 1);
        sViewsWithIds.put(R.id.tvMode, 2);
        sViewsWithIds.put(R.id.header, 3);
        sViewsWithIds.put(R.id.tvUserName, 4);
        sViewsWithIds.put(R.id.tvRole, 5);
        sViewsWithIds.put(R.id.tvRoleHint, 6);
        sViewsWithIds.put(R.id.cardIncomingAlert, 7);
        sViewsWithIds.put(R.id.btnSos, 8);
        sViewsWithIds.put(R.id.tvStatus, 9);
        sViewsWithIds.put(R.id.sensorPanel, 10);
        sViewsWithIds.put(R.id.ivVoiceIndicator, 11);
        sViewsWithIds.put(R.id.ivFaceIndicator, 12);
        sViewsWithIds.put(R.id.tvFaceStatus, 13);
        sViewsWithIds.put(R.id.actionButtons, 14);
        sViewsWithIds.put(R.id.btnAcceptCase, 15);
        sViewsWithIds.put(R.id.btnTrackVictims, 16);
        sViewsWithIds.put(R.id.btnNavigate, 17);
        sViewsWithIds.put(R.id.cardFallWarning, 18);
        sViewsWithIds.put(R.id.tvCountdown, 19);
        sViewsWithIds.put(R.id.btnImOk, 20);
        sViewsWithIds.put(R.id.bottomNav, 21);
    }
    // views
    @NonNull
    private final androidx.constraintlayout.widget.ConstraintLayout mboundView0;
    // variables
    // values
    // listeners
    // Inverse Binding Event Handlers

    public ActivityMainBindingImpl(@Nullable androidx.databinding.DataBindingComponent bindingComponent, @NonNull View root) {
        this(bindingComponent, root, mapBindings(bindingComponent, root, 22, sIncludes, sViewsWithIds));
    }
    private ActivityMainBindingImpl(androidx.databinding.DataBindingComponent bindingComponent, View root, Object[] bindings) {
        super(bindingComponent, root, 0
            , (android.widget.LinearLayout) bindings[14]
            , (com.google.android.material.bottomnavigation.BottomNavigationView) bindings[21]
            , (android.widget.Button) bindings[15]
            , (com.google.android.material.button.MaterialButton) bindings[20]
            , (android.widget.Button) bindings[17]
            , (com.google.android.material.button.MaterialButton) bindings[8]
            , (android.widget.Button) bindings[16]
            , (com.google.android.material.card.MaterialCardView) bindings[18]
            , (com.google.android.material.card.MaterialCardView) bindings[7]
            , (android.widget.LinearLayout) bindings[3]
            , (android.widget.ImageView) bindings[12]
            , (android.widget.ImageView) bindings[11]
            , (android.widget.LinearLayout) bindings[10]
            , (android.widget.TextView) bindings[19]
            , (android.widget.TextView) bindings[13]
            , (android.widget.TextView) bindings[2]
            , (android.widget.TextView) bindings[5]
            , (android.widget.TextView) bindings[6]
            , (android.widget.TextView) bindings[9]
            , (android.widget.TextView) bindings[1]
            , (android.widget.TextView) bindings[4]
            );
        this.mboundView0 = (androidx.constraintlayout.widget.ConstraintLayout) bindings[0];
        this.mboundView0.setTag(null);
        setRootTag(root);
        // listeners
        invalidateAll();
    }

    @Override
    public void invalidateAll() {
        synchronized(this) {
                mDirtyFlags = 0x1L;
        }
        requestRebind();
    }

    @Override
    public boolean hasPendingBindings() {
        synchronized(this) {
            if (mDirtyFlags != 0) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean setVariable(int variableId, @Nullable Object variable)  {
        boolean variableSet = true;
            return variableSet;
    }

    @Override
    protected boolean onFieldChange(int localFieldId, Object object, int fieldId) {
        switch (localFieldId) {
        }
        return false;
    }

    @Override
    protected void executeBindings() {
        long dirtyFlags = 0;
        synchronized(this) {
            dirtyFlags = mDirtyFlags;
            mDirtyFlags = 0;
        }
        // batch finished
    }
    // Listener Stub Implementations
    // callback impls
    // dirty flag
    private  long mDirtyFlags = 0xffffffffffffffffL;
    /* flag mapping
        flag 0 (0x1L): null
    flag mapping end*/
    //end
}