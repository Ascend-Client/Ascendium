package io.github.betterclient.ascendium.util

import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

class MutableLazy<T>(private val initializer: () -> T) : ReadWriteProperty<Any?, T> {
    private object UNINITIALIZED
    @Volatile private var cachedValue: Any? = UNINITIALIZED

    override fun getValue(thisRef: Any?, property: KProperty<*>): T {
        if (cachedValue === UNINITIALIZED) {
            cachedValue = initializer()
        }

        @Suppress("UNCHECKED_CAST")
        return cachedValue as T
    }

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        cachedValue = value
    }
}

fun <T> mutableLazy(initializer: () -> T) = MutableLazy(initializer)