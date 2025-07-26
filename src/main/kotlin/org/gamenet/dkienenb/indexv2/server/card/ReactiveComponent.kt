package org.gamenet.dkienenb.indexv2.server.card

import org.gamenet.dkienenb.component.ComponentedObject
import org.gamenet.dkienenb.component.ListStoringComponent

abstract class ReactiveComponent<T> : ListStoringComponent<(ComponentedObject, T) -> Unit>() {

    protected fun addEffect(effect: (ComponentedObject, T) -> Unit) {
        value.add(effect)
    }

    protected fun trigger(trigger: T) {
        stream().forEach { consumer ->
            consumer.invoke(attached, trigger)
        }
    }

    protected fun clearEffects() {
        value.clear()
    }
}