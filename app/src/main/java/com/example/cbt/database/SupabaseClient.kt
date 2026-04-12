import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest

object SupabaseClient {

    val client = createSupabaseClient(
        supabaseUrl = "https://fhfwbhujnzoecmobqumi.supabase.co",
        supabaseKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImZoZndiaHVqbnpvZWNtb2JxdW1pIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NzAyMzQyNDMsImV4cCI6MjA4NTgxMDI0M30.6Qv2l-e28yp2h69HhyfqXDqcvWkf0mPuaNJPO-h0aOQ"
    ) {
        install(Postgrest)
    }

}