package org.gamenet.dkienenb.indexv2.client.message

public enum class YesOrNoQuestionType {
    ANOTHER_CARD,
    TO_ATTACK,
    TO_USE_ALTERNATE_ATTACK,
}

public abstract class Message {
    abstract fun toStringMessage(): String
}

public data class DeckSizeMessage(val deckSize: Int): Message() {
    override fun toStringMessage(): String = "You have $deckSize cards in your deck."
}

public data class MoneyRemainderMessage(val money: Int): Message() {
    override fun toStringMessage(): String = "You have $money money left."
}

public data class PlayerInfoMessage(val playerId: Int, val playerName: String, val deckType: String, val deckSize: Int): Message() {
    override fun toStringMessage(): String = "Player info: #$playerId \"$playerName\" - $deckType, total card count $deckSize"
}

public data class PlayerLossMessage(val playerId: Int): Message() {
    override fun toStringMessage(): String = "Player #$playerId is out of cards and has lost."
}

public data class MoneyDieMessage(val money: Int): Message() {
    override fun toStringMessage(): String = "Money die result is $money."
}

public data class FightingCardPlayedMessage(val cardId: Int, val cardName: String,
                                     val health: Int, val damage: Int, val defense: Int, val playerId: Int, val placedBefore: Int?): Message() {
    override fun toStringMessage(): String = "Player #${playerId} plays a $cardName (Health: $health, " +
            "Damage: $damage, Defense $defense) with id $cardId. ${placedBefore?.let { " It was placed before $it." } ?: ""}"
}

public data class CardMovedMessage(val cardId: Int, val placedBefore: Int?): Message() {
    override fun toStringMessage(): String = "Card with id $cardId was moved to before $placedBefore."
}

public data class CardPlayedMessage(val playerId: Int, val cardName: String) : Message() {
    override fun toStringMessage(): String = "Player #${playerId} plays a $cardName."
}

public data class CardDeathMessage(val cardId: Int): Message() {
    override fun toStringMessage(): String = "Card with id $cardId is no more."
}

public data class CardHealthChangedMessage(val newHealth: Int, val cardId: Int): Message() {
    override fun toStringMessage(): String =
        "Card with id $cardId had a health change (resulting in $newHealth health)."
}

public data class DrawCardMessage(val playerId: Int): Message() {
    override fun toStringMessage(): String = "Player #$playerId draws a card."
}

public data class StealCardMessage(val aggressorId: Int, val victimId: Int): Message() {
    override fun toStringMessage(): String = "Player #${aggressorId} steals a card from #${victimId}'s deck!"
}

public data class TurnStartMessage(val playerId: Int, val playerName: String): Message() {
    override fun toStringMessage(): String = "Turn start: Player #${playerId} (named $playerName)."
}

public data class TurnEndMessage(val playerId: Int, val playerName: String): Message() {
    override fun toStringMessage(): String = "Turn end: Player #${playerId} (named $playerName)."
}

public data class NowAttackingWithMessage(val cardId: Int): Message() {
    override fun toStringMessage(): String = "Now attacking with card with id $cardId!"
}

public data class StatusEffectInflictedMessage(val cardId: Int, val statusEffectName: String,
                                        val statusEffectDuration: Int, val potency: Int, val newEffect: Boolean = true): Message() {
    override fun toStringMessage(): String {
        return if (newEffect) {
            "Card with id $cardId now has $statusEffectDuration $statusEffectName (Potency level $potency)."
        } else {
            "Card with id $cardId's $statusEffectName ticks to $statusEffectDuration."
        }
    }
}