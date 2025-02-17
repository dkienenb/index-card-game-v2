package org.gamenet.dkienenb.indexv2.server

class FightingCard(
    name: String,
    cost: Int,
    health: Int,
    damage: Int,
    defense: Int,
    ranged: Boolean,
    player: Player?,
    vararg tags: Tag
) : Card(name, cost) {
    init {
        addComponent(OriginalPlayerOwnedComponent(player))
        addComponent(MortalComponent())
        addComponent(MaxHealthComponent(health))
        addComponent(MinHealthComponent())
        addComponent(HealthComponent())
        addComponent(DefenseComponent(defense))
        addComponent(AttackerComponent(damage, ranged))
        val tagComponent = TagComponent()
        tags.forEach { tagComponent.tag(it) }
        addComponent(tagComponent)
        addComponent(TargetComponent())
        getComponent(MortalComponent::class.java).addDeathEffect {
            Main.sendAllExcept("${it.getComponent(NameComponent::class.java).getName()} is no more.", null)
        }
    }

    override fun play(player: Player): CardPlayResultLocation {
        getComponent(HealthComponent::class.java).setHealth(getComponent(MaxHealthComponent::class.java).getMaxHealth())
        getComponent(MortalComponent::class.java).revive()
        if (hasComponent(FlyingComponent::class.java)) {
            return CardPlayResultLocation.BATTLE_PLAYER_CHOICE
        }
        getComponent(MortalComponent::class.java).addDeathEffect {
            player.deck.getComponent(DeckComponent::class.java).discardCard(it as Card)
            player.removeFromPlay(it)
        }
        return CardPlayResultLocation.BATTLE_BACK
    }
}