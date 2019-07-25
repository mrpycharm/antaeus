package io.pleo.antaeus.scheduler

import io.pleo.antaeus.core.services.BillingService
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*
import java.sql.Date
import kotlin.concurrent.fixedRateTimer

class BillingScheduler : IScheduler{
    private val initialDelay = 30_000L
    private val schedulePeriod = 86400000L

    override fun start(billingService: BillingService): Timer {
        return fixedRateTimer("billing scheduler",
                daemon = false,
                initialDelay = initialDelay,
                period = schedulePeriod
        ) {
            if (LocalDateTime.now().dayOfMonth == 26) {
                logger.info("Time to run the billing job.")
                billingService.chargePendingInvoices()
                logger.info("Billing job completed")
            }
        }
    }
}