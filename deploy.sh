#!/bin/bash -e

docker compose up --build -d

docker compose logs -f backend