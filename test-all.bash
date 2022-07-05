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
: ${PORT=8080}

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


function recreateComposite() {
    local courseId=$1
    local composite=$2

    assertCurl 200 "curl -X DELETE http://$HOST:$PORT/course-composite/${courseId} -s"
    curl -X POST http://$HOST:$PORT/course-composite -H "Content-Type: application/json" --data "$composite"
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
    recreateComposite 1 "$body"

    body=\
'{"averageRating": 0,"courseCreatedDate": "2022-07-05T11:40:37.262Z","courseDetails": "string","courseId": 123, "courseTitle": "string","getCourseLastUpdatedDate": "2022-07-05T11:40:37.262Z", "language": "string","numberOfStudents": 0,"price": 0, "priceCurrency": "string"}'
    recreateComposite 123 "$body"

}



set -e

echo "Start:" `date`

echo "HOST=${HOST}"
echo "PORT=${PORT}"

if [[ $@ == *"start"* ]]
then
    echo "Restarting the test environment..."
    echo "$ docker-compose down"
    docker-compose down
    echo "$ docker-compose up -d"
    docker-compose up -d
fi

waitForService curl -X DELETE http://$HOST:$PORT/course-composite/13

setupTestdata

# Verify that a normal request works, expect three lectures and three ratings
assertCurl 200 "curl http://$HOST:${PORT}/course-composite/1 -s"
assertEqual 1 $(echo $RESPONSE | jq .courseId)
assertEqual 3 $(echo $RESPONSE | jq ".lectures | length")
assertEqual 3 $(echo $RESPONSE | jq ".ratings | length")

# Verify that a 404 (Not Found) error is returned for a non existing courseId (13)
assertCurl 404 "curl http://$HOST:${PORT}/course-composite/13 -s"

# Verify that no lectures and no ratings are returned for courseId 123
assertCurl 200 "curl http://$HOST:${PORT}/course-composite/123 -s"
assertEqual 123 $(echo $RESPONSE | jq .courseId)
assertEqual 0 $(echo $RESPONSE | jq ".lectures | length")
assertEqual 0 $(echo $RESPONSE | jq ".ratings | length")

# Verify that a 422 (Unprocessable Entity) error is returned for a courseId that is out of range (-1)
assertCurl 422 "curl http://$HOST:${PORT}/course-composite/-1 -s"
assertEqual "\"Invalid courseId: -1\"" "$(echo $RESPONSE | jq .message)"

# Verify that a 400 (Bad Request) error error is returned for a courseId that is not a number, i.e. invalid format
assertCurl 400 "curl http://$HOST:${PORT}/course-composite/invalidCourseId -s"
assertEqual "\"Type mismatch\"" "$(echo $RESPONSE | jq .message)"

if [[ $@ == *"stop"* ]]
then
    echo "We are done, stopping the test environment..."
    echo "$ docker-compose down"
    docker-compose down
fi

echo "End:" `date`