import com.github.sanctuscorvus.GeminiClient
import com.github.sanctuscorvus.SafetyCategory
import java.time.LocalDate

object GeminiModelSwitcher {

    private const val DAILY_REQUEST_LIMIT = 50
    private var requestCount = 0
    private var lastRequestDate: LocalDate = LocalDate.now()
    private var currentModelName: GeminiClient.GeminiModel = BotConfig.defaultGeminiModel

    private val defaultModelName = BotConfig.defaultGeminiModel
    private val fallbackModelName = BotConfig.fallbackGeminiModel

    fun getGeminiClient(): GeminiClient {
        updateDailyRequestCount()

        val modelNameToUse = if (requestCount < DAILY_REQUEST_LIMIT) {
            defaultModelName
        } else {
            fallbackModelName
        }
        currentModelName = modelNameToUse

        return GeminiClient(
            GeminiClient.Configuration.create(
                apiKey = BotConfig.geminiApiKey,
                modelName = modelNameToUse,
                safetySettingsBuilder = {
                    blockNone(SafetyCategory.HARM_CATEGORY_HATE_SPEECH)
                    blockNone(SafetyCategory.HARM_CATEGORY_HARASSMENT)
                    blockNone(SafetyCategory.HARM_CATEGORY_SEXUALLY_EXPLICIT)
                    blockNone(SafetyCategory.HARM_CATEGORY_DANGEROUS_CONTENT)
                    blockNone(SafetyCategory.HARM_CATEGORY_CIVIC_INTEGRITY)
                }
            ))
    }

    fun incrementRequestCount() {
        requestCount++
        println("Gemini request made. Daily request count: $requestCount, model: $currentModelName")
    }

    private fun updateDailyRequestCount() {
        val currentDate = LocalDate.now()
        if (currentDate > lastRequestDate) {
            requestCount = 0
            lastRequestDate = currentDate
            println("New day started. Request count reset.")
        }
    }
}