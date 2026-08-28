package hu.tb.domain

enum class ProfileType(val value: String) {
    COACH("coach"), NORMAL("normal");

    companion object {
        fun fromValue(value: String): ProfileType =
            entries.firstOrNull { it.value == value } ?: NORMAL
    }
}