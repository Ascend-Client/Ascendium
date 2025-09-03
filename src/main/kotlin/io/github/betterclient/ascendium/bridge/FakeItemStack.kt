package io.github.betterclient.ascendium.bridge

class FakeItemStack(
    override val itemIdentifier: IdentifierBridge,
    override val itemCount: Int,
    override val durability: Float
) : ItemStackBridge