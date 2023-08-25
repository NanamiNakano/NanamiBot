package utils

import dev.inmo.tgbotapi.extensions.utils.extensions.raw.from
import dev.inmo.tgbotapi.types.message.abstracts.CommonMessage
import dev.inmo.tgbotapi.types.message.content.TextContent
import dev.inmo.tgbotapi.utils.RiskFeature
import io.github.oshai.kotlinlogging.KLogger
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.exposed.sql.ResultRow

object MessageBuilder {
    @OptIn(RiskFeature::class)
    fun info(it: ResultRow, message: CommonMessage<TextContent>, logger: KLogger): String {
        logger.info {
            "Built info message for ${message.from}"
        }
        return "Hello! " + (message.from?.username?.username ?: return "Failed build info message for ${message.from}") + "\n" +
                "Your user id is: " + it[Users.userid] + "\n" +
                "You used this bot for the first time at: " + it[Users.firstUse].toLocalDateTime(TimeZone.UTC) + "(UTC)" + "\n"
    }
}
