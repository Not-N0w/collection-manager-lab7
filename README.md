# Lab Work №7

**Variant:** 5527  
**Author:** Yakov Makogon 

## Description

This lab extends [Lab Work №6](https://github.com/Not-N0w/collection-manager-lab6) by introducing persistent storage and user authorization.

### Key Features

- PostgreSQL integration (replaces file-based storage)
- User registration and login with SHA-224 password hashing
- Database-side ID generation via sequences
- In-memory collection reflects only successfully committed database changes
- Users can view all elements, but modify only their own
- Secure per-request user authentication (login + password)
- Multi-threaded server

## Technologies

- Java 17
- JDBC (PostgreSQL)
- java.util.concurrent (Executors, ThreadPools)
- Password hashing with SHA-224
- Gradle

## Report

📄 See full implementation details in [`report.pdf`](./лаб7.pdf)
