package civ.cnam.zetes.api.client.model

import org.w3c.dom.Element
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class ZetesRequest(element: Element): ZetesXmlObject<ZetesRequest>(element) {
    companion object {
        private val DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        private const val ID_TAG = "id"
        private const val DATE_TAG = "date"
        private const val OPERATOR_TAG = "operator"
        private const val ENROLMENT_KIT_ID_TAG = "enrolmentKitId"
        private const val ENROLMENT_SITE_CODE_TAG = "enrolmentSiteCode"
        private const val ENROLMENT_SITE_TAG = "enrolmentSite"
        private const val AGENT_CODE_TAG = "cnamAgentCode"
    }

    val requestIdentifier: String get() = child(ID_TAG).textContent

    val enrolmentSiteCode: String get() = child(ENROLMENT_SITE_CODE_TAG).textContent

    fun id(id: String): ZetesRequest = text(ID_TAG, id)
    fun date(date: LocalDateTime): ZetesRequest = text(DATE_TAG, DATE_TIME_FORMATTER.format(date))
    fun operator(operator: String): ZetesRequest = text(OPERATOR_TAG, operator)
    fun enrolmentKitId(enrolmentKitId: String): ZetesRequest =
        text(ENROLMENT_KIT_ID_TAG, enrolmentKitId)
    fun enrolmentSiteCode(enrolmentSite: ZetesEnrolmentSite): ZetesRequest =
        text(ENROLMENT_SITE_CODE_TAG, enrolmentSite.agencyCode.value)
    fun enrolmentSite(enrolmentSite: ZetesEnrolmentSite): ZetesRequest =
        text(ENROLMENT_SITE_TAG, enrolmentSite.toString())
    fun cnamAgentCode(agentCode: String): ZetesRequest = text(AGENT_CODE_TAG, agentCode)
}
