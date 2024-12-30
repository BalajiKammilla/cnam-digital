package civ.cnam.zetes.api.model

import civ.cnam.enrolment.domain.model.referencedata.AgencyCode
import civ.cnam.enrolment.domain.model.referencedata.DocumentTypeCode
import civ.cnam.enrolment.domain.model.referencedata.SubPrefectureCode
import civ.cnam.enrolment.domain.model.value.EnrolmentId
import civ.cnam.enrolment.domain.model.value.attachment.DocumentAttachmentId
import civ.cnam.zetes.api.client.model.ZetesEnrolmentSite
import civ.cnam.zetes.api.client.model.ZetesSupportingDocumentPage
import dev.dry.common.io.encoding.Base64
import dev.dry.core.data.format.xml.Xml
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.util.*

class ZetesSupportingDocumentPageTest : BaseXmlTest() {
    @Test
    fun test() {
        val enrolmentId = EnrolmentId.generate()
        val documentId = DocumentAttachmentId(UUID.randomUUID().toString())
        val documentTypeCode = DocumentTypeCode("PAS")
        val enrollmentSite = ZetesEnrolmentSite(
            subPrefectureCode = SubPrefectureCode("BAKO"),
            agencyLabel = "BOUAKE 01,AUBERGE DE LA JEUNESSE",
            agencyCode = AgencyCode("BOKE01"),
        )
        val documentBytes = "123".toByteArray()
        val documentBase64 = Base64.encodeToString(documentBytes)
        val document = ZetesSupportingDocumentPage.newInstance()
            .requestUuid(enrolmentId)
            .documentId(documentId.value)
            .documentType(documentTypeCode)
            .enrollmentSite(enrollmentSite)
            .documentBody(documentBytes)
        val documentXml = document.toXmlString()
        val rootElement = Xml.Transform.toDocument(documentXml).documentElement

        assertEquals(enrolmentId.value, childText(rootElement, ZetesSupportingDocumentPage.REQUEST_UUID))
        assertEquals(documentId.value, childText(rootElement, ZetesSupportingDocumentPage.DOCUMENT_ID))
        assertEquals(documentTypeCode.value, childText(rootElement, ZetesSupportingDocumentPage.DOCUMENT_TYPE))
        assertEquals(enrollmentSite.toString(), childText(rootElement, ZetesSupportingDocumentPage.ENROLMENT_SITE))
        assertEquals(documentBase64, childText(rootElement, ZetesSupportingDocumentPage.DOCUMENT_BODY))
    }
}
