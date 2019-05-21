package com.merricklabs.karmabot.handlers

import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.RequestHandler
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.merricklabs.karmabot.models.ApiGatewayResponse
import com.merricklabs.karmabot.models.SlackChallenge
import com.merricklabs.karmabot.models.SlackEvent
import mu.KotlinLogging

private val log = KotlinLogging.logger {}

class KarmaBotHandler : RequestHandler<Map<String, Any>, ApiGatewayResponse> {

    override fun handleRequest(input: Map<String, Any>?, context: Context?): ApiGatewayResponse {
        val body = input!!["body"] as String
        log.info("Received payload")
        log.info(body)
        val mapper = ObjectMapper()
        mapper.registerModule(KotlinModule())
        val event = mapper.readValue(body, SlackEvent::class.java)
        return if(event.type == "url_verification"){
            log.info("Received challenge")
            val challengeEvent = mapper.readValue(body, SlackChallenge::class.java)
            ApiGatewayResponse(200, challengeEvent.challenge)
        } else {
            ApiGatewayResponse(200, "hello world")
        }
    }
}