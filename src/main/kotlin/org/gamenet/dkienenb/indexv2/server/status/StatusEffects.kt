package org.gamenet.dkienenb.indexv2.server.status

import org.gamenet.dkienenb.component.ComponentedObject
import org.gamenet.dkienenb.indexv2.server.combat.HealthComponent

class StatusEffects {
    companion object {
        val BURNING = BurningDebuff()
        val FLYING = FlyingBuff()
        val WALL = WallBuff()
        val PIERCING = ArmorPiercingBuff()
        val NO_RETALIATION = NoRetaliationBuff()
        val PLATED_ARMOR = PlatedArmorBuff()
        val SPIKES = InstantDamageDebuff("Spikes")
    }
}

class PlatedArmorBuff: StatusEffect(StatusEffectCategory.BUFF, "Block", true) {

    override fun onApply(potency: Int, currentDuration: Int, victim: ComponentedObject, inflictor: ComponentedObject?) {
        victim.getComponent(HealthComponent::class.java).temporaryHealth = potency
    }
    override fun onTick(potency: Int, currentDuration: Int, victim: ComponentedObject, inflictor: ComponentedObject?) {
        victim.getComponent(HealthComponent::class.java).temporaryHealth = potency
    }
}

class NoRetaliationBuff: MarkerStatusEffect(StatusEffectCategory.BUFF, "Swift")
class ArmorPiercingBuff : MarkerStatusEffect(StatusEffectCategory.BUFF, "Armor piercing")
class WallBuff : MarkerStatusEffect(StatusEffectCategory.BUFF, "Wall")
class FlyingBuff : MarkerStatusEffect(StatusEffectCategory.BUFF, "Flying")

// debuffs

class InstantDamageDebuff(name: String) : StatusEffect(StatusEffectCategory.DEBUFF, name, false) {
    override fun onTick(potency: Int, currentDuration: Int, victim: ComponentedObject, inflictor: ComponentedObject?) {}
    override fun onApply(potency: Int, currentDuration: Int, victim: ComponentedObject, inflictor: ComponentedObject?) {
        victim.getComponent(HealthComponent::class.java).changeHealth(-potency)
    }
    override fun tickChangeDuration(potency: Int, currentDuration: Int): Int = 0
}

class BurningDebuff: StatusEffect(StatusEffectCategory.DEBUFF, "Burning", true) {
    override fun onTick(potency: Int, currentDuration: Int, victim: ComponentedObject, inflictor: ComponentedObject?) {
        victim.getComponent(HealthComponent::class.java).changeHealth(-1)
    }
}

// types

class CreatureTypes {
    companion object {
        val RAT = RatCreatureType()
        val SLIME = SlimeCreatureType()
        val WALL = WallCreatureType()
        val LOWER_BEING = LowerBeingCreatureType()
    }
}

class LowerBeingCreatureType : MarkerStatusEffect(StatusEffectCategory.INNATE, "Lower Being")
class WallCreatureType : MarkerStatusEffect(StatusEffectCategory.INNATE, "Wall")
class SlimeCreatureType : MarkerStatusEffect(StatusEffectCategory.INNATE, "Slime")
class RatCreatureType : MarkerStatusEffect(StatusEffectCategory.INNATE, "Rat")
