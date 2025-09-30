# Diagrama de Arquitectura - Aplicación LOAM TP1

## Arquitectura General de la Aplicación

```mermaid
graph TB
    %% UI Layer
    subgraph "📱 Presentation Layer"
        MA[MainActivity<br/>🏠 Main Menu]
        CA[ChatActivity<br/>💬 Chat]
        PA[PreciosActivity<br/>💰 Precios]
        CAM[CameraActivity<br/>📸 Cámara]
        GA[GrabadorAudioActivity<br/>🎙️ Audio]
        ARM[ARMeasureActivity<br/>📏 AR Medición]
        HAR[HelloArActivity<br/>🔮 AR Demo]
    end

    %% ViewModel Layer
    subgraph "🧠 ViewModel Layer"
        PVM[PreciosViewModel]
        DVM[DolarViewModel]
    end

    %% Service Layer
    subgraph "⚙️ Service Layer"
        PS[PreciosService]
        DS[DolarService]
    end

    %% Repository Layer
    subgraph "🗄️ Repository Layer"
        MR[MensajeRepository]
        PR[PreciosRepository]
    end

    %% Model Layer
    subgraph "📊 Model Layer"
        MSG[Mensaje]
        PRECIO[PrecioReferencia]
        DOLAR[Dolar]
        TIPO[TipoPrecio]
    end

    %% External Services
    subgraph "☁️ Firebase Services"
        FS[Firestore Database]
        FA[Firebase Analytics]
    end

    subgraph "🌐 External APIs"
        DAPI[DolarAPI<br/>https://dolarapi.com]
    end

    %% Utils & Hardware
    subgraph "🔧 Utils & Hardware"
        PERM[Permisos]
        CAMH[CamaraHandler]
        ARC[ARCore]
        CAM2[Camera2 API]
        AUDIO[AudioRecorder]
    end

    %% Connections
    MA --> CA
    MA --> PA
    MA --> CAM
    MA --> GA
    MA --> ARM
    MA --> HAR

    PA --> PVM
    MA --> DVM

    PVM --> PS
    DVM --> DS

    PS --> PR
    DS --> DAPI

    CA --> MR
    PR --> FS
    MR --> FS

    PVM --> PRECIO
    DVM --> DOLAR
    MR --> MSG
    PR --> TIPO

    CAM --> CAMH
    GA --> AUDIO
    ARM --> ARC
    HAR --> ARC

    FS --> FA

    CAMH --> CAM2
    ARM --> CAM2

    %% Permission management
    CAM --> PERM
    GA --> PERM
    ARM --> PERM
    MA --> PERM

    %% Styling
    classDef uiClass fill:#e1f5fe,stroke:#01579b,stroke-width:2px
    classDef viewModelClass fill:#f3e5f5,stroke:#4a148c,stroke-width:2px
    classDef serviceClass fill:#e8f5e8,stroke:#1b5e20,stroke-width:2px
    classDef repositoryClass fill:#fff3e0,stroke:#e65100,stroke-width:2px
    classDef modelClass fill:#fce4ec,stroke:#880e4f,stroke-width:2px
    classDef firebaseClass fill:#ffebee,stroke:#c62828,stroke-width:2px
    classDef externalClass fill:#f1f8e9,stroke:#33691e,stroke-width:2px
    classDef utilClass fill:#f9fbe7,stroke:#827717,stroke-width:2px

    class MA,CA,PA,CAM,GA,ARM,HAR uiClass
    class PVM,DVM viewModelClass
    class PS,DS serviceClass
    class MR,PR repositoryClass
    class MSG,PRECIO,DOLAR,TIPO modelClass
    class FS,FA firebaseClass
    class DAPI externalClass
    class PERM,CAMH,ARC,CAM2,AUDIO utilClass
```

## Detalle de Firebase Integration

