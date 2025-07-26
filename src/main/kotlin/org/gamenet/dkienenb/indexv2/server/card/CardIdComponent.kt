package org.gamenet.dkienenb.indexv2.server.card

import org.gamenet.dkienenb.component.ImmutableDataStoringComponent

class CardIdComponent : ImmutableDataStoringComponent<Int>(generateId()) {
    fun getId(): Int = value

    companion object {
        var currentId: Int = 0
        fun generateId(): Int = ++currentId
    }
}