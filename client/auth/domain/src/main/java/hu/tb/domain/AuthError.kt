package hu.tb.domain

/**
 * Why an authentication attempt failed, for callers that must branch on the reason rather than
 * only show [AuthResults.errorMessage] -- a background token refresh keeps the session alive on
 * [NO_INTERNET] but ends it on anything else.
 */
enum class AuthError {
    NO_INTERNET,
    UNAUTHORIZED,
    CONFLICT,
    UNKNOWN
}
