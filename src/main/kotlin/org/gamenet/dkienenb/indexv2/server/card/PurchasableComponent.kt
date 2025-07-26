package org.gamenet.dkienenb.indexv2.server.card

import org.gamenet.dkienenb.indexv2.server.ModifiedIntComponent

open class PurchasableComponent(cost: Int) : ModifiedIntComponent(cost) {

    fun getCost(): Int = value

    fun setCost(cost: Int) {
        value = cost
    }
}