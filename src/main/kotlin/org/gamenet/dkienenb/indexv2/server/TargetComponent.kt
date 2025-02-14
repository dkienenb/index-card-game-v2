package org.gamenet.dkienenb.indexv2.server

import org.gamenet.dkienenb.component.Component
import org.gamenet.dkienenb.component.ComponentedObject
import org.gamenet.dkienenb.component.ListStoringComponent

class TargetComponent : ListStoringComponent<(ComponentedObject, Int) -> Unit>() {

    fun addOnAttackEffect(onAttackEffect: (attacker: ComponentedObject, damage: Int) -> Unit) {
        super.getValue().add(onAttackEffect)  
    }

    fun attack(attacker: ComponentedObject, damage: Int) {
        super.stream().forEach {
            it(attacker, damage)
        }
        TODO("attacker parameter, reduce hp, min damage, account for defense, retaliation, damage types (such as pierce)")
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