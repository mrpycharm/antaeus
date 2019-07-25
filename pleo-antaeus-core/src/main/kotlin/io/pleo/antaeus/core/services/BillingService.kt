package io.pleo.antaeus.core.services

import io.pleo.antaeus.core.external.PaymentProvider
import io.pleo.antaeus.data.InvoiceDal
import io.pleo.antaeus.models.Invoice
import io.pleo.antaeus.models.InvoiceStatus
import io.pleo.antaeus.core.exceptions.CurrencyMismatchException
import io.pleo.antaeus.core.exceptions.CustomerNotFoundException
import io.pleo.antaeus.core.exceptions.NetworkException
import io.pleo.antaeus.app.logger
import io.pleo.antaeus.models.InvoiceFailureReason
import kotlinx.coroutines.*

class BillingService(
    private val paymentProvider: PaymentProvider,
    private val invoiceDal: InvoiceDal
) {
    /* chargePendingInvoices will be responsible for performing the invoice payments.
     * This function will be scheduled as a job in the job scheduler.
     * Its going to get all the pending invoices from the database and pay them one by one.
     * One suggestion would be to add a little delay between charge calls for all the
     * pending invoices, to avoid bombarding the payment provider with hundreds and
     * thousands of requests in a second.
     */
    fun chargePendingInvoices() {
        logger.info("Charging pending invoices.")
        payInvoices(InvoiceStatus.PENDING)

        // TODO: generate a report and send an email to the admin
    }

    /* retryInvoices will be responsible for retrying all the invoices that failed
     * due to any network or other kind of unexpected error.
     */
    fun retryInvoices() {
        logger.info("retrying pending invoices.")
        payInvoices(InvoiceStatus.RETRY)
    }

    private fun payInvoices(status: InvoiceStatus) {
        runBlocking {
            invoiceDal.fetchInvoicesByStatus(status)
                    .map {
                        async { processInvoice(it) }
                    }
                    .map { it.await() }
        }
    }

    /* processInvoice is a private function that will process a single invoice
     * and call the payment provider's method to charge the customer. In case
     * of any network or unexpected error, we'll mark the status of invoice as
     * RETRY (however there could be a case when the payment provider has actually
     * debited the amount but returned an unexpected error. In this case a manual
     * settlement would be required - at least in Pakistan :) ). FAILED in case
     * the payment provider returns false.
     *
     * @param invoice the single invoice that needs to be processed.
     */
    private suspend fun processInvoice(invoice: Invoice) {
        logger.info("Processing invoice id: ${invoice.id}")

        try {
            var paid = paymentProvider.charge(invoice)
            when(paid) {
                true -> {
                    logger.info("Setting invoice status to PAID for ${invoice.id}")
                    invoiceDal.setInvoiceStatus(invoice.id, InvoiceStatus.PAID)
                }
                false -> {
                    logger.info("Setting invoice status to FAILED for ${invoice.id}")
                    invoiceDal.setInvoiceStatus(invoice.id, InvoiceStatus.FAILED,
                            InvoiceFailureReason.INSUFFICIENT_BALANCE)
                }
            }

        } catch (e: Exception) {
            logger.error("Error: ${e.message}")
            when(e) {
                is CustomerNotFoundException -> {
                    logger.info("Setting invoice status to FAILED for ${invoice.id}")
                    invoiceDal.setInvoiceStatus(invoice.id, InvoiceStatus.FAILED,
                            InvoiceFailureReason.CUSTOMER_NOT_FOUND)
                }
                is CurrencyMismatchException -> {
                    logger.info("Setting invoice status to FAILED for ${invoice.id}")
                    invoiceDal.setInvoiceStatus(invoice.id, InvoiceStatus.FAILED,
                            InvoiceFailureReason.CURRENCY_MISMATCH)
                }
                is NetworkException, is Exception -> {
                    logger.info("Setting invoice status to RETRY for ${invoice.id}")
                    invoiceDal.setInvoiceStatus(invoice.id, InvoiceStatus.RETRY)
                }
            }
        }
    }
}