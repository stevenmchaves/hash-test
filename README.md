# hash-test

This assignment was done leveraging `Java`, `mvn`, `RestAssured`, `TestNG, and some other Java libraries for `base64` and `SHA512` validation.

## Pre-reqs
* Clone repo
* Maven issued
* Java 1.8 or above
* PORT 8090 is defined (This was used instead of 8088. Had a conflict because I was already using that port for something else)
* IDE that has some integration with TestNG OR 
* Use CLI, execute at the root level of the local clone of the repo `mvn compile`

## Test Cases Covered in Test Automation

* Application only answers on the PORT specified in the PORT environment
* Validated that simultaneousl requests can be made meaning that multiconnections can be made.

### Shutdown 
* Found that there are occurences on shutdown that the `port` is left open
* Validated that In-flight requests are completed
* Validated that rejects for new requests are done. Was not able to validated
* Validated that any additional while shutdown is is pending or completed results in `Connection refused`

### POST `/hash`
* Validated that 5 seconds elapses for a job identifier to be created and password hash is generated.
* Hashing algorithm doesn't seem to be `SHA512` based on the test conducted.

### GET `/hash`
* Validated that a job identifier is accepted
* Validated that the string returned in `base64` encoded

### GET `/stats`
* Validated that accepts no data
* Validated that accepts empty data
* Validated that the data structure response in JSON
* Validated that the response has `TotalRequests` since the server started
* Validated that the respose when no requests were sent is 0 Total Requests and 0 AverageTime
* Validated that the response has 'AverageTime` in milliseconds
