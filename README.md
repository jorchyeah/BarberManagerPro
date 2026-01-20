# BarberManagerPro

![Kotlin](https://img.shields.io/badge/kotlin-%237F52FF.svg?style=for-the-badge&logo=kotlin&logoColor=white)
![Android](https://img.shields.io/badge/Android-3DDC84?style=for-the-badge&logo=android&logoColor=white)
![Firebase](https://img.shields.io/badge/firebase-%23039BE5.svg?style=for-the-badge&logo=firebase)
![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-4285F4?style=for-the-badge&logo=android&logoColor=white)

> **SaaS de gestión integral y fidelización de clientes para barberías.**

BarberManagerPro es una aplicación nativa de Android diseñada para digitalizar la operación de barberías locales. A diferencia de un CRM genérico, esta solución se enfoca en la **retención de clientes** mediante reglas de negocio automatizadas (cumpleaños, frecuencia de visitas) y está preparada para escalar a un modelo **Multi-tenant (SaaS)**.

---

## Screenshots

| Registro de Cliente | Listado de Clientes | Panel de Estadísticas |
|:-------------------:|:-------------------:|:---------------------:|
| ![Add Customer](docs/screenshots/add_customer.png) | ![List](docs/screenshots/list_placeholder.png) | ![Stats](docs/screenshots/stats_placeholder.png) |
| *Formulario con validación MVVM* | *Búsqueda en tiempo real* | *Métricas de retención* |

---

## Tech Stack & Arquitectura

Este proyecto sigue estrictamente los principios de **Clean Architecture** y **SOLID**, modularizado por funcionalidades ("Feature-First").

### Librerías Principales
* **Lenguaje:** [Kotlin]
* **UI:** [Jetpack Compose] (Material Design 3)
* **Arquitectura:** MVVM (Model-View-ViewModel) + Clean Architecture
* **Inyección de Dependencias:** [Dagger Hilt]
* **Concurrencia:** Coroutines & Flow
* **Backend (BaaS):** Firebase Firestore (NoSQL)
* **Testing:** JUnit4, Mockk, Turbine

### Diagrama de Arquitectura
El proyecto se divide en 3 capas claras para garantizar la escalabilidad y testabilidad:

```mermaid
graph TD
    UI["Presentation Layer<br>(Compose + ViewModel)"] --> Domain["Domain Layer<br>(Use Cases + Models)"]
    Data["Data Layer<br>(Repositories + Data Sources)"] --> Domain
    Data --> Remote[Firebase / API]
