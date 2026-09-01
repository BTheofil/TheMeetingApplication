package hu.tb.search.domain


data class CoachResult(
    val id: String,
    val name: String,
) {
    val initials: String
        get() = name.split(' ')
            .filter { it.isNotBlank() }
            .take(2)
            .map { it.first().uppercaseChar() }
            .joinToString("")
}