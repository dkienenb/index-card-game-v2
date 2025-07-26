package org.gamenet.dkienenb.indexv2.server.card

import org.gamenet.dkienenb.indexv2.client.message.CardDeathMessage
import org.gamenet.dkienenb.indexv2.server.Main
import org.gamenet.dkienenb.indexv2.server.Player
import org.gamenet.dkienenb.indexv2.server.card.deck.DeckComponent
import org.gamenet.dkienenb.indexv2.server.combat.*
import org.gamenet.dkienenb.indexv2.server.status.StatusEffectComponent
import org.gamenet.dkienenb.indexv2.server.status.StatusEffectInfliction
import org.gamenet.dkienenb.indexv2.server.status.StatusEffectInflictorComponent
import org.gamenet.dkienenb.indexv2.server.status.StatusEffects

class FightingCard(
    name: String,
    cost: Int,
    health: Int,
    damage: Int,
    defense: Int = 0,
    ranged: Boolean = false,
    player: Player,
    inflictions: List<StatusEffectInfliction> = emptyList(),
) : Card(name, cost) {
    init {
        addComponent(PlayerOwnedComponent(player))
        addComponent(OriginalPlayerOwnedComponent(player))
        addComponent(MortalComponent())
        addComponent(MaxHealthComponent(health))
        addComponent(MinHealthComponent())
        addComponent(HealthComponent())
        addComponent(DefenseComponent(defense))
        addComponent(AttackerComponent(damage, ranged))
        addComponent(TargetComponent())
        val statusEffectComponent = StatusEffectComponent()
        addComponent(statusEffectComponent)
        if (inflictions.isNotEmpty()) {
            val statusEffectInflictorComponent = StatusEffectInflictorComponent()
            addComponent(statusEffectInflictorComponent)
            inflictions.forEach{
                statusEffectInflictorComponent.addInfliction(it)
            }
        }
        getComponent(MortalComponent::class.java).addDeathEffect { it, _ ->
            Main.sendMessageToAll(CardDeathMessage(it.getComponent(CardIdComponent::class.java).getId()))
        }
        getComponent(OnPlayEffectsComponent::class.java).addOnPlayEffect{
            _, playerWhoPlayedThis, _ ->
            getComponent(HealthComponent::class.java).setHealth(getComponent(MaxHealthComponent::class.java).getMaxHealth())
            getComponent(MortalComponent::class.java).revive()
            getComponent(MortalComponent::class.java).addDeathEffect { it, _ ->
                playerWhoPlayedThis.deck.getComponent(DeckComponent::class.java).discardCard(it as Card)
                playerWhoPlayedThis.removeFromPlay(it)
            }
            if (getComponent(StatusEffectComponent::class.java).has(StatusEffects.FLYING)) {
                return@addOnPlayEffect CardPlayResultLocation.BATTLE_PLAYER_CHOICE
            }
            return@addOnPlayEffect CardPlayResultLocation.BATTLE_BACK
        }
    }

}