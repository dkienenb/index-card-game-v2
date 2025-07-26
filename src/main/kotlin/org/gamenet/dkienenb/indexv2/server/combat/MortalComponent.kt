package org.gamenet.dkienenb.indexv2.server.combat

import org.gamenet.dkienenb.component.ComponentedObject
import org.gamenet.dkienenb.indexv2.server.card.ReactiveComponent

class MortalComponent : ReactiveComponent<Unit>() {

    private val taggedEffects = mutableListOf<(ComponentedObject, ComponentedObject?) -> Unit>()
    private var living: Boolean = true
    var combatTag: ComponentedObject? = null

    fun addDeathEffect(deathEffect: (ComponentedObject, Unit) -> Unit) {
        addEffect(deathEffect)
    }

    fun addTaggedDeathEffect(deathEffect: (ComponentedObject, ComponentedObject?) -> Unit) {
        taggedEffects.add(deathEffect)
    }

    fun die() {
        if (living) {
            taggedEffects.stream().forEach { consumer ->
                consumer.invoke(attached, combatTag)
            }
            trigger(Unit)
            living = false
        }
    }

    fun revive() {
        living = true
    }

    fun clearOnDeathEffects() {
        clearEffects()
    }

    fun isLiving(): Boolean = living
}