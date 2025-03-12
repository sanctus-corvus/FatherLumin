import com.github.demidko.telegram.TelegramStorage
import io.github.cdimascio.dotenv.dotenv
import it.tdlight.client.APIToken
import it.tdlight.client.SimpleTelegramClientFactory
import java.nio.file.Paths

object BotConfig {

    private val localEnv = dotenv {
        ignoreIfMissing = true
    }
    val telegramStorageChannelId: String = localEnv["STORAGE_ID"]
        ?: System.getenv("STORAGE_ID") ?: "STORAGE_ID не найден"
    val telegramBotToken: String = localEnv["TOKEN"]
        ?: System.getenv("TOKEN") ?: "TOKEN не найден"

    val configKeys = listOf("GEMINI_API_KEY", "API_ID", "API_HASH", "PHONE", "GROUP_ID", "ALLOWED_CHAT_IDS", "SPEC_ID")

    val config: MutableMap<String, String> = mutableMapOf()

    // геттеры для доступа к переменным
    val geminiApiKey: String
        get() = config["GEMINI_API_KEY"] ?: "GEMINI_API_KEY"

    val apiId: String
        get() = config["API_ID"] ?: "API_ID не найден или некорректен в конфигурации"

    val apiHash: String
        get() = config["API_HASH"] ?: "API_HASH не найден в конфигурации"

    val phone: String
        get() = config["PHONE"] ?: "PHONE не найден в конфигурации"

    val specId: Long?
        get() = config["SPEC_ID"]?.toLongOrNull()

    val groupId: String?
        get() = config["GROUP_ID"]

    val allowedChatIds: Set<Long>
        get() = config["ALLOWED_CHAT_IDS"]?.split(",")
            ?.mapNotNull { it.trim().toLongOrNull() }
            ?.toSet() ?: groupId?.toLongOrNull()?.let { setOf(it) } ?: emptySet()

    const val botCreator = "Sanctus Corvus"
    const val botName = "Люмин" // Имя бота, на которое он реагирует
    var botNamePrefix = botName.lowercase()
    const val historySize = 85*2
    fun bootstrapConfig(telegramStorage: TelegramStorage<StorageKey, StorageValue>) {
        val localEnv = dotenv {
            ignoreIfMissing = true
        }

        for (key in BotConfig.configKeys) {
            val localValue = localEnv[key]
            if (localValue != null) {
                telegramStorage[StorageKey.Config(key)] = StorageValue.ConfigValue(localValue)
                BotConfig.config[key] = localValue
            } else {
                val stored = telegramStorage[StorageKey.Config(key)]
                if (stored is StorageValue.ConfigValue) {
                    BotConfig.config[key] = stored.value
                } else {
                    if (key == "ALLOWED_CHAT_IDS" || key == "SPEC_ID") {
                        BotConfig.config[key] = ""
                    } else {
                        throw IllegalStateException("Конфигурационная переменная $key не найдена ни в .env, ни в TelegramStorage")
                    }
                }
            }
        }
        println("Конфигурация успешно загружена: ${BotConfig.config}")
    }


}