package api

import ind.glowingstone.core.Database
import org.http4k.core.*
import org.http4k.routing.bind
import org.http4k.routing.path
import org.http4k.routing.routes
import org.http4k.server.ApacheServer
import org.http4k.server.asServer
import permission.Auth

class Http {
    fun start() {
        app.asServer(ApacheServer(8080)).start()
    }
    private val auth = Auth()
    private val database = Database.getInstance()
    private val app: HttpHandler = routes(
        "/query/{key}" bind Method.GET to { request ->
            val key = request.path("key") ?: ""
            queryKey(key)
        },
        "/insert" bind Method.POST to { request ->
            val headers = request.headers
            val key = request.query("key")
            val value = request.query("value")
            insertKey(headers, key, value)
        }
    )

    private fun queryKey(key: String): Response {
        val result = database.get(key)
        return if (result.success) {
            Response(Status.OK).body(result.result.toString())
        } else {
            Response(Status.NOT_FOUND)
        }
    }

    private fun insertKey(headers: Headers, key: String?, value: String?): Response {
        val authHeader = headers.find { it.first == "auth" }
        if (key.isNullOrEmpty() || value.isNullOrEmpty()) {
            return Response(Status.BAD_REQUEST).body("INVALID REQUEST: 'key' and 'value' parameters are required")
        }
        if (authHeader == null || authHeader.second.isNullOrEmpty()) {
            return Response(Status.BAD_REQUEST).body("Missing or empty 'auth' header")
        }

        return if (auth.auth(authHeader.second!!)) {
            database.put(key, value)
            Response(Status.OK).body("Inserted key-value pair successfully")
        } else {
            Response(Status.UNAUTHORIZED).body("Invalid 'auth' header")
        }
    }
}
fun main() {
    val server = Http()
    server.start()
}
