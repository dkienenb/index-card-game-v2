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
import kotlinx.html.*
import kotlinx.serialization.Serializable

@Serializable
data class UserSession(
    val username: String = "Remote User",
    val choiceMessage: String = "",
    val choiceOptions: List<String> = listOf(),
    val playerChoice: String? = null,
    val messageBacklog: List<String> = listOf()
)

class KtorClient(val username: String, val server: KtorServer) : Client() {

    override fun displayMessage(message: String) {
        val sessions = server.sessions
        val currentSession = sessions[username]
            ?: throw IllegalStateException("Somehow don't have a session for user $username")
        val newSession = UserSession(currentSession.username, currentSession.choiceMessage, currentSession.choiceOptions,
            currentSession.playerChoice, currentSession.messageBacklog + message)
        val newSessions = sessions - currentSession.username + Pair(username, newSession)
        server.sessions = newSessions
    }

    override fun checkIfPlayerWants(message: String, additionalData: Map<String, String>): Boolean {
        additionalData.forEach { displayMessage("${it.key} is ${it.value}.") }
        return makeChoice("Do you want $message?", setOf("Yes", "No")) == "Yes"
    }

    override fun makeChoice(choiceLabel: String, options: Set<String>): String {
        val sessions = server.sessions
        val currentSession = sessions[username]
            ?: throw IllegalStateException("Somehow don't have a session for user $username")
        val optionsList = options.toList().mapIndexed { index, option -> "[${index}] $option" }
        val newSession = UserSession(currentSession.username, choiceLabel, optionsList, "", currentSession.messageBacklog)
        val newSessions = sessions - currentSession.username + Pair(username, newSession)
        server.sessions = newSessions
        val currentChoice : String
        while (true) {
            val currentSession2 = server.sessions[username]
                ?: throw IllegalStateException("Somehow don't have a session for user $username")
            val newChoice = currentSession2.playerChoice
            if (newChoice != null) {
                if (options.contains(newChoice)) {
                    currentChoice = newChoice
                    break
                }
                val intResult = newChoice.toIntOrNull()
                if (intResult != null) {
                    currentChoice = optionsList[intResult]
                    break
                }
            }
            Thread.sleep(1000)
        }
        val sessions2 = server.sessions
        val currentSession2 = sessions2[username]
            ?: throw IllegalStateException("Somehow don't have a session for user $username")
        val newSession2 = UserSession(currentSession2.username, "", listOf(), "", currentSession2.messageBacklog)
        val newSessions2 = sessions2 - currentSession2.username + Pair(username, newSession2)
        server.sessions = newSessions2
        return currentChoice
    }

    override fun getName(): String = username

}

class KtorServer {

    private var clients = mapOf<String, KtorClient>()
    var sessions = mapOf<String, UserSession>()

    init {
        embeddedServer(Netty, port = 8080, host = "0.0.0.0", module = { module() }).start()
    }

    fun addClient(username: String): KtorClient {
        val client = KtorClient(username, this)
        clients += Pair(username, client)
        sessions += Pair(username, UserSession(username))
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
                Thread.sleep(500)
                val cachedSession = call.sessions.get<UserSession>()
                val session = sessions[cachedSession?.username]
                call.respondHtml {
                    head {
                        title("Index card game v2 KTor client")
                    }
                    body {
                        if (session != null) {
                            h1 { +"Welcome, ${session.username}!" }
                            h2 { +"Session id: ${call.sessionId}" }
                            for (message in session.messageBacklog) {
                                h3 { +message }
                            }
                            br
                            form(action = "clear_backlog", method = FormMethod.post) {
                                button(type = ButtonType.submit) { +"Clear backlog" }
                            }
                            br
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
                            form(action = "set_name", method = FormMethod.post) {
                                label {
                                    +"Name: "
                                    input(type = InputType.text, name = "username") {
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
                val session = sessions[cachedSession?.username]
                if (session != null) {
                    val username = session.username
                    val newSession = UserSession(username, session.choiceMessage, session.choiceOptions,
                        session.playerChoice, listOf()
                    )
                    sessions = sessions - username + Pair(username, newSession)
                }
                call.respondRedirect("/")
            }

            post("/set_name") {
                val parameters = call.receiveParameters()
                val username = parameters["username"]
                if (username != null) {
                    val userSession = sessions[username]
                    if (userSession != null) {
                        call.sessions.set(userSession)
                    } else {
                        call.respondRedirect("/?invalid_session=1")
                    }
                }
                call.respondRedirect("/")
            }

            post("/decide") {
                val cachedSession = call.sessions.get<UserSession>()
                val session = sessions[cachedSession?.username]
                if (session != null) {
                    val username = session.username
                    val parameters = call.receiveParameters()
                    val choice = parameters["choice"]
                    val newSession = UserSession(username, session.choiceMessage, session.choiceOptions, choice, session.messageBacklog)
                    sessions = sessions - username + Pair(username, newSession)
                }
                call.respondRedirect("/")
            }
        }
    }
}