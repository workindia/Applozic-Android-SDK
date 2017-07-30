package com.applozic.mobicomkit.uiwidgets.conversation.fragment;

import android.os.Build;
import android.view.View;
import android.view.animation.Animation;

import java.lang.ref.WeakReference;
import java.util.WeakHashMap;

public class View10 extends Animation {

    private static final WeakHashMap<View, View10> PROXIES = new WeakHashMap<View, View10>();
    public static boolean NEED_PROXY = Build.VERSION.SDK_INT < 11;
    private final WeakReference<View> mView;
    private float mAlpha = 1;

    private View10(View view) {
        setDuration(0);
        setFillAfter(true);
        view.setAnimation(this);
        mView = new WeakReference<View>(view);
    }

    public static View10 wrap(View view) {
        View10 proxy = PROXIES.get(view);
        Animation animation = view.getAnimation();
        if (proxy == null || proxy != animation && animation != null) {
            proxy = new View10(view);
            PROXIES.put(view, proxy);
        } else if (animation == null) {
            view.setAnimation(proxy);
        }
        return proxy;
    }

    public float getAlpha() {
        return mAlpha;
    }

    public void setAlpha(float alpha) {
        if (mAlpha != alpha) {
            mAlpha = alpha;
            View view = mView.get();
            if (view != null) {
                view.invalidate();
            }
        }
    }

    public float getX() {
        View view = mView.get();
        if (view == null) {
            return 0;
        }
        return view.getLeft();
    }
}
