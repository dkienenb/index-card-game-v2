package org.gamenet.indexv2

import org.gamenet.dkienenb.component.ComponentedObject
import org.gamenet.dkienenb.component.MapStoringComponent

class TagComponent : MapStoringComponent<Tag, Int>() {
    fun tag(tag: Tag) {
        val currentCount = value.getOrDefault(tag, 0)
        put(tag, currentCount + 1)
    }

    fun untag(tag: Tag) {
        val currentCount = value.getOrDefault(tag, 0)
        put(tag, currentCount - 1)
    }

    fun doPerTag(tag: Tag, action:(ComponentedObject) -> Unit) {
        val currentCount = value.getOrDefault(tag, 0)
        for (i: Int in 0 until currentCount) {
            action(attached)
        }
    }
}