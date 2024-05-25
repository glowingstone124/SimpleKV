import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializer
import kotlinx.serialization.descriptors.*
import kotlinx.serialization.encoding.*

@Serializer(forClass = Any::class)
object AnySerializer : KSerializer<Any> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("Any") {
        element<String>("type")
        element<String>("value")
    }

    override fun serialize(encoder: Encoder, value: Any) {
        encoder.encodeStructure(descriptor) {
            when (value) {
                is Int -> {
                    encodeStringElement(descriptor, 0, "Int")
                    encodeIntElement(descriptor, 1, value)
                }
                is String -> {
                    encodeStringElement(descriptor, 0, "String")
                    encodeStringElement(descriptor, 1, value)
                }
                is Boolean -> {
                    encodeStringElement(descriptor, 0, "Boolean")
                    encodeBooleanElement(descriptor, 1, value)
                }
                else -> throw IllegalArgumentException("Unsupported type: ${value::class}")
            }
        }
    }

    override fun deserialize(decoder: Decoder): Any {
        return decoder.decodeStructure(descriptor) {
            var type: String? = null
            var value: Any? = null

            while (true) {
                when (val index = decodeElementIndex(descriptor)) {
                    CompositeDecoder.DECODE_DONE -> break
                    0 -> type = decodeStringElement(descriptor, index)
                    1 -> value = when (type) {
                        "Int" -> decodeIntElement(descriptor, index)
                        "String" -> decodeStringElement(descriptor, index)
                        "Boolean" -> decodeBooleanElement(descriptor, index)
                        else -> throw IllegalArgumentException("Unsupported type: $type")
                    }
                    else -> throw IllegalArgumentException("Unexpected index: $index")
                }
            }
            value ?: throw IllegalStateException("Value cannot be null")
        }
    }
}
