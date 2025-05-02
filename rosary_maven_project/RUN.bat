@echo off
title Spring Boot Runner
echo Cleaning Maven...
mvn clean

echo.
echo Starting Spring Boot...
mvn spring-boot:run

