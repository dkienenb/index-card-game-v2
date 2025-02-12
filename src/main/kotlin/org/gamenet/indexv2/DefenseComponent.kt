package org.gamenet.indexv2

import org.gamenet.dkienenb.component.MutableDataStoringComponent

class DefenseComponent : MutableDataStoringComponent<Int>() {
    init {
        setDefense(0)
    }

    fun getDefense(): Int {
        return getValue()
    }

    fun setDefense(defense: Int) {
        setValue(defense)
    }
}