package org.gamenet.dkienenb.indexv2.server.status

import org.gamenet.dkienenb.component.ComponentedObject
import org.gamenet.dkienenb.component.ListStoringComponent
import org.gamenet.dkienenb.indexv2.server.Main
import org.gamenet.dkienenb.indexv2.server.Player
import org.gamenet.dkienenb.indexv2.server.card.OnPlayEffectsComponent
import org.gamenet.dkienenb.indexv2.server.card.PlayerOwnedComponent
import org.gamenet.dkienenb.indexv2.server.card.ReactsToCardDrawsComponent
import org.gamenet.dkienenb.indexv2.server.card.ReactsToCardPlaysComponent
import org.gamenet.dkienenb.indexv2.server.combat.AttackerComponent
import org.gamenet.dkienenb.indexv2.server.combat.MortalComponent
import org.gamenet.dkienenb.indexv2.server.combat.TargetComponent

enum class StatusEffectInflictionTiming {
    ON_PLAY, // target == self
    ON_ATTACKED, // target == attacker
    ON_ATTACK, // target == attacked
    ON_DEATH, // target == cause of death
    ON_ALIVE_CARD_PLAY, // target == new card played
    ON_ALIVE_DRAW_CARD, // target == drawn card
//    ON_ALIVE_TURN_START, // just create a status effect that inflicts another
}

enum class StatusEffectInflictionScope {
    DEPLOYED_CARDS,
    SELF,
    TARGET,
    HAND_CARDS,
}

enum class StatusEffectInflictionAllyState {
    ALL,
    FRIENDLY,
    HOSTILE,
}

class StatusEffectInfliction(
    val inflictionTiming: StatusEffectInflictionTiming,
    val inflictionScope: StatusEffectInflictionScope,
    val inflictionAllyState: StatusEffectInflictionAllyState,
    val duration: Int,
    val effect: StatusEffect,
    val prerequisiteEffects: List<StatusEffect>,
)

class StatusEffectInflictorComponent : ListStoringComponent<StatusEffectInfliction>() {
    fun addInfliction(infliction: StatusEffectInfliction) {
        checkNotNull(attached) { "Not attached yet! Attach inflictor component, then add inflictions." }
        value.add(infliction)
        addHooks(infliction) {
            Main.players
        }
    }

    private fun inflictScopedCheckingAllyStateAndPrerequisiteEffects(infliction: StatusEffectInfliction, target: ComponentedObject, allPlayersSupplier: () -> List<Player>) {
        val allPlayers = allPlayersSupplier()
        when (infliction.inflictionScope) {
            StatusEffectInflictionScope.TARGET -> {
                inflictCheckingAllyStateAndPrerequisiteEffects(target, infliction)
            }
            StatusEffectInflictionScope.DEPLOYED_CARDS -> {
                allPlayers.filter { !it.isOut() }.forEach { player ->
                    player.applyToAllDeployedCards {
                        inflictCheckingAllyStateAndPrerequisiteEffects(it, infliction)
                    }
                }
            }
            StatusEffectInflictionScope.SELF -> inflictCheckingAllyStateAndPrerequisiteEffects(attached, infliction)
            StatusEffectInflictionScope.HAND_CARDS -> {
                allPlayers.filter { !it.isOut() }.forEach { player ->
                    player.hand.forEach {
                        inflictCheckingAllyStateAndPrerequisiteEffects(it, infliction)
                    }
                }
            }
        }
    }

    private fun inflictCheckingAllyStateAndPrerequisiteEffects(
        target: ComponentedObject,
        infliction: StatusEffectInfliction
    ) {
        when (infliction.inflictionAllyState) {
            StatusEffectInflictionAllyState.ALL -> inflictCheckingPrerequisiteEffects(target, infliction)
            StatusEffectInflictionAllyState.HOSTILE -> {
                if (target.getComponent(PlayerOwnedComponent::class.java) != attached.getComponent(PlayerOwnedComponent::class.java)) {
                    inflictCheckingPrerequisiteEffects(target, infliction)
                }
            }
            StatusEffectInflictionAllyState.FRIENDLY -> {
                if (target.getComponent(PlayerOwnedComponent::class.java) == attached.getComponent(PlayerOwnedComponent::class.java)) {
                    inflictCheckingPrerequisiteEffects(target, infliction)
                }
            }
        }
    }

    private fun inflictCheckingPrerequisiteEffects(
        target: ComponentedObject,
        infliction: StatusEffectInfliction
    ) {
        if (target.hasComponent(StatusEffectComponent::class.java)) {
            if (target.getComponent(StatusEffectComponent::class.java).hasAll(infliction.prerequisiteEffects)) {
                target.getComponent(StatusEffectComponent::class.java)
                    .applyStatusEffect(infliction.duration, infliction.effect, attached)
            }
        }
    }

    private fun addHooks(infliction: StatusEffectInfliction, allPlayers: () -> List<Player>) {
        when (infliction.inflictionTiming) {
            StatusEffectInflictionTiming.ON_DEATH -> {
                attached.getComponent(MortalComponent::class.java).addTaggedDeathEffect { _, attacker ->
                    if (attacker != null) {
                        inflictScopedCheckingAllyStateAndPrerequisiteEffects(infliction, attacker, allPlayers)
                    }
                }
            }
            StatusEffectInflictionTiming.ON_PLAY -> {
                attached.getComponent(OnPlayEffectsComponent::class.java).addOnPlayEffect {victim, _, currentLocation ->
                    inflictScopedCheckingAllyStateAndPrerequisiteEffects(infliction, victim, allPlayers)
                    return@addOnPlayEffect currentLocation
                }
            }
            StatusEffectInflictionTiming.ON_ATTACKED -> {
                attached.getComponent(TargetComponent::class.java).addOnAttackedEffect { attacker, _ ->
                    inflictScopedCheckingAllyStateAndPrerequisiteEffects(infliction, attacker, allPlayers)
                }
            }
            StatusEffectInflictionTiming.ON_ATTACK -> {
                attached.getComponent(AttackerComponent::class.java).addOnAttackEffect { victim: ComponentedObject ->
                    inflictScopedCheckingAllyStateAndPrerequisiteEffects(infliction, victim, allPlayers)
                }
            }
            StatusEffectInflictionTiming.ON_ALIVE_CARD_PLAY -> {
                attached.getComponent(ReactsToCardPlaysComponent::class.java).addPlayReaction { _, card->
                    inflictScopedCheckingAllyStateAndPrerequisiteEffects(infliction, card, allPlayers)
                }
            }
            StatusEffectInflictionTiming.ON_ALIVE_DRAW_CARD -> {
                attached.getComponent(ReactsToCardDrawsComponent::class.java).addDrawReaction { _, card->
                    inflictScopedCheckingAllyStateAndPrerequisiteEffects(infliction, card, allPlayers)
                }
            }
        }
    }
}