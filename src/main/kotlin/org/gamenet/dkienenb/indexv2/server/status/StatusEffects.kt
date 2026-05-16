package org.gamenet.dkienenb.indexv2.server.status

import org.gamenet.dkienenb.component.ComponentedObject
import org.gamenet.dkienenb.indexv2.server.combat.HealthComponent

public class StatusEffects {
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

public class PlatedArmorBuff: StatusEffect(StatusEffectCategory.BUFF, "Block", true) {

    override fun onApply(potency: Int, currentDuration: Int, victim: ComponentedObject, inflictor: ComponentedObject?) {
        victim.getComponent(HealthComponent::class.java).temporaryHealth = potency
    }
    override fun onTick(potency: Int, currentDuration: Int, victim: ComponentedObject, inflictor: ComponentedObject?) {
        victim.getComponent(HealthComponent::class.java).temporaryHealth = potency
    }
}

public class NoRetaliationBuff: MarkerStatusEffect(StatusEffectCategory.BUFF, "Swift")
public class ArmorPiercingBuff : MarkerStatusEffect(StatusEffectCategory.BUFF, "Armor piercing")
public class WallBuff : MarkerStatusEffect(StatusEffectCategory.BUFF, "Wall")
public class FlyingBuff : MarkerStatusEffect(StatusEffectCategory.BUFF, "Flying")

// debuffs

public class InstantDamageDebuff(name: String) : StatusEffect(StatusEffectCategory.DEBUFF, name, false) {
    override fun onTick(potency: Int, currentDuration: Int, victim: ComponentedObject, inflictor: ComponentedObject?) {}
    override fun onApply(potency: Int, currentDuration: Int, victim: ComponentedObject, inflictor: ComponentedObject?) {
        victim.getComponent(HealthComponent::class.java).changeHealth(-potency)
    }
    override fun tickChangeDuration(potency: Int, currentDuration: Int): Int = 0
}

public class BurningDebuff: StatusEffect(StatusEffectCategory.DEBUFF, "Burning", true) {
    override fun onTick(potency: Int, currentDuration: Int, victim: ComponentedObject, inflictor: ComponentedObject?) {
        victim.getComponent(HealthComponent::class.java).changeHealth(-1)
    }
}

// types

public class CreatureTypes {
    companion object {
        val RAT = RatCreatureType()
        val SLIME = SlimeCreatureType()
        val WALL = WallCreatureType()
        val LOWER_BEING = LowerBeingCreatureType()
    }
}

public class LowerBeingCreatureType : MarkerStatusEffect(StatusEffectCategory.INNATE, "Lower Being")
public class WallCreatureType : MarkerStatusEffect(StatusEffectCategory.INNATE, "Wall")
public class SlimeCreatureType : MarkerStatusEffect(StatusEffectCategory.INNATE, "Slime")
public class RatCreatureType : MarkerStatusEffect(StatusEffectCategory.INNATE, "Rat")
