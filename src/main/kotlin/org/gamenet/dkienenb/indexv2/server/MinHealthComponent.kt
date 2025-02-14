package org.gamenet.dkienenb.indexv2.server

import org.gamenet.dkienenb.component.MutableDataStoringComponent

class MinHealthComponent : MutableDataStoringComponent<Int>() {
    init {
        setValue(0)
    }

    fun setMinHealth(health: Int) {
        setValue(health)
    }

    fun getMinHealth(): Int = super.getValue()
}