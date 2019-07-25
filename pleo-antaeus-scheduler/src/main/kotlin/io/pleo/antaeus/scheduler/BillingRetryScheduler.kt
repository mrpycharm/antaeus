package io.pleo.antaeus.scheduler

import io.pleo.antaeus.core.services.BillingService
import java.time.LocalDateTime
import java.util.*
import kotlin.concurrent.fixedRateTimer

class BillingRetryScheduler : IScheduler {
    private val initialDelay = 30_000L
    private val schedulePeriod = 300_000L

    override fun start(billingService: BillingService): Timer {
        return fixedRateTimer("billing scheduler",
                daemon = false,
                initialDelay = initialDelay,
                period = schedulePeriod
        ) {
            logger.info("Time to run the retry billing job.")
            billingService.retryInvoices()
            logger.info("Retry billing job completed")
        }
    }
}