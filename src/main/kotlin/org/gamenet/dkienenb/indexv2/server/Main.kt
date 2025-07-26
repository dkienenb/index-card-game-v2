package org.gamenet.dkienenb.indexv2.server

import org.gamenet.dkienenb.event.EventBus
import org.gamenet.dkienenb.event.EventListener
import org.gamenet.dkienenb.event.EventListenerPriorityLevel
import org.gamenet.dkienenb.indexv2.client.Client
import org.gamenet.dkienenb.indexv2.client.KtorServer
import org.gamenet.dkienenb.indexv2.client.RandomDecisionsAIClient
import org.gamenet.dkienenb.indexv2.client.message.Message
import org.gamenet.dkienenb.indexv2.client.message.MoneyDieMessage
import org.gamenet.dkienenb.indexv2.client.message.PlayerInfoMessage
import org.gamenet.dkienenb.indexv2.server.card.deck.DeckComponent
import org.gamenet.dkienenb.indexv2.server.combat.HealthComponent
import kotlin.random.Random

object Main {

    lateinit var players: List<Player>
    private val ktorServer = KtorServer()
    private val clientSendAllEventBus: EventBus = EventBus()

    fun sendMessageToAll(message: String, exception: Player?) {
        clientSendAllEventBus.addEvent(MultiSendEventOld(message, exception))
        clientSendAllEventBus.callNextEvent()
        println(message)
    }

    fun sendMessageToAll(message: Message) {
        clientSendAllEventBus.addEvent(MultiSendEventNew(message))
        clientSendAllEventBus.callNextEvent()
        println(message.toStringMessage())
    }

    private fun prepare(client: Client): Client {
        clientSendAllEventBus.addListener(
            EventListener(
                MultiSendEventOld::class.java,
                EventListenerPriorityLevel.REACT
            ) { event ->
                val multiSendEvent = event as MultiSendEventOld
                if (client != multiSendEvent.exception?.client) {
                    client.displayMessage(multiSendEvent.message)
                }
            })
        clientSendAllEventBus.addListener(
            EventListener(
                MultiSendEventNew::class.java,
                EventListenerPriorityLevel.REACT
            ) { event ->
                val multiSendEvent = event as MultiSendEventNew
                client.displayMessage(multiSendEvent.message)
            })
        return client
    }

    @JvmStatic
    fun main(args: Array<String>) {
        players = listOf(
            Player(prepare(ktorServer.addClient("butter")), 1),
            Player(prepare(ktorServer.addClient("spark")), 2),
            Player(prepare(RandomDecisionsAIClient("BillyBob")), 4),
            Player(prepare(RandomDecisionsAIClient("Hal")), 5),
        )
        for (player in players) {
            sendMessageToAll(
                PlayerInfoMessage(player.id, player.client.getName(),
                    player.deck.getComponent(DeckComponent::class.java).type.typeName,
                    player.deck.getComponent(HealthComponent::class.java).getHealth())
            )
        }
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
        sendMessageToAll(MoneyDieMessage(money))
        var nextPlayer = firstPlayer
        do {
            if (!nextPlayer.isOut()) {
                nextPlayer.takeTurn(players)
            }
            nextPlayer = nextPlayer(players, nextPlayer)
        } while ((nextPlayer != firstPlayer) && (players.filterNot { it.isOut() }.size > 1))
    }
}