package com.example.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ProfileDto(
    val id: String,
    val username: String? = null,
    @SerialName("full_name") val fullName: String? = null,
    val email: String? = null,
    val phone: String? = null,
    val gender: String? = null,
    @SerialName("avatar_url") val avatarUrl: String? = null,
    @SerialName("status_message") val statusMessage: String? = null,
    @SerialName("date_of_birth") val dateOfBirth: String? = null,
    @SerialName("dob_calendar") val dobCalendar: String? = null,
    val age: Int? = null,
    @SerialName("onboarding_completed") val onboardingCompleted: Boolean = false,
    @SerialName("parties_hosted") val partiesHosted: Int = 0,
    @SerialName("hours_watched") val hoursWatched: Int = 0,
    @SerialName("is_online") val isOnline: Boolean = true,
    @SerialName("is_suspended") val isSuspended: Boolean = false,
    @SerialName("suspension_reason") val suspensionReason: String? = null,
    @SerialName("suspension_type") val suspensionType: String? = null,
    @SerialName("suspension_until") val suspensionUntil: String? = null,
    @SerialName("force_password_reset") val forcePasswordReset: Boolean = false,
    val role: String = "user",
    @SerialName("created_at") val createdAt: String? = null
)

@Serializable
data class ReportDto(
    val id: String,
    @SerialName("target_type") val targetType: String,
    @SerialName("target_id") val targetId: String,
    @SerialName("target_title") val targetTitle: String? = null,
    @SerialName("reported_user_id") val reportedUserId: String? = null,
    @SerialName("reported_user_name") val reportedUserName: String? = null,
    @SerialName("reporter_name") val reporterName: String,
    val reason: String,
    val details: String? = null,
    val status: String = "pending",
    @SerialName("created_at") val createdAt: String? = null
)

@Serializable
data class AuditLogDto(
    val id: String,
    @SerialName("admin_id") val adminId: String,
    @SerialName("admin_name") val adminName: String,
    @SerialName("target_user_id") val targetUserId: String? = null,
    @SerialName("target_user_name") val targetUserName: String? = null,
    val action: String,
    val reason: String,
    val details: String? = null,
    @SerialName("created_at") val createdAt: String? = null
)

@Serializable
data class RoomDto(
    val id: String,
    val code: String,
    val title: String,
    @SerialName("video_url") val videoUrl: String,
    @SerialName("video_source") val videoSource: String = "YouTube",
    @SerialName("video_title") val videoTitle: String = "Live Video Stream",
    val category: String = "Anime & Gaming",
    @SerialName("is_private") val isPrivate: Boolean = false,
    @SerialName("pin_code") val pinCode: String = "",
    @SerialName("host_id") val hostId: String,
    @SerialName("is_playing") val isPlaying: Boolean = true,
    @SerialName("playback_position") val playbackPosition: Int = 0,
    @SerialName("max_participants") val maxParticipants: Int = 12
)

@Serializable
data class MessageDto(
    val id: String,
    @SerialName("room_id") val roomId: String? = null,
    @SerialName("sender_id") val senderId: String,
    @SerialName("receiver_id") val receiverId: String? = null,
    val content: String,
    @SerialName("created_at") val createdAt: String? = null
)

@Serializable
data class FriendshipDto(
    val id: String,
    @SerialName("user_id") val userId: String,
    @SerialName("friend_id") val friendId: String,
    val status: String = "accepted",
    @SerialName("created_at") val createdAt: String? = null
)
