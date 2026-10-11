# BanKL - Sistema Bancario Web

> Proyecto academico de Base de Datos I
> Fundacion Universitaria Konrad Lorenz - 2026

---

## Descripcion

BanKL es una aplicacion web bancaria completa que permite la gestion de clientes, tarjetas, prestamos, inversiones y transferencias. Fue desarrollada con una arquitectura de tres capas: base de datos relacional MySQL, backend REST con Spring Boot y frontend en HTML/CSS/JavaScript.

---

## Autor

Jorge Alejandro Lopez Paez 

---

## Arquitectura

```
Frontend (HTML + CSS + JS)
        |
Controller (Spring Boot REST API)
        |
Service (Logica de negocio)
        |
Repository (JPA / Hibernate)
        |
Base de Datos (MySQL 8)
```

---

## Tecnologias

| Capa | Tecnologia | Version |
|---|---|---|
| Base de datos | MySQL | 8.0 |
| ORM | JPA / Hibernate | 6.5 |
| Backend | Spring Boot | 3.5.16 |
| Lenguaje | Java | 25 |
| Frontend | HTML5 + CSS3 + JavaScript | — |

---

## Estructura del Proyecto

```
bankl/
├── src/
│   ├── main/
│   │   ├── java/co/edu/konradlorenz/
│   │   │   ├── BanklApplication.java
│   │   │   ├── model/
│   │   │   │   ├── Cliente.java
│   │   │   │   ├── ClienteNatural.java
│   │   │   │   ├── ClienteAdmin.java
│   │   │   │   ├── Cuenta.java
│   │   │   │   ├── Prestamo.java
│   │   │   │   ├── Inversion.java
│   │   │   │   ├── Transferencia.java
│   │   │   │   ├── Beneficiario.java
│   │   │   │   └── Notificacion.java
│   │   │   ├── repository/
│   │   │   ├── service/
│   │   │   └── controller/
│   │   └── resources/
│   │       ├── application.properties
│   │       └── static/
│   │           ├── html/
│   │           ├── css/
│   │           └── js/
└── pom.xml
```

---

## Base de Datos

El sistema cuenta con 9 tablas normalizadas hasta 3FN con estrategia de herencia JOINED:

| Tabla | Descripcion |
|---|---|
| `cliente` | Tabla padre con datos comunes de todos los usuarios |
| `cliente_natural` | Subtipo: usuarios del banco |
| `cliente_admin` | Subtipo: administradores del sistema |
| `cuenta` | Tarjetas debito y credito de cada cliente |
| `prestamo` | Prestamos solicitados por los clientes |
| `inversion` | CDT y ahorros programados |
| `transferencia` | Historial de transferencias entre cuentas |
| `beneficiario` | Contactos de transferencia guardados |
| `notificacion` | Alertas automaticas del sistema |

---

## Configuracion

### Requisitos previos
- Java 25 (LTS)
- MySQL 8
- Maven

