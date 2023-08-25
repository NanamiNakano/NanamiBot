package utils

import kotlinx.datetime.Instant
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.kotlin.datetime.timestamp

object Users: Table() {
    val userid: Column<Int> = integer("userid")
    val firstUse: Column<Instant> = timestamp("firsUse")
    val lastUse: Column<Instant> = timestamp("lastUse")
    val role: Column<String> = varchar("role", 6)

    override val primaryKey = PrimaryKey(userid)
}
