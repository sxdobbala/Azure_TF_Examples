package com.optum.config

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
class Contrast implements Serializable
{
    String contrastProfile
    String applicationName
    Integer vulnerabilityCount
    String vulnerabilitySeverity
}