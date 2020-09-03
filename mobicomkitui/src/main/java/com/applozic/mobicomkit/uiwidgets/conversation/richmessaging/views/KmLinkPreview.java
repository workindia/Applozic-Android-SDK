package com.applozic.mobicomkit.uiwidgets.conversation.richmessaging.views;

import android.content.Context;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.applozic.mobicomkit.api.conversation.Message;
import com.applozic.mobicomkit.listners.AlCallback;
import com.applozic.mobicomkit.uiwidgets.AlCustomizationSettings;
import com.applozic.mobicomkit.uiwidgets.R;
import com.applozic.mobicomkit.uiwidgets.async.AlMessageMetadataUpdateTask;
import com.applozic.mobicomkit.uiwidgets.conversation.richmessaging.models.KmLinkPreviewModel;
import com.applozic.mobicomkit.uiwidgets.conversation.richmessaging.utils.KmRegexHelper;
import com.applozic.mobicommons.commons.core.utils.Utils;
import com.applozic.mobicommons.json.GsonUtils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.GlideException;

import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class KmLinkPreview {
    public static final String LINK_PREVIEW_META_KEY = "KM_LINK_PREVIEW_META_KEY";
    private Context context;
    private Message message;
    private RelativeLayout urlLoadLayout;
    private AlCustomizationSettings alCustomizationSettings;
    private ImageView imageView;
    private TextView titleText;
    private TextView descriptionText;
    private ImageView imageOnlyView;

    public KmLinkPreview(Context context, Message message, RelativeLayout urlLoadLayout, AlCustomizationSettings alCustomizationSettings) {
        this.context = context;
        this.message = message;
        this.urlLoadLayout = urlLoadLayout;
        this.alCustomizationSettings = alCustomizationSettings;
        this.imageView = urlLoadLayout.findViewById(R.id.url_image);
        this.titleText = urlLoadLayout.findViewById(R.id.url_header);
        this.descriptionText = urlLoadLayout.findViewById(R.id.url_body);
        this.imageOnlyView = urlLoadLayout.findViewById(R.id.image_only_view);
    }

    public void createView() {
        KmLinkPreviewModel existingLinkModel = getUrlMetaModel();
        if (existingLinkModel != null) {
            updateViews(existingLinkModel);
        } else {
            urlLoadLayout.setVisibility(View.GONE);
            new UrlLoader(context, message, new AlCallback() {
                @Override
                public void onSuccess(Object response) {

                }

                @Override
                public void onError(Object error) {

                }
            }).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
    }

    public void updateViews(KmLinkPreviewModel linkPreviewModel) {
        if (linkPreviewModel != null && linkPreviewModel.hasLinkData()) {
            urlLoadLayout.setVisibility(View.VISIBLE);

            if (linkPreviewModel.hasImageOnly()) {
                toggleImageOnlyVisibility(true);
                Glide.with(context).load(linkPreviewModel.getImageLink()).into(imageOnlyView);
            } else {
                toggleImageOnlyVisibility(false);
                titleText.setText(linkPreviewModel.getTitle());
                descriptionText.setText(linkPreviewModel.getDescription());
                if (!TextUtils.isEmpty(linkPreviewModel.getImageLink())) {
                    imageView.setVisibility(View.VISIBLE);
                    Glide.with(context).load(linkPreviewModel.getImageLink()).into(imageView);
                } else {
                    imageView.setVisibility(View.GONE);
                }
            }
        } else {
            urlLoadLayout.setVisibility(View.GONE);
        }
    }

    private void toggleImageOnlyVisibility(boolean showImageOnly) {
        imageOnlyView.setVisibility(showImageOnly ? View.VISIBLE : View.GONE);
        imageView.setVisibility(showImageOnly ? View.GONE : View.VISIBLE);
        titleText.setVisibility(showImageOnly ? View.GONE : View.VISIBLE);
        descriptionText.setVisibility(showImageOnly ? View.GONE : View.VISIBLE);
    }

    public KmLinkPreviewModel getUrlMetaModel() {
        try {
            if (message.getMetadata() != null && message.getMetadata().containsKey(LINK_PREVIEW_META_KEY)) {
                return (KmLinkPreviewModel) GsonUtils.getObjectFromJson(message.getMetadata().get(LINK_PREVIEW_META_KEY), KmLinkPreviewModel.class);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static class UrlLoader extends AsyncTask<Void, Void, KmLinkPreviewModel> {

        private WeakReference<Context> context;
        private Message message;
        private AlCallback callback;

        public UrlLoader(Context context, Message message, AlCallback callback) {
            this.context = new WeakReference<>(context);
            this.message = message;
            this.callback = callback;
        }

        @Override
        protected KmLinkPreviewModel doInBackground(Void... voids) {
            String validUrl = getValidUrl(message);
            KmLinkPreviewModel linkPreviewModel = null;
            try {
                validUrl = getValidUrl(message);
                if (!TextUtils.isEmpty(validUrl) && Pattern.compile(KmRegexHelper.IMAGE_PATTERN).matcher(validUrl).matches()) {
                    linkPreviewModel = new KmLinkPreviewModel();
                    linkPreviewModel.setImageLink(validUrl);
                } else {
                    Document document = Jsoup.connect(validUrl).get();
                    linkPreviewModel = getMetaTags(document.toString());
                    if (TextUtils.isEmpty(linkPreviewModel.getTitle())) {
                        linkPreviewModel.setTitle(document.title());
                    }
                    if (!TextUtils.isEmpty(linkPreviewModel.getImageLink()) &&
                            !(linkPreviewModel.getImageLink().startsWith(KmRegexHelper.HTTP_PROTOCOL) || linkPreviewModel.getImageLink().startsWith(KmRegexHelper.HTTPS_PROTOCOL))) {
                        linkPreviewModel.setImageLink(getValidUrl(message) + linkPreviewModel.getImageLink());
                    }
                }
                return linkPreviewModel;
            } catch (HttpStatusException e) {
                if (linkPreviewModel == null) {
                    linkPreviewModel = new KmLinkPreviewModel();
                }
                linkPreviewModel.setInvalidUrl(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return linkPreviewModel;
        }

        @Override
        protected void onPostExecute(final KmLinkPreviewModel urlMetaModel) {
            if (callback != null) {
                if (urlMetaModel != null) {
                    if (urlMetaModel.hasLinkData()) {
                        Map<String, String> metadata = message.getMetadata();
                        if (metadata == null) {
                            metadata = new HashMap<>();
                        }
                        metadata.put(LINK_PREVIEW_META_KEY, GsonUtils.getJsonFromObject(urlMetaModel, KmLinkPreviewModel.class));
                        new AlMessageMetadataUpdateTask(context.get(), message.getKeyString(), metadata, new AlMessageMetadataUpdateTask.MessageMetadataListener() {
                            @Override
                            public void onSuccess(Context context, String message) {
                                callback.onSuccess(urlMetaModel);
                            }

                            @Override
                            public void onFailure(Context context, String error) {
                                callback.onError(error);
                            }
                        }).executeOnExecutor(THREAD_POOL_EXECUTOR);
                    }
                    callback.onSuccess(urlMetaModel);
                } else {
                    callback.onError(null);
                }
            }
            super.onPostExecute(urlMetaModel);
        }
    }

    private static KmLinkPreviewModel getMetaTags(String content) {
        KmLinkPreviewModel linkPreviewModel = new KmLinkPreviewModel();

        List<String> matches = KmRegexHelper.pregMatchAll(content,
                KmRegexHelper.METATAG_PATTERN, 1);

        for (String match : matches) {
            final String lowerCase = match.toLowerCase();
            if (lowerCase.contains("property=\"og:url\"")
                    || lowerCase.contains("property='og:url'")
                    || lowerCase.contains("name=\"url\"")
                    || lowerCase.contains("name='url'")) {
                linkPreviewModel.setUrl(separeMetaTagsContent(match));
            } else if (lowerCase.contains("property=\"og:title\"")
                    || lowerCase.contains("property='og:title'")
                    || lowerCase.contains("name=\"title\"")
                    || lowerCase.contains("name='title'")) {
                linkPreviewModel.setTitle(separeMetaTagsContent(match));
            } else if (lowerCase
                    .contains("property=\"og:description\"")
                    || lowerCase
                    .contains("property='og:description'")
                    || lowerCase.contains("name=\"description\"")
                    || lowerCase.contains("name='description'")) {
                linkPreviewModel.setDescription(separeMetaTagsContent(match));
            } else if (lowerCase.contains("property=\"og:image\"")
                    || lowerCase.contains("property='og:image'")
                    || lowerCase.contains("name=\"image\"")
                    || lowerCase.contains("name='image'")) {
                linkPreviewModel.setImageLink(separeMetaTagsContent(match));
            }
        }

        return linkPreviewModel;
    }

    /**
     * Gets content from metatag
     */
    private static String separeMetaTagsContent(String content) {
        String result = KmRegexHelper.pregMatch(content, KmRegexHelper.METATAG_CONTENT_PATTERN,
                1);
        return htmlDecode(result);
    }

    /**
     * Transforms from html to normal string
     */
    private static String htmlDecode(String content) {
        return Jsoup.parse(content).text();
    }

    private static String getValidUrl(Message message) {
        String url = message.getFirstUrl();
        if (!TextUtils.isEmpty(url) && !(url.startsWith(KmRegexHelper.HTTP_PROTOCOL) || url.startsWith(KmRegexHelper.HTTPS_PROTOCOL))) {
            return KmRegexHelper.HTTP_PROTOCOL + url;
        }
        return url;
    }
}
