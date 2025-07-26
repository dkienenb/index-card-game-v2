package org.gamenet.dkienenb.indexv2.server.combat

import org.gamenet.dkienenb.component.Component
import org.gamenet.dkienenb.component.ComponentedObject
import org.gamenet.dkienenb.indexv2.server.Main
import org.gamenet.dkienenb.indexv2.server.ModifiedIntComponent
import org.gamenet.dkienenb.indexv2.server.card.NameComponent
import org.gamenet.dkienenb.indexv2.server.card.OriginalPlayerOwnedComponent
import org.gamenet.dkienenb.indexv2.server.card.PlayerOwnedComponent
import org.gamenet.dkienenb.indexv2.server.status.StatusEffectComponent
import org.gamenet.dkienenb.indexv2.server.status.StatusEffects

class AttackerComponent(damage: Int, var ranged: Boolean) : ModifiedIntComponent(damage) {

    private val attackEffects = mutableListOf<(ComponentedObject) -> Unit>()

    fun getDamage(): Int = value

    fun setDamage(damage: Int) {
        value = damage
    }

    override fun getDependencies(): MutableList<Class<out Component>> {
        val list = super.getDependencies()
        list.add(OriginalPlayerOwnedComponent::class.java)
        return list
    }

    fun attack(target: ComponentedObject, hasRetaliation: Boolean) {
        val owner = attached.getComponent(PlayerOwnedComponent::class.java).getPlayer()
        val attackedPlayer = target.getComponent(PlayerOwnedComponent::class.java).getPlayer()
        Main.sendMessageToAll(
            "${owner.client.getName()}'s ${attached.getComponent(NameComponent::class.java).getName()} attacked" +
                    " ${attackedPlayer.client.getName()}'s ${
                        target.getComponent(NameComponent::class.java).getName()
                    }.", null
        )
        attackEffects.forEach {
            it(target)
        }
        target.getComponent(TargetComponent::class.java).receiveAttack(
            attached,
            getDamage(),
            attached.getComponent(StatusEffectComponent::class.java).has(StatusEffects.PIERCING),
            hasRetaliation,
        )
    }

    fun addOnAttackEffect(effect: (ComponentedObject) -> Unit) {
        attackEffects.add(effect)
    }

}