# Social Media App - Architecture Documentation

## Overview
This is a production-ready Android social media application built with **Clean Architecture**, **MVI pattern**, and modern Android development best practices. The app features feeds, chat, and user profiles with offline-first capabilities.

## Architecture Summary

### High-Level Architecture
```
┌─────────────────────────────────────────────────────────────┐
│                     PRESENTATION LAYER                       │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐      │
│  │   UI (Compose)│  │  ViewModel   │  │   UI State   │      │
│  │   Screens    │←→│   (MVI)      │←→│   & Events   │      │
│  └──────────────┘  └──────────────┘  └──────────────┘      │
└─────────────────────────────────────────────────────────────┘
                            ↕
┌─────────────────────────────────────────────────────────────┐
│                      DOMAIN LAYER                            │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐      │
│  │  Use Cases   │  │Domain Models │  │ Repositories │      │
│  │  (Business   │  │  (Entities)  │  │ (Interfaces) │      │
│  │   Logic)     │  │              │  │              │      │
│  └──────────────┘  └──────────────┘  └──────────────┘      │
└─────────────────────────────────────────────────────────────┘
                            ↕
┌─────────────────────────────────────────────────────────────┐
│                       DATA LAYER                             │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐      │
│  │ Repository   │  │  Data Models │  │   Mappers    │      │
│  │Implementations│  │  DTO/Entity  │  │              │      │
│  └──────────────┘  └──────────────┘  └──────────────┘      │
│         ↕                  ↕                                 │
│  ┌──────────────┐  ┌──────────────┐                        │
│  │ Remote (API) │  │Local (RoomDB)│                        │
│  │   Retrofit   │  │    Cache     │                        │
│  └──────────────┘  └──────────────┘                        │
└─────────────────────────────────────────────────────────────┘
```

## Layer Breakdown

### 1. Presentation Layer (UI + ViewModel)
**Location**: `feature/*/presentation/`

**Components**:
- **UI (Jetpack Compose)**: Declarative UI screens
- **ViewModel**: Manages UI state using MVI pattern
- **UI State**: Immutable state classes
- **UI Events**: User interactions
- **UI Effects**: One-time side effects
- **UI Models**: Presentation-optimized data models

**Pattern**: MVI (Model-View-Intent)
```kotlin
// State - Single source of truth
data class FeedUiState(
    val posts: Flow<PagingData<PostUiModel>>,
    val isRefreshing: Boolean,
    val error: String?
)

// Events - User intentions
sealed class FeedUiEvent {
    data class LikePost(val postId: String) : FeedUiEvent()
    data object Refresh : FeedUiEvent()
}

// Effects - One-time actions
sealed class FeedEffect {
    data class ShowError(val message: String) : FeedEffect()
}
```

**Key Files**:
- `FeedScreen.kt` - Composable UI
- `FeedViewModel.kt` - State management
- `FeedContract.kt` - State, Events, Effects
- `PostUiModel.kt` - UI data model

### 2. Domain Layer (Business Logic)
**Location**: `core/domain/`

**Components**:
- **Use Cases**: Single responsibility business logic
- **Domain Models**: Pure Kotlin entities
- **Repository Interfaces**: Data access contracts
- **Result Class**: Sealed class for operation states

**Principles**:
- No Android dependencies
- Pure Kotlin/Java
- Business rules enforcement
- Easily testable

**Key Files**:
- `GetFeedPostsUseCase.kt` - Fetch posts
- `LikePostUseCase.kt` - Like post logic
- `Post.kt` - Domain entity
- `PostRepository.kt` - Repository contract
- `Result.kt` - Result wrapper (Idle, Loading, Success, Error)

**Result Pattern**:
```kotlin
sealed class Resource<out T> {
    data object Idle : Resource<Nothing>()
    data object Loading : Resource<Nothing>()
    data class Success<T>(val data: T) : Resource<T>()
    data class Error(val message: String, val throwable: Throwable?) : Resource<Nothing>()
}
```

### 3. Data Layer (Repository Implementation)
**Location**: `feature/*/data/`

**Components**:
- **Repository Implementations**: Data access logic
- **Data Sources**: Remote (API) and Local (Database)
- **DTOs**: API response models
- **Entities**: Database models
- **Mappers**: Convert between model types

**Data Flow**:
```
API (DTO) → Mapper → Entity (DB) → Mapper → Domain Model → Mapper → UI Model
```

