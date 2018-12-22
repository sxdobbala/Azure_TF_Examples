package com.optum.jenkins.pipeline.library.event

/**
 * Types of tests that can be reported by TestEvent.
 */
enum TestType {

  /** Unit tests test a single class or method, with mocked dependencies. */
  UNIT,

  /**
   * Component tests test a single component (think microservice), while mocking dependent components. For example,
   * given microservices A and B, A depends on B via REST calls. The component test for A would run the full A
   * microservice, and it would interact with a mocked B service using REST (think Wiremock).
   */
  COMPONENT,

  /**
   * Integration tests test interaction between two services. For example, given microservices A and B, A depends on B
   * via REST calls. The integration test will run both services and test scenarios where the two services interact.
   */
  INTEGRATION,

  /**
   * End-to-end tests test realistic data flows through an entire integrated system.
   */
  END_TO_END,

  /**
   * Performance tests run various load scenarios against a system and measure the system's responsiveness.
   */
  PERFORMANCE

}
