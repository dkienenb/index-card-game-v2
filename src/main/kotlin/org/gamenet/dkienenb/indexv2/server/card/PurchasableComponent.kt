package org.gamenet.dkienenb.indexv2.server.card

import org.gamenet.dkienenb.indexv2.server.ModifiedIntComponent

public open class PurchasableComponent(cost: Int) : ModifiedIntComponent(cost) {

    fun getCost(): Int = getValue()
    fun setCost(cost: Int) = setValue(cost)
}