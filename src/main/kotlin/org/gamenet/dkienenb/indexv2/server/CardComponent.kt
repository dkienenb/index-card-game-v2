package org.gamenet.dkienenb.indexv2.server

import org.gamenet.dkienenb.component.Component

class CardComponent : Component() {
    override fun getDependencies(): MutableList<Class<out Component>> {
        val list = super.getDependencies()
        list.add(NameComponent::class.java)
        list.add(PurchasableComponent::class.java)
        return list
    }
}