package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.LockReset
import androidx.compose.material.icons.filled.MeetingRoom
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.ReportProblem
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AdminAuditLog
import com.example.model.ReportedItem
import com.example.model.User
import com.example.model.WatchRoom
import com.example.ui.components.KXaButton
import com.example.ui.components.KXaGlassCard
import com.example.ui.components.KXaOutlinedButton
import com.example.ui.components.KXaTextField
import com.example.ui.components.UserAvatar
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.KxaRadius
import com.example.ui.theme.KxaSpacing
import com.example.ui.theme.KxaTheme
import com.example.ui.theme.LiveRed
import com.example.ui.theme.PurpleLight
import com.example.ui.theme.PurplePrimary
import kotlinx.coroutines.launch

enum class AdminTab(val title: String, val icon: ImageVector) {
    USERS("Users", Icons.Default.Person),
    REPORTS("Reports", Icons.Default.ReportProblem),
    ROOMS("Rooms", Icons.Default.Tv),
    AUDIT_LOGS("Audit Logs", Icons.Default.History)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminPanelScreen(
    currentUser: User,
    allUsers: List<User>,
    reports: List<ReportedItem>,
    rooms: List<WatchRoom>,
    auditLogs: List<AdminAuditLog>,
    adminMessage: String? = null,
    onClearAdminMessage: () -> Unit = {},
    onRefreshData: () -> Unit = {},
    onSuspendUser: (target: User, reason: String, type: String, until: String?) -> Unit,
    onUnsuspendUser: (target: User, reason: String) -> Unit,
    onEditUserProfile: (target: User, name: String, handle: String, phone: String?, gender: String?, role: String, status: String, reason: String) -> Unit,
    onForcePasswordReset: (target: User, email: String, disableSignIn: Boolean, reason: String) -> Unit,
    onResolveReport: (reportId: String, status: String, actionTaken: String, notes: String) -> Unit,
    onCloseRoom: (roomId: String, roomTitle: String, reason: String) -> Unit,
    onBack: () -> Unit
) {
    var selectedTab by remember { mutableStateOf(AdminTab.USERS) }
    var searchQuery by remember { mutableStateOf("") }
    var selectedUserFilter by remember { mutableStateOf("All") } // "All", "Active", "Suspended", "Staff"

    // Selected user for detail sheet
    var managingUser by remember { mutableStateOf<User?>(null) }
    var showSuspendDialog by remember { mutableStateOf<User?>(null) }
    var showUnsuspendDialog by remember { mutableStateOf<User?>(null) }
    var showEditProfileDialog by remember { mutableStateOf<User?>(null) }
    var showPasswordResetDialog by remember { mutableStateOf<User?>(null) }
    var showCloseRoomDialog by remember { mutableStateOf<WatchRoom?>(null) }
    var showModerateReportDialog by remember { mutableStateOf<ReportedItem?>(null) }

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    LaunchedEffect(adminMessage) {
        adminMessage?.let { msg ->
            scope.launch {
                snackbarHostState.showSnackbar(msg)
                onClearAdminMessage()
            }
        }
    }

    val isAdminOrMod = currentUser.role.equals("admin", ignoreCase = true) ||
            currentUser.role.equals("moderator", ignoreCase = true)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(KxaTheme.colors.background)
            .statusBarsPadding()
            .testTag("screen_admin_panel")
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Header Bar
            AdminHeader(
                currentUser = currentUser,
                onBack = onBack,
                onRefresh = onRefreshData
            )

            // Tabs Selector
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = KxaSpacing.standard, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                AdminTab.values().forEach { tab ->
                    val isSelected = selectedTab == tab
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(KxaRadius.md))
                            .background(
                                if (isSelected) Brush.horizontalGradient(
                                    listOf(PurplePrimary, CyanAccent)
                                ) else Brush.linearGradient(
                                    listOf(KxaTheme.colors.surfaceVariant, KxaTheme.colors.surfaceVariant)
                                )
                            )
                            .clickable { selectedTab = tab }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = tab.icon,
                                contentDescription = tab.title,
                                tint = if (isSelected) Color.White else KxaTheme.colors.textSecondary,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = tab.title,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) Color.White else KxaTheme.colors.textSecondary
                            )
                        }
                    }
                }
            }

            // Tab Content
            when (selectedTab) {
                AdminTab.USERS -> {
                    UsersTabContent(
                        allUsers = allUsers,
                        searchQuery = searchQuery,
                        onSearchChange = { searchQuery = it },
                        selectedFilter = selectedUserFilter,
                        onFilterChange = { selectedUserFilter = it },
                        onSelectUser = { managingUser = it }
                    )
                }

                AdminTab.REPORTS -> {
                    ReportsTabContent(
                        reports = reports,
                        onModerate = { showModerateReportDialog = it },
                        onQuickResolve = { rep ->
                            onResolveReport(rep.id, "resolved", "Dismissed by admin", "Issue resolved")
                        }
                    )
                }

                AdminTab.ROOMS -> {
                    RoomsTabContent(
                        rooms = rooms,
                        onCloseRoom = { showCloseRoomDialog = it }
                    )
                }

                AdminTab.AUDIT_LOGS -> {
                    AuditLogsTabContent(auditLogs = auditLogs)
                }
            }
        }

        // Snackbar Host for feedback
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 24.dp)
        )
    }

    // Modal Sheet for managing user details
    managingUser?.let { user ->
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        ModalBottomSheet(
            onDismissRequest = { managingUser = null },
            sheetState = sheetState,
            containerColor = KxaTheme.colors.surface
        ) {
            UserDetailSheetContent(
                targetUser = user,
                currentAdmin = currentUser,
                onSuspendClick = {
                    managingUser = null
                    showSuspendDialog = user
                },
                onUnsuspendClick = {
                    managingUser = null
                    showUnsuspendDialog = user
                },
                onEditProfileClick = {
                    managingUser = null
                    showEditProfileDialog = user
                },
                onPasswordResetClick = {
                    managingUser = null
                    showPasswordResetDialog = user
                },
                onClose = { managingUser = null }
            )
        }
    }

    // Suspend Dialog
    showSuspendDialog?.let { target ->
        SuspendUserDialog(
            targetUser = target,
            onDismiss = { showSuspendDialog = null },
            onConfirm = { reason, type, until ->
                onSuspendUser(target, reason, type, until)
                showSuspendDialog = null
            }
        )
    }

    // Unsuspend Dialog
    showUnsuspendDialog?.let { target ->
        UnsuspendUserDialog(
            targetUser = target,
            onDismiss = { showUnsuspendDialog = null },
            onConfirm = { reason ->
                onUnsuspendUser(target, reason)
                showUnsuspendDialog = null
            }
        )
    }

    // Edit Profile Dialog
    showEditProfileDialog?.let { target ->
        EditUserProfileDialog(
            targetUser = target,
            onDismiss = { showEditProfileDialog = null },
            onConfirm = { name, handle, phone, gender, role, status, reason ->
                onEditUserProfile(target, name, handle, phone, gender, role, status, reason)
                showEditProfileDialog = null
            }
        )
    }

    // Force Password Reset Dialog
    showPasswordResetDialog?.let { target ->
        ForcePasswordResetDialog(
            targetUser = target,
            onDismiss = { showPasswordResetDialog = null },
            onConfirm = { email, disableSignIn, reason ->
                onForcePasswordReset(target, email, disableSignIn, reason)
                showPasswordResetDialog = null
            }
        )
    }

    // Close Room Dialog
    showCloseRoomDialog?.let { room ->
        CloseRoomDialog(
            room = room,
            onDismiss = { showCloseRoomDialog = null },
            onConfirm = { reason ->
                onCloseRoom(room.id, room.title, reason)
                showCloseRoomDialog = null
            }
        )
    }

    // Moderate Report Dialog
    showModerateReportDialog?.let { report ->
        ModerateReportDialog(
            report = report,
            onDismiss = { showModerateReportDialog = null },
            onConfirm = { status, action, notes ->
                onResolveReport(report.id, status, action, notes)
                showModerateReportDialog = null
            }
        )
    }
}

