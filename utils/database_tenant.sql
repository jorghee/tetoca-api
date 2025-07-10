-- Tabla: AZZ_TIPO_TRABAJADOR
INSERT INTO AZZ_TIPO_TRABAJADOR (TipTraCod, TipTraNom, TipTraDes, TipTraEstReg) VALUES
(1, 'Administrativo', 'Personal de gestión y administración.', 'A'),
(2, 'Atención al Cliente', 'Personal que opera las colas y atiende turnos.', 'A'),
(3, 'Gerencia', 'Personal de alta dirección.', 'A')
ON CONFLICT (TipTraCod) DO NOTHING;

-- Tabla: AZZ_ESTADO_COLA
INSERT INTO AZZ_ESTADO_COLA (EstColCod, EstColNom, EstColDes, EstColEstReg) VALUES
(1, 'ABIERTA', 'La cola está aceptando nuevos clientes.', 'A'),
(2, 'CERRADA', 'La cola no acepta nuevos clientes (ej. fuera de horario).', 'A'),
(3, 'PAUSADA', 'La cola está temporalmente detenida, no se llaman turnos.', 'A')
ON CONFLICT (EstColCod) DO NOTHING;

-- Tabla: AZZ_TIPO_COLA
INSERT INTO AZZ_TIPO_COLA (TipColCod, TipColNom, TipColDes, TipColEstReg) VALUES
(1, 'Atención General', 'Cola para trámites y consultas generales.', 'A'),
(2, 'Plataforma', 'Cola para operaciones de caja y plataforma.', 'A'),
(3, 'Preferencial', 'Cola de atención preferencial (adultos mayores, etc.).', 'A'),
(4, 'Créditos', 'Cola especializada en evaluación y desembolso de créditos.', 'A')
ON CONFLICT (TipColCod) DO NOTHING;

-- Tabla: AZZ_ESTADO_TURNO
INSERT INTO AZZ_ESTADO_TURNO (EstTurCod, EstTurNom, EstTurDes, EstTurEstReg) VALUES
(1, 'EN_ESPERA', 'El cliente ha tomado un turno y está esperando ser llamado.', 'A'),
(2, 'LLAMANDO', 'El turno está siendo llamado por un operario.', 'A'),
(3, 'ATENDIDO', 'El cliente ha sido atendido y el turno ha finalizado.', 'A'),
(4, 'CANCELADO', 'El cliente canceló su turno.', 'A'),
(5, 'AUSENTE', 'El cliente fue llamado pero no se presentó.', 'A')
ON CONFLICT (EstTurCod) DO NOTHING;

-- Crear una División de ejemplo.
INSERT INTO A1M_DIVISION (DivCod, DivNom, DivDes, DivEstReg) VALUES
(1, 'División Sur', 'Division que agrupa a todas las agencias de la zona sur del país.', 'A')
ON CONFLICT (DivCod) DO NOTHING;

-- Crear una Agencia de ejemplo en Arequipa, asociada a la División Sur.
INSERT INTO A1M_AGENCIA (AgeCod, AgeDivCod, AgeNom, AgeDir, AgeRef, AgeEstReg) VALUES
(1, 1, 'Agencia Mercaderes', 'Calle Mercaderes 410, Arequipa, Arequipa', 'Frente a la Plaza de Armas', 'A')
ON CONFLICT (AgeCod) DO NOTHING;

-- Cola 1: Plataforma (Atención General)
INSERT INTO A2M_COLA (ColCod, ColTipColCod, ColAgeCod, ColEstColCod, ColNom, ColCapMax, ColTieEst, ColEstReg) VALUES
(101, 2, 1, 1, 'Plataforma de Servicios', 50, 5, 'A')
ON CONFLICT (ColCod) DO NOTHING;

-- Cola 2: Créditos
INSERT INTO A2M_COLA (ColCod, ColTipColCod, ColAgeCod, ColEstColCod, ColNom, ColCapMax, ColTieEst, ColEstReg) VALUES
(102, 4, 1, 1, 'Asesoría de Créditos', 20, 15, 'A')
ON CONFLICT (ColCod) DO NOTHING;

-- Cola 3: Atención Preferencial
INSERT INTO A2M_COLA (ColCod, ColTipColCod, ColAgeCod, ColEstColCod, ColNom, ColCapMax, ColTieEst, ColEstReg) VALUES
(103, 3, 1, 1, 'Atención Preferencial', 15, 7, 'A')
ON CONFLICT (ColCod) DO NOTHING;

-- Crear otra agencia para tener más datos de prueba.
INSERT INTO A1M_AGENCIA (AgeCod, AgeDivCod, AgeNom, AgeDir, AgeRef, AgeEstReg) VALUES
(2, 1, 'Agencia Yanahuara', 'Av. Ejército 700, Yanahuara, Arequipa', 'Dentro del C.C. Lambramani', 'A')
ON CONFLICT (AgeCod) DO NOTHING;

INSERT INTO A2M_COLA (ColCod, ColTipColCod, ColAgeCod, ColEstColCod, ColNom, ColCapMax, ColTieEst, ColEstReg) VALUES
(201, 2, 2, 1, 'Plataforma y Caja', 40, 6, 'A')
ON CONFLICT (ColCod) DO NOTHING;

INSERT INTO A2M_COLA (ColCod, ColTipColCod, ColAgeCod, ColEstColCod, ColNom, ColCapMax, ColTieEst, ColEstReg) VALUES
(202, 4, 2, 1, 'Créditos Hipotecarios', 10, 20, 'A')
ON CONFLICT (ColCod) DO NOTHING;

-- Fin del script para la DB del Tenant.
