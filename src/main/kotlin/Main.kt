import BotConfig.apiIdStr
import com.github.demidko.telegram.TelegramStorage.Constructors.TelegramStorage
import com.github.sanctuscorvus.GeminiClient
import com.github.sanctuscorvus.SafetyCategory
import io.github.cdimascio.dotenv.dotenv
import it.tdlight.Init
import it.tdlight.Log
import it.tdlight.Slf4JLogMessageHandler
import it.tdlight.client.APIToken
import it.tdlight.client.AuthenticationSupplier
import it.tdlight.client.SimpleTelegramClient
import it.tdlight.client.SimpleTelegramClientFactory
import it.tdlight.jni.TdApi
import it.tdlight.jni.TdApi.*
import java.nio.file.Paths
import java.time.Instant
import java.util.concurrent.TimeUnit
fun main() {
    val geminiClient = GeminiClient(
        GeminiClient.Configuration.create(
        apiKey = BotConfig.geminiApiKey,
        modelName = GeminiClient.GeminiModel.GEMINI_20FLASH_EXP,
        safetySettingsBuilder = {
            blockNone(SafetyCategory.HARM_CATEGORY_HATE_SPEECH)
            blockNone(SafetyCategory.HARM_CATEGORY_HARASSMENT)
            blockNone(SafetyCategory.HARM_CATEGORY_SEXUALLY_EXPLICIT)
            blockNone(SafetyCategory.HARM_CATEGORY_DANGEROUS_CONTENT)
            blockNone(SafetyCategory.HARM_CATEGORY_CIVIC_INTEGRITY)
        }
    ))

    val telegramStorage = TelegramStorage<Long, ChatData>(
        bot = com.github.kotlintelegrambot.bot { token = BotConfig.telegramBotToken },
        channel = com.github.kotlintelegrambot.entities.ChatId.fromId(BotConfig.telegramStorageChannelId.toLong())
    )

    val chatBot = GeminiBot(BotConfig, geminiClient, telegramStorage)
    chatBot.start()

/*    println("bot запущен")
    while (true) {
        Thread.sleep(10000)
    }*/
}