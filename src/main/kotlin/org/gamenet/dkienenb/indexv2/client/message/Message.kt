package org.gamenet.dkienenb.indexv2.client.message

enum class YesOrNoQuestionType {
    ANOTHER_CARD,
    TO_ATTACK,
    TO_USE_ALTERNATE_ATTACK,
}

abstract class Message {
    abstract fun toStringMessage(): String
}

data class DeckSizeMessage(val deckSize: Int): Message() {
    override fun toStringMessage(): String = "You have $deckSize cards in your deck."
}

data class MoneyRemainderMessage(val money: Int): Message() {
    override fun toStringMessage(): String = "You have $money money left."
}

data class PlayerInfoMessage(val playerId: Int, val playerName: String, val deckType: String, val deckSize: Int): Message() {
    override fun toStringMessage(): String = "Player info: #$playerId \"$playerName\" - $deckType, total card count $deckSize"
}

data class PlayerLossMessage(val playerId: Int): Message() {
    override fun toStringMessage(): String = "Player #$playerId is out of cards and has lost."
}

data class MoneyDieMessage(val money: Int): Message() {
    override fun toStringMessage(): String = "Money die result is $money."
}

data class CardPlayedMessage(val cardId: Int, val cardName: String,
                             val health: Int, val damage: Int, val defense: Int, val playerId: Int): Message() {
    override fun toStringMessage(): String = "Player #${playerId} plays a $cardName (Health: $health, " +
            "Damage: $damage, Defense $defense) with id $cardId."
}

data class CardDeathMessage(val cardId: Int): Message() {
    override fun toStringMessage(): String = "Card with id $cardId is no more."
}

data class CardHealthChangedMessage(val newHealth: Int, val cardId: Int): Message() {
    override fun toStringMessage(): String =
        "Card with id $cardId had a health change (resulting in $newHealth health)."
}

data class DrawCardMessage(val playerId: Int): Message() {
    override fun toStringMessage(): String = "Player #$playerId draws a card."
}

data class StealCardMessage(val aggressorId: Int, val victimId: Int): Message() {
    override fun toStringMessage(): String = "Player #${aggressorId} steals a card from ${victimId}'s deck!"
}

data class TurnStartMessage(val playerId: Int, val playerName: String): Message() {
    override fun toStringMessage(): String = "Turn start: Player #${playerId} (named $playerName)"

}

data class TurnEndMessage(val playerId: Int, val playerName: String): Message() {
    override fun toStringMessage(): String = "Turn end: Player #${playerId} (named $playerName)"
}

data class NowAttackingWithMessage(val cardId: Int): Message() {
    override fun toStringMessage(): String = "Now attacking with card with id $cardId!"
}