@Composable
private fun AdminHeader(
    currentUser: User,
    onBack: () -> Unit,
    onRefresh: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = KxaSpacing.standard, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(KxaTheme.colors.surfaceVariant)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = KxaTheme.colors.textPrimary
                )
            }

            Column {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "Admin Console",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = KxaTheme.colors.textPrimary
                    )
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(KxaRadius.pill))
                            .background(PurplePrimary.copy(alpha = 0.2f))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = currentUser.role.uppercase(),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = PurpleLight
                        )
                    }
                }
                Text(
                    text = "Role-based moderation & security audit",
                    style = MaterialTheme.typography.bodySmall,
                    color = KxaTheme.colors.textSecondary
                )
            }
        }

        IconButton(
            onClick = onRefresh,
            modifier = Modifier
                .size(38.dp)
                .clip(CircleShape)
                .background(KxaTheme.colors.surfaceVariant)
        ) {
            Icon(
                imageVector = Icons.Default.Refresh,
                contentDescription = "Refresh Data",
                tint = CyanAccent
            )
        }
    }
}

// ==========================================
// TAB 1: USERS DIRECTORY
// ==========================================

@Composable
private fun UsersTabContent(
    allUsers: List<User>,
    searchQuery: String,
    onSearchChange: (String) -> Unit,
    selectedFilter: String,
    onFilterChange: (String) -> Unit,
    onSelectUser: (User) -> Unit
) {
    val filteredUsers = remember(allUsers, searchQuery, selectedFilter) {
        allUsers.filter { user ->
            val matchesQuery = searchQuery.isBlank() ||
                    user.name.contains(searchQuery, ignoreCase = true) ||
                    user.username.contains(searchQuery, ignoreCase = true) ||
                    (user.email?.contains(searchQuery, ignoreCase = true) == true) ||
                    user.id.contains(searchQuery, ignoreCase = true)

            val matchesFilter = when (selectedFilter) {
                "Active" -> !user.isSuspended
                "Suspended" -> user.isSuspended
                "Staff" -> user.role.equals("admin", ignoreCase = true) || user.role.equals("moderator", ignoreCase = true)
                else -> true
            }

            matchesQuery && matchesFilter
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = KxaSpacing.standard)
    ) {
        // Search Input
        KXaTextField(
            value = searchQuery,
            onValueChange = onSearchChange,
            placeholder = "Search user by name, @handle, email or ID...",
            leadingIcon = Icons.Default.Search,
            trailingIcon = if (searchQuery.isNotEmpty()) {
                {
                    IconButton(onClick = { onSearchChange("") }) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Clear",
                            tint = KxaTheme.colors.textSecondary
                        )
                    }
                }
            } else null,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Filter Chips
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf("All", "Active", "Suspended", "Staff").forEach { filter ->
                val isSelected = selectedFilter == filter
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(KxaRadius.pill))
                        .background(
                            if (isSelected) PurplePrimary else KxaTheme.colors.surfaceVariant
                        )
                        .clickable { onFilterChange(filter) }
                        .padding(horizontal = 14.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = filter,
                        fontSize = 12.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        color = if (isSelected) Color.White else KxaTheme.colors.textSecondary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Users Count Summary
        Text(
            text = "${filteredUsers.size} user${if (filteredUsers.size == 1) "" else "s"} found",
            style = MaterialTheme.typography.bodySmall,
            color = KxaTheme.colors.textSecondary
        )

        Spacer(modifier = Modifier.height(8.dp))

        // List of users
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 40.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(filteredUsers, key = { it.id }) { user ->
                AdminUserRow(user = user, onClick = { onSelectUser(user) })
            }
        }
    }
}

