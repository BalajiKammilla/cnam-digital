package civ.cnam.zetes.api.client.model

import org.w3c.dom.Element

class ZetesNaturalPerson(element: Element): ZetesXmlObject<ZetesNaturalPerson>(element) {
    fun firstName(firstName: String): ZetesNaturalPerson = text("firstName", firstName)
    fun lastname(lastname: String): ZetesNaturalPerson = text("lastname", lastname)
    fun enrolmentId(enrolmentId: String): ZetesNaturalPerson = text("enrolmentId", enrolmentId)
}
