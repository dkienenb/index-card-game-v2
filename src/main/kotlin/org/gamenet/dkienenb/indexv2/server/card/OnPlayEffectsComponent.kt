package org.gamenet.dkienenb.indexv2.server.card

import org.gamenet.dkienenb.component.ComponentedObject
import org.gamenet.dkienenb.component.ListStoringComponent
import org.gamenet.dkienenb.indexv2.server.Player
import java.util.stream.Stream

class OnPlayEffectsComponent : ListStoringComponent<(ComponentedObject, Player, CardPlayResultLocation) -> CardPlayResultLocation>() {
    fun addOnPlayEffect(effect: (ComponentedObject, Player, CardPlayResultLocation) -> CardPlayResultLocation) {
        value.add(effect)
    }

    fun streamEffects(): Stream<(ComponentedObject, Player, CardPlayResultLocation) -> CardPlayResultLocation> = stream()
}