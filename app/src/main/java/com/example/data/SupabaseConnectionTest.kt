package com.example.data

import io.github.jan.supabase.auth.auth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object SupabaseConnectionTest {

    suspend fun testConnection(): Result<String> {
        return withContext(Dispatchers.IO) {
            try {
                val session = KXaSupabase.client.auth.currentSessionOrNull()

                Result.success(
                    if (session == null) {
                        "Supabase connected successfully"
                    } else {
                        "Supabase connected. User is signed in."
                    }
                )
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
}