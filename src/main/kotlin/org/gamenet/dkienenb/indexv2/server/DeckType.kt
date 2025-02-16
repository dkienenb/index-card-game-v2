package org.gamenet.dkienenb.indexv2.server

enum class DeckType(val typeName: String) {
    SEWERS("Sewers"),
    CASTLE("Castle"),
    MYSTIC("Mystic");

    fun initialCardList(player: Player?): List<Card> {
        val slime = Tag("Slime")
        val lowerBeing = Tag("Lower Being")
        return when (this) {
            SEWERS -> listOf(
                FightingCard("Purple slime", 0, 1, 0, 0, false, player, slime),
                FightingCard("Brown slime", 1, 1, 1, 0, false, player, slime),
                FightingCard("Green slime", 2, 2, 2, 0, false, player, slime),
                FightingCard("Blue slime", 3, 3, 3, 0, false, player, slime),
            )
            CASTLE -> listOf(
                FightingCard("Archer", 3, 2, 1, 0, true, player),
                FightingCard("Archer", 3, 2, 1, 0, true, player),
            )
            MYSTIC -> listOf(
                FightingCard("Muk", 1, 1, 1, 0, false, player, lowerBeing),
                FightingCard("Muk", 1, 1, 1, 0, false, player, lowerBeing),
                FightingCard("Muk", 1, 1, 1, 0, false, player, lowerBeing),
                FightingCard("Muk", 1, 1, 1, 0, false, player, lowerBeing),
            )
        }
    }

}
