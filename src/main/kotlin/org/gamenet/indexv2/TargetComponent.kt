package org.gamenet.indexv2

import org.gamenet.dkienenb.component.Component
import org.gamenet.dkienenb.component.ComponentedObject
import org.gamenet.dkienenb.component.ListStoringComponent

class TargetComponent : ListStoringComponent<(ComponentedObject) -> Unit>() {

    fun addOnAttackEffect(onAttackEffect: (ComponentedObject) -> Unit) {
        super.getValue().add(onAttackEffect)
    }

    fun attack() {
        super.stream().forEach { consumer ->
            consumer.invoke(attached)
        }
        TODO("attacker parameter, reduce hp, min damage, account for defense, retaliation, damage types (such as pierce)")
    }

    override fun getDependencies(): MutableList<Class<out Component>> {
        val list = super.getDependencies()
        list.add(HealthComponent::class.java)
        list.add(DefenseComponent::class.java)
        list.add(PurchasableComponent::class.java)
        list.add(TagComponent::class.java)
        return list
    }
}