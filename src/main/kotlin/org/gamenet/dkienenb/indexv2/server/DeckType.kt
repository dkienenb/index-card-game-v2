package org.gamenet.dkienenb.indexv2.server

import org.gamenet.dkienenb.component.ComponentedObject

enum class DeckType(val typeName: String) {
    SEWERS("Sewers"),
    CASTLE("Castle"),
    MYSTIC("Mystic");

    fun initialCardList(player: Player): List<Card> {
        val slime = Tag("Slime")
        val rat = Tag("Rat")
        val wall = Tag("Wall")
        val lowerBeing = Tag("Lower Being")
        return when (this) {
            SEWERS -> listOf(
                FightingCard("Purple slime", 0, 1, 0, player = player, tags = arrayOf(slime)),
                FightingCard("Brown slime", 1, 1, 1, player = player, tags = arrayOf(slime)),
                FightingCard("Green slime", 2, 2, 1, player = player, tags = arrayOf(slime)),
                FightingCard("Blue slime", 3, 3, 1, player = player, tags = arrayOf(slime)),
                FightingCard("Spiked slime", 4, 2, 2, player = player, tags = arrayOf(slime)).apply {
                    getComponent(MortalComponent::class.java).addTaggedDeathEffect { _: ComponentedObject, attacker: ComponentedObject? ->
                        if (attacker != null) {
                            if (attacker.hasComponent(TargetComponent::class.java)) {
                                attacker.getComponent(TargetComponent::class.java).receiveAttack(
                                    this, 1,
                                    ignoreDefense = true,
                                    hasRetaliation = false,
                                    attackingPlayer = player,
                                    defendingPlayer = attacker.getComponent(OriginalPlayerOwnedComponent::class.java)
                                        .getPlayer()
                                )
                            }
                        }
                    }
                },
                FightingCard("Two slimes", 4, 2, 2, player = player, tags = arrayOf(slime, slime)),
                FightingCard("Slime and rat", 5, 3, 2, player = player, tags = arrayOf(slime, rat)),
                FightingCard("Iron Slime", 6, 3, 1, 1, player = player, tags = arrayOf(slime))
            )

            CASTLE -> listOf(
                FightingCard("Timber Palisade", 1, 2, 0, player = player, tags = arrayOf(wall))
                    .addComponent(BlocksRangedAttacksComponent()) as FightingCard,
                FightingCard("Rammed Earth Wall", 2, 3, 0, player = player, tags = arrayOf(wall))
                    .addComponent(BlocksRangedAttacksComponent()) as FightingCard,
                FightingCard("Brick Wall", 3, 4, 0, player = player, tags = arrayOf(wall))
                    .addComponent(BlocksRangedAttacksComponent()) as FightingCard,
                FightingCard("Limestone Block Wal", 4, 5, 0, player = player, tags = arrayOf(wall))
                    .addComponent(BlocksRangedAttacksComponent()) as FightingCard,
                FightingCard("Granite-Basalt Block Wall", 5, 6, 0, player = player, tags = arrayOf(wall))
                    .addComponent(BlocksRangedAttacksComponent()) as FightingCard,
                FightingCard("Tungsten-Titanium Wall", 6, 5, 0, 1, player = player, tags = arrayOf(wall))
                    .addComponent(BlocksRangedAttacksComponent()) as FightingCard,
                FightingCard("Archer", 3, 2, 1, 0, true, player),
                FightingCard("Archer", 3, 2, 1, 0, true, player),
            )

            MYSTIC -> listOf(
                FightingCard("Muk", 1, 1, 1, player = player, tags = arrayOf(lowerBeing)),
                FightingCard("Muk", 1, 1, 1, player = player, tags = arrayOf(lowerBeing)),
                FightingCard("Muk", 1, 1, 1, player = player, tags = arrayOf(lowerBeing)),
                FightingCard("Muk", 1, 1, 1, player = player, tags = arrayOf(lowerBeing)),
            )
        }
    }

}
