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
    }

    override fun play(player: Player): CardPlayResultLocation {
        getComponent(HealthComponent::class.java).setHealth(getComponent(MaxHealthComponent::class.java).getMaxHealth())
        getComponent(MortalComponent::class.java).clearOnDeathEffects()
        getComponent(MortalComponent::class.java).revive()
        getComponent(MortalComponent::class.java).addDeathEffect {
            player.deck.getComponent(DeckComponent::class.java).discardCard(it as Card)
            player.removeFromPlay(it)
        }
        if (hasComponent(FlyingComponent::class.java)) {
            return CardPlayResultLocation.BATTLE_PLAYER_CHOICE
        }
        return CardPlayResultLocation.BATTLE_BACK
    }
}