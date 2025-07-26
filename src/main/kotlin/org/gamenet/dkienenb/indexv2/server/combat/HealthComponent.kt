package org.gamenet.dkienenb.indexv2.server.combat

import org.gamenet.dkienenb.component.Component
import org.gamenet.dkienenb.indexv2.client.message.CardHealthChangedMessage
import org.gamenet.dkienenb.indexv2.server.Main
import org.gamenet.dkienenb.indexv2.server.ModifiedIntComponent
import org.gamenet.dkienenb.indexv2.server.card.CardIdComponent

class HealthComponent : ModifiedIntComponent(0) {
    override fun onAdd() {
        setHealth(attached.getComponent(MaxHealthComponent::class.java).getMaxHealth())
    }

    fun getHealth(): Int = value

    fun setHealth(health: Int) {
        var newHealth = health
        val minHealth = attached.getComponent(MinHealthComponent::class.java).getMinHealth()
        if (health <= minHealth) {
            attached.getComponent(MortalComponent::class.java).die()
            newHealth = minHealth
        }
        val maxHealth = attached.getComponent(MaxHealthComponent::class.java).getMaxHealth()
        if (health > maxHealth) {
            newHealth = maxHealth
        }
        if (attached.hasComponent(CardIdComponent::class.java)) {
            Main.sendMessageToAll(
                CardHealthChangedMessage(
                    newHealth,
                    attached.getComponent(CardIdComponent::class.java).getId()
                )
            )
        }
        value = newHealth
    }

    fun changeHealth(change: Int) {
        setHealth(getHealth() + change)
    }

    override fun getDependencies(): MutableList<Class<out Component>> {
        val list = super.getDependencies()
        list.add(MinHealthComponent::class.java)
        list.add(MaxHealthComponent::class.java)
        list.add(MortalComponent::class.java)
        return list
    }
}