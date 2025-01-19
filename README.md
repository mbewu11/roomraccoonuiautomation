# roomraccoonuiautomation
This repository contains automation tests for UI, Selenium Grid setup, and MySQL CRUD operations.

## Prerequisites
- Docker & Docker Compose installed
- Java JDK 8+ installed
- Maven installed

## Setup Steps

### 1. Clone the Repository
```
git clone <repository_url>
```

### 2. Start Selenium Grid
```
docker-compose -f selenium-grid-docker-compose.yml up -d
```
This starts the Selenium Hub and Chrome/Firefox nodes.

### 3. Start MySQL Service
```
docker-compose -f mysql-docker-compose.yml up -d
```
This starts the MySQL container with the database `test_db`.

### 4. Run the Tests

#### UI Tests:
1. Update `BaseTest` to point to the Selenium Grid Hub (`http://localhost:4444/wd/hub`).
2. Use Maven to execute tests:
```
mvn clean test
```
