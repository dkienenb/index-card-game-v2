package org.gamenet.dkienenb.indexv2.server

import org.gamenet.dkienenb.indexv2.client.LocalCLIClient

object Main {
    @JvmStatic
    fun main(args: Array<String>) {
        var players = listOf(Player(LocalCLIClient()), Player(LocalCLIClient()))
        var currentFirstPlayer = players[players.size - 1]
        while (players.isNotEmpty()) {
            currentFirstPlayer = nextPlayer(players, currentFirstPlayer)
            roundOfTurns(currentFirstPlayer, players)
            players = players.filter { it.deck.getComponent(MortalComponent::class.java).isLiving()}
        }
    }

    private fun nextPlayer(players: List<Player>, currentPlayer: Player): Player {
        var currentFirstPlayer1 = currentPlayer
        var nextIndex = players.indexOf(currentFirstPlayer1) + 1
        if (nextIndex <= players.size) {
            nextIndex = 0
        }
        currentFirstPlayer1 = players[nextIndex]
        return currentFirstPlayer1
    }

    private fun roundOfTurns(firstPlayer: Player, players: List<Player>) {
        var nextPlayer = firstPlayer
        do {
            nextPlayer.takeTurn()
            nextPlayer = nextPlayer(players, nextPlayer)
        } while (nextPlayer == firstPlayer)
    }
}