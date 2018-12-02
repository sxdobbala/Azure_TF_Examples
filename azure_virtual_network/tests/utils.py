import json
import pytest
import logging

# Helper functions used in testing.

LOGGER = logging.getLogger()


def parse_tfstate(path_to_state_file):
    """Reads the Terraform state file and returns a data
    structure with the outputs from the modules.
    The outputs from the Terraform run will be used in the integration tests.
    :param string path_to_state_file: path to Terraform state file
    """
    try:
        with open(path_to_state_file) as file:
            state = json.load(file)
        data = {}
        for module in state["modules"]:
            if len(module["path"]) == 1:
                joined = "root"
            else:
                module["path"].pop(0)
                joined = ".".join(module["path"])
            data[joined] = {}
            for key in module["outputs"]:
                data[joined][key] = module["outputs"][key]["value"]
        return data
    except Exception as exception:
        LOGGER.error(
            "Could not parse the Terraform state file: %s", str(exception))