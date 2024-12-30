package civ.cnam.zetes.api.client.model

import org.w3c.dom.Element

class ZetesPersonalInformation(element: Element): ZetesXmlObject<ZetesPersonalInformation>(element) {
    private val currentNationality: ZetesCountry = ZetesCountry(child("currentNationality"))
    private val phone: ZetesPhone = ZetesPhone(child("phones", "phone"))

    fun title(title: String): ZetesPersonalInformation = text("title", title)
    fun firstNames(firstNames: String): ZetesPersonalInformation = text("firstNames", firstNames)
    fun lastName(lastName: String): ZetesPersonalInformation = text("lastName", lastName)
    fun maidenName(maidenName: String?): ZetesPersonalInformation =
        text("maidenName", maidenName ?: "")
    fun gender(gender: String): ZetesPersonalInformation = text("gender", gender)
    fun currentNationality(mutator: ZetesCountry.() -> Unit): ZetesPersonalInformation =
        mutate(currentNationality, mutator)
    fun email(email: String?): ZetesPersonalInformation = text("email", email ?: "")
    fun phone(mutator: ZetesPhone.() -> Unit): ZetesPersonalInformation = mutate(phone, mutator)
    fun postOfficeBox(postOfficeBox: String?): ZetesPersonalInformation =
        text("postOfficeBox", postOfficeBox ?: "")
}
