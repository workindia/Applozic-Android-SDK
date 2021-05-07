package com.applozic.mobicomkit.uiwidgets.conversation.mentions;

import android.content.Context;
import android.graphics.Color;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.CharacterStyle;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatMultiAutoCompleteTextView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MentionAutoCompleteTextView extends AppCompatMultiAutoCompleteTextView {
    private ArrayAdapter<Mention> mentionAdapter;
    @Nullable private Pattern mentionPattern;
    @Nullable private OnClickListener mentionClickListener;
    @Nullable private OnChangedListener mentionChangedListener;
    private boolean mentionEditing;

    public MentionAutoCompleteTextView(Context context) {
        super(context);
    }

    public MentionAutoCompleteTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MentionAutoCompleteTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void initMentions(ArrayAdapter<Mention> mentionAdapter) {
        addTextChangedListener(textWatcher);
        setTokenizer(new CharTokenizer());
        setMentionAdapter(mentionAdapter);
        setAdapter(mentionAdapter);
        setThreshold(0);

        recolorize();
    }

    public boolean isMentionEnabled() {
        return true;
    }

    @Override
    public boolean enoughToFilter() {
        return true;
    }

    @SuppressWarnings("FieldCanBeLocal")
    private final TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            if (count > 0 && start > 0) {
                final char c = s.charAt(start - 1);
                if(c == '@') {
                    mentionEditing = true;
                }
                if (!Character.isLetterOrDigit(c)) {
                    mentionEditing = false;
                } else if (mentionChangedListener != null && mentionEditing) {
                    mentionChangedListener.onChanged(MentionAutoCompleteTextView.this, s.subSequence(
                            indexOfPreviousNonLetterDigit(s, 0, start - 1) + 1, start
                    ));
                }
            }
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (s.length() == 0) {
                return;
            }
            recolorize();
            if (start < s.length()) {
                final int index = start + count - 1;
                if (index < 0) {
                    return;
                }
                if (s.charAt(index) == '@') {
                    mentionEditing = true;
                    performFiltering("", 0);
                }
                if (!Character.isLetterOrDigit(s.charAt(start))) {
                    mentionEditing = false;
                } else if (mentionChangedListener != null && mentionEditing) {
                    mentionChangedListener.onChanged(MentionAutoCompleteTextView.this, s.subSequence(
                            indexOfPreviousNonLetterDigit(s, 0, start) + 1, start + count
                    ));
                }
            }
        }

        @Override
        public void afterTextChanged(Editable s) { }
    };

    @Nullable
    public ArrayAdapter<Mention> getMentionAdapter() {
        return mentionAdapter;
    }

    public void setMentionAdapter(@Nullable ArrayAdapter adapter) {
        mentionAdapter = adapter;
    }

    @NonNull
    public Pattern getMentionPattern() {
        return mentionPattern != null ? mentionPattern : Pattern.compile("@(\\w+)");
    }

    public void setMentionPattern(@Nullable Pattern pattern) {
        if (mentionPattern != null) {
            mentionPattern = pattern;
            recolorize();
        }
    }

    public void setOnMentionClickListener(@Nullable OnClickListener listener) {
        mentionClickListener = listener;
        recolorize();
    }

    public void setMentionTextChangedListener(@Nullable OnChangedListener listener) {
        mentionChangedListener = listener;
    }

    @NonNull
    public List<String> getMentions() {
        return listOf(getText(), getMentionPattern());
    }

    private void recolorize() {
        final Spannable spannable = getText();
        if (spannable == null || TextUtils.isEmpty(spannable.toString())) {
            return;
        }

        for (final Object span : spannable.getSpans(0, spannable.length(), CharacterStyle.class)) {
            spannable.removeSpan(span);
        }

        if (isMentionEnabled()) {
            spanAll(spannable, getMentionPattern(), new Callable<CharacterStyle>() {
                        @Override
                        public CharacterStyle call() {
                            return mentionClickListener != null
                                    ? new MentionAutoCompleteTextView.MentionClickableSpan(mentionClickListener)
                                    : new ForegroundColorSpan(Color.RED);
                        }
                    }
            );
        }
    }

    private static int indexOfNextNonLetterDigit(CharSequence text, int start) {
        for (int i = start + 1; i < text.length(); i++) {
            if (!Character.isLetterOrDigit(text.charAt(i))) {
                return i;
            }
        }
        return text.length();
    }

    private static int indexOfPreviousNonLetterDigit(CharSequence text, int start, int end) {
        for (int i = end; i > start; i--) {
            if (!Character.isLetterOrDigit(text.charAt(i))) {
                return i;
            }
        }
        return start;
    }

    private static void spanAll(Spannable spannable, Pattern pattern, Callable<CharacterStyle> styleSupplier) {
        try {
            final Matcher matcher = pattern.matcher(spannable);
            while (matcher.find()) {
                final int start = matcher.start();
                final int end = matcher.end();
                final Object span = styleSupplier.call();
                spannable.setSpan(span, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                if (span instanceof MentionAutoCompleteTextView.MentionClickableSpan) {
                    ((MentionAutoCompleteTextView.MentionClickableSpan) span).text = spannable.subSequence(start, end);
                }
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    private static List<String> listOf(CharSequence text, Pattern pattern) {
        final List<String> list = new ArrayList<>();
        final Matcher matcher = pattern.matcher(text);
        while (matcher.find()) {
            list.add(matcher.group(1));
        }
        return list;
    }

    private class CharTokenizer implements Tokenizer {
        private final Collection<Character> chars = new ArrayList<>();

        CharTokenizer() {
            if (isMentionEnabled()) {
                chars.add('@');
            }
        }

        @Override
        public int findTokenStart(CharSequence text, int cursor) {
            int i = cursor;

            while (i > 0 && !chars.contains(text.charAt(i - 1))) {
                i--;
            }
            while (i < cursor && text.charAt(i) == ' ') {
                i++;
            }

            // imperfect fix for dropdown still showing without symbol found
            if (i == 0 && isPopupShowing()) {
                dismissDropDown();
            }
            return i;
        }

        @Override
        public int findTokenEnd(CharSequence text, int cursor) {
            int i = cursor;
            int len = text.length();

            while (i < len) {
                if (chars.contains(text.charAt(i))) {
                    return i;
                } else {
                    i++;
                }
            }

            return len;
        }

        @Override
        public CharSequence terminateToken(CharSequence text) {
            int i = text.length();

            while (i > 0 && text.charAt(i - 1) == ' ') {
                i--;
            }

            if (i > 0 && chars.contains(text.charAt(i - 1))) {
                return text;
            } else {
                if (text instanceof Spanned) {
                    final Spannable sp = new SpannableString(text + " ");
                    TextUtils.copySpansFrom((Spanned) text, 0, text.length(), Object.class, sp, 0);
                    return sp;
                } else {
                    return text + " ";
                }
            }
        }
    }

    private static class MentionClickableSpan extends ClickableSpan {
        private final OnClickListener listener;
        private final int color;
        private CharSequence text;

        private MentionClickableSpan(OnClickListener listener) {
            this.listener = listener;
            this.color = Color.RED;
        }

        @Override
        public void onClick(@NonNull View widget) {
            if (!(widget instanceof MentionAutoCompleteTextView)) {
                throw new IllegalStateException("Clicked widget is not an instance of MentionView.");
            }
            listener.onClick((MentionAutoCompleteTextView) widget, text.subSequence(1, text.length()));
        }

        @Override
        public void updateDrawState(@NonNull TextPaint ds) {
            ds.setColor(color);
        }
    }

    interface OnClickListener {
        void onClick(@NonNull MentionAutoCompleteTextView view, @NonNull CharSequence text);
    }

    interface OnChangedListener {
        void onChanged(@NonNull MentionAutoCompleteTextView view, @NonNull CharSequence text);
    }
}