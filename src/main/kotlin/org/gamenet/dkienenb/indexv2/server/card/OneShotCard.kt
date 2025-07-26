package org.gamenet.dkienenb.indexv2.server.card

import org.gamenet.dkienenb.indexv2.server.Player

class OneShotCard(name: String, cost: Int, val action: (Player) -> CardPlayResultLocation) : Card(name, cost) {
    init {
        getComponent(OnPlayEffectsComponent::class.java).addOnPlayEffect{ _, player, _ ->
            return@addOnPlayEffect action(player)
        }
    }
}