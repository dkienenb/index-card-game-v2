package org.gamenet.dkienenb.indexv2.server.combat

import org.gamenet.dkienenb.indexv2.server.ModifiedIntComponent

class MaxHealthComponent(maxHealth: Int) : ModifiedIntComponent(maxHealth) {

    fun getMaxHealth(): Int = value

    fun setMaxHealth(maxHealth: Int) {
        value = maxHealth
    }
}