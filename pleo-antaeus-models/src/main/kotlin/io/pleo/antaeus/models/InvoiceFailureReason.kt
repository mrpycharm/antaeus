package io.pleo.antaeus.models

enum class InvoiceFailureReason {
    INSUFFICIENT_BALANCE,
    CUSTOMER_NOT_FOUND,
    CURRENCY_MISMATCH,
    NONE
}