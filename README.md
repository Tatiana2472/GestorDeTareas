Gestor de Tareas - Práctica Programación I

Aplicación de escritorio en Java (NetBeans) que gestiona tareas usando Swing + JDBC + PostgreSQL. Incluye validaciones, eliminación lógica, deshacer , y capas separadas (Dominio, DAO, Servicio, UI).

 Base de datos
- Motor: PostgreSQL
- Archivo de creación 
- Tabla principal: `tarea` (id, titulo, prioridad, estado, especial, fecha, visible, creado_en, actualizado_en)

Conexión JDBC
- Driver: postgresql-42.x.x.jar
- URL JDBC: `jdbc:postgresql://localhost:5432/gestor_tareas`
- Usuario: `postgres` (ajustar)
- Contraseña: `tu_contraseña` (ajustar)
- Clase de conexión: `dao.ConexionBD` (método `getConexion()`)

## Estructura del proyecto
- `gestortareas` - Main
- `dominio` - Modelo Tarea
- `dao` - ConexionBD, TareaDAO
- `servicio` - TareaServicio
- `ui` - VentanaPrincipal
- `excepciones` - DatoInvalidoException

Requisitos / Validaciones
- Título no vacío.
- Prioridad entre 1 y 3.
- Fecha en formato `YYYY-MM-DD` (opcional) use jspinner
- Eliminación lógica (visible=false), con posibilidad de deshacer la última eliminación (stack limitado).

 Transacciones
- `eliminarLogico` y `restaurar` se ejecutan con transacción simple (commit/rollback).

 Paradigmas
- Imperativo: estructura secuencial en la capa DAO.
- OOP: modelo `Tarea`, encapsulamiento, modularidad por paquetes.
- Declarativo/Streams: se puede agregar un ejemplo con Streams en `TareaServicio` si se desea (p.ej. filtrar lista por prioridad).

Ejecución
1. Ejecutar `01_create_db_and_table.sql`.
2. Ajustar `dao/ConexionBD.java` (usuario/contraseña).
3. Agregar driver JDBC a las librerías.
4. Ejecutar `app.Main`.
   
Autor: Tatiana Urbina Arroliga
