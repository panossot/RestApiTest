#!/bin/bash

export RDIR=modules/testcases/v1/authorApiTestsuite/test-configurations/target/surefire-reports/

./report.sh


export RDIR=modules/testcases/v1/bookApiTestsuite/test-configurations/target/surefire-reports/

./report.sh



export RDIR=modules/testcases/v1/happyPathApiTestsuite/test-configurations/target/surefire-reports/

./report.sh



export RDIR=modules/testcases/v1/performanceApiTestsuite/test-configurations/target/surefire-reports/

./report.sh




export RDIR=modules/testcases/v1/restApiEdgeTestsuite/test-configurations/target/surefire-reports/

./report.sh





export RDIR=modules/testcases/v1/securityApiTestsuite/test-configurations/target/surefire-reports/

./report.sh
