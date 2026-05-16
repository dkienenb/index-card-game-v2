package org.gamenet.dkienenb.indexv2.server.combat

import org.gamenet.dkienenb.indexv2.server.ModifiedIntComponent

public class MinHealthComponent : ModifiedIntComponent(0) {

    fun getMinHealth(): Int = getValue()
    fun setMinHealth(value: Int) = setValue(value)

}