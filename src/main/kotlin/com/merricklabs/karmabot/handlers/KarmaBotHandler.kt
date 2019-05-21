package com.merricklabs.karmabot.handlers

import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.RequestHandler
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.merricklabs.karmabot.models.ApiGatewayResponse
import com.merricklabs.karmabot.slack.SlackCallbackEvent
import com.merricklabs.karmabot.slack.SlackChallengeEvent
import com.merricklabs.karmabot.slack.SlackEvent
import mu.KotlinLogging

private val log = KotlinLogging.logger {}

class KarmaBotHandler : RequestHandler<Map<String, Any>, ApiGatewayResponse> {
    private val mapper = ObjectMapper()

    override fun handleRequest(input: Map<String, Any>?, context: Context?): ApiGatewayResponse {
        val body = input!!["body"] as String
        log.info("Received payload: $body")
        mapper.registerModule(KotlinModule())
        val event = mapper.readValue(body, SlackEvent::class.java)
        return when(event.type) {
            "url_verification" -> {
                log.info("Received challenge")
                val challengeEvent = mapper.readValue(body, SlackChallengeEvent::class.java)
                ApiGatewayResponse(200, challengeEvent.challenge)
            }
            "event_callback" -> {
                val callbackEvent = mapper.readValue(body, SlackCallbackEvent::class.java)
                ApiGatewayResponse(200, "hello world")
            }
            else -> ApiGatewayResponse(200, "hello world")
        }
    }
}