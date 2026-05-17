package org.gamenet.dkienenb.indexv2.server.card.deck

import org.gamenet.dkienenb.indexv2.server.Player
import org.gamenet.dkienenb.indexv2.server.card.Card
import org.gamenet.dkienenb.indexv2.server.card.FightingCard
import org.gamenet.dkienenb.indexv2.server.status.CreatureTypes
import org.gamenet.dkienenb.indexv2.server.status.StatusEffects

class CastleDeckType : DeckType("Castle") {
    val wallTypeInfliction = generateSelfInfliction(1, CreatureTypes.WALL)
    val wallInfliction = generateSelfInfliction(1, StatusEffects.WALL)

    val armorPiercingInfliction = generateSelfInfliction(1, StatusEffects.PIERCING)

    override fun getInitialCardList(player: Player): List<Card> {
        return listOf(
            FightingCard("Timber Palisade", 1, 2, 0, player = player,
                inflictions = listOf(wallInfliction, wallTypeInfliction)),
            FightingCard("Rammed Earth Wall", 2, 3, 0, player = player,
                inflictions = listOf(wallInfliction, wallTypeInfliction)),
            FightingCard("Brick Wall", 3, 4, 0, player = player,
                inflictions = listOf(wallInfliction, wallTypeInfliction)),
            FightingCard("Limestone Block Wall", 4, 5, 0, player = player,
                inflictions = listOf(wallInfliction, wallTypeInfliction)),
            FightingCard("Granite-Basalt Block Wall", 5, 6, 0, player = player,
                inflictions = listOf(wallInfliction, wallTypeInfliction)),
            FightingCard("Tungsten-Titanium Wall", 6, 5, 0, 1, player = player,
                inflictions = listOf(wallInfliction, wallTypeInfliction)),
            FightingCard("Archer", 3, 2, 1, 0, true, player),
            FightingCard("Archer", 3, 2, 1, 0, true, player),
            FightingCard("Heavy Archer", 5, 2, 2, 0, true, player = player,
                inflictions = listOf(armorPiercingInfliction)),
        )
    }
}