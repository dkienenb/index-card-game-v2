package org.gamenet.dkienenb.indexv2.server.card

import org.gamenet.dkienenb.component.ImmutableDataStoringComponent
import org.gamenet.dkienenb.indexv2.server.Player

class OriginalPlayerOwnedComponent(player: Player?) : ImmutableDataStoringComponent<Player>(player) {
    fun getPlayer(): Player = value
}
