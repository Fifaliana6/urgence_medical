-- =====================================================================
-- Base de données : urgences_db
-- Plateforme de gestion des urgences médicales
-- NB : le module de gestion des stocks de médicaments est VOLONTAIREMENT
--      absent -> il doit vivre dans un projet indépendant.
-- =====================================================================

-- On utilise VARCHAR + CHECK plutôt que des types ENUM PostgreSQL natifs.
-- Raison : Hibernate (ddl-auto=validate) mappe @Enumerated(EnumType.STRING)
-- sur des colonnes VARCHAR classiques. Avec un vrai type ENUM Postgres,
-- la validation de schéma de Hibernate au démarrage échoue souvent
-- ("type mismatch"). VARCHAR + CHECK donne le même niveau de garantie
-- d'intégrité, sans ce piège classique pour un débutant Spring/JPA.

-- ---------------------------------------------------------------------
-- Module 1 : Patients
-- ---------------------------------------------------------------------
CREATE TABLE patient (
    id                          BIGSERIAL PRIMARY KEY,
    nom                         VARCHAR(100) NOT NULL,
    prenom                      VARCHAR(100) NOT NULL,
    date_naissance              DATE,
    sexe                        CHAR(1) CHECK (sexe IN ('M','F')),
    telephone                   VARCHAR(20),
    adresse                     VARCHAR(255),
    numero_securite_sociale     VARCHAR(50) UNIQUE
);

-- ---------------------------------------------------------------------
-- Module 7 : Ressources (médecins, salles, lits) -- SANS le stock
-- ---------------------------------------------------------------------
CREATE TABLE doctor (
    id          BIGSERIAL PRIMARY KEY,
    nom         VARCHAR(100) NOT NULL,
    prenom      VARCHAR(100) NOT NULL,
    specialite  VARCHAR(100),
    statut      VARCHAR(20) NOT NULL DEFAULT 'DISPONIBLE'
                CHECK (statut IN ('DISPONIBLE','OCCUPE','HORS_SERVICE')),
    telephone   VARCHAR(20)
);

