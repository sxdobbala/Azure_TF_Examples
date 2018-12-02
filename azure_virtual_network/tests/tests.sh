#!/bin/bash
bash <(curl -s -N https://github.optum.com/raw/CommercialCloud-EAC/utility-scripts/master/test_fixtures/python/test_driver.txt ) -TestDir examples/three_tiered_network $@
