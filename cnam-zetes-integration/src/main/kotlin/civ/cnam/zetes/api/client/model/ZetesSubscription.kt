package civ.cnam.zetes.api.client.model

import org.w3c.dom.Element

class ZetesSubscription(element: Element): ZetesXmlObject<ZetesSubscription>(element) {
    private val naturalPerson: ZetesNaturalPerson = ZetesNaturalPerson(child("naturalPerson"))

    fun paidBy(paidBy: String): ZetesSubscription = text("paidBy", paidBy)
    fun naturalPerson(mutator: ZetesNaturalPerson.() -> Unit): ZetesSubscription = mutate(naturalPerson, mutator)
}
