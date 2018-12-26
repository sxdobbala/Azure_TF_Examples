#!/usr/bin/env python3
import boto3
import pytest
import terraformrunner
import json


# tests to make sure an aws account has the correct cloudtrail implemented by launchpad
class TestPTestCanRun:

    # test to confirm codebuild can run
    def test_can_run(self):
        print("runs")

