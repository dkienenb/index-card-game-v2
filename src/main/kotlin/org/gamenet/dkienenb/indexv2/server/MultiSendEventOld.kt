package org.gamenet.dkienenb.indexv2.server

import org.gamenet.dkienenb.event.Event
import org.gamenet.dkienenb.indexv2.client.message.Message

class MultiSendEventOld(val message: String, val exception: Player?) : Event()

class MultiSendEventNew(val message: Message, val exception: Player?) : Event()