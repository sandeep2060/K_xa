package com.example.viewmodel

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.MockData
import com.example.data.SupabaseRepository
import com.example.model.AdminAuditLog
import com.example.model.AuthState
import com.example.model.ChatMessage
import com.example.model.FlyingReaction
import com.example.model.Friend
import com.example.model.ReportedItem
import com.example.model.User
import com.example.model.WatchRoom
import com.example.ui.theme.ThemeMode
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.random.Random

enum class AppScreen {
    SPLASH,
    ONBOARDING,
    LOGIN,
    POST_LOGIN_SETUP,
    HOME,
    FRIENDS,
    CREATE_ROOM,
    JOIN_ROOM,
    WATCH_ROOM,
    PRIVATE_CHAT,
    PROFILE,
    ADMIN_PANEL
}

class AppViewModel @JvmOverloads constructor(
    application: Application,
    private val repository: SupabaseRepository = SupabaseRepository()
) : AndroidViewModel(application) {

    private val prefs = application.getSharedPreferences("kxa_preferences", Context.MODE_PRIVATE)

    private val _currentScreen = MutableStateFlow(AppScreen.SPLASH)
    val currentScreen: StateFlow<AppScreen> = _currentScreen.asStateFlow()

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    private val _currentUser = MutableStateFlow(
        User(
            id = "user_guest",
            name = "Guest User",
            username = "guest",
            avatarInitial = "G",
            statusMessage = "Ready to stream",
            partiesHosted = 0,
            hoursWatched = 0,
            friendsCount = 0
        )
    )
    val currentUser: StateFlow<User> = _currentUser.asStateFlow()

    private val _rooms = MutableStateFlow(MockData.activeRooms)
    val rooms: StateFlow<List<WatchRoom>> = _rooms.asStateFlow()

    private val _friends = MutableStateFlow(MockData.friendsList)
    val friends: StateFlow<List<Friend>> = _friends.asStateFlow()

    private val _activeRoom = MutableStateFlow<WatchRoom?>(MockData.activeRooms.firstOrNull())
    val activeRoom: StateFlow<WatchRoom?> = _activeRoom.asStateFlow()

    private val _roomChat = MutableStateFlow(MockData.initialRoomChat)
    val roomChat: StateFlow<List<ChatMessage>> = _roomChat.asStateFlow()

    private val _selectedFriendForChat = MutableStateFlow(MockData.friendsList[0])
    val selectedFriendForChat: StateFlow<Friend> = _selectedFriendForChat.asStateFlow()

    private val _privateChat = MutableStateFlow(MockData.initialPrivateChat)
    val privateChat: StateFlow<List<ChatMessage>> = _privateChat.asStateFlow()

    // Video Player & Room Controls State
    private val _isPlayingVideo = MutableStateFlow(true)
    val isPlayingVideo: StateFlow<Boolean> = _isPlayingVideo.asStateFlow()

    private val _videoPositionSeconds = MutableStateFlow(342)
    val videoPositionSeconds: StateFlow<Int> = _videoPositionSeconds.asStateFlow()

    private val _isMicMuted = MutableStateFlow(false)
    val isMicMuted: StateFlow<Boolean> = _isMicMuted.asStateFlow()

    private val _isCameraOn = MutableStateFlow(false)
    val isCameraOn: StateFlow<Boolean> = _isCameraOn.asStateFlow()

    private val _isSyncedWithHost = MutableStateFlow(true)
    val isSyncedWithHost: StateFlow<Boolean> = _isSyncedWithHost.asStateFlow()

    // Flying Reactions
    private val _flyingReactions = MutableStateFlow<List<FlyingReaction>>(emptyList())
    val flyingReactions: StateFlow<List<FlyingReaction>> = _flyingReactions.asStateFlow()

    // Initial check for auth flow
    private val _isSignUpMode = MutableStateFlow(false)
    val isSignUpMode: StateFlow<Boolean> = _isSignUpMode.asStateFlow()

    init {
        // Continuous simulated video timeline progression for active playback
        viewModelScope.launch {
            while (true) {
                delay(1000)
                if (_isPlayingVideo.value) {
                    _videoPositionSeconds.update { pos ->
                        val active = _activeRoom.value
                        val maxDur = active?.totalDurationSeconds ?: 1455
                        if (pos >= maxDur) 0 else pos + 1
                    }
                }
            }
        }
    }

    fun navigateTo(screen: AppScreen, isSignUp: Boolean = false) {
        if (screen == AppScreen.LOGIN) {
            _isSignUpMode.value = isSignUp
        }
        _currentScreen.value = screen
    }

    fun completeSplash() {
        viewModelScope.launch {
            checkAuthSession()
        }
    }

    suspend fun checkAuthSession() {
        _authState.value = AuthState.Loading
        try {
            val session = repository.getCurrentSession()
            if (session != null) {
                val user = repository.fetchUserProfile(session.user?.id ?: "", session.user?.email ?: "")
                _currentUser.value = user
                _authState.value = AuthState.Authenticated(user)
                if (!user.onboardingCompleted) {
                    _currentScreen.value = AppScreen.POST_LOGIN_SETUP
                } else {
                    _currentScreen.value = AppScreen.HOME
                }
            } else {
                _authState.value = AuthState.Unauthenticated
                _currentScreen.value = AppScreen.ONBOARDING
            }
        } catch (e: Exception) {
            Log.w("AppViewModel", "Session restoration check encountered exception: ${e.message}")
            if (e.message?.contains("suspended", ignoreCase = true) == true) {
                _authState.value = AuthState.Suspended(e.message ?: "Account suspended")
            } else {
                _authState.value = AuthState.Unauthenticated
                _currentScreen.value = AppScreen.ONBOARDING
            }
        }
    }

    suspend fun signIn(email: String, password: String): Result<Unit> {
        _authState.value = AuthState.Loading
        val result = repository.signIn(email, password)
        return result.fold(
            onSuccess = { user ->
                _currentUser.value = user
                _authState.value = AuthState.Authenticated(user)
                if (!user.onboardingCompleted) {
                    _currentScreen.value = AppScreen.POST_LOGIN_SETUP
                } else {
                    _currentScreen.value = AppScreen.HOME
                }
                Result.success(Unit)
            },
            onFailure = { error ->
                _authState.value = AuthState.Error(error.localizedMessage ?: "Sign in failed")
                Result.failure(error)
            }
        )
    }

    suspend fun signUp(
        email: String,
        password: String,
        username: String,
        fullName: String,
        dateOfBirth: String? = null,
        dobCalendar: String = "AD",
        age: Int? = null
    ): Result<Unit> {
        _authState.value = AuthState.Loading
        val result = repository.signUp(
            email = email,
            password = password,
            username = username,
            fullName = fullName,
            dateOfBirth = dateOfBirth,
            dobCalendar = dobCalendar,
            age = age
        )
        return result.fold(
            onSuccess = { user ->
                _currentUser.value = user
                _authState.value = AuthState.Authenticated(user)
                _currentScreen.value = AppScreen.POST_LOGIN_SETUP
                Result.success(Unit)
            },
            onFailure = { error ->
                _authState.value = AuthState.Error(error.localizedMessage ?: "Sign up failed")
                Result.failure(error)
            }
        )
    }

    suspend fun uploadAvatar(imageBytes: ByteArray, mimeType: String = "image/jpeg"): Result<String> {
        val result = repository.uploadAvatar(_currentUser.value.id, imageBytes, mimeType)
        result.onSuccess { publicUrl ->
            _currentUser.update { it.copy(avatarUrl = publicUrl) }
        }
        return result
    }

    fun completePostLoginOnboarding() {
        _currentUser.update { it.copy(onboardingCompleted = true) }
        _currentScreen.value = AppScreen.HOME
        viewModelScope.launch {
            repository.completeOnboarding(_currentUser.value.id)
        }
    }

    fun addSuggestedFriend(user: User) {
        val exists = _friends.value.any { it.user.id == user.id }
        if (!exists) {
            val newFriend = Friend(
                user = user,
                isBestFriend = false,
                lastActive = "Just now",
                mutualFriends = 1
            )
            _friends.update { listOf(newFriend) + it }
            _currentUser.update { it.copy(friendsCount = it.friendsCount + 1) }
        }
    }

    fun logout() {
        viewModelScope.launch {
            repository.signOut()
            _currentUser.value = User(
                id = "user_guest",
                name = "Guest User",
                username = "guest",
                avatarInitial = "G"
            )
            _authState.value = AuthState.Unauthenticated
            _currentScreen.value = AppScreen.ONBOARDING
        }
    }

    fun openWatchRoom(room: WatchRoom) {
        _activeRoom.value = room
        _videoPositionSeconds.value = room.currentPositionSeconds
        _isPlayingVideo.value = room.isPlaying
        _currentScreen.value = AppScreen.WATCH_ROOM
    }

    fun openPrivateChat(friend: Friend) {
        _selectedFriendForChat.value = friend
        _currentScreen.value = AppScreen.PRIVATE_CHAT
    }

    fun togglePlayPause() {
        _isPlayingVideo.update { !it }
    }

    fun seekTo(seconds: Int) {
        _videoPositionSeconds.value = seconds
    }

    fun toggleMic() {
        _isMicMuted.update { !it }
    }

    fun toggleCamera() {
        _isCameraOn.update { !it }
    }

    fun syncPlayback() {
        viewModelScope.launch {
            _isSyncedWithHost.value = false
            delay(400)
            _isSyncedWithHost.value = true
        }
    }

    fun sendRoomMessage(text: String) {
        if (text.isBlank()) return
        val current = _currentUser.value
        val active = _activeRoom.value
        val newMsg = ChatMessage(
            id = "msg_${System.currentTimeMillis()}",
            sender = current,
            text = text.trim(),
            timestamp = "Just now",
            isHost = active?.host?.id == current.id
        )
        _roomChat.update { it + newMsg }

        // Sync with backend repository asynchronously
        if (active != null) {
            viewModelScope.launch {
                repository.sendRoomMessage(active.id, current, text.trim())
            }
        }
    }

    fun sendPrivateMessage(text: String) {
        if (text.isBlank()) return
        val newMsg = ChatMessage(
            id = "pm_${System.currentTimeMillis()}",
            sender = _currentUser.value,
            text = text.trim(),
            timestamp = "Just now"
        )
        _privateChat.update { it + newMsg }
    }

    fun triggerReaction(emoji: String) {
        val reaction = FlyingReaction(
            id = System.currentTimeMillis() + Random.nextLong(1000),
            emoji = emoji,
            startXRatio = Random.nextFloat().coerceIn(0.2f, 0.8f),
            scale = Random.nextFloat().coerceIn(0.9f, 1.4f)
        )
        _flyingReactions.update { it + reaction }

        // Automatically clean up reaction after animation
        viewModelScope.launch {
            delay(2200)
            _flyingReactions.update { list -> list.filterNot { it.id == reaction.id } }
        }
    }

    fun createNewRoom(
        title: String,
        videoUrl: String,
        videoSource: String,
        category: String,
        isPrivate: Boolean,
        pin: String
    ): WatchRoom {
        val code = "KX-" + (1000..9999).random()
        val user = _currentUser.value
        val newRoom = WatchRoom(
            id = "room_${System.currentTimeMillis()}",
            code = code,
            title = if (title.isBlank()) "${user.name}'s Watch Party" else title,
            host = user,
            videoTitle = if (videoUrl.contains("youtu", ignoreCase = true)) "Featured Stream - Online Watchlist" else "Live Synced Media Stream",
            videoSource = videoSource,
            videoUrl = if (videoUrl.isBlank()) "https://youtube.com/watch?v=kxa_default" else videoUrl,
            currentPositionSeconds = 0,
            totalDurationSeconds = 1800,
            isPlaying = true,
            participants = listOf(user),
            category = category,
            isPrivate = isPrivate,
            pinCode = pin,
            maxParticipants = 12
        )
        _rooms.update { listOf(newRoom) + it }
        openWatchRoom(newRoom)

        viewModelScope.launch {
            repository.createRoom(newRoom)
        }

        return newRoom
    }

    fun joinRoomByCode(code: String): Boolean {
        val cleanCode = code.trim().uppercase()
        val found = _rooms.value.find { it.code.uppercase() == cleanCode || it.code.replace("-", "").uppercase() == cleanCode.replace("-", "") }
        if (found != null) {
            val updatedRoom = found.copy(
                participants = if (found.participants.any { it.id == _currentUser.value.id }) found.participants else found.participants + _currentUser.value
            )
            _rooms.update { list -> list.map { if (it.id == found.id) updatedRoom else it } }
            openWatchRoom(updatedRoom)
            return true
        }
        // If not matching default code, join as dynamic private room
        val dynamicRoom = WatchRoom(
            id = "room_join_${System.currentTimeMillis()}",
            code = if (cleanCode.startsWith("KX-")) cleanCode else "KX-$cleanCode",
            title = "Party Room ($cleanCode)",
            host = MockData.friendMaya,
            videoTitle = "Synchronized Stream Broadcast",
            videoSource = "YouTube",
            videoUrl = "https://youtube.com/watch?v=synced_feed",
            currentPositionSeconds = 120,
            totalDurationSeconds = 2400,
            isPlaying = true,
            participants = listOf(MockData.friendMaya, _currentUser.value),
            category = "Live Stream",
            isPrivate = false
        )
        _rooms.update { listOf(dynamicRoom) + it }
        openWatchRoom(dynamicRoom)
        return true
    }

    private val _themeMode = MutableStateFlow(
        try {
            val savedName = prefs.getString("theme_mode", ThemeMode.DARK.name) ?: ThemeMode.DARK.name
            ThemeMode.valueOf(savedName)
        } catch (e: Exception) {
            ThemeMode.DARK
        }
    )
    val themeMode: StateFlow<ThemeMode> = _themeMode.asStateFlow()

    fun setThemeMode(mode: ThemeMode) {
        _themeMode.value = mode
        prefs.edit().putString("theme_mode", mode.name).apply()
    }

    // Admin & Moderation State
    private val _allUsers = MutableStateFlow<List<User>>(MockData.allDirectoryUsers)
    val allUsers: StateFlow<List<User>> = _allUsers.asStateFlow()

    private val _reports = MutableStateFlow<List<ReportedItem>>(MockData.initialReports)
    val reports: StateFlow<List<ReportedItem>> = _reports.asStateFlow()

    private val _auditLogs = MutableStateFlow<List<AdminAuditLog>>(MockData.initialAuditLogs)
    val auditLogs: StateFlow<List<AdminAuditLog>> = _auditLogs.asStateFlow()

    private val _adminMessage = MutableStateFlow<String?>(null)
    val adminMessage: StateFlow<String?> = _adminMessage.asStateFlow()

    private val _isAdminLoading = MutableStateFlow(false)
    val isAdminLoading: StateFlow<Boolean> = _isAdminLoading.asStateFlow()

    fun clearAdminMessage() {
        _adminMessage.value = null
    }

    fun loadAdminData() {
        viewModelScope.launch {
            _isAdminLoading.value = true
            try {
                val usersRes: Result<List<User>> = repository.fetchAllUsers()
                usersRes.onSuccess { list: List<User> ->
                    if (list.isNotEmpty()) _allUsers.value = list
                }
                val repRes: Result<List<ReportedItem>> = repository.fetchReports()
                repRes.onSuccess { list: List<ReportedItem> ->
                    if (list.isNotEmpty()) _reports.value = list
                }
                val logsRes: Result<List<AdminAuditLog>> = repository.fetchAuditLogs()
                logsRes.onSuccess { list: List<AdminAuditLog> ->
                    if (list.isNotEmpty()) _auditLogs.value = list
                }
            } catch (e: Exception) {
                Log.w("AppViewModel", "Failed to load admin data: ${e.message}")
            } finally {
                _isAdminLoading.value = false
            }
        }
    }

    fun suspendUser(
        targetUser: User,
        reason: String,
        suspensionType: String,
        suspensionUntil: String? = null
    ) {
        val admin = _currentUser.value
        val updatedUser = targetUser.copy(
            isSuspended = true,
            suspensionReason = reason,
            suspensionType = suspensionType,
            suspensionUntil = suspensionUntil
        )
        _allUsers.update { list -> list.map { if (it.id == targetUser.id) updatedUser else it } }

        val newLog = AdminAuditLog(
            id = "log_${System.currentTimeMillis()}",
            adminId = admin.id,
            adminName = "${admin.name} (${admin.role.uppercase()})",
            targetUserId = targetUser.id,
            targetUserName = targetUser.username,
            action = if (suspensionType == "permanent") "SUSPEND_PERMANENT" else "SUSPEND_TEMPORARY",
            reason = reason,
            details = "Suspension type: $suspensionType, Until: ${suspensionUntil ?: "Indefinite"}",
            timestamp = "Just now"
        )
        _auditLogs.update { listOf(newLog) + it }
        _adminMessage.value = "Account @${targetUser.username} has been suspended ($suspensionType)."

        viewModelScope.launch {
            repository.suspendUser(admin, targetUser, reason, suspensionType, suspensionUntil)
        }
    }

    fun unsuspendUser(targetUser: User, reason: String) {
        val admin = _currentUser.value
        val updatedUser = targetUser.copy(
            isSuspended = false,
            suspensionReason = null,
            suspensionType = null,
            suspensionUntil = null
        )
        _allUsers.update { list -> list.map { if (it.id == targetUser.id) updatedUser else it } }

        val newLog = AdminAuditLog(
            id = "log_${System.currentTimeMillis()}",
            adminId = admin.id,
            adminName = "${admin.name} (${admin.role.uppercase()})",
            targetUserId = targetUser.id,
            targetUserName = targetUser.username,
            action = "UNSUSPEND_USER",
            reason = reason,
            details = "Account access restored.",
            timestamp = "Just now"
        )
        _auditLogs.update { listOf(newLog) + it }
        _adminMessage.value = "Account @${targetUser.username} has been unsuspended."

        viewModelScope.launch {
            repository.unsuspendUser(admin, targetUser, reason)
        }
    }

    fun editUserProfile(
        targetUser: User,
        updatedName: String,
        updatedUsername: String,
        updatedPhone: String?,
        updatedGender: String?,
        updatedRole: String,
        updatedStatus: String,
        reason: String
    ) {
        val admin = _currentUser.value
        val updatedUser = targetUser.copy(
            name = updatedName,
            username = updatedUsername,
            phone = updatedPhone,
            gender = updatedGender,
            role = updatedRole,
            statusMessage = updatedStatus
        )
        _allUsers.update { list -> list.map { if (it.id == targetUser.id) updatedUser else it } }

        val newLog = AdminAuditLog(
            id = "log_${System.currentTimeMillis()}",
            adminId = admin.id,
            adminName = "${admin.name} (${admin.role.uppercase()})",
            targetUserId = targetUser.id,
            targetUserName = targetUser.username,
            action = "EDIT_PROFILE_DETAILS",
            reason = reason,
            details = "Updated name, handle, role ($updatedRole), contact details",
            timestamp = "Just now"
        )
        _auditLogs.update { listOf(newLog) + it }
        _adminMessage.value = "Profile for @$updatedUsername updated successfully."

        viewModelScope.launch {
            repository.editUserProfile(
                admin,
                targetUser,
                updatedName,
                updatedUsername,
                updatedPhone,
                updatedGender,
                updatedRole,
                updatedStatus,
                reason
            )
        }
    }

    fun forcePasswordReset(
        targetUser: User,
        email: String,
        disableSignInUntilReset: Boolean,
        reason: String
    ) {
        val admin = _currentUser.value
        val newLog = AdminAuditLog(
            id = "log_${System.currentTimeMillis()}",
            adminId = admin.id,
            adminName = "${admin.name} (${admin.role.uppercase()})",
            targetUserId = targetUser.id,
            targetUserName = targetUser.username,
            action = "FORCE_PASSWORD_RESET",
            reason = reason,
            details = "Triggered secure recovery email to $email. DisableSignIn=$disableSignInUntilReset",
            timestamp = "Just now"
        )
        _auditLogs.update { listOf(newLog) + it }
        _adminMessage.value = "Secure password reset email sent to $email."

        viewModelScope.launch {
            repository.forcePasswordReset(admin, targetUser, email, disableSignInUntilReset, reason)
        }
    }

    fun resolveReport(
        reportId: String,
        status: String,
        actionTaken: String,
        notes: String
    ) {
        val admin = _currentUser.value
        _reports.update { list ->
            list.map { if (it.id == reportId) it.copy(status = status) else it }
        }
        val newLog = AdminAuditLog(
            id = "log_${System.currentTimeMillis()}",
            adminId = admin.id,
            adminName = "${admin.name} (${admin.role.uppercase()})",
            targetUserId = null,
            targetUserName = null,
            action = "MODERATE_REPORT",
            reason = "Report $reportId marked as $status",
            details = "Action: $actionTaken. Notes: $notes",
            timestamp = "Just now"
        )
        _auditLogs.update { listOf(newLog) + it }
        _adminMessage.value = "Report updated to $status."

        viewModelScope.launch {
            repository.resolveReport(admin, reportId, status, actionTaken, notes)
        }
    }

    fun closeAdminRoom(roomId: String, roomTitle: String, reason: String) {
        val admin = _currentUser.value
        _rooms.update { list -> list.filterNot { it.id == roomId } }
        val newLog = AdminAuditLog(
            id = "log_${System.currentTimeMillis()}",
            adminId = admin.id,
            adminName = "${admin.name} (${admin.role.uppercase()})",
            targetUserId = null,
            targetUserName = null,
            action = "ADMIN_CLOSE_ROOM",
            reason = reason,
            details = "Closed active room '$roomTitle' (ID: $roomId)",
            timestamp = "Just now"
        )
        _auditLogs.update { listOf(newLog) + it }
        _adminMessage.value = "Room '$roomTitle' has been closed."

        viewModelScope.launch {
            repository.closeOrDeleteRoom(admin, roomId, roomTitle, reason)
        }
    }

    fun updateProfileStatus(status: String) {
        _currentUser.update { it.copy(statusMessage = status) }
        viewModelScope.launch {
            repository.updateStatus(_currentUser.value.id, status)
        }
    }
}
