package org.gamenet.dkienenb.indexv2.server.card.deck

import org.gamenet.dkienenb.indexv2.client.message.DrawCardMessage
import org.gamenet.dkienenb.indexv2.server.Main
import org.gamenet.dkienenb.indexv2.server.Player
import org.gamenet.dkienenb.indexv2.server.card.Card
import org.gamenet.dkienenb.indexv2.server.card.CardPlayResultLocation
import org.gamenet.dkienenb.indexv2.server.card.FightingCard
import org.gamenet.dkienenb.indexv2.server.card.OneShotCard
import org.gamenet.dkienenb.indexv2.server.combat.BurnAttack
import org.gamenet.dkienenb.indexv2.server.combat.DefensiveMove
import org.gamenet.dkienenb.indexv2.server.combat.WindAttack
import org.gamenet.dkienenb.indexv2.server.status.CreatureTypes
import org.gamenet.dkienenb.indexv2.server.status.StatusEffects

class MysticDeckType : DeckType("Mystic") {
    val lowerBeingTypeInfliction = generateSelfInfliction(1, CreatureTypes.LOWER_BEING)

    val noRetaliationInfliction = generateSelfInfliction(1, StatusEffects.NO_RETALIATION)
    val flyingInfliction = generateSelfInfliction(1, StatusEffects.FLYING)
    val platedArmorInfliction = generateSelfInfliction(1, StatusEffects.PLATED_ARMOR)

    override fun getInitialCardList(player: Player): List<Card> {
        return listOf(
            FightingCard("Muk", 1, 1, 1, player = player,
                inflictions = listOf(lowerBeingTypeInfliction)),
            FightingCard("Muk", 1, 1, 1, player = player,
                inflictions = listOf(lowerBeingTypeInfliction)),
            FightingCard("Muk", 1, 1, 1, player = player,
                inflictions = listOf(lowerBeingTypeInfliction)),
            FightingCard("Muk", 1, 1, 1, player = player,
                inflictions = listOf(lowerBeingTypeInfliction)),

            FightingCard("Fire Mystic", 3, 2, 1, player = player,
                inflictions = listOf(lowerBeingTypeInfliction, flyingInfliction))
                .addAlternateAttack(BurnAttack()),
            FightingCard("Fire Mystic", 3, 2, 1, player = player,
                inflictions = listOf(lowerBeingTypeInfliction, flyingInfliction))
                .addAlternateAttack(BurnAttack()),
            FightingCard("Firebrand - Fire Adept", 5, 2, 2, player = player,
                inflictions = listOf(flyingInfliction))
                .addAlternateAttack(BurnAttack(3)),

            FightingCard("Earth Mystic", 3, 1, 1, player = player,
                inflictions = listOf(lowerBeingTypeInfliction))
                .addAlternateAttack(DefensiveMove()),
            FightingCard("Earth Mystic", 3, 1, 1, player = player,
                inflictions = listOf(lowerBeingTypeInfliction))
                .addAlternateAttack(DefensiveMove()),
            FightingCard("Terrance - Earth Adept", 5, 2, 1, player = player)
                .addAlternateAttack(DefensiveMove(2)),

            FightingCard("Water Mystic", 4, 1, 1, player = player,
                inflictions = listOf(lowerBeingTypeInfliction, platedArmorInfliction)),
            FightingCard("Water Mystic", 4, 1, 1, player = player,
                inflictions = listOf(lowerBeingTypeInfliction, platedArmorInfliction)),
            FightingCard("Vortex - Water Adept", 5, 2, 2, player = player,
                inflictions = listOf(platedArmorInfliction)),

            FightingCard("Wind Mystic", 3, 1, 1, player = player,
                inflictions = listOf(lowerBeingTypeInfliction, noRetaliationInfliction))
                .addAlternateAttack(WindAttack()),
            FightingCard("Wind Mystic", 3, 1, 1, player = player,
                inflictions = listOf(lowerBeingTypeInfliction, noRetaliationInfliction))
                .addAlternateAttack(WindAttack()),
            FightingCard("Zepherstorm - Wind Adept", 5, 2, 2, player = player,
                inflictions = listOf(noRetaliationInfliction))
                .addAlternateAttack(WindAttack()),

            createSeekOutCard(),
            createSeekOutCard(),
            createSeekOutCard(),
        )
    }

    private fun createSeekOutCard() = OneShotCard("Seek out", 3) {
        val deck = it.deck.getComponent(DeckComponent::class.java)
        val chosenName = it.clientChoice("card to seek out", deck.cardNames()) { name ->
            name
        }
        val card = deck.seekOut(chosenName)
        it.addCard(card)
        Main.sendMessageToAll(DrawCardMessage(it.id))
        deck.shuffle()
        return@OneShotCard CardPlayResultLocation.DISCARD
    }
}