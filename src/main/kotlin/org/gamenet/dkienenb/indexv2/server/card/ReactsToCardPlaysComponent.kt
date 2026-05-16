package org.gamenet.dkienenb.indexv2.server.card

import org.gamenet.dkienenb.component.ComponentedObject

public class ReactsToCardPlaysComponent : ReactiveComponent<Card>() {
    fun addPlayReaction(effect: (ComponentedObject, Card) -> Unit) {
        addEffect(effect)
    }

    fun onCardPlay(card: Card) {
        trigger(card)
    }
}