#!/usr/bin/env bash
#
# ./grdelw clean build
# docker-compose build
# docker-compose up -d
#
# Sample usage:
#
#   HOST=localhost PORT=7000 ./test-em-all.bash
#
: ${HOST=localhost}
: ${PORT=8443}
: ${CRS_ID_LECS_RATS=1}
: ${CRS_ID_NOT_FOUND=13}
: ${CRS_ID_NO_LECS_NO_RATS=123}

function assertCurl() {

  local expectedHttpCode=$1
  local curlCmd="$2 -w \"%{http_code}\""
  local result=$(eval $curlCmd)
  local httpCode="${result:(-3)}"
  RESPONSE='' && (( ${#result} > 3 )) && RESPONSE="${result%???}"

  if [ "$httpCode" = "$expectedHttpCode" ]
  then
    if [ "$httpCode" = "200" ]
    then
      echo "Test OK (HTTP Code: $httpCode)"
    else
      echo "Test OK (HTTP Code: $httpCode, $RESPONSE)"
    fi
  else
      echo  "Test FAILED, EXPECTED HTTP Code: $expectedHttpCode, GOT: $httpCode, WILL ABORT!"
      echo  "- Failing command: $curlCmd"
      echo  "- Response Body: $RESPONSE"
      exit 1
  fi
}

function assertEqual() {

  local expected=$1
  local actual=$2

  if [ "$actual" = "$expected" ]
  then
    echo "Test OK (actual value: $actual)"
  else
    echo "Test FAILED, EXPECTED VALUE: $expected, ACTUAL VALUE: $actual, WILL ABORT"
    exit 1
  fi
}

function testUrl() {
    url=$@
    if curl $url -ks -f -o /dev/null
    then
          echo "Ok"
          return 0
    else
          echo -n "not yet"
		  return 1
    fi;
}

function waitForService() {
    url=$@
    echo -n "Wait for: $url... "
    c=0
    until testUrl $url
    do
		echo -n ", c: $c"
        ((c=c+1))
		echo -n ", c: $c"
        if [[ $c == 20 ]]
        then
            echo " Give up"
            exit 1
        else
            sleep 6
            echo -n ", retry #$c "
        fi
    done
	echo "waitForService done"
}

function testCompositeCreated() {

    # Expect that the Product Composite for courseId $CRS_ID_LECS_RATS has been created with three lectures and three ratings
    if ! assertCurl 200 "curl $AUTH -k https://$HOST:$PORT/course-composite/$CRS_ID_LECS_RATS -s"
    then
        echo -n "FAIL"
        return 1
    fi

    set +e
    assertEqual "$CRS_ID_LECS_RATS" $(echo $RESPONSE | jq .courseId)
    if [ "$?" -eq "1" ] ; then return 1; fi

    assertEqual 3 $(echo $RESPONSE | jq ".lectures | length")
    if [ "$?" -eq "1" ] ; then return 1; fi

    assertEqual 3 $(echo $RESPONSE | jq ".ratings | length")
    if [ "$?" -eq "1" ] ; then return 1; fi

    set -e
}

function waitForMessageProcessing() {
    echo "Wait for messages to be processed... "

    # Give background processing some time to complete...
    sleep 1

    n=0
    until testCompositeCreated
    do
        n=$((n + 1))
        if [[ $n == 40 ]]
        then
            echo " Give up"
            exit 1
        else
            sleep 6
            echo -n ", retry #$n "
        fi
    done
    echo "All messages are now processed!"
}





function recreateComposite() {
    local courseId=$1
    local composite=$2

    assertCurl 200 "curl $AUTH -X DELETE -k https://$HOST:$PORT/course-composite/${courseId} -s"
    curl -X POST -k https://$HOST:$PORT/course-composite -H "Content-Type: application/json" -H "Authorization: Bearer $ACCESS_TOKEN" --data "$composite"
}

function setupTestdata() {
    body=\
'{"averageRating": 0,"courseCreatedDate": "2022-07-05T11:40:37.262Z","courseDetails": "string","courseId": 1, "courseTitle": "string","getCourseLastUpdatedDate": "2022-07-05T11:40:37.262Z", "language": "string","numberOfStudents": 0,"price": 0, "priceCurrency": "string","lectures": [
   {"durationInMinutes": 0, "lectureDetails": "string", "lectureId": 13, "lectureOrder": 0,"lectureTitle": "string"},
   {"durationInMinutes": 0, "lectureDetails": "string", "lectureId": 14, "lectureOrder": 1,"lectureTitle": "string"},
   {"durationInMinutes": 0, "lectureDetails": "string", "lectureId": 15, "lectureOrder": 2,"lectureTitle": "string"}
 ], "ratings":[
   {"ratingCreatedDate": "2022-07-05T11:40:37.262Z","ratingId": 13,"starRating": 0,"text": "string","userId": 0},
   {"ratingCreatedDate": "2022-07-05T11:40:37.262Z","ratingId": 14,"starRating": 0,"text": "string","userId": 1},
   {"ratingCreatedDate": "2022-07-05T11:40:37.262Z","ratingId": 15,"starRating": 0,"text": "string","userId": 2}
]}'
    recreateComposite "$CRS_ID_LECS_RATS" "$body"

    body=\
'{"averageRating": 0,"courseCreatedDate": "2022-07-05T11:40:37.262Z","courseDetails": "string","courseId": 123, "courseTitle": "string","getCourseLastUpdatedDate": "2022-07-05T11:40:37.262Z", "language": "string","numberOfStudents": 0,"price": 0, "priceCurrency": "string"}'
    recreateComposite "$CRS_ID_NO_LECS_NO_RATS" "$body"

}

function testCircuitBreaker() {

    echo "Start Circuit Breaker tests!"

    # Access actuator endpoints from container attached to the internal network (otherwise not possible if not exposed through edge server)
    EXEC="docker run --rm --network=my-network alpine"

    # First, use the health - endpoint to verify that the circuit breaker is closed
    assertEqual "CLOSED" "$($EXEC wget course-composite:8080/actuator/health -qO - | jq -r .components.circuitBreakers.details.course.details.state)"

    # Open the circuit breaker by running three slow calls in a row, i.e. that cause a timeout exception
    # Also, verify that we get 500 back and a timeout related error message
    for ((n=0; n<3; n++))
    do
        assertCurl 500 "curl -k https://$HOST:$PORT/course-composite/$CRS_ID_LECS_RATS?delay=3 $AUTH -s"
        message=$(echo $RESPONSE | jq -r .message)
        assertEqual "Did not observe any item or terminal signal within 2000ms" "${message:0:57}"
    done

    # Verify that the circuit breaker now is open by running the slow call again, verify it gets 200 back, i.e. fail fast works, and a response from the fallback method.
    assertCurl 200 "curl -k https://$HOST:$PORT/course-composite/$CRS_ID_LECS_RATS?delay=3 $AUTH -s"
    assertEqual "Fallback course" "$(echo "$RESPONSE" | jq -r .courseTitle)"

    # Also, verify that the circuit breaker is open by running a normal call, verify it also gets 200 back and a response from the fallback method.
    assertCurl 200 "curl -k https://$HOST:$PORT/course-composite/$CRS_ID_LECS_RATS $AUTH -s"
    assertEqual "Fallback course" "$(echo "$RESPONSE" | jq -r .courseTitle)"

    # Verify that a 404 (Not Found) error is returned for a non existing courseId ($CRS_ID_NOT_FOUND) from the fallback method.
    assertCurl 404 "curl -k https://$HOST:$PORT/course-composite/$CRS_ID_NOT_FOUND $AUTH -s"
    assertEqual "Course Id: $CRS_ID_NOT_FOUND not found in fallback cache!" "$(echo $RESPONSE | jq -r .message)"

    # Wait for the circuit breaker to transition to the half open state (i.e. max 10 sec)
    echo "Will sleep for 10 sec waiting for the CB to go Half Open..."
    sleep 10

    # Verify that the circuit breaker is in half open state
    assertEqual "HALF_OPEN" "$($EXEC wget course-composite:8080/actuator/health -qO - | jq -r .components.circuitBreakers.details.course.details.state)"

    # Close the circuit breaker by running three normal calls in a row
    # Also, verify that we get 200 back and a response based on information in the course database
    for ((n=0; n<3; n++))
    do
        assertCurl 200 "curl -k https://$HOST:$PORT/course-composite/$CRS_ID_LECS_RATS $AUTH -s"
        assertEqual "string" "$(echo "$RESPONSE" | jq -r .courseTitle)"
    done

    # Verify that the circuit breaker is in closed state again
    assertEqual "CLOSED" "$($EXEC wget course-composite:8080/actuator/health -qO - | jq -r .components.circuitBreakers.details.course.details.state)"

    # Verify that the expected state transitions happened in the circuit breaker
    assertEqual "CLOSED_TO_OPEN"      "$($EXEC wget course-composite:8080/actuator/circuitbreakerevents/course/STATE_TRANSITION -qO - | jq -r .circuitBreakerEvents[-3].stateTransition)"
    assertEqual "OPEN_TO_HALF_OPEN"   "$($EXEC wget course-composite:8080/actuator/circuitbreakerevents/course/STATE_TRANSITION -qO - | jq -r .circuitBreakerEvents[-2].stateTransition)"
    assertEqual "HALF_OPEN_TO_CLOSED" "$($EXEC wget course-composite:8080/actuator/circuitbreakerevents/course/STATE_TRANSITION -qO - | jq -r .circuitBreakerEvents[-1].stateTransition)"
}

set -e

echo "Start:" `date`

echo "HOST=${HOST}"
echo "PORT=${PORT}"

if [[ $@ == *"start"* ]]
then
    echo "Restarting the test environment..."
    echo "$ docker-compose down --remove-orphans"
    docker-compose down --remove-orphans
    echo "$ docker-compose up -d"
    docker-compose up -d
fi

waitForService curl -k https://$HOST:$PORT/actuator/health

ACCESS_TOKEN=$(curl -k https://writer:secret@$HOST:$PORT/oauth/token -d grant_type=password -d username=natalija -d password=password -s | jq .access_token -r)
AUTH="-H \"Authorization: Bearer $ACCESS_TOKEN\""

echo "$AUTH"

setupTestdata

waitForMessageProcessing

# Verify that a normal request works, expect three lectures and three ratings
assertCurl 200 "curl -k https://$HOST:${PORT}/course-composite/1  $AUTH -s"
assertEqual 1 $(echo $RESPONSE | jq .courseId)
assertEqual 3 $(echo $RESPONSE | jq ".lectures | length")
assertEqual 3 $(echo $RESPONSE | jq ".ratings | length")

# Verify that a 404 (Not Found) error is returned for a non existing courseId (13)
assertCurl 404 "curl -k https://$HOST:${PORT}/course-composite/13 $AUTH -s"

# Verify that no lectures and no ratings are returned for courseId 123
assertCurl 200 "curl -k https://$HOST:${PORT}/course-composite/123 $AUTH -s"
assertEqual 123 $(echo $RESPONSE | jq .courseId)
assertEqual 0 $(echo $RESPONSE | jq ".lectures | length")
assertEqual 0 $(echo $RESPONSE | jq ".ratings | length")

# Verify that a 422 (Unprocessable Entity) error is returned for a courseId that is out of range (-1)
assertCurl 422 "curl -k https://$HOST:${PORT}/course-composite/-1 $AUTH -s"
assertEqual "\"Invalid courseId: -1\"" "$(echo $RESPONSE | jq .message)"

# Verify that a 400 (Bad Request) error error is returned for a courseId that is not a number, i.e. invalid format
assertCurl 400 "curl -k https://$HOST:${PORT}/course-composite/invalidCourseId $AUTH -s"
assertEqual "\"Type mismatch.\"" "$(echo $RESPONSE | jq .message)"


# Verify that a request without access token fails on 401, Unauthorized
assertCurl 401 "curl -k https://$HOST:$PORT/course-composite/$CRS_ID_LECS_RATS -s"

# Verify that the reader - client with only read scope can call the read API but not delete API.
READER_ACCESS_TOKEN=$(curl -k https://reader:secret@$HOST:$PORT/oauth/token -d grant_type=password -d username=natalija -d password=password -s | jq .access_token -r)
READER_AUTH="-H \"Authorization: Bearer $READER_ACCESS_TOKEN\""

echo "$READER_AUTH"

assertCurl 200 "curl -k https://$HOST:$PORT/course-composite/$CRS_ID_LECS_RATS $READER_AUTH -s"
assertCurl 403 "curl -k https://$HOST:$PORT/course-composite/$CRS_ID_LECS_RATS $READER_AUTH -X DELETE -s"

testCircuitBreaker

echo "End, all tests OK:" `date`

if [[ $@ == *"stop"* ]]
then
    echo "Stopping the test environment..."
    echo "$ docker-compose down --remove-orphans"
    docker-compose down --remove-orphans
fi