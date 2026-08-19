# DevOps Modern End-to-End Deployment

![Docker](https://img.shields.io/badge/Docker-Containerized-blue)
![Java](https://img.shields.io/badge/Java-21-orange)
![Maven](https://img.shields.io/badge/Maven-Build-red)
![Terraform](https://img.shields.io/badge/Terraform-IaC-purple)
![Ansible](https://img.shields.io/badge/Ansible-Automation-black)
![Jenkins](https://img.shields.io/badge/Jenkins-CI%2FCD-red)

## Overview

This project demonstrates an end-to-end DevOps workflow for deploying and managing multiple applications.

The project combines containerization, source control, infrastructure as code, configuration management and CI/CD automation.

## Applications

### Portfolio Web Application

A responsive portfolio website built with:

- HTML
- CSS
- Nginx
- Docker

### Java Application

A Java web application built with:

- Java 21
- Maven
- Docker

## DevOps Technologies

- Linux / WSL2
- Git
- GitHub
- Docker
- Docker Compose
- Java
- Maven
- Jenkins
- SonarQube
- Terraform
- Ansible
- AWS

## Development Environment

The project is developed and tested locally using WSL2, Docker and VS Code.

## Current Architecture

```LiftedDevOps-10
                    GitHub
                       |
                       v
                    Jenkins
                       |
             +---------+---------+
             |                   |
             v                   v
       Portfolio App          Java App
             |                   |
             v                   v
           Docker              Docker
             |                   |
             +---------+---------+
                       |
                       v
                Docker Compose
                       |
             +---------+---------+
             |                   |
             v                   v
        localhost:8082      localhost:8081