```mermaid
graph LR
    subgraph "📱 App Components"
        CA[ChatActivity]
        PA[PreciosActivity]
        MR[MensajeRepository]
        PR[PreciosRepository]
    end

    subgraph "☁️ Firebase"
        FS[(Firestore)]
        FA[Analytics]
        
        subgraph "📄 Collections"
            MC[mensajes]
            PC[precios_referencia]
        end
    end

    subgraph "🔧 Configuration"
        GS[google-services.json]
        GP[Google Services Plugin]
    end

    CA --> MR
    PA --> PR
    
    MR -->|CRUD Operations| MC
    PR -->|CRUD Operations| PC
    
    MC --> FS
    PC --> FS
    
    FS --> FA
    
    GS -.->|Configures| FS
    GP -.->|Enables| GS

    %% Data flow annotations
    MR -->|Real-time<br/>Snapshots| CA
    PR -->|Flow<br/>Emissions| PA
```

## Arquitectura por Capas

```mermaid
graph TD
    subgraph "🎨 UI Layer"
        A1[Activities]
        A2[Compose Components]
        A3[View Binding]
    end

    subgraph "🧠 Presentation Layer"
        V1[ViewModels]
        V2[UI States]
        V3[LiveData/Flow]
    end

    subgraph "⚙️ Business Layer"
        S1[Services]
        S2[Use Cases]
        S3[Business Logic]
    end

    subgraph "🗄️ Data Layer"
        R1[Repositories]
        R2[Data Sources]
        R3[Models]
    end

    subgraph "🌐 External Layer"
        E1[Firebase Firestore]
        E2[REST APIs]
        E3[Hardware APIs]
    end

    A1 --> V1
    A2 --> V1
    A3 --> V1
    
    V1 --> S1
    V2 --> S1
    V3 --> S1
    
    S1 --> R1
    S2 --> R1
    S3 --> R1
    
    R1 --> E1
    R2 --> E1
    R3 --> E1
    
    R1 --> E2
    R2 --> E2
    
    R1 --> E3
    R2 --> E3

    classDef uiLayer fill:#e3f2fd,stroke:#1976d2
    classDef presentationLayer fill:#f3e5f5,stroke:#7b1fa2
    classDef businessLayer fill:#e8f5e8,stroke:#388e3c
    classDef dataLayer fill:#fff3e0,stroke:#f57c00
    classDef externalLayer fill:#ffebee,stroke:#d32f2f

    class A1,A2,A3 uiLayer
    class V1,V2,V3 presentationLayer
    class S1,S2,S3 businessLayer
    class R1,R2,R3 dataLayer
    class E1,E2,E3 externalLayer
```

## Tecnologías y Dependencias Clave

### 🔧 Core Technologies
- **Kotlin** - Lenguaje principal
- **Android SDK** - Plataforma base
- **Jetpack Compose** - UI moderna
- **View Binding** - Binding de vistas

### ☁️ Firebase Services
- **Firestore** - Base de datos NoSQL en tiempo real
- **Analytics** - Análisis de uso
- **Google Services** - Configuración y autenticación

### 📱 Android Jetpack
- **Lifecycle** - Gestión del ciclo de vida
- **ViewModel** - Arquitectura MVVM
- **LiveData/Flow** - Programación reactiva
- **Navigation** - Navegación entre pantallas
- **Camera** - API de cámara

### 🔮 AR/3D Features
- **ARCore** - Realidad aumentada
- **Camera2** - Control avanzado de cámara
- **OBJ Loader** - Carga de modelos 3D

### 🌐 Networking
- **Ktor** - Cliente HTTP para APIs REST
- **Gson** - Serialización JSON

### 📊 Data Flow
1. **UI Events** → ViewModels
2. **ViewModels** → Services/Repositories
3. **Repositories** → Firebase/APIs
4. **Data Changes** → Reactive Updates (Flow/LiveData)
5. **UI Updates** → Compose Recomposition

### 🔐 Permissions
- **CAMERA** - Acceso a cámara
- **RECORD_AUDIO** - Grabación de audio
- **INTERNET** - Conexión a APIs y Firebase
- **STORAGE** - Almacenamiento local
- **CALL_PHONE** - Llamadas telefónicas

Esta aplicación sigue una arquitectura limpia con separación clara de responsabilidades, implementando patrones modernos de Android como MVVM, programación reactiva con Flow/LiveData, y integración robusta con Firebase para funcionalidades en tiempo real.