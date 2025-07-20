package org.gamenet.dkienenb.indexv2.client

import org.gamenet.dkienenb.indexv2.client.message.DeckSizeMessage
import org.gamenet.dkienenb.indexv2.client.message.Message
import org.gamenet.dkienenb.indexv2.client.message.YesOrNoQuestionType
import kotlin.random.Random

class RandomDecisionsAIClient(val displayName: String) : Client() {

    var outOfCards = false

    override fun displayMessage(message: String) {}
    override fun displayMessage(message: Message) {
        if (message is DeckSizeMessage) {
            if (message.deckSize < 2) {
                outOfCards = true
            }
        }
    }

    override fun checkIfPlayerWants(questionType: YesOrNoQuestionType): Boolean {
        when (questionType) {
            YesOrNoQuestionType.ANOTHER_CARD -> if (outOfCards) {
                return false
            }
            YesOrNoQuestionType.TO_ATTACK -> return true
        }
        return Random.nextBoolean()
    }

    override fun makeChoice(choiceLabel: String, options: Set<String>): String = options.random()

    override fun getName(): String = displayName
}
