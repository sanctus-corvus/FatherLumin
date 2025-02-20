import com.github.demidko.telegram.TelegramStorage.Constructors.TelegramStorage
import com.github.sanctuscorvus.GeminiClient
import com.github.sanctuscorvus.SafetyCategory

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