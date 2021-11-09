package com.applozic.mobicomkit.uiwidgets.conversation.utils.events

import com.applozic.mobicommons.people.channel.Channel

data class ActivityEvent(
        val channel: Channel,
        val activityOpen: String
)