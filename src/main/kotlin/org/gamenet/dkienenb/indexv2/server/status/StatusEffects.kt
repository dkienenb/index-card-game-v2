package org.gamenet.dkienenb.indexv2.server.status

import org.gamenet.dkienenb.component.ComponentedObject
import org.gamenet.dkienenb.indexv2.server.card.PlayerOwnedComponent
import org.gamenet.dkienenb.indexv2.server.card.PurchasableComponent
import org.gamenet.dkienenb.indexv2.server.card.deck.DeckComponent
import org.gamenet.dkienenb.indexv2.server.combat.HealthComponent
import org.gamenet.dkienenb.indexv2.server.combat.MortalComponent

public class StatusEffects {
    companion object {
        val BURNING = BurningDebuff()
        val SPIKED = InstantDamageDebuff("Spiked")

        val FLYING = FlyingBuff()
        val WALL = WallBuff()
        val PIERCING = ArmorPiercingBuff()
        val NO_RETALIATION = NoRetaliationBuff()
        val PLATED_ARMOR = PlatedArmorBuff()
        val GOOPY = GoopyBuff()
    }
}

public class GoopyBuff: StatusEffect(StatusEffectCategory.BUFF, "Goopy", false) {
    override fun onTick(potency: Int, currentDuration: Int, victim: ComponentedObject, inflictor: ComponentedObject?) {}
    override fun onApply(potency: Int, currentDuration: Int, victim: ComponentedObject, inflictor: ComponentedObject?) {
        victim.getComponent(MortalComponent::class.java).addDeathEffect { it, _ ->
            if (it.getComponent(StatusEffectComponent::class.java).has(StatusEffects.GOOPY)) {
                val cost = it.getComponent(PurchasableComponent::class.java).getCost()
                val player = it.getComponent(PlayerOwnedComponent::class.java).getPlayer()
                val deck = player.deck.getComponent(DeckComponent::class.java)
                val cardOptions = deck.listDicardsCostingLessThan(cost)
                val chosen = player.clientSelectCardOrNoCard(cardOptions, "retrieve via Goopy")
                if (chosen != null) {
                    deck.removeFromDiscard(chosen)
                    player.addCard(chosen)
                }
            }
        }
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
