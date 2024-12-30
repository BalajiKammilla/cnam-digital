package civ.cnam.zetes.api.client.model

import org.w3c.dom.Element

class ZetesCountry(element: Element): ZetesXmlObject<ZetesCountry>(element) {
    fun iso31661a3(iso31661a3: String): ZetesCountry = text("iso31661a3", iso31661a3)
    fun label(label: String): ZetesCountry = text("label", label)
}
