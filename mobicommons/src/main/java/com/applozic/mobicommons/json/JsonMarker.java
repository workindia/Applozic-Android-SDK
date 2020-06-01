package com.applozic.mobicommons.json;

import androidx.annotation.NonNull;

import java.io.Serializable;

/**
 * Created by adarsh on 6/8/15.
 */

public class JsonMarker implements Serializable {

    @NonNull
    @Override
    public String toString() {
        return GsonUtils.getJsonFromObject(this, JsonMarker.class);
    }
}
