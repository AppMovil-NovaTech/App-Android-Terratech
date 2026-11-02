# EPIC 09 · US24, US25 y US26 (Android nativo)

Esta entrega parte de los archivos `App-Android-Terratech-develop (2).zip` y
`BackEnd-TerraTech-develop (2).zip` compartidos por el equipo. No incorpora `.git`.

## Alcance

- **US24**: registrar stock usando un producto de catálogo o registrar un producto
  nuevo y después su inventario. `POST /api/v1/products`, `POST /api/v1/inventories`.
- **US25**: consultar inventario con búsqueda por producto/almacén, filtro por tipo
  y filtro disponibles/sin stock. `GET /api/v1/products`, `GET /api/v1/inventories`.
- **US26**: editar la cantidad total en stock. `PUT /api/v1/inventories/{id}`.

La app reutiliza Retrofit y el interceptor de autenticación existente, Hilt,
Compose y la navegación tipada. No modifica IAM, Room ni código de Monitoring.
No necesita nuevas migraciones para este módulo. Utiliza los contratos existentes
para stocks y catálogo del backend suministrado.

## Incorporación en el repositorio Git existente

1. En Android, crear una rama desde el `origin/develop` más reciente.
2. Comparar el contenido de esta entrega contra el nuevo `develop` antes de copiar,
   pues el equipo puede haber subido cambios desde el ZIP de referencia.
3. Incorporar los archivos modificados y nuevos del paquete, sin copiar `.git`
   ni reemplazar `local.properties`.
4. En Android Studio, configurar la URL del backend existente y compilar Debug.

   ```powershell
   .\gradlew.bat :app:assembleDebug -PTERRATECH_API_URL=http://127.0.0.1:5022/
   ```

   Si utilizas el emulador y backend local: `adb reverse tcp:5022 tcp:5022`.
   Tu backend debe tener sus migraciones nuevas aplicadas a MySQL mediante
   el procedimiento del equipo (haz un respaldo antes).

5. Iniciar sesión, abrir la pestaña **Insumos** y probar:
   - US24: registrar un producto nuevo y 10 unidades; verificar en Swagger/MySQL.
   - US25: buscarlo, filtrarlo por tipo y probar el filtro de disponibles.
   - US26: actualizar stock a 0; comprobar el filtro Sin stock y refrescar.
   - Reiniciar la app y comprobar que la consulta vuelve a cargar desde el backend.

## Limitaciones y requisitos de validación

- El inventario aplica el filtro de propietario por usuario en el backend actual;
  el catálogo de productos es compartido. No hay filtrado por parcela. No se inventaron endpoints.
- La actualización `PUT` modifica el total de existencias, no aplica un movimiento
  incremental ni cambia la ubicación: ambos comportamientos requerirían API adicional.
- Si se crea un producto nuevo y falla el `POST /inventories`, puede quedar el
  producto sin inventario. La pantalla lo informa y permite elegir ese producto
  existente para registrar el stock sin duplicarlo.
- **No se ha ejecutado un build Android real en este entorno**. Haz las pruebas
  de compilación y funcionamiento anteriores antes del PR.
- No publicar credenciales JWT, contraseñas, ni configuraciones locales.
