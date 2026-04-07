# EduPlazas

Plataforma SaaS para la gestión de admisiones universitarias en Madrid, desarrollada como proyecto académico para la asignatura ISST.

Simula el proceso de admisión universitaria basado en la EvAU, permitiendo a estudiantes solicitar plazas en grados universitarios y a las universidades gestionar su oferta académica.

## Tecnologías

**Backend:** Java 21 · Spring Boot · Spring Security · JPA/Hibernate · H2 (base de datos en memoria)

**Frontend:** React 19 · React Router · Axios · Vite

## Requisitos previos

- Java 21 o superior
- Maven
- Node.js y npm

## Instalación y arranque

### Backend

    cd backend
    mvn spring-boot:run

Disponible en `http://localhost:8080`

Consola H2 (base de datos): `http://localhost:8080/h2-console`
- JDBC URL: `jdbc:h2:mem:eduplazas`
- Usuario: `sa` · Contraseña: *(vacía)*

### Frontend

    cd frontend
    npm install
    npm run dev

Disponible en `http://localhost:5173`

## Funcionalidades implementadas

- Registro y login de estudiantes (validación contra EvAU) y universidades (validación de email institucional)
- Exploración de oferta de plazas por convocatoria
- Creación y consulta de solicitudes con preferencias ordenadas
- Publicación de ofertas por parte de las universidades
- Algoritmo de asignación de plazas por nota

## Estructura del proyecto

    EduPlazas/
    ├── backend/
    │   └── src/main/java/com/eduplazas/backend/
    │       ├── config/        # Seguridad y datos de ejemplo
    │       ├── controller/    # API REST
    │       ├── model/         # Entidades JPA
    │       ├── repository/    # Acceso a datos
    │       └── service/       # Lógica de negocio
    └── frontend/
        └── src/
            ├── pages/         # Vistas por rol (estudiante, universidad)
            └── services/      # Llamadas a la API

## Equipo

- Carmen Cervera Gómez
- Xiaolian Ferradas Fernández
- Laura Corrales Ávila
- Mauro Bartolomé de la Jara
- Luis de la Fuente Martínez

Proyecto desarrollado en equipo siguiendo metodología Scrum.
