package org.gamenet.dkienenb.indexv2.server

import org.gamenet.dkienenb.component.ComponentedObject
import org.gamenet.dkienenb.indexv2.client.Client

private const val NORMAL_DRAW_BANNED_AT = 10

class Player(var client: Client) {

    val deck = Deck(this, askClientForDeckType())
    private val hand = mutableListOf<Card>()
    val buildings = mutableListOf<ComponentedObject>()
    val battleLine = mutableListOf<ComponentedObject>()
    var unspentMoney: Int = 0

    fun <T> clientChoice(choiceLabel: String, choices: List<T>, stringMapper:(T) -> String) : T {
        val labelToThingMap = choices.associateBy (stringMapper)
        val chosen = client.makeChoice(choiceLabel, labelToThingMap.keys)
        return labelToThingMap.getValue(chosen)
    }

    private fun askClientForDeckType(): DeckType {
        val chosen = clientChoice("deck type", DeckType.entries) { it.typeName }
        Main.sendAllExcept("${this.client.getName()} chose deck type ${chosen.typeName}", this)
        return chosen
    }

    fun takeTurn(players: List<Player>) {
        Main.sendAllExcept("Turn begin: ${client.getName()}", this)
        drawCards()
        playCards()
        attackPhase(players)
        // retreat cards
        Main.sendAllExcept("Turn end: ${client.getName()}", this)
    }

    private fun attackPhase(players: List<Player>) {
        if (isOut()) return
        if (battleLine.isEmpty()) return
        val attacker = battleLine[0]
        val attackerComponent = attacker.getComponent(AttackerComponent::class.java)
        val attackedPlayer = selectOtherPlayer(players)
        val targets = attackedPlayer.getMeleeTargets()
        val selectedTarget = clientChoice("target", targets) {it.getComponent(NameComponent::class.java).getName()}
        attackerComponent.attack(selectedTarget)
        Main.sendAllExcept("${this.client.getName()}'s ${attacker.getComponent(NameComponent::class.java).getName()} attacked" +
                " ${attackedPlayer.client.getName()}'s ${selectedTarget.getComponent(NameComponent::class.java).getName()}.", null)
        // TODO ranged attackers
    }

    private fun getMeleeTargets(): List<ComponentedObject> {
        if (battleLine.isNotEmpty()) {
            return listOf(battleLine[0])
        }
        return buildings.plus(deck)
    }

    private fun selectOtherPlayer(players: List<Player>): Player {
        val targets = players.filter { it != this }
        if (targets.isEmpty()) throw Exception("empty player list")
        return selectPlayer(targets)
    }

    private fun selectPlayer(players: List<Player>): Player =
        clientChoice("attack target", players) {it.client.getName()}

    private fun playCards() {
        while (true) {
            if (isOut()) {
                break
            }
            attemptPlay(selectOneCardToPlay() ?: break)
        }
    }

    private fun attemptPlay(card: Card) {
        val cost = card.getComponent(PurchasableComponent::class.java).getCost()
        if (unspentMoney >= cost) {
            unspentMoney -= cost
            Main.sendAllExcept("${client.getName()} plays a ${card.getComponent(NameComponent::class.java).getName()}.", this)
            val cardNewLocation = card.play(this)
            hand.remove(card)
            when(cardNewLocation) {
                CardPlayResultLocation.DISCARD -> deck.getComponent(DeckComponent::class.java).discardCard(card)
                CardPlayResultLocation.BATTLE_PLAYER_CHOICE -> TODO()
                CardPlayResultLocation.BATTLE_BACK -> battleLine.add(card)
                CardPlayResultLocation.BUILDING -> buildings.add(card)
                CardPlayResultLocation.HAND -> hand.add(card)
                CardPlayResultLocation.TOPDECK -> deck.getComponent(DeckComponent::class.java).topDeckCard(card)
                CardPlayResultLocation.BOTTOMDECK -> deck.getComponent(DeckComponent::class.java).bottomDeckCard(card)
            }
        } else {
            client.displayMessage("You don't have enough money to play that.")
        }
    }

    private fun selectOneCardToPlay(): Card? {
        val cardToNameMap = hand.associateBy { "(${it.getComponent(PurchasableComponent::class.java).getCost()}) ${it.getComponent(NameComponent::class.java).getName()}" }.plus("No card" to null)
        val chosen = client.makeChoice("card to play", cardToNameMap.keys)
        return cardToNameMap[chosen]
    }

    private fun drawCards() {
        while (hand.size < NORMAL_DRAW_BANNED_AT) {
            val deckComponent = deck.getComponent(DeckComponent::class.java)
            if (client.checkIfPlayerWants("another card", mapOf("deckSize" to "${deck.getComponent(HealthComponent::class.java).getHealth()}"))) {
                Main.sendAllExcept("${client.getName()} draws a card.", this)
                val card = deckComponent.drawCard()
                client.displayMessage("You drew a ${card.getComponent(NameComponent::class.java).getName()}.")
                addCard(card)
                if (isOut()) {
                    break
                }
            } else {
                break
            }
        }
    }

    fun isOut() = !(deck.getComponent(MortalComponent::class.java).isLiving())

    fun addCard(card: Card) {
        hand.add(card)
    }

    fun removeFromPlay(card: Card) {
        battleLine.remove(card)
        buildings.remove(card)
    }
}