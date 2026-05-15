# KutiraKoneAI 🧵

A mobile application that connects fabric scrap sellers and buyers through an intelligent swap and purchase platform with AI-powered design suggestions.

## 📋 Table of Contents

- [Features](#features)
- [Screenshots](#screenshots)
- [Tech Stack](#tech-stack)
- [Installation](#installation)
- [Project Structure](#project-structure)
- [Usage](#usage)
- [Firebase Setup](#firebase-setup)
- [Contributing](#contributing)
- [License](#license)

## ✨ Features

### Core Features
- 🔐 **User Authentication** - Secure login and registration with Firebase Auth
- 📸 **Scrap Management** - Upload, view, and filter fabric scraps by material type
- 💬 **Real-time Chat** - Direct messaging between users
- 🔄 **Swap Requests** - Request to swap scraps with other users
- 💰 **Buy Requests** - Purchase scraps directly from sellers
- 🤖 **AI Design Suggestions** - AI-powered design ideas for fabric scraps
- 📱 **User Profiles** - Complete user profile management
- 🏷️ **Material Filtering** - Filter scraps by material (Silk, Cotton, Wool)

### User Roles
- **Sellers**: Upload and manage fabric scraps
- **Buyers**: Browse, request swaps, and purchase scraps
- **Both**: Send and receive swap/buy requests, chat with users

## 📸 Screenshots

| Home Screen | Scrap Details | My Requests | Chat |
|-------------|---------------|-------------|------|
| ![Home](docs/home.png) | ![Details](docs/details.png) | ![Requests](docs/requests.png) | ![Chat](docs/chat.png) |

## 🛠️ Tech Stack

### Frontend
- **Language**: Kotlin
- **UI Framework**: Android Material Design 3
- **Architecture**: MVVM (Model-View-ViewModel)
- **Networking**: Retrofit

### Backend
- **Firebase Authentication** - User management
- **Cloud Firestore** - Real-time database
- **Firebase Cloud Messaging** - Push notifications
- **Firebase Storage** - Image storage

### Libraries
- AndroidX (AppCompat, RecyclerView, ConstraintLayout)
- Material Components
- Glide - Image loading
- Firebase Admin SDK

## 📦 Installation

### Prerequisites
- Android Studio (Arctic Fox or later)
- Android SDK 21 or higher
- Firebase account
- Git

### Steps

1. **Clone the Repository**
   ```bash
   git clone https://github.com/YOUR_USERNAME/KutiraKoneAI.git
   cd KutiraKoneAI
   ```

2. **Open in Android Studio**
   - Launch Android Studio
   - Select "Open an Existing Project"
   - Navigate to the cloned folder
   - Click "Open"

3. **Firebase Setup**
   - Go to [Firebase Console](https://console.firebase.google.com/)
   - Create a new project named "KutiraKoneAI"
   - Download `google-services.json`
   - Place it in `app/` directory

4. **Enable Firebase Services**
   - Authentication → Enable Email/Password
   - Firestore Database → Create database (Production mode)
   - Storage → Create bucket

5. **Build and Run**
   ```bash
   ./gradlew build
   ```
   - Connect Android device or emulator
   - Click "Run" in Android Studio

## 📁 Project Structure

```
KutiraKoneAI/
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/example/kutirakonai/
│   │   │   │   ├── activities/
│   │   │   │   │   ├── MainActivity.kt
│   │   │   │   │   ├── AuthActivity.kt
│   │   │   │   │   ├── MyRequestsActivity.kt
│   │   │   │   │   ├── SwapRequestActivity.kt
│   │   │   │   │   ├── ChatActivity.kt
│   │   │   │   │   ├── UploadScrapActivity.kt
│   │   │   │   │   └── SplashActivity.kt
│   │   │   │   ├── adapters/
│   │   │   │   │   ├── ScrapAdapter.kt
│   │   │   │   │   └── RequestsAdapter.kt
│   │   │   │   ├── models/
│   │   │   │   │   ├── ScrapItem.kt
│   │   │   │   │   ├── SwapRequest.kt
│   │   │   │   │   └── ChatMessage.kt
│   │   │   │   ├── utils/
│   │   │   │   │   ├── FirebaseHelper.kt
│   │   │   │   │   └── HuggingFaceHelper.kt
│   │   │   │   └── ui/
│   │   │   │       └── theme/
│   │   │   ├── res/
│   │   │   │   ├── layout/
│   │   │   │   ├── drawable/
│   │   │   │   ├── values/
│   │   │   │   └── menu/
│   │   │   └── AndroidManifest.xml
│   │   └── google-services.json
│   ├── build.gradle.kts
│   └── proguard-rules.pro
├── build.gradle.kts
├── settings.gradle.kts
├── README.md
└── .gitignore
```

## 🚀 Usage

### For Users

1. **Sign Up**
   - Launch the app
   - Tap "Create Account"
   - Enter email and password
   - Verify email

2. **Upload Scrap**
   - Tap green "+" button
   - Select image from gallery
   - Fill in material, color, and size
   - Tap "Upload"

3. **Browse Scraps**
   - View all scraps on home screen
   - Filter by material using chips
   - Tap scrap card to view details

4. **Swap or Buy**
   - Tap scrap card
   - Choose "Swap" or "Buy"
   - Add message (optional)
   - Send request

5. **View Requests**
   - Tap green "✓" button
   - Switch between "Sent", "Received", "Swap" tabs
   - Accept, Reject, or Chat

6. **Chat**
   - Tap "Chat" button on any request
   - Send and receive messages in real-time

## 🔧 Firebase Setup

### Firestore Database Structure

```
users/
├── {uid}
│   ├── name: string
│   ├── email: string
│   ├── profilePicUrl: string
│   └── createdAt: timestamp

scraps/
├── {scrapId}
│   ├── id: string
│   ├── userId: string
│   ├── ownerName: string
│   ├── material: string
│   ├── color: string
│   ├── sizeMeters: number
│   ├── imageUrl: string
│   ├── available: boolean
│   ├── aiSuggestions: string
│   └── createdAt: timestamp

swapRequests/
├── {requestId}
│   ├── id: string
│   ├── requesterId: string
│   ├── requesterName: string
│   ├── scrapOwnerId: string
│   ├── scrapOwnerName: string
│   ├── scrapId: string
│   ├── scrapMaterial: string
│   ├── scrapColor: string
│   ├── scrapSize: number
│   ├── type: string (SWAP/BUY)
│   ├── status: string (pending/accepted/rejected)
│   ├── message: string
│   └── createdAt: timestamp

chatMessages/
├── {requestId}
│   ├── {messageId}
│   │   ├── senderId: string
│   │   ├── senderName: string
│   │   ├── message: string
│   │   └── timestamp: timestamp
```

### Security Rules

Set Firestore security rules to protect user data:

```
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    match /users/{uid} {
      allow read, write: if request.auth.uid == uid;
    }
    match /scraps/{doc=**} {
      allow read: if true;
      allow write: if request.auth != null;
    }
    match /swapRequests/{doc=**} {
      allow read, write: if request.auth != null;
    }
    match /chatMessages/{doc=**} {
      allow read, write: if request.auth != null;
    }
  }
}
```

## 📝 Dependencies

### Gradle Dependencies
- androidx.appcompat:appcompat:1.6.1
- androidx.recyclerview:recyclerview:1.3.0
- com.google.android.material:material:1.9.0
- com.google.firebase:firebase-auth-ktx
- com.google.firebase:firebase-firestore-ktx
- com.google.firebase:firebase-storage-ktx
- com.github.bumptech.glide:glide:4.15.1

## 🐛 Known Issues

- AI suggestions may take a few seconds to load
- Image compression is applied for faster uploads
- Chat may have slight delays on slow connections

## 🚀 Future Enhancements

- [ ] In-app payment system
- [ ] Rating and review system
- [ ] Wishlist feature
- [ ] Advanced search and filters
- [ ] Sustainability tracking
- [ ] Community forum
- [ ] Push notifications for requests

## 🤝 Contributing

Contributions are welcome! Please follow these steps:

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit changes (`git commit -m 'Add AmazingFeature'`)
4. Push to branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 👥 Authors

- **Shrujana R.T** - Internship Project
- https://github.com/S2C-ux

## 📞 Contact & Support

- 📧 Email: rtshrujana@gmail.com
- 💬 Issues:https://github.com/S2C-ux/KutiraKoneAI.githttps://github.com/S2C-ux/KutiraKoneAI.git/issues

## 🙏 Acknowledgments

- Firebase for backend services
- Material Design for UI components
- HuggingFace for AI suggestions
- Android community for support

---

**Made with ❤️ during internship**

Last Updated: 2026
