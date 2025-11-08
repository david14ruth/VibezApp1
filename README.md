🌀 VibezApp  🎵
Your personalized music experience powered by Kotlin, Firebase, and Supabase

📱 About the App
VibezApp10 is an Android music application that allows users to search, stream lyrics, and personalize their experience through a modern, clean interface.
Built with Kotlin, it integrates Firebase Authentication for secure login and Supabase for cloud data storage, enabling a seamless and connected experience.

🎯 Purpose

The main purpose of VibezApp10 is to:

Provide an intuitive platform for users to discover and explore songs.

Offer a smooth and engaging lyrics viewing experience.

Allow users to customize profiles and preferences.

Demonstrate best practices in modern Android development using Jetpack components, Coroutines, and MVVM principles.

Showcase continuous integration and delivery (CI/CD) using GitHub Actions.

🎨 App Features

✅ User Authentication – Register and log in securely using Firebase
✅ Search Songs – Dynamic search powered by the SongsService API
✅ Lyrics Viewer – Launch full lyrics in LyricsActivity
✅ Profile Management – Sync user info from Supabase profiles
✅ Favorites System – Mark songs you love
✅ Recent Searches – Quickly access previous queries
✅ Dark Theme Support – Eye-friendly viewing
✅ Real-time Data Sync – Supabase ensures your preferences stay updated

📸 Screenshots
![WhatsApp Image 2025-10-07 at 21 12 51](https://github.com/user-attachments/assets/515d488d-9e31-4955-8435-7a295e1ab7ab)
![WhatsApp Image 2025-10-07 at 21 12 50](https://github.com/user-attachments/assets/53fde743-66c5-42a0-a41c-8422c8bf10db)
![WhatsApp Image 2025-10-07 at 21 12 50 (1)](https://github.com/user-attachments/assets/657b7cfa-5691-4e82-8d97-aad30fe53f4e)
![WhatsApp Image 2025-10-07 at 21 12 50 (2)](https://github.com/user-attachments/assets/9f69b109-b52a-45b4-9468-a66c9e319f57)


🚀 Development Workflow steps to run:

1. Clone the repository:

git clone https://github.com/yourusername/vibezapp10.git
cd vibezapp10


2. Open in Android Studio 

3. Add your own Firebase config:

   Place your google-services.json in /app

4.Add your Supabase credentials in a Kotlin object:

  object SupabaseManager {
    const val SUPABASE_URL = "https://YOUR_URL.supabase.co"
    const val SUPABASE_KEY = "YOUR_SERVICE_KEY"
  }


5.Build and run:

  ./gradlew assembleDebug


💬 Acknowledgments

Supabase Kotlin SDK

Firebase for Android

ReactiveCircus Emulator Runner

The Android Developer Community ❤️

YouTube: https://youtube.com/shorts/ioEkxlm70HI?si=t4gEjZggT7tgRsrl





