# roomraccoonuiautomation
This repository contains automation tests for UI, Selenium Grid setup, and MySQL CRUD operations.

## Prerequisites
- Docker & Docker Compose installed
- Java JDK 11 correto installed
- Maven installed
- GIT

## Setup Steps

### 1. Clone the Repository
```
git clone <https://github.com/mbewu11/roomraccoonuiautomation.git>
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
Setup Run/Debug settings for TestNG:

Click the down arrow and then click “Edit configuration”

Open Templates > TestNG

Output directory: target/surefire-report
VM Options: -ea -Denv=acc -Ddriver=chrome
#### UI Tests:
1. Update `BaseTest` to point to the Selenium Grid Hub (`http://localhost:4444/wd/hub`).
2. Use Maven to execute tests:
```
mvn clean test
```
