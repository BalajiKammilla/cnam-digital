package civ.cnam.enrolment.domain.model.type.enrolment.completed

import civ.cnam.enrolment.domain.model.referencedata.PayerType

class SubscriptionDetails(
    val paidBy: PayerType,
    val payer: NaturalPersonPayer?,
)
