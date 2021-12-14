# hash-test

This assignment was done leveraging `Java`, `mvn`, `RestAssured`, `TestNG, and some other Java libraries for `base64` and `SHA512` validation. <br>
<b>Note: I believe the repo is SAFE from the log4j issues. I am using the latest versions of the maven dependencies</b>

## Pre-reqs

* Clone repo
* Maven issued
* Java 1.8 or above
* PORT 8090 is defined (This was used instead of 8088. Had a conflict because I was already using that port for something else)
* IDE that has some integration with TestNG OR
* Use CLI, execute at the root level of the local clone of the repo `mvn compile`

## Test Cases Covered in Test Automation

### PasswordPortTest

* Application only answers on the PORT specified in the PORT environment (See `testInvalidPort`)

### Concurrency

* Validated that simultaneously requests can be made meaning that multiconnections can be made. This can be seen in the execution of the `concurrency.xml` TestNG.xml file <br>
  The execution runs to test run concurrently in 5 threads. (Runs `testTwoCallsA` and `testTwoCallsB` conccurently) <br> See `org.chaves.steven.PasswordConcurrencyTest` for further information.

#### Execute concurrency tests

1. Start the server up first.
1. `mvn test -P concurrency-test`

### Shutdown

The test cases/validation points below are validated by running the `shutdown.xml` TestNG file. This file runs test methods concurrently which allows the validates of the pending request validations to occur. See  `org.chaves.steven.PasswordShutdownTest` for further information.

* Found that there are occurrences on shutdown that the `port` is left open
* Validated that In-flight requests are completed (see `testMultipleCalls`)
* Validated that rejects for new requests are done. Was not able to validated (see `testMultipleCalls` and `testShutdown`)
* Validated that any additional while shutdown is is pending or completed results in `Connection refused` (see `testMultipleCalls` and `testShutdown`)

#### Execute shutdown tests

1. Start the server up first.
1. `mvn test -P shutdown-test`
<br>Note: The test cleanup tries to restart the server again. If it fails, start up the server again before executing the next test suite.

### Standard Set of tests

#### POST `/hash` - PasswordPostHashingTest

* Test case - Hashing algorithm doesn't seem to be `SHA512` based on the test conducted for password. - See `testHappyPathValidatePasswordSHA512`
* Test case - Post is supported with no data - `Malformed Input - 400` (See `testPostApi`)
* Test case - Post with invalid parameters - See `testPostAPIInvalidParameters`
* Test case - Post with empty data - See `testPostWithEmptyDataProvided`
* Test case - Post with password, but with no value - See `testPostWithInvalidDataNoPasswordValue`
    <b>BUG: Seems to be a bug as this should not allow empty password</b>
* Test case - invalid key in the data - See `testPostWithInvalidDataKeyValue`
    <b>BUG: Should probably returned 400 with something about fomrat being incorrect </b>
* Test case - validate response time - Validated that 5 seconds elapses for a job identifier to be created and password hash is generated. - See `testPostValidateResponseTime`
* Test case - Confirm that entire data json is not being used for SHA512- See `testPostNotAllJsonDataSHA512`

#### GET `/hash` - PasswordGetHashingTest

* Test case - Without job identifier. See `testGetHashWithoutJobId` method.
* Test case - Without job identifier and data. See `testGetHashWithoutJobIdEmptyDataProvided` method.
* Test case - Get call with invalid parametersValidated that a job identifier is accepted. See `testGetHashInvalidParameters` method.
* Test case - Job Identifier does not exist - See `testGetHashJobIdNotFound` method.
* Test case - Job identifier does not exist and supply empty data. See `testGetHashJobIdNotFoundWithData` method.
* Test case - Happy path - Validated that the string returned in `base64` encoded and Job Identifier exists - See `testGetHashHappyPath` method.

#### GET `/stats` - PasswordStatsHashingTest

* Test case - Supply parameters - See `testGetStatsWithParameters` method.
* Test case - Validated that accepts no data - See `testGetStatsWithProvidingData` method.
* Test case - Validated that accepts empty data - See `testGetStatsWithEmptyDataProvided` method.
    <b> BUG - Expected a 400 here with some type of error message. Same as test case above</b>
* Test case - Happy path - Validated that the data structure response in JSON - See `testGetStatsHappyPath` method.
* Test case - Total requests get incremented - Validated that the response has `TotalRequests` since the server started See `testStatsTotalRequests`
    <b> BUG - This doesn't seem to be working correctly. Either it takes 5 seconds to be updated because it does increment sometimes.
* Test case - Validated that the response when no requests were sent is 0 Total Requests and 0 AverageTime
* Test case - Validated that the response has 'AverageTime` in milliseconds

#### Execute standard set

1. Make sure the server is started
1. `mvn test`

Please reach out to me: `stevenmchaves@gmail.com` for further information.
