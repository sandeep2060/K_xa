package com.example.data

import com.example.model.AdminAuditLog
import com.example.model.ChatMessage
import com.example.model.Friend
import com.example.model.ReportedItem
import com.example.model.User
import com.example.model.VideoItem
import com.example.model.WatchRoom

object MockData {
    val currentUser = User(
        id = "user_me",
        name = "Kai Vance",
        username = "kaivance",
        avatarInitial = "K",
        avatarColorHex = 0xFF8B5CF6,
        email = "sandeepgaire8@gmail.com",
        phone = "+977-9801234567",
        gender = "Male",
        role = "admin",
        dateOfBirth = "2001-05-14",
        dobCalendar = "AD",
        age = 25,
        onboardingCompleted = true,
        isOnline = true,
        statusMessage = "Always down for sci-fi & anime watch parties ✨",
        partiesHosted = 24,
        hoursWatched = 186,
        friendsCount = 42,
        createdAt = "2024-01-15"
    )

    val friendMaya = User(
        id = "user_maya",
        name = "Maya Lin",
        username = "mayalin",
        avatarInitial = "M",
        avatarColorHex = 0xFFEC4899,
        email = "maya.lin@example.com",
        phone = "+977-9841987654",
        gender = "Female",
        role = "moderator",
        dateOfBirth = "2002-11-20",
        dobCalendar = "AD",
        age = 23,
        onboardingCompleted = true,
        isOnline = true,
        isSpeaking = true,
        statusMessage = "Watching Cyberpunk anime 🔥",
        partiesHosted = 14,
        hoursWatched = 98,
        friendsCount = 31,
        createdAt = "2024-02-10"
    )

    val friendAlex = User(
        id = "user_alex",
        name = "Alex Rivera",
        username = "arivera",
        avatarInitial = "A",
        avatarColorHex = 0xFF06B6D4,
        email = "alex.r@example.com",
        phone = "+977-9812345678",
        gender = "Male",
        role = "user",
        dateOfBirth = "2003-08-19",
        dobCalendar = "AD",
        age = 22,
        onboardingCompleted = true,
        isOnline = true,
        isSpeaking = false,
        statusMessage = "Chilling in LoL stream room",
        partiesHosted = 6,
        hoursWatched = 64,
        friendsCount = 18,
        createdAt = "2024-03-05"
    )

    val friendJordan = User(
        id = "user_jordan",
        name = "Jordan Blake",
        username = "jblake",
        avatarInitial = "J",
        avatarColorHex = 0xFF10B981,
        email = "jordan.b@example.com",
        phone = "+977-9851122334",
        gender = "Non-Binary",
        role = "user",
        dateOfBirth = "2000-02-12",
        dobCalendar = "AD",
        age = 26,
        onboardingCompleted = true,
        isOnline = true,
        isSpeaking = false,
        statusMessage = "Listening to lofi beats",
        partiesHosted = 12,
        hoursWatched = 120,
        friendsCount = 25,
        createdAt = "2024-01-20"
    )

    val friendSarah = User(
        id = "user_sarah",
        name = "Sarah Chen",
        username = "sarahc",
        avatarInitial = "S",
        avatarColorHex = 0xFFF59E0B,
        email = "sarah.chen@example.com",
        phone = "+977-9860011223",
        gender = "Female",
        role = "user",
        dateOfBirth = "2004-10-30",
        dobCalendar = "AD",
        age = 21,
        onboardingCompleted = true,
        isOnline = false,
        statusMessage = "AFK until 8 PM",
        partiesHosted = 3,
        hoursWatched = 45,
        friendsCount = 12,
        createdAt = "2024-04-12"
    )

    val friendDevon = User(
        id = "user_devon",
        name = "Devon Miles",
        username = "dmiles",
        avatarInitial = "D",
        avatarColorHex = 0xFF6366F1,
        email = "devon.m@example.com",
        phone = "+977-9811223344",
        gender = "Male",
        role = "user",
        dateOfBirth = "2001-12-05",
        dobCalendar = "AD",
        age = 24,
        onboardingCompleted = true,
        isOnline = true,
        statusMessage = "Hyped for movie night!",
        partiesHosted = 9,
        hoursWatched = 82,
        friendsCount = 29,
        createdAt = "2024-02-28"
    )

    val userSuspendedSpammer = User(
        id = "user_spammer_99",
        name = "Troll Spammer",
        username = "spam_bot_99",
        avatarInitial = "T",
        avatarColorHex = 0xFFEF4444,
        email = "spammer99@junkmail.com",
        phone = "+977-9809988776",
        gender = "Other",
        role = "user",
        dateOfBirth = "1999-01-01",
        dobCalendar = "AD",
        age = 27,
        onboardingCompleted = true,
        isOnline = false,
        isSuspended = true,
        suspensionReason = "Mass posting spam links in public room chat",
        suspensionType = "permanent",
        statusMessage = "Suspended account",
        partiesHosted = 0,
        hoursWatched = 1,
        friendsCount = 0,
        createdAt = "2024-05-01"
    )

