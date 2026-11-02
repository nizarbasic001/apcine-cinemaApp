package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Mail
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.CineStreamBackground
import com.example.ui.theme.CineStreamBorder
import com.example.ui.theme.CineStreamCard
import com.example.ui.theme.CineStreamGold
import com.example.ui.theme.CineStreamRed
import com.example.ui.theme.CineStreamSuccess
import com.example.ui.theme.CineStreamSurfaceVariant
import com.example.ui.theme.CineStreamTextMuted
import com.example.ui.viewmodel.CineStreamViewModel
import com.example.util.LocaleHelper

@Composable
fun ProfileScreen(
    viewModel: CineStreamViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val currentUser by viewModel.currentUser.collectAsState()
    val currentLanguage by viewModel.currentLanguage.collectAsState()

    var isSignUpMode by remember { mutableStateOf(false) }
    var emailInput by remember { mutableStateOf("") }
    var passwordInput by remember { mutableStateOf("") }
    var nameInput by remember { mutableStateOf("") }
    var isSubmitting by remember { mutableStateOf(false) }
    var authError by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(CineStreamBackground)
            .windowInsetsPadding(WindowInsets.statusBars)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
            .padding(bottom = 90.dp)
    ) {
        Text(
            text = stringResource(R.string.profile_title),
            color = Color.White,
            fontSize = 24.sp,
            fontWeight = FontWeight.Black
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Language Settings Section (Featured at top of Profile Settings)
        Text(
            text = stringResource(R.string.settings_title),
            color = Color.White,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = CineStreamCard),
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, CineStreamBorder, RoundedCornerShape(16.dp))
                .testTag("settings_language_card")
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(CineStreamRed.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Language,
                            contentDescription = stringResource(R.string.settings_language),
                            tint = CineStreamRed,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Column {
                        Text(
                            text = stringResource(R.string.settings_language),
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = stringResource(R.string.settings_language_desc),
                            color = CineStreamTextMuted,
                            fontSize = 12.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // English Option
                LanguageSelectorItem(
                    title = stringResource(R.string.language_english),
                    subtitle = "Left-to-Right (LTR)",
                    isSelected = currentLanguage == LocaleHelper.LANGUAGE_ENGLISH,
                    onClick = {
                        if (currentLanguage != LocaleHelper.LANGUAGE_ENGLISH) {
                            viewModel.setLanguage(LocaleHelper.LANGUAGE_ENGLISH)
                            Toast.makeText(context, "Language changed to English", Toast.LENGTH_SHORT).show()
                        }
                    },
                    testTag = "language_option_en"
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Arabic Option
                LanguageSelectorItem(
                    title = stringResource(R.string.language_arabic),
                    subtitle = "من اليمين لليسار (RTL)",
                    isSelected = currentLanguage == LocaleHelper.LANGUAGE_ARABIC,
                    onClick = {
                        if (currentLanguage != LocaleHelper.LANGUAGE_ARABIC) {
                            viewModel.setLanguage(LocaleHelper.LANGUAGE_ARABIC)
                            Toast.makeText(context, "تم تغيير لغة التطبيق إلى العربية", Toast.LENGTH_SHORT).show()
                        }
                    },
                    testTag = "language_option_ar"
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // User Account or Login Section
        if (currentUser != null) {
            // Logged In Card
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = CineStreamCard),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, CineStreamBorder, RoundedCornerShape(16.dp))
                    .testTag("profile_user_card")
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(CircleShape)
                            .background(CineStreamRed),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = (currentUser?.displayName ?: currentUser?.email ?: "U").take(1).uppercase(),
                            color = Color.White,
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = currentUser?.displayName ?: stringResource(R.string.profile_default_user),
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = currentUser?.email ?: "",
                        color = CineStreamTextMuted,
                        fontSize = 14.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier
                            .background(Color.Black.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                            .border(0.5.dp, CineStreamBorder, RoundedCornerShape(8.dp))
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = CineStreamSuccess,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = stringResource(R.string.profile_auth_status),
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Button(
                        onClick = { viewModel.logout() },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.White.copy(alpha = 0.12f),
                            contentColor = CineStreamRed
                        ),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("profile_logout_button")
                    ) {
                        Icon(Icons.Default.ExitToApp, contentDescription = null, tint = CineStreamRed)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = stringResource(R.string.auth_logout),
                            fontWeight = FontWeight.Bold,
                            color = CineStreamRed
                        )
                    }
                }
            }
        } else {
            // Authentication Card
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = CineStreamCard),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, CineStreamBorder, RoundedCornerShape(16.dp))
                    .testTag("profile_auth_card")
            ) {
                Column(
                    modifier = Modifier.padding(20.dp)
                ) {
                    Text(
                        text = if (isSignUpMode) stringResource(R.string.auth_signup_title) else stringResource(R.string.auth_login_title),
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = stringResource(R.string.auth_sync_subtitle),
                        color = CineStreamTextMuted,
                        fontSize = 13.sp
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    if (isSignUpMode) {
                        OutlinedTextField(
                            value = nameInput,
                            onValueChange = { nameInput = it },
                            label = { Text(stringResource(R.string.auth_name)) },
                            leadingIcon = {
                                Icon(Icons.Default.Person, contentDescription = null, tint = CineStreamRed)
                            },
                            singleLine = true,
                            shape = RoundedCornerShape(10.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = CineStreamSurfaceVariant,
                                unfocusedContainerColor = CineStreamSurfaceVariant,
                                focusedBorderColor = CineStreamRed,
                                unfocusedBorderColor = CineStreamBorder,
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                    }

                    OutlinedTextField(
                        value = emailInput,
                        onValueChange = { emailInput = it },
                        label = { Text(stringResource(R.string.auth_email)) },
                        leadingIcon = {
                            Icon(Icons.Default.Mail, contentDescription = null, tint = CineStreamRed)
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = CineStreamSurfaceVariant,
                            unfocusedContainerColor = CineStreamSurfaceVariant,
                            focusedBorderColor = CineStreamRed,
                            unfocusedBorderColor = CineStreamBorder,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("auth_email_input")
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = passwordInput,
                        onValueChange = { passwordInput = it },
                        label = { Text(stringResource(R.string.auth_password)) },
                        leadingIcon = {
                            Icon(Icons.Default.Lock, contentDescription = null, tint = CineStreamRed)
                        },
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = CineStreamSurfaceVariant,
                            unfocusedContainerColor = CineStreamSurfaceVariant,
                            focusedBorderColor = CineStreamRed,
                            unfocusedBorderColor = CineStreamBorder,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("auth_password_input")
                    )

                    if (!authError.isNullOrBlank()) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = authError ?: "",
                            color = CineStreamRed,
                            fontSize = 12.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            if (emailInput.isBlank() || passwordInput.isBlank()) {
                                authError = "Please enter both email and password."
                                return@Button
                            }
                            isSubmitting = true
                            authError = null

                            if (isSignUpMode) {
                                viewModel.signUp(emailInput, passwordInput, nameInput) { success, err ->
                                    isSubmitting = false
                                    if (success) {
                                        Toast.makeText(context, "Account created successfully!", Toast.LENGTH_SHORT).show()
                                    } else {
                                        authError = err ?: "Sign up failed."
                                    }
                                }
                            } else {
                                viewModel.login(emailInput, passwordInput) { success, err ->
                                    isSubmitting = false
                                    if (success) {
                                        Toast.makeText(context, "Welcome to CineStream!", Toast.LENGTH_SHORT).show()
                                    } else {
                                        authError = err ?: "Invalid email or password."
                                    }
                                }
                            }
                        },
                        enabled = !isSubmitting,
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = CineStreamRed),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(46.dp)
                            .testTag("auth_submit_button")
                    ) {
                        if (isSubmitting) {
                            CircularProgressIndicator(
                                color = Color.White,
                                modifier = Modifier.size(20.dp),
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text(
                                text = if (isSignUpMode) stringResource(R.string.auth_sign_up_button) else stringResource(R.string.auth_sign_in_button),
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = if (isSignUpMode) stringResource(R.string.auth_already_have_account) else stringResource(R.string.auth_dont_have_account),
                            color = CineStreamTextMuted,
                            fontSize = 13.sp
                        )
                        Text(
                            text = if (isSignUpMode) stringResource(R.string.auth_sign_in_button) else stringResource(R.string.auth_sign_up_button),
                            color = CineStreamRed,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier
                                .clickable {
                                    isSignUpMode = !isSignUpMode
                                    authError = null
                                }
                                .testTag("auth_toggle_mode_button")
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // System & Integration Status Card
        Text(
            text = stringResource(R.string.platform_details_title),
            color = Color.White,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = CineStreamCard),
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, CineStreamBorder, RoundedCornerShape(12.dp))
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Tv, contentDescription = null, tint = CineStreamRed, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(stringResource(R.string.platform_player_status), color = Color.White, fontSize = 13.sp)
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Security, contentDescription = null, tint = CineStreamGold, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(stringResource(R.string.platform_db_status), color = Color.White, fontSize = 13.sp)
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.AccountCircle, contentDescription = null, tint = CineStreamTextMuted, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(stringResource(R.string.platform_version_status), color = CineStreamTextMuted, fontSize = 12.sp)
                }
            }
        }
    }
}

@Composable
private fun LanguageSelectorItem(
    title: String,
    subtitle: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    testTag: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(if (isSelected) CineStreamRed.copy(alpha = 0.12f) else CineStreamSurfaceVariant)
            .border(
                width = 1.dp,
                color = if (isSelected) CineStreamRed else CineStreamBorder,
                shape = RoundedCornerShape(10.dp)
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 10.dp)
            .testTag(testTag),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text(
                text = title,
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.SemiBold
            )
            Text(
                text = subtitle,
                color = if (isSelected) CineStreamRed else CineStreamTextMuted,
                fontSize = 11.sp
            )
        }

        RadioButton(
            selected = isSelected,
            onClick = onClick,
            colors = RadioButtonDefaults.colors(
                selectedColor = CineStreamRed,
                unselectedColor = CineStreamTextMuted
            )
        )
    }
}
