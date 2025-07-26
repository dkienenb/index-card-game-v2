package org.gamenet.dkienenb.indexv2.server.status

import org.gamenet.dkienenb.component.ComponentedObject
import org.gamenet.dkienenb.component.ListStoringComponent

class StatusEffectComponent : ListStoringComponent<StatusEffectInstance>() {

    fun applyStatusEffect(duration: Int, statusEffect: StatusEffect, inflictor: ComponentedObject?) {
        // TODO stackable effects
        val instance = StatusEffectInstance(statusEffect, duration, attached, inflictor)
        value.add(instance)
        instance.apply()
    }

    fun tick() = stream().forEach { it.tick() }
    fun hasAll(prerequisiteEffects: List<StatusEffect>): Boolean {
        prerequisiteEffects.forEach { effect ->
            if (!has(effect)) {
                return false
            }
        }
        return true
    }

    fun has(effect: StatusEffect) = value.any { it.effect == effect }

}