@Composable
private fun AdminUserRow(
    user: User,
    onClick: () -> Unit
) {
    KXaGlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("admin_user_card_${user.id}"),
        borderColor = if (user.isSuspended) LiveRed.copy(alpha = 0.6f) else KxaTheme.colors.borderSubtle
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                UserAvatar(
                    user = user,
                    size = 46.dp,
                    showOnlineDot = !user.isSuspended && user.isOnline
                )

                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = user.name,
                            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
                            color = KxaTheme.colors.textPrimary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        if (user.role.equals("admin", ignoreCase = true)) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(KxaRadius.pill))
                                    .background(PurplePrimary)
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "ADMIN",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        } else if (user.role.equals("moderator", ignoreCase = true)) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(KxaRadius.pill))
                                    .background(CyanAccent)
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "MOD",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Black
                                )
                            }
                        }
                    }

                    Text(
                        text = "@${user.username} • ${user.email ?: "No email"}",
                        style = MaterialTheme.typography.bodySmall,
                        color = KxaTheme.colors.textSecondary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        if (user.isSuspended) {
                            Text(
                                text = "⛔ SUSPENDED (${user.suspensionType?.uppercase() ?: "PERM"})",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = LiveRed
                            )
                        } else {
                            Text(
                                text = "✓ Active",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Medium,
                                color = CyanAccent
                            )
                        }

                        user.age?.let {
                            Text(
                                text = "• Age: $it (${user.dobCalendar})",
                                fontSize = 10.sp,
                                color = KxaTheme.colors.textSecondary
                            )
                        }
                    }
                }
            }

            Icon(
                imageVector = Icons.Default.Edit,
                contentDescription = "Manage",
                tint = CyanAccent,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

// ==========================================
// TAB 2: REPORTS & MODERATION
// ==========================================

@Composable
private fun ReportsTabContent(
    reports: List<ReportedItem>,
    onModerate: (ReportedItem) -> Unit,
    onQuickResolve: (ReportedItem) -> Unit
) {
    if (reports.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = CyanAccent,
                    modifier = Modifier.size(48.dp)
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "No pending reports",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = KxaTheme.colors.textPrimary
                )
                Text(
                    text = "Community is peaceful and healthy ✨",
                    style = MaterialTheme.typography.bodySmall,
                    color = KxaTheme.colors.textSecondary
                )
            }
        }
        return
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = KxaSpacing.standard),
        contentPadding = PaddingValues(bottom = 40.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(reports, key = { it.id }) { item ->
            ReportCard(
                report = item,
                onModerate = { onModerate(item) },
                onDismiss = { onQuickResolve(item) }
            )
        }
    }
}

