package org.gamenet.dkienenb.indexv2.client

import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.html.*
import io.ktor.server.netty.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import io.ktor.util.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.html.*
import kotlinx.serialization.Serializable

@Serializable
data class UserSession(
    val token: String,
    val username: String = "Remote user",
    val choiceMessage: String = "",
    val choiceOptions: List<String> = listOf(),
    val playerChoice: String? = null,
    val messageBacklog: List<String> = listOf()
)

class KtorServer {

    private var clients = mapOf<String, KtorClient>()
    var sessions = mapOf<String, UserSession>()

    init {
        embeddedServer(Netty, port = 8080, host = "0.0.0.0", module = { module() }).start()
    }

    fun modifySession(token: String?, modifier: (UserSession) -> UserSession) {
        if (token != null) {
            val oldSession = sessions[token]
            if (oldSession != null) {
                val newSession = modifier(oldSession)
                sessions = sessions - token + Pair(token, newSession)
            }
        }
    }

    fun addClient(token: String): KtorClient {
        val client = KtorClient(token, this)
        clients += Pair(token, client)
        sessions += Pair(token, UserSession(token))
        return client
    }

    fun Application.module() {
        install(Sessions) {
            val secretSignKey = hex("96202d627ba9409eabccc91b278e")
            cookie<UserSession>("USER_SESSION", SessionStorageMemory()) {
                cookie.path = "/"
                transform(SessionTransportTransformerMessageAuthentication(secretSignKey))
            }
        }

        routing {
            get("/") {
                val cachedSession = call.sessions.get<UserSession>()
                val session = sessions[cachedSession?.token]
                call.respondHtml {
                    head {
                        title("Index card game v2 KTor client")
                    }
                    body {
                        if (session != null) {
                            val client = clients[session.token] ?:
                                throw IllegalStateException("Somehow don't have a client for user ${session.token}")
                            h1 { +"Welcome, ${session.username}!" }
                            form(action = "change_name", method = FormMethod.post) {
                                input(type = InputType.text, name = "name") {
                                    value = session.username
                                }
                                button(type = ButtonType.submit) { +"Change" }
                            }
                            h2 { +"User token - \"${session.token}\" Session id: ${call.sessionId}" }
                            h2 { +"Deck size: ${client.deckSize}" }
                            h2 { +"Money: ${client.remainingMoney}/${client.moneyDieResult}" }
                            h3 { +"Players:" }
                            client.players
                                .map {
                                    "${it.playerName} - " +
                                            "${it.playerDeckType} Deck: ${it.playerDeckSize} cards, " +
                                            "Hand: ${it.playerHandSize}"
                                }
                                .forEach { h4 { +it } }
                            if (session.messageBacklog.isNotEmpty()) {
                                session.messageBacklog
                                    .map { it + "\n" }
                                    .forEach { div { +it } }
                                br
                                form(action = "clear_backlog", method = FormMethod.post) {
                                    button(type = ButtonType.submit) { +"Clear backlog" }
                                }
                                br
                            }
                            h2 { +session.choiceMessage}
                            for (option in session.choiceOptions) {
                                h3 { +option }
                            }
                            if (session.choiceMessage != "") {
                                form(action = "decide", method = FormMethod.post) {
                                    label {
                                        +"Choice: "
                                        input(type = InputType.text, name = "choice") {
                                            value = ""
                                        }
                                    }
                                    button(type = ButtonType.submit) { +"Submit" }
                                }
                            }
                            form(action = "/", method = FormMethod.get) {
                                button(type = ButtonType.submit) { +"Reload" }
                            }
                        } else {
                            form(action = "set_token", method = FormMethod.post) {
                                label {
                                    +"User token: "
                                    input(type = InputType.text, name = "token") {
                                        value = ""
                                    }
                                }
                                button(type = ButtonType.submit) { +"Submit" }
                            }
                        }
                    }
                }
            }

            post("/clear_backlog") {
                val cachedSession = call.sessions.get<UserSession>()
                modifySession(cachedSession?.token) {
                    UserSession(
                        it.token, it.username, it.choiceMessage, it.choiceOptions,
                        it.playerChoice, listOf()
                    )
                }
                reload()
            }

            post("/change_name") {
                val cachedSession = call.sessions.get<UserSession>()
                val parameters = call.receiveParameters()
                val name = parameters["name"]
                modifySession(cachedSession?.token) {
                    UserSession(
                        it.token, name ?: it.username, it.choiceMessage, it.choiceOptions,
                        it.playerChoice, it.messageBacklog
                    )
                }
                reload()
            }

            post("/set_token") {
                val parameters = call.receiveParameters()
                val token = parameters["token"]
                if (token != null) {
                    val userSession = sessions[token]
                    if (userSession != null) {
                        call.sessions.set(userSession)
                    } else {
                        call.respondRedirect("/?invalid_session=1")
                    }
                }
                reload()
            }

            post("/decide") {
                val cachedSession = call.sessions.get<UserSession>()
                val parameters = call.receiveParameters()
                val choice = parameters["choice"]
                modifySession(cachedSession?.token) {
                    UserSession(
                        it.token, it.username, it.choiceMessage, it.choiceOptions,
                        choice, it.messageBacklog
                    )
                }
                reload()
            }
        }
    }

    private suspend fun RoutingContext.reload() {
        withContext(Dispatchers.IO) {
            Thread.sleep(500)
        }
        call.respondRedirect("/")
    }
}