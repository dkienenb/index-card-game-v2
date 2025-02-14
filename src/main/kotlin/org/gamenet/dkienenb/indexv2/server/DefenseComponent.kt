package org.gamenet.dkienenb.indexv2.server

import org.gamenet.dkienenb.component.MutableDataStoringComponent

class DefenseComponent(defense: Int) : MutableDataStoringComponent<Int>() {
    init {
        setDefense(defense)
    }

    fun getDefense(): Int = getValue()

    fun setDefense(defense: Int) {
        setValue(defense)
    }
}