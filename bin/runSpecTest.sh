#!/bin/bash

CP=$M2_REPO/commons-io/commons-io/2.0.1/commons-io-2.0.1.jar:$M2_REPO/junit/junit/4.8.2/junit-4.8.2.jar:$M2_REPO/org/scala-lang/scala-library/2.9.0-1/scala-library-2.9.0-1.jar:$M2_REPO/org/scalatest/scalatest_2.9.0/1.6.1/scalatest_2.9.0-1.6.1.jar

scala -cp ./target/classes:./target/test-classes:$CP org.scalatest.tools.Runner -p . -o -s shop.RecipeSpec