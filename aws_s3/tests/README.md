# Tests

This directory contains integration tests. The testing framework used is [pytest](https://docs.pytest.org/en/latest/). On a pull request, all tests in this directory are run. Files with tests must be prefixed with `test_` in order to be discovered. Test methods must also be prefixed with `test_`.

On a pull request, Jenkins will deploy all examples that have a corresponding test file in this directory. For example, if there is a file in this directory called `test_create_resource.py`, Jenkins will go into `examples/create_resource` and run `terraform apply`.  Each run's outputs are saved to a file with the same name as the example, i.e. `create_resource.json`. Integration tests will then be run. Finally, all the examples that were deployed will be destroyed.

# To run from developer work station

1. ensure you have the aws cli installed.
2. pip3 install pytest 
3. pip3 install boto3
5. perform an aws-cli authentication (see [aws-cli-saml](https://github.optum.com/CommercialCloud-EAC/python-scripts/tree/master/aws-cli-saml))
6. run the example(s) for the respective test(s) with a ```terraform init/plan/apply```, and the outputs of the example(s) with ```terraform output --json > example.json```
6. execute ```pytest test_example.py``` or ```pytest``` to run all pytests in the tests folder.
