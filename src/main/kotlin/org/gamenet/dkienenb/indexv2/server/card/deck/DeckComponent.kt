package org.gamenet.dkienenb.indexv2.server.card.deck

import org.gamenet.dkienenb.component.Component
import org.gamenet.dkienenb.component.ComponentedObject
import org.gamenet.dkienenb.component.ListStoringComponent
import org.gamenet.dkienenb.indexv2.client.message.PlayerLossMessage
import org.gamenet.dkienenb.indexv2.client.message.StealCardMessage
import org.gamenet.dkienenb.indexv2.server.Main
import org.gamenet.dkienenb.indexv2.server.Player
import org.gamenet.dkienenb.indexv2.server.card.Card
import org.gamenet.dkienenb.indexv2.server.card.NameComponent
import org.gamenet.dkienenb.indexv2.server.card.OriginalPlayerOwnedComponent
import org.gamenet.dkienenb.indexv2.server.card.PlayerOwnedComponent
import org.gamenet.dkienenb.indexv2.server.combat.HealthComponent
import org.gamenet.dkienenb.indexv2.server.combat.MaxHealthComponent
import org.gamenet.dkienenb.indexv2.server.combat.MortalComponent
import org.gamenet.dkienenb.indexv2.server.combat.TargetComponent

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
            .addOnAttackedEffect { attacker: ComponentedObject, damage: Int ->
                val player = attacker.getComponent(PlayerOwnedComponent::class.java).getPlayer()
                val attackerId = player.id
                val attackedId = attached.getComponent(PlayerOwnedComponent::class.java).getPlayer().id
                for (i in 1..damage) {
                    if (value.isNotEmpty()) {
                        val card = giveCard(player)
                        Main.sendMessageToAll(StealCardMessage(attackerId, attackedId))
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
        attached.getComponent(MortalComponent::class.java).addDeathEffect { it, _ ->
            val player = it.getComponent(PlayerOwnedComponent::class.java).getPlayer()
            Main.sendMessageToAll(PlayerLossMessage(player.id))
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