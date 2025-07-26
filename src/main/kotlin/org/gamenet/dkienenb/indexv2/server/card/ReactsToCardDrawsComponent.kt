package org.gamenet.dkienenb.indexv2.server.card

import org.gamenet.dkienenb.component.ComponentedObject

class ReactsToCardDrawsComponent : ReactiveComponent<Card>() {
    fun addDrawReaction(effect: (ComponentedObject, Card) -> Unit) {
        addEffect(effect)
    }

    fun onCardDraw(card: Card) {
        trigger(card)
    }
}