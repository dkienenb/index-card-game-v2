package org.gamenet.indexv2

import org.gamenet.dkienenb.component.MutableDataStoringComponent

class MaxHealthComponent(maxHealth: Int) : MutableDataStoringComponent<Int>() {
    init {
        setMaxHealth(maxHealth)
    }

    fun getMaxHealth(): Int {
        return super.getValue()
    }

    fun setMaxHealth(maxHealth: Int) {
        super.setValue(maxHealth)
    }
}