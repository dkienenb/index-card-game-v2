package org.gamenet.dkienenb.indexv2.server.combat

import org.gamenet.dkienenb.component.Component
import org.gamenet.dkienenb.component.ComponentedObject
import org.gamenet.dkienenb.indexv2.server.card.NameComponent
import org.gamenet.dkienenb.indexv2.server.card.PurchasableComponent
import org.gamenet.dkienenb.indexv2.server.card.ReactiveComponent

class TargetComponent : ReactiveComponent<Int>() {

    fun addOnAttackedEffect(onAttackEffect: (attacker: ComponentedObject, damage: Int) -> Unit) {
        addEffect(onAttackEffect)
    }

    fun receiveAttack(
        attacker: ComponentedObject,
        damage: Int,
        ignoreDefense: Boolean,
        hasRetaliation: Boolean,
    ) {
        val actualDamage: Int = if (ignoreDefense) {
            damage
        } else {
            damage - attached.getComponent(DefenseComponent::class.java).getDefense()
        }
        val reasonableDamage = if (actualDamage < 0) {0} else {actualDamage}
        attached.getComponent(HealthComponent::class.java).changeHealth(-reasonableDamage)
        if (hasRetaliation) {
            if (attached.getComponent(MortalComponent::class.java).isLiving()) {
                if (attached.hasComponent(AttackerComponent::class.java)) {
                    if (attacker.hasComponent(TargetComponent::class.java)) {
                        attached.getComponent(AttackerComponent::class.java).attack(
                            attacker,
                            false,
                        )
                    }
                }
            }
        }
        trigger(damage)
    }

    override fun getDependencies(): MutableList<Class<out Component>> {
        val list = super.getDependencies()
        list.add(HealthComponent::class.java)
        list.add(DefenseComponent::class.java)
        list.add(PurchasableComponent::class.java)
        list.add(NameComponent::class.java)
        return list
    }

}