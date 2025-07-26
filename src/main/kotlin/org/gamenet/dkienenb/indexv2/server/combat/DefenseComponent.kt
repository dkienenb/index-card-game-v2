package org.gamenet.dkienenb.indexv2.server.combat

import org.gamenet.dkienenb.indexv2.server.ModifiedIntComponent

class DefenseComponent(defense: Int) : ModifiedIntComponent(defense) {

    fun getDefense(): Int = value

    fun setDefense(defense: Int) {
        value = defense
    }
}