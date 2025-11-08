package com.example.vibezapp10.Network

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.realtime.Realtime
import io.github.jan.supabase.storage.Storage

object SupabaseManager {

    val client:SupabaseClient = createSupabaseClient(
        supabaseUrl = "https://lueirepdjoxpbziluboi.supabase.co",
        supabaseKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Imx1ZWlyZXBkam94cGJ6aWx1Ym9pIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NTc1OTI4MjAsImV4cCI6MjA3MzE2ODgyMH0.fEywGdJneKcet5t4Pp01XXI-k9h3gukuRAjJWPXoEwQ"
    ) {
        install(Postgrest)
        install(Storage)
        install(Realtime)
    }

}

