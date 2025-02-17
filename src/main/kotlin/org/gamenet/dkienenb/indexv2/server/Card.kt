package org.gamenet.dkienenb.indexv2.server

import org.gamenet.dkienenb.component.ComponentedObject

abstract class Card(name: String, cost: Int) : ComponentedObject() {
    init {
        this.addComponent(PurchasableComponent(cost))
        this.addComponent(NameComponent(name))
        this.addComponent(CardComponent())
    }

    abstract fun play(player: Player): CardPlayResultLocation
}