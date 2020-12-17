package com.applozic.mobicomkit.sample;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.applozic.mobicomkit.api.account.user.MobiComUserPreference;
import com.applozic.mobicomkit.api.conversation.MessageBuilder;
import com.applozic.mobicomkit.uiwidgets.conversation.ConversationUIService;
import com.applozic.mobicomkit.uiwidgets.conversation.activity.ConversationActivity;

import java.util.HashMap;
import java.util.Map;

public class SampleRichMessagesActivity extends AppCompatActivity {

    Button linkButton,
            suggestedButton,
            submitButton,
            imageRichMessageButton,
            listRichMessageButton,
            genericCardRichMessageButton,
            cardCarouselRichMessageButton;
    String receiverUserId;

    public enum RichMessage {
        // Rich message payload
        LinkButtonPayload(Payload.link),
        SuggestedReplyPayload(Payload.suggestedReply),
        SubmitButtonPayload(Payload.submit),
        ImagePayload(Payload.image),
        ListPayload(Payload.list),
        GenericCardPayload(Payload.genericCard),
        CardCarouselPayload(Payload.cardCarousel);

        private final String value;

        public String getValue() {
            return value;
        }

        RichMessage(String value) {
            this.value = value;
        }

        static class Payload {
            private static final String link = "[{ \"type\": \"link\", \"url\": \"https://www.google.com\", \"name\": \"Go To Google\" },{ \"type\": \"link\", \"url\": \"https://www.facebook.com\", \"name\": \"Go To Facebook\" }]";
            private static final String suggestedReply = "[{ \"title\": \"Yes\", \"message\": \"Cool! send me more.\" },{ \"title\": \"No\", \"message\": \"Not at all\"}]";
            private static final String image = "[{ \"caption\": \"Image caption\", \"url\": \"https://images.pexels.com/photos/544980/pexels-photo-544980.jpeg?cs=srgb&dl=dew-drop-droplet-544980.jpg&fm=jpg\"}]";
            private static final String submit = "[{ \"name\": \"Pay\", \"replyText\":\"optional, will be used as acknowledgement message to user in case of requestType JSON. Default value is same as name parameter\" }]";
            private static final String list = "{ \"headerImgSrc\": \"https://images.pexels.com/photos/271624/pexels-photo-271624.jpeg?auto=compress&cs=tinysrgb&dpr=2&h=650&w=940\", \"headerText\": \"Header text.\", \"elements\": [{ \"imgSrc\": \"https://images.pexels.com/photos/271624/pexels-photo-271624.jpeg?auto=compress&cs=tinysrgb&dpr=2&h=650&w=940\", \"title\": \"List item 1\", \"description\": \"Description for the list item\", \"action\": { \"url\": \"https://www.google.com\", \"type\": \"link\" } }], \"buttons\": [{ \"name\": \"See us on facebook\", \"action\": { \"url\": \"https://www.facebook.com\", \"type\": \"link\" } }]}";
            private static final String genericCard = "[ { \"title\": \"Card Title\", \"subtitle\": \"Card Subtitle \", \"header\": { \"overlayText\": \"Overlay Text\", \"imgSrc\": \"https://images.pexels.com/photos/271624/pexels-photo-271624.jpeg?auto=compress&cs=tinysrgb&dpr=2&h=650&w=940\" }, \"description\": \"This is a sample description of the card. It is for sampling purposes.\", \"titleExt\": \"Title extension\", \"buttons\": [ { \"name\": \"Open facebook\", \"action\": { \"type\": \"link\", \"payload\": { \"url\": \"https://www.facebook.com\" } } } ] } ]";
            private static final String cardCarousel = "[ { \"title\": \"OYO Rooms 1\", \"subtitle\": \"Kundanahalli road turn.\", \"header\": { \"overlayText\": \"$400\", \"imgSrc\": \"http://www.tollesonhotels.com/wp-content/uploads/2017/03/hotel-room.jpg\" }, \"description\": \"Bharathi Road \\n Near Head Post Office\", \"titleExt\": \"4.2/5\", \"buttons\": [ { \"name\": \"Link Button\", \"action\": { \"type\": \"link\", \"payload\": { \"url\": \"https://www.facebook.com\" } } }, { \"name\": \"Suggested Reply\", \"action\": { \"type\": \"quickReply\", \"payload\": { \"message\": \"text will be sent as message\", \"replyMetadata\": { \"key1\": \"value1\" } } } }, { \"name\": \"Submit button\", \"action\": { \"type\": \"submit\", \"payload\": { \"text\": \"acknowledgement text\", \"formData\": { \"amount\": \"$55\", \"description\": \"movie ticket\" }, \"formAction\": \"https://reqres.in/api/users\", \"requestType\": \"json\" } } } ] }, { \"title\": \"OYO Rooms 2\", \"subtitle\": \"Kundanahalli \", \"header\": { \"overlayText\": \"$360\", \"imgSrc\": \"http://www.tollesonhotels.com/wp-content/uploads/2017/03/hotel-room.jpg\" }, \"description\": \"Bharathi Road | Near Head Post Office, Cuddalore 607001\", \"titleExt\": \"4.2/5\", \"buttons\": [ { \"name\": \"Link Button\", \"action\": { \"type\": \"link\", \"payload\": { \"url\": \"https://www.facebook.com\" } } }, { \"name\": \"Submit button\", \"action\": { \"type\": \"submit\", \"payload\": { \"text\": \"acknowledgement text\", \"formData\": { \"amount\": \"$22\", \"description\": \"movie ticket\" }, \"formAction\": \"https://example.com/book\", \"requestType\": \"json\" } } }, { \"name\": \"Suggested Reply\", \"action\": { \"type\": \"quickReply\", \"payload\": { \"message\": \"text will be sent as message\", \"replyMetadata\": { \"key1\": \"value1\" } } } } ] }, { \"title\": \"OYO Rooms 3\", \"subtitle\": \"Kundanahalli \", \"header\": { \"overlayText\": \"$750\", \"imgSrc\": \"http://www.tollesonhotels.com/wp-content/uploads/2017/03/hotel-room.jpg\" }, \"description\": \"Bharathi Road | Near Head Post Office, Cuddalore 607001\", \"titleExt\": \"4.2/5\", \"buttons\": [ { \"name\": \"Link Button\", \"action\": { \"type\": \"link\", \"payload\": { \"url\": \"https://www.facebook.com\" } } }, { \"name\": \"Submit button\", \"action\": { \"type\": \"submit\", \"payload\": { \"text\": \"acknowledgement text\", \"formData\": { \"amount\": \"$45\", \"description\": \"movie ticket\" }, \"formAction\": \"https://example.com/book\", \"requestType\": \"json\" } } }, { \"name\": \"Suggested Reply\", \"action\": { \"type\": \"quickReply\", \"payload\": { \"message\": \"text will be sent as message\", \"replyMetadata\": { \"key1\": \"value1\" } } } } ] } ]";
        }

