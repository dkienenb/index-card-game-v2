package org.gamenet.dkienenb.indexv2.server.status

import org.gamenet.dkienenb.component.ComponentedObject
import org.gamenet.dkienenb.component.ListStoringComponent
import org.gamenet.dkienenb.indexv2.client.message.StatusEffectInflictedMessage
import org.gamenet.dkienenb.indexv2.server.Main
import org.gamenet.dkienenb.indexv2.server.card.CardIdComponent

public class StatusEffectComponent : ListStoringComponent<StatusEffectInstance>() {

    fun applyStatusEffect(potency: Int, duration: Int, statusEffect: StatusEffect, inflictor: ComponentedObject?) {
        val id = attached.getComponent(CardIdComponent::class.java).getId()
        if (statusEffect.combineStackDurations && has(statusEffect, potency)) {
            val instance = value.first { it.effect == statusEffect && it.potency == potency }
            instance.duration = instance.duration + duration
            instance.apply()
            Main.sendMessageToAll(StatusEffectInflictedMessage(id, statusEffect.name, duration, potency))
        } else {
            val instance = StatusEffectInstance(statusEffect, potency, duration, attached, inflictor)
            value.add(instance)
            instance.apply()
            Main.sendMessageToAll(StatusEffectInflictedMessage(id, statusEffect.name, duration, potency))
        }
    }

    fun tick() {
        value.removeAll {
            it.duration <= 0
        }
        value.forEach(StatusEffectInstance::tick)
    }
    fun hasAll(prerequisiteEffects: List<StatusEffect>): Boolean {
        prerequisiteEffects.forEach { effect ->
            if (!has(effect)) {
                return@hasAll false
            }
        }
        return true
    }

    fun has(effect: StatusEffect) = value.any { it.effect == effect }
    fun has(effect: StatusEffect, potency: Int) = value.any { it.effect == effect && it.potency == potency }
    fun clearAllStatuses() = value.clear()

}