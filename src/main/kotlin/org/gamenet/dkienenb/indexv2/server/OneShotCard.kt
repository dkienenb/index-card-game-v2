package org.gamenet.dkienenb.indexv2.server

class OneShotCard(name: String, cost: Int, val action: (Player) -> CardPlayResultLocation) : Card(name, cost) {
    override fun play(player: Player) = action(player)
}