**Key Files**:
- `PostRepositoryImpl.kt` - Repository implementation
- `PostApi.kt` - Retrofit API interface
- `PostDto.kt` - API response model
- `PostEntity.kt` - Room database model
- `PostDao.kt` - Database operations
- `PostMapper.kt` - Model conversions

### 4. Dependency Injection (Hilt)
**Location**: `core/di/`

**Modules**:
- `NetworkModule.kt` - Retrofit, OkHttp, APIs
- `DatabaseModule.kt` - Room database, DAOs
- `RepositoryModule.kt` - Repository bindings

**Benefits**:
- Loose coupling
- Easy testing with mocks
- Singleton management
- Compile-time validation

## Key Features Implementation

### 1. Pagination with Paging 3
**Implementation**:
```kotlin
// RemoteMediator for network + cache
@OptIn(ExperimentalPagingApi::class)
class PostRemoteMediator(
    private val postApi: PostApi,
    private val database: SocialMediaDatabase
) : RemoteMediator<Int, PostEntity>()

// Repository returns Flow<PagingData>
fun getFeedPosts(): Flow<PagingData<Post>> {
    return Pager(
        config = PagingConfig(pageSize = 20),
        remoteMediator = PostRemoteMediator(...),
        pagingSourceFactory = { postDao.getPostsPagingSource() }
    ).flow
}
```

**Benefits**:
- Automatic loading states
- Network + Cache coordination
- Infinite scrolling
- Memory efficient

### 2. Offline-First with Room
**Strategy**:
1. Fetch from network
2. Cache in Room database
3. Return cached data on error
4. Observe changes with Flow

**Example**:
```kotlin
override suspend fun getPostById(postId: String): Resource<Post> {
    return try {
        val postDto = postApi.getPostById(postId)
        postDao.insertPost(postDto.toEntity()) // Cache
        Resource.Success(postDto.toDomain())
    } catch (e: Exception) {
        val cachedPost = postDao.getPostById(postId)
        if (cachedPost != null) {
            Resource.Success(cachedPost.toDomain())
        } else {
            Resource.Error("Failed to load post")
        }
    }
}
```

### 3. Reactive State Management
**Using Kotlin Flow**:
```kotlin
// ViewModel
private val _uiState = MutableStateFlow(FeedUiState())
val uiState: StateFlow<FeedUiState> = _uiState.asStateFlow()

// UI
val uiState by viewModel.uiState.collectAsState()
```

**Benefits**:
- Lifecycle-aware
- Automatic updates
- No memory leaks
- Backpressure handling

### 4. Type-Safe Navigation
**Using Compose Navigation**:
New NNavigation Material3 
```

## Model Mapping Strategy

### Four Model Types:
1. **DTO (Data Transfer Object)**: API responses
2. **Entity**: Database models
3. **Domain**: Business logic models
4. **UI Model**: Presentation-optimized models

### Mapping Flow:
```
Network → DTO → Entity (cache) → Domain → UI Model → Screen
```

### Example:
```kotlin
// 1. DTO from API
data class PostDto(
    @SerializedName("id") val id: String,
    @SerializedName("user_id") val userId: String
)

// 2. Entity for Room
@Entity(tableName = "posts")
data class PostEntity(
    @PrimaryKey val id: String,
    val userId: String
)

// 3. Domain model
data class Post(
    val id: String,
    val userId: String
)

