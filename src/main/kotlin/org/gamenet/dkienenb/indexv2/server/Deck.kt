package org.gamenet.dkienenb.indexv2.server

import org.gamenet.dkienenb.component.ComponentedObject

class Deck(player: Player, deckType: DeckType) : ComponentedObject() {
    init {
        addComponent(MaxHealthComponent(0))
        addComponent(MinHealthComponent())
        addComponent(MortalComponent())
        addComponent(HealthComponent())
        addComponent(DefenseComponent(0))
        addComponent(PurchasableComponent(0))
        addComponent(NameComponent("$deckType Deck"))
        addComponent(TagComponent())
        addComponent(TargetComponent())
        addComponent(PlayerOwnedComponent(player))
        addComponent(DeckComponent(deckType))
    }
}