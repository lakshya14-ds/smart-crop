-- ============================================================
-- Smart Crop Advisory System — Supabase Schema
-- Run this in: Supabase Dashboard → SQL Editor → New Query
-- ============================================================

-- 1. SOIL DATA TABLE
create table if not exists soil_data (
    id             bigserial    primary key,
    user_email     text         not null,
    soil_type      text         not null default 'loamy',
    ph             numeric(4,2) not null default 7.0,
    nitrogen       numeric(6,2) not null default 0,
    phosphorus     numeric(6,2) not null default 0,
    potassium      numeric(6,2) not null default 0,
    organic_matter numeric(5,2) not null default 0,
    moisture       numeric(5,2) not null default 0,
    created_at     timestamptz  not null default now()
);
create index if not exists idx_soil_data_user_email on soil_data(user_email);

-- ============================================================
-- 2. CROPS TABLE
--    Master catalogue of crops + their growing requirements.
--    The app reads this table to show recommendations.
--    Pre-seeded with 6 common crops — add more rows as needed.
-- ============================================================
create table if not exists crops (
    id                bigserial    primary key,
    name              text         not null unique,
    suitability       text         not null default 'good',   -- excellent | good | fair
    expected_yield    text         not null default '',        -- e.g. "4-5 tons/ha"
    growth_period     text         not null default '',        -- e.g. "120-150 days"
    water_requirement text         not null default 'Moderate',
    -- Soil preference ranges (used for smart matching)
    min_ph            numeric(4,2) not null default 5.5,
    max_ph            numeric(4,2) not null default 7.5,
    min_nitrogen      numeric(6,2) not null default 20,
    min_phosphorus    numeric(6,2) not null default 15,
    min_potassium     numeric(6,2) not null default 20,
    -- Reasons shown in the UI (pipe-separated so no JSON needed)
    reasons           text         not null default '',
    created_at        timestamptz  not null default now()
);

-- Seed the crops table with default data
insert into crops (name, suitability, expected_yield, growth_period, water_requirement,
                   min_ph, max_ph, min_nitrogen, min_phosphorus, min_potassium, reasons)
values
(
    'Rice', 'excellent', '4-5 tons/ha', '120-150 days', 'High',
    6.0, 7.0, 40, 20, 30,
    'Loamy soil provides ideal drainage and nutrient retention|Current pH level is optimal for rice cultivation|Adequate moisture and favorable temperature conditions|NPK levels support strong vegetative growth'
),
(
    'Wheat', 'good', '3-4 tons/ha', '110-130 days', 'Moderate',
    6.0, 7.5, 30, 20, 25,
    'Soil nutrients support good grain development|Temperature range suitable for winter wheat|Moderate water requirement matches current conditions'
),
(
    'Maize', 'good', '5-6 tons/ha', '90-120 days', 'Moderate to High',
    5.8, 7.0, 40, 25, 30,
    'High nitrogen levels promote vegetative growth|Loamy soil provides good root development|Current weather conditions favor maize cultivation'
),
(
    'Soybean', 'good', '2-3 tons/ha', '90-120 days', 'Moderate',
    6.0, 7.0, 20, 40, 30,
    'Fixes atmospheric nitrogen, improving soil health|Thrives in well-drained loamy soils|Good rotation crop after cereals'
),
(
    'Sugarcane', 'fair', '60-80 tons/ha', '300-365 days', 'Very High',
    6.0, 7.5, 50, 30, 50,
    'Requires high potassium for sugar synthesis|Long growing season needs consistent irrigation|Deep fertile soils give best yields'
),
(
    'Cotton', 'fair', '1.5-2.5 tons/ha', '150-180 days', 'Moderate',
    5.8, 8.0, 30, 20, 35,
    'Tolerates slightly alkaline soils well|Requires well-drained deep soils|Warm temperatures during boll formation are critical'
)
on conflict (name) do nothing;

-- ============================================================
-- 3. REPORTS TABLE
-- ============================================================
create table if not exists reports (
    id         bigserial   primary key,
    user_email text        not null,
    title      text        not null,
    type       text        not null default 'Comprehensive',
    content    text,
    created_at timestamptz not null default now()
);
create index if not exists idx_reports_user_email on reports(user_email);

-- ============================================================
-- GRANT anon access (required for publishable/anon API key)
-- ============================================================
grant select, insert, update, delete on soil_data to anon;
grant select, insert, update, delete on reports   to anon;
grant select                          on crops     to anon;   -- crops is read-only from app
grant usage, select on sequence soil_data_id_seq  to anon;
grant usage, select on sequence reports_id_seq    to anon;
grant usage, select on sequence crops_id_seq      to anon;

-- ============================================================
-- ROW LEVEL SECURITY (enable for production)
-- Uncomment once auth.users is configured.
-- ============================================================
-- alter table soil_data enable row level security;
-- alter table reports    enable row level security;
-- alter table crops      enable row level security;

-- create policy "own soil data"  on soil_data using (user_email = auth.jwt() ->> 'email');
-- create policy "own reports"    on reports   using (user_email = auth.jwt() ->> 'email');
-- create policy "crops readonly" on crops     for select using (true);
