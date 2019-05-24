package com.merricklabs.echobot.handlers

import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.RequestHandler
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.merricklabs.echobot.models.ApiGatewayResponse
import com.merricklabs.echobot.slack.SlackBotMessage
import com.merricklabs.echobot.slack.SlackCallbackMessage
import com.merricklabs.echobot.slack.SlackChallengeMessage
import com.merricklabs.echobot.slack.SlackMessage
import mu.KotlinLogging
import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody

private val log = KotlinLogging.logger {}

class EchoBotHandler : RequestHandler<Map<String, Any>, ApiGatewayResponse> {

    private val mapper = ObjectMapper()
    private val botToken = System.getenv()["BOT_TOKEN"]
    private val botUserId = System.getenv()["BOT_USER_ID"]

    init {
        mapper.registerModule(KotlinModule())
    }

    override fun handleRequest(input: Map<String, Any>, context: Context?): ApiGatewayResponse {
        log.info("Received payload: ${input["body"]}")
        val message = mapper.convertValue(input["body"], SlackMessage::class.java)
        return when(message.type) {
            "url_verification" -> {
                log.info("Received challenge")
                val challengeMessage = mapper.convertValue(input["body"], SlackChallengeMessage::class.java)
                ApiGatewayResponse(200, challengeMessage.challenge)
            }
            "event_callback" -> {
                val callbackMessage = mapper.convertValue(input["body"], SlackCallbackMessage::class.java)
                // Only respond to at-mentions of our bot and ignore messages from bots (ourselves or otherwise)
                if(isAtMention(callbackMessage)){
                    log.info("Is an at-mention of our bot.")
                    sendReply(callbackMessage)
                }
                ApiGatewayResponse(200)
            }
            else -> ApiGatewayResponse(200)
        }
    }

    private fun isAtMention(message: SlackCallbackMessage) = message.event.type == "app_mention"
            && message.event.text.contains("<@$botUserId>")
            && message.event.bot_id == null

    private fun sendReply(message: SlackCallbackMessage){
        val responseMessage = SlackBotMessage(message.event.channel, message.event.text.replace("<@$botUserId>", "<@${message.event.user}>").trim())
        val okHttpClient = OkHttpClient()
        val json = MediaType.get("application/json; charset=utf-8")
        val body = RequestBody.create(json, mapper.writeValueAsString(responseMessage))
        log.info("Sending message back to Slack: ${mapper.writeValueAsString(responseMessage)}")
        val request = Request.Builder()
                .header("Authorization", "Bearer $botToken")
                .url(SLACK_URL)
                .post(body)
                .build()
        val response = okHttpClient.newCall(request).execute()
        log.info("Got response from Slack API: $response with body ${response.body()!!.string()}")
    }

    companion object {
        const val SLACK_URL = "https://slack.com/api/chat.postMessage"
    }
}