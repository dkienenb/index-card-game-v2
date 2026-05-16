package org.gamenet.dkienenb.indexv2.server.card.deck

import org.gamenet.dkienenb.component.ComponentedObject
import org.gamenet.dkienenb.indexv2.server.Player
import org.gamenet.dkienenb.indexv2.server.card.*
import org.gamenet.dkienenb.indexv2.server.combat.*
import org.gamenet.dkienenb.indexv2.server.status.StatusEffectComponent

public class Deck(player: Player, deckType: DeckType) : ComponentedObject() {
    init {
        addComponent(CardIdComponent())
        // placeholder value, when the deck component is added, this is addressed properly
        addComponent(MaxHealthComponent(100))
        addComponent(MinHealthComponent())
        addComponent(MortalComponent())
        addComponent(HealthComponent())
        addComponent(DefenseComponent(0))
        addComponent(PurchasableComponent(0))
        addComponent(NameComponent("${deckType.typeName} Deck"))
        addComponent(TargetComponent())
        addComponent(OriginalPlayerOwnedComponent(player))
        addComponent(PlayerOwnedComponent(player))
        addComponent(StatusEffectComponent())
        val deckComponent = DeckComponent(deckType, player)
        addComponent(deckComponent)
        deckComponent.shuffle()
    }
}