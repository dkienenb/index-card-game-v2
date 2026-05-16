package org.gamenet.dkienenb.indexv2.server.combat

import org.gamenet.dkienenb.component.ComponentedObject
import org.gamenet.dkienenb.component.ListStoringComponent
import org.gamenet.dkienenb.indexv2.server.ConstantBonusModifier
import org.gamenet.dkienenb.indexv2.server.card.PlayerOwnedComponent
import org.gamenet.dkienenb.indexv2.server.status.StatusEffectComponent
import org.gamenet.dkienenb.indexv2.server.status.StatusEffects

public class AlternativeAttacksComponent : ListStoringComponent<AlternativeAttack>() {
    fun addAlternativeAttack(newAttack: AlternativeAttack) {
        value.add(newAttack)
    }

    fun getAvailableAttacks(attacker: ComponentedObject): List<AlternativeAttack> = value.filter { it.isAvailable(attacker) }
}

public abstract class AlternativeAttack {
    open fun isAvailable(attacker: ComponentedObject): Boolean = true
    abstract fun getAttackName(): String

    abstract fun doAttack(attacker: ComponentedObject, victim: ComponentedObject?)
    open fun requiresTarget() = true
    open fun isRanged(fighter: ComponentedObject): Boolean = fighter.getComponent(AttackerComponent::class.java).ranged
}

public abstract class LimitedQuantityAlternateAttack(private val maxNumberOfUses: Int) : AlternativeAttack() {

    private var numberOfUses = 0

    abstract fun doLimitedAttack(attacker: ComponentedObject, victim: ComponentedObject?, timesPreviouslyDone: Int)
    open fun meetsExtraConditions(): Boolean = true

    final override fun isAvailable(attacker: ComponentedObject): Boolean = numberOfUses < maxNumberOfUses && meetsExtraConditions()
    final override fun doAttack(attacker: ComponentedObject, victim: ComponentedObject?) {
        doLimitedAttack(attacker, victim, numberOfUses)
        numberOfUses++
    }
}

public class DefensiveMove(maxNumberOfUses: Int = 1, private val amountOfDefense: Int = 1) : LimitedQuantityAlternateAttack(maxNumberOfUses) {
    override fun doLimitedAttack(attacker: ComponentedObject, victim: ComponentedObject?, timesPreviouslyDone: Int) {
        attacker.getComponent(DefenseComponent::class.java).addModifier(ConstantBonusModifier(amountOfDefense))
    }
    override fun getAttackName(): String = "Defend"
    override fun requiresTarget(): Boolean = false
}

public class BurnAttack(private val amountOfBurn: Int = 1): AlternativeAttack() {
    override fun getAttackName(): String = "Burn"
    override fun doAttack(attacker: ComponentedObject, victim: ComponentedObject?) {
        victim!!.getComponent(StatusEffectComponent::class.java).applyStatusEffect(1, amountOfBurn, StatusEffects.BURNING, attacker)
    }
}

public class WindAttack: AlternativeAttack() {
    override fun getAttackName(): String = "Haste"
    override fun doAttack(attacker: ComponentedObject, victim: ComponentedObject?) {
        attacker.getComponent(AttackerComponent::class.java).attack(victim!!, false)
        attacker.getComponent(PlayerOwnedComponent::class.java).getPlayer().clientMove(attacker)
    }
}