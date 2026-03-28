# Base OAuth2 Authorization Server

[![Java Version](https://img.shields.io/badge/Java-21-blue.svg)](https://openjdk.org/projects/jdk/21/)
[![Spring Boot Version](https://img.shields.io/badge/Spring%20Boot-3.2.4-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![License](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)
[![Build Status](https://img.shields.io/badge/build-passing-brightgreen.svg)]()

A production-ready OAuth2 Authorization Server built with Spring Boot 3.x and Java 21, providing centralized authentication and authorization with JDBC token storage.

## 📋 Table of Contents
- [Overview](#overview)
- [Features](#features)
- [Technology Stack](#technology-stack)
- [Architecture](#architecture)
- [Prerequisites](#prerequisites)
- [Getting Started](#getting-started)
- [Configuration](#configuration)
- [Database Schema](#database-schema)
- [OAuth2 Flows](#oauth2-flows)
- [API Endpoints](#api-endpoints)
- [Security](#security)
- [Testing](#testing)
- [Deployment](#deployment)
- [Troubleshooting](#troubleshooting)
- [Contributing](#contributing)
- [License](#license)

## 🎯 Overview

**Base OAuth2 Authorization Server** is a comprehensive OAuth2 server that enables:
- **Single Sign-On (SSO)** across multiple applications
- **Secure API access** through OAuth2 tokens
- **Centralized user management** with role-based access control
- **Token lifecycle management** with persistent JDBC storage

## ✨ Features

### Core Features
- ✅ **OAuth2 Authorization Server** - Full OAuth2 protocol support
- ✅ **Multiple Grant Types** - Authorization Code, Client Credentials, Refresh Token, Password
- ✅ **JWT Tokens** - JSON Web Tokens with RSA signing
- ✅ **JDBC Token Store** - Persistent token storage in PostgreSQL
- ✅ **User Management** - Complete user CRUD with account status tracking
- ✅ **Role-Based Access Control** - Roles and permissions management
- ✅ **Account Security** - Failed login tracking and account lockout
- ✅ **Password Encryption** - BCrypt with DelegatingPasswordEncoder

### Advanced Features
- 🔐 **CORS Support** - Configurable cross-origin resource sharing
- 📊 **Actuator Endpoints** - Health, metrics, and monitoring
- 🚀 **Connection Pooling** - HikariCP for database connections
- 📝 **Comprehensive Logging** - Configurable log levels and file rotation
- 🎯 **Profile-Based Configuration** - Dev, test, and production profiles
- 🔄 **Token Revocation** - Support for token revocation endpoint

## 🛠️ Technology Stack

| Category | Technology | Version |
|----------|------------|---------|
| **Framework** | Spring Boot | 3.2.4 |
| **Security** | Spring Security | 6.2.4 |
| **OAuth2** | Spring Security OAuth2 Authorization Server | 1.2.4 |
| **Database** | PostgreSQL | 15+ |
| **ORM** | Spring Data JPA | 3.2.4 |
| **Build Tool** | Maven | 3.8+ |
| **Language** | Java | 21 |
| **Utilities** | Lombok | 1.18.32 |

## 🏗️ Architecture