@Composable
private fun ReportCard(
    report: ReportedItem,
    onModerate: () -> Unit,
    onDismiss: () -> Unit
) {
    KXaGlassCard(
        modifier = Modifier.fillMaxWidth(),
        borderColor = if (report.status == "pending") LiveRed.copy(alpha = 0.5f) else KxaTheme.colors.borderSubtle
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = "Report",
                        tint = if (report.status == "pending") LiveRed else CyanAccent,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "Target: ${report.targetType.uppercase()} • ${report.targetTitle}",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                        color = KxaTheme.colors.textPrimary
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(KxaRadius.pill))
                        .background(
                            if (report.status == "pending") LiveRed.copy(alpha = 0.2f) else CyanAccent.copy(alpha = 0.2f)
                        )
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = report.status.uppercase(),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (report.status == "pending") LiveRed else CyanAccent
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Reason: ${report.reason}",
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                color = PurpleLight
            )

            if (report.details.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = report.details,
                    style = MaterialTheme.typography.bodySmall,
                    color = KxaTheme.colors.textSecondary
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Reported by @${report.reporterName} • ${report.timestamp}",
                    style = MaterialTheme.typography.bodySmall,
                    color = KxaTheme.colors.textMuted,
                    fontSize = 11.sp
                )

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    TextButton(onClick = onDismiss) {
                        Text("Dismiss", color = KxaTheme.colors.textSecondary, fontSize = 12.sp)
                    }
                    KXaButton(
                        text = "Take Action",
                        onClick = onModerate,
                        modifier = Modifier.height(34.dp)
                    )
                }
            }
        }
    }
}

// ==========================================
// TAB 3: WATCH ROOMS MANAGEMENT
// ==========================================

@Composable
private fun RoomsTabContent(
    rooms: List<WatchRoom>,
    onCloseRoom: (WatchRoom) -> Unit
) {
    if (rooms.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "No active watch rooms right now",
                style = MaterialTheme.typography.bodyMedium,
                color = KxaTheme.colors.textSecondary
            )
        }
        return
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = KxaSpacing.standard),
        contentPadding = PaddingValues(bottom = 40.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(rooms, key = { it.id }) { room ->
            KXaGlassCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = room.title,
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = KxaTheme.colors.textPrimary
                            )
                            Text(
                                text = "Code: ${room.code} • Category: ${room.category}",
                                style = MaterialTheme.typography.bodySmall,
                                color = PurpleLight
                            )
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(KxaRadius.pill))
                                .background(CyanAccent.copy(alpha = 0.15f))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "👥 ${room.participants.size} watching",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = CyanAccent
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Video: ${room.videoTitle} (${room.videoSource})",
                        style = MaterialTheme.typography.bodySmall,
                        color = KxaTheme.colors.textSecondary
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Host: @${room.host.username}",
                            style = MaterialTheme.typography.bodySmall,
                            color = KxaTheme.colors.textMuted
                        )

                        KXaOutlinedButton(
                            text = "Close Room",
                            onClick = { onCloseRoom(room) },
                            icon = Icons.Default.Close,
                            modifier = Modifier.height(34.dp)
                        )
                    }
                }
            }
        }
    }
}

// ==========================================
// TAB 4: AUDIT LOGS
// ==========================================