CREATE TABLE room (
    id          BIGSERIAL PRIMARY KEY,
    nom         VARCHAR(50) NOT NULL,
    type        VARCHAR(20) NOT NULL
                CHECK (type IN ('DECHOCAGE','CONSULTATION','EXAMEN')),
    disponible  BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE bed (
    id       BIGSERIAL PRIMARY KEY,
    numero   VARCHAR(20) NOT NULL,
    service  VARCHAR(100) NOT NULL,   -- ex: CARDIOLOGIE, REANIMATION
    occupe   BOOLEAN NOT NULL DEFAULT FALSE
);

-- ---------------------------------------------------------------------
-- Module 1/2/5 : Passage aux urgences
-- ---------------------------------------------------------------------
CREATE TABLE emergency_visit (
    id              BIGSERIAL PRIMARY KEY,
    patient_id      BIGINT NOT NULL REFERENCES patient(id),
    date_arrivee    TIMESTAMP NOT NULL DEFAULT now(),
    symptomes       TEXT,
    niveau_urgence  VARCHAR(20) NOT NULL
                    CHECK (niveau_urgence IN ('CRITIQUE','ELEVEE','MOYENNE','FAIBLE')),
    statut          VARCHAR(30) NOT NULL DEFAULT 'EN_ATTENTE'
                    CHECK (statut IN ('EN_ATTENTE','EN_CONSULTATION','EN_EXAMEN',
                                       'DECIDE','DISCHARGED','HOSPITALIZED')),
    medecin_id      BIGINT REFERENCES doctor(id),
    salle_id        BIGINT REFERENCES room(id),
    lit_id          BIGINT REFERENCES bed(id),
    date_sortie     TIMESTAMP
);

-- ---------------------------------------------------------------------
-- Module 3 : Consultation / diagnostic / prescriptions / examens
-- ---------------------------------------------------------------------
CREATE TABLE consultation (
    id                 BIGSERIAL PRIMARY KEY,
    visite_id          BIGINT NOT NULL REFERENCES emergency_visit(id),
    medecin_id         BIGINT NOT NULL REFERENCES doctor(id),
    date_consultation  TIMESTAMP NOT NULL DEFAULT now(),
    diagnostic         TEXT,
    plan_traitement    TEXT
);

CREATE TABLE prescription (
    id               BIGSERIAL PRIMARY KEY,
    consultation_id  BIGINT NOT NULL REFERENCES consultation(id),
    medicament       VARCHAR(150) NOT NULL,
    dosage           VARCHAR(100),
    duree            VARCHAR(50),
    instructions     TEXT
);

-- ---------------------------------------------------------------------
-- Module 4 : Examens et résultats
-- ---------------------------------------------------------------------
CREATE TABLE exam (
    id               BIGSERIAL PRIMARY KEY,
    consultation_id  BIGINT NOT NULL REFERENCES consultation(id),
    type             VARCHAR(150) NOT NULL,     -- ex: "Radio thorax", "NFS"
    statut           VARCHAR(30) NOT NULL DEFAULT 'PRESCRIT'
                     CHECK (statut IN ('PRESCRIT','EN_ATTENTE','REALISE','RESULTAT_DISPONIBLE')),
    date_demande     TIMESTAMP DEFAULT now(),
    date_resultat    TIMESTAMP,
    resultat         TEXT
);

-- ---------------------------------------------------------------------
-- Module 6 : Facturation
-- ---------------------------------------------------------------------
CREATE TABLE invoice (
    id              BIGSERIAL PRIMARY KEY,
    visite_id       BIGINT NOT NULL UNIQUE REFERENCES emergency_visit(id),
    date_emission   TIMESTAMP NOT NULL DEFAULT now(),
    montant_total   NUMERIC(10,2) NOT NULL DEFAULT 0,
    statut          VARCHAR(20) NOT NULL DEFAULT 'EN_ATTENTE'
                    CHECK (statut IN ('EN_ATTENTE','PAYEE','IMPAYEE'))
);

CREATE TABLE invoice_item (
    id             BIGSERIAL PRIMARY KEY,
    invoice_id     BIGINT NOT NULL REFERENCES invoice(id),
    description    VARCHAR(255) NOT NULL,
    quantite       INTEGER NOT NULL DEFAULT 1,
    prix_unitaire  NUMERIC(10,2) NOT NULL,
    montant        NUMERIC(10,2) NOT NULL,
    type           VARCHAR(30) NOT NULL   -- CONSULTATION, EXAMEN, MEDICAMENT
);

-- ---------------------------------------------------------------------
-- Module 8 : Sécurité / audit / utilisateurs API
-- ---------------------------------------------------------------------
CREATE TABLE audit_log (
    id             BIGSERIAL PRIMARY KEY,
    utilisateur    VARCHAR(100),
    action         VARCHAR(100),
    entite         VARCHAR(100),
    entite_id      BIGINT,
    date_heure     TIMESTAMP NOT NULL DEFAULT now(),
    details        TEXT
);

CREATE TABLE app_user (
    id             BIGSERIAL PRIMARY KEY,
    username       VARCHAR(50) NOT NULL UNIQUE,
    password_hash  VARCHAR(255) NOT NULL,
    role           VARCHAR(20) NOT NULL
                   CHECK (role IN ('RECEPTIONNISTE','MEDECIN','ADMIN','LABO')),
    nom            VARCHAR(100),
    prenom         VARCHAR(100)
);

-- ---------------------------------------------------------------------
-- Index utiles (la file d'attente est triée par urgence + heure d'arrivée)
-- ---------------------------------------------------------------------
CREATE INDEX idx_visit_statut_urgence ON emergency_visit (statut, niveau_urgence, date_arrivee);
CREATE INDEX idx_doctor_statut        ON doctor (statut);
CREATE INDEX idx_bed_service_occupe   ON bed (service, occupe);
CREATE INDEX idx_room_type_dispo      ON room (type, disponible);

-- =====================================================================
-- Données de test (facultatif mais recommandé pour tester le JMS tout
-- de suite : sans médecin DISPONIBLE, le consumer n'assignera personne)
-- =====================================================================
INSERT INTO doctor (nom, prenom, specialite, statut, telephone) VALUES
    ('Martin', 'Claire', 'Médecine d''urgence', 'DISPONIBLE', '0601020304'),
    ('Dubois', 'Karim',  'Cardiologie',         'DISPONIBLE', '0605060708');

INSERT INTO room (nom, type, disponible) VALUES
    ('Salle de déchocage 1', 'DECHOCAGE', TRUE),
    ('Salle de consultation 1', 'CONSULTATION', TRUE);

INSERT INTO bed (numero, service, occupe) VALUES
    ('R-101', 'REANIMATION', FALSE),
    ('C-204', 'CARDIOLOGIE', FALSE);

INSERT INTO patient (nom, prenom, date_naissance, sexe, telephone) VALUES
    ('Rakoto', 'Hery', '1990-04-12', 'M', '0341234567');

-- =====================================================================
-- Quelques requêtes utiles (exemples, à tester dans psql / DBeaver)
-- =====================================================================

-- File d'attente triée par urgence (CRITIQUE d'abord), puis heure d'arrivée
-- SELECT ev.id, p.nom, p.prenom, ev.niveau_urgence, ev.date_arrivee, ev.statut
-- FROM emergency_visit ev
-- JOIN patient p ON p.id = ev.patient_id
-- WHERE ev.statut = 'EN_ATTENTE'
-- ORDER BY
--   CASE ev.niveau_urgence
--     WHEN 'CRITIQUE' THEN 1
--     WHEN 'ELEVEE'   THEN 2
--     WHEN 'MOYENNE'  THEN 3
--     ELSE 4
--   END,
--   ev.date_arrivee;

-- Dossier complet d'une visite (consultation + prescriptions + examens)
-- SELECT c.diagnostic, c.plan_traitement, pr.medicament, ex.type, ex.statut
-- FROM consultation c
-- LEFT JOIN prescription pr ON pr.consultation_id = c.id
-- LEFT JOIN exam ex ON ex.consultation_id = c.id
-- WHERE c.visite_id = 1;

-- Chiffre d'affaires par mois
-- SELECT date_trunc('month', date_emission) AS mois, SUM(montant_total) AS total
-- FROM invoice
-- WHERE statut = 'PAYEE'
-- GROUP BY 1 ORDER BY 1;