package org.gamenet.dkienenb.indexv2.server.card

import org.gamenet.dkienenb.indexv2.client.message.CardDeathMessage
import org.gamenet.dkienenb.indexv2.server.Main
import org.gamenet.dkienenb.indexv2.server.Player
import org.gamenet.dkienenb.indexv2.server.card.deck.DeckComponent
import org.gamenet.dkienenb.indexv2.server.combat.*
import org.gamenet.dkienenb.indexv2.server.status.StatusEffectComponent
import org.gamenet.dkienenb.indexv2.server.status.StatusEffectInfliction
import org.gamenet.dkienenb.indexv2.server.status.StatusEffectInflictorComponent

public abstract class AttackableCard(
    name: String,
    cost: Int,
    health: Int,
    defense: Int = 0,
    player: Player,
    inflictions: List<StatusEffectInfliction> = emptyList(),
    wherePlayedDeterminer: (AttackableCard) -> CardPlayResultLocation,
) : Card(name, cost) {
    init {
        this.addComponent(CardIdComponent())
        this.addComponent(PlayerOwnedComponent(player))
        this.addComponent(OriginalPlayerOwnedComponent(player))
        this.addComponent(MortalComponent())
        this.addComponent(MaxHealthComponent(health))
        this.addComponent(MinHealthComponent())
        this. addComponent(HealthComponent())
        this.addComponent(DefenseComponent(defense))
        this.addComponent(TargetComponent())
        val statusEffectComponent = StatusEffectComponent()
        this.addComponent(statusEffectComponent)
        if (inflictions.isNotEmpty()) {
            val statusEffectInflictorComponent = StatusEffectInflictorComponent()
            this.addComponent(statusEffectInflictorComponent)
            inflictions.forEach(statusEffectInflictorComponent::addInfliction)
        }
        this.getComponent(MortalComponent::class.java).addDeathEffect { it, _ ->
            Main.sendMessageToAll(CardDeathMessage(it.getComponent(CardIdComponent::class.java).getId()))
        }
        this.getComponent(OnPlayEffectsComponent::class.java).addOnPlayEffect{
                card, playerWhoPlayedThis, _ ->
            getComponent(StatusEffectComponent::class.java).clearAllStatuses()
            getComponent(HealthComponent::class.java).setHealth(getComponent(MaxHealthComponent::class.java).getMaxHealth())
            getComponent(MortalComponent::class.java).revive()
            getComponent(MortalComponent::class.java).addDeathEffect { it, _ ->
                playerWhoPlayedThis.deck.getComponent(DeckComponent::class.java).discardCard(it as Card)
                playerWhoPlayedThis.removeFromPlay(it)
            }
            return@addOnPlayEffect wherePlayedDeterminer(card as AttackableCard)
        }
    }
}