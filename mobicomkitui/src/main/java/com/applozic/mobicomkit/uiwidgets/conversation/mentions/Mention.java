package com.applozic.mobicomkit.uiwidgets.conversation.mentions;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class Mention {
    private final CharSequence userId;
    private final CharSequence displayName;
    private final Object avatar;

    public Mention(@NonNull CharSequence userId) {
        this(userId, null);
    }

    public Mention(@NonNull CharSequence userId, @Nullable CharSequence displayName) {
        this(userId, displayName, null);
    }

    public Mention(@NonNull CharSequence userId, @Nullable CharSequence displayName, @Nullable Object avatar) {
        this.userId = userId;
        this.displayName = displayName;
        this.avatar = avatar;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        return obj instanceof Mention && ((Mention) obj).userId == userId;
    }

    @Override
    public int hashCode() {
        return userId.hashCode();
    }

    @NonNull
    @Override
    public String toString() {
        return userId.toString();
    }

    @NonNull
    public CharSequence getUserId() {
        return userId;
    }

    @Nullable
    public CharSequence getDisplayName() {
        return displayName;
    }

    @Nullable
    public Object getAvatar() {
        return avatar;
    }
}