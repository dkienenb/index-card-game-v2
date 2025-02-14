package org.gamenet.dkienenb.indexv2.server

import org.gamenet.dkienenb.component.Component
import org.gamenet.dkienenb.component.ComponentedObject
import org.gamenet.dkienenb.component.ListStoringComponent

class DeckComponent(val type: DeckType) : ListStoringComponent<Card>() {

    val discardPile: MutableList<Card> = ArrayList()

    init {
        value.addAll(type.initialCardList())
    }

    override fun getDependencies(): MutableList<Class<out Component>> {
        val list = super.getDependencies()
        list.add(TargetComponent::class.java)
        list.add(PlayerOwnedComponent::class.java)
        return list
    }

    override fun onAdd() {
        val cardCount = value.size
        attached.getComponent(MaxHealthComponent::class.java).setMaxHealth(cardCount)
        recalculateHealth()
        attached.getComponent(TargetComponent::class.java).addOnAttackEffect{ attacker: ComponentedObject, damage: Int ->
            val player = attacker.getComponent(PlayerOwnedComponent::class.java).getPlayer()
            for (i in 1..damage) {
                giveCard(player)
            }
        }
        attached.getComponent(MortalComponent::class.java).addDeathEffect {
            it.getComponent(PlayerOwnedComponent::class.java).getPlayer().client.displayMessage("Your deck is out of cards. You lose!")
        }
    }

    private fun removeFirstCard(): Card = value.removeAt(0)
    private fun recalculateHealth() {
        attached.getComponent(HealthComponent::class.java).setHealth(value.size)
    }

    fun drawCard(): Card {
        val firstCard = removeFirstCard()
        recalculateHealth()
        return firstCard
    }

    private fun giveCard(player: Player) {
        player.addCard(removeFirstCard())
    }

    fun topDeckCard(card: Card) {
        value.addFirst(card)
        recalculateHealth()
    }

    fun discardCard(card: Card) {
        discardPile.add(card)
    }

    fun shuffle() = value.shuffle()
    fun isEmpty(): Boolean = value.isEmpty()

}