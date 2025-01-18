# NewsBuddy – Your AI-Powered News Companion

Stay informed like never before with **NewsBuddy**, the smart app that brings you the latest news, summarizes it, and lets you chat about it! Designed to keep you updated effortlessly, NewsBuddy transforms the way you consume and interact with daily news.  

## Key Features

- **Interactive News Chats:** Dive deeper into the day’s top headlines by engaging in AI-driven conversations about the topics you care about. 
- **Daily News Sync:** Automatically downloads the latest news each day to keep you informed.  
- **AI-Powered Summaries:** Get concise, easy-to-read summaries for all major news stories.  
- **Personalized Experience:** Explore news tailored to your interests, with interactive prompts to help you discover new perspectives.  

## App Architecture

- Utilized Kotlin and Jetpack Compose for Android UI development following the Material design guidelines
- Used MVI architecture by hoisting the UI states in ViewModels and ensuring better unidirectional data flow.
- Implemented a ServiceLocator pattern for seamless dependency injection throught the app and data layer.
- Integrated [News API](https://newsapi.org/) for fetching daily articles with Ktor client and Room DB KMP on a Kotlin Multiplatform library as backend.
- Summarised day’s news and Implemented chat functionality with real-time responses based on summarized news using **Vertex AI** with **Firebase**.
- Used Firebase analytics, crashlytics, remote config and Google Admob for scalability and monetization.

### Technologies Implemented

* Kotlin Multiplatform
* Jetpack Compose
* Material design
* MVI architecture
* Ktor Client
* Room DB
* News API
* Dependency Injection
* Vertex AI and Gemini
* Firebase Analytics, Crashlytics, Remote Config and Google Admob
