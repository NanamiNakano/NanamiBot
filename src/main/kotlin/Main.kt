import dev.inmo.tgbotapi.bot.ktor.telegramBot
import dev.inmo.tgbotapi.extensions.api.bot.getMe
import io.github.oshai.kotlinlogging.KotlinLogging

suspend fun main() {
    val bot = telegramBot(System.getenv("BOT_TOKEN"))
    val me = bot.getMe()

    val logger = KotlinLogging.logger { }
    logger.info { "Bot is Running, BotID ${me.id}, Name ${me.username}" }
    CommandHandler(bot, logger)
}
