# Tests

This directory contains integration tests. The testing framework used is [pytest](https://docs.pytest.org/en/latest/). On a pull request, all tests in this directory are run. Files with tests must be prefixed with `test_` in order to be discovered by pytest. Test methods must also be prefixed with `test_`.

On a pull request, Jenkins will deploy all the examples that have a corresponding test file in this directory. For e.g., if there is a file in this directory called `test_create_resource.py`, Jenkins will go into `examples/create_resource` and run `terraform apply`.  Each run's outputs are saved to a file with the same name as the example directory name, i.e., `create_resource.json`. Integration tests will then be run. Finally, all the examples that were deployed will be destroyed.

## How to run the tests from a developer's work station

1. Ensure that you have the aws cli installed.
2. Run this command: `pip3 install -r requirements.txt`
3. Perform an aws-cli authentication (see [aws-cli-saml](https://github.optum.com/CommercialCloud-EAC/python-scripts/tree/master/aws-cli-saml))
4. Run the example(s) for the respective test(s) by executing `terraform init/plan/apply` *within that directory* and capture the outputs of the example(s) with `terraform output -json > <directory_name>.json`
5. Switch to the `tests` directory and execute `pytest test_<directory_name>.py` or `pytest` to run all pytests in the tests folder.