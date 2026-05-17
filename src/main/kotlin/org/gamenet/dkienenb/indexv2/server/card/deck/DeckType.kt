package org.gamenet.dkienenb.indexv2.server.card.deck

import org.gamenet.dkienenb.indexv2.server.Player
import org.gamenet.dkienenb.indexv2.server.card.Card
import org.gamenet.dkienenb.indexv2.server.card.FightingCard
import org.gamenet.dkienenb.indexv2.server.combat.AlternativeAttack
import org.gamenet.dkienenb.indexv2.server.combat.AlternativeAttacksComponent
import org.gamenet.dkienenb.indexv2.server.status.StatusEffect
import org.gamenet.dkienenb.indexv2.server.status.StatusEffectInfliction
import org.gamenet.dkienenb.indexv2.server.status.StatusEffectInflictionAllyState
import org.gamenet.dkienenb.indexv2.server.status.StatusEffectInflictionScope
import org.gamenet.dkienenb.indexv2.server.status.StatusEffectInflictionTiming

abstract class DeckType(val name: String) {
    companion object {
        val registry = listOf<DeckType>(
            SewerDeckType(),
            CastleDeckType(),
            MysticDeckType(),
        )
    }

    abstract fun getInitialCardList(player: Player): List<Card>

    protected fun generateSelfInfliction(potency: Int, effect: StatusEffect, duration: Int = Int.MAX_VALUE) = StatusEffectInfliction(
        StatusEffectInflictionTiming.ON_PLAY,
        StatusEffectInflictionScope.SELF, StatusEffectInflictionAllyState.FRIENDLY,
        duration, potency, effect, emptyList()
    )

    protected fun generateOnKilledInfliction(duration: Int, potency: Int, effect: StatusEffect) = StatusEffectInfliction(
        StatusEffectInflictionTiming.ON_DEATH,
        StatusEffectInflictionScope.TARGET, StatusEffectInflictionAllyState.HOSTILE,
        duration, potency, effect, emptyList()
    )

    protected fun generateCreatureBuffInflictions(duration: Int, potency: Int, effect: StatusEffect, prereq: StatusEffect,
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

    protected fun FightingCard.addAlternateAttack(newAttack: AlternativeAttack): FightingCard {
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