@Composable
private fun AuditLogsTabContent(
    auditLogs: List<AdminAuditLog>
) {
    if (auditLogs.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "No audit log records yet.",
                style = MaterialTheme.typography.bodyMedium,
                color = KxaTheme.colors.textSecondary
            )
        }
        return
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = KxaSpacing.standard),
        contentPadding = PaddingValues(bottom = 40.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(auditLogs, key = { it.id }) { log ->
            KXaGlassCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(KxaRadius.pill))
                                .background(PurplePrimary.copy(alpha = 0.2f))
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = log.action,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = PurpleLight,
                                fontFamily = FontFamily.Monospace
                            )
                        }

                        Text(
                            text = log.timestamp,
                            fontSize = 11.sp,
                            color = KxaTheme.colors.textMuted
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Admin: ${log.adminName}",
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                        color = KxaTheme.colors.textPrimary
                    )

                    if (log.targetUserName != null) {
                        Text(
                            text = "Target: @${log.targetUserName}",
                            style = MaterialTheme.typography.bodySmall,
                            color = CyanAccent
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "Reason: ${log.reason}",
                        style = MaterialTheme.typography.bodySmall,
                        color = KxaTheme.colors.textSecondary
                    )

                    if (log.details.isNotBlank()) {
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Details: ${log.details}",
                            style = MaterialTheme.typography.bodySmall,
                            color = KxaTheme.colors.textMuted,
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }
    }
}

// ==========================================
// USER DETAIL MODAL SHEET
// ==========================================

