package ind.glowingstone.core

import AnySerializer
import core.Response
import kotlinx.serialization.builtins.MapSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.cbor.Cbor
import kotlinx.serialization.decodeFromByteArray
import kotlinx.serialization.encodeToByteArray
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.contextual
import java.io.File

class Database private constructor() {
    fun init() {
        getFromFile()
        Runtime.getRuntime().addShutdownHook(Thread {
            println("Saving database")
            saveToFile()
            println("Complete")
        })
    }
    companion object {
        private var instance: Database? = null

        @Synchronized
        fun getInstance(): Database {
            if (instance == null) {
                instance = Database()
            }
            return instance!!
        }
    }

    private val serializersModule = SerializersModule {
        contextual(AnySerializer)
    }

    private val cbor = Cbor { encodeDefaults = true; serializersModule = this@Database.serializersModule }

    private val databaseLocation = "database"

    val KVMap: HashMap<String, Any> = HashMap()

    fun getFromFile() {
        val file = File(databaseLocation)
        if (!file.exists()) {
            file.createNewFile()
            println("No database found. Creating default...")
            return
        }
        val byteArray = file.readBytes()
        if (byteArray.isNotEmpty()) {
            val loadedMap: Map<String, Any> = cbor.decodeFromByteArray(MapSerializer(String.serializer(), AnySerializer), byteArray)
            KVMap.putAll(loadedMap)
        }
    }

    fun saveToFile() {
        val byteArray = cbor.encodeToByteArray(MapSerializer(String.serializer(), AnySerializer), KVMap)
        File(databaseLocation).writeBytes(byteArray)
    }
    fun put(key:String, value:Any){
        KVMap[key] = value
    }
    fun get(key:String): Response.QueryResponse {
        if(KVMap.containsKey(key)){
            return Response.QueryResponse(KVMap.get(key)!!, true)
        } else {
            return Response.QueryResponse(null, false)
        }
    }
}
