package com.bolyartech.forge.server.misc

import com.google.gson.*
import java.lang.reflect.Type
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

class LocalDateSerializer : JsonSerializer<LocalDate> {
    override fun serialize(
        localDateTime: LocalDate,
        srcType: Type,
        context: JsonSerializationContext
    ): JsonElement {
        return JsonPrimitive(DateTimeFormatter.ISO_LOCAL_DATE.format(localDateTime))
    }
}

class LocalDateDeserializer : JsonDeserializer<LocalDate> {
    @Throws(JsonParseException::class)
    override fun deserialize(
        json: JsonElement,
        typeOfT: Type,
        context: JsonDeserializationContext
    ): LocalDate {
        return LocalDate.parse(
            json.asString,
            DateTimeFormatter.ISO_LOCAL_DATE
        )
    }
}

class LocalDateTimeSerializer : JsonSerializer<LocalDateTime> {
    override fun serialize(
        localDateTime: LocalDateTime,
        srcType: Type,
        context: JsonSerializationContext
    ): JsonElement {
        return JsonPrimitive(DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(localDateTime))
    }
}

class LocalDateTimeDeserializer : JsonDeserializer<LocalDateTime> {
    @Throws(JsonParseException::class)
    override fun deserialize(
        json: JsonElement,
        typeOfT: Type,
        context: JsonDeserializationContext
    ): LocalDateTime {
        return LocalDateTime.parse(
            json.asString,
            DateTimeFormatter.ISO_LOCAL_DATE_TIME
        )
    }
}


class ZonedDateTimeSerializer : JsonSerializer<ZonedDateTime> {
    override fun serialize(
        localDateTime: ZonedDateTime,
        srcType: Type,
        context: JsonSerializationContext
    ): JsonElement {
        return JsonPrimitive(DateTimeFormatter.ISO_ZONED_DATE_TIME.format(localDateTime))
    }
}

class ZonedDateTimeDeserializer : JsonDeserializer<ZonedDateTime> {
    @Throws(JsonParseException::class)
    override fun deserialize(
        json: JsonElement,
        typeOfT: Type,
        context: JsonDeserializationContext
    ): ZonedDateTime {
        return ZonedDateTime.parse(
            json.asString,
            DateTimeFormatter.ISO_ZONED_DATE_TIME
        )
    }
}
