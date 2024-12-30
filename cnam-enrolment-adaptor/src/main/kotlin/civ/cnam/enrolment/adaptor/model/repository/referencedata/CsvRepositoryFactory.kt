package civ.cnam.enrolment.adaptor.model.repository.referencedata

import java.io.InputStream
import kotlin.reflect.KClass
import kotlin.reflect.full.isSubclassOf

object CsvRepositoryFactory {
    fun <T: Any> constructCsvRepository(csvRepositoryClass: KClass<T>): T {
        val packagePath = csvRepositoryClass.java.packageName.replace(".", "/")
        val className = csvRepositoryClass.java.simpleName
            ?: throw RuntimeException("failed to derive class name for CSV repository")
        val csvFileName = className
            .replace("Csv", "")
            .replace("Repository", "")
        val constructor = csvRepositoryClass.constructors.find {
            it.parameters.size == 1 &&
                    (it.parameters[0].type.classifier as KClass<*>).isSubclassOf(InputStream::class)
        } ?: throw RuntimeException("failed to find constructor (InputStream) for CSV repository '$className'")

        val csvResourcePath = "/$packagePath/$csvFileName.csv"

        // lazy-loading
        val csvInputStream = csvRepositoryClass.java.getResourceAsStream(csvResourcePath)
            ?: throw RuntimeException("failed to load CSV resource '$csvResourcePath' for repository '$className'")

        return csvInputStream.use { constructor.call(it) }
    }

    inline fun <reified T: Any> constructCsvRepository(): T = constructCsvRepository(T::class)
}
