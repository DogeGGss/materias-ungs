# Aplicación de Gestión de Materias Universitarias - UNGS

Aplicación de escritorio desarrollada con Java Swing y Spring Boot para la gestión de materias universitarias.

## Características

- Interfaz gráfica con [FlatLaf](https://www.formdev.com/flatlaf/) (look & feel moderno, tarjetas con esquinas redondeadas, avatares circulares)
- Gestión de materias aprobadas por estudiante
- Visualización de materias disponibles para cursar (basado en correlativas)
- Base de datos H2 embebida (los datos se guardan localmente)

## Requisitos

- Java 21 o superior
- Maven 3.6 o superior

## Ejecución

### Opción 1: Ejecutar desde el IDE
1. Abre el proyecto en tu IDE favorito (IntelliJ IDEA, Eclipse, etc.)
2. Ejecuta la clase `DemoApplication.java`

### Opción 2: Ejecutar desde Maven
```bash
cd demo
mvn spring-boot:run
```

### Opción 3: Doble click en `Ejecutar.bat` (Windows)
La primera vez compila el proyecto (tarda un poco); las siguientes abre directo. No deja ninguna consola abierta de fondo — corre el `.jar` empaquetado con `javaw`, así que solo se ve la ventana de la app.

Si modificás el código (por ejemplo para agregar un usuario nuevo), corré `Recompilar.bat` antes de volver a abrir la app, para que tome los cambios.

## Uso

1. Al iniciar la aplicación, se mostrará una ventana de selección de perfil
2. Selecciona un usuario (admin, Pedro, Valen, o Kevin)
3. En el dashboard podrás:
   - **Materias Aprobadas**: Ver y eliminar materias aprobadas
   - **Materias Disponibles**: Ver materias disponibles para cursar según correlativas
   - **Actualizar Materias**: Marcar materias como aprobadas usando checkboxes

## Plan de estudios

El plan de materias (`PlanEstudiosLoader`) sigue el plan vigente de la **Licenciatura en Sistemas** (Res. CS Nº7016/18) y la **Tecnicatura Universitaria en Informática** (Res. CS Nº9794/25, 18/12/2025). Esta última resolución sacó **Taller de Lectura y Escritura en las Disciplinas (TLED)** e **Inglés Lectocomprensión III (INGL3)** de los requisitos de la Tecnicatura: hoy son materias exclusivas de la Licenciatura.

## Base de Datos

La aplicación usa H2 Database embebida. Los datos se guardan en:
- `demo/data/demo.mv.db`

Los datos persisten aunque cierres la aplicación.

## Agregar Nuevos Usuarios

Para agregar un nuevo usuario, edita el archivo `DemoApplication.java` y agrega una entrada en el Map `usuarios` con el siguiente formato:

```java
"NuevoUsuario", new Usuario("NuevoUsuario", "Nombre Completo", "/img/imagen.jpg", "ESTUDIANTE",
        List.of("Intereses del usuario"))
```

Las imágenes pueden colocarse en `demo/src/main/resources/static/img/` (aunque ya no se usan en la versión de escritorio, se mantiene la estructura por compatibilidad).
