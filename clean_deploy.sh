#!/bin/bash -e

docker stop spring-rental-car-backend-1 || echo "container spring-rental-car-backend-1 stopped"

docker rm spring-rental-car-backend-1 || echo "container spring-rental-car-backend-1 removed"

docker stop spring-rental-car-mysql-1 || echo "container spring-rental-car-mysql-1 stopped"

docker rm spring-rental-car-mysql-1 || echo "container spring-rental-car-mysql-1 removed"

docker rmi spring-rental-car-backend || echo "image spring-rental-car-backend removed"

docker rmi mysql:8 || echo "image mysql:8 removed"