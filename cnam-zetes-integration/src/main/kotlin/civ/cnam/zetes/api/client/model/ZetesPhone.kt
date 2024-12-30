package civ.cnam.zetes.api.client.model

import org.w3c.dom.Element

class ZetesPhone(element: Element): ZetesXmlObject<ZetesPhone>(element) {
    fun number(number: String): ZetesPhone = text("number", number)
    fun type(type: String): ZetesPhone = text("type", type)
}
