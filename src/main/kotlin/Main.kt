import BotConfig.bootstrapConfig
import com.github.demidko.telegram.TelegramStorage.Constructors.TelegramStorage
import com.github.sanctuscorvus.GeminiClient
import com.github.sanctuscorvus.SafetyCategory
import io.github.cdimascio.dotenv.Dotenv
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.StandardCopyOption
import java.util.zip.Deflater
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream

fun main() {
    println("Current working directory: ${Paths.get(".").toAbsolutePath()}")
    val telegramStorage = TelegramStorage<StorageKey, StorageValue>(
        bot = com.github.kotlintelegrambot.bot { token = BotConfig.telegramBotToken },
        channel = com.github.kotlintelegrambot.entities.ChatId.fromId(BotConfig.telegramStorageChannelId.toLong())
    )

    bootstrapConfig(telegramStorage)

    val sessionDir = Paths.get("test-session")

    if (Files.exists(sessionDir) && sessionDir.toFile().list()?.isNotEmpty() == true) {
        println("Локальная сессия найдена, загрузка из TelegramStorage пропущена.")
    } else {
        val storedSession = telegramStorage[StorageKey.Session]
        if (storedSession is StorageValue.SessionValue) {
            println("Локальная сессия не найдена или пуста, восстанавливаю из TelegramStorage...")
            unzipToDirectory(storedSession.zipData, sessionDir)
            println("Сессия из TelegramStorage успешно восстановлена.")
            println("Содержимое директории test-session после распаковки:")
            Files.walk(sessionDir)
                .filter { Files.isRegularFile(it) }
                .forEach { file ->
                    val fileSizeKB = Files.size(file) / 1024.0
                    val fullPath = file.toString() // Полный путь
                    println("  - ${file.fileName} (${"%.2f".format(fileSizeKB)} KB), Path: $fullPath") // Лог с полным путем
                }
        } else {
            println("Сессия не найдена ни локально, ни в TelegramStorage. Используется новая сессия.")
        }
    }

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

    val chatBot = GeminiBot(BotConfig, geminiClient, telegramStorage)

    chatBot.start()
    chatBot.autoUpdateSession(sessionDir, telegramStorage)


}

fun zipDirectory(sourceDirPath: Path): ByteArray {
    val byteArrayOutputStream = ByteArrayOutputStream()
    ZipOutputStream(byteArrayOutputStream).use { zipOut ->
        zipOut.setLevel(Deflater.BEST_COMPRESSION)
        Files.walk(sourceDirPath)
            .filter { Files.isRegularFile(it) }
            .forEach { filePath ->
                // Нормализуем путь, заменяя обратные слэши на прямые
                val relativePath = sourceDirPath.relativize(filePath).toString().replace(File.separatorChar, '/')
                val zipEntry = ZipEntry(relativePath).apply {
                    method = ZipEntry.DEFLATED
                }
                zipOut.putNextEntry(zipEntry)
                Files.copy(filePath, zipOut)
                zipOut.closeEntry()
            }
    }
    return byteArrayOutputStream.toByteArray()
}


fun unzipToDirectory(zipData: ByteArray, targetDir: Path) {
    ZipInputStream(ByteArrayInputStream(zipData)).use { zipIn ->
        var entry: ZipEntry? = zipIn.nextEntry
        while (entry != null) {
            val filePath = targetDir.resolve(entry.name)
            if (entry.isDirectory) {
                Files.createDirectories(filePath)
            } else {
                Files.createDirectories(filePath.parent)
                Files.copy(zipIn, filePath, StandardCopyOption.REPLACE_EXISTING)
            }
            zipIn.closeEntry()
            entry = zipIn.nextEntry
        }
    }
}