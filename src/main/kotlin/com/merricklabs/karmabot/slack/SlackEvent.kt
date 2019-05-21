package com.merricklabs.karmabot.slack

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
open class SlackEvent(val type: String)