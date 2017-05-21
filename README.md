# TaxiServiceServer
server RESTful app developed using Spring framework

##Public API

**GENERAL NOTES** 
* Date and DateTime should be in format ISO 8601.
* OrderStatus enumeration contains values : NEW/ACCEPTED/CANCELLED/DONE/WAITING/PROCESSING
* UserType enumeration contains values : CUSTOMER/TAXI_DRIVER
* CarType enumeration contains values : TRUCK/PASSENGER_CAR/MINIBUS
 
 
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
  "userType" : "UserType",
  "car" : {
    "manufacturer" : "String",
    "model" : "String",
    "plateNumber" : "String",
    "seatsNumber" : "Integer",
    "carType" : "CarType"
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
  "userType" : "UserType",
  "car" : {
    "manufacturer" : "String",
    "model" : "String",
    "plateNumber" : "String",
    "seatsNumber" : "Integer",
    "carType" : "CarType"
  },
  "driverLicense":{
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
    "carType" : "CarType"
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
  "password":"String",
  "notificationToken":"String"
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
**NOTES:**
* notification token is optional field. If present - notification feature will be switched on.

**_6. Toggle notifications_**
```
/user/notifications?toggle=<boolean_value>
```

RequestType : **POST**

Request body: absent

**NOTES:**
* <boolean_value> - true/false;
* if there's no notification token for user - no exception will be thrown;
* if false - no notifications will be sent until switch on

**_7. Get notification toggle status_**
```
/user/notifications/toggle-position
```

RequestType: **GET**

Response body: boolean


###Order API
**_1. Get all orders (by type)_**
```
/order?orderStatus='actualOrderStatus'
```
**orderStatus** = "OrderStatus" + ALL

Request type: GET

Response:

```json
[{
    "orderId" : "Long",
    "startTime" : "DateTime",
    "startPoint" : "String",
    "endPoint" : "String",
    "price" : "Double",
    "status":"OrderStatus"
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
    "status" : "OrderStatus",
    "customerId": "Integer",
    "driverId" : "Integer",
    "distance": "Double",
    "duration":"Time",
    "price" : "Double",
    "extraPrice":"Double",
    "comment":"String",
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
  "routePoints":[{
    "latitude":"String",
    "longtitude":"String"
  }],
  "additionalRequirements" : [{
    "reqId" : "Integer",
    "reqValueId" : "Integer"
  }]
}
```
**NOTES:**
* Only customer can perform operation
* if "startTime" is null - quick request. The date of request will be saved and returned by next get request

**_4. Change order status _**
```
/order/{orderId}/status
```
Request type: **PUT**

Request body:

```json
{
  "orderStatus" : "OrderStatus"
}
```
**NOTES:**
* Customer, that not an owner of the order, can't perform this operation.
* Customer-owner of the order can only cancel the order.
* Taxi driver can mark order as done only after accepting its servicing.
* When customer is logged in, specified notification token and driver changes status - notification will be sent to order owner.
* When driver is logged in, specified notification token and customer cancels order - notification will be send to driver. 

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
Request type: **PATCH**

Request body:

```json
{
  "quickRequest":"Boolean",
  "startTime" : "DateTime",
  "comment":"String",
  "routePoint":[{
      "routePointId":"Long",
      "routePointIndex":"Integer",
      "latitude":"String",
      "longtitude":"String"
    }],
    "additionalRequirements" : [{
      "reqId" : "Integer",
      "reqValueId" : "Integer"
    }]
}
```

**NOTES**
* if 'quickRequest' is true, 'startTime' will be omitted
* 'orderPrice' can be omitted if there are no changes in additional requirements.
* if there are some changes in additional requirements - add just changed ones.
* 'routePoint' can be omitted if there are no changes in route.
* to remove or change existed route point - add it's 'lat', 'long' and id.
* to add route point to the end of list - omit 'routePointIndex'.
* to add route point to the specific position - fill 'routePointIndex'.
* to remove route point - add it's 'routePointId' and fill 'routePointIndex' to **NULL**.
* to change route point position - specify its 'routePointIndex'.
* if any route point was update - distance recalculation process will be triggered.
* price recalculation will perform automatically.
* all another params will be ignored.

**_7. Get order route info_**
```
/order/routeinfo
```
Request type: **POST**

Request body:
```json
{
  "routePoint":{
    "latitude":"String",
    "longtitude":"String"
  },
  "additionalRequirements":[{
    "reqId" : "Integer",
    "reqValueId" : "Integer"
  }]
}
```

Response body:
```json
{
  "price":"Double",
  "distance":"Double",
  "duration":"Time"
}
```

**NOTES**
* Can be performed only by customer.
* Duration value format - ISO-8601


##Restore password

**Step-by-step:**
1. /user/password
* Type: **POST**
* Request body:
```json
{"email":"String"}
```
* Response body: id of password restore request - _restore_id_request_
* Result: returned id request that will be used at next request and email message with _secret_code_

2. /user/password/_restore_id_request_
* Type: **POST**
* Request body:
```json
{
  "password":"String",
  "code":"String"
}
```

**GOOGLE CLOUD MESSAGING TEMPLATES**
* Change order status notification (for order owner):
1. driver is waiting for customer:
```json
 {
    "data":{
        "orderStatus":"WAITING",
        "plateNumber":"String",
        "model":"String",
        "manufacturer":"String",
        "name":"String",
        "orderId":"Long"
    },
    "to":"String"
 }
 ```
2. driver/customer changes status
```json
{
    "data":{
        "orderStatus":"OrderStatus",
        "name":"String",
        "orderId":"Long"
    },
    "to":"String"
}
```


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