        // ContentType of rich message
        public static final String contentType = "300";

        // Will be used in submit button
        public static final String requestType = "json";
        public static final String formData = "{\"amount\": \"1000\",\"description\": \"movie ticket\"}";
        public static final String formDataActionUrl = "https://reqres.in/api/users";

        // Template id of rich message types
        public static class Template {
            public static class Button {
                public static final String linkOrSubmit = "3";
                public static final String suggestedReply = "6";
            }

            public static class Image {
                public static final String type = "9";
            }

            public static class List {
                public static final String type = "7";
            }

            public static class Card {
                public static final String type = "10";
            }
        }

        // Message text for rich messages
        public static class Message {
            public static class Button {
                public static final String link = "Here are the links you can checkout";
                public static final String submit = "This sample message for pay click";
                public static final String suggestedReply = "Do you want some more updates?";
            }

            static class Image {
                public static final String text = "This is below sample image";
            }

            static class List {
                public static final String text = "This is a sample list.";
            }

            static class Card {
                public static final String genericText = "This is a sample generic Card message.";
                public static final String cardCarouselText = "This is a sample Card Carousel message.";
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rich_messages);

        linkButton = findViewById(R.id.linkButton);
        submitButton = findViewById(R.id.submitButton);
        suggestedButton = findViewById(R.id.suggestedReplies);
        imageRichMessageButton = findViewById(R.id.imageRichMessageButton);
        listRichMessageButton = findViewById(R.id.listRichMessageButton);
        genericCardRichMessageButton = findViewById(R.id.genericCardRichMessageButton);
        cardCarouselRichMessageButton = findViewById(R.id.cardCarouselRichMessageButton);

