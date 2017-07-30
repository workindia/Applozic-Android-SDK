package com.applozic.mobicomkit.uiwidgets.conversation.fragment;

import android.view.View;

public class ViewProxy {


    public static void setAlpha(View view, float alpha) {
        if (View10.NEED_PROXY) {
            View10.wrap(view).setAlpha(alpha);
        } else {
            view.setAlpha(alpha);
        }
    }

    public static float getX(View view) {
        if (View10.NEED_PROXY) {
            return View10.wrap(view).getX();
        } else {
            return view.getX();
        }
    }
}
