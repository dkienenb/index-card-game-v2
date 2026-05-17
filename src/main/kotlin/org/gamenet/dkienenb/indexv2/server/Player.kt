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

public class Player(val client: Client, val id: Int) {

    val deck = Deck(this, askClientForDeckType())
    val hand = mutableListOf<Card>()
    private val buildings = mutableListOf<ComponentedObject>()
    private val battleLine = mutableListOf<ComponentedObject>()
    var unspentMoney: Int = 0
    private var turnsTaken: Int = 0

    fun <T> clientChoice(choiceLabel: String, choices: List<T>, stringMapper: (T) -> String): T {
        val labelToThingMap = choices.associateBy(stringMapper)
        val chosen = client.makeChoice(choiceLabel, labelToThingMap.keys)
        return labelToThingMap.getValue(chosen)
    }

    private fun askClientForDeckType(): DeckType =
        clientChoice("deck type", DeckType.registry, DeckType::name)

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

    fun clientMove(fighter: ComponentedObject) {
        battleLine.remove(fighter)
        val placedBeforeId = placeAnywhereInBattleLine(fighter)
        Main.sendMessageToAll(CardMovedMessage(fighter.getComponent(CardIdComponent::class.java).getId(), placedBeforeId))
    }

    fun applyToAllDeployedCards(function: (ComponentedObject) -> Unit) {
        val newList = buildings + battleLine + deck
        newList.forEach(function)
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
        if (players.any { !it.isOut() && it != this }) {
            client.displayMessage(NowAttackingWithMessage(fighter.getComponent(CardIdComponent::class.java).getId()))
            if (client.checkIfPlayerWants(YesOrNoQuestionType.TO_ATTACK)) {
                val alternativeAttacksComponent = if (fighter.hasComponent(AlternativeAttacksComponent::class.java)) {
                    fighter.getComponent(AlternativeAttacksComponent::class.java)
                } else {
                    null
                }
                if (alternativeAttacksComponent != null) {
                    val alternativeAttacks = alternativeAttacksComponent.getAvailableAttacks(fighter)
                    if (alternativeAttacks.isNotEmpty() && client.checkIfPlayerWants(YesOrNoQuestionType.TO_USE_ALTERNATE_ATTACK)) {
                        val selectedAttack = clientChoice("alternative attack", alternativeAttacks, AlternativeAttack::getAttackName)
                        if (selectedAttack.requiresTarget()) {
                            selectedAttack.doAttack(fighter, selectTarget(players, selectedAttack.isRanged(fighter), fighter))
                        } else {
                            selectedAttack.doAttack(fighter, null)
                        }
                    } else {
                        attackNormally(fighter, players)
                    }
                } else {
                    attackNormally(fighter, players)
                }
            }
        }
    }

    private fun attackNormally(
        fighter: ComponentedObject,
        players: List<Player>
    ) {
        val attackerComponent = fighter.getComponent(AttackerComponent::class.java)
        val selectedTarget = selectTarget(players, attackerComponent.ranged, fighter)
        attackerComponent.attack(selectedTarget, !(attackerComponent.ranged))
    }

    private fun selectTarget(
        players: List<Player>,
        ranged: Boolean,
        fighter: ComponentedObject
    ): ComponentedObject {
        val attackedPlayer = selectOtherPlayer(players)
        val targets = if (ranged) {
            attackedPlayer.getRangedTargets()
        } else {
            attackedPlayer.getMeleeTargets()
        }
        val selectedTarget =
            clientChoice(
                "target for ${fighter.getComponent(NameComponent::class.java).getName()}",
                targets
            ) {
                val id: String = it.getComponent(CardIdComponent::class.java).getId().toString()
                it.getComponent(NameComponent::class.java).getName() + " [" + id + "]"
            }
        return selectedTarget
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
            val cardNewLocation = card.play(this)
            hand.remove(card)
            var placedBeforeId : Int? = null
            when (cardNewLocation) {
                CardPlayResultLocation.DISCARD -> deck.getComponent(DeckComponent::class.java).discardCard(card)
                CardPlayResultLocation.BATTLE_PLAYER_CHOICE -> placedBeforeId = placeAnywhereInBattleLine(card)
                CardPlayResultLocation.BATTLE_BACK -> battleLine.add(card)
                CardPlayResultLocation.BUILDING -> buildings.add(card)
                CardPlayResultLocation.HAND -> hand.add(card)
                CardPlayResultLocation.TOPDECK -> deck.getComponent(DeckComponent::class.java).topDeckCard(card)
                CardPlayResultLocation.BOTTOMDECK -> deck.getComponent(DeckComponent::class.java).bottomDeckCard(card)
            }
            if (card.hasComponent(HealthComponent::class.java)) {
                val health = card.getComponent(HealthComponent::class.java).getHealth()
                val damage = card.getComponent(AttackerComponent::class.java).getDamage()
                val defense = card.getComponent(DefenseComponent::class.java).getDefense()
                Main.sendMessageToAll(
                    FightingCardPlayedMessage(uuid, name, health, damage, defense, id, placedBeforeId)
                )
            } else {
                Main.sendMessageToAll(CardPlayedMessage(id, name))
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

    private fun placeAnywhereInBattleLine(card: ComponentedObject): Int? {
        var placedBeforeId: Int? = null
        val placedBefore = clientChoice("position for card", battleLine + null) {
            if (it != null) {
                "Before " + it.getComponent(NameComponent::class.java).getName() + " [" + it.getComponent(
                    CardIdComponent::class.java
                ).getId() + "]"
            } else {
                "At the end"
            }
        }
        if (placedBefore != null) {
            placedBeforeId = placedBefore.getComponent(CardIdComponent::class.java).getId()
            battleLine.add(battleLine.indexOf(placedBefore), card)
        } else {
            battleLine.add(card)
        }
        return placedBeforeId
    }

    private fun selectOneCardToPlay(): Card? {
        client.displayMessage(MoneyRemainderMessage(unspentMoney))
        val cards = hand
        val verb = "play"
        return clientSelectCardOrNoCard(cards, verb)
    }

    private fun clientSelectCardInternal(cards: List<Card>, verb: String, allowNoCard: Boolean): Card? {
        val cardToNameMap = cards.associateBy {
            val cost = it.getComponent(PurchasableComponent::class.java).getCost()
            val name = it.getComponent(NameComponent::class.java).getName()
            val id = it.getComponent(CardIdComponent::class.java).getId()
            "($cost) $name [$id]"
        }.let { if (allowNoCard) it + ("No card" to null) else it }

        val chosen = client.makeChoice("card to $verb", cardToNameMap.keys)
        return cardToNameMap[chosen]
    }

    fun clientSelectCardOrNoCard(cards: List<Card>, verb: String): Card? = clientSelectCardInternal(cards, verb, allowNoCard = true)
    fun clientSelectCard(cards: List<Card>, verb: String): Card = clientSelectCardInternal(cards, verb, allowNoCard = false)!!

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
        if (card.hasComponent(PlayerOwnedComponent::class.java)) {
            card.getComponent(PlayerOwnedComponent::class.java).setPlayer(this)
        }
        hand.add(card)
    }

    fun removeFromPlay(card: Card) {
        battleLine.remove(card)
        buildings.remove(card)
    }
}