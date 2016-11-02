# TaxiServiceServer
server RESTful app developed using Spring framework

##Public API

###Order API
**_1. Get all orders (by type)_**
```
/order/all/{order_type}
```
Request type: GET
<p>Response:</p>

```json
[{
    "orderId" : "Long",
    "startTime": "DateTime",
    "startPoint" : "String",
    "endPoint" : "String",
    "price" : "Double"
}]
```
**NOTES:**
* Only a taxi driver is allowed to perform operation

**_2. Get order with specific id_**
```
/order/{id}
```
Request type: **GET**
<p>Response:</p>

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
    "additionalRequirements" : [
        {"id" : "Integer",
        "name":"String",
        "description":"String"
        }]
}
```
**NOTES**
* taxiDriver can be empty or null if status is NEW.
* If user is a customer, it must be the owner of the order. 

**_3. Add request_**
```
/order
```
Request type: **POST**
<p>Request body:</p>

```json
{
  "customerId" : "Long",
  "startTime" : "DateTime",
  "startPoint" : "String",
  "endPoint" : "String",
  "additionalRequirements":["id_1","id_2"]
}
```
<b>Note_1:</b>Only customer can perform operation

**_4. AcceptOrder / CancelOrder / MarkAsDone_**
```
/order/{id}
```
Request type: **PUT**
<p>Request body:</p>

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