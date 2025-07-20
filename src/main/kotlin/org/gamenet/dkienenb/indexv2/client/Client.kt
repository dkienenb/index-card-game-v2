package org.gamenet.dkienenb.indexv2.client

import org.gamenet.dkienenb.indexv2.client.message.Message
import org.gamenet.dkienenb.indexv2.client.message.YesOrNoQuestionType

abstract class Client {
    abstract fun displayMessage(message: String)
    abstract fun checkIfPlayerWants(questionType: YesOrNoQuestionType): Boolean
    abstract fun makeChoice(choiceLabel: String, options: Set<String>): String
    abstract fun getName(): String
    abstract fun displayMessage(message: Message)
}
