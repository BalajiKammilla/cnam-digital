package civ.cnam.api.context

import io.restassured.response.Response
import java.lang.ThreadLocal.withInitial
import kotlin.reflect.KClass

object ScenarioContext {
    private val threadLocal = withInitial<MutableMap<String, Any?>> { HashMap() }

    operator fun <T> get(name: String): T? {
        @Suppress("UNCHECKED_CAST")
        return threadLocal.get()[name] as T?
    }

    operator fun <T : Any> get(type: KClass<T>): T? {
        return get(type.java.name) as T?
    }

    operator fun <T: Any?> set(name: String, value: T): T {
        threadLocal.get()[name] = value
        return value
    }

    operator fun <T: Any> set(type: KClass<T>, value: T): T {
        threadLocal.get()[type.java.name] = value
        return value
    }

    var lastResponse: Response?
        get() = this[LAST_RESPONSE]
        set(lastResponse) {
            this[LAST_RESPONSE] = lastResponse
        }

    fun reset() {
        threadLocal.get().clear()
    }

    private const val LAST_RESPONSE = "LAST_RESPONSE"
}