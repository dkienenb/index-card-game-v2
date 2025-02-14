package org.gamenet.dkienenb.indexv2.server

import org.gamenet.dkienenb.component.MutableDataStoringComponent

open class PurchasableComponent(cost: Int) : MutableDataStoringComponent<Int>() {
    init {
        super.setValue(cost)
    }

    fun getCost(): Int = super.getValue()

    fun setCost(cost: Int) {
        super.setValue(cost)
    }
}