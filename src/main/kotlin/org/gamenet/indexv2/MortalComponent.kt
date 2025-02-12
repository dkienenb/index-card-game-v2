package org.gamenet.indexv2

import org.gamenet.dkienenb.component.ComponentedObject
import org.gamenet.dkienenb.component.ListStoringComponent

class MortalComponent : ListStoringComponent<(ComponentedObject) -> Unit>() {

    fun addDeathEffect(deathEffect: (ComponentedObject) -> Unit) {
        super.getValue().add(deathEffect)
    }

    fun die() {
        super.stream().forEach { consumer ->
            consumer.invoke(attached)
        }
    }
}