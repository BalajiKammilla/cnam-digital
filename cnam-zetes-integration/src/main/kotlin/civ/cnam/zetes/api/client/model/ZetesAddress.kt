package civ.cnam.zetes.api.client.model

import org.w3c.dom.Element

class ZetesAddress(element: Element): ZetesXmlObject<ZetesAddress>(element) {
    fun subPrefecture(subPrefecture: String): ZetesAddress = text("subPrefecture", subPrefecture)
    fun locationCode(locationCode: String): ZetesAddress = text("locationCode", locationCode)
    fun city(city: String): ZetesAddress = text("city", city)
    fun street(street: String): ZetesAddress = text("street", street)
}
