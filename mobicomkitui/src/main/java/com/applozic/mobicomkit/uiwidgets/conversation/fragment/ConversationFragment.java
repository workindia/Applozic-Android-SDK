package com.applozic.mobicomkit.uiwidgets.conversation.fragment;

import android.location.Location;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.applozic.mobicomkit.api.conversation.MessageIntentService;
import com.applozic.mobicomkit.api.conversation.MobiComConversationService;
import com.applozic.mobicomkit.uiwidgets.ApplozicApplication;
import com.applozic.mobicomkit.uiwidgets.ApplozicSetting;
import com.applozic.mobicomkit.uiwidgets.conversation.MultimediaOptionsGridView;
import com.applozic.mobicomkit.uiwidgets.R;

import com.applozic.mobicommons.commons.core.utils.LocationUtils;
import com.applozic.mobicommons.people.channel.Channel;
import com.applozic.mobicommons.people.contact.Contact;

public class ConversationFragment extends MobiComConversationFragment {

    private static final String TAG = "ConversationFragment";
    private MultimediaOptionsGridView popupGrid;

    public ConversationFragment() {
        this.messageIntentClass = MessageIntentService.class;
    }

    public ConversationFragment(Contact contact, Channel channel) {
        this.messageIntentClass = MessageIntentService.class;
        this.contact = contact;
        this.channel = channel;

    }

    public void attachLocation(Location mCurrentLocation) {
        String address = LocationUtils.getAddress(getActivity(), mCurrentLocation);
        if (!TextUtils.isEmpty(address)) {
            address = "Address: " + address + "\n";
        } else {
            address = "";
        }
        this.messageEditText.setText(address + "http://maps.google.com/?q=" + mCurrentLocation.getLatitude() + "," + mCurrentLocation.getLongitude());
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        this.title = ApplozicApplication.TITLE;
        this.conversationService = new MobiComConversationService(getActivity());
        hideExtendedSendingOptionLayout = true;

        View view = super.onCreateView(inflater, container, savedInstanceState);

        sendType.setSelection(1);

        messageEditText.setHint(R.string.enter_mt_message_hint);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.secret_message_timer_array, R.layout.mobiframework_custom_spinner);

        adapter.setDropDownViewResource(R.layout.mobiframework_custom_spinner);

        popupGrid = new MultimediaOptionsGridView(getActivity());

        if (!(popupGrid.showPopup == null) && popupGrid.showPopup.isShowing()) {
            individualMessageSendLayout.setVisibility(View.GONE);
        } else {
            individualMessageSendLayout.setVisibility(View.VISIBLE);
        }

        attachButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (contact != null && !contact.isBlocked() || channel != null) {
                    if (attachmentLayout.getVisibility() == View.VISIBLE) {
                        Toast.makeText(getActivity(), R.string.select_file_count_limit, Toast.LENGTH_LONG).show();
                        return;
                    }

                    if (ApplozicSetting.getInstance(getActivity()).isPriceOptionVisible()) {
                        popupGrid.showPopup(attachButton, getResources().getStringArray(R.array.multimediaOptionIcons_without_price), getResources().getStringArray(R.array.multimediaOptions_without_price_text));

                    } else {
                        popupGrid.showPopup(attachButton, getResources().getStringArray(R.array.multimediaOptionIcons_without_price), getResources().getStringArray(R.array.multimediaOptions_without_price_text));
                    }
                }
                if (contact != null && contact.isBlocked()) {
                    userUnBlockDialog(contact.getUserId());
                }

            }
        });

        return view;
    }

    @Override
    protected void processMobiTexterUserCheck() {

    }

    public void updateTitle() {
        //((ActionBarActivity) getActivity()).getSupportActionBar().setTitle(ApplozicApplication.TITLE);
        super.updateTitle();
    }

}