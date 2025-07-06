# API de Autenticación - EstiloYa

## Descripción

Se ha implementado una API completa de autenticación para la aplicación EstiloYa con soporte para autenticación de dos factores (2FA), recuperación de contraseña y login con Google.

## Endpoints Implementados

### Autenticación Básica
- `POST /api/auth/register` - Registro de usuario
- `POST /api/auth/login` - Login tradicional
- `POST /api/auth/login-google` - Login con Google

### Autenticación de Dos Factores (2FA)
- `POST /api/auth/login/verify-2fa` - Verificar código 2FA
- `POST /api/auth/2fa/register-email` - Registrar correo alternativo
- `POST /api/auth/2fa/send-code` - Enviar código 2FA

### Recuperación de Contraseña
- `POST /api/auth/forgot-password` - Recuperar contraseña
- `POST /api/auth/reset-password` - Restablecer contraseña

## Configuración

### URL Base
La URL base está configurada en `ApiClient.kt`:
- **Desarrollo local**: `http://localhost:8080/api/`
- **Emulador Android**: `http://10.0.2.2:8080/api/`
- **Dispositivo físico**: `http://192.168.18.2:8080/api/` (ajustar IP según tu red)

### Cambiar URL Base
Para cambiar la URL base, edita el archivo `app/src/main/java/com/asparrin/carlos/estiloya/api/ApiClient.kt` y descomenta la línea correspondiente.

## Características Implementadas

### 1. Autenticación con API
- ✅ Login y registro conectados a la API
- ✅ Manejo de errores de red
- ✅ Indicadores de carga (ProgressBar)
- ✅ Validación de campos en el cliente

### 2. Autenticación de Dos Factores (2FA)
- ✅ Verificación de código 2FA
- ✅ Reenvío de códigos
- ✅ Configuración de email alternativo
- ✅ Navegación automática a 2FA cuando es requerido

### 3. Recuperación de Contraseña
- ✅ Solicitud de recuperación por email
- ✅ Validación de correo electrónico
- ✅ Interfaz dedicada para recuperación

### 4. Gestión de Sesión
- ✅ Almacenamiento seguro de tokens
- ✅ Interceptor automático para agregar token a peticiones
- ✅ Persistencia de datos de usuario

### 5. Login con Google
- ✅ Soporte para autenticación con Google
- ✅ Integración con 2FA para login con Google

## Estructura de Archivos

### Modelos de Datos
- `AuthModels.kt` - Modelos para requests y responses de autenticación

### API y Red
- `AuthService.kt` - Interface de Retrofit para endpoints de autenticación
- `AuthRepository.kt` - Repositorio que maneja las llamadas a la API
- `AuthInterceptor.kt` - Interceptor para agregar token automáticamente
- `ApiClient.kt` - Cliente HTTP configurado

### ViewModel
- `AuthViewModel.kt` - ViewModel con lógica de negocio y estados

### Actividades
- `LoginActivity.kt` - Login principal (actualizado para usar API)
- `RegisterActivity.kt` - Registro (actualizado para usar API)
- `TwoFactorActivity.kt` - Verificación 2FA
- `AlternativeEmailActivity.kt` - Configuración email alternativo
- `ForgotPasswordActivity.kt` - Recuperación de contraseña

### Utilidades
- `SessionManager.kt` - Gestión de sesión y preferencias (actualizado)

## Flujo de Autenticación

### Login Normal
1. Usuario ingresa credenciales
2. App envía petición a `/api/auth/login`
3. Si requiere 2FA → Navega a `TwoFactorActivity`
4. Si no requiere 2FA → Guarda token y navega a `HomeActivity`

### Login con 2FA
1. Usuario ingresa credenciales
2. App envía petición a `/api/auth/login`
3. API responde con `requires2FA: true`
4. App navega a `TwoFactorActivity`
5. Usuario ingresa código 2FA
6. App envía petición a `/api/auth/login/verify-2fa`
7. Si código es válido → Guarda token y navega a `HomeActivity`

### Registro
1. Usuario completa formulario de registro
2. App envía petición a `/api/auth/register`
3. Si exitoso → Guarda token y navega a `LoginActivity`

## Estados de Autenticación

El `AuthViewModel` maneja los siguientes estados:
- `Idle` - Estado inicial
- `Loading` - Cargando
- `Success` - Operación exitosa
- `Error` - Error en la operación
- `Requires2FA` - Requiere verificación 2FA
- `CodeSent` - Código 2FA enviado
- `AlternativeEmailRegistered` - Email alternativo registrado
- `PasswordResetEmailSent` - Email de recuperación enviado
- `PasswordResetSuccess` - Contraseña restablecida exitosamente

## Seguridad

### Tokens
- Los tokens se almacenan de forma segura en `SharedPreferences`
- Se agregan automáticamente a todas las peticiones HTTP
- Se limpian al cerrar sesión

### Validación
- Validación de correo institucional (@tecsup.edu.pe)
- Validación de longitud de contraseña (mínimo 6 caracteres)
- Validación de formato de email
- Validación de código 2FA (6 dígitos)

## Uso

### Para Desarrolladores

1. **Configurar URL Base**: Editar `ApiClient.kt` según el entorno
2. **Probar Endpoints**: Usar las actividades existentes
3. **Manejar Estados**: Observar `authState` en el ViewModel
4. **Gestionar Errores**: Observar `errorMessage` en el ViewModel

### Para Usuarios

1. **Registro**: Usar la actividad de registro existente
2. **Login**: Usar la actividad de login existente
3. **2FA**: Si está habilitado, seguir las instrucciones en pantalla
4. **Recuperar Contraseña**: Usar "¿Olvidaste tu contraseña?" en login

## Notas Importantes

- La interfaz de usuario no se modificó, solo se actualizó la lógica para usar la API
- Se mantiene compatibilidad con el código existente
- Los tokens se manejan automáticamente
- El 2FA es opcional y se activa según la configuración del usuario
- La recuperación de contraseña requiere configuración del servidor de email

## Próximos Pasos

1. Configurar el servidor backend con los endpoints correspondientes
2. Implementar el envío real de emails para 2FA y recuperación
3. Configurar Google Sign-In para login con Google
4. Agregar más validaciones de seguridad según sea necesario 