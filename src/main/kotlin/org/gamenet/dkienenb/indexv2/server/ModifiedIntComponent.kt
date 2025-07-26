package org.gamenet.dkienenb.indexv2.server

import org.gamenet.dkienenb.component.MutableDataStoringComponent

abstract class ModifiedIntComponent(value: Int) : MutableDataStoringComponent<Int>() {

    private val modifiers = mutableListOf<Modifier>()

    init {
        setValue(value)
    }

    // end of the line, gumbo
    final override fun setValue(v: Int) {
        super.setValue(v)
    }

    fun addModifier(modifier: Modifier) {
        modifiers.add(modifier)
    }

    final override fun getValue(): Int {
        var current = value
        modifiers.forEach {
            if (it.active()) {
                current = it.modify(current)
            }
        }
        modifiers.removeIf { !it.stillValid() }
        return current
    }
}

abstract class Modifier {
    abstract fun stillValid(): Boolean
    abstract fun active(): Boolean
    abstract fun modify(current: Int): Int
}