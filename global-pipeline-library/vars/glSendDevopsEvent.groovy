import com.optum.jenkins.pipeline.library.event.BuildEvent
import com.optum.jenkins.pipeline.library.event.SonarEvent
import com.optum.jenkins.pipeline.library.event.FortifyCloudEvent
import com.optum.jenkins.pipeline.library.event.FortifyLocalEvent
import com.optum.jenkins.pipeline.library.event.TestExecutionEvent
import com.optum.jenkins.pipeline.library.event.ComplianceCheckEvent
import com.optum.jenkins.pipeline.library.event.ApprovalEvent

def call(String eventType, Map<String, Object> config) {
  def eventTypes = [
    build:{ Object jenkins, Map props -> new BuildEvent(jenkins, props).send() },
    sonar:{ Object jenkins, Map props -> new SonarEvent(jenkins, props).send() },
    fortifylocal:{ Object jenkins, Map props -> new FortifyLocalEvent(jenkins, props).send() },
    fortifycloud:{ Object jenkins, Map props -> new FortifyCloudEvent(jenkins, props).send() },
    testexecution: {Object jenkins, Map props -> new TestExecutionEvent(jenkins, props).send() },
    complaincecheck: {Object jenkins, Map props -> new ComplianceCheckEvent(jenkins, props).send()},
    approval:{ Object jenkins, Map props -> new ApprovalEvent(jenkins, props).send() } ]

  def value = eventType.split('\\.').inject(eventTypes) { map, key -> map[key] }
  if (value in Closure) {
    value = value(this, config)
  } else {
    String evtTypeKeys = ''
    eventTypes.each { evtTypeKeys += " - ${it.getKey()}\n" }
    error "'$eventType' is not a valid event type for library function 'glSendDevopsEvent'!\nValid event types:\n$evtTypeKeys"
  }
  return value
}
