package com.applozic.mobicomkit.uiwidgets.conversation.fragment;

import android.location.Location;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.Toast;

import com.applozic.mobicomkit.api.conversation.MessageIntentService;
import com.applozic.mobicomkit.api.conversation.MobiComConversationService;
import com.applozic.mobicomkit.uiwidgets.ApplozicApplication;
import com.applozic.mobicomkit.uiwidgets.R;
import com.applozic.mobicomkit.uiwidgets.conversation.MultimediaOptionsGridView;
import com.applozic.mobicomkit.uiwidgets.conversation.activity.ConversationActivity;
import com.applozic.mobicomkit.uiwidgets.conversation.adapter.MobicomMultimediaPopupAdapter;
import com.applozic.mobicommons.commons.core.utils.LocationUtils;
import com.applozic.mobicommons.people.channel.Channel;
import com.applozic.mobicommons.people.contact.Contact;

public class ConversationFragment extends MobiComConversationFragment {

    private static final String TAG = "ConversationFragment";
    private MultimediaOptionsGridView popupGrid;
    public GridView multimediaPopupGrid;
    ConversationActivity conversationActivity;

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

        multimediaPopupGrid = (GridView) view.findViewById(R.id.mobicom_multimedia_options1);
        multimediaPopupGrid.setVisibility(View.GONE);
        conversationActivity = (ConversationActivity) getActivity();
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.secret_message_timer_array, R.layout.mobiframework_custom_spinner);

        adapter.setDropDownViewResource(R.layout.mobiframework_custom_spinner);

        attachButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                MobicomMultimediaPopupAdapter adap = new MobicomMultimediaPopupAdapter(getActivity(), getResources().getStringArray(R.array.multimediaOptionIcons_without_price), getResources().getStringArray(R.array.multimediaOptions_without_price_text));
                multimediaPopupGrid.setAdapter(adap);

                if (multimediaPopupGrid.getVisibility() == View.GONE) {
                    multimediaPopupGrid.setVisibility(View.VISIBLE);
                    conversationActivity.isMultimediaOptionGridOpen = true;
                } else {
                    multimediaPopupGrid.setVisibility(View.GONE);
                    conversationActivity.isMultimediaOptionGridOpen = false;
                }

                MultimediaOptionsGridView itemClickHandler = new MultimediaOptionsGridView(getActivity(), multimediaPopupGrid);
                itemClickHandler.setMultimediaClickListener();

                if (contact != null && !contact.isBlocked() || channel != null) {
                    if (attachmentLayout.getVisibility() == View.VISIBLE) {
                        Toast.makeText(getActivity(), R.string.select_file_count_limit, Toast.LENGTH_LONG).show();
                        return;
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

    public void hideMultimediaOptionGrid() {
        if (multimediaPopupGrid.getVisibility() == View.VISIBLE) {
            multimediaPopupGrid.setVisibility(View.GONE);
            conversationActivity.isMultimediaOptionGridOpen = false;
        }
    }
}