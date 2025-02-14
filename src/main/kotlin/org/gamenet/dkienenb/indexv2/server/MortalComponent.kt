package org.gamenet.dkienenb.indexv2.server

import org.gamenet.dkienenb.component.ComponentedObject
import org.gamenet.dkienenb.component.ListStoringComponent

class MortalComponent(private var living: Boolean = true) : ListStoringComponent<(ComponentedObject) -> Unit>() {

    fun addDeathEffect(deathEffect: (ComponentedObject) -> Unit) {
        super.getValue().add(deathEffect)
    }

    fun die() {
        if (living) {
            super.stream().forEach { consumer ->
                consumer.invoke(attached)
            }
            living = false
        }
    }

    fun isLiving(): Boolean {
        return living
    }
}