@Composable
private fun UserDetailSheetContent(
    targetUser: User,
    currentAdmin: User,
    onSuspendClick: () -> Unit,
    onUnsuspendClick: () -> Unit,
    onEditProfileClick: () -> Unit,
    onPasswordResetClick: () -> Unit,
    onClose: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // User Profile Summary
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            UserAvatar(user = targetUser, size = 64.dp)
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = targetUser.name,
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = KxaTheme.colors.textPrimary
                )
                Text(
                    text = "@${targetUser.username}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = PurpleLight
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(KxaRadius.pill))
                            .background(if (targetUser.isSuspended) LiveRed else CyanAccent)
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = if (targetUser.isSuspended) "SUSPENDED" else "ACTIVE",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(KxaRadius.pill))
                            .background(PurplePrimary.copy(alpha = 0.2f))
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "ROLE: ${targetUser.role.uppercase()}",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = PurpleLight
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))
        HorizontalDivider(color = KxaTheme.colors.borderSubtle)
        Spacer(modifier = Modifier.height(16.dp))

        // Profile Details Grid
        Text(
            text = "PROFILE ATTRIBUTES",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = KxaTheme.colors.textSecondary,
            letterSpacing = 1.sp
        )
        Spacer(modifier = Modifier.height(8.dp))

        DetailRow(label = "Email Address", value = targetUser.email ?: "Not set")
        DetailRow(label = "Phone Number", value = targetUser.phone ?: "Not set")
        DetailRow(label = "Gender", value = targetUser.gender ?: "Not specified")
        DetailRow(
            label = "Date of Birth",
            value = targetUser.dateOfBirth?.let { "$it (${targetUser.dobCalendar}) • Age ${targetUser.age ?: "16+"}" } ?: "Not set"
        )
        DetailRow(label = "Status Message", value = targetUser.statusMessage)
        DetailRow(label = "Parties Hosted", value = "${targetUser.partiesHosted} rooms")
        DetailRow(label = "Hours Watched", value = "${targetUser.hoursWatched} hrs")
        DetailRow(label = "Member Since", value = targetUser.createdAt ?: "2024")

        if (targetUser.isSuspended) {
            Spacer(modifier = Modifier.height(12.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(KxaRadius.md))
                    .background(LiveRed.copy(alpha = 0.15f))
                    .border(1.dp, LiveRed.copy(alpha = 0.5f), RoundedCornerShape(KxaRadius.md))
                    .padding(12.dp)
            ) {
                Column {
                    Text(
                        text = "⛔ Suspension Details",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                        color = LiveRed
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Reason: ${targetUser.suspensionReason ?: "Violation of terms"}",
                        style = MaterialTheme.typography.bodySmall,
                        color = KxaTheme.colors.textPrimary
                    )
                    Text(
                        text = "Type: ${targetUser.suspensionType ?: "Permanent"}${targetUser.suspensionUntil?.let { " (Expires: $it)" } ?: ""}",
                        style = MaterialTheme.typography.bodySmall,
                        color = KxaTheme.colors.textSecondary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))
        HorizontalDivider(color = KxaTheme.colors.borderSubtle)
        Spacer(modifier = Modifier.height(16.dp))

        // Admin Actions
        Text(
            text = "MODERATOR CONTROLS",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = KxaTheme.colors.textSecondary,
            letterSpacing = 1.sp
        )
        Spacer(modifier = Modifier.height(12.dp))

        // Edit Profile Button
        KXaOutlinedButton(
            text = "Edit Profile Information",
            icon = Icons.Default.Edit,
            onClick = onEditProfileClick,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Force Password Reset Button
        KXaOutlinedButton(
            text = "Force Password Reset via Email",
            icon = Icons.Default.LockReset,
            onClick = onPasswordResetClick,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Suspend / Unsuspend Button
        if (targetUser.isSuspended) {
            KXaButton(
                text = "Unsuspend Account",
                icon = Icons.Default.CheckCircle,
                onClick = onUnsuspendClick,
                modifier = Modifier.fillMaxWidth()
            )
        } else {
            KXaButton(
                text = "Suspend Account (Temporary / Permanent)",
                icon = Icons.Default.Block,
                onClick = onSuspendClick,
                modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = KxaTheme.colors.textSecondary
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
            color = KxaTheme.colors.textPrimary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

// ==========================================
// DIALOGS & CONFIRMATIONS
// ==========================================

@Composable
private fun SuspendUserDialog(
    targetUser: User,
    onDismiss: () -> Unit,
    onConfirm: (reason: String, type: String, until: String?) -> Unit
) {
    var reason by remember { mutableStateOf("") }
    var suspensionType by remember { mutableStateOf("temporary") } // "temporary", "permanent"
    var durationOption by remember { mutableStateOf("7 Days") } // "24 Hours", "3 Days", "7 Days", "30 Days"
    var isError by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Icon(imageVector = Icons.Default.Block, contentDescription = null, tint = LiveRed)
                Text("Suspend @${targetUser.username}")
            }
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Suspending will block the user from logging in and accessing rooms.",
                    style = MaterialTheme.typography.bodySmall,
                    color = KxaTheme.colors.textSecondary
                )
                Spacer(modifier = Modifier.height(14.dp))

                Text("Suspension Type", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = KxaTheme.colors.textPrimary)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = suspensionType == "temporary",
                        onClick = { suspensionType = "temporary" },
                        colors = RadioButtonDefaults.colors(selectedColor = PurplePrimary)
                    )
                    Text("Temporary", fontSize = 13.sp, color = KxaTheme.colors.textPrimary)

                    Spacer(modifier = Modifier.width(16.dp))

                    RadioButton(
                        selected = suspensionType == "permanent",
                        onClick = { suspensionType = "permanent" },
                        colors = RadioButtonDefaults.colors(selectedColor = LiveRed)
                    )
                    Text("Permanent", fontSize = 13.sp, color = KxaTheme.colors.textPrimary)
                }

                if (suspensionType == "temporary") {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Duration", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = KxaTheme.colors.textPrimary)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf("24 Hours", "3 Days", "7 Days", "30 Days").forEach { dur ->
                            val isSel = durationOption == dur
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(KxaRadius.sm))
                                    .background(if (isSel) PurplePrimary else KxaTheme.colors.surfaceVariant)
                                    .clickable { durationOption = dur }
                                    .padding(vertical = 6.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = dur,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSel) Color.White else KxaTheme.colors.textSecondary
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text("Reason (Logged in Audit Log) *", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = KxaTheme.colors.textPrimary)
                Spacer(modifier = Modifier.height(4.dp))
                KXaTextField(
                    value = reason,
                    onValueChange = {
                        reason = it
                        if (it.isNotBlank()) isError = false
                    },
                    placeholder = "e.g. Abusive behavior, spamming chat...",
                    isError = isError,
                    errorMessage = if (isError) "Reason is required for audit logs" else null,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            KXaButton(
                text = "Confirm Suspension",
                onClick = {
                    if (reason.isBlank()) {
                        isError = true
                    } else {
                        val until = if (suspensionType == "temporary") {
                            when (durationOption) {
                                "24 Hours" -> "2026-08-20"
                                "3 Days" -> "2026-08-22"
                                "7 Days" -> "2026-08-26"
                                else -> "2026-09-18"
                            }
                        } else null
                        onConfirm(reason.trim(), suspensionType, until)
                    }
                },
                modifier = Modifier.height(40.dp)
            )
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = KxaTheme.colors.textSecondary)
            }
        }
    )
}

