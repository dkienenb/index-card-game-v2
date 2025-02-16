package org.gamenet.dkienenb.indexv2.client

import kotlin.random.Random

class RandomDecisionsAIClient(val displayName: String) : Client() {

    override fun displayMessage(message: String) {}

    override fun checkIfPlayerWants(message: String, additionalData: Map<String, String>): Boolean = Random.nextBoolean()

    override fun makeChoice(choiceLabel: String, options: Set<String>): String = options.random()

    override fun getName(): String = displayName
}