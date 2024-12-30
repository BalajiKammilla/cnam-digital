package civ.cnam.zetes.api.model

import dev.dry.core.data.format.xml.Xml
import org.junit.jupiter.api.Assertions
import org.w3c.dom.Element

open class BaseXmlTest {
    companion object {
        fun child(parent: Element, tagName: String): Element {
            val child = Xml.getFirstElementByTagName(parent, tagName)
            Assertions.assertNotNull(child)
            return child!!
        }

        fun childText(parent: Element, tagName: String): String = child(parent, tagName).textContent
    }
}