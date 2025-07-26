package org.gamenet.dkienenb.indexv2.server.status

import org.gamenet.dkienenb.component.ComponentedObject

class StatusEffectInstance(
    val effect: StatusEffect,
    var duration: Int,
    val victim: ComponentedObject,
    val inflictor: ComponentedObject?
) {
    fun active() = duration == 0

    fun apply() {
        effect.onApply(duration, victim, inflictor)
    }

    fun tick() {
        effect.onTick(duration, victim, inflictor)
        duration = effect.tickChangeDuration(duration)
        if (!active()) {
            effect.onExpire(duration, victim, inflictor)
        }
    }
}

enum class StatusEffectCategory {
    BUFF,
    DEBUFF,
    INNATE
}

abstract class StatusEffect(val category: StatusEffectCategory, val name: String) {
    open fun onApply(currentDuration: Int, victim: ComponentedObject, inflictor: ComponentedObject?) = Unit
    open fun onExpire(currentDuration: Int, victim: ComponentedObject, inflictor: ComponentedObject?) = Unit
    abstract fun onTick(currentDuration: Int, victim: ComponentedObject, inflictor: ComponentedObject?)
    open fun tickChangeDuration(currentDuration: Int): Int = currentDuration - 1
}