package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AlternateEmail
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.components.BsAndAdDobPicker
import com.example.ui.components.CalendarType
import com.example.ui.components.KXaButton
import com.example.ui.components.KXaTextField
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.KxaRadius
import com.example.ui.theme.KxaSpacing
import com.example.ui.theme.KxaTheme
import com.example.ui.theme.LiveRed
import com.example.ui.theme.OnlineGreen
import com.example.ui.theme.PurpleLight
import com.example.ui.theme.PurplePrimary
import com.example.util.BikramSambatUtils
import com.example.util.BsDate
import java.time.LocalDate
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(
    onSignIn: suspend (email: String, password: String) -> Result<Unit>,
    onSignUp: suspend (
        email: String,
        password: String,
        username: String,
        fullName: String,
        dateOfBirth: String?,
        dobCalendar: String,
        age: Int?
    ) -> Result<Unit>,
    onBackToOnboarding: () -> Unit,
    initialIsSignUp: Boolean = false
) {
    var isSignUp by remember { mutableStateOf(initialIsSignUp) }

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var displayName by remember { mutableStateOf("") }

    // Field-level Error States (Shown in Red)
    var emailError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }
    var confirmPasswordError by remember { mutableStateOf<String?>(null) }
    var usernameError by remember { mutableStateOf<String?>(null) }
    var displayNameError by remember { mutableStateOf<String?>(null) }
    var dobError by remember { mutableStateOf<String?>(null) }

    // Date of Birth & 16+ Age State
    var selectedCalendar by remember { mutableStateOf(CalendarType.AD) }
    var selectedAdDate by remember { mutableStateOf<LocalDate?>(null) }
    var selectedBsDate by remember { mutableStateOf<BsDate?>(null) }

    val calculatedAge = remember(selectedCalendar, selectedAdDate, selectedBsDate) {
        when (selectedCalendar) {
            CalendarType.AD -> {
                selectedAdDate?.let { date ->
                    BikramSambatUtils.calculateAge(date.year, date.monthValue, date.dayOfMonth, isBS = false)
                }
            }
            CalendarType.BS -> {
                selectedBsDate?.let { date ->
                    BikramSambatUtils.calculateAge(date.year, date.month, date.day, isBS = true)
                }
            }
        }
    }

    val isAgeEligible = calculatedAge != null && BikramSambatUtils.isAgeEligible(calculatedAge)

    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }

    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var successMessage by remember { mutableStateOf<String?>(null) }

    val scrollState = rememberScrollState()
    val focusManager = LocalFocusManager.current
    val scope = rememberCoroutineScope()

    fun clearAllErrors() {
        errorMessage = null
        successMessage = null
        emailError = null
        passwordError = null
        confirmPasswordError = null
        usernameError = null
        displayNameError = null
        dobError = null
    }

    fun handleSubmit() {
        val trimmedEmail = email.trim()
        val trimmedPassword = password
        val trimmedUsername = username.trim().lowercase()
        val trimmedDisplayName = displayName.trim()

        clearAllErrors()

        var hasValidationError = false

        if (isSignUp) {
            if (trimmedDisplayName.isBlank()) {
                displayNameError = "Please enter your display name."
                hasValidationError = true
            }
            if (trimmedUsername.isBlank()) {
                usernameError = "Please choose a username handle."
                hasValidationError = true
            } else if (trimmedUsername.length < 3) {
                usernameError = "Username must be at least 3 characters."
                hasValidationError = true
            } else if (!trimmedUsername.matches(Regex("^[a-zA-Z0-9_]+$"))) {
                usernameError = "Username can only contain letters, numbers, and underscores."
                hasValidationError = true
            }

            if (trimmedEmail.isBlank()) {
                emailError = "Please enter your email address."
                hasValidationError = true
            } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(trimmedEmail).matches()) {
                emailError = "Please enter a valid email address (e.g. user@example.com)."
                hasValidationError = true
            }

            if (trimmedPassword.isBlank()) {
                passwordError = "Please enter a password."
                hasValidationError = true
            } else if (trimmedPassword.length < 6) {
                passwordError = "Password must be at least 6 characters."
                hasValidationError = true
            }

            if (confirmPassword.isBlank()) {
                confirmPasswordError = "Please confirm your password."
                hasValidationError = true
            } else if (trimmedPassword != confirmPassword) {
                confirmPasswordError = "Wrong confirm password. Passwords do not match."
                hasValidationError = true
            }

            // DOB and 16+ Verification
            val hasDob = if (selectedCalendar == CalendarType.AD) selectedAdDate != null else selectedBsDate != null
            if (!hasDob) {
                dobError = "Please select your date of birth to continue."
                hasValidationError = true
            } else if (!isAgeEligible) {
                dobError = "You must be at least 16 years old to create a K Xa account."
                hasValidationError = true
            }
        } else {
            // Sign In Validations
            if (trimmedEmail.isBlank()) {
                emailError = "Please enter your email address."
                hasValidationError = true
            } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(trimmedEmail).matches()) {
                emailError = "Please enter a valid email address."
                hasValidationError = true
            }

            if (trimmedPassword.isBlank()) {
                passwordError = "Please enter your password."
                hasValidationError = true
            } else if (trimmedPassword.length < 6) {
                passwordError = "Password must be at least 6 characters."
                hasValidationError = true
            }
        }

        if (hasValidationError) {
            errorMessage = displayNameError ?: usernameError ?: emailError ?: passwordError ?: confirmPasswordError ?: dobError ?: "Please check the highlighted fields in red."
            return
        }

        val dobIso = when (selectedCalendar) {
            CalendarType.AD -> selectedAdDate?.toString()
            CalendarType.BS -> selectedBsDate?.toIsoString()
        }
        val calTypeStr = if (selectedCalendar == CalendarType.AD) "AD" else "BS"

        isLoading = true
        focusManager.clearFocus()

        scope.launch {
            try {
                if (isSignUp) {
                    val result = onSignUp(
                        trimmedEmail,
                        trimmedPassword,
                        trimmedUsername,
                        trimmedDisplayName,
                        dobIso,
                        calTypeStr,
                        calculatedAge
                    )
                    result.fold(
                        onSuccess = {
                            successMessage = "Account created successfully! Welcome to K Xa."
                        },
                        onFailure = { error ->
                            val msg = error.localizedMessage ?: "Failed to create account. Please try again."
                            errorMessage = msg
                            if (msg.contains("Username", ignoreCase = true) && msg.contains("taken", ignoreCase = true)) {
                                usernameError = msg
                            } else if (msg.contains("already exists", ignoreCase = true) || msg.contains("already registered", ignoreCase = true)) {
                                emailError = msg
                            }
                        }
                    )
                } else {
                    val result = onSignIn(trimmedEmail, trimmedPassword)
                    result.fold(
                        onSuccess = {
                            // Navigation handled by AuthState listener in Scaffold
                        },
                        onFailure = { error ->
                            val msg = error.localizedMessage ?: "Invalid email or password. Please verify credentials."
                            errorMessage = msg
                            if (msg.contains("not found", ignoreCase = true)) {
                                emailError = msg
                            } else if (msg.contains("Incorrect password", ignoreCase = true) || msg.contains("password", ignoreCase = true)) {
                                passwordError = msg
                            }
                        }
                    )
                }
            } finally {
                isLoading = false
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(KxaTheme.colors.background)
            .statusBarsPadding()
            .navigationBarsPadding()
            .imePadding()
            .verticalScroll(scrollState)
            .padding(horizontal = KxaSpacing.standard, vertical = KxaSpacing.md)
            .testTag("screen_login"),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // App Icon
        Box(
            modifier = Modifier
                .size(72.dp)
                .clip(RoundedCornerShape(22.dp))
                .background(KxaTheme.colors.surfaceVariant)
                .border(
                    1.5.dp,
                    Brush.linearGradient(listOf(PurplePrimary, CyanAccent)),
                    RoundedCornerShape(22.dp)
                )
                .padding(10.dp),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.img_app_icon),
                contentDescription = "K Xa Logo",
                modifier = Modifier.size(52.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Screen Heading
        Text(
            text = if (isSignUp) "Create Your K Xa Account" else "Welcome Back",
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp
            ),
            color = KxaTheme.colors.textPrimary
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = if (isSignUp) "Stream, chat, and react in real-time with friends" else "Sign in with your email to join active watch parties",
            style = MaterialTheme.typography.bodyMedium,
            color = KxaTheme.colors.textSecondary
        )

        Spacer(modifier = Modifier.height(22.dp))

        // Mode Switcher Tabs
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(KxaTheme.colors.surface)
                .border(1.dp, KxaTheme.colors.borderSubtle, RoundedCornerShape(14.dp))
                .padding(4.dp)
        ) {
            Box(
                modifier = Modifier
                    .testTag("tab_sign_in")
                    .weight(1f)
                    .clip(RoundedCornerShape(10.dp))
                    .background(if (!isSignUp) PurplePrimary else Color.Transparent)
                    .clickable {
                        isSignUp = false
                        clearAllErrors()
                    }
                    .padding(vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Sign In",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = if (!isSignUp) Color.White else KxaTheme.colors.textMuted
                )
            }

            Box(
                modifier = Modifier
                    .testTag("tab_sign_up")
                    .weight(1f)
                    .clip(RoundedCornerShape(10.dp))
                    .background(if (isSignUp) PurplePrimary else Color.Transparent)
                    .clickable {
                        isSignUp = true
                        clearAllErrors()
                    }
                    .padding(vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Sign Up",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = if (isSignUp) Color.White else KxaTheme.colors.textMuted
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Sign Up Additional Fields
        if (isSignUp) {
            KXaTextField(
                value = displayName,
                onValueChange = {
                    displayName = it
                    displayNameError = null
                    errorMessage = null
                },
                label = "Display Name",
                placeholder = "Your Name",
                leadingIcon = Icons.Default.Person,
                isError = displayNameError != null,
                errorMessage = displayNameError,
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onNext = { focusManager.moveFocus(FocusDirection.Down) }
                ),
                testTag = "input_login_display_name"
            )

            Spacer(modifier = Modifier.height(12.dp))

            KXaTextField(
                value = username,
                onValueChange = {
                    username = it
                    usernameError = if (it.isNotBlank() && !it.matches(Regex("^[a-zA-Z0-9_]+$"))) {
                        "Username can only contain letters, numbers, and underscores."
                    } else {
                        null
                    }
                    errorMessage = null
                },
                label = "Username (@handle)",
                placeholder = "username",
                leadingIcon = Icons.Default.AlternateEmail,
                isError = usernameError != null,
                errorMessage = usernameError,
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onNext = { focusManager.moveFocus(FocusDirection.Down) }
                ),
                testTag = "input_login_username"
            )

            Spacer(modifier = Modifier.height(12.dp))
        }

        // Email Address
        KXaTextField(
            value = email,
            onValueChange = {
                email = it
                emailError = null
                errorMessage = null
            },
            label = "Email Address",
            placeholder = "you@example.com",
            leadingIcon = Icons.Default.Email,
            isError = emailError != null,
            errorMessage = emailError,
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(
                onNext = { focusManager.moveFocus(FocusDirection.Down) }
            ),
            testTag = "input_login_email"
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Password Field
        KXaTextField(
            value = password,
            onValueChange = {
                password = it
                passwordError = null
                if (isSignUp && confirmPassword.isNotBlank()) {
                    confirmPasswordError = if (it != confirmPassword) "Wrong confirm password. Passwords do not match." else null
                }
                errorMessage = null
            },
            label = "Password (min 6 characters)",
            leadingIcon = Icons.Default.Lock,
            isError = passwordError != null,
            errorMessage = passwordError,
            trailingIcon = {
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(
                        imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                        contentDescription = if (passwordVisible) "Hide password" else "Show password",
                        tint = KxaTheme.colors.textMuted
                    )
                }
            },
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = if (isSignUp) ImeAction.Next else ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onNext = { focusManager.moveFocus(FocusDirection.Down) },
                onDone = { handleSubmit() }
            ),
            testTag = "input_login_password"
        )

        // Confirm Password Field (Sign Up Only)
        if (isSignUp) {
            Spacer(modifier = Modifier.height(12.dp))

            KXaTextField(
                value = confirmPassword,
                onValueChange = {
                    confirmPassword = it
                    confirmPasswordError = if (it.isNotBlank() && it != password) {
                        "Wrong confirm password. Passwords do not match."
                    } else {
                        null
                    }
                    errorMessage = null
                },
                label = "Confirm Password",
                leadingIcon = Icons.Default.Lock,
                isError = confirmPasswordError != null,
                errorMessage = confirmPasswordError,
                trailingIcon = {
                    IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                        Icon(
                            imageVector = if (confirmPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                            contentDescription = if (confirmPasswordVisible) "Hide password" else "Show password",
                            tint = KxaTheme.colors.textMuted
                        )
                    }
                },
                visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = { focusManager.clearFocus() }
                ),
                testTag = "input_login_confirm_password"
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Date of Birth Selector (BS & AD Support with 16+ Restriction)
            BsAndAdDobPicker(
                selectedCalendar = selectedCalendar,
                onCalendarTypeChanged = { selectedCalendar = it },
                selectedAdDate = selectedAdDate,
                onAdDateSelected = {
                    selectedAdDate = it
                    dobError = null
                    errorMessage = null
                },
                selectedBsDate = selectedBsDate,
                onBsDateSelected = {
                    selectedBsDate = it
                    dobError = null
                    errorMessage = null
                },
                age = calculatedAge,
                isAgeEligible = isAgeEligible
            )

            if (dobError != null) {
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.ErrorOutline,
                        contentDescription = "Error",
                        tint = LiveRed,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = dobError ?: "",
                        color = LiveRed,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Error Message Banner
        AnimatedVisibility(
            visible = errorMessage != null,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            errorMessage?.let { msg ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(LiveRed.copy(alpha = 0.15f))
                        .border(1.dp, LiveRed.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.ErrorOutline,
                        contentDescription = "Error",
                        tint = LiveRed,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = msg,
                        color = LiveRed,
                        fontSize = 13.sp,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // Success Message Banner
        AnimatedVisibility(
            visible = successMessage != null,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            successMessage?.let { msg ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(OnlineGreen.copy(alpha = 0.15f))
                        .border(1.dp, OnlineGreen.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = msg,
                        color = OnlineGreen,
                        fontSize = 13.sp,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Submit Button
        KXaButton(
            text = if (isSignUp) "Create Account ➔" else "Sign In ➔",
            onClick = { handleSubmit() },
            isLoading = isLoading,
            modifier = Modifier.fillMaxWidth(),
            testTag = "btn_submit_auth"
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Back to Onboarding Link
        Text(
            text = "Back to Welcome",
            color = KxaTheme.colors.textMuted,
            fontSize = 13.sp,
            modifier = Modifier
                .testTag("btn_back_onboarding")
                .clickable { onBackToOnboarding() }
                .padding(8.dp)
        )
    }
}
