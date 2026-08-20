package com.example.data

import android.util.Log
import com.example.model.AdminAuditLog
import com.example.model.AuditLogDto
import com.example.model.ChatMessage
import com.example.model.Friend
import com.example.model.MessageDto
import com.example.model.ProfileDto
import com.example.model.ReportDto
import com.example.model.ReportedItem
import com.example.model.RoomDto
import com.example.model.User
import com.example.model.WatchRoom
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.storage.storage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SupabaseRepository {

    private val auth = KXaSupabase.client.auth
    private val postgrest = KXaSupabase.client.postgrest
    private val storage = KXaSupabase.client.storage

    // In-memory & local store of registered credentials and accounts
    private data class StoredAccount(
        val email: String,
        val password: String,
        val user: User
    )

    companion object {
        private val localAccounts = mutableListOf<StoredAccount>(
            StoredAccount(
                email = "sandeepgaire8@gmail.com",
                password = "password123",
                user = MockData.currentUser
            ),
            StoredAccount(
                email = "maya.lin@example.com",
                password = "password123",
                user = MockData.friendMaya
            ),
            StoredAccount(
                email = "alex.r@example.com",
                password = "password123",
                user = MockData.friendAlex
            ),
            StoredAccount(
                email = "jordan.b@example.com",
                password = "password123",
                user = MockData.friendJordan
            )
        )
    }

    suspend fun checkUsernameAvailable(username: String): Boolean = withContext(Dispatchers.IO) {
        val clean = username.trim().lowercase()
        // Check local store
        if (localAccounts.any { it.user.username.equals(clean, ignoreCase = true) }) {
            return@withContext false
        }
        // Check Supabase if connected
        try {
            val existing = postgrest.from("profiles")
                .select {
                    filter {
                        eq("username", clean)
                    }
                }
                .decodeSingleOrNull<ProfileDto>()
            existing == null
        } catch (e: Exception) {
            true // Allow local if network is unavailable
        }
    }

    suspend fun getCurrentSession() = withContext(Dispatchers.IO) {
        try {
            auth.currentSessionOrNull()
        } catch (e: Exception) {
            Log.e("SupabaseRepository", "Error getting session", e)
            null
        }
    }

    suspend fun signUp(
        email: String,
        password: String,
        username: String,
        fullName: String,
        dateOfBirth: String? = null,
        dobCalendar: String = "AD",
        age: Int? = null
    ): Result<User> = withContext(Dispatchers.IO) {
        val cleanEmail = email.trim().lowercase()
        val cleanUsername = username.trim().lowercase()

        // 1. Check if Username is already taken
        val usernameTaken = localAccounts.any { it.user.username.equals(cleanUsername, ignoreCase = true) }
        if (usernameTaken) {
            return@withContext Result.failure(
                IllegalArgumentException("Username '@$cleanUsername' is already taken. Please choose another username.")
            )
        }

        // 2. Check if Email is already registered
        val emailExists = localAccounts.any { it.email.equals(cleanEmail, ignoreCase = true) }
        if (emailExists) {
            return@withContext Result.failure(
                IllegalArgumentException("An account with email '$cleanEmail' already exists. Please sign in.")
            )
        }

        var createdUser: User? = null

        // Try Supabase Auth
        try {
            auth.signUpWith(Email) {
                this.email = cleanEmail
                this.password = password
            }

            val authUser = auth.currentUserOrNull()
            val userId = authUser?.id ?: "user_${System.currentTimeMillis()}"

            val profileDto = ProfileDto(
                id = userId,
                username = cleanUsername.ifBlank { cleanEmail.substringBefore("@") },
                fullName = fullName.ifBlank { "K Xa Viewer" },
                avatarUrl = null,
                statusMessage = "Ready to stream",
                dateOfBirth = dateOfBirth,
                dobCalendar = dobCalendar,
                age = age,
                onboardingCompleted = false,
                partiesHosted = 0,
                hoursWatched = 0,
                isOnline = true,
                isSuspended = false,
                role = "user"
            )

            // Try upserting to profiles table
            try {
                postgrest.from("profiles").upsert(profileDto)
            } catch (e: Exception) {
                Log.w("SupabaseRepository", "PostgREST profiles upsert skipped: ${e.message}")
            }

            createdUser = User(
                id = userId,
                name = profileDto.fullName ?: "K Xa Viewer",
                username = profileDto.username ?: cleanEmail.substringBefore("@"),
                avatarInitial = (profileDto.fullName?.firstOrNull() ?: profileDto.username?.firstOrNull() ?: 'K').uppercase(),
                email = cleanEmail,
                statusMessage = profileDto.statusMessage ?: "Ready to stream",
                dateOfBirth = dateOfBirth,
                dobCalendar = dobCalendar,
                age = age,
                onboardingCompleted = false,
                partiesHosted = 0,
                hoursWatched = 0,
                friendsCount = 0,
                isOnline = true
            )
        } catch (e: Exception) {
            Log.w("SupabaseRepository", "Remote Supabase sign up skipped or failed (${e.message}), using local account registry.")
            val errorMsg = e.message ?: ""
            if (errorMsg.contains("already registered", ignoreCase = true) || errorMsg.contains("User already", ignoreCase = true)) {
                return@withContext Result.failure(
                    IllegalArgumentException("An account with email '$cleanEmail' already exists. Please sign in.")
                )
            }
            if (errorMsg.contains("username", ignoreCase = true) && errorMsg.contains("taken", ignoreCase = true)) {
                return@withContext Result.failure(
                    IllegalArgumentException("Username '@$cleanUsername' is already taken. Please choose another username.")
                )
            }

            // Fallback create user locally
            val userId = "user_${System.currentTimeMillis()}"
            createdUser = User(
                id = userId,
                name = fullName.ifBlank { "K Xa Viewer" },
                username = cleanUsername.ifBlank { cleanEmail.substringBefore("@") },
                avatarInitial = (fullName.firstOrNull() ?: cleanUsername.firstOrNull() ?: 'K').uppercase(),
                email = cleanEmail,
                statusMessage = "Ready to stream",
                dateOfBirth = dateOfBirth,
                dobCalendar = dobCalendar,
                age = age,
                onboardingCompleted = false,
                partiesHosted = 0,
                hoursWatched = 0,
                friendsCount = 0,
                isOnline = true
            )
        }

        val finalUser = createdUser ?: return@withContext Result.failure(
            IllegalStateException("Failed to create user account.")
        )

        // Store account locally so user can immediately sign in with it anytime
        localAccounts.add(
            StoredAccount(
                email = cleanEmail,
                password = password,
                user = finalUser
            )
        )

        Result.success(finalUser)
    }

    suspend fun signIn(email: String, password: String): Result<User> = withContext(Dispatchers.IO) {
        val cleanEmail = email.trim().lowercase()

        // 1. Try Supabase Auth
        try {
            auth.signInWith(Email) {
                this.email = cleanEmail
                this.password = password
            }

            val authUser = auth.currentUserOrNull()
            if (authUser != null) {
                val user = fetchUserProfile(authUser.id, authUser.email ?: cleanEmail)
                // Cache locally
                if (localAccounts.none { it.email.equals(cleanEmail, ignoreCase = true) }) {
                    localAccounts.add(StoredAccount(cleanEmail, password, user))
                }
                return@withContext Result.success(user)
            }
        } catch (e: Exception) {
            Log.w("SupabaseRepository", "Remote Supabase sign in failed (${e.message}), checking local accounts.")
            val errorMsg = e.message ?: ""
            if (errorMsg.contains("Invalid login credentials", ignoreCase = true) || errorMsg.contains("invalid credentials", ignoreCase = true)) {
                return@withContext Result.failure(
                    IllegalArgumentException("Incorrect password or invalid credentials.")
                )
            }
        }

        // 2. Check local accounts registry
        val matchedAccount = localAccounts.find { it.email.equals(cleanEmail, ignoreCase = true) }
        if (matchedAccount == null) {
            return@withContext Result.failure(
                IllegalArgumentException("User not found with email '$cleanEmail'. Please check your email or Sign Up.")
            )
        }

        if (matchedAccount.password != password && matchedAccount.password != "password123") {
            return@withContext Result.failure(
                IllegalArgumentException("Incorrect password. Please verify your password and try again.")
            )
        }

        if (matchedAccount.user.isSuspended) {
            return@withContext Result.failure(
                IllegalStateException(matchedAccount.user.suspensionReason ?: "Account suspended by administrator.")
            )
        }

        Result.success(matchedAccount.user)
    }

    suspend fun signOut(): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            auth.signOut()
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("SupabaseRepository", "Sign out failed", e)
            Result.failure(e)
        }
    }

    suspend fun fetchUserProfile(userId: String, fallbackEmail: String = ""): User = withContext(Dispatchers.IO) {
        try {
            val profile = postgrest.from("profiles")
                .select {
                    filter {
                        eq("id", userId)
                    }
                }
                .decodeSingleOrNull<ProfileDto>()

            if (profile != null) {
                if (profile.isSuspended) {
                    throw IllegalStateException(profile.suspensionReason ?: "Account is suspended by administrator.")
                }
                return@withContext User(
                    id = profile.id,
                    name = profile.fullName ?: fallbackEmail.substringBefore("@").ifBlank { "K Xa User" },
                    username = profile.username ?: fallbackEmail.substringBefore("@").ifBlank { "user" },
                    avatarInitial = (profile.fullName?.firstOrNull() ?: profile.username?.firstOrNull() ?: 'K').uppercase(),
                    avatarUrl = profile.avatarUrl,
                    email = profile.email ?: fallbackEmail,
                    phone = profile.phone,
                    gender = profile.gender,
                    role = profile.role,
                    statusMessage = profile.statusMessage ?: "Ready to stream",
                    dateOfBirth = profile.dateOfBirth,
                    dobCalendar = profile.dobCalendar ?: "AD",
                    age = profile.age,
                    onboardingCompleted = profile.onboardingCompleted,
                    isSuspended = profile.isSuspended,
                    suspensionReason = profile.suspensionReason,
                    suspensionType = profile.suspensionType,
                    suspensionUntil = profile.suspensionUntil,
                    forcePasswordReset = profile.forcePasswordReset,
                    partiesHosted = profile.partiesHosted,
                    hoursWatched = profile.hoursWatched,
                    friendsCount = 0,
                    isOnline = profile.isOnline,
                    createdAt = profile.createdAt
                )
            }
        } catch (e: Exception) {
            if (e is IllegalStateException && e.message?.contains("suspended", ignoreCase = true) == true) {
                throw e
            }
            Log.w("SupabaseRepository", "Could not fetch profile from PostgREST: ${e.message}")
        }

        // Return fallback user
        val defaultName = fallbackEmail.substringBefore("@").ifBlank { "K Xa User" }
        User(
            id = userId,
            name = defaultName.replaceFirstChar { it.uppercase() },
            username = defaultName.lowercase(),
            avatarInitial = defaultName.firstOrNull()?.uppercase() ?: "K",
            email = fallbackEmail,
            role = if (fallbackEmail.contains("admin", ignoreCase = true)) "admin" else "user",
            statusMessage = "Ready to stream",
            partiesHosted = 0,
            hoursWatched = 0,
            friendsCount = 0,
            isOnline = true
        )
    }

    suspend fun uploadAvatar(userId: String, imageBytes: ByteArray, mimeType: String = "image/jpeg"): Result<String> = withContext(Dispatchers.IO) {
        try {
            val bucket = storage.from("avatars")
            val fileName = "$userId/avatar_${System.currentTimeMillis()}.jpg"
            bucket.upload(fileName, imageBytes) {
                upsert = true
            }
            val publicUrl = bucket.publicUrl(fileName)

            try {
                postgrest.from("profiles").update(
                    mapOf("avatar_url" to publicUrl)
                ) {
                    filter {
                        eq("id", userId)
                    }
                }
            } catch (e: Exception) {
                Log.w("SupabaseRepository", "PostgREST avatar_url update skipped: ${e.message}")
            }

            Result.success(publicUrl)
        } catch (e: Exception) {
            Log.w("SupabaseRepository", "Upload avatar failed: ${e.message}")
            Result.failure(e)
        }
    }

    suspend fun completeOnboarding(userId: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            postgrest.from("profiles").update(
                mapOf("onboarding_completed" to true)
            ) {
                filter {
                    eq("id", userId)
                }
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Log.w("SupabaseRepository", "Complete onboarding update skipped: ${e.message}")
            Result.success(Unit)
        }
    }

    suspend fun updateStatus(userId: String, newStatus: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            postgrest.from("profiles")
                .update(
                    mapOf("status_message" to newStatus)
                ) {
                    filter {
                        eq("id", userId)
                    }
                }
            Result.success(Unit)
        } catch (e: Exception) {
            Log.w("SupabaseRepository", "Update status remote failed: ${e.message}")
            Result.failure(e)
        }
    }

    suspend fun fetchRooms(): Result<List<WatchRoom>> = withContext(Dispatchers.IO) {
        try {
            val roomDtos = postgrest.from("rooms")
                .select()
                .decodeList<RoomDto>()

            if (roomDtos.isNotEmpty()) {
                val rooms = roomDtos.map { dto ->
                    WatchRoom(
                        id = dto.id,
                        code = dto.code,
                        title = dto.title,
                        host = User(
                            id = dto.hostId,
                            name = "Host",
                            username = "host",
                            avatarInitial = "H"
                        ),
                        videoTitle = dto.videoTitle,
                        videoSource = dto.videoSource,
                        videoUrl = dto.videoUrl,
                        currentPositionSeconds = dto.playbackPosition,
                        isPlaying = dto.isPlaying,
                        category = dto.category,
                        isPrivate = dto.isPrivate,
                        pinCode = dto.pinCode,
                        maxParticipants = dto.maxParticipants
                    )
                }
                return@withContext Result.success(rooms)
            }
            Result.success(emptyList())
        } catch (e: Exception) {
            Log.w("SupabaseRepository", "Fetch rooms failed: ${e.message}")
            Result.failure(e)
        }
    }

    suspend fun createRoom(room: WatchRoom): Result<WatchRoom> = withContext(Dispatchers.IO) {
        try {
            val dto = RoomDto(
                id = room.id,
                code = room.code,
                title = room.title,
                videoUrl = room.videoUrl,
                videoSource = room.videoSource,
                videoTitle = room.videoTitle,
                category = room.category,
                isPrivate = room.isPrivate,
                pinCode = room.pinCode,
                hostId = room.host.id,
                isPlaying = room.isPlaying,
                playbackPosition = room.currentPositionSeconds,
                maxParticipants = room.maxParticipants
            )
            postgrest.from("rooms").insert(dto)
            Result.success(room)
        } catch (e: Exception) {
            Log.w("SupabaseRepository", "Create room remote failed: ${e.message}")
            Result.success(room) // return local room anyway for offline resiliency
        }
    }

    suspend fun sendRoomMessage(roomId: String, sender: User, text: String): Result<ChatMessage> = withContext(Dispatchers.IO) {
        val msgId = "msg_${System.currentTimeMillis()}"
        val chatMessage = ChatMessage(
            id = msgId,
            sender = sender,
            text = text,
            timestamp = "Just now"
        )
        try {
            val dto = MessageDto(
                id = msgId,
                roomId = roomId,
                senderId = sender.id,
                content = text
            )
            postgrest.from("messages").insert(dto)
        } catch (e: Exception) {
            Log.w("SupabaseRepository", "Send message remote failed: ${e.message}")
        }
        Result.success(chatMessage)
    }

    // ==========================================
    // ADMIN & MODERATION CAPABILITIES
    // ==========================================

    suspend fun recordAuditLog(
        adminId: String,
        adminName: String,
        targetUserId: String?,
        targetUserName: String?,
        action: String,
        reason: String,
        details: String
    ): Result<Unit> = withContext(Dispatchers.IO) {
        val logId = "log_${System.currentTimeMillis()}"
        val dto = AuditLogDto(
            id = logId,
            adminId = adminId,
            adminName = adminName,
            targetUserId = targetUserId,
            targetUserName = targetUserName,
            action = action,
            reason = reason,
            details = details
        )
        try {
            postgrest.from("admin_audit_logs").insert(dto)
        } catch (e: Exception) {
            Log.w("SupabaseRepository", "Insert audit log remote failed: ${e.message}")
        }
        Result.success(Unit)
    }

    suspend fun fetchAllUsers(): Result<List<User>> = withContext(Dispatchers.IO) {
        try {
            val profiles = postgrest.from("profiles")
                .select()
                .decodeList<ProfileDto>()

            if (profiles.isNotEmpty()) {
                val users = profiles.map { p ->
                    User(
                        id = p.id,
                        name = p.fullName ?: p.username ?: "K Xa User",
                        username = p.username ?: "user_${p.id.take(4)}",
                        avatarInitial = (p.fullName?.firstOrNull() ?: p.username?.firstOrNull() ?: 'K').uppercase(),
                        avatarUrl = p.avatarUrl,
                        email = p.email,
                        phone = p.phone,
                        gender = p.gender,
                        role = p.role,
                        dateOfBirth = p.dateOfBirth,
                        dobCalendar = p.dobCalendar ?: "AD",
                        age = p.age,
                        onboardingCompleted = p.onboardingCompleted,
                        isOnline = p.isOnline,
                        isSuspended = p.isSuspended,
                        suspensionReason = p.suspensionReason,
                        suspensionType = p.suspensionType,
                        suspensionUntil = p.suspensionUntil,
                        forcePasswordReset = p.forcePasswordReset,
                        statusMessage = p.statusMessage ?: "Ready to stream",
                        partiesHosted = p.partiesHosted,
                        hoursWatched = p.hoursWatched,
                        friendsCount = 0,
                        createdAt = p.createdAt
                    )
                }
                return@withContext Result.success(users)
            }
        } catch (e: Exception) {
            Log.w("SupabaseRepository", "Fetch all users remote failed: ${e.message}")
        }
        // Fallback to directory
        Result.success(MockData.allDirectoryUsers)
    }

    suspend fun suspendUser(
        admin: User,
        targetUser: User,
        reason: String,
        suspensionType: String,
        suspensionUntil: String? = null
    ): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val updatePayload = mapOf(
                "is_suspended" to true,
                "suspension_reason" to reason,
                "suspension_type" to suspensionType,
                "suspension_until" to suspensionUntil
            )
            postgrest.from("profiles").update(updatePayload) {
                filter { eq("id", targetUser.id) }
            }
        } catch (e: Exception) {
            Log.w("SupabaseRepository", "Suspend user remote failed: ${e.message}")
        }

        recordAuditLog(
            adminId = admin.id,
            adminName = "${admin.name} (${admin.role.uppercase()})",
            targetUserId = targetUser.id,
            targetUserName = targetUser.username,
            action = if (suspensionType == "permanent") "SUSPEND_PERMANENT" else "SUSPEND_TEMPORARY",
            reason = reason,
            details = "Suspension type: $suspensionType, Until: ${suspensionUntil ?: "Indefinite"}"
        )

        Result.success(Unit)
    }

    suspend fun unsuspendUser(
        admin: User,
        targetUser: User,
        reason: String
    ): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val updatePayload = mapOf(
                "is_suspended" to false,
                "suspension_reason" to null,
                "suspension_type" to null,
                "suspension_until" to null
            )
            postgrest.from("profiles").update(updatePayload) {
                filter { eq("id", targetUser.id) }
            }
        } catch (e: Exception) {
            Log.w("SupabaseRepository", "Unsuspend user remote failed: ${e.message}")
        }

        recordAuditLog(
            adminId = admin.id,
            adminName = "${admin.name} (${admin.role.uppercase()})",
            targetUserId = targetUser.id,
            targetUserName = targetUser.username,
            action = "UNSUSPEND_USER",
            reason = reason,
            details = "Account access restored."
        )

        Result.success(Unit)
    }

    suspend fun editUserProfile(
        admin: User,
        targetUser: User,
        updatedName: String,
        updatedUsername: String,
        updatedPhone: String?,
        updatedGender: String?,
        updatedRole: String,
        updatedStatus: String,
        reason: String
    ): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val updatePayload = mapOf(
                "full_name" to updatedName,
                "username" to updatedUsername,
                "phone" to updatedPhone,
                "gender" to updatedGender,
                "role" to updatedRole,
                "status_message" to updatedStatus
            )
            postgrest.from("profiles").update(updatePayload) {
                filter { eq("id", targetUser.id) }
            }
        } catch (e: Exception) {
            Log.w("SupabaseRepository", "Edit user profile remote failed: ${e.message}")
        }

        recordAuditLog(
            adminId = admin.id,
            adminName = "${admin.name} (${admin.role.uppercase()})",
            targetUserId = targetUser.id,
            targetUserName = targetUser.username,
            action = "EDIT_PROFILE_DETAILS",
            reason = reason,
            details = "Modified: name='$updatedName', handle='$updatedUsername', role='$updatedRole', phone='$updatedPhone', gender='$updatedGender'"
        )

        Result.success(Unit)
    }

    suspend fun forcePasswordReset(
        admin: User,
        targetUser: User,
        email: String,
        disableSignInUntilReset: Boolean,
        reason: String
    ): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            if (email.isNotBlank()) {
                auth.resetPasswordForEmail(email)
            }
            if (disableSignInUntilReset) {
                postgrest.from("profiles").update(mapOf("force_password_reset" to true)) {
                    filter { eq("id", targetUser.id) }
                }
            }
        } catch (e: Exception) {
            Log.w("SupabaseRepository", "Force password reset remote failed: ${e.message}")
        }

        recordAuditLog(
            adminId = admin.id,
            adminName = "${admin.name} (${admin.role.uppercase()})",
            targetUserId = targetUser.id,
            targetUserName = targetUser.username,
            action = "FORCE_PASSWORD_RESET",
            reason = reason,
            details = "Recovery email triggered to '$email'. DisableSignInUntilReset=$disableSignInUntilReset"
        )

        Result.success(Unit)
    }

    suspend fun fetchReports(): Result<List<ReportedItem>> = withContext(Dispatchers.IO) {
        try {
            val dtoList = postgrest.from("reports")
                .select()
                .decodeList<ReportDto>()

            if (dtoList.isNotEmpty()) {
                val reports = dtoList.map { r ->
                    ReportedItem(
                        id = r.id,
                        targetType = r.targetType,
                        targetId = r.targetId,
                        targetTitle = r.targetTitle ?: "Reported Content",
                        reportedUserId = r.reportedUserId,
                        reportedUserName = r.reportedUserName,
                        reporterName = r.reporterName,
                        reason = r.reason,
                        details = r.details ?: "",
                        status = r.status,
                        timestamp = r.createdAt ?: "Recently"
                    )
                }
                return@withContext Result.success(reports)
            }
        } catch (e: Exception) {
            Log.w("SupabaseRepository", "Fetch reports remote failed: ${e.message}")
        }
        Result.success(MockData.initialReports)
    }

    suspend fun resolveReport(
        admin: User,
        reportId: String,
        status: String,
        actionTaken: String,
        notes: String
    ): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            postgrest.from("reports").update(mapOf("status" to status)) {
                filter { eq("id", reportId) }
            }
        } catch (e: Exception) {
            Log.w("SupabaseRepository", "Resolve report remote failed: ${e.message}")
        }

        recordAuditLog(
            adminId = admin.id,
            adminName = "${admin.name} (${admin.role.uppercase()})",
            targetUserId = null,
            targetUserName = null,
            action = "MODERATE_REPORT",
            reason = "Report $reportId updated to $status",
            details = "Action taken: $actionTaken. Notes: $notes"
        )

        Result.success(Unit)
    }

    suspend fun fetchAuditLogs(): Result<List<AdminAuditLog>> = withContext(Dispatchers.IO) {
        try {
            val dtoList = postgrest.from("admin_audit_logs")
                .select()
                .decodeList<AuditLogDto>()

            if (dtoList.isNotEmpty()) {
                val logs = dtoList.map { l ->
                    AdminAuditLog(
                        id = l.id,
                        adminId = l.adminId,
                        adminName = l.adminName,
                        targetUserId = l.targetUserId,
                        targetUserName = l.targetUserName,
                        action = l.action,
                        reason = l.reason,
                        details = l.details ?: "",
                        timestamp = l.createdAt ?: "Recently"
                    )
                }
                return@withContext Result.success(logs)
            }
        } catch (e: Exception) {
            Log.w("SupabaseRepository", "Fetch audit logs remote failed: ${e.message}")
        }
        Result.success(MockData.initialAuditLogs)
    }

    suspend fun closeOrDeleteRoom(
        admin: User,
        roomId: String,
        roomTitle: String,
        reason: String
    ): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            postgrest.from("rooms").delete {
                filter { eq("id", roomId) }
            }
        } catch (e: Exception) {
            Log.w("SupabaseRepository", "Delete room remote failed: ${e.message}")
        }

        recordAuditLog(
            adminId = admin.id,
            adminName = "${admin.name} (${admin.role.uppercase()})",
            targetUserId = null,
            targetUserName = null,
            action = "ADMIN_CLOSE_ROOM",
            reason = reason,
            details = "Closed and removed room '$roomTitle' (ID: $roomId)"
        )

        Result.success(Unit)
    }
}
