package org.gamenet.dkienenb.indexv2.server.status

import org.gamenet.dkienenb.component.ComponentedObject

class StatusEffects {
    companion object {
        val FLYING = FlyingStatusEffect()
        val WALL = WallStatusEffect()
        val PIERCING = ArmorPiercingStatusEffect()
    }
}

class ArmorPiercingStatusEffect : StatusEffect(StatusEffectCategory.BUFF, "Armor piercing") {
    override fun onTick(currentDuration: Int, victim: ComponentedObject, inflictor: ComponentedObject?) = Unit
    override fun tickChangeDuration(currentDuration: Int): Int = currentDuration
}

class WallStatusEffect : StatusEffect(StatusEffectCategory.BUFF, "Wall") {
    override fun onTick(currentDuration: Int, victim: ComponentedObject, inflictor: ComponentedObject?) = Unit
    override fun tickChangeDuration(currentDuration: Int): Int = currentDuration
}

class FlyingStatusEffect : StatusEffect(StatusEffectCategory.BUFF, "Flying") {
    override fun onTick(currentDuration: Int, victim: ComponentedObject, inflictor: ComponentedObject?) = Unit
    override fun tickChangeDuration(currentDuration: Int): Int = currentDuration
}
