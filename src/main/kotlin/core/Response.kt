package core

class Response {
    data class QueryResponse(
        val result: Any?,
        val success: Boolean
    )
}