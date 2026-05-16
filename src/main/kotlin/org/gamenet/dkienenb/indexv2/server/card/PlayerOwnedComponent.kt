package org.gamenet.dkienenb.indexv2.server.card

import org.gamenet.dkienenb.component.MutableDataStoringComponent
import org.gamenet.dkienenb.indexv2.server.Player

public class PlayerOwnedComponent(player: Player) : MutableDataStoringComponent<Player>() {
    init {
        setPlayer(player)
    }

    fun getPlayer(): Player = getValue()
    fun setPlayer(player: Player) = setValue(player)
}
