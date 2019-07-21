/*
    Implements the data access layer (DAL).
    This file implements the database queries used to fetch and insert rows in our database tables.

    See the `mappings` module for the conversions between database rows and Kotlin objects.
 */

package io.pleo.antaeus.data

import io.pleo.antaeus.models.Currency
import io.pleo.antaeus.models.Customer
import io.pleo.antaeus.models.Invoice
import io.pleo.antaeus.models.InvoiceStatus
import io.pleo.antaeus.models.Money
import io.pleo.antaeus.data.InvoiceDal
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction


class AntaeusDal(private val db: Database) {
    val invoiceDal = InvoiceDal(db)
    val customerDal = CustomerDal(db)
}
