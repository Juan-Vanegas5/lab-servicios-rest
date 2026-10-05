# Laboratorio 5 – Servicios REST · Muebles de los Alpes

Juan Andrés Vanegas · Arquitectura de Software · Universidad Piloto de Colombia

Se exponen como servicios REST (GET, POST, PUT, DELETE) los cinco EJB del paquete
`co.edu.uniandes.csw.mueblesdelosalpes.logica.ejb`:

| EJB | Servicio REST | Ruta base |
|---|---|---|
| `ServicioCatalogoMock` | `CatalogoService` | `/webresources/Catalogo` |
| `ServicioCarritoMock` | `CarroComprasService` | `/webresources/CarroCompras` |
| `ServicioRegistroMock` | `RegistroService` | `/webresources/Registro` |
| `ServicioSeguridadMock` | `SeguridadService` | `/webresources/Seguridad` |
| `ServicioVendedoresMock` | `VendedoresService` | `/webresources/Vendedores` |

URL base: `http://localhost:8080/mueblesdelosalpes.servicios/webresources`

## Entorno

- NetBeans 12.6
- GlassFish Server 4.1 (con JDK 8)
- Maven (los proyectos son Maven, NetBeans los abre directamente)

## Cómo ejecutar

1. En NetBeans: **File → Open Project** y abrir `mueblesdelosalpes.backend` y `mueblesdelosalpes.servicios`.
2. Clic derecho en `mueblesdelosalpes.backend` → **Clean and Build**.
3. Clic derecho en `mueblesdelosalpes.servicios` → **Clean and Build** y luego **Run**.
   Si NetBeans pregunta por el servidor, elegir **GlassFish Server 4.1**.
   Solo se despliega `servicios`; el backend viaja dentro del WAR.
4. Abrir en el navegador:
   `http://localhost:8080/mueblesdelosalpes.servicios/webresources/Catalogo/muebles`

El proyecto `mueblesdelosalpes.interfaz` es del código base del profesor y no se usa en este laboratorio.

## Endpoints

Todas las peticiones y respuestas son JSON (`Content-Type: application/json`).
Los errores responden `{"mensaje": "..."}` con el código HTTP correspondiente.

### Catálogo – `/Catalogo`

| Verbo | Ruta | Qué hace | EJB |
|---|---|---|---|
| GET | `muebles` | Lista todos los muebles | `darMuebles` |
| GET | `muebles/{id}` | Un mueble por referencia (404 si no existe) | `darMuebles` |
| POST | `agregar` | Crea un mueble (la referencia la asigna el sistema) | `agregarMueble` |
| PUT | `actualizar/{id}` | Modifica los datos de un mueble | `actualizarMueble` |
| PUT | `removerEjemplar/{id}` | Descuenta una unidad del inventario | `removerEjemplarMueble` |
| DELETE | `eliminar/{id}` | Elimina el mueble | `eliminarMueble` |

```json
POST /Catalogo/agregar
{"nombre": "Mesa ovalada estilo griego", "descripcion": "Mesa ovalada con un elegante estilo griego",
 "tipo": "Interior", "precio": 140000, "cantidad": 2, "imagen": "mesaOvalada"}
```

### Carrito de compras – `/CarroCompras`

| Verbo | Ruta | Qué hace | EJB |
|---|---|---|---|
| GET | `muebles` | Muebles que hay en el carrito | `getInventario` |
| GET | `resumen` | Muebles, total de unidades y precio total | `getTotalUnidades`, `getPrecioTotalInventario` |
| POST | `agregar` | Agrega una unidad de cada mueble de la lista | `agregarItem` |
| PUT | `comprar/{login}` | Compra el carrito a nombre del cliente: descuenta existencias, guarda el historial y vacía el carrito | `comprar` |
| DELETE | `borrar` | Quita una unidad de cada mueble de la lista | `removerItem` |
| DELETE | `limpiar` | Vacía el carrito | `limpiarLista` |

```json
POST /CarroCompras/agregar        DELETE /CarroCompras/borrar
[{"referencia": 1}, {"referencia": 4}]
```

Basta con la referencia: el nombre y el precio se toman del catálogo. Si se piden más
unidades de las que hay, responde 400 con la cantidad existente (regla del enunciado).

### Registro de clientes – `/Registro`

| Verbo | Ruta | Qué hace | EJB |
|---|---|---|---|
| GET | `clientes` | Lista los usuarios | `darClientes` |
| GET | `clientes/{login}` | Un usuario por login | `darClientes` |
| POST | `registrar` | Registra un cliente | `registrar` |
| PUT | `actualizar/{login}` | Modifica los datos del cliente (conserva sus compras) | `actualizarCliente` |
| DELETE | `eliminar/{login}` | Elimina el cliente; 409 si ya realizó compras | `eliminarCliente` |

