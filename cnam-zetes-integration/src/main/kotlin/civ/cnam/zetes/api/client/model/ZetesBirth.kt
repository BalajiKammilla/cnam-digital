package civ.cnam.zetes.api.client.model

import org.w3c.dom.Element
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class ZetesBirth(element: Element): ZetesXmlObject<ZetesBirth>(element) {
    companion object {
        private val DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy")
    }

    private val location: ZetesBirthLocation = ZetesBirthLocation(child("location"))

    fun date(date: LocalDate): ZetesBirth = text("date", DATE_FORMATTER.format(date))
    fun extractNumber(extractNumber: String): ZetesBirth = text("extractNumber", extractNumber)
    fun extractIssuingDate(extractIssuingDate: LocalDate): ZetesBirth =
        text("extractIssuingDate", DATE_FORMATTER.format(extractIssuingDate))
    fun location(mutator: ZetesBirthLocation.() -> Unit): ZetesBirth {
        mutator(location)
        return this
    }
}
