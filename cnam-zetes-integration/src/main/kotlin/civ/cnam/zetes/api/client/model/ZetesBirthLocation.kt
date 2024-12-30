package civ.cnam.zetes.api.client.model

import org.w3c.dom.Element

class ZetesBirthLocation(element: Element): ZetesXmlObject<ZetesBirthLocation>(element) {
    private val country: ZetesCountry = ZetesCountry(child("country"))

    fun subPrefecture(subPrefecture: String): ZetesBirthLocation = text("subPrefecture", subPrefecture)
    fun city(city: String): ZetesBirthLocation = text("city", city)
    fun locationCode(locationCode: String): ZetesBirthLocation = text("locationCode", locationCode)
    fun country(mutator: ZetesCountry.() -> Unit): ZetesBirthLocation {
        mutator(country)
        return this
    }
}
