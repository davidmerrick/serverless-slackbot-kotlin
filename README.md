# echobot

A simple serverless Slack bot implemented in Kotlin. The bot just responds to any at-mentions with the same message.

Inspired by: https://www.sentialabs.io/2018/08/16/Building-a-Slackbot-with-Serverless-Framework.html

# Installation

- Set up a Slack bot in your workspace with event subscriptions for `message.channels` and `message.groups`.
- Set the `BOT_TOKEN` environment variable to the one Slack provides: https://api.slack.com/apps/<your-app-id>/oauth.
- Deploy the app with `./gradlew deploy`.
- Point the `Request URL` in the app config to the endpoint url returned from that deployment.
- Once you know your bot's user id, redeploy with the `BOT_USER_ID` environment variable set to that.

# Todo

- Implement SQS integration to make the app more asynchronous.