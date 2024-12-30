package civ.cnam.zetes.api.client.model

import dev.dry.common.exception.SystemRuntimeException
import dev.dry.common.io.resource.Resource
import dev.dry.core.data.format.xml.Xml
import org.w3c.dom.Document

open class ZetesXmlRootObject<T: ZetesXmlRootObject<T>>(
    private val document: Document
): ZetesXmlObject<T>(document.documentElement) {
    companion object {
        private val xmlTemplateByResourceName = mutableMapOf<String, String>()

        @JvmStatic
        protected fun <T: ZetesXmlRootObject<T>> newInstance(
            xmlTemplateResource: Resource,
            construct: (document: Document) -> T
        ): T {
            val xmlTemplate = xmlTemplateByResourceName.computeIfAbsent(xmlTemplateResource.name) {
                xmlTemplateResource.readText(Charsets.UTF_8)
                    ?: throw SystemRuntimeException(
                        "failed to load zetes XML object from resource '${xmlTemplateResource.name}'"
                    )
            }
            val document = Xml.Transform.toDocument(xmlTemplate)
            document.normalizeDocument()
            return construct(document)
        }
    }

    fun toXmlString(): String = Xml.Transform.toString(document)
}
