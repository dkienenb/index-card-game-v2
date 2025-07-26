package org.gamenet.dkienenb.indexv2.server.card.deck

import org.gamenet.dkienenb.component.ComponentedObject
import org.gamenet.dkienenb.indexv2.server.Player
import org.gamenet.dkienenb.indexv2.server.card.Card
import org.gamenet.dkienenb.indexv2.server.card.FightingCard
import org.gamenet.dkienenb.indexv2.server.combat.MortalComponent
import org.gamenet.dkienenb.indexv2.server.combat.TargetComponent
import org.gamenet.dkienenb.indexv2.server.status.*

enum class DeckType(val typeName: String) {
    SEWERS("Sewers"),
    CASTLE("Castle"),
    MYSTIC("Mystic");

    fun initialCardList(player: Player): List<Card> {
        val wallInfliction = generateSelfInfliction(1, StatusEffects.WALL)
        val armorPiercingInfliction = generateSelfInfliction(1, StatusEffects.PIERCING)
        return when (this) {
            SEWERS -> listOf(
                FightingCard("Purple slime", 0, 1, 0, player = player),
                FightingCard("Brown slime", 1, 1, 1, player = player),
                FightingCard("Green slime", 2, 2, 1, player = player),
                FightingCard("Blue slime", 3, 3, 1, player = player),
                FightingCard("Spiked slime", 4, 2, 2, player = player).apply {
                    getComponent(MortalComponent::class.java).addTaggedDeathEffect { _: ComponentedObject, attacker: ComponentedObject? ->
                        if (attacker != null) {
                            if (attacker.hasComponent(TargetComponent::class.java)) {
                                attacker.getComponent(TargetComponent::class.java).receiveAttack(
                                    this, 1,
                                    ignoreDefense = true,
                                    hasRetaliation = false,
                                )
                            }
                        }
                    }
                },
                FightingCard("Two slimes", 4, 2, 2, player = player),
                FightingCard("Slime and rat", 5, 3, 2, player = player),
                FightingCard("Iron Slime", 6, 3, 1, 1, player = player)
            )

            CASTLE -> listOf(
                FightingCard("Timber Palisade", 1, 2, 0, player = player,
                    inflictions = listOf(wallInfliction)),
                FightingCard("Rammed Earth Wall", 2, 3, 0, player = player,
                    inflictions = listOf(wallInfliction)),
                FightingCard("Brick Wall", 3, 4, 0, player = player,
                    inflictions = listOf(wallInfliction)),
                FightingCard("Limestone Block Wall", 4, 5, 0, player = player,
                    inflictions = listOf(wallInfliction)),
                FightingCard("Granite-Basalt Block Wall", 5, 6, 0, player = player,
                    inflictions = listOf(wallInfliction)),
                FightingCard("Tungsten-Titanium Wall", 6, 5, 0, 1, player = player,
                    inflictions = listOf(wallInfliction)),
                FightingCard("Archer", 3, 2, 1, 0, true, player),
                FightingCard("Archer", 3, 2, 1, 0, true, player),
                FightingCard("Heavy Archer", 5, 2, 2, 0, true, player = player,
                    inflictions = listOf(armorPiercingInfliction)),
            )

            MYSTIC -> listOf(
                FightingCard("Muk", 1, 1, 1, player = player),
                FightingCard("Muk", 1, 1, 1, player = player),
                FightingCard("Muk", 1, 1, 1, player = player),
                FightingCard("Muk", 1, 1, 1, player = player),
            )
        }
    }

    private fun generateSelfInfliction(
        duration: Int,
        effect: StatusEffect
    ) = StatusEffectInfliction(
        StatusEffectInflictionTiming.ON_PLAY,
        StatusEffectInflictionScope.SELF, StatusEffectInflictionAllyState.ALL,
        duration, effect, emptyList()
    )

}