    val userTempSuspended = User(
        id = "user_toxic_sam",
        name = "Sam Toxicity",
        username = "toxic_sam",
        avatarInitial = "S",
        avatarColorHex = 0xFFF97316,
        email = "sam.toxic@example.com",
        phone = "+977-9844332211",
        gender = "Male",
        role = "user",
        dateOfBirth = "2005-07-22",
        dobCalendar = "AD",
        age = 21,
        onboardingCompleted = true,
        isOnline = false,
        isSuspended = true,
        suspensionReason = "Abusive chat language during live stream",
        suspensionType = "temporary",
        suspensionUntil = "2026-08-26",
        statusMessage = "Account restricted",
        partiesHosted = 1,
        hoursWatched = 15,
        friendsCount = 2,
        createdAt = "2024-04-18"
    )

    val allDirectoryUsers = listOf(
        currentUser,
        friendMaya,
        friendAlex,
        friendJordan,
        friendSarah,
        friendDevon,
        userSuspendedSpammer,
        userTempSuspended
    )

    val initialReports = listOf(
        ReportedItem(
            id = "rep_101",
            targetType = "room",
            targetId = "room_cyberpunk",
            targetTitle = "Cyberpunk Night & Chill Discussion",
            reportedUserId = "user_toxic_sam",
            reportedUserName = "toxic_sam",
            reporterName = "mayalin",
            reason = "Hate speech and trolling in room voice/text stream",
            details = "User repeatedly used discriminatory language after being warned by the host.",
            status = "pending",
            timestamp = "10 mins ago"
        ),
        ReportedItem(
            id = "rep_102",
            targetType = "user",
            targetId = "user_spammer_99",
            targetTitle = "@spam_bot_99 Profile",
            reportedUserId = "user_spammer_99",
            reportedUserName = "spam_bot_99",
            reporterName = "arivera",
            reason = "Impersonation and phishing link distribution",
            details = "Bio links to phishing site pretending to be free Nitro.",
            status = "pending",
            timestamp = "45 mins ago"
        ),
        ReportedItem(
            id = "rep_103",
            targetType = "message",
            targetId = "msg_999",
            targetTitle = "Message in Lofi Beats Room",
            reportedUserId = "user_toxic_sam",
            reportedUserName = "toxic_sam",
            reporterName = "jblake",
            reason = "Harassment of room participants",
            details = "Targeted bullying directed at moderator.",
            status = "resolved",
            timestamp = "2 hours ago"
        )
    )

    val initialAuditLogs = listOf(
        AdminAuditLog(
            id = "log_01",
            adminId = "user_me",
            adminName = "Kai Vance (Admin)",
            targetUserId = "user_spammer_99",
            targetUserName = "spam_bot_99",
            action = "SUSPEND_USER_PERMANENT",
            reason = "Automated spamming and phishing attempts in chat",
            details = "Permanently blocked login access.",
            timestamp = "Today, 10:15 AM"
        ),
        AdminAuditLog(
            id = "log_02",
            adminId = "user_me",
            adminName = "Kai Vance (Admin)",
            targetUserId = "user_toxic_sam",
            targetUserName = "toxic_sam",
            action = "SUSPEND_USER_TEMPORARY",
            reason = "Toxic behavior in stream",
            details = "Suspended for 7 days (until 2026-08-26).",
            timestamp = "Today, 09:30 AM"
        ),
        AdminAuditLog(
            id = "log_03",
            adminId = "user_me",
            adminName = "Kai Vance (Admin)",
            targetUserId = "user_alex",
            targetUserName = "arivera",
            action = "FORCE_PASSWORD_RESET",
            reason = "User requested credential recovery assistance",
            details = "Sent secure recovery link to alex.r@example.com.",
            timestamp = "Yesterday, 4:20 PM"
        )
    )

    val sampleVideos = listOf(
        VideoItem(
            id = "vid_1",
            title = "Cyberpunk Neo-Tokyo: High Octane Chase (4K 60FPS)",
            channel = "Synthwave Cinema",
            duration = "24:15",
            views = "1.4M",
            category = "Sci-Fi & Action",
            platform = "YouTube",
            sampleUrl = "https://youtube.com/watch?v=kxa_demo_01"
        ),
        VideoItem(
            id = "vid_2",
            title = "Grand Finals Championship 2026 Live Highlights",
            channel = "Esports Central",
            duration = "45:30",
            views = "890K",
            category = "Gaming",
            platform = "Twitch",
            sampleUrl = "https://twitch.tv/esports_kxa"
        ),
        VideoItem(
            id = "vid_3",
            title = "Deep Space Voyage - Ambient Sound & Visual Odyssey",
            channel = "Cosmic Vision",
            duration = "1:15:00",
            views = "3.2M",
            category = "Music & Chill",
            platform = "Vimeo",
            sampleUrl = "https://vimeo.com/cosmic_kxa"
        ),
        VideoItem(
            id = "vid_4",
            title = "Epic Studio Ghibli Tribute Orchestral Concert",
            channel = "Anime Symphony",
            duration = "38:40",
            views = "2.1M",
            category = "Anime",
            platform = "YouTube",
            sampleUrl = "https://youtube.com/watch?v=ghibli_kxa"
        )
    )

    val activeRooms = listOf(
        WatchRoom(
            id = "room_cyberpunk",
            code = "KX-8492",
            title = "Cyberpunk Night & Chill Discussion",
            host = friendMaya,
            videoTitle = "Cyberpunk Neo-Tokyo: High Octane Chase (4K 60FPS)",
            videoSource = "YouTube",
            videoUrl = "https://youtube.com/watch?v=kxa_demo_01",
            currentPositionSeconds = 342,
            totalDurationSeconds = 1455,
            isPlaying = true,
            participants = listOf(friendMaya, currentUser, friendAlex, friendJordan),
            category = "Sci-Fi & Action",
            isPrivate = false,
            maxParticipants = 10
        ),
        WatchRoom(
            id = "room_esports",
            code = "KX-2041",
            title = "LoL Pro Finals Watch Party 🏆",
            host = friendAlex,
            videoTitle = "Grand Finals Championship 2026 Live Highlights",
            videoSource = "Twitch",
            videoUrl = "https://twitch.tv/esports_kxa",
            currentPositionSeconds = 1290,
            totalDurationSeconds = 2730,
            isPlaying = true,
            participants = listOf(friendAlex, friendDevon),
            category = "Gaming",
            isPrivate = false,
            maxParticipants = 15
        ),
        WatchRoom(
            id = "room_lofi",
            code = "KX-9910",
            title = "Late Night Study & Lofi Beats ☕",
            host = friendJordan,
            videoTitle = "Deep Space Voyage - Ambient Sound & Visual Odyssey",
            videoSource = "Vimeo",
            videoUrl = "https://vimeo.com/cosmic_kxa",
            currentPositionSeconds = 450,
            totalDurationSeconds = 4500,
            isPlaying = true,
            participants = listOf(friendJordan, friendSarah),
            category = "Music & Chill",
            isPrivate = true,
            pinCode = "4499",
            maxParticipants = 6
        )
    )

    val friendsList = listOf(
        Friend(
            user = friendMaya,
            isBestFriend = true,
            lastActive = "Active now",
            activeRoom = activeRooms[0],
            mutualFriends = 12
        ),
        Friend(
            user = friendAlex,
            isBestFriend = true,
            lastActive = "Active now",
            activeRoom = activeRooms[1],
            mutualFriends = 9
        ),
        Friend(
            user = friendJordan,
            isBestFriend = false,
            lastActive = "Active now",
            activeRoom = activeRooms[2],
            mutualFriends = 5
        ),
        Friend(
            user = friendDevon,
            isBestFriend = false,
            lastActive = "Active now",
            activeRoom = activeRooms[1],
            mutualFriends = 7
        ),
        Friend(
            user = friendSarah,
            isBestFriend = false,
            lastActive = "2 hours ago",
            activeRoom = null,
            mutualFriends = 4
        )
    )

    val initialRoomChat = listOf(
        ChatMessage(
            id = "msg_1",
            sender = friendMaya,
            text = "Welcome everyone! Syncing starting at 05:42 🍿",
            timestamp = "8:30 PM",
            isHost = true
        ),
        ChatMessage(
            id = "msg_2",
            sender = friendAlex,
            text = "This animation quality is absolutely insane!",
            timestamp = "8:31 PM"
        ),
        ChatMessage(
            id = "msg_3",
            sender = friendJordan,
            text = "Wait watch this drop coming up!! 🔥🔥",
            timestamp = "8:32 PM"
        ),
        ChatMessage(
            id = "msg_4",
            sender = currentUser,
            text = "Audio is perfectly synced on my end 👍",
            timestamp = "8:33 PM"
        )
    )

    val initialPrivateChat = listOf(
        ChatMessage(
            id = "pm_1",
            sender = friendMaya,
            text = "Hey Kai! Are we still watching the tournament finals tonight?",
            timestamp = "7:15 PM"
        ),
        ChatMessage(
            id = "pm_2",
            sender = currentUser,
            text = "Yes! I created a K Xa room with Alex and Jordan joining too.",
            timestamp = "7:18 PM"
        ),
        ChatMessage(
            id = "pm_3",
            sender = friendMaya,
            text = "Awesome! Send me the room code as soon as you start the stream 🍿",
            timestamp = "7:20 PM"
        ),
        ChatMessage(
            id = "pm_4",
            sender = currentUser,
            text = "Code is KX-8492 or tap join from your friends list!",
            timestamp = "7:22 PM"
        )
    )
}
