package org.gamenet.dkienenb.indexv2.server

import org.gamenet.dkienenb.event.Event
import org.gamenet.dkienenb.indexv2.client.message.Message

public class MultiSendEventOld(val message: String, val exception: Player?) : Event()

public class MultiSendEventNew(val message: Message) : Event()