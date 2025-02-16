package org.gamenet.dkienenb.indexv2.server

import org.gamenet.dkienenb.component.ComponentedObject

class Deck(player: Player, deckType: DeckType) : ComponentedObject() {
    init {
        addComponent(MaxHealthComponent(100))
        addComponent(MinHealthComponent())
        addComponent(MortalComponent())
        addComponent(HealthComponent())
        addComponent(DefenseComponent(0))
        addComponent(PurchasableComponent(0))
        addComponent(NameComponent("${deckType.typeName} Deck"))
        addComponent(TagComponent())
        addComponent(TargetComponent())
        addComponent(OriginalPlayerOwnedComponent(player))
        val deckComponent = DeckComponent(deckType, player)
        addComponent(deckComponent)
        deckComponent.shuffle()
    }
}