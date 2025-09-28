# Configuración de Firebase para TrabajoPractico1LOAM

## ✅ Configuración Completada

Se ha configurado Firebase con Firestore en tu proyecto de Android Kotlin. Los cambios incluyen:

### 1. Dependencias Agregadas
- **gradle/libs.versions.toml**: Agregadas versiones de Firebase y Google Services
- **build.gradle.kts (proyecto)**: Agregado plugin de Google Services
- **app/build.gradle.kts**: Agregado plugin y dependencias de Firebase

### 2. Código Implementado
- **MainActivity.kt**: Implementado ejemplo funcional con Firestore
- Funciones de escritura y lectura de datos
- Interfaz de usuario con Compose para interactuar con Firestore

## 🔧 Pasos Pendientes para Completar la Configuración

### 1. Crear Proyecto en Firebase Console
1. Ve a [Firebase Console](https://console.firebase.google.com/)
2. Crea un nuevo proyecto o selecciona uno existente
3. Agrega una aplicación Android al proyecto
4. Usa el package name: `com.loam.trabajopractico1loam`

### 2. Descargar google-services.json
1. En la consola de Firebase, descarga el archivo `google-services.json`
2. **IMPORTANTE**: Reemplaza el archivo placeholder en `app/google-services.json` con el archivo real descargado
3. El archivo actual contiene valores de placeholder que deben ser reemplazados

### 3. Habilitar Firestore
1. En Firebase Console, ve a "Firestore Database"
2. Click en "Crear base de datos"
3. Selecciona el modo (producción o prueba)
4. Elige una ubicación para tu base de datos

### 4. Configurar Reglas de Seguridad (Opcional)
Para desarrollo, puedes usar estas reglas permisivas:
```javascript
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    match /{document=**} {
      allow read, write: if true;
    }
  }
}
```

## 🚀 Funcionalidades Implementadas

### FirestoreDemo Component
- **Guardar datos**: Permite escribir texto a Firestore
- **Leer datos**: Obtiene el último registro guardado
- **UI intuitiva**: Interfaz simple con Material Design 3

### Funciones de Firestore
- `saveDataToFirestore()`: Guarda datos con timestamp
- `readDataFromFirestore()`: Lee el último registro guardado

## 📱 Uso de la Aplicación

1. Ejecuta la app en tu dispositivo/emulador
2. Escribe texto en el campo de entrada
3. Presiona "Guardar en Firestore" para almacenar datos
4. Presiona "Leer de Firestore" para recuperar el último dato guardado

## 🐛 Solución de Problemas

- **Error de compilación**: Asegúrate de que el archivo `google-services.json` sea el correcto
- **Error de conexión**: Verifica que Firestore esté habilitado en Firebase Console
- **Permisos**: Revisa las reglas de seguridad de Firestore

## 📚 Próximos Pasos

Puedes expandir esta configuración agregando:
- Autenticación de usuarios
- Estructura de datos más compleja
- Escucha en tiempo real de cambios
- Manejo de errores más robusto
- Validación de datos

¡Firebase con Firestore está listo para usar en tu proyecto! 🎉