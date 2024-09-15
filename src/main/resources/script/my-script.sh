#!/bin/bash
ARG1=$1
ARG2=$2

echo "argument1: $ARG1, argument2: $ARG2"

for i in {1..5}
do
  echo $i
  sleep 5
done