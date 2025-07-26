package org.gamenet.dkienenb.indexv2.server.card

import org.gamenet.dkienenb.component.ComponentedObject
import org.gamenet.dkienenb.indexv2.server.Player

abstract class Card(name: String, cost: Int) : ComponentedObject() {
    init {
        this.addComponent(PurchasableComponent(cost))
        this.addComponent(NameComponent(name))
        this.addComponent(CardIdComponent())
        this.addComponent(OnPlayEffectsComponent())
        this.addComponent(CardComponent())
    }

    fun play(player: Player): CardPlayResultLocation {
        var current = CardPlayResultLocation.DISCARD
        getComponent(OnPlayEffectsComponent::class.java).streamEffects().forEach {
            current = it(this, player, current)
        }
        return current
    }
}