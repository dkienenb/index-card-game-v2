package org.gamenet.dkienenb.indexv2.server.card

import org.gamenet.dkienenb.component.ImmutableDataStoringComponent

class NameComponent(value: String) : ImmutableDataStoringComponent<String>(value) {
    fun getName(): String = value
}