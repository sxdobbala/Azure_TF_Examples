#!/usr/bin/env python3
import boto3
import pytest
import terraformrunner
import json


class TestPSpecCanRun:

    # test to confirm codebuild can run
    def test_can_run(self):
        print("runs")

