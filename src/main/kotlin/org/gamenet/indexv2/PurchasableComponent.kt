package org.gamenet.indexv2

import org.gamenet.dkienenb.component.MutableDataStoringComponent

open class PurchasableComponent(cost: Int) : MutableDataStoringComponent<Int>() {
    init {
        super.setValue(cost)
    }

    fun getCost(): Int {
        return super.getValue()
    }

    fun setCost(cost: Int) {
        super.setValue(cost)
    }
}