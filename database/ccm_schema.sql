--
-- PostgreSQL database dump
--

-- Dumped from database version 12.8
-- Dumped by pg_dump version 14.2

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
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
-- Name: hour_of_action_event; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.hour_of_action_event (
    id text NOT NULL,
    event_start timestamp with time zone NOT NULL,
    event_name text NOT NULL,
    secret text NOT NULL,
    synced boolean NOT NULL DEFAULT FALSE
);


--
-- Name: hour_of_action_event hour_of_action_event_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.hour_of_action_event
    ADD CONSTRAINT hour_of_action_event_pkey PRIMARY KEY (id);


--
-- PostgreSQL database dump complete
--

