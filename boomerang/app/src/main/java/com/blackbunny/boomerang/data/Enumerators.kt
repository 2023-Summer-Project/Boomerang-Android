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
    LOG_IN, LOGGED_IN, INITIAL, SIGN_IN, SIGN_UP, AUTHENTICATED, ERROR, NOT_VERIFIED
}