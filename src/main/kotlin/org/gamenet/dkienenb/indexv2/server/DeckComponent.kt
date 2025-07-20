package org.gamenet.dkienenb.indexv2.server

import org.gamenet.dkienenb.component.Component
import org.gamenet.dkienenb.component.ComponentedObject
import org.gamenet.dkienenb.component.ListStoringComponent

class DeckComponent(val type: DeckType, player: Player) : ListStoringComponent<Card>() {

    private val discardPile: MutableList<Card> = ArrayList()

    init {
        value.addAll(type.initialCardList(player))
    }

    override fun getDependencies(): MutableList<Class<out Component>> {
        val list = super.getDependencies()
        list.add(TargetComponent::class.java)
        list.add(OriginalPlayerOwnedComponent::class.java)
        return list
    }

    override fun onAdd() {
        val cardCount = value.size
        attached.getComponent(MaxHealthComponent::class.java).setMaxHealth(cardCount)
        attached.getComponent(TargetComponent::class.java)
            .addOnAttackEffect { attacker: ComponentedObject, damage: Int ->
                val player = attacker.getComponent(OriginalPlayerOwnedComponent::class.java).getPlayer()
                for (i in 1..damage) {
                    if (value.isNotEmpty()) {
                        val card = giveCard(player)
                        player.client.displayMessage(
                            "Received ${card.getComponent(NameComponent::class.java).getName()} from" +
                                    " ${attached.getComponent(NameComponent::class.java).getName()}."
                        )
                    } else {
                        player.client.displayMessage(
                            "Overkilled ${attached.getComponent(NameComponent::class.java).getName()}! " +
                                    "Sadly, cards do not come from thin air."
                        )
                    }
                }
            }
        attached.getComponent(MortalComponent::class.java).addDeathEffect {
            val player = it.getComponent(OriginalPlayerOwnedComponent::class.java).getPlayer()
            player.client.displayMessage("Your deck is out of cards. You lose!")
            Main.sendAllExcept("${player.client.getName()} is out of cards and has lost.", player)
        }
        recalculateHealth()
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

    private fun giveCard(player: Player): Card {
        val card = removeFirstCard()
        player.addCard(card)
        return card
    }

    fun topDeckCard(card: Card) {
        value.addFirst(card)
        recalculateHealth()
    }

    fun bottomDeckCard(card: Card) {
        value.addLast(card)
        recalculateHealth()
    }

    fun discardCard(card: Card) {
        discardPile.add(card)
    }

    fun shuffle() = value.shuffle()
    fun isEmpty(): Boolean = value.isEmpty()

}