package org.gamenet.dkienenb.indexv2.server.status

import org.gamenet.dkienenb.component.ComponentedObject
import org.gamenet.dkienenb.indexv2.client.message.StatusEffectInflictedMessage
import org.gamenet.dkienenb.indexv2.server.Main
import org.gamenet.dkienenb.indexv2.server.card.CardIdComponent

class StatusEffectInstance(
    val effect: StatusEffect,
    private val potency: Int,
    var duration: Int,
    private val victim: ComponentedObject,
    private val inflictor: ComponentedObject?
) {
    private fun active() = duration == 0

    fun apply() {
        effect.onApply(potency, duration, victim, inflictor)
    }

    fun tick() {
        val oldDuration = duration
        effect.onTick(potency, oldDuration, victim, inflictor)
        duration = effect.tickChangeDuration(potency, oldDuration)
        if (!active()) {
            effect.onExpire(potency, victim, inflictor)
        }
        if (oldDuration != duration) {
            val id = victim.getComponent(CardIdComponent::class.java).getId()
            Main.sendMessageToAll(StatusEffectInflictedMessage(id, effect.name, duration, potency,false))
        }
    }
}

enum class StatusEffectCategory {
    BUFF,
    DEBUFF,
    INNATE
}

abstract class StatusEffect(val category: StatusEffectCategory, val name: String, val combinesWithExistingStacks: Boolean) {

    open fun onApply(potency: Int, currentDuration: Int, victim: ComponentedObject, inflictor: ComponentedObject?) = Unit
    open fun onExpire(potency: Int, victim: ComponentedObject, inflictor: ComponentedObject?) = Unit
    abstract fun onTick(potency: Int, currentDuration: Int, victim: ComponentedObject, inflictor: ComponentedObject?)
    open fun tickChangeDuration(potency: Int, currentDuration: Int): Int = if (currentDuration != Int.MAX_VALUE) {
        currentDuration - 1
    } else {
        Int.MAX_VALUE
    }
}

abstract class MarkerStatusEffect(category: StatusEffectCategory, name: String) : StatusEffect(category, name, false) {
    override fun onTick(potency: Int, currentDuration: Int, victim: ComponentedObject, inflictor: ComponentedObject?) = Unit
}