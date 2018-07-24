#!/usr/bin/env bash

ENDPOINT='http://localhost:8080/books'

echo_error(){ >&2 echo $@; }

test_response() {
expected=$1
url=$2
opts=$3

URL=${ENDPOINT}
response=$(curl ${opts} --write-out "%{http_code}\n" --silent --output /dev/null ${url})
if [[ ${response} != ${expected} ]]; then
  echo_error "Invalid response for ${URL} | expected: ${expected}, actual: ${response}"
  exit 1;
fi
}

test_response 200 "${ENDPOINT}"
test_response 200 "${ENDPOINT}?id=1"
test_response 201 "${ENDPOINT}" "-d name=Book"
test_response 200 "${ENDPOINT}?id=6&name=UpdatedBook" "-X PUT"
test_response 204 "${ENDPOINT}?id=6" "-X DELETE"
