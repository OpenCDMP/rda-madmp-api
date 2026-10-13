\connect -reuse-previous=on "dbname='madmp'"
--
-- PostgreSQL database dump
--

-- Dumped from database version 16.2
-- Dumped by pg_dump version 17.0

-- Started on 2025-02-26 11:49:02

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET transaction_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- TOC entry 2 (class 3079 OID 283746)
-- Name: uuid-ossp; Type: EXTENSION; Schema: -; Owner: -
--

CREATE EXTENSION IF NOT EXISTS "uuid-ossp" WITH SCHEMA public;


--
-- TOC entry 4085 (class 0 OID 0)
-- Dependencies: 2
-- Name: EXTENSION "uuid-ossp"; Type: COMMENT; Schema: -; Owner: -
--

COMMENT ON EXTENSION "uuid-ossp" IS 'generate universally unique identifiers (UUIDs)';


--
-- TOC entry 217 (class 1259 OID 124012)
-- Name: DBVersion; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public."DBVersion" (
    key character varying NOT NULL,
    version character varying NOT NULL,
    "releasedAt" timestamp with time zone NOT NULL,
    "deployedAt" timestamp with time zone,
    description text
);


--
-- TOC entry 218 (class 1259 OID 124083)
-- Name: Description; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE IF NOT EXISTS public."madmp_User"
(
    id uuid NOT NULL,
    name character varying(250) COLLATE pg_catalog."default",
    additional_info character varying COLLATE pg_catalog."default",
    created_at timestamp without time zone NOT NULL DEFAULT now(),
    updated_at timestamp without time zone NOT NULL DEFAULT now(),
    is_active smallint NOT NULL DEFAULT 1,
    CONSTRAINT "madmp_User_pkey" PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS public."madmp_UserCredential"
(
    id uuid NOT NULL,
    "user" uuid NOT NULL,
    external_id character varying(512) COLLATE pg_catalog."default" NOT NULL,
    created_at timestamp without time zone NOT NULL,
    updated_at timestamp without time zone NOT NULL,
    data character varying COLLATE pg_catalog."default",
    CONSTRAINT "madmp_UserCredential_pkey" PRIMARY KEY (id),
    CONSTRAINT "madmp_UserCredential_user_fkey" FOREIGN KEY ("user")
        REFERENCES public."madmp_User" (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
);

CREATE TABLE IF NOT EXISTS public."madmp_Tenant"
(
    id uuid NOT NULL,
    code character varying(200) COLLATE pg_catalog."default" NOT NULL,
    created_at timestamp without time zone NOT NULL DEFAULT now(),
    updated_at timestamp without time zone NOT NULL DEFAULT now(),
    is_active smallint NOT NULL DEFAULT 1,
    CONSTRAINT "madmp_Tenant_pkey" PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS public."madmp_TenantUser"
(
    id uuid NOT NULL,
    "user" uuid NOT NULL,
    tenant uuid NOT NULL,
    is_active smallint NOT NULL,
    created_at timestamp without time zone NOT NULL,
    updated_at timestamp without time zone NOT NULL,
    CONSTRAINT "madmp_TenantUser_pkey" PRIMARY KEY (id),
    CONSTRAINT "madmp_TenantUser_tenant_fkey" FOREIGN KEY (tenant)
        REFERENCES public."madmp_Tenant" (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION,
    CONSTRAINT "madmp_TenantUser_user_fkey" FOREIGN KEY ("user")
        REFERENCES public."madmp_User" (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
);

CREATE TABLE IF NOT EXISTS public."madmp_QueueInbox"
(
    id uuid NOT NULL,
    queue character varying(200) COLLATE pg_catalog."default" NOT NULL,
    exchange character varying(200) COLLATE pg_catalog."default" NOT NULL,
    route character varying(200) COLLATE pg_catalog."default" NOT NULL,
    application_id character varying(100) COLLATE pg_catalog."default" NOT NULL,
    message_id uuid NOT NULL,
    message json NOT NULL,
    retry_count integer,
    status smallint NOT NULL,
    created_at timestamp without time zone NOT NULL,
    updated_at timestamp without time zone NOT NULL,
    tenant uuid,
    is_active smallint NOT NULL,
    CONSTRAINT "madmp_QueryInbox_pkey" PRIMARY KEY (id),
    CONSTRAINT "madmp_QueryInbox_tenant_fkey" FOREIGN KEY (tenant)
        REFERENCES public."madmp_Tenant" (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
);

CREATE TABLE IF NOT EXISTS public."madmp_QueueOutbox"
(
    id uuid NOT NULL,
    exchange character varying(200) COLLATE pg_catalog."default" NOT NULL,
    route character varying(200) COLLATE pg_catalog."default" NOT NULL,
    message_id uuid NOT NULL,
    notify_status smallint NOT NULL,
    retry_count integer NOT NULL,
    published_at timestamp without time zone,
    confirmed_at timestamp without time zone,
    tenant uuid,
    created_at timestamp without time zone NOT NULL,
    updated_at timestamp without time zone NOT NULL,
    message text COLLATE pg_catalog."default" NOT NULL,
    is_active smallint NOT NULL,
    CONSTRAINT "madmp_QueueOutbox_pkey" PRIMARY KEY (id)
);



