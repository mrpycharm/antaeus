package io.pleo.antaeus.core.services

import io.pleo.antaeus.core.external.PaymentProvider
import io.pleo.antaeus.data.InvoiceDal
import io.pleo.antaeus.models.Invoice
import io.pleo.antaeus.models.InvoiceStatus
import io.pleo.antaeus.core.exceptions.CurrencyMismatchException
import io.pleo.antaeus.core.exceptions.CustomerNotFoundException
import io.pleo.antaeus.core.exceptions.NetworkException


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
        payInvoices(InvoiceStatus.PENDING)
    }

    /* retryInvoices will be responsible for retrying all the invoices that failed
     * due to any network or other kind of unexpected error.
     */
    fun retryInvoices() {
        payInvoices(InvoiceStatus.RETRY)
    }

    private fun payInvoices(status: InvoiceStatus) {
        // TODO: add delay between mapping
        invoiceDal.fetchInvoicesByStatus(status).map {
            processInvoice(it)
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
    private fun processInvoice(invoice: Invoice) {
        try {
            var paid = paymentProvider.charge(invoice)
            when(paid) {
                true -> invoiceDal.setInvoiceStatus(invoice.id, InvoiceStatus.PAID)
                false -> invoiceDal.setInvoiceStatus(invoice.id, InvoiceStatus.FAILED)
            }

        } catch (e: Exception) {
            // TODO: log exception
            when(e) {
                is CustomerNotFoundException, is CurrencyMismatchException -> {
                    invoiceDal.setInvoiceStatus(invoice.id, InvoiceStatus.FAILED)
                }
                is NetworkException, is Exception -> {
                    invoiceDal.setInvoiceStatus(invoice.id, InvoiceStatus.RETRY)
                }
            }
        }
    }
}