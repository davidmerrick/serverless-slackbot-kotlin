package com.merricklabs.karmabot

import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.RequestHandler
import com.merricklabs.karmabot.models.ApiGatewayResponse

class KarmaBotHandler : RequestHandler<Map<String, Any>, ApiGatewayResponse> {

    override fun handleRequest(input: Map<String, Any>?, context: Context?): ApiGatewayResponse {
        return ApiGatewayResponse(200, "hello world")
    }
}