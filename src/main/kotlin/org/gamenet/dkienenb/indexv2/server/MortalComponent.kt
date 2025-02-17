package org.gamenet.dkienenb.indexv2.server

import org.gamenet.dkienenb.component.ComponentedObject
import org.gamenet.dkienenb.component.ListStoringComponent

class MortalComponent : ListStoringComponent<(ComponentedObject) -> Unit>() {

    private val taggedEffects = mutableListOf<(ComponentedObject, ComponentedObject?) -> Unit>()
    private var living: Boolean = true
    var combatTag: ComponentedObject? = null

    fun addDeathEffect(deathEffect: (ComponentedObject) -> Unit) {
        value.add(deathEffect)
    }

    fun addTaggedDeathEffect(deathEffect: (ComponentedObject, ComponentedObject?) -> Unit) {
        taggedEffects.add(deathEffect)
    }

    fun die() {
        if (living) {
            taggedEffects.stream().forEach { consumer ->
                consumer.invoke(attached, combatTag)
            }
            stream().forEach { consumer ->
                consumer.invoke(attached)
            }
            living = false
        }
    }

    fun revive() {
        living = true
    }

    fun clearOnDeathEffects() {
        value.clear()
    }

    fun isLiving(): Boolean = living
}