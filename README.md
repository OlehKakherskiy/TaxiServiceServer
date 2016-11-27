# TaxiServiceServer
server RESTful app developed using Spring framework

##Public API
###User API

**_1. Register_**
```
/user
```
RequestType: **POST**

Request body:
```json
{
  "name": "String",
  "email" : "String",
  "password" : "String",
  "mobileNumbers" : ["mobileNumber1","mobileNumber2"],
  "userType" : "CUSTOMER/TAXI_DRIVER",
  "car" : {
    "manufacturer" : "String",
    "model" : "String",
    "plateNumber" : "String",
    "seatsNumber" : "Integer",
    "carType" : "TRUCK, PASSENGER_CAR, MINIBUS"
  }
}
```
**NOTES**
* If userType is TAXI_DRIVER, car object **MUST** contain all fields.
* If userType is CUSTOMER, car object will be omitted even if it exists.

**_2. Recovery password_** (could be reviewed)
```
/user/{userId}/password/
```
RequestType: **PUT**

Request body:
```json
{
    "email" : "String",
    "password" : "String"
}
```

**_3. Get user profile_**
```
/user/{userId}
```
RequestType: **GET**

Response:
```json
{
  "name": "String",
  "email" : "String",
  "mobileNumbers" : ["mobileNumber1","mobileNumber2"],
  "userType" : "CUSTOMER/TAXI_DRIVER",
  "car" : {
    "manufacturer" : "String",
    "model" : "String",
    "plateNumber" : "String",
    "seatsNumber" : "Integer",
    "carType" : "TRUCK, PASSENGER_CAR, MINIBUS"
  }
}
```

**_4. Update user profile_**
```
/user/{userId}
```

RequestType: **PUT**

Request body:
```json
{
  "name": "String",
  "email" : "String",
  "password" : "String",
  "mobileNumbers" : ["mobileNumber1","mobileNumber2"],
  "userType" : "CUSTOMER/TAXI_DRIVER",
  "car" : {
    "manufacturer" : "String",
    "model" : "String",
    "plateNumber" : "String",
    "seatsNumber" : "Integer",
    "carType" : "TRUCK, PASSENGER_CAR, MINIBUS"
  }
}
```

**NOTES:**
* If car object is null - no changes will be performed with it.
* Car object will be omitted if userType = CUSTOMER. 

###Order API
**_1. Get all orders (by type)_**
```
/order/{orderStatus}
```
**orderStatus** = NEW/ACCEPTED/CANCELLED/DONE/ALL

Request type: GET

Response:

```json
[{
    "orderId" : "Long",
    "startTime" : "DateTime",
    "startPoint" : "String",
    "endPoint" : "String",
    "price" : "Double",
    "status":"NEW/ACCEPTED/CANCELLED/DONE"
}]
```
**NOTES:**
* Only a taxi driver is allowed to perform operation

**_2. Get order with specific id_**
```
/order/{orderId}
```
Request type: **GET**

Response:

```json
{
    "orderId" : "Long",
    "startTime" : "DateTime",
    "startPoint" : "String",
    "endPoint" : "String",
    "status" : "NEW/ACCEPTED/CANCELLED/DONE",
    "customer" :{
        "customerId" : "Long",
        "name" : "String"
    },
    "taxiDriver":{
        "taxiDriverId" : "Long",
        "name" : "String"
    },
    "price" : "Double",
    "additionalRequirements" : [{  
        "reqId" : "Integer",
        "reqValueId":"Integer"
    }]
}
```
**NOTES**
* taxiDriver can be empty or null if status is NEW.
* If user is a customer, it must be the owner of the order. 

**_3. Add order_**
```
/order
```
Request type: **POST**

Request body:

```json
{
  "customerId" : "Long",
  "startTime" : "DateTime",
  "startPoint" : "String",
  "endPoint" : "String",
  "additionalRequirements" : [{
      "reqId" : "Integer",
      "reqValueId" : "Integer"
  }]
}
```
**NOTES:**
* Only customer can perform operation

**_4. Accept order / Refuse order / Mark as done_**
```
/order/{orderId}/status
```
Request type: **PUT**

Request body:

```json
{
  "userId" : "Long",
  "type" : "ACCEPTED/CANCELLED/DONE"
}
```
**NOTES:**
* Customer, that not an owner of the order, can't perform this operation.
* Customer-owner of the order can only cancel the order.
* Taxi driver can mark order as done only after accepting its servicing.

**_5. Delete order_**
```
/order/{orderId}
```
Request type: **DELETE**

**NOTES**
* Can be performed only by customer-owner.

**_6. Update order_**
```
/order/{orderId}
```
Request type: **PUT**

Request body:

```json
{
  "startTime" : "DateTime",
  "startPoint" : "String",
  "endPoint" : "String",
  "additionalRequirements": [{
      "reqId" : "Integer",
      "reqValueId" : "Integer"
  }]
}