        // Passed the login userId for testing in two way
        // Note: receiverUserId needs to be different userId not the login userId
        receiverUserId = MobiComUserPreference.getInstance(this).getUserId();
        linkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendRichMessageAndLaunchConversation(receiverUserId,
                        RichMessage.Message.Button.link,
                        RichMessage.Template.Button.linkOrSubmit,
                        RichMessage.LinkButtonPayload.getValue());
            }
        });

        suggestedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendRichMessageAndLaunchConversation(receiverUserId,
                        RichMessage.Message.Button.suggestedReply,
                        RichMessage.Template.Button.suggestedReply,
                        RichMessage.SuggestedReplyPayload.getValue());
            }
        });
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Map<String, String> messageMetadata = new HashMap<>();
                messageMetadata.put("contentType", RichMessage.contentType);
                messageMetadata.put("templateId", RichMessage.Template.Button.linkOrSubmit);
                messageMetadata.put("payload", RichMessage.SubmitButtonPayload.getValue());
                messageMetadata.put("formData", RichMessage.formData);
                messageMetadata.put("formAction", RichMessage.formDataActionUrl);
                messageMetadata.put("requestType", RichMessage.requestType);
                sendRichMessageAndLaunchConversation(receiverUserId,
                        messageMetadata,
                        RichMessage.Message.Button.submit);
            }
        });
        imageRichMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendRichMessageAndLaunchConversation(receiverUserId,
                        RichMessage.Message.Button.suggestedReply,
                        RichMessage.Template.Image.type,
                        RichMessage.ImagePayload.getValue());
            }
        });

        listRichMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendRichMessageAndLaunchConversation(receiverUserId,
                        RichMessage.Message.List.text,
                        RichMessage.Template.List.type,
                        RichMessage.ListPayload.getValue());
            }
        });
        genericCardRichMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendRichMessageAndLaunchConversation(receiverUserId,
                        RichMessage.Message.Card.genericText,
                        RichMessage.Template.Card.type,
                        RichMessage.GenericCardPayload.getValue());
            }
        });

        cardCarouselRichMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendRichMessageAndLaunchConversation(receiverUserId,
                        RichMessage.Message.Card.cardCarouselText,
                        RichMessage.Template.Card.type,
                        RichMessage.CardCarouselPayload.getValue());
            }
        });
    }

    public void sendRichMessageAndLaunchConversation(String receiverUserId,
                                                     String message,
                                                     String templateId,
                                                     String payload) {
        Map<String, String> messageMetadata = new HashMap<>();
        messageMetadata.put("contentType", RichMessage.contentType);
        messageMetadata.put("templateId", templateId);
        messageMetadata.put("payload", payload);
        new MessageBuilder(this)
                .setMessage(message)
                .setTo(receiverUserId)
                .setMetadata(messageMetadata)
                .send();
        launchConversation(receiverUserId);
    }

    public void sendRichMessageAndLaunchConversation(String receiverUserId,
                                                     Map<String, String> messageMetadata,
                                                     String message) {
        new MessageBuilder(this)
                .setMessage(message)
                .setTo(receiverUserId)
                .setMetadata(messageMetadata)
                .send();
        launchConversation(receiverUserId);
    }

    public void launchConversation(String receiverUserId) {
        Intent takeOrderIntent = new Intent(this, ConversationActivity.class);
        takeOrderIntent.putExtra(ConversationUIService.TAKE_ORDER, true);
        takeOrderIntent.putExtra(ConversationUIService.USER_ID, receiverUserId);
        startActivity(takeOrderIntent);
    }
}