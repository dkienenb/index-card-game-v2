package org.gamenet.dkienenb.indexv2.server

import org.gamenet.dkienenb.component.ComponentedObject
import org.gamenet.dkienenb.indexv2.client.Client
import org.gamenet.dkienenb.indexv2.client.message.*
import org.gamenet.dkienenb.indexv2.server.card.*
import org.gamenet.dkienenb.indexv2.server.card.deck.Deck
import org.gamenet.dkienenb.indexv2.server.card.deck.DeckComponent
import org.gamenet.dkienenb.indexv2.server.card.deck.DeckType
import org.gamenet.dkienenb.indexv2.server.combat.*
import org.gamenet.dkienenb.indexv2.server.status.StatusEffectComponent
import org.gamenet.dkienenb.indexv2.server.status.StatusEffects

private const val NORMAL_DRAW_BANNED_AT = 10

class Player(val client: Client, val id: Int) {

    val deck = Deck(this, askClientForDeckType())
    val hand = mutableListOf<Card>()
    val buildings = mutableListOf<ComponentedObject>()
    val battleLine = mutableListOf<ComponentedObject>()
    var unspentMoney: Int = 0
    var turnsTaken: Int = 0

    fun <T> clientChoice(choiceLabel: String, choices: List<T>, stringMapper: (T) -> String): T {
        val labelToThingMap = choices.associateBy(stringMapper)
        val chosen = client.makeChoice(choiceLabel, labelToThingMap.keys)
        return labelToThingMap.getValue(chosen)
    }

    private fun askClientForDeckType(): DeckType =
        clientChoice("deck type", DeckType.values().toList()) { it.typeName }

    fun takeTurn(players: List<Player>) {
        Main.sendMessageToAll(TurnStartMessage(id, client.getName()))
        client.displayMessage(MoneyRemainderMessage(unspentMoney))
        tickStatusEffects()
        drawCards(players)
        playCards(players)
        if (turnsTaken > 0) {
            attackPhase(players)
        }
        // TODO retreat cards
        Main.sendMessageToAll(TurnEndMessage(id, client.getName()))
        turnsTaken++
    }

    private fun tickStatusEffects() {
        val tickStatusEffectComponent: (ComponentedObject) -> Unit = {
            if (it.hasComponent(StatusEffectComponent::class.java)) {
                it.getComponent(StatusEffectComponent::class.java).tick()
            }
        }
        hand.stream().forEach(tickStatusEffectComponent)
        applyToAllDeployedCards(tickStatusEffectComponent)
    }

    fun applyToAllDeployedCards(tickStatusEffectComponent: (ComponentedObject) -> Unit) {
        buildings.stream().forEach(tickStatusEffectComponent)
        battleLine.stream().forEach(tickStatusEffectComponent)
        deck.apply(tickStatusEffectComponent)
    }

    private fun attackPhase(players: List<Player>) {
        if (isOut()) return
        if (battleLine.isEmpty()) return
        val meleeAttacker = battleLine[0]
        attackWith(meleeAttacker, players)
        battleLine
            .asSequence()
            .filter { it != meleeAttacker }
            .filter { it.getComponent(AttackerComponent::class.java).ranged }
            .forEach { attackWith(it, players) }
    }

    private fun attackWith(
        fighter: ComponentedObject,
        players: List<Player>
    ) {
        if (players.any { !it.isOut() }) {
            client.displayMessage(NowAttackingWithMessage(fighter.getComponent(CardIdComponent::class.java).getId()))
            if (client.checkIfPlayerWants(YesOrNoQuestionType.TO_ATTACK)) {
                if (fighter.hasComponent(AlternativeAttackComponent::class.java) && client.checkIfPlayerWants(YesOrNoQuestionType.TO_USE_ALTERNATE_ATTACK)) {
                    val alternativeAttackComponent = fighter.getComponent(AlternativeAttackComponent::class.java)
                    alternativeAttackComponent.doAlternativeAttack()
                } else {
                    val attackerComponent = fighter.getComponent(AttackerComponent::class.java)
                    val attackedPlayer = selectOtherPlayer(players)
                    val targets = if (attackerComponent.ranged) {
                        attackedPlayer.getRangedTargets()
                    } else {
                        attackedPlayer.getMeleeTargets()
                    }
                    val selectedTarget =
                        clientChoice(
                            "target for ${fighter.getComponent(NameComponent::class.java).getName()}",
                            targets
                        ) {
                            val id: String = if (it.hasComponent(CardIdComponent::class.java)) {
                                it.getComponent(CardIdComponent::class.java).getId().toString()
                            } else {
                                "no id"
                            }
                            it.getComponent(NameComponent::class.java).getName() +
                                    " [" + id + "]"
                        }
                    attackerComponent.attack(selectedTarget, !attackerComponent.ranged)
                }
            }
        }
    }

