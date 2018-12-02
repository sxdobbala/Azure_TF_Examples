# Tests

This directory contains integration tests. The testing framework used is [pytest](https://docs.pytest.org/en/latest/). On a pull request, all tests in this directory are run. Files with tests must be prefixed with `test_` in order to be discovered. Test methods must also be prefixed with `test_`.

On a pull request, Jenkins will deploy all examples that have a corresponding test file in this directory. For example, if there is a file in this directory called `test_create_resource.py`, Jenkins will go into `examples/create_resource` and run `terraform apply`.  Each run's outputs are saved to a file with the same name as the example, i.e. `create_resource.json`. Integration tests will then be run. Finally, all the examples that were deployed will be destroyed.

# To run from developer work station

1. ensure you have the az cli installed. 
2. pip3 install pytest
3. pip3 install azure
4. pip3 install azure-cli-core
5. perform an az login
6. execute tests.sh -Setup -Test -Destroy (note that each script option can be used independently.)
  
When these tests are run from a developer work station via the tests.sh script the following log files will be created.

| Name | Description |
| --- | --- |
| tf.init.log | logfile from the terraform init command |
| tf.apply.log | logfile from the terraform apply command |
| tf.destroy.log | logfile from the terraform destroy command |