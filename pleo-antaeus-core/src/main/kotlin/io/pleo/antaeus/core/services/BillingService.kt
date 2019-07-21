package io.pleo.antaeus.core.services

import io.pleo.antaeus.core.external.PaymentProvider

class BillingService(
    private val paymentProvider: PaymentProvider
) {
    /* payInvoices will be responsible for performing the invoice payments.
     * This function will be scheduled as a job in the job scheduler.
     * Its going to get all the pending invoices from the database and pay them one by one.
     * TODO - exception and error handling to be added for this function
     */
    fun payInvoices() {}
}