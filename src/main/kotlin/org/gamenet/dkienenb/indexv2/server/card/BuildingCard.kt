package org.gamenet.dkienenb.indexv2.server.card

import org.gamenet.dkienenb.indexv2.server.Player
import org.gamenet.dkienenb.indexv2.server.status.StatusEffectInfliction

class BuildingCard(
    name: String,
    cost: Int,
    health: Int,
    defense: Int = 0,
    player: Player,
    inflictions: List<StatusEffectInfliction> = emptyList(),
) : AttackableCard(name, cost, health, defense, player, inflictions, { CardPlayResultLocation.BUILDING })