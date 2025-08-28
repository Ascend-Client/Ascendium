package io.github.betterclient.ascendium.event

import io.github.betterclient.ascendium.BridgeRenderer
import io.github.betterclient.ascendium.EntityBridge
import io.github.betterclient.ascendium.Pos3D
import io.github.betterclient.ascendium.TextBridge
import io.github.betterclient.ascendium.minecraft
import java.util.concurrent.atomic.AtomicReference


class RenderHudEvent(val renderer: BridgeRenderer) : Event()
class MouseClickEvent(val button: Int, val pressed: Boolean) : Event()
class ChatEvent(val text: TextBridge): Event()
class EntityHitEvent(val attacker: EntityBridge, val receiver: EntityBridge) : Event() {
    val distance = getAttackDistance()

    fun getAttackDistance(): Double {
        val camera = attacker.getCameraPosVec(1)
        val rotation = attacker.getRotationVec(1)

        var maxPos = receiver.getPos()
        val max: AtomicReference<Double> = AtomicReference(0.0)

        maxPos = compareTo(camera, maxPos.add(0.0, 0.0, receiver.getBox().end.z), max)
        maxPos = compareTo(camera, maxPos.add(0.0, 0.0, receiver.getBox().start.z), max)
        maxPos = compareTo(camera, maxPos.add(0.0, receiver.getBox().end.y, 0.0), max)
        maxPos = compareTo(camera, maxPos.add(0.0, receiver.getBox().start.y, 0.0), max)
        maxPos = compareTo(camera, maxPos.add(receiver.getBox().end.x, 0.0, 0.0), max)
        compareTo(camera, maxPos.add(receiver.getBox().end.x, 0.0, 0.0), max)

        val d: Double = max.get() + 0.5
        val possibleHits = camera.add(rotation.x * d, rotation.y * d, rotation.z * d)
        val box = attacker.getBox().stretch(rotation.multiply(d)).expand(1.0, 1.0, 1.0)

        val raycast = minecraft
            .raycast(attacker, camera, possibleHits, box, receiver.getID(), d)
        if (raycast == null || raycast.entity == null) {
            return -1.0
        }
        return camera.distanceTo(raycast.pos)
    }

    private fun compareTo(compare: Pos3D, test: Pos3D, max: AtomicReference<Double>): Pos3D {
        val dist = compare.distanceTo(test)
        if (dist > max.get()) {
            max.set(dist)
            return test
        }
        return compare
    }
}