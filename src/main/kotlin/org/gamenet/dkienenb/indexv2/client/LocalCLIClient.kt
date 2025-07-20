package org.gamenet.dkienenb.indexv2.client

import org.gamenet.dkienenb.indexv2.client.message.Message
import org.gamenet.dkienenb.indexv2.client.message.YesOrNoQuestionType
import java.util.Scanner

class LocalCLIClient : Client() {

    private val scanner = Scanner(System.`in`)

    private fun <T> choice(label: String, things: List<T>): T = choiceButWithAPrompt("Choose a $label:", things)

    private fun <T> choiceButWithAPrompt(prompt: String, things: List<T>): T {
        while (true) {
            displayMessage(prompt)
            things.forEachIndexed { index, thing ->
                println("${index + 1}. $thing")
            }
            print("Enter the number of your choice: ")

            val input = scanner.nextLine().trim()
            val choice = input.toIntOrNull()

            if (choice != null && choice in 1..things.size) {
                return things[choice - 1]
            }
            displayMessage("Invalid input. Please enter a number between 1 and ${things.size}.")
        }
    }

    override fun displayMessage(message: String) {
        println(message)
    }

    override fun displayMessage(message: Message) {
        displayMessage(message.toStringMessage())
    }

    override fun checkIfPlayerWants(questionType: YesOrNoQuestionType): Boolean =
        choiceButWithAPrompt("Do you want $questionType?", listOf("Yes", "No")) == "Yes"

    override fun makeChoice(choiceLabel: String, options: Set<String>): String =
        choice(choiceLabel, options.toList())

    override fun getName(): String = "localhost"
}
