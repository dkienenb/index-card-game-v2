package org.gamenet.dkienenb.indexv2.server.card.deck

import org.gamenet.dkienenb.indexv2.client.message.DrawCardMessage
import org.gamenet.dkienenb.indexv2.server.Main
import org.gamenet.dkienenb.indexv2.server.Player
import org.gamenet.dkienenb.indexv2.server.card.Card
import org.gamenet.dkienenb.indexv2.server.card.CardPlayResultLocation
import org.gamenet.dkienenb.indexv2.server.card.FightingCard
import org.gamenet.dkienenb.indexv2.server.card.OneShotCard
import org.gamenet.dkienenb.indexv2.server.combat.*
import org.gamenet.dkienenb.indexv2.server.status.*

enum class DeckType(val typeName: String) {
    SEWERS("Sewers"),
    CASTLE("Castle"),
    MYSTIC("Mystic");

    fun initialCardList(player: Player): List<Card> {
        val ratTypeInfliction = generateSelfInfliction(1, CreatureTypes.RAT)
        val slimeTypeInfliction = generateSelfInfliction(1, CreatureTypes.SLIME)
        val wallTypeInfliction = generateSelfInfliction(1, CreatureTypes.WALL)
        val lowerBeingTypeInfliction = generateSelfInfliction(1, CreatureTypes.LOWER_BEING)

        val wallInfliction = generateSelfInfliction(1, StatusEffects.WALL)
        val armorPiercingInfliction = generateSelfInfliction(1, StatusEffects.PIERCING)
        val noRetaliationInfliction = generateSelfInfliction(1, StatusEffects.NO_RETALIATION)
        val flyingInfliction = generateSelfInfliction(1, StatusEffects.FLYING)
        val platedArmorInfliction = generateSelfInfliction(1, StatusEffects.PLATED_ARMOR)

        val spikedRevengeInfliction = generateOnKilledInfliction(1, 1, StatusEffects.SPIKES)

        return when (this) {
            SEWERS -> listOf(
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

            CASTLE -> listOf(
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

            MYSTIC -> listOf(
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

    private fun generateSelfInfliction(
        potency: Int,
        effect: StatusEffect,
        duration: Int = Int.MAX_VALUE,
        ) = StatusEffectInfliction(
        StatusEffectInflictionTiming.ON_PLAY,
        StatusEffectInflictionScope.SELF, StatusEffectInflictionAllyState.FRIENDLY,
        duration, potency, effect, emptyList()
    )

    private fun generateOnKilledInfliction(
        duration: Int,
        potency: Int,
        effect: StatusEffect
    ) = StatusEffectInfliction(
        StatusEffectInflictionTiming.ON_DEATH,
        StatusEffectInflictionScope.TARGET, StatusEffectInflictionAllyState.HOSTILE,
        duration, potency, effect, emptyList()
    )

    private fun generateCreatureBuffInflictions(
        duration: Int,
        potency: Int,
        effect: StatusEffect,
        prereq: StatusEffect,
        scope: StatusEffectInflictionScope = StatusEffectInflictionScope.DEPLOYED_CARDS,
        timingForNew: StatusEffectInflictionTiming = StatusEffectInflictionTiming.ON_ALIVE_CARD_PLAY,
    ): List<StatusEffectInfliction> {
        return listOf(
            StatusEffectInfliction(StatusEffectInflictionTiming.ON_PLAY, scope, StatusEffectInflictionAllyState.FRIENDLY,
                duration, potency, effect, listOf(prereq)),
            StatusEffectInfliction(timingForNew, StatusEffectInflictionScope.TARGET, StatusEffectInflictionAllyState.FRIENDLY,
                duration, potency, effect, listOf(prereq))
        )
    }

    private fun FightingCard.addAlternateAttack(newAttack: AlternativeAttack): FightingCard {
        val alternativeAttacksComponent = if (hasComponent(AlternativeAttacksComponent::class.java)) {
            getComponent(AlternativeAttacksComponent::class.java)
        } else {
            val newComponent = AlternativeAttacksComponent()
            addComponent(newComponent)
            newComponent
        }
        alternativeAttacksComponent.addAlternativeAttack(newAttack)
        return this
    }
}
