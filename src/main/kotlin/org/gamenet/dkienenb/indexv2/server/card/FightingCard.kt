package org.gamenet.dkienenb.indexv2.server.card

import org.gamenet.dkienenb.indexv2.server.Player
import org.gamenet.dkienenb.indexv2.server.combat.AttackerComponent
import org.gamenet.dkienenb.indexv2.server.status.StatusEffectComponent
import org.gamenet.dkienenb.indexv2.server.status.StatusEffectInfliction
import org.gamenet.dkienenb.indexv2.server.status.StatusEffects

public class FightingCard(
    name: String,
    cost: Int,
    health: Int,
    damage: Int,
    defense: Int = 0,
    ranged: Boolean = false,
    player: Player,
    inflictions: List<StatusEffectInfliction> = emptyList(),
) : AttackableCard(name, cost, health, defense, player, inflictions, {
    if (it.getComponent(StatusEffectComponent::class.java).has(StatusEffects.FLYING)) {
        CardPlayResultLocation.BATTLE_PLAYER_CHOICE
    } else {
        CardPlayResultLocation.BATTLE_BACK
    }
}) {
    init {
        addComponent(AttackerComponent(damage, ranged))
    }
}