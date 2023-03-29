## Author
#### Name: Ikechukwu Michael, Email: mikeikechi3@gmail.com
# Introduction🖖
This is a simple USSD-based application that allows users to create an account, access their account balance, and perform instant deposits and withdrawals. The API is designed to be user-friendly and straightforward, making it easy for users to perform transactions seamlessly. With this API, users can manage their finances efficiently and effortlessly.

---

### Step One - Tools and Technologies used 🎼

- Spring Boot(V2.7.3)
- Spring Data JPA
- Lombok (To get rid of boilerplate code by providing encapsulations support)
- JDK 1.8
- Embedded Tomcat
- Mysql Database(Mysql Workbench)
- Maven
- Java IDE (Intellij)

---

### Step Two - Steps to Run the project Locally ⚙️

Create a [redis labs](https://redislabs.com/try-free/) account and deploy a free redis instance. [MySQL Workbench](https://www.mysql.com/products/workbench) was also used to run the database locally. Navigate to Project application.yml and modify the credential per your redis and Mysql database requirement.
```yaml
spring:
  cache:
    port: #redis-port
    host: #redis-host
    password: #provided password
    default-ttl: 180
  
  datasource:
      driver-class-name: com.mysql.cj.jdbc.Driver
      url: #Mysql-host
      username: #Mysql-username
      password: #Mysql-password
```
## Installation

* Clone this repo:

```bash
git clone https://github.com/michaelik/Financial-USSD-Application.git
```
* Navigate to the root directory of the project.
* Build the application
```bash
./mvnw clean install
```
* Run the application
```bash
./mvnw spring-boot:run
```
---

## Usage 🧨

### The Client should be able to:

**Test that the application is accessible**

Request

```
curl -X GET http://localhost:8080/
```

Response

```
Your have reached us
```

**View the application whole menu**

Request

```
curl -X GET http://localhost:8080/menus
```

Response

```json
{
  "1": {
    "id": 230,
    "menu_level": 1,
    "menu_options": [
      {
        "type": "level",
        "response": null,
        "next_menu_level": 2,
        "action": null
      },
      {
        "type": "level",
        "response": null,
        "next_menu_level": 5,
        "action": null
      },
      {
        "type": "level",
        "response": null,
        "next_menu_level": 7,
        "action": null
      },
      {
        "type": "response",
        "response": "END ${account_balance}",
        "next_menu_level": null,
        "action": "PROCESS_USER_ACCOUNT_BALANCE"
      }
    ],
    "text": "CON What would you like to do\n1. Create an account\n2. Deposit\n3. Withdraw\n4. Check Balance"
  },
  "2": {
    "id": 230,
    "menu_level": 2,
    "text": "CON Enter first name?",
    "menu_options": [
      {
        "type": "level_name",
        "response": null,
        "next_menu_level": 3,
        "action": null
      }
    ]
  },
  "3": {
    "id": 230,
    "menu_level": 3,
    "text": "CON Enter last name?",
    "menu_options": [
      {
        "type": "level_name",
        "response": null,
        "next_menu_level": 4,
        "action": null
      }
    ]
  },
  "4": {
    "id": 230,
    "menu_level": 4,
    "text": "CON Enter email?",
    "menu_options": [
      {
        "type": "level_email",
        "response": "END ${account_creation}",
        "next_menu_level": null,
        "action": "PROCESS_USER_ACCOUNT_CREATION"
      }
    ]
  },
  "5": {
    "id": 230,
    "menu_level": 5,
    "text": "CON Enter Account Number?",
    "menu_options": [
      {
        "type": "level_Account_number",
        "response": "END ${deposit_amount}",
        "next_menu_level": 6,
        "action": null
      }
    ]
  },
  "6": {
    "id": 230,
    "menu_level": 5,
    "text": "CON Enter Amount to deposit?",
    "menu_options": [
      {
        "type": "level_d_amount",
        "response": "END ${deposit_amount}",
        "next_menu_level": null,
        "action": "PROCESS_USER_DEPOSIT_AMOUNT"
      }
    ]
  },
  "7": {
    "id": 230,
    "menu_level": 7,
    "text": "CON Enter Amount to Withdraw?",
    "menu_options": [
      {
        "type": "level_w_amount",
        "response": "END ${withdraw_amount}",
        "next_menu_level": null,
        "action": "PROCESS_USER_WITHDRAW_AMOUNT"
      }
    ]
  }
}
```

**Start the application**

The url will have the following request:

- `sessionId`: This enables the application to track and destroy the session accordingly from the application's end.
- `serviceCode`: This is the code for accepting the application through the mobile operator
- `phoneNumber`: This is the mobile number interacting with the USSD application
- `text`: Users can interact with the application by inputting text commands and receiving text-based responses as a result of this functionality

Request

```
curl -X GET http://localhost:8080/ussd?sessionId=124&serviceCode=*141%23&phoneNumber=08186036917&text=
```

Response

```
CON What would you like to do
1. Create an account
2. Deposit
3. Withdraw
4. Check Balance
```