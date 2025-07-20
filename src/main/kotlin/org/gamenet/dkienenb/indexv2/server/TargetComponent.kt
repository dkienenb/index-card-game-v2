package org.gamenet.dkienenb.indexv2.server

import org.gamenet.dkienenb.component.Component
import org.gamenet.dkienenb.component.ComponentedObject
import org.gamenet.dkienenb.component.ListStoringComponent

class TargetComponent : ListStoringComponent<(ComponentedObject, Int) -> Unit>() {

    fun addOnAttackEffect(onAttackEffect: (attacker: ComponentedObject, damage: Int) -> Unit) {
        super.getValue().add(onAttackEffect)
    }

    fun receiveAttack(
        attacker: ComponentedObject,
        damage: Int,
        ignoreDefense: Boolean,
        hasRetaliation: Boolean,
        attackingPlayer: Player,
        defendingPlayer: Player
    ) {
        super.stream().forEach {
            it(attacker, damage)
        }
        val actualDamage: Int = if (ignoreDefense) {
            damage
        } else {
            damage - attached.getComponent(DefenseComponent::class.java).getDefense()
        }
        Main.sendAllExcept(
            "${attached.getComponent(NameComponent::class.java).getName()} took $actualDamage damage.",
            null
        )
        attached.getComponent(HealthComponent::class.java).changeHealth(-actualDamage)
        if (hasRetaliation) {
            if (attached.getComponent(MortalComponent::class.java).isLiving()) {
                if (attached.hasComponent(AttackerComponent::class.java)) {
                    if (attacker.hasComponent(TargetComponent::class.java)) {
                        attached.getComponent(AttackerComponent::class.java).attack(
                            attacker,
                            false,
                            owner = defendingPlayer,
                            attackedPlayer = attackingPlayer
                        )
                    }
                }
            }
        }
    }

    override fun getDependencies(): MutableList<Class<out Component>> {
        val list = super.getDependencies()
        list.add(HealthComponent::class.java)
        list.add(DefenseComponent::class.java)
        list.add(PurchasableComponent::class.java)
        list.add(TagComponent::class.java)
        list.add(NameComponent::class.java)
        return list
    }

}