package civ.cnam.enrolment.domain.model.entity

import civ.cnam.enrolment.domain.model.referencedata.PayerTypeCode

interface SubscriptionDetails {
    val paidByCode: PayerTypeCode
    val payer: NaturalPersonPayer?
}
