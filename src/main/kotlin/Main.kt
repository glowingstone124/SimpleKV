package ind.glowingstone

import api.Http
import ind.glowingstone.core.Database
import kotlin.random.Random
import kotlin.system.measureTimeMillis

fun main() {
    val database = Database.getInstance()
    database.init()
    val http = Http()
    http.start()
}