    private fun getRangedTargets(): List<ComponentedObject> {
        val list = mutableListOf<ComponentedObject>()
        if (battleLine.isNotEmpty()) {
            for (fighter in battleLine) {
                list.add(fighter)
                if (fighter.getComponent(StatusEffectComponent::class.java).has(StatusEffects.WALL)) {
                    break
                }
            }
        } else {
            list.addAll(buildings.plus(deck))
        }
        return list
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
        clientChoice("attack target", players.filter { !it.isOut() }) { it.client.getName() + " #" + it.id }

    private fun playCards(players: List<Player>) {
        while (true) {
            if (isOut()) {
                break
            }
            attemptPlay(selectOneCardToPlay() ?: break, players)
        }
    }

    private fun attemptPlay(card: Card, players: List<Player>) {
        val cost = card.getComponent(PurchasableComponent::class.java).getCost()
        if (unspentMoney >= cost) {
            unspentMoney -= cost
            val uuid = card.getComponent(CardIdComponent::class.java).getId()
            val name = card.getComponent(NameComponent::class.java).getName()
            val health = card.getComponent(HealthComponent::class.java).getHealth()
            val damage = card.getComponent(AttackerComponent::class.java).getDamage()
            val defense = card.getComponent(DefenseComponent::class.java).getDefense()
            Main.sendMessageToAll(
                CardPlayedMessage(uuid, name, health, damage, defense, id)
            )
            val cardNewLocation = card.play(this)
            hand.remove(card)
            when (cardNewLocation) {
                CardPlayResultLocation.DISCARD -> deck.getComponent(DeckComponent::class.java).discardCard(card)
                CardPlayResultLocation.BATTLE_PLAYER_CHOICE -> TODO()
                CardPlayResultLocation.BATTLE_BACK -> battleLine.add(card)
                CardPlayResultLocation.BUILDING -> buildings.add(card)
                CardPlayResultLocation.HAND -> hand.add(card)
                CardPlayResultLocation.TOPDECK -> deck.getComponent(DeckComponent::class.java).topDeckCard(card)
                CardPlayResultLocation.BOTTOMDECK -> deck.getComponent(DeckComponent::class.java).bottomDeckCard(card)
            }
            players.filter { !it.isOut() }.forEach { player ->
                player.applyToAllDeployedCards {
                    if (it.hasComponent(ReactsToCardPlaysComponent::class.java)) {
                        it.getComponent(ReactsToCardPlaysComponent::class.java).onCardPlay(card)
                    }
                }
            }
        } else {
            client.displayMessage("You don't have enough money to play that.")
        }
    }

    private fun selectOneCardToPlay(): Card? {
        client.displayMessage(MoneyRemainderMessage(unspentMoney))
        val cardToNameMap = hand.associateBy {
            "(${it.getComponent(PurchasableComponent::class.java).getCost()}) " +
                    "${it.getComponent(NameComponent::class.java).getName()} " +
                    "[${it.getComponent(CardIdComponent::class.java).getId()}]"
        }.plus("No card" to null)
        val chosen = client.makeChoice("card to play", cardToNameMap.keys)
        return cardToNameMap[chosen]
    }

    private fun drawCards(players: List<Player>) {
        while (hand.size < NORMAL_DRAW_BANNED_AT) {
            val deckComponent = deck.getComponent(DeckComponent::class.java)
            client.displayMessage(DeckSizeMessage(deck.getComponent(HealthComponent::class.java).getHealth()))
            if (client.checkIfPlayerWants(YesOrNoQuestionType.ANOTHER_CARD)) {
                Main.sendMessageToAll(DrawCardMessage(id))
                val card = deckComponent.drawCard()
                client.displayMessage("You drew a ${card.getComponent(NameComponent::class.java).getName()}.")
                addCard(card)
                players.filter { !it.isOut() }.forEach { player ->
                    player.applyToAllDeployedCards {
                        if (it.hasComponent(ReactsToCardDrawsComponent::class.java)) {
                            it.getComponent(ReactsToCardDrawsComponent::class.java).onCardDraw(card)
                        }
                    }
                }
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
        card.getComponent(PlayerOwnedComponent::class.java).setPlayer(this)
        hand.add(card)
    }

    fun removeFromPlay(card: Card) {
        battleLine.remove(card)
        buildings.remove(card)
    }
}