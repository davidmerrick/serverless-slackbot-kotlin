package com.merricklabs.karmabot.handlers

import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.RequestHandler
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.merricklabs.karmabot.models.ApiGatewayResponse
import com.merricklabs.karmabot.slack.SlackBotMessage
import com.merricklabs.karmabot.slack.SlackCallbackMessage
import com.merricklabs.karmabot.slack.SlackChallengeMessage
import com.merricklabs.karmabot.slack.SlackMessage
import mu.KotlinLogging
import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody

private val log = KotlinLogging.logger {}

class KarmaBotHandler : RequestHandler<Map<String, Any>, ApiGatewayResponse> {

    private val mapper = ObjectMapper()
    private val botToken = System.getenv()["BOT_TOKEN"]

    override fun handleRequest(input: Map<String, Any>?, context: Context?): ApiGatewayResponse {
        val body = input!!["body"] as String
        log.info("Received payload: $body")
        mapper.registerModule(KotlinModule())
        val message = mapper.readValue(body, SlackMessage::class.java)
        return when(message.type) {
            "url_verification" -> {
                log.info("Received challenge")
                val challengeMessage = mapper.readValue(body, SlackChallengeMessage::class.java)
                ApiGatewayResponse(200, challengeMessage.challenge)
            }
            "event_callback" -> {
                val callbackMessage = mapper.readValue(body, SlackCallbackMessage::class.java)
                sendMessage(callbackMessage)
                ApiGatewayResponse(200)
            }
            else -> ApiGatewayResponse(200)
        }
    }

    private fun sendMessage(event: SlackCallbackMessage){
        val message = SlackBotMessage(event.event.channel, "Hey")
        val okHttpClient = OkHttpClient()
        val json = MediaType.get("application/json; charset=utf-8")
        val body = RequestBody.create(json, mapper.writeValueAsString(message))
        log.info("Sending message back to Slack: ${mapper.writeValueAsString(message)}")
        val request = Request.Builder()
                .header("Authorization", "Bearer $botToken")
                .url(SLACK_URL)
                .post(body)
                .build()
        val response = okHttpClient.newCall(request).execute()
        log.info("Got response: $response")
    }

    companion object {
        const val SLACK_URL = "https://slack.com/api/chat.postMessage"
    }
}