### application.properties
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/bankl?useSSL=false&serverTimezone=UTC
spring.datasource.username=root
spring.datasource.password=TU_PASSWORD
spring.jpa.hibernate.ddl-auto=update
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQLDialect
```

### Crear la base de datos en MySQL
```sql
CREATE DATABASE bankl;
USE bankl;
```

### Crear la cuenta de administrador de prueba

Con la base de datos `bankl` y las tablas de la aplicación ya creadas, ejecuta desde la raíz del repositorio:

```bash
mysql -u root -p bankl < bankl/database/seed_admin.sql
```

La cuenta de demostración queda con estos datos:

| Usuario | Contraseña | PIN | Rol |
|---|---|---:|---|
| `admin` | `Admin123` | `1234` | Administrador |

Si despliegas en Render, el proyecto también incluye un inicializador controlado por `ADMIN_SEED_ENABLED`. Después de desplegar estos cambios, agrega `ADMIN_SEED_ENABLED=true` en **Render → servicio BanKL → Environment** y espera a que la aplicación arranque conectada a Aiven. El inicializador crea la cuenta únicamente si `admin` no existe; no cambia una cuenta ya creada. Cuando aparezca en los logs `README demo administrator account created`, elimina la variable o configúrala como `false` y vuelve a desplegar.
El script es idempotente para poder volver a ejecutarlo sin duplicar la cuenta. La contraseña de demostración se guarda inicialmente como texto y el sistema la transforma a PBKDF2 cuando se inicia sesión por primera vez. Usa estas credenciales solo para desarrollo local y cámbialas antes de cualquier uso real.

El panel está disponible en `/html/admin.html`. Permite buscar clientes por nombre, usuario, correo o teléfono y editar sus datos. Tanto la página como `/api/admin/**` requieren una sesión con rol `ClienteAdmin`.
---

## Como correr el proyecto

```bash
# 1. Clonar el repositorio
git clone https://github.com/Alejandro-LP/BanKL.git

# 2. Entrar al proyecto
cd bankl

# 3. Correr con Maven
./mvnw spring-boot:run
```

O desde VS Code: abrir `BanklApplication.java` y presionar Run.

El servidor inicia en: `http://localhost:8080`

---

## Paginas de la Aplicacion

| URL | Descripcion | Acceso |
|---|---|---|
| `/html/Login.html` | Inicio de sesion | Todos |
| `/html/registro.html` | Registro de nuevo cliente | Publico |
| `/html/dashboard.html` | Inicio y resumen financiero del cliente | ClienteNatural |
| `/html/dashboard.html?seccion=tarjetas` | Consulta y gestion de tarjetas | ClienteNatural |
| `/html/dashboard.html?seccion=transferencias` | Nueva transferencia e historial | ClienteNatural |
| `/html/dashboard.html?seccion=beneficiarios` | Gestion de beneficiarios | ClienteNatural |
| `/html/dashboard.html?seccion=prestamos` | Solicitud y seguimiento de prestamos | ClienteNatural |
| `/html/dashboard.html?seccion=inversiones` | Creacion y seguimiento de inversiones | ClienteNatural |
| `/html/dashboard.html?seccion=notificaciones` | Avisos y actividad de la cuenta | ClienteNatural |
| `/html/admin.html` | Panel de administracion | ClienteAdmin |

---

El dashboard del cliente mantiene una URL por apartado mediante el parametro `seccion`. Por ejemplo, ``/html/dashboard.html?seccion=tarjetas``. La navegacion se adapta al escritorio y al movil, y el historial del navegador permite regresar a la vista anterior.

## API REST - Endpoints

### Clientes
| Metodo | Endpoint | Descripcion |
|---|---|---|
| POST | `/clientes/registro` | Registrar nuevo cliente natural |
| POST | `/clientes/login` | Iniciar sesion |
| GET | `/clientes/cuentas?usuario=` | Obtener cuentas del cliente |

### Cuentas
| Metodo | Endpoint | Descripcion |
|---|---|---|
| POST | `/cuentas/consignar` | Consignar dinero |
| POST | `/cuentas/retirar` | Retirar dinero |
| POST | `/cuentas/cambiar` | Regenerar datos de tarjeta |

### Prestamos
| Metodo | Endpoint | Descripcion |
|---|---|---|
| POST | `/prestamos/solicitar` | Solicitar un prestamo |
| GET | `/prestamos?usuario=` | Listar prestamos del cliente |
| POST | `/prestamos/pagar/{id}` | Pagar una cuota |

### Inversiones
| Metodo | Endpoint | Descripcion |
|---|---|---|
| POST | `/inversiones` | Crear una inversion |
| GET | `/inversiones?usuario=` | Listar inversiones del cliente |
| POST | `/inversiones/retirar/{id}` | Retirar una inversion |

### Transferencias
| Metodo | Endpoint | Descripcion |
|---|---|---|
| POST | `/transferencias` | Realizar una transferencia |
| GET | `/transferencias?usuario=` | Ver historial de transferencias |

### Beneficiarios
| Metodo | Endpoint | Descripcion |
|---|---|---|
| POST | `/beneficiarios` | Agregar beneficiario |
| GET | `/beneficiarios?usuario=` | Listar beneficiarios |
| DELETE | `/beneficiarios/{id}` | Eliminar beneficiario |

### Notificaciones
| Metodo | Endpoint | Descripcion |
|---|---|---|
| GET | `/notificaciones?usuario=` | Ver notificaciones |
| POST | `/notificaciones/leer/{id}` | Marcar como leida |

### Administrador
| Metodo | Endpoint | Descripcion |
|---|---|---|
| GET | `/api/admin/clientes` | Listar clientes naturales |
| PUT | `/api/admin/clientes/{id}` | Editar datos de un cliente |

---

## Funcionalidades

- Registro con validacion completa y PIN automatico
- Login con usuario, contrasena y PIN
- Tarjeta debito y credito generadas automaticamente al registrarse
- Consignaciones y retiros con validacion de fondos
- Prestamos con calculo de cuota por interes compuesto
- Inversiones CDT y ahorro programado con rendimiento calculado
- Transferencias con acreditacion en tiempo real a la cuenta destino
- Gestion de beneficiarios
- Notificaciones automaticas por cada operacion
- Panel de administrador con edicion de clientes
- Dashboard organizado en vistas independientes para inicio, tarjetas, transferencias, beneficiarios, prestamos, inversiones y notificaciones
- Navegacion adaptable: barra lateral en escritorio, menu inferior en movil y enlaces directos por seccion
- Identidad visual BanKL con logotipo vectorial compartido y tarjetas en tonos verde bosque, marfil y lima
- Modo oscuro persistente en el dashboard

---

## Credenciales de prueba

| Usuario | Contrasena | PIN | Rol |
|---|---|---|---|
| admin | Admin123 | 1234 | Administrador |
| (registro propio) | (definida al registrarse) | (se muestra al registrarse) | Cliente Natural |

La contrasena debe tener minimo 8 caracteres, una mayuscula y un numero.

---

## Licencia

Proyecto academico — Fundacion Universitaria Konrad Lorenz, Base de Datos I, 2026.
