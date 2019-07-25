package io.pleo.antaeus.scheduler

import io.pleo.antaeus.core.services.BillingService
import mu.KotlinLogging
import java.util.*

// Scheduler logger
val logger = KotlinLogging.logger {}

class Scheduler (
        private val billingService: BillingService
) {
    fun init() {
        // init billing scheduler
        logger.info("Initializing billing schedulers.")
        val billingScheduler = BillingScheduler()
        billingScheduler.start(billingService)
        logger.info("Billing schedulers initialized.")

        logger.info("Initializing billing retry schedulers.")
        val billingRetryScheduler = BillingRetryScheduler()
        billingRetryScheduler.start(billingService)
        logger.info("Billing retry schedulers initialized.")

    }
}

interface IScheduler {
    fun start(billingService: BillingService): Timer
}