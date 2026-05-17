package org.gamenet.dkienenb.indexv2.server.card.deck

import org.gamenet.dkienenb.indexv2.server.Player
import org.gamenet.dkienenb.indexv2.server.card.Card
import org.gamenet.dkienenb.indexv2.server.card.FightingCard
import org.gamenet.dkienenb.indexv2.server.status.CreatureTypes
import org.gamenet.dkienenb.indexv2.server.status.StatusEffects

public class SewerDeckType : DeckType("Sewers") {

    val ratTypeInfliction = generateSelfInfliction(1, CreatureTypes.RAT)
    val slimeTypeInfliction = generateSelfInfliction(1, CreatureTypes.SLIME)

    val spikedRevengeInfliction = generateOnKilledInfliction(1, 1, StatusEffects.SPIKED)

    override fun getInitialCardList(player: Player): List<Card> {
        return listOf(
            FightingCard("Purple slime", 0, 1, 0, player = player,
                inflictions = listOf(slimeTypeInfliction)),
            FightingCard("Brown slime", 1, 1, 1, player = player,
                inflictions = listOf(slimeTypeInfliction)),
            FightingCard("Green slime", 2, 2, 1, player = player,
                inflictions = listOf(slimeTypeInfliction)),
            FightingCard("Blue slime", 3, 3, 1, player = player,
                inflictions = listOf(slimeTypeInfliction)),
            FightingCard("Spiked slime", 4, 2, 2, player = player,
                inflictions = listOf(slimeTypeInfliction, spikedRevengeInfliction)),
            FightingCard("Two slimes", 4, 2, 2, player = player,
                inflictions = listOf(slimeTypeInfliction, slimeTypeInfliction)),
            FightingCard("Slime and rat", 5, 3, 2, player = player,
                inflictions = listOf(slimeTypeInfliction, ratTypeInfliction)),
            FightingCard("Iron Slime", 6, 3, 1, 1, player = player,
                inflictions = listOf(slimeTypeInfliction))
        )
    }
}