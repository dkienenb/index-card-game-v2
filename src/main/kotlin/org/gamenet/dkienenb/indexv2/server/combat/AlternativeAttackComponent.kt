package org.gamenet.dkienenb.indexv2.server.combat

import org.gamenet.dkienenb.component.ImmutableDataStoringComponent

class AlternativeAttackComponent(altAttack: (() -> Unit), val altAttackAvailable: (() -> Boolean)) : ImmutableDataStoringComponent<()->Unit>(altAttack) {

    fun doAlternativeAttack() {
        value()
    }

    fun alternativeAttackAvailible(): Boolean = altAttackAvailable()
}