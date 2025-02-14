package org.gamenet.dkienenb.indexv2.server

import org.gamenet.dkienenb.component.Component
import org.gamenet.dkienenb.component.MutableDataStoringComponent

class AttackerComponent(damage: Int) : MutableDataStoringComponent<Int>() {
    init {
        value = damage
    }

    fun getDamage() = getValue()

    fun setDamage(damage: Int) {
        setValue(damage)
    }

    override fun getDependencies(): MutableList<Class<out Component>> {
        val list = super.getDependencies()
        list.add(PlayerOwnedComponent::class.java)
        return list
    }
}