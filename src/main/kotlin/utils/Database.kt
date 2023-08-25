package utils

import kotlinx.datetime.Clock
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.transactions.transaction

object Database {
    init {
        Database.connect(
            url = "jdbc:pgsql://localhost:5432/postgres",
            user = "postgres",
            password = "supersafepassword"
        )
    }

    fun updateUseDate(userId: Int) {
        transaction {
            addLogger(StdOutSqlLogger)

            SchemaUtils.create(Users)

            Users.update({
                Users.userid.eq(userId)
            }) {
                it[lastUse] = Clock.System.now()
            }
        }
    }
}
