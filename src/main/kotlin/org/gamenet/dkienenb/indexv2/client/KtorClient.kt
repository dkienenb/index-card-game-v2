package org.gamenet.dkienenb.indexv2.client

import org.gamenet.dkienenb.indexv2.client.message.*

data class ClientCardView(
    val cardId: Int,
    val cardName: String,
    val playerId: Int,
    var currentHealth: Int
)

data class ClientPlayerView(
    val playerId: Int,
    val playerName: String,
    val playerDeckType: String,
    var playerDeckSize: Int,
    var playerHandSize: Int
)

class KtorClient(val token: String, val server: KtorServer) : Client() {

    var deckSize = 0
    var remainingMoney = 0
    var moneyDieResult = 0
    var cardsInPlay = listOf<ClientCardView>()
    var players: List<ClientPlayerView> = listOf()

    override fun displayMessage(message: String) {
        server.modifySession(token) {
            UserSession(
                it.token, it.username, it.choiceMessage, it.choiceOptions,
                it.playerChoice, it.messageBacklog + message
            )
        }
    }

    fun modifyPlayer(playerId: Int, modifier: (ClientPlayerView) -> ClientPlayerView) {
        val oldPlayer = players.firstOrNull { it.playerId == playerId }
        if (oldPlayer != null) {
            val newPlayer = modifier(oldPlayer)
            players = players.filter { it.playerId != playerId } + newPlayer
        }
    }

    override fun displayMessage(message: Message) {
        when (message) {
            is DeckSizeMessage -> {
                deckSize = message.deckSize
            }
            is MoneyRemainderMessage -> {
                remainingMoney = message.money
            }
            is MoneyDieMessage -> {
                moneyDieResult = message.money
            }
            is CardPlayedMessage -> {
                cardsInPlay += ClientCardView(message.cardId, message.cardName, message.playerId, message.health)
            }
            is CardDeathMessage -> {
                val uuid = message.cardId
                cardsInPlay -= cardsInPlay.filter { uuid == it.cardId }
            }
            is PlayerInfoMessage -> {
                players += ClientPlayerView(message.playerId, message.playerName, message.deckType, message.deckSize, 0)
            }
            is DrawCardMessage -> {
                modifyPlayer(message.playerId) {
                    ClientPlayerView(it.playerId, it.playerName, it.playerDeckType, it.playerDeckSize - 1, it.playerHandSize + 1)
                }
            }
            else -> {
                displayMessage(message.toStringMessage())
            }
        }
    }

    override fun checkIfPlayerWants(questionType: YesOrNoQuestionType): Boolean {
        val displayType: String
        when (questionType) {
            YesOrNoQuestionType.ANOTHER_CARD -> {
                displayType = "another card"
                if (deckSize <= 1) {
                    return false
                }
            }
            YesOrNoQuestionType.TO_ATTACK -> {
                displayType = "to attack"
            }
        }
        return select("Do you want $displayType?", setOf("Yes", "No")) == "Yes"
    }

    override fun makeChoice(choiceLabel: String, options: Set<String>): String =
        select("Choose a ${choiceLabel}:", options)

    private fun select(choiceMessage: String, options: Set<String>): String {
        val optionsList = options.toList()
        val labeledOptionsList = optionsList.mapIndexed { index, option -> "[${index + 1}] $option" }
        server.modifySession(token) {
            UserSession(
                it.token, it.username, choiceMessage, labeledOptionsList,
                "", it.messageBacklog
            )
        }
        val currentChoice: String
        while (true) {
            val currentSession = server.sessions[token]
                ?: throw IllegalStateException("Somehow don't have a session for user $token")
            val newChoice = currentSession.playerChoice
            if (newChoice != null) {
                if (options.contains(newChoice)) {
                    currentChoice = newChoice
                    break
                }
                val intResult = newChoice.toIntOrNull()
                if (intResult != null && 0 < intResult && intResult <= optionsList.size) {
                    val result = optionsList[intResult - 1]
                    if (options.contains(result)) {
                        currentChoice = result
                        break
                    }
                }
            }
            Thread.sleep(100)
        }
        server.modifySession(token) {
            UserSession(
                it.token, it.username, "", listOf(),
                "", it.messageBacklog
            )
        }
        return currentChoice
    }

    override fun getName(): String = token

}

