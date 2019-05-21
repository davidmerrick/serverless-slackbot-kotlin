package com.merricklabs.karmabot.models

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
open class SlackEvent(val type: String)