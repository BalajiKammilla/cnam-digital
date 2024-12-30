package civ.cnam.enrolment.adaptor.model.repository

import civ.cnam.enrolment.domain.model.repository.EnrolmentRepository
import io.quarkus.test.common.QuarkusTestResource
import io.quarkus.test.junit.QuarkusTest
import jakarta.inject.Inject

@QuarkusTest
@QuarkusTestResource(PostgresTestResource::class)
class EnrolmentRepositoryTestJpa : AbstractEnrolmentRepositoryTest() {
    @Inject
    lateinit var repository: EnrolmentRepository

    override val unit: EnrolmentRepository get() = repository
}
