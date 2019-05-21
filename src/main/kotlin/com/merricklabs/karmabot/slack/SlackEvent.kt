package com.merricklabs.karmabot.slack

data class EventPayload(
        val client_msg_id: String,
        val type: String,
        val text: String,
        val user: String,
        val ts: String,
        val channel: String,
        val event_ts: String
)