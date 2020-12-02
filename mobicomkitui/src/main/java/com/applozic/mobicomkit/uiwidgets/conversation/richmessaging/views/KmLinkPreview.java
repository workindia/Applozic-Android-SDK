package com.applozic.mobicomkit.uiwidgets.conversation.richmessaging.views;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.view.View;
import android.webkit.URLUtil;
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
import com.applozic.mobicommons.ApplozicService;
import com.applozic.mobicommons.commons.core.utils.Utils;
import com.applozic.mobicommons.json.GsonUtils;
import com.applozic.mobicommons.task.AlAsyncTask;
import com.applozic.mobicommons.task.AlTask;
import com.bumptech.glide.Glide;

import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.lang.ref.WeakReference;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
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
            AlTask.execute(new UrlLoader(context, message, new AlCallback() {
                @Override
                public void onSuccess(Object response) {
                    updateViews((KmLinkPreviewModel) response);
                }

                @Override
                public void onError(Object error) {

                }
            }));
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

        urlLoadLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openUrl(getValidUrl(message));
            }
        });
    }

    public void openUrl(String url) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        context.startActivity(browserIntent);
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

    public static class UrlLoader extends AlAsyncTask<Void, KmLinkPreviewModel> {

        private WeakReference<Context> context;
        private Message message;
        private AlCallback callback;

        public UrlLoader(Context context, Message message, AlCallback callback) {
            this.context = new WeakReference<>(context);
            this.message = message;
            this.callback = callback;
        }

        @Override
        protected KmLinkPreviewModel doInBackground() {
            String validUrl = getValidUrl(message);
            KmLinkPreviewModel linkPreviewModel = null;
            try {
                if (!TextUtils.isEmpty(validUrl) && Pattern.compile(KmRegexHelper.IMAGE_PATTERN).matcher(validUrl).matches()) {
                    linkPreviewModel = new KmLinkPreviewModel();
                    linkPreviewModel.setImageLink(validUrl);
                } else {
                    Document document = Jsoup.connect(validUrl).get();
                    linkPreviewModel = getMetaTags(document, message);
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
                        AlTask.execute(new AlMessageMetadataUpdateTask(context.get(), message.getKeyString(), metadata, new AlMessageMetadataUpdateTask.MessageMetadataListener() {
                            @Override
                            public void onSuccess(Context context, String message) {
                                callback.onSuccess(urlMetaModel);
                            }

                            @Override
                            public void onFailure(Context context, String error) {
                                callback.onError(error);
                            }
                        }));
                    }
                    callback.onSuccess(urlMetaModel);
                } else {
                    callback.onError(null);
                }
            }
            super.onPostExecute(urlMetaModel);
        }
    }

    private static KmLinkPreviewModel getMetaTags(Document doc, Message message) {
        KmLinkPreviewModel linkPreviewModel = new KmLinkPreviewModel();
        String url = getValidUrl(message);
        try {
            Elements elements = doc.getElementsByTag("meta");

            String title = doc.select("meta[property=og:title]").attr("content");

            Utils.printLog(ApplozicService.getAppContext(), "LinkTest", "Title : " + title);
            if (!TextUtils.isEmpty(title)) {
                linkPreviewModel.setTitle(title);
            } else {
                linkPreviewModel.setTitle(doc.title());
            }

            //getDescription
            String description = doc.select("meta[name=description]").attr("content");
            if (description.isEmpty() || description == null) {
                description = doc.select("meta[name=Description]").attr("content");
            }
            if (description.isEmpty() || description == null) {
                description = doc.select("meta[property=og:description]").attr("content");
            }
            if (description.isEmpty() || description == null) {
                description = "";
            }
            linkPreviewModel.setDescription(description);

            //getImages
            Elements imageElements = doc.select("meta[property=og:image]");
            if (imageElements.size() > 0) {
                String image = imageElements.attr("content");
                if (!TextUtils.isEmpty(image)) {
                    linkPreviewModel.setImageLink(resolveURL(url, image));
                }
            }
            if (TextUtils.isEmpty(linkPreviewModel.getImageLink())) {
                String src = doc.select("link[rel=image_src]").attr("href");
                if (!TextUtils.isEmpty(src)) {
                    linkPreviewModel.setImageLink(resolveURL(url, src));
                } else {
                    src = doc.select("link[rel=apple-touch-icon]").attr("href");
                    if (!TextUtils.isEmpty(src)) {
                        linkPreviewModel.setImageLink(resolveURL(url, src));
                    } else {
                        src = doc.select("link[rel=icon]").attr("href");
                        if (!TextUtils.isEmpty(src)) {
                            linkPreviewModel.setImageLink(resolveURL(url, src));
                        }
                    }
                }
            }

            //Favicon
            String src = doc.select("link[rel=apple-touch-icon]").attr("href");
            if (!TextUtils.isEmpty(src) && TextUtils.isEmpty(linkPreviewModel.getImageLink())) {
                linkPreviewModel.setImageLink(resolveURL(url, src));
            } else {
                src = doc.select("link[rel=icon]").attr("href");
                if (!TextUtils.isEmpty(src) && TextUtils.isEmpty(linkPreviewModel.getImageLink())) {
                    linkPreviewModel.setImageLink(resolveURL(url, src));
                }
            }

            for (Element element : elements) {
                if (element.hasAttr("property")) {
                    String strProperty = element.attr("property").toString().trim();
                    if (strProperty.equals("og:url")) {
                        linkPreviewModel.setUrl(element.attr("content").toString());
                    }
                    if (strProperty.equals("og:site_name") && TextUtils.isEmpty(linkPreviewModel.getTitle())) {
                        linkPreviewModel.setTitle(element.attr("content").toString());
                    }
                }
            }

            if (TextUtils.isEmpty(linkPreviewModel.getUrl())) {
                URI uri = null;
                try {
                    uri = new URI(url);
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                }
                linkPreviewModel.setUrl(uri == null ? url : uri.getHost());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return linkPreviewModel;
    }

    private static String getValidUrl(Message message) {
        String url = message.getFirstUrl();
        if (!TextUtils.isEmpty(url) && !(url.startsWith(KmRegexHelper.HTTP_PROTOCOL) || url.startsWith(KmRegexHelper.HTTPS_PROTOCOL))) {
            return KmRegexHelper.HTTP_PROTOCOL + url;
        }
        return url;
    }

    private static String resolveURL(String url, String part) {
        if (URLUtil.isValidUrl(part)) {
            return part;
        } else {
            URI baseUri = null;
            try {
                baseUri = new URI(url);
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
            if (baseUri != null) {
                baseUri = baseUri.resolve(part);
                return baseUri.toString();
            }
            return null;
        }
    }
}
