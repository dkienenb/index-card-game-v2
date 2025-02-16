package org.gamenet.dkienenb.indexv2.server

import org.gamenet.dkienenb.component.Component
import org.gamenet.dkienenb.component.ComponentedObject
import org.gamenet.dkienenb.component.MutableDataStoringComponent

class AttackerComponent(damage: Int, var ranged: Boolean) : MutableDataStoringComponent<Int>() {
    init {
        value = damage
    }

    fun getDamage(): Int = getValue()

    fun setDamage(damage: Int) {
        setValue(damage)
    }

    override fun getDependencies(): MutableList<Class<out Component>> {
        val list = super.getDependencies()
        list.add(OriginalPlayerOwnedComponent::class.java)
        return list
    }

    fun attack(target: ComponentedObject, hasRetaliation: Boolean, owner: Player, attackedPlayer: Player) {
        Main.sendAllExcept("${owner.client.getName()}'s ${attached.getComponent(NameComponent::class.java).getName()} attacked" +
                " ${attackedPlayer.client.getName()}'s ${target.getComponent(NameComponent::class.java).getName()}.", null)
        target.getComponent(TargetComponent::class.java).attack(attached, getDamage(), attached.hasComponent(AttacksIgnoreDefenseComponent::class.java), hasRetaliation, owner, attackedPlayer)
    }

}