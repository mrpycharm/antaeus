package io.pleo.antaeus.rest

import io.javalin.Context
import io.pleo.antaeus.app.logger

internal fun logRequest(ctx: Context, ms: Float): Unit {
    logger.info("${ctx.method()} ${ctx.path()} -> (took ${ms} ms)")
}