```json
POST /Registro/registrar
{"login": "jvanegas", "contraseña": "clave123", "nombreCompleto": "Juan Vanegas",
 "tipoDocumento": "CC", "documento": 1012345678, "telefonoLocal": 6012345,
 "direccion": "Cra 9 # 45-12", "correo": "jv@correo.com", "profesion": "Ingeniero"}
```

Valores válidos: `tipoDocumento` = `CC` | `TarjetaIdentidad`; `profesion` = `Abogado`, `Arquitecto`,
`Administrador`, `Diseñador`, `Economista`, `Estudiante`, `Médico`, `Ingeniero`.

### Seguridad – `/Seguridad`

| Verbo | Ruta | Qué hace | EJB |
|---|---|---|---|
| POST | `ingresar` | Autentica al usuario; 401 si las credenciales no son válidas | `ingresar` |

```json
POST /Seguridad/ingresar
{"login": "client", "contrasena": "clientclient"}
```

El EJB de seguridad solo tiene la operación `ingresar`. Se expone con POST porque las
credenciales no deben viajar en la URL. Usuarios de prueba: `admin/adminadmin` y `client/clientclient`.

### Vendedores – `/Vendedores`

| Verbo | Ruta | Qué hace | EJB |
|---|---|---|---|
| GET | `vendedores` | Lista los vendedores | `getVendedores` |
| GET | `vendedores/{id}` | Un vendedor por identificación | `getVendedores` |
| POST | `agregar` | Crea un vendedor | `agregarVendedor` |
| PUT | `actualizar/{id}` | Modifica los datos del vendedor | `actualizarVendedor` |
| DELETE | `eliminar/{id}` | Elimina el vendedor | `eliminarVendedor` |

```json
POST /Vendedores/agregar
{"nombres": "Laura", "apellidos": "Gómez", "salario": 1500000, "comisionVentas": 120000,
 "perfil": "Asesora comercial", "foto": "vendedor1",
 "experiencia": [{"id": 5, "nombreEmpesa": "Hogar SAS", "cargo": "Vendedora",
                  "descripcion": "Ventas de muebles", "ano": 2021}]}
```

## Colección de Postman

`MueblesDeLosAlpes.postman_collection.json` trae todas las peticiones anteriores.
En Postman: **Import** → seleccionar el archivo.

## Cambios sobre el código base

**Backend (`mueblesdelosalpes.backend`)**

- `ServicioRegistroMock` y `ServicioSeguridadMock` no tenían `@Stateless`, y sus interfaces
  locales no tenían `@Local` (la remota de registro tampoco tenía `@Remote`). Sin eso no son EJB
  y no se pueden inyectar con `@EJB`.
- `ServicioVendedoresMock` inyectaba la persistencia con `@EJB`, pero `ServicioPersistenciaMock`
  no es un EJB y el despliegue fallaba. Ahora la crea en el constructor, igual que los demás servicios.
- `ServicioCarritoMock` pasó de `@Stateless` a `@Singleton`. Un bean sin estado no garantiza que dos
  peticiones seguidas usen la misma instancia, así que el carrito podía "perder" lo agregado.
- Para el PUT se agregaron `actualizarMueble`, `actualizarCliente` y `actualizarVendedor`. Los EJB
  originales no tenían cómo modificar, y el enunciado pide modificar muebles y datos del cliente.
- `registrar` inicializa el historial de compras del cliente nuevo (antes quedaba `null` y la compra fallaba).
- `eliminarCliente` rechaza clientes con compras, como pide el enunciado.
- Se quitó la dependencia a Jersey 1 y se compila para Java 8.

**Servicios (`mueblesdelosalpes.servicios`)**

- `ApplicationConfig` reemplaza el servlet de Jersey 1 del `web.xml`. GlassFish 4.1 ya trae
  JAX-RS 2.0 (Jersey 2) y los dos no conviven. Las URL no cambian (`/webresources/...`).
- El JSON se genera con Jackson (`JacksonConfig`). MOXy, el convertidor por defecto de GlassFish 4.1,
  tiene un error conocido en esa versión (`NoClassDefFoundError ... BeanValidationHelper`).
- Los servicios usan las interfaces locales de los EJB, porque los DTO no son `Serializable` y por la
  interfaz remota la llamada fallaría.
- `glassfish-web.xml` fija la ruta `/mueblesdelosalpes.servicios`.

## Problemas comunes

- **`EJB Container initialization error`** al desplegar: con GlassFish iniciado, ejecutar en
  `glassfish4/bin/asadmin`:
  `set server.ejb-container.property.disable-nonportable-jndi-names="true"` y volver a desplegar.
- **GlassFish no arranca**: GlassFish 4.1 solo funciona con JDK 8. En NetBeans:
  Tools → Servers → GlassFish → pestaña Java → Java Platform = JDK 1.8.
- Los datos viven en memoria (mock): al reiniciar el servidor vuelven a los iniciales.
