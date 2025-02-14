package org.gamenet.dkienenb.indexv2.server

import org.gamenet.dkienenb.component.MutableDataStoringComponent

class MaxHealthComponent(maxHealth: Int) : MutableDataStoringComponent<Int>() {
    init {
        setMaxHealth(maxHealth)
    }

    fun getMaxHealth(): Int = super.getValue()

    fun setMaxHealth(maxHealth: Int) {
        value = maxHealth
    }
}