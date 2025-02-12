package org.gamenet.indexv2

import org.gamenet.dkienenb.component.MutableDataStoringComponent

class MinHealthComponent : MutableDataStoringComponent<Int>() {
    init {
        super.setValue(0)
    }

    fun setMinHealth(health: Int) {
        super.setValue(health)
    }

    fun getMinHealth(): Int {
        return super.getValue()
    }
}