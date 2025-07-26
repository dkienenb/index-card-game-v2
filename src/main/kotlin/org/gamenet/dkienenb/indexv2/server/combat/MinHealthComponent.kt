package org.gamenet.dkienenb.indexv2.server.combat

import org.gamenet.dkienenb.indexv2.server.ModifiedIntComponent

class MinHealthComponent : ModifiedIntComponent(0) {

    fun setMinHealth(health: Int) {
        value = health
    }

    fun getMinHealth(): Int = value
}