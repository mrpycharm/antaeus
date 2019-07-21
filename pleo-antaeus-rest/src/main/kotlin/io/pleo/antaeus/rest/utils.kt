package io.pleo.antaeus.rest

import io.javalin.Context

internal fun logRequest(ctx: Context, ms: Float): Unit {
    logger.info("${ctx.method()} ${ctx.path()} -> (took ${ms} ms)")
}