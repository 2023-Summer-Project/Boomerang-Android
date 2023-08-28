package com.blackbunny.boomerang.data

import androidx.annotation.StringRes
import com.blackbunny.boomerang.R

enum class PasswordValidation(@StringRes val text: Int) {
    INITIAL(text = R.string.re_enter_password),
    NOT_VALID(text = R.string.password_not_match),
    VALID(text = R.string.password_match)
}

enum class EmailValidation(@StringRes val text: Int) {
    INITIAL(text = R.string.email_enter),
    INVALID_FORMAT(text = R.string.email_invalid_format),
    EXISTS(text = R.string.email_already_exists),
    VALID(text = R.string.email_valid)
}

enum class EmailVerification {
    NOT_VERIFIED, VERIFYING, VERIFIED
}

enum class MainAppStatus {
    STARTUP, INITIAL, SIGN_IN, SIGN_UP, AUTHENTICATED, ERROR, NOT_VERIFIED, REGISTRATION
}

enum class SignOutRequest(@StringRes val text: Int) {
    YET_REQUESTED(text = R.string.sign_out_yet_requested),
    REQUESTING(text = R.string.sign_out_requested),
    SUCCESS(text = R.string.sign_out_success),
    FAILED(text = R.string.sign_out_failed)
}

enum class DataFetchRequest {
    READY, REQUESTED, COMPLETED
}

enum class TransactionStatus(@StringRes val text: Int) {
    YET_INITIALIZED(text = R.string.transaction_yet_initialized),
    REQUESTED(text = R.string.transaction_requested),
    ACCEPTED(text = R.string.transaction_accepted),
    REJECTED(text = R.string.transaction_rejected),
    NOT_RETURNED(text = R.string.transaction_not_returned),
    COMPLETED(text = R.string.transaction_completed)
}