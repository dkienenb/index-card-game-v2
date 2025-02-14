package org.gamenet.dkienenb.indexv2.server

import org.gamenet.dkienenb.component.ComponentedObject

class Card(name: String, cost: Int) : ComponentedObject() {
    init {
        addComponent(PurchasableComponent(cost))
        addComponent(NameComponent(name))
        addComponent(CardComponent())
    }
}