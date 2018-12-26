# Tests

This directory contains integration tests for launchpad. The testing framework used is [pytest](https://docs.pytest.org/en/latest/). On a pull request, all tests in this directory are run. Files with tests must be prefixed with `test_` in order to be discovered. Test methods must also be prefixed with `test_`.

`test_tenants.yml` contains the subscription(s) that will have launchpad applied during a pull request. Test subscriptions should be limited to Commercial Cloud subscriptions.

Any dependencies needed to run the tests should be placed in `requirements.txt`.