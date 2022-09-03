package com.bolyartech.forge.server.misc

import com.google.gson.*
import java.lang.reflect.Type
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*


internal class LocalDateTimeSerializer : JsonSerializer<LocalDateTime> {
    override fun serialize(
        localDateTime: LocalDateTime,
        srcType: Type,
        context: JsonSerializationContext
    ): JsonElement {
        return JsonPrimitive(formatter.format(localDateTime))
    }

    companion object {
        private val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("d:MM:uuuu HH:mm:ss")
    }
}

internal class LocalDateTimeDeserializer : JsonDeserializer<LocalDateTime> {
    @Throws(JsonParseException::class)
    override fun deserialize(
        json: JsonElement,
        typeOfT: Type,
        context: JsonDeserializationContext
    ): LocalDateTime {
        return LocalDateTime.parse(
            json.asString,
            DateTimeFormatter.ofPattern("d:MM:uuuu HH:mm:ss").withLocale(Locale.ENGLISH)
        )
    }
}
