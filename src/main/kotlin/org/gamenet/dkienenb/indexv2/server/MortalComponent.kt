package org.gamenet.dkienenb.indexv2.server

import org.gamenet.dkienenb.component.ComponentedObject
import org.gamenet.dkienenb.component.ListStoringComponent

class MortalComponent : ListStoringComponent<(ComponentedObject) -> Unit>() {

    private var living: Boolean = true

    fun addDeathEffect(deathEffect: (ComponentedObject) -> Unit) {
        value.add(deathEffect)
    }

    fun die() {
        if (living) {
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