package com.example.data

import android.util.Log
import com.example.BuildConfig
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.realtime.Realtime
import io.github.jan.supabase.storage.Storage

object KXaSupabase {

    val isConfigured: Boolean
        get() {
            val url = BuildConfig.SUPABASE_URL
            val key = BuildConfig.SUPABASE_PUBLISHABLE_KEY
            return url.isNotBlank() &&
                    url.startsWith("http") &&
                    !url.contains("YOUR_SUPABASE_URL") &&
                    key.isNotBlank() &&
                    !key.contains("YOUR_SUPABASE")
        }

    private val supabaseUrl: String
        get() {
            val url = BuildConfig.SUPABASE_URL
            return if (url.isNotBlank() && url.startsWith("http")) url else "https://placeholder-kxa.supabase.co"
        }

    private val supabasePublishableKey: String
        get() {
            val key = BuildConfig.SUPABASE_PUBLISHABLE_KEY
            return if (key.isNotBlank() && !key.contains("YOUR_SUPABASE")) key else "placeholder-key-kxa-dummy-value"
        }

    val client: SupabaseClient by lazy {
        try {
            createSupabaseClient(
                supabaseUrl = supabaseUrl,
                supabaseKey = supabasePublishableKey
            ) {
                install(Auth)
                install(Postgrest)
                install(Realtime)
                install(Storage)
            }
        } catch (e: Exception) {
            Log.w("KXaSupabase", "Supabase client initialized in fallback mode: ${e.message}")
            createSupabaseClient(
                supabaseUrl = "https://placeholder-kxa.supabase.co",
                supabaseKey = "placeholder-key"
            ) {
                install(Auth)
                install(Postgrest)
            }
        }
    }
}
