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
        var current = super.getValue()
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

class ConstantBonusModifier(private val constant: Int) : Modifier() {
    override fun stillValid(): Boolean = true
    override fun active() = true
    override fun modify(current: Int) = (current + constant)
}

class OverrideValueModifier(val supplier: () -> Int) : Modifier() {
    override fun stillValid() = true
    override fun active() = true
    override fun modify(current: Int): Int = supplier()
}