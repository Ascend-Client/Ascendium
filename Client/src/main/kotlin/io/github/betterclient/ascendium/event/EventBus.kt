package io.github.betterclient.ascendium.event

import java.lang.reflect.Method
import java.lang.reflect.Modifier

//choo choo or whatever buses say
object EventBus {
    internal val subscribers = mutableListOf<MethodCaller>()

    fun subscribe(subscriber: Any) {
        unsubscribe(subscriber) //unsubscribe first so double events don't happen

        for (method in subscriber.javaClass.methods) {
            if (method.isAnnotationPresent(EventTarget::class.java) && method.parameterCount == 1) {
                val type = method.parameterTypes[0]

                if ((type.superclass != null && type.superclass == Event::class.java) || type == Event::class.java) {
                    subscribers.add(MethodCaller(method, if (Modifier.isStatic(method.modifiers)) {
                        null
                    } else {
                        subscriber
                    }, subscriber.javaClass))
                }
            }
        }
    }

    fun unsubscribe(subscriber: Any?) {
        if (subscriber == null) {
            //re-call with the caller class
            getCallerClass()?.let {
                unsubscribe(it)
            }
            return
        }

        subscribers.removeIf {
            it.clazz == (subscriber as? Class<*> ?: subscriber.javaClass)
        }
    }
}

val Any.eventBus: AnyEventBus
    get() {
        return AnyEventBus(this)
    }

class AnyEventBus(val obj: Any) {
    fun subscribe() = EventBus.subscribe(obj)
    fun unsubscribe() = EventBus.unsubscribe(obj)
}

@Target(AnnotationTarget.FUNCTION)
annotation class EventTarget

open class Event {
    fun broadcast() {
        EventBus.subscribers.forEach {
            if (it.method.parameters[0].type.isInstance(this)) {
                it.method.invoke(it.instance, this)
            }
        }
    }
}

internal data class MethodCaller(val method: Method, val instance: Any?, val clazz: Class<*>)

internal fun getCallerClass(): Class<*>? {
    val stackTrace = Thread.currentThread().stackTrace
    for (i in stackTrace.indices) {
        if (stackTrace[i].className == Thread::class.java.name &&
            i + 3 < stackTrace.size) {
            return Class.forName(stackTrace[i + 3].className)
        }
    }
    return null
}