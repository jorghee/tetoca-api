-- Tabla: GZZ_ESTADO_EMPRESA
INSERT INTO GZZ_ESTADO_EMPRESA (EstEmpCod, EstEmpNom, EstEmpDes, EstEmpEstReg) VALUES
(1, 'Activo', 'La empresa está operando normalmente en la plataforma.', 'A'),
(2, 'Inactivo', 'La empresa ha sido desactivada temporalmente.', 'A'),
(3, 'Suspendido', 'La empresa ha sido suspendida por falta de pago u otra razón.', 'A')
ON CONFLICT (EstEmpCod) DO NOTHING;

-- Tabla: GZZ_CATEGORIA_EMPRESA
INSERT INTO GZZ_CATEGORIA_EMPRESA (CatEmpCod, CatEmpNom, CatEmpDes, CatEmpEstReg) VALUES
(1, 'Financiero', 'Bancos, cajas municipales, cooperativas de ahorro y crédito.', 'A'),
(2, 'Salud', 'Clínicas, hospitales, centros médicos.', 'A'),
(3, 'Gobierno', 'Entidades públicas, municipalidades, ministerios.', 'A'),
(4, 'Retail', 'Tiendas por departamento, supermercados.', 'A')
ON CONFLICT (CatEmpCod) DO NOTHING;

-- Tabla: GZZ_TIPO_DB
INSERT INTO GZZ_TIPO_DB (TipBdCod, TipBdNom, TipBdDes, TipBdEstReg) VALUES
(1, 'PostgreSQL', 'Base de datos relacional PostgreSQL.', 'A'),
(2, 'MySQL', 'Base de datos relacional MySQL.', 'A')
ON CONFLICT (TipBdCod) DO NOTHING;

-- Tabla: GZZ_ESTADO_CONEXION
INSERT INTO GZZ_ESTADO_CONEXION (EstConCod, EstConNom, EstConDes, EstConEstReg) VALUES
(1, 'Activo', 'La conexión a la base de datos del tenant está funcionando.', 'A'),
(2, 'Inactivo', 'La conexión está desactivada intencionalmente.', 'A'),
(3, 'Error de Conexión', 'No se puede establecer conexión con la base de datos.', 'A'),
(4, 'Mantenimiento', 'La base de datos del tenant está en mantenimiento.', 'A'),
(5, 'PROVISIONING_FAILED', 'Falló la creación inicial de la base de datos o esquema.', 'A')
ON CONFLICT (EstConCod) DO NOTHING;

-- Tabla: G1M_ADMIN_SAAS
INSERT INTO G1M_ADMIN_SAAS (AdmSaaCod, AdmSaaNom, AdmSaaCorEle, AdmSaaCla, AdmSaaFecReg, AdmSaaEstReg) VALUES
(1, 'Admin Principal Tetoca', 'jorghee@tetoca.com', '{clave_encriptada}', 20231028, 'A')
ON CONFLICT (AdmSaaCod) DO NOTHING;

-- Fin del script para la DB Global.
