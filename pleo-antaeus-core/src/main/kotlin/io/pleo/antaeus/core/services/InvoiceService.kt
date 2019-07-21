/*
    Implements endpoints related to invoices.
 */

package io.pleo.antaeus.core.services

import io.pleo.antaeus.core.exceptions.InvoiceNotFoundException
import io.pleo.antaeus.data.AntaeusDal
import io.pleo.antaeus.models.Invoice

class InvoiceService(private val dal: AntaeusDal) {
    fun fetchAll(): List<Invoice> {
       return dal.invoiceDal.fetchInvoices()
    }

    fun fetch(id: Int): Invoice {
        return dal.invoiceDal.fetchInvoice(id) ?: throw InvoiceNotFoundException(id)
    }
}
