# Talleres Unidos

Talleres Unidos es una aplicación móvil desarrollada para coordinar la solicitud, envío y registro de la instalación de refacciones entre talleres mecánicos. La solución está diseñada para optimizar tiempos de respuesta y asegurar la trazabilidad de las piezas, facilitando la colaboración entre una red de talleres que comparten refacciones y piezas para la reparación de vehículos.

## Vista Previa de la Aplicación

A continuación, se muestran algunos flujos de la aplicación en funcionamiento:

### Solicitud de refacción
![Flujo de Instalación](flow1.gif)

### Flujo 2: Solicitud de Refacción entre talleres
![Flujo de Solicitud](flow2.gif)

## Objetivos del Proyecto

- **Registro de Instalación**: Permitir que el taller solicitante registre la instalación de la refacción, incluyendo:
    - Solicitud.
    - Selección de la pieza (verificando disponibilidad en inventario).
    - Información del taller (nombre y dirección).
    - Geolocalización del taller.
    - Fecha de instalación.
    - Estado de la instalación (ej. "instalado").
    - Fotografía de evidencia.

- **Solicitud de Refacciones entre Talleres**: En caso de no contar con la refacción en inventario, se debe poder realizar una solicitud a otros talleres. La solicitud incluye:
    - VIN del vehículo.
    - Pieza solicitada.
    - Registro del mecánico que realiza la solicitud.
    - Taller solicitante y su geolocalización.
    - Fecha de solicitud.
    - Estado de la solicitud.

## Principales Funcionalidades y Componentes

### Listado de Talleres
- **Pantalla Principal**: Se muestra un listado de talleres disponibles utilizando un `LazyColumn` para renderizar cada tarjeta (card) de información.
- **Información del Taller**: Cada ítem del listado incluye:
    - Nombre del taller con un ícono.
    - Número de refacciones en stock.
    - Dirección del taller.
    - Imagen representativa (logo) presentada en forma circular.

### Detalle del Taller
- **Pantalla de Información**: Al seleccionar un taller, se despliega una pantalla con información detallada:
    - Logo, nombre y dirección del taller.
    - Fecha actual y número de refacciones en stock.
    - Lista de refacciones disponibles (generada de forma dinámica).
    - Botón flotante para solicitar una refacción.

### Solicitud de Refacción
- **Pantalla de Solicitud**: Permite realizar la solicitud de refacción cuando no está disponible en el taller:
    - **Búsqueda y Autocompletado**: Campo de búsqueda que permite seleccionar refacciones de una lista sugerida.
    - **Solicitud entre Talleres**: En caso de no encontrar la pieza en el inventario local, se propone verificar la disponibilidad entre otros talleres mediante un diálogo de confirmación.
    - **Datos Requeridos**: Se solicitan datos como VIN del vehículo, nombre del mecánico y fecha de solicitud.
    - **Geolocalización**: Se integra Google Maps para mostrar la ubicación actual del taller solicitante.
    - **Captura de Evidencia**: Funcionalidad para tomar una foto (usando la cámara del dispositivo) que sirva como evidencia de la instalación o para la solicitud.
    - **Selección de Estado**: Menú desplegable para definir el estado de la instalación ("Instalado" o "No instalado").

### Integraciones y Tecnologías Utilizadas
- **Jetpack Compose**: Para construir interfaces de usuario modernas y responsivas.
- **Material3**: Componentes de Material Design adaptados a Compose.
- **Google Maps**: Para mostrar la ubicación geográfica del taller en la solicitud.
- **Fused Location Provider**: Para obtener la ubicación actual del dispositivo.
- **Coil**: Carga de imágenes de manera asíncrona.
- **Permisos y Cámara**: Implementación de permisos en tiempo real y uso de la cámara para capturar evidencia fotográfica.

## Estructura del Código

El proyecto se compone principalmente de tres actividades:

1. **MainActivity**:
    - Muestra el listado de talleres.
    - Configura la navegación hacia la pantalla de detalle de un taller al seleccionar un ítem.

2. **TallerInfoActivity**:
    - Presenta la información detallada del taller seleccionado.
    - Muestra la lista de refacciones disponibles y permite navegar a la solicitud de refacción.

3. **SolicitarRefaccionActivity**:
    - Permite realizar la solicitud de refacción en casos de no disponibilidad en el inventario local.
    - Integra búsqueda, autocompletado, captura de evidencia fotográfica, geolocalización y selección del estado de la instalación.
    - Maneja permisos de ubicación y cámara de forma dinámica.

## Conclusión

Talleres Unidos es una solución integral para la gestión de refacciones entre talleres, facilitando tanto el registro de instalaciones como la solicitud de piezas en situaciones de baja disponibilidad. Con un diseño moderno y el uso de tecnologías actuales (como Jetpack Compose y Material3), la aplicación mejora la eficiencia operativa y la trazabilidad en la reparación de vehículos.
