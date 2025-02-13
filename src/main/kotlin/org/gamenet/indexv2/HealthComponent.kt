package org.gamenet.indexv2

import org.gamenet.dkienenb.component.Component
import org.gamenet.dkienenb.component.ComponentedObject
import org.gamenet.dkienenb.component.MutableDataStoringComponent

class HealthComponent : MutableDataStoringComponent<Int>() {
    override fun onAdd() {
        setHealth(attached!!.getComponent(MaxHealthComponent::class.java).getMaxHealth())
    }

    fun getHealth(): Int {
        return super.getValue()
    }

    fun setHealth(health: Int) {
        var newHealth = health
        val minHealth = attached.getComponent(MinHealthComponent::class.java).getMinHealth()
        if (health < minHealth) {
            attached.getComponent(MortalComponent::class.java).die()
            newHealth = minHealth
        }
        val maxHealth = attached.getComponent(MaxHealthComponent::class.java).getMaxHealth()
        if (health > maxHealth) {
            newHealth = maxHealth
        }
        super.setValue(newHealth)
    }

    fun changeHealth(change: Int) {
        setHealth(getHealth() + change)
    }

    override fun getDependencies(): MutableList<Class<out Component>> {
        val list = super.getDependencies()
        list.add(MinHealthComponent::class.java)
        list.add(MaxHealthComponent::class.java)
        list.add(TargetComponent::class.java)
        return list
    }
}