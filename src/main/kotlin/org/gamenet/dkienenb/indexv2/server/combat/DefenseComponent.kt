package org.gamenet.dkienenb.indexv2.server.combat

import org.gamenet.dkienenb.indexv2.server.ModifiedIntComponent

public class DefenseComponent(defense: Int) : ModifiedIntComponent(defense) {

    fun getDefense(): Int = getValue()

}