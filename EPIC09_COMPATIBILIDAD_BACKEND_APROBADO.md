# EPIC 09 - Adaptacion Android al backend aprobado (Sprint 3)

- Backend: **sin modificaciones**. Fuente de contratos: ZIP `BackEnd-TerraTech-develop (3).zip`, `StockManagement/Interfaces/REST`.
- Android: registrar cosecha con perfil/parcela reales; una sola llamada `POST /api/v1/inventories` crea el producto comercial en el servidor.
- Consultar: `GET /api/v1/inventories`, lista `{id, productId, fieldId, profileId, name, quantity, unitType, averageWeightPerUnitKg, totalWeightKg, price, createdAt}`.
- Actualizar: `PUT /api/v1/inventories/{id}` con `{stockQuantity: N}`; responde InventoryResource con `quantity`.
- Validaciones del servidor al crear: name/UnitType/ImageUrl no vacios; fieldId/profileId/quantity > 0; averageWeightPerUnitKg/price > 0.
- `profileId` tomado de `ProfileState.profile.id`; `fieldId` tomado de `MonitoringState.fields` de ese perfil. No hay ids ficticios.
- El contrato nuevo describe **cosechas**, no fertilizantes/insumos: corroborar aceptacion de US24-US26 con el equipo.
- No se agregan migraciones Android ni MySQL; no se modifica codigo de Monitoring/Sprint 2.
- Ejecutar `:app:testDebugUnitTest` y `:app:assembleDebug`, luego verificaciones funcionales manuales de US24-US26 y Sprint 2 en Pixel 5.
- Si falla el `POST` con 400/500, comprobar perfil, parcela, URL de imagen y estado de migraciones de la base *de pruebas*. No tocar DB original desde el script.
- Los commits de EPIC 09 originales siguen en `develop`; esta adaptacion se realiza en rama aparte.