@Composable
private fun UnsuspendUserDialog(
    targetUser: User,
    onDismiss: () -> Unit,
    onConfirm: (reason: String) -> Unit
) {
    var reason by remember { mutableStateOf("") }
    var isError by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Unsuspend @${targetUser.username}") },
        text = {
            Column {
                Text(
                    text = "This will restore full login, watch room, and chat access for this account.",
                    style = MaterialTheme.typography.bodySmall,
                    color = KxaTheme.colors.textSecondary
                )
                Spacer(modifier = Modifier.height(14.dp))
                Text("Reason *", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = KxaTheme.colors.textPrimary)
                Spacer(modifier = Modifier.height(4.dp))
                KXaTextField(
                    value = reason,
                    onValueChange = {
                        reason = it
                        if (it.isNotBlank()) isError = false
                    },
                    placeholder = "e.g. Appeal approved, time served...",
                    isError = isError,
                    errorMessage = if (isError) "Reason is required" else null,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            KXaButton(
                text = "Unsuspend",
                onClick = {
                    if (reason.isBlank()) isError = true else onConfirm(reason.trim())
                },
                modifier = Modifier.height(40.dp)
            )
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = KxaTheme.colors.textSecondary)
            }
        }
    )
}

@Composable
private fun EditUserProfileDialog(
    targetUser: User,
    onDismiss: () -> Unit,
    onConfirm: (name: String, handle: String, phone: String?, gender: String?, role: String, status: String, reason: String) -> Unit
) {
    var name by remember { mutableStateOf(targetUser.name) }
    var handle by remember { mutableStateOf(targetUser.username) }
    var phone by remember { mutableStateOf(targetUser.phone ?: "") }
    var gender by remember { mutableStateOf(targetUser.gender ?: "Male") }
    var role by remember { mutableStateOf(targetUser.role) }
    var statusMessage by remember { mutableStateOf(targetUser.statusMessage) }
    var reason by remember { mutableStateOf("") }
    var isError by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit User Profile (@${targetUser.username})") },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = "Passwords are never exposed or editable by administrators.",
                    style = MaterialTheme.typography.bodySmall,
                    color = PurpleLight
                )
                Spacer(modifier = Modifier.height(12.dp))

                Text("Full Name", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = KxaTheme.colors.textPrimary)
                KXaTextField(value = name, onValueChange = { name = it }, placeholder = "Full name", modifier = Modifier.fillMaxWidth())

                Spacer(modifier = Modifier.height(8.dp))

                Text("Username / Handle", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = KxaTheme.colors.textPrimary)
                KXaTextField(value = handle, onValueChange = { handle = it }, placeholder = "Username", modifier = Modifier.fillMaxWidth())

                Spacer(modifier = Modifier.height(8.dp))

                Text("Phone", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = KxaTheme.colors.textPrimary)
                KXaTextField(value = phone, onValueChange = { phone = it }, placeholder = "+977-XXXXXXXXXX", modifier = Modifier.fillMaxWidth())

                Spacer(modifier = Modifier.height(8.dp))

                Text("Role", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = KxaTheme.colors.textPrimary)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf("user", "moderator", "admin").forEach { r ->
                        val isSel = role.equals(r, ignoreCase = true)
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(KxaRadius.sm))
                                .background(if (isSel) PurplePrimary else KxaTheme.colors.surfaceVariant)
                                .clickable { role = r }
                                .padding(vertical = 6.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = r.uppercase(),
                                fontSize = 11.sp,
                                fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSel) Color.White else KxaTheme.colors.textSecondary
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text("Bio / Status Message", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = KxaTheme.colors.textPrimary)
                KXaTextField(value = statusMessage, onValueChange = { statusMessage = it }, placeholder = "Status...", modifier = Modifier.fillMaxWidth())

                Spacer(modifier = Modifier.height(8.dp))

                Text("Reason for Profile Edit *", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = KxaTheme.colors.textPrimary)
                KXaTextField(
                    value = reason,
                    onValueChange = {
                        reason = it
                        if (it.isNotBlank()) isError = false
                    },
                    placeholder = "Audit reason...",
                    isError = isError,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            KXaButton(
                text = "Save Changes",
                onClick = {
                    if (reason.isBlank() || name.isBlank() || handle.isBlank()) {
                        isError = true
                    } else {
                        onConfirm(name.trim(), handle.trim(), phone.ifBlank { null }, gender, role, statusMessage.trim(), reason.trim())
                    }
                },
                modifier = Modifier.height(40.dp)
            )
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = KxaTheme.colors.textSecondary)
            }
        }
    )
}

