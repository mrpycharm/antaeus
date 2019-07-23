package io.pleo.antaeus.data


import io.pleo.antaeus.models.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction


// Data Access Layer for Invoices
class InvoiceDal (private val db: Database) {
    fun fetchInvoice(id: Int): Invoice? {
        // transaction(db) runs the internal query as a new database transaction.
        return transaction(db) {
            // Returns the first invoice with matching id.
            InvoiceTable
                    .select { InvoiceTable.id.eq(id) }
                    .firstOrNull()
                    ?.toInvoice()
        }
    }

    fun fetchInvoices(): List<Invoice> {
        return transaction(db) {
            InvoiceTable
                    .selectAll()
                    .map { it.toInvoice() }
        }
    }

    fun fetchInvoicesByStatus(status: InvoiceStatus): List<Invoice> {
        return transaction(db) {
            InvoiceTable
                    .select { InvoiceTable.status.eq(status.toString()) }
                    .map { it.toInvoice() }
        }
    }

    fun createInvoice(amount: Money, customer: Customer, status: InvoiceStatus = InvoiceStatus.PENDING): Invoice? {
        val id = transaction(db) {
            // Insert the invoice and returns its new id.
            InvoiceTable
                    .insert {
                        it[this.value] = amount.value
                        it[this.currency] = amount.currency.toString()
                        it[this.status] = status.toString()
                        it[this.customerId] = customer.id
                        it[this.failureReason] = InvoiceFailureReason.NONE.toString()
                    } get InvoiceTable.id
        }

        return fetchInvoice(id!!)
    }

    fun setInvoiceStatus(id: Int, status: InvoiceStatus,
                         failureReason: InvoiceFailureReason = InvoiceFailureReason.NONE) {

        transaction(db) {
            InvoiceTable
                    .update({ InvoiceTable.id.eq(id) }) {
                        it[this.status] = status.toString()
                        it[this.failureReason] = failureReason.toString()
                    }
        }
    }
}