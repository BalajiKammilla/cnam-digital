package civ.cnam.zetes.api.client.model

import org.w3c.dom.Element

class ZetesProfessionalInformation(element: Element): ZetesXmlObject<ZetesProfessionalInformation>(element) {
    fun profession(profession: String?): ZetesProfessionalInformation =
        text("profession", profession ?: "")
    fun professionCode(professionCode: String?): ZetesProfessionalInformation =
        text("professionCode", professionCode ?: "")
    fun employerOrganisation(employerOrganisation: String?): ZetesProfessionalInformation =
        text("employerOrganisation", employerOrganisation ?: "")
    fun employerOrganisationCode(employerOrganisationCode: String?): ZetesProfessionalInformation =
        text("employerOrganisationCode", employerOrganisationCode ?: "")
    fun cnpsNumber(cnpsNumber: String?): ZetesProfessionalInformation =
        text("cnpsNumber", cnpsNumber ?: "")
    fun matriculeFP(matriculeFP: String?): ZetesProfessionalInformation =
        text("matriculeFP", matriculeFP ?: "")
    fun matriculeMilitary(matriculeMilitary: String?): ZetesProfessionalInformation =
        text("matriculeMilitary", matriculeMilitary ?: "")
}