// 4. UI model
data class PostUiModel(
    val id: String,
    val userId: String,
    val formattedTime: String // Presentation logic
)
```

## Testing Strategy

### Unit Tests
**Testable Components**:
- ✅ ViewModels (business logic)
- ✅ Use Cases (business rules)
- ✅ Repositories (data operations)
- ✅ Mappers (transformations)

**Example Test**:
```kotlin
@Test
fun `likePost should call repository and emit success`() = runTest {
    // Given
    val postId = "123"
    coEvery { likePostUseCase(postId) } returns Result.Success(mockPost)
    
    // When
    viewModel.onEvent(FeedUiEvent.LikePost(postId))
    
    // Then
    coVerify { likePostUseCase(postId) }
    assertTrue(result is Result.Success)
}
```

**Test Libraries**:
- JUnit4
- MockK (mocking)
- Turbine (Flow testing)
- Coroutines Test

### Why This is Perfect for Unit Testing:
1. **Dependency Injection**: Easy mocking
2. **Pure Functions**: No side effects
3. **Interfaces**: Abstract implementations
4. **Coroutines**: Deterministic testing
5. **Sealed Classes**: Exhaustive testing

## Technology Stack

### Core
- **Language**: Kotlin
- **Architecture**: Clean Architecture + MVI
- **UI**: Jetpack Compose
- **DI**: Hilt

### Networking
- **HTTP Client**: Retrofit + OkHttp
- **Serialization**: Gson
- **Interceptors**: Logging, Auth

### Local Storage
- **Database**: Room
- **Coroutines**: Flow integration
- **Pagination**: PagingSource

### Enycrypted and secured datastore and shared preferences
  
What's Encrypted (EncryptedPreferences):

✅ Auth tokens (JWT/Bearer)

✅ Refresh tokens

✅ User credentials

✅ Session IDs

✅ Device IDs

What's in DataStore (Settings):

✅ Theme mode (reactive)

✅ Notifications enabled

✅ Feed layout preferences

✅ Privacy settings

✅ Language

✅ App launch count


### Async
- **Coroutines**: Async operations
- **Flow**: Reactive streams
- **StateFlow**: State management

### Pagination
- **Paging 3**: Infinite scroll
- **RemoteMediator**: Network + Cache

### Image Loading
- **Coil**: Async image loading

### Testing
- **JUnit**: Test framework
- **MockK**: Mocking
- **Turbine**: Flow testing
- **Coroutines Test**: Async testing

## State Management (MVI)

### MVI Flow:
```
User Action → Intent (Event) → ViewModel → State Update → UI Render
     ↑                                                         ↓
     └──────────────────← Effect (Side Effect) ←─────────────┘
```

### Benefits:
- **Unidirectional Data Flow**: Predictable state
- **Single Source of Truth**: One state object
- **Immutable State**: No unexpected changes
- **Time Travel Debugging**: State history
- **Easy Testing**: Pure functions

## Best Practices Implemented

### Clean Architecture
✅ Separation of concerns
✅ Dependency rule (inward)
✅ Framework independence
✅ Testable business logic

### SOLID Principles
✅ Single Responsibility
✅ Open/Closed
✅ Liskov Substitution
✅ Interface Segregation
✅ Dependency Inversion

### Android Best Practices
✅ Lifecycle awareness
✅ Configuration change handling
✅ Memory leak prevention
✅ Offline-first approach
✅ Efficient pagination

### Code Quality
✅ Type safety
✅ Null safety
✅ Immutability
✅ Documentation
✅ Consistent naming

## Performance Optimizations

1. **Pagination**: Load data incrementally
2. **Caching**: Room database for offline
3. **Image Loading**: Coil with caching
4. **Coroutines**: Non-blocking async
5. **Flow**: Backpressure handling
6. **Lazy Loading**: Compose LazyColumn
7. **State Hoisting**: Recomposition optimization

## Scalability

### Adding New Features:
1. Create feature package
2. Add domain models
3. Create use cases
4. Implement repository
5. Build ViewModel
6. Create UI screens
7. Update navigation
8. Write tests

### Modularization Ready:
- Feature modules
- Core module
- Shared modules
- Dynamic delivery

## Summary

This Social Media App demonstrates **production-grade Android development** with:

- ✅ **Clean Architecture** for maintainability
- ✅ **MVI Pattern** for predictable state
- ✅ **Offline-First** with Room caching
- ✅ **Pagination** for performance
- ✅ **Dependency Injection** for testability
- ✅ **Reactive Programming** with Flow
- ✅ **Type Safety** with Kotlin
- ✅ **Modern UI** with Jetpack Compose
- ✅ **Comprehensive Testing** strategy


## Overview
<img width="270" height="480" alt="image" src="https://github.com/user-attachments/assets/a8bfee29-2cf0-475d-9814-93a8560792dd" />
<img width="270" height="480" alt="image" src="https://github.com/user-attachments/assets/6c9327ad-1ce0-41b5-9907-bba951a0f616" />
<img width="270" height="480" alt="image" src="https://github.com/user-attachments/assets/5d9a66ef-599a-4203-9853-a5e4b588c5c7" />
<img width="270" height="480" alt="image" src="https://github.com/user-attachments/assets/b62968ba-e777-4f50-8c31-8c842e923a72" />
<img width="270" height="480" alt="image" src="https://github.com/user-attachments/assets/3daf718b-ac15-42d5-ae3a-ea4046ca8169" />
<img width="270" height="480" alt="image" src="https://github.com/user-attachments/assets/96021b72-7e30-433e-b63f-2a3ad2bac3f0" />
<img width="270" height="480" alt="image" src="https://github.com/user-attachments/assets/b311667f-aba3-45e6-a6a4-222cae62b271" />



