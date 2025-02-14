package org.gamenet.dkienenb.indexv2.server

enum class DeckType(val typeName: String) {
    SEWERS("Sewers"),
    CASTLE("Castle"),
    MYSTIC("Mystic");

    fun initialCardList(): List<Card> {
        // TODO deck
        return listOf(Card("Test card 1", 1), Card("Test card 2", 1))
    }

}
