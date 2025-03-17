# RestApi Testsuite
### A TESTSUITE TO DEVELOP TESTS AGAINST INFINITE NUMBER OF SOFTWARE PROJECT VERSIONS


INNOVATION
-----------------------------------
The innovative part of RestApi Testsuite (Known as EAT for the JBOSS Servers) is creating the test once and testing with any version of the tested software. It may be firstly applied for the JBOSS Servers, but, in general, a similar structure, can be used for creating tests about any software with multiple versions or for multiple software programs that have a part of the testsuite in common.


Testing v1 RestApi Testsuite
---------------------------------------
1. Make sure that API_VERSION environment variable is set with the version of the API to test (e.g. 1.0.0). This is useful when we test multiple api versions. (Now it could be ommitted)
2. Build and run the RestApi Testsuite activating the v1 profile (mvn clean install -Dv1 -fae).
3. Different subcategories could be run separatelly :
	- mvn clean install -Dv1 -fae -Dmodule=authorApi
	- mvn clean install -Dv1 -fae -Dmodule=bookApi
	- mvn clean install -Dv1 -fae -Dmodule=happyPathApi
	- mvn clean install -Dv1 -fae -Dmodule=performanceApi
	- mvn clean install -Dv1 -fae -Dmodule=edgeApi
	- mvn clean install -Dv1 -fae -Dmodule=securityApi
	

Generate Reports
-----------------
From parent dir run : ./generatereports.sh (report images will also be generated in the parent dir)



Note : In the parent folder there is also a Netbeans version of the project. The code is the same. 


