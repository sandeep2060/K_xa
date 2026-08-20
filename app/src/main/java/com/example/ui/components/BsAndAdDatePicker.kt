package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
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
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

enum class CalendarType {
    AD, BS
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BsAndAdDobPicker(
    selectedCalendar: CalendarType,
    onCalendarTypeChanged: (CalendarType) -> Unit,
    selectedAdDate: LocalDate?,
    onAdDateSelected: (LocalDate) -> Unit,
    selectedBsDate: BsDate?,
    onBsDateSelected: (BsDate) -> Unit,
    age: Int?,
    isAgeEligible: Boolean,
    modifier: Modifier = Modifier
) {
    var showAdDatePickerDialog by remember { mutableStateOf(false) }
    var showBsDatePickerDialog by remember { mutableStateOf(false) }

    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        // Label & Segmented Control Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "DATE OF BIRTH",
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                ),
                color = PurpleLight
            )

            // Segmented Calendar Switcher [ AD ] [ BS ]
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(KxaRadius.pill))
                    .background(KxaTheme.colors.surfaceVariant)
                    .border(1.dp, KxaTheme.colors.borderSubtle, RoundedCornerShape(KxaRadius.pill))
                    .padding(3.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                CalendarSegmentButton(
                    title = "AD (Gregorian)",
                    isSelected = selectedCalendar == CalendarType.AD,
                    onClick = { onCalendarTypeChanged(CalendarType.AD) },
                    testTag = "tab_dob_ad"
                )
                Spacer(modifier = Modifier.width(2.dp))
                CalendarSegmentButton(
                    title = "BS (नेपाली पात्रो)",
                    isSelected = selectedCalendar == CalendarType.BS,
                    onClick = { onCalendarTypeChanged(CalendarType.BS) },
                    testTag = "tab_dob_bs"
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Date Display & Picker Trigger Box
        val formattedDateString = when (selectedCalendar) {
            CalendarType.AD -> {
                selectedAdDate?.format(DateTimeFormatter.ofPattern("MMMM d, yyyy (yyyy-MM-dd)"))
                    ?: "Tap to select your date of birth (AD)"
            }
            CalendarType.BS -> {
                selectedBsDate?.format(includeNepali = true)
                    ?: "Tap to select your Nepali date of birth (BS)"
            }
        }

        val hasDate = if (selectedCalendar == CalendarType.AD) selectedAdDate != null else selectedBsDate != null

        Box(
            modifier = Modifier
                .testTag("dob_selector_trigger")
                .fillMaxWidth()
                .clip(RoundedCornerShape(KxaRadius.md))
                .background(KxaTheme.colors.surfaceVariant)
                .border(
                    1.dp,
                    if (hasDate) {
                        if (isAgeEligible) PurplePrimary.copy(alpha = 0.6f) else LiveRed.copy(alpha = 0.8f)
                    } else {
                        KxaTheme.colors.borderSubtle
                    },
                    RoundedCornerShape(KxaRadius.md)
                )
                .clickable {
                    if (selectedCalendar == CalendarType.AD) {
                        showAdDatePickerDialog = true
                    } else {
                        showBsDatePickerDialog = true
                    }
                }
                .padding(horizontal = KxaSpacing.standard, vertical = 14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = Icons.Default.CalendarMonth,
                        contentDescription = "Date of Birth",
                        tint = if (hasDate) PurpleLight else KxaTheme.colors.textMuted,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = formattedDateString,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = if (hasDate) FontWeight.SemiBold else FontWeight.Normal
                        ),
                        color = if (hasDate) KxaTheme.colors.textPrimary else KxaTheme.colors.textMuted
                    )
                }

                Icon(
                    imageVector = Icons.Default.ExpandMore,
                    contentDescription = "Select Date",
                    tint = KxaTheme.colors.textSecondary,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        // Live Age Calculation & 16+ Feedback Bar
        AnimatedVisibility(
            visible = age != null,
            enter = fadeIn(tween(200)),
            exit = fadeOut(tween(150))
        ) {
            if (age != null) {
                Column(modifier = Modifier.padding(top = 8.dp)) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(KxaRadius.sm))
                            .background(
                                if (isAgeEligible) OnlineGreen.copy(alpha = 0.12f) else LiveRed.copy(alpha = 0.14f)
                            )
                            .border(
                                1.dp,
                                if (isAgeEligible) OnlineGreen.copy(alpha = 0.4f) else LiveRed.copy(alpha = 0.5f),
                                RoundedCornerShape(KxaRadius.sm)
                            )
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = if (isAgeEligible) Icons.Default.Check else Icons.Default.ErrorOutline,
                                contentDescription = null,
                                tint = if (isAgeEligible) OnlineGreen else LiveRed,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Age: $age years old",
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                color = if (isAgeEligible) OnlineGreen else LiveRed
                            )
                        }

                        Text(
                            text = if (isAgeEligible) "Eligible (16+)" else "Under 16 Restriction",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = if (isAgeEligible) OnlineGreen else LiveRed
                        )
                    }

                    if (!isAgeEligible) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "You must be at least 16 years old to create a K Xa account.",
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                            color = LiveRed,
                            modifier = Modifier.padding(start = 4.dp)
                        )
                    }
                }
            }
        }
    }

    // Material 3 AD Date Picker Dialog
    if (showAdDatePickerDialog) {
        val initialEpochMillis = remember(selectedAdDate) {
            val date = selectedAdDate ?: LocalDate.of(2004, 1, 1)
            date.atStartOfDay(ZoneId.of("UTC")).toInstant().toEpochMilli()
        }

        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = initialEpochMillis
        )

        DatePickerDialog(
            onDismissRequest = { showAdDatePickerDialog = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        val millis = datePickerState.selectedDateMillis
                        if (millis != null) {
                            val localDate = Instant.ofEpochMilli(millis)
                                .atZone(ZoneId.of("UTC"))
                                .toLocalDate()
                            onAdDateSelected(localDate)
                        }
                        showAdDatePickerDialog = false
                    }
                ) {
                    Text("Select Date", color = PurpleLight, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAdDatePickerDialog = false }) {
                    Text("Cancel", color = KxaTheme.colors.textMuted)
                }
            },
            colors = DatePickerDefaults.colors(
                containerColor = KxaTheme.colors.surfaceElevated
            )
        ) {
            DatePicker(
                state = datePickerState,
                colors = DatePickerDefaults.colors(
                    containerColor = KxaTheme.colors.surfaceElevated,
                    titleContentColor = KxaTheme.colors.textPrimary,
                    headlineContentColor = PurpleLight,
                    weekdayContentColor = KxaTheme.colors.textSecondary,
                    subheadContentColor = KxaTheme.colors.textPrimary,
                    yearContentColor = KxaTheme.colors.textPrimary,
                    currentYearContentColor = CyanAccent,
                    selectedYearContentColor = Color.White,
                    selectedYearContainerColor = PurplePrimary,
                    dayContentColor = KxaTheme.colors.textPrimary,
                    selectedDayContentColor = Color.White,
                    selectedDayContainerColor = PurplePrimary,
                    todayContentColor = CyanAccent,
                    todayDateBorderColor = CyanAccent
                )
            )
        }
    }

    // Bikram Sambat (BS) Custom Dialog
    if (showBsDatePickerDialog) {
        BsDatePickerModalDialog(
            initialBsDate = selectedBsDate ?: BsDate(2060, 1, 1),
            onDismiss = { showBsDatePickerDialog = false },
            onConfirm = { bsDate ->
                onBsDateSelected(bsDate)
                showBsDatePickerDialog = false
            }
        )
    }
}

