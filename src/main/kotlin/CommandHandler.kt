import dev.inmo.tgbotapi.bot.TelegramBot
import dev.inmo.tgbotapi.extensions.api.send.reply
import dev.inmo.tgbotapi.extensions.behaviour_builder.buildBehaviourWithLongPolling
import dev.inmo.tgbotapi.extensions.behaviour_builder.filters.MessageFilterByChat
import dev.inmo.tgbotapi.extensions.behaviour_builder.triggers_handling.onCommand
import dev.inmo.tgbotapi.extensions.behaviour_builder.triggers_handling.onCommandWithArgs
import dev.inmo.tgbotapi.extensions.behaviour_builder.triggers_handling.onContentMessage
import dev.inmo.tgbotapi.extensions.utils.asTextContent
import dev.inmo.tgbotapi.extensions.utils.extensions.raw.from
import dev.inmo.tgbotapi.types.message.Markdown
import dev.inmo.tgbotapi.utils.PreviewFeature
import dev.inmo.tgbotapi.utils.RiskFeature
import io.github.oshai.kotlinlogging.KLogger
import io.ipinfo.api.errors.RateLimitedException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.Clock
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import utils.*
import utils.Database

@OptIn(RiskFeature::class, PreviewFeature::class)
class CommandHandler(private val bot: TelegramBot, private val logger: KLogger) {
    init {
        runBlocking(Dispatchers.Default) {
            bot.buildBehaviourWithLongPolling {
                onCommand("start", initialFilter = {
                    it.content.textSources.size == 1
                }) {
                    logger.info {
                        it.from?.username.toString() + "issued command: start"
                    }
                    Database.updateUseDate(it.chat.id.chatId.toInt())
                    reply(it, "Hi, I'm the bot of The Nanami(@thynanami)")
                }

                onCommand("getme", initialFilter = {
                    it.content.textSources.size == 1
                }) { message ->
                    logger.info {
                        message.from?.username.toString() + "issued command: start"
                    }

                    val userId = message.chat.id.chatId.toInt()
                    val time = Clock.System.now()

                    Database
                    transaction {
                        addLogger(StdOutSqlLogger)

                        SchemaUtils.create(Users)

                        val result = Users.select {
                            Users.userid.eq(userId)
                        }

                        if (result.empty()) {
                            Users.insert {
                                it[userid] = userId
                                it[lastUse] = time
                                it[firstUse] = time
                                it[role] = "user"
                            }
                        } else {
                            Database.updateUseDate(userId)

                            result.forEach {
                                launch {
                                    reply(
                                        message,
                                        MessageBuilder.info(it, message, logger),
                                        Markdown
                                    )
                                }
                            }
                        }
                    }
                }

                onCommandWithArgs("ip") { message, args ->
                    if (args.size > 3) {
                        reply(message, "You can only query 3 ips in one time.")
                    }
                    args.forEach {
                        try {
                            reply(message, IpInfo.getIpInfo(it).toString())
                        } catch (ex: RateLimitedException) {
                            reply(message, "API rate limit reached! Please try again later.")
                            return@onCommandWithArgs
                        }
                    }
                }

                onContentMessage(subcontextUpdatesFilter = MessageFilterByChat) {
                    val content = it.content.asTextContent()?.text ?: return@onContentMessage
                    if (!Bilibili.isBV(content)) {
                        return@onContentMessage
                    }
                    reply(it, "https://bilibili.com/video/${content}")
                }
            }.join()
        }
    }
}
