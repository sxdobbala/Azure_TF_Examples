#!/bin/sh

# Assigns policy initiatives

PROGNAME=$0
RC=0
sec=""

# Policy Umbrella (non-critical)

sec="Assign policy initiative"
az policy assignment create --display-name "Policy Umbrella Initiative" --name "umbrella_policy_initiative_assignment" \
	--policy-set-definition "umbrella_policy_initiative"

RC=$?
if (($RC != 0))
	then
		echo Program $PROGNAME failed, step $sec
	fi

# Critical Policy Umbrella

sec="Assign critical policy initiative"
az policy assignment create --display-name "Critical Policy Umbrella Initiative" --name "critical_umbrella_policy_initiative_assignment" \
	--policy-set-definition "critical_umbrella_policy_initiative"

RC=$?
if (($RC != 0))
	then
		echo Program $PROGNAME failed, step $sec
	fi