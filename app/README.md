# 📦 Sistema de Registro de Pedidos para Delivery

Aplicación Android para gestión de pedidos de delivery con arquitectura MVVM, base de datos Room, sincronización con servidor mediante Retrofit y captura de imágenes con cámara real.

![Android](https://img.shields.io/badge/Android-3DDC84?style=for-the-badge&logo=android&logoColor=white)
![Kotlin](https://img.shields.io/badge/Kotlin-0095D5?style=for-the-badge&logo=kotlin&logoColor=white)
![Material Design](https://img.shields.io/badge/Material%20Design-757575?style=for-the-badge&logo=material-design&logoColor=white)

## 📋 Características Principales

- ✅ **Arquitectura MVVM**: Separación clara entre Model, View y ViewModel
- ✅ **Base de Datos Room**: Almacenamiento local persistente de pedidos
- ✅ **Sincronización con Retrofit**: Envío de datos a servidor REST
- ✅ **Captura de Imágenes con CameraX**: Fotos reales de pedidos
- ✅ **Sistema de Logs**: Registro detallado de eventos con fecha y hora
- ✅ **UI/UX Moderna**: Material Design 3 con tema oscuro
- ✅ **Gestión de Estados**: Control de estados de pedidos (Pendiente, En Camino, Entregado, Cancelado)


## 🏗️ Arquitectura del Proyecto

```
app/
├── data/
│   ├── local/
│   │   ├── dao/              # Data Access Objects
│   │   ├── database/         # Room Database
│   │   └── entity/           # Entidades de base de datos
│   ├── remote/
│   │   └── api/              # Retrofit API Service
│   └── repository/           # Repositorios (patrón Repository)
├── model/                    # Modelos de datos para API
├── ui/
│   ├── adapter/              # RecyclerView Adapters
│   └── activities/           # Activities de la app
├── viewmodel/                # ViewModels (MVVM)
└── util/                     # Utilidades (Logs, Imágenes)
```

## 🛠️ Tecnologías Utilizadas

- **Lenguaje**: Kotlin
- **Arquitectura**: MVVM (Model-View-ViewModel)
- **Base de Datos**: Room Persistence Library
- **Networking**: Retrofit 2 + OkHttp
- **Imágenes**: CameraX + Glide
- **UI**: Material Design Components 3
- **Async**: Kotlin Coroutines + LiveData
- **ViewBinding**: Para acceso type-safe a vistas

## 📦 Dependencias Principales

```gradle
// Room Database
implementation "androidx.room:room-runtime:2.6.1"
kapt "androidx.room:room-compiler:2.6.1"

// Retrofit
implementation "com.squareup.retrofit2:retrofit:2.9.0"
implementation "com.squareup.retrofit2:converter-gson:2.9.0"

// CameraX
implementation "androidx.camera:camera-camera2:1.3.1"
implementation "androidx.camera:camera-lifecycle:1.3.1"
implementation "androidx.camera:camera-view:1.3.1"

// ViewModel & LiveData
implementation "androidx.lifecycle:lifecycle-viewmodel-ktx:2.7.0"
implementation "androidx.lifecycle:lifecycle-livedata-ktx:2.7.0"
```

## 🚀 Instalación y Configuración

### Requisitos Previos
- Android Studio Arctic Fox o superior
- SDK de Android 24 (Nougat) o superior
- JDK 17

### Pasos de Instalación

1. **Clonar el repositorio**
```bash
git clone https://github.com/tu-usuario/delivery-app.git
cd delivery-app
```

2. **Configurar URL del servidor**

Editar `RetrofitClient.kt` y actualizar la URL base:
```kotlin
private const val BASE_URL = "https://tu-servidor.com/api/"
```

3. **Sincronizar Gradle**
```bash
./gradlew build
```

4. **Ejecutar la aplicación**
- Conectar un dispositivo Android o iniciar un emulador
- Presionar Run en Android Studio o ejecutar:
```bash
./gradlew installDebug
```

## 📱 Funcionalidades

### 1. Gestión de Pedidos
- ➕ **Crear Pedido**: Formulario completo con validaciones
- 📝 **Editar Pedido**: Modificación de datos existentes
- 🗑️ **Eliminar Pedido**: Borrado de registros
- 👁️ **Ver Detalle**: Visualización completa de información

### 2. Captura de Imágenes
- 📸 Integración con cámara del dispositivo
- 🖼️ Vista previa de imagen
- 💾 Almacenamiento local optimizado
- 📦 Compresión automática para reducir tamaño

### 3. Sincronización con Servidor
- 🔄 Sincronización individual de pedidos
- 📤 Sincronización masiva de pendientes
- 🔐 Conversión de imágenes a Base64
- ⚠️ Manejo de errores de red

### 4. Sistema de Logs
- 📊 Registro de eventos clave
- ⏰ Timestamps automáticos
- 💾 Almacenamiento en archivo local
- 🔍 Filtrado por tipo de evento

## 🗂️ Modelo de Datos

### Entidad Pedido (Room)

```kotlin
@Entity(tableName = "pedidos")
data class PedidoEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val nombreCliente: String,
    val telefonoCliente: String,
    val direccionCliente: String,
    val emailCliente: String?,
    val descripcionPedido: String,
    val montoPedido: Double,
    val estadoPedido: String,
    val imagenPath: String?,
    val sincronizado: Boolean = false,
    val fechaCreacion: Long,
    val fechaModificacion: Long
)
```

### Request API (Retrofit)

```kotlin
data class PedidoRequest(
    val id: Int,
    val nombre_cliente: String,
    val telefono_cliente: String,
    val direccion_cliente: String,
    val email_cliente: String?,
    val descripcion_pedido: String,
    val monto_pedido: Double,
    val estado_pedido: String,
    val imagen_base64: String?,
    val fecha_creacion: Long,
    val fecha_modificacion: Long
)
```

## 📊 Estados de Pedido

| Estado | Color | Descripción |
|--------|-------|-------------|
| Pendiente | 🟡 Amarillo | Pedido recién creado |
| En Camino | 🔵 Azul | Pedido en proceso de entrega |
| Entregado | 🟢 Verde | Pedido completado |
| Cancelado | 🔴 Rojo | Pedido cancelado |

## 🧪 Pruebas

### Casos de Prueba Documentados

1. **Crear Pedido**
    - Abrir app → Presionar FAB → Llenar formulario → Guardar
    - ✅ Verificar que aparezca en la lista

2. **Capturar Imagen**
    - En formulario → Tomar Foto → Permitir permisos → Capturar
    - ✅ Verificar vista previa

3. **Sincronizar Pedido**
    - Lista de pedidos → Tocar icono de sincronización
    - ✅ Verificar que cambie el indicador

4. **Ver Logs**
    - Menú → Ver Logs
    - ✅ Verificar registro de eventos

## 📸 Capturas de Pantalla

_(Agregar capturas de pantalla aquí)_

## 🔐 Permisos Requeridos

- `CAMERA`: Para capturar fotos de pedidos
- `INTERNET`: Para sincronización con servidor
- `ACCESS_NETWORK_STATE`: Para verificar conectividad

## 🐛 Resolución de Problemas

### Error: "Camera permission denied"
**Solución**: Ir a Configuración → Apps → Delivery App → Permisos → Habilitar Cámara

### Error: "Network error"
**Solución**: Verificar URL del servidor en `RetrofitClient.kt` y conexión a internet

### Error: "Room database migration"
**Solución**: Desinstalar y reinstalar la app (en desarrollo)

## 📝 Logs del Sistema

Los logs se almacenan en:
```
/data/data/com.tuusuario.deliveryapp/files/delivery_logs.txt
```

Formato de log:
```
[2025-01-15 10:30:45] [INFO] Pedido creado localmente con ID: 5
[2025-01-15 10:31:02] [SYNC_EVENT] Tipo: INICIO | Pedido 5
[2025-01-15 10:31:05] [INFO] Pedido 5 sincronizado exitosamente
```

## 🤝 Contribuciones

Las contribuciones son bienvenidas. Por favor:

1. Fork el proyecto
2. Crear una rama (`git checkout -b feature/nueva-funcionalidad`)
3. Commit cambios (`git commit -m 'Agregar nueva funcionalidad'`)
4. Push a la rama (`git push origin feature/nueva-funcionalidad`)
5. Abrir Pull Request

## 📄 Licencia

Este proyecto está bajo la Licencia MIT. Ver archivo `LICENSE` para más detalles.

## 👨‍💻 Autor

**Tu Nombre**
- GitHub: [@TobyFranco](https://github.com/Tobyfranco)
- Email: tobyfranco@ehotmail.com

## 🙏 Agradecimientos

- Material Design Guidelines
- Android Developers Documentation
- Comunidad de Stack Overflow

---

**Versión**: 1.0.0  
**Última actualización**: Diciembre 2025