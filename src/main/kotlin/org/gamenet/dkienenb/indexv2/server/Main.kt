package org.gamenet.dkienenb.indexv2.server

import org.gamenet.dkienenb.event.EventBus
import org.gamenet.dkienenb.event.EventListener
import org.gamenet.dkienenb.event.EventListenerPriorityLevel
import org.gamenet.dkienenb.indexv2.client.*
import kotlin.random.Random

object Main {

    private val ktorServer = KtorServer()
    private val clientSendAllEventBus: EventBus = EventBus()

    fun sendAllExcept(message: String, exception: Player?) {
        clientSendAllEventBus.addEvent(MultiSendEvent(message, exception))
        clientSendAllEventBus.callNextEvent()
    }

    private fun prepare(client: Client): Client {
        clientSendAllEventBus.addListener(
            EventListener(
                MultiSendEvent::class.java,
                EventListenerPriorityLevel.REACT
            ) { event ->
                val multiSendEvent = event as MultiSendEvent
                if (client != multiSendEvent.exception?.client) {
                    client.displayMessage(multiSendEvent.message)
                }
            })
        return client
    }

    @JvmStatic
    fun main(args: Array<String>) {
        var players = listOf(
            Player(prepare(ktorServer.addClient("Ollieve"))),
            Player(prepare(LocalCLIClient())),
            Player(prepare(RandomDecisionsAIClient("BillyBob"))),
            Player(prepare(RandomDecisionsAIClient("Hal"))),
        )
        var currentFirstPlayer = players.random()
        while (players.size > 1) {
            currentFirstPlayer = nextPlayer(players, currentFirstPlayer)
            roundOfTurns(currentFirstPlayer, players)
            players = players.filterNot { it.isOut() }
        }
    }

    private fun nextPlayer(players: List<Player>, currentPlayer: Player): Player {
        var nextPlayer = currentPlayer
        var nextIndex = players.indexOf(nextPlayer) + 1
        if (nextIndex >= players.size) {
            nextIndex = 0
        }
        nextPlayer = players[nextIndex]
        return nextPlayer
    }

    private fun roundOfTurns(firstPlayer: Player, players: List<Player>) {
        val money = Random.nextInt(1, 7)
        players.forEach { it.unspentMoney = money }
        sendAllExcept("Money die result is $money", null)
        var nextPlayer = firstPlayer
        do {
            if (!nextPlayer.isOut()) {
                nextPlayer.takeTurn(players)
            }
            nextPlayer = nextPlayer(players, nextPlayer)
        } while ((nextPlayer != firstPlayer) && (players.filterNot { it.isOut() }.size > 1))
    }
}