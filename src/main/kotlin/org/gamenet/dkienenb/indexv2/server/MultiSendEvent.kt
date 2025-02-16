package org.gamenet.dkienenb.indexv2.server

import org.gamenet.dkienenb.event.Event

class MultiSendEvent(val message: String, val exception: Player?) : Event()