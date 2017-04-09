# TaxiServiceServer
server RESTful app developed using Spring framework

##Public API
###User API

**_1. Register_**
```
/user/register
```
RequestType: **POST**

Request body:
```json
{
  "name": "String",
  "email" : "String",
  "password" : "String",
  "mobileNumbers" :[
  	  {"mobileNumber":"String"}
  ],
  "userType" : "CUSTOMER/TAXI_DRIVER",
  "car" : {
    "manufacturer" : "String",
    "model" : "String",
    "plateNumber" : "String",
    "seatsNumber" : "Integer",
    "carType" : "TRUCK, PASSENGER_CAR, MINIBUS"
  },
  "driverLicense":{
    "code":"String",
    "expirationTime":"DateTime"
  }
}
```
**NOTES**
* If userType is TAXI_DRIVER, car object **MUST** contain all fields.
* If userType is CUSTOMER, car/driverLicense object will be omitted even if it exists.

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
  "userId":"String",
  "name": "String",
  "email" : "String",
  "mobileNumbers" : [{
     "idMobileNumber":"Integer",
     "mobileNumber": "String"
  }],
  "userType" : "CUSTOMER/TAXI_DRIVER",
  "car" : {
    "carId":"Integer",
    "manufacturer" : "String",
    "model" : "String",
    "plateNumber" : "String",
    "seatsNumber" : "Integer",
    "carType" : "TRUCK, PASSENGER_CAR, MINIBUS"
  },
  "driverLicense":{
    "driverLicenseId":"Integer",
    "driverLicense":"String",
    "expirationTime":"Date"
  }
}
```

**_4. Update user profile_**
```
/user
```

RequestType: **PATCH**

Request body:
```json
{
  "name": "String",
  "mobileNumbers" : [{
     "idMobileNumber":"Integer",
     "mobileNumber": "String"
  }],
  "car" : {
    "manufacturer" : "String",
    "model" : "String",
    "plateNumber" : "String",
    "seatsNumber" : "Integer",
    "carType" : "TRUCK, PASSENGER_CAR, MINIBUS"
  },
  "driverLicense":{
    "driverLicense":"String",
    "expirationTime":"Date"
  }
}
```

**NOTES:**
* Car and driver license objects will be omitted if userType = CUSTOMER.
* Fields can be omitted because of PATCH function usage
* Exception will be thrown if **'email'** field is present (you cannot update your login)
* Exception will be thrown if **'userType'** field is present (you cannot change yours role in system)
* If you want to delete mobile number - add it id and set **'mobileNumber'** to **NULL**
* If you want to add mobile number - just set it id to null and fill **mobileNumber** field
* If you want to update mobile number - just set it id and fill **mobileNumber** field
* No guarantee that mobile numbers will be added in order they came in request

**_5. Login_**
```
/login
```
RequestType: **POST**

Request body:
```json
{
  "email":"String",
  "password":"String"
}
```

Response body:
```json
{
  "token":"String",
  "token_type":"String",
  "user_type":"String"
}
```
###Order API
**_1. Get all orders (by type)_**
```
/order?orderStatus='actualOrderStatus'
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
    "status":"NEW/ACCEPTED/CANCELLED/DONE/WAITING/PROCESSING"
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
     "routePoint":[{
        "adminArea":"String",
        "latitude":"String",
        "longtitude":"String",
        "street":"String",
        "houseNumber":"String",
        "city":"String"
     }],
    "status" : "NEW/ACCEPTED/CANCELLED/DONE/WAITING/PROCESSING",
    "customerId": "Integer",
    "driverId" : "Integer",
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
  "startTime" : "DateTime",
  "comment" : "String",
  "routePoint":[{
    "adminArea":"String",
    "latitude":"String",
    "longtitude":"String",
    "street":"String",
    "houseNumber":"String",
    "city":"String"
  }],
  "orderPrice":{
        "distance":"Double",
        "additionalRequirements" : [{
            "reqId" : "Integer",
            "reqValueId" : "Integer"
      }]
  }
}
```
**NOTES:**
* Only customer can perform operation
* if "startTime" is null - quick request

**_4. Accept order / Refuse order / Mark as done_**
```
/order/{orderId}/status
```
Request type: **PUT**

Request body:

```json
{
  "userId" : "Long",
  "orderStatus" : "ACCEPTED/CANCELLED/DONE"
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
  "customerId" : "Long",
  "startTime" : "DateTime",
  "startPoint" : "String",
  "endPoint" : "String",
  "orderPrice":{
          "distance":"Double",
          "additionalRequirements" : [{
              "reqId" : "Integer",
              "reqValueId" : "Integer"
        }]
    }
}
```

**_7. Calculate order price_**
```
/order/price
```
Request type: **POST**

Request body:
```json
{
  "distance" : "Double",
  "additionalRequirements":[{
    "reqId" : "Integer",
    "reqValueId" : "Integer"
  }]
}
```
**NOTES**
* Can be performed only by customer.


**EXCEPTION RESPONSE TEMPLATES**

*Request validation error template:*
```json
[{
  "field" : "String",
  "code"  : "String",
  "message" : "String"
}]
```

*Any other exception:*
```json
{"message" : "String"}
```