package com.example.model

data class User(
    val id: String,
    val name: String,
    val username: String,
    val avatarInitial: String,
    val avatarColorHex: Long = 0xFF8B5CF6,
    val avatarUrl: String? = null,
    val email: String? = null,
    val phone: String? = null,
    val gender: String? = null,
    val role: String = "user", // "user", "moderator", "admin"
    val dateOfBirth: String? = null,
    val dobCalendar: String = "AD",
    val age: Int? = null,
    val onboardingCompleted: Boolean = false,
    val isOnline: Boolean = true,
    val isSpeaking: Boolean = false,
    val isSuspended: Boolean = false,
    val suspensionReason: String? = null,
    val suspensionType: String? = null, // "temporary", "permanent"
    val suspensionUntil: String? = null,
    val forcePasswordReset: Boolean = false,
    val statusMessage: String = "Ready to stream",
    val partiesHosted: Int = 18,
    val hoursWatched: Int = 142,
    val friendsCount: Int = 64,
    val createdAt: String? = null
)

data class ReportedItem(
    val id: String,
    val targetType: String, // "room", "user", "message"
    val targetId: String,
    val targetTitle: String,
    val reportedUserId: String? = null,
    val reportedUserName: String? = null,
    val reporterName: String,
    val reason: String,
    val details: String = "",
    val status: String = "pending", // "pending", "resolved", "dismissed"
    val timestamp: String
)

data class AdminAuditLog(
    val id: String,
    val adminId: String,
    val adminName: String,
    val targetUserId: String? = null,
    val targetUserName: String? = null,
    val action: String,
    val reason: String,
    val details: String = "",
    val timestamp: String
)

data class WatchRoom(
    val id: String,
    val code: String,
    val title: String,
    val host: User,
    val videoTitle: String,
    val videoSource: String, // e.g. "YouTube", "Twitch", "Vimeo", "Public Stream"
    val videoUrl: String,
    val currentPositionSeconds: Int = 185,
    val totalDurationSeconds: Int = 720,
    val isPlaying: Boolean = true,
    val participants: List<User> = emptyList(),
    val category: String = "Anime & Gaming",
    val isPrivate: Boolean = false,
    val pinCode: String = "",
    val maxParticipants: Int = 12
)

data class ChatMessage(
    val id: String,
    val sender: User,
    val text: String,
    val timestamp: String,
    val isHost: Boolean = false,
    val isSystem: Boolean = false
)

data class Friend(
    val user: User,
    val isBestFriend: Boolean = false,
    val lastActive: String = "Just now",
    val activeRoom: WatchRoom? = null,
    val mutualFriends: Int = 8
)

data class VideoItem(
    val id: String,
    val title: String,
    val channel: String,
    val duration: String,
    val views: String,
    val category: String,
    val platform: String,
    val sampleUrl: String
)

data class FlyingReaction(
    val id: Long,
    val emoji: String,
    val startXRatio: Float,
    val scale: Float
)
