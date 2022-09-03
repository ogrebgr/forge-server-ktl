package com.bolyartech.forge.server.db

import java.math.BigDecimal
import java.sql.PreparedStatement
import java.sql.SQLException
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.OffsetDateTime


@Throws(SQLException::class)
fun PreparedStatement.setValue(position: Int, value: Boolean) {
    this.setBoolean(position, value)
}

@Throws(SQLException::class)
fun PreparedStatement.setValue(position: Int, value: String?) {
    if (value != null) {
        this.setString(position, value)
    } else {
        this.setNull(position, java.sql.Types.VARCHAR)
    }
}


@Throws(SQLException::class)
fun PreparedStatement.setValue(position: Int, value: java.util.Date?) {
    if (value != null) {
        this.setDate(position, java.sql.Date(value.time)) // Convert util.Date to sql.Date
    } else {
        this.setNull(position, java.sql.Types.DATE)
    }
}


@Throws(SQLException::class)
fun PreparedStatement.setValue(position: Int, value: java.sql.Date?) {
    if (value != null) {
        this.setDate(position, value)
    } else {
        this.setNull(position, java.sql.Types.DATE)
    }
}


@Throws(SQLException::class)
fun PreparedStatement.setValue(position: Int, value: java.sql.Time?) {
    if (value != null) {
        this.setTime(position, value)
    } else {
        this.setNull(position, java.sql.Types.TIME)
    }
}


@Throws(SQLException::class)
fun PreparedStatement.setValue(position: Int, value: java.sql.Timestamp?) {
    if (value != null) {
        this.setTimestamp(position, value)
    } else {
        this.setNull(position, java.sql.Types.TIMESTAMP)
    }
}


@Throws(SQLException::class)
fun PreparedStatement.setValue(position: Int, value: Byte?) {
    if (value != null) {
        this.setByte(position, value)
    } else {
        this.setNull(position, java.sql.Types.TINYINT) // JDBC : "TINYINT" => getByte/setByte
    }
}


@Throws(SQLException::class)
fun PreparedStatement.setValue(position: Int, value: Short?) {
    if (value != null) {
        this.setShort(position, value)
    } else {
        this.setNull(position, java.sql.Types.SMALLINT)
    }
}


@Throws(SQLException::class)
fun PreparedStatement.setValue(position: Int, value: Int?) {
    if (value != null) {
        this.setInt(position, value)
    } else {
        this.setNull(position, java.sql.Types.INTEGER)
    }
}


@Throws(SQLException::class)
fun PreparedStatement.setValue(position: Int, value: Long?) {
    if (value != null) {
        this.setLong(position, value)
    } else {
        this.setNull(position, java.sql.Types.BIGINT) // JDBC : "BIGINT" => getLong/setLong
    }
}


@Throws(SQLException::class)
fun PreparedStatement.setValue(position: Int, value: Float?) {
    if (value != null) {
        this.setFloat(position, value)
    } else {
        this.setNull(position, java.sql.Types.FLOAT)
    }
}


@Throws(SQLException::class)
fun PreparedStatement.setValue(position: Int, value: Double?) {
    if (value != null) {
        this.setDouble(position, value)
    } else {
        this.setNull(position, java.sql.Types.DOUBLE)
    }
}


@Throws(SQLException::class)
fun PreparedStatement.setValue(position: Int, value: BigDecimal?) {
    if (value != null) {
        this.setBigDecimal(position, value)
    } else {
        this.setNull(position, java.sql.Types.DECIMAL)
    }
}


@Throws(SQLException::class)
fun PreparedStatement.setValue(position: Int, value: ByteArray?) {
    if (value != null) {
        this.setBytes(position, value)
    } else {
        this.setNull(position, java.sql.Types.BINARY)
    }
}


@Throws(SQLException::class)
fun PreparedStatement.setValue(position: Int, value: LocalTime?) {
    if (value != null) {
        this.setObject(position, value)
    } else {
        this.setNull(position, java.sql.Types.TIME)
    }
}

@Throws(SQLException::class)
fun PreparedStatement.setValue(position: Int, value: LocalDate?) {
    if (value != null) {
        this.setObject(position, value)
    } else {
        this.setNull(position, java.sql.Types.DATE)
    }
}


@Throws(SQLException::class)
fun PreparedStatement.setValue(position: Int, value: LocalDateTime?) {
    if (value != null) {
        this.setObject(position, value)
    } else {
        this.setNull(position, java.sql.Types.TIMESTAMP)
    }
}


@Throws(SQLException::class)
fun PreparedStatement.setValue(position: Int, value: OffsetDateTime?) {
    if (value != null) {
        this.setObject(position, value)
    } else {
        this.setNull(position, java.sql.Types.TIMESTAMP_WITH_TIMEZONE)
    }
}