@Composable
private fun CalendarSegmentButton(
    title: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    testTag: String
) {
    val animatedBg by animateColorAsState(
        targetValue = if (isSelected) PurplePrimary else Color.Transparent,
        animationSpec = tween(150),
        label = "segment_bg"
    )
    val animatedText by animateColorAsState(
        targetValue = if (isSelected) Color.White else KxaTheme.colors.textSecondary,
        animationSpec = tween(150),
        label = "segment_text"
    )

    Box(
        modifier = Modifier
            .testTag(testTag)
            .clip(RoundedCornerShape(KxaRadius.pill))
            .background(animatedBg)
            .clickable(onClick = onClick)
            .padding(horizontal = 10.dp, vertical = 5.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                fontSize = 11.sp
            ),
            color = animatedText
        )
    }
}

@Composable
fun BsDatePickerModalDialog(
    initialBsDate: BsDate,
    onDismiss: () -> Unit,
    onConfirm: (BsDate) -> Unit
) {
    var selectedYear by remember { mutableIntStateOf(initialBsDate.year) }
    var selectedMonth by remember { mutableIntStateOf(initialBsDate.month) }
    var selectedDay by remember { mutableIntStateOf(initialBsDate.day) }

    val years = remember { BikramSambatUtils.getAvailableBsYears() }
    val months = remember { (1..12).toList() }
    val daysInMonth = remember(selectedYear, selectedMonth) {
        BikramSambatUtils.getDaysInBsMonth(selectedYear, selectedMonth)
    }

    // Ensure day doesn't exceed days in new selected month
    LaunchedEffect(daysInMonth) {
        if (selectedDay > daysInMonth) {
            selectedDay = daysInMonth
        }
    }

    val previewAge = remember(selectedYear, selectedMonth, selectedDay) {
        BikramSambatUtils.calculateAge(selectedYear, selectedMonth, selectedDay, isBS = true)
    }
    val isEligible = previewAge >= 16

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(KxaRadius.xl),
            color = KxaTheme.colors.surfaceElevated,
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .border(1.dp, KxaTheme.colors.borderSubtle, RoundedCornerShape(KxaRadius.xl))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(KxaSpacing.standard)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Bikram Sambat (BS)",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = KxaTheme.colors.textPrimary
                        )
                        Text(
                            text = "नेपाली जन्ममिति छान्नुहोस्",
                            style = MaterialTheme.typography.bodySmall,
                            color = PurpleLight
                        )
                    }

                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(KxaTheme.colors.surfaceVariant)
                            .clickable(onClick = onDismiss),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = KxaTheme.colors.textMuted,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Current Selection Summary Pill
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(KxaRadius.md))
                        .background(PurplePrimary.copy(alpha = 0.12f))
                        .border(1.dp, PurplePrimary.copy(alpha = 0.35f), RoundedCornerShape(KxaRadius.md))
                        .padding(12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    val nepMonth = BikramSambatUtils.BS_MONTH_NAMES_NEPALI.getOrElse(selectedMonth - 1) { "" }
                    val engMonth = BikramSambatUtils.BS_MONTH_NAMES.getOrElse(selectedMonth - 1) { "" }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "$selectedYear $nepMonth ${selectedDay.toString().padStart(2, '0')} ($engMonth)",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            ),
                            color = PurpleLight
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Calculated Age: $previewAge years (${if (isEligible) "Eligible 16+" else "Under 16"})",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                            color = if (isEligible) OnlineGreen else LiveRed
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 3-Column Wheels for Year, Month, Day
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(190.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Year Column
                    BsWheelSelector(
                        title = "Year (वर्ष)",
                        items = years.map { it.toString() },
                        selectedIndex = years.indexOf(selectedYear).coerceAtLeast(0),
                        onItemSelected = { idx -> selectedYear = years[idx] },
                        modifier = Modifier.weight(1.1f)
                    )

                    // Month Column
                    BsWheelSelector(
                        title = "Month (महिना)",
                        items = months.map { m ->
                            val n = BikramSambatUtils.BS_MONTH_NAMES_NEPALI.getOrElse(m - 1) { "" }
                            val e = BikramSambatUtils.BS_MONTH_NAMES.getOrElse(m - 1) { "" }.take(4)
                            "$n ($e)"
                        },
                        selectedIndex = (selectedMonth - 1).coerceIn(0, 11),
                        onItemSelected = { idx -> selectedMonth = idx + 1 },
                        modifier = Modifier.weight(1.3f)
                    )

                    // Day Column
                    val days = (1..daysInMonth).toList()
                    BsWheelSelector(
                        title = "Day (गते)",
                        items = days.map { it.toString().padStart(2, '0') },
                        selectedIndex = (selectedDay - 1).coerceIn(0, days.size - 1),
                        onItemSelected = { idx -> selectedDay = idx + 1 },
                        modifier = Modifier.weight(0.9f)
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Actions
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        shape = RoundedCornerShape(KxaRadius.md)
                    ) {
                        Text("Cancel", color = KxaTheme.colors.textMuted)
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Button(
                        onClick = {
                            onConfirm(BsDate(selectedYear, selectedMonth, selectedDay))
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = PurplePrimary),
                        shape = RoundedCornerShape(KxaRadius.md)
                    ) {
                        Text("Confirm BS Date", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
private fun BsWheelSelector(
    title: String,
    items: List<String>,
    selectedIndex: Int,
    onItemSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val listState = rememberLazyListState(initialFirstVisibleItemIndex = (selectedIndex - 1).coerceAtLeast(0))

    LaunchedEffect(selectedIndex) {
        if (selectedIndex >= 0 && selectedIndex < items.size) {
            listState.animateScrollToItem((selectedIndex - 1).coerceAtLeast(0))
        }
    }

    Column(
        modifier = modifier
            .clip(RoundedCornerShape(KxaRadius.md))
            .background(KxaTheme.colors.surfaceVariant)
            .border(1.dp, KxaTheme.colors.borderSubtle, RoundedCornerShape(KxaRadius.md))
            .padding(vertical = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 10.sp),
            color = PurpleLight,
            modifier = Modifier.padding(bottom = 4.dp)
        )

        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxWidth()
        ) {
            items(items.size) { index ->
                val isSelected = index == selectedIndex
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 4.dp, vertical = 2.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(if (isSelected) PurplePrimary else Color.Transparent)
                        .clickable { onItemSelected(index) }
                        .padding(vertical = 6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = items[index],
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            fontSize = if (isSelected) 13.sp else 12.sp
                        ),
                        color = if (isSelected) Color.White else KxaTheme.colors.textSecondary
                    )
                }
            }
        }
    }
}
