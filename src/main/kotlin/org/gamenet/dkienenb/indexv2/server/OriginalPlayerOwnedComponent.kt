package org.gamenet.dkienenb.indexv2.server

import org.gamenet.dkienenb.component.ImmutableDataStoringComponent

class OriginalPlayerOwnedComponent(player: Player?) : ImmutableDataStoringComponent<Player>(player) {
    fun getPlayer(): Player = value
}
