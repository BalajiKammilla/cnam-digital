package civ.cnam.zetes.api.client.model

import civ.cnam.zetes.api.client.exception.ZetesException
import dev.dry.core.data.format.xml.Xml
import org.w3c.dom.Element

open class ZetesXmlObject<T: ZetesXmlObject<T>>(protected val objectRootElement: Element) {
    companion object {
        private fun child(parentElement: Element, childElementTagName: String): Element {
            return Xml.getFirstElementByTagName(parentElement, childElementTagName)
                ?: throw elementNotFound(childElementTagName)
        }

        private fun child(parentElement: Element, vararg childElementTagNamePath: String): Element {
            return Xml.getFirstElementByTagNamePath(parentElement, *childElementTagNamePath)
                ?: throw elementNotFound(childElementTagNamePath.joinToString("/"))
        }

        private fun elementNotFound(tagName: String): ZetesException {
            val objectClassName = this::class.java.simpleName
            return ZetesException("element with tag name '$tagName' not found for object type '$objectClassName'")
        }
    }

    @Suppress("UNCHECKED_CAST")
    protected fun text(childElementTagName: String, textContent: String): T {
        child(childElementTagName).textContent = textContent.trim()
        return this as T
    }

    @Suppress("UNCHECKED_CAST")
    protected fun text(childElementTagNamePath: Array<String>, textContent: String): T {
        child(*childElementTagNamePath).textContent = textContent.trim()
        return this as T
    }

    @Suppress("UNCHECKED_CAST")
    fun <V> mutate(value: V, mutator: (V) -> Unit): T {
        mutator(value)
        return this as T
    }

    fun child(childElementTagName: String): Element = child(objectRootElement, childElementTagName)

    fun child(vararg childElementTagNamePath: String): Element =
        child(objectRootElement, *childElementTagNamePath)

    fun child(childElementTagName: String, consumer: (Element) -> Unit): Unit =
        consumer(child(objectRootElement, childElementTagName))
}
