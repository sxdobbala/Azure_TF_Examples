# Unit Testing
The [Spock](http://spockframework.org/) framework is used for Unit Testing.

Existing tests are located within the  [/test](/test/groovy/com/optum/jenkins/pipeline/library) folder with a path matching that of [/src](/src/com/optum/jenkins/pipeline/library). Running, and reviewing, some of the unit tests in an IDE can be a good starting point to learn how to use Spock. The unit tests are designed so that they can be run without jenkins.

To help keep things organized, tests for each class are typically located in a corresponding specification file. For example, the Angular class has a corresponding file called AngularSpec which contains all the unit tests for that class. This is a general guideline and a good starting point - it is not a strict practice and multiple specification files for a class can be used where it makes sense. Keep the class name in the filename for the test specification to help keep the codebase readable and maintainable.

Unit tests are run automatically each time there is a pull request or merge to master. This occurs in the first stage of the GPL [Jenkinsfile](/Jenkinsfile) by calling "gradle test".  
The tests can also be run in an IDE prior to submitting a pull request. To get started will depend on the IDE - it revolves around importing gradle and using the build.gradle from the codebase. Once initially set up, running the tests from an IDE is straightforward and gives fast feedback for local testing.

## Coverage
All new classes should have an accompanying set of unit tests.

## Spock dependencies
testCompile in [build.gradle](build.gradle) contains the dependencies for the spockframework, cglib, and objennesis.

## Same test, different input data
Use the ["where"](http://spockframework.org/spock/docs/1.1/spock_primer.html) block when executing the same code with different inputs. Use the [Unroll](http://spockframework.org/spock/javadoc/1.1-rc-4/spock/lang/Unroll.html) annotation so that there is a result explicitly output for each input. There are examples in [DockerSpec](/test/groovy/com/optum/jenkins/pipeline/library/docker/DockerSpec.groovy).

## Jenkins Context
In general, the classes in the Global Pipeline Library make use of a jenkins context. This is used to access pipeline steps, environment variables and methods in /vars from within a class.  
The jenkins context must be created in the test specification to enable unit testing without the need for jenkins.

```
def jenkins = [
  env     : [NODEJS_TOOLS_DIR: 'toolsdir', NODEJS_VERSION: '1', NODEJS_HOME: 'nodejshome', PATH: 'path'],
  echo    : {},
  error   : { msg -> throw new JenkinsErrorException(msg) },
  command : { String cmd -> calledJenkinsCommand = cmd },
  withEnv : { java.util.ArrayList a, Closure c -> withEnvClosure = c }
]

Jenkins context with sample of stubbed pipeline steps and a stubbed method from /vars (i.e. command)
```
By using the syntax above, any environment variable, pipeline step implementation, and /var method implementation can be stubbed.

- jenkins.env: set jenkins environment variables
- jenkins.echo: required since class methods call it, can be left empty
- jenkins.error: if the code under test calls this, the actual error message used in the code is thrown, and is available to be checked.
- jenkins.command: with this implementation, when the code under test calls jenkins.command, then the value of the command passed in is written to "calledJenkinsCommand". This can be compared against known good command syntax to check if the command was formed correctly for the given inputs.
- jenkins.withEnv: with this implementation the closure that jenkins.withEnv is called with is available to the test via "withEnvClosure".

## Running unit tests locally
It is recommended to run unit tests locally prior to submitting a pull request. This provides fast feedback.

To run from the command line (gradle installed):
```
gradle test
```
When run from the command line, a test report can be found at:
build\reports\tests\test\index.html

Alternatively, the unit tests can be run from an IDE. For example, in one approach with IntelliJ, right click over the "test" folder and select "Run 'All Tests'".