@Composable
private fun ForcePasswordResetDialog(
    targetUser: User,
    onDismiss: () -> Unit,
    onConfirm: (email: String, disableSignIn: Boolean, reason: String) -> Unit
) {
    var email by remember { mutableStateOf(targetUser.email ?: "") }
    var disableSignInUntilReset by remember { mutableStateOf(false) }
    var reason by remember { mutableStateOf("") }
    var isError by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Icon(imageVector = Icons.Default.LockReset, contentDescription = null, tint = CyanAccent)
                Text("Force Password Reset")
            }
        },
        text = {
            Column {
                Text(
                    text = "A secure reset link will be sent to the user's email. Passwords are never seen by anyone.",
                    style = MaterialTheme.typography.bodySmall,
                    color = KxaTheme.colors.textSecondary
                )
                Spacer(modifier = Modifier.height(12.dp))

                Text("Recipient Email *", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = KxaTheme.colors.textPrimary)
                KXaTextField(
                    value = email,
                    onValueChange = { email = it },
                    placeholder = "user@example.com",
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = disableSignInUntilReset,
                        onCheckedChange = { disableSignInUntilReset = it },
                        colors = CheckboxDefaults.colors(checkedColor = PurplePrimary)
                    )
                    Text(
                        text = "Disable sign-in until password reset",
                        fontSize = 12.sp,
                        color = KxaTheme.colors.textPrimary
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text("Audit Reason *", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = KxaTheme.colors.textPrimary)
                KXaTextField(
                    value = reason,
                    onValueChange = {
                        reason = it
                        if (it.isNotBlank()) isError = false
                    },
                    placeholder = "e.g. Account recovery requested...",
                    isError = isError,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            KXaButton(
                text = "Send Reset Email",
                onClick = {
                    if (email.isBlank() || reason.isBlank()) {
                        isError = true
                    } else {
                        onConfirm(email.trim(), disableSignInUntilReset, reason.trim())
                    }
                },
                modifier = Modifier.height(40.dp)
            )
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = KxaTheme.colors.textSecondary)
            }
        }
    )
}

@Composable
private fun CloseRoomDialog(
    room: WatchRoom,
    onDismiss: () -> Unit,
    onConfirm: (reason: String) -> Unit
) {
    var reason by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Close Watch Room") },
        text = {
            Column {
                Text(
                    text = "Are you sure you want to close \"${room.title}\"? All active viewers will be disconnected.",
                    style = MaterialTheme.typography.bodySmall,
                    color = KxaTheme.colors.textSecondary
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text("Reason *", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = KxaTheme.colors.textPrimary)
                KXaTextField(
                    value = reason,
                    onValueChange = { reason = it },
                    placeholder = "e.g. Inappropriate content, room violation...",
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            KXaButton(
                text = "Close Room",
                onClick = { onConfirm(reason.ifBlank { "Closed by moderator" }) },
                modifier = Modifier.height(40.dp)
            )
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = KxaTheme.colors.textSecondary)
            }
        }
    )
}

@Composable
private fun ModerateReportDialog(
    report: ReportedItem,
    onDismiss: () -> Unit,
    onConfirm: (status: String, action: String, notes: String) -> Unit
) {
    var status by remember { mutableStateOf("resolved") }
    var action by remember { mutableStateOf("Issued warning") }
    var notes by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Moderate Report (#${report.id})") },
        text = {
            Column {
                Text(
                    text = "Target: ${report.targetType} - ${report.targetTitle}",
                    style = MaterialTheme.typography.bodySmall,
                    color = PurpleLight
                )
                Text(
                    text = "Reported Reason: ${report.reason}",
                    style = MaterialTheme.typography.bodySmall,
                    color = KxaTheme.colors.textPrimary
                )
                Spacer(modifier = Modifier.height(12.dp))

                Text("Resolution Status", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = KxaTheme.colors.textPrimary)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf("resolved", "dismissed").forEach { s ->
                        val isSel = status == s
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(KxaRadius.sm))
                                .background(if (isSel) PurplePrimary else KxaTheme.colors.surfaceVariant)
                                .clickable { status = s }
                                .padding(vertical = 6.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = s.uppercase(),
                                fontSize = 11.sp,
                                fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSel) Color.White else KxaTheme.colors.textSecondary
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text("Action Taken", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = KxaTheme.colors.textPrimary)
                KXaTextField(
                    value = action,
                    onValueChange = { action = it },
                    placeholder = "e.g. Warning sent, content removed...",
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text("Moderator Notes", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = KxaTheme.colors.textPrimary)
                KXaTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    placeholder = "Internal notes...",
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            KXaButton(
                text = "Apply Resolution",
                onClick = { onConfirm(status, action, notes) },
                modifier = Modifier.height(40.dp)
            )
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = KxaTheme.colors.textSecondary)
            }
        }
    )
}
