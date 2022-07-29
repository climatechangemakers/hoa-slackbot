#!/bin/bash

pg_dump --no-acl\
       	--no-owner\
       	--schema-only\
       	--table "hour_of_action_event"\
       	postgres\
       	-U ccm_readonly\
       	-h ccm-db.c51ekbqkhdej.us-west-2.rds.amazonaws.com\
       	-p 5432\
       	> ccm_schema.sql