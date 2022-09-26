#!/bin/bash

result=`ls *.proto`
for var in $result
do
    echo $var
    protoc --java_out=../src/main/java --proto_path=.  $var
done





