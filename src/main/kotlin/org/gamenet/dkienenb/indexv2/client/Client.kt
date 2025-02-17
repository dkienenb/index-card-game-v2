package org.gamenet.dkienenb.indexv2.client

abstract class Client {
    abstract fun displayMessage(message: String)
    abstract fun checkIfPlayerWants(message: String, additionalData: Map<String, String>): Boolean
    abstract fun makeChoice(choiceLabel: String, options: Set<String>): String
    abstract fun getName(): String
}
