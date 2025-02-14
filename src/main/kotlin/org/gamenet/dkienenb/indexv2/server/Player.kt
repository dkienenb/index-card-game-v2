package org.gamenet.dkienenb.indexv2.server

import org.gamenet.dkienenb.component.ComponentedObject
import org.gamenet.dkienenb.indexv2.client.Client

class Player(var client: Client) {
    val deck = Deck(this, getDeckType())

    private fun getDeckType(): DeckType {
        val nameToTypeMap = DeckType.entries.associateBy { it.typeName }
        val chosen = client.makeChoice("deck type", nameToTypeMap.keys)
        return nameToTypeMap.getValue(chosen)
    }

    private val hand = mutableListOf<Card>()
    val buildings = mutableListOf<ComponentedObject>()
    val battleLine = mutableListOf<ComponentedObject>()
    var unspentMoney: Int = 0

    fun takeTurn() {
        drawCards()
        playCards()
        attackPhase()
    }

    private fun attackPhase() {
        TODO("Not yet implemented")
    }

    private fun playCards() {
        while (true) {
            val nextToPlay = selectOneCardToPlay() ?: break
            TODO()
        }
    }

    private fun selectOneCardToPlay(): Card? {
        val cardToNameMap =
            hand.associateBy { it.getComponent(NameComponent::class.java).getName() }.plus("No card" to null)
        val chosen = client.makeChoice("card to play", cardToNameMap.keys)
        return cardToNameMap[chosen]
    }

    private fun drawCards() {
        while (hand.size < 10) {
            val deckComponent = deck.getComponent(DeckComponent::class.java)
            if (client.checkIfPlayerWants("another card", mapOf("deckSize" to "${deck.getComponent(HealthComponent::class.java).getHealth()}"))) {
                val card = deckComponent.drawCard()
                addCard(card)
                if (!deck.getComponent(MortalComponent::class.java).isLiving()) {
                    break
                }
            } else {
                break
            }
        }
    }

    fun addCard(card: Card) {
        client.displayMessage("You drew a ${card.getComponent(NameComponent::class.java).getName()}.")
        hand.add(card)
    }
}