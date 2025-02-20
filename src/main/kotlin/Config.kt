//package com.github.sanctuscorvus
import io.github.cdimascio.dotenv.dotenv
import it.tdlight.client.APIToken
import it.tdlight.client.SimpleTelegramClientFactory
import java.nio.file.Paths

object BotConfig {
    private val dotenv = dotenv()
    val telegramStorageChannelId: String = dotenv["STORAGE_ID"]
    val telegramBotToken: String = dotenv["TOKEN"]
        ?: throw IllegalStateException("TOKEN не найден в .env!")
    val geminiApiKey: String = dotenv["GEMINI_API_KEY"]
        ?: throw IllegalStateException("GEMINI_API_KEY не найден в .env!")

    const val botName = "Люмин" // Имя бота, на которое он реагирует

    val apiIdStr = dotenv["API_ID"] ?: System.getenv("API_ID") ?: ""
    val apiHash = dotenv["API_HASH"] ?: System.getenv("API_HASH") ?: ""
    val phone = dotenv["PHONE"] ?: System.getenv("PHONE") ?: ""
    val groupId: String? = dotenv["GROUP_ID"]
    val apiId = apiIdStr.toInt()
    val allowedChatIds = setOf(
            groupId?.toLong()
    )

  //  val groupId: String? = dotenv["GROUP_ID"]

    const val botCreator = "Sanctus Corvus"
    var botNamePrefix = botName.lowercase()
    const val historySize = 85

    val clientFactory = SimpleTelegramClientFactory()
    val apiToken = APIToken(BotConfig.apiId, BotConfig.apiHash)
    val settings = it.tdlight.client.TDLibSettings.create(apiToken)
    val sessionPath = Paths.get("new-session")

    var botUserId = 0L
}