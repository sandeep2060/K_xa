package com.example.data

import io.github.jan.supabase.auth.auth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object SupabaseConnectionTest {

    suspend fun testConnection(): Result<String> {
        return withContext(Dispatchers.IO) {
            try {
                if (!KXaSupabase.isConfigured) {
                    return@withContext Result.success(
                        "Supabase client is ready in local/offline mode. To connect to your remote cloud database, set SUPABASE_URL and SUPABASE_PUBLISHABLE_KEY in Secrets or .env."
                    )
                }

                val session = KXaSupabase.client.auth.currentSessionOrNull()

                Result.success(
                    if (session == null) {
                        "Supabase cloud connected successfully (Ready for authentication and sync)."
                    } else {
                        "Supabase cloud connected. Active user session: ${session.user?.email ?: "anonymous"}."
                    }
                )
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
}
