package com.optum.jenkins.pipeline.library.docker

import com.optum.jenkins.pipeline.library.JenkinsErrorException
import spock.lang.Specification
import spock.lang.Unroll

class DockerSpec extends Specification {

  def "jenkins context is available"(){
    given: "Default jenkins context"
    def jenkins = [echo : 'hello']
    when: 'Creating class with jenkins context'
    def docker = new Docker(jenkins)
    then: "Jenkins context is available"
    docker.getJenkins() == jenkins
  }

  def "error for missing jenkins context"(){
    when: 'Creating class without jenkins context'
    def docker = new Docker()
    then: "Exception is thrown"
    def e = thrown(Exception)
    e.message.contains('`this` must be passed when creating new class')
  }

  def "buildPushDockerImage no error for correct parameters"() {
    given: 'Jenkins mocked for needed values'
    def jenkins = [
      env             : [],
      echo            : {},
      error           : { msg -> throw new JenkinsErrorException(msg) },
      command         : { String cmd -> 'nop' },
      withCredentials : { java.util.ArrayList a, Closure c -> 'nop'}
    ]
    when: "I build, tag, and push image to registry using minimum required parameters"
    def docker = new Docker(jenkins)
    def dockerConfig = [
      tag: "docker tag",
      dockerCredentialsId: "credentials"
    ]
    docker.buildPushDockerImage(dockerConfig)
    then: "no exception is thrown"
    noExceptionThrown()
  }

  def "buildPushDockerImage withCredentials map structured correctly"() {
    given: 'Jenkins mocked for needed values'
    def withCredentialsMap
    def jenkins = [
      env     : [],
      echo    : {},
      error   : { msg -> throw new JenkinsErrorException(msg) },
      command : { String cmd -> 'nop' },
      withCredentials : { java.util.ArrayList a, Closure c -> withCredentialsMap = a }
    ]
    def expectedWithCredentialsMap = "[[\$class:UsernamePasswordMultiBinding, credentialsId:credentials, usernameVariable:DOCKER_USER, passwordVariable:DOCKER_PASS]]"
    when: "I build, tag, and push image"
    def docker = new Docker(jenkins)
    def dockerConfig = [
      tag: "docker tag",
      dockerCredentialsId: "credentials"
    ]
    docker.buildPushDockerImage(dockerConfig)
    then: "The withCredentials map is structured correctly"
    withCredentialsMap.toString() == expectedWithCredentialsMap
  }

  @Unroll
  def "buildPushDockerImage error for missing required parameter '#missingParam'"() {
    given: 'Jenkins mocked for needed values'
    def jenkins = [
      env             : [],
      echo            : {},
      error           : { msg -> throw new JenkinsErrorException(msg) },
      command         : { String cmd -> 'nop' },
      withCredentials : { java.util.ArrayList a, Closure c -> 'nop' }
    ]
    when: "I build, tag, and push image to registry without required parameter"
    def docker = new Docker(jenkins)
    docker.buildPushDockerImage(dockerConfig)
    then: "Exception is thrown"
    JenkinsErrorException e = thrown()
    e.message.contains(errorMessage)
    where:
    dockerConfig                                 | missingParam          || errorMessage
    [tag: "tag"]                                 | "dockerCredentialsId" || "Required parameters missing: dockerCredentialsId"
    [dockerCredentialsId: "credentials"]         | "tag"                 || "Required parameters missing: tag"
    [tag: "tag", dockerHost: "docker.optum.com"] | "dockerCredentialsId" || "Required parameters missing: dockerCredentialsId"
    [:]                                          | "all"                 || "Required parameters missing: dockerCredentialsId,tag"
  }

  def "buildDockerImage no error for correct parameters"() {
    given: 'Jenkins mocked for needed values'
    def jenkins = [
      env     : [],
      echo    : {},
      error   : { msg -> throw new RuntimeException(msg) },
      command : { String cmd -> 'nop' }
    ]
    when: "I build image using minimum required parameters"
    def docker = new Docker(jenkins)
    def dockerConfig = [
      tag: "tag"
    ]
    docker.buildDockerImage(dockerConfig)
    then: "no exception is thrown"
    noExceptionThrown()
  }

  def "buildDockerImage docker command is structured correctly with pull"() {
    given: 'Jenkins mocked for needed values'
    def calledJenkinsCommand
    def jenkins = [
      env     : [],
      echo    : {},
      error   : { msg -> throw new JenkinsErrorException(msg) },
      command : { String cmd -> calledJenkinsCommand = cmd }
    ]
    def expectedCommand = "docker  build --pull --tag       \"tag\"  . "
    when: "I build docker image"
    def docker = new Docker(jenkins)
    def dockerConfig = [
      tag: "tag",
      requirePull: true
    ]
    docker.buildDockerImage(dockerConfig)
    then: "The docker command is structured correctly"
    calledJenkinsCommand.toString() == expectedCommand
  }

  def "buildDockerImage docker command is structured correctly without pull"() {
    given: 'Jenkins mocked for needed values'
    def calledJenkinsCommand
    def jenkins = [
      env     : [],
      echo    : {},
      error   : { msg -> throw new JenkinsErrorException(msg) },
      command : { String cmd -> calledJenkinsCommand = cmd }
    ]
    def expectedCommand = "docker  build  --tag       \"tag\"  . "
    when: "I build docker image"
    def docker = new Docker(jenkins)
    def dockerConfig = [
      tag: "tag",
      requirePull: false
    ]
    docker.buildDockerImage(dockerConfig)
    then: "The docker command is structured correctly"
    calledJenkinsCommand.toString() == expectedCommand
  }

  def "tagDockerImage no error for correct parameters"() {
    given: 'Jenkins mocked for needed values'
    def jenkins = [
      env     : [],
      echo    : {},
      error   : { msg -> throw new JenkinsErrorException(msg) },
      command : { String cmd -> 'nop' }
    ]
    when: "I tag docker image using minimum required parameters"
    def docker = new Docker(jenkins)
    def dockerConfig = [
      sourceTag: "source tag",
      destTag: "dest tag"
    ]
    docker.tagDockerImage(dockerConfig)
    then: "no exception is thrown"
    noExceptionThrown()
  }

  def "tagDockerImage docker command is structured correctly"() {
    given: 'Jenkins mocked for needed values'
    def calledJenkinsCommand
    def jenkins = [
      env     : [],
      echo    : {},
      error   : { msg -> throw new JenkinsErrorException(msg) },
      command : { String cmd -> calledJenkinsCommand = cmd }
    ]
    def expectedDockerCmd = "docker  tag \"source tag\" \"dest tag\""
    when: "I tag docker image"
    def docker = new Docker(jenkins)
    def dockerConfig = [
      sourceTag: "source tag",
      destTag: "dest tag"
    ]
    docker.tagDockerImage(dockerConfig)
    then: "The docker command is structured correctly"
    calledJenkinsCommand.toString() == expectedDockerCmd
  }

  @Unroll
  def "tagDockerImage error for missing required parameter '#missingParam'"() {
    given: 'Jenkins mocked for needed values'
    def jenkins = [
      env     : [],
      echo    : {},
      error   : { msg -> throw new JenkinsErrorException(msg) },
      command : { String cmd -> 'nop' }
    ]
    when: "I tag docker image without required parameter"
    def docker = new Docker(jenkins)
    docker.tagDockerImage(dockerConfig)
    then: "Exception is thrown"
    JenkinsErrorException e = thrown()
    e.message.contains(errorMessage)
    where:
    dockerConfig                                        | missingParam  || errorMessage
    [sourceTag: "source tag"]                           | "destTag"     || "Required parameters missing: destTag"
    [destTag: "dest tag"]                               | "sourceTag"   || "Required parameters missing: sourceTag"
    [sourceTag: "source tag", extraFlagOptions: "flag"] | "destTag"     || "Required parameters missing: destTag"
    [:]                                                 | "all"         || "Required parameters missing: destTag,sourceTag"
  }

  def "pushDockerImage no error for correct parameters"() {
    given: 'Jenkins mocked for needed values'
    def jenkins = [
      env             : [],
      echo            : {},
      error           : { msg -> throw new JenkinsErrorException(msg) },
      command         : { String cmd -> 'nop' },
      withCredentials : { java.util.ArrayList a, Closure c -> 'nop'}
    ]
    when: "I push docker image using minimum required parameters"
    def docker = new Docker(jenkins)
    def dockerConfig = [
      tag: "docker tag",
      dockerCredentialsId: "credentials"
    ]
    docker.pushDockerImage(dockerConfig)
    then: "no exception is thrown"
    noExceptionThrown()
  }

  def "pushDockerImage withCredentials map structured correctly"() {
    given: 'Jenkins mocked for needed values'
    def withCredentialsMap
    def jenkins = [
      env     : [],
      echo    : {},
      error   : { msg -> throw new JenkinsErrorException(msg) },
      command : { String cmd -> 'command reply' },
      withCredentials : { java.util.ArrayList a, Closure c -> withCredentialsMap = a}
    ]
    def expectedWithCredentialsMap = "[[\$class:UsernamePasswordMultiBinding, credentialsId:credentials, usernameVariable:DOCKER_USER, passwordVariable:DOCKER_PASS]]"
    when: "I push docker image"
    def docker = new Docker(jenkins)
    def dockerConfig = [
      tag: "docker tag",
      dockerCredentialsId: "credentials"
    ]
    docker.pushDockerImage(dockerConfig)
    then: "The withCredentials map is structured correctly"
    withCredentialsMap.toString() == expectedWithCredentialsMap
  }

  @Unroll
  def "pushDockerImage error for missing required parameter '#missingParam'"() {
    given: 'Jenkins mocked for needed values'
    def jenkins = [
      env             : [],
      echo            : {},
      error           : { msg -> throw new JenkinsErrorException(msg) },
      withCredentials : { java.util.ArrayList a, Closure c -> 'nop'}
    ]
    when: "I push docker image without required parameter"
    def docker = new Docker(jenkins)
    docker.pushDockerImage(dockerConfig)
    then: "Exception is thrown"
    JenkinsErrorException e = thrown()
    e.message.contains(errorMessage)
    where:
    dockerConfig                         | missingParam           || errorMessage
    [tag: "tag"]                         | "dockerCredentialsId"  || "Required parameters missing: dockerCredentialsId"
    [dockerCredentialsId: "credentials"] | "tag"                  || "Required parameters missing: tag"
    [:]                                  | "all"                  || "Required parameters missing: dockerCredentialsId,tag"
  }

  def "pullDockerImage no error for correct parameters"() {
    given: 'Jenkins mocked for needed values'
    def jenkins = [
      command         : { String cmd -> 'nop' },
      env             : [],
      echo            : {},
      error           : { msg -> throw new JenkinsErrorException(msg) },
      withCredentials : { java.util.ArrayList a, Closure c -> 'nop'}
    ]
    when: "I pull docker image using minimum required parameters"
    def docker = new Docker(jenkins)
    def dockerConfig = [
      tag: "docker tag",
      dockerCredentialsId: "credentials"
    ]
    docker.pullDockerImage(dockerConfig)
    then: "no exception is thrown"
    noExceptionThrown()
  }

  def "pullDockerImage withCredentials map structured correctly"() {
    given: 'Jenkins mocked for needed values'
    def withCredentialsMap
    def jenkins = [
      env     : [],
      echo    : {},
      error   : { msg -> throw new JenkinsErrorException(msg) },
      command : { String cmd -> 'command reply' },
      withCredentials : { java.util.ArrayList a, Closure c -> withCredentialsMap = a}
    ]
    def expectedWithCredentialsMap = "[[\$class:UsernamePasswordMultiBinding, credentialsId:credentials, usernameVariable:DOCKER_USER, passwordVariable:DOCKER_PASS]]"
    when: "I pull docker image"
    def docker = new Docker(jenkins)
    def dockerConfig = [
      tag: "docker tag",
      dockerCredentialsId: "credentials"
    ]
    docker.pullDockerImage(dockerConfig)
    then: "The withCredentials map is structured correctly"
    withCredentialsMap.toString() == expectedWithCredentialsMap
  }

  @Unroll
  def "pullDockerImage error for missing required parameter '#missingParam'"() {
    given: 'Jenkins mocked for needed values'
    def jenkins = [
      env             : [],
      echo            : {},
      error           : { msg -> throw new JenkinsErrorException(msg) },
      withCredentials : { java.util.ArrayList a, Closure c -> 'nop'}
    ]
    when: "I pull docker image without required parameter"
    def docker = new Docker(jenkins)
    docker.pullDockerImage(dockerConfig)
    then: "Exception is thrown"
    JenkinsErrorException e = thrown()
    e.message.contains(errorMessage)
    where:
    dockerConfig                         | missingParam           || errorMessage
    [tag: "tag"]                         | "dockerCredentialsId"  || "Required parameters missing: dockerCredentialsId"
    [dockerCredentialsId: "credentials"] | "tag"                  || "Required parameters missing: tag"
    [:]                                  | "all"                  || "Required parameters missing: dockerCredentialsId,tag"
  }

  def "createDockerRepository no error for correct parameters"() {
    given: 'Jenkins mocked for needed values'
    def jenkins = [
      env             : [],
      echo            : {},
      error           : { msg -> throw new JenkinsErrorException(msg) },
      withCredentials : { java.util.ArrayList a, Closure c -> 'nop'}
    ]
    when: "I create a docker repository using minimum required parameters"
    def docker = new Docker(jenkins)
    def dockerConfig = [
      dockerCredentialsId: "credentials",
      namespace: "namespace",
      repository: "repo name"
    ]
    docker.createDockerRepository(dockerConfig)
    then: "no exception is thrown"
    noExceptionThrown()
  }

  def "createDockerRepository withCredentials map structured correctly"() {
    given: 'Jenkins mocked for needed values'
    def withCredentialsMap
    def jenkins = [
      env     : [],
      echo    : {},
      error   : { msg -> throw new JenkinsErrorException(msg) },
      command : { String cmd -> 'command reply' },
      withCredentials : { java.util.ArrayList a, Closure c -> withCredentialsMap = a}
    ]
    def expectedWithCredentialsMap = "[[\$class:UsernamePasswordMultiBinding, credentialsId:credentials, usernameVariable:DOCKER_USER, passwordVariable:DOCKER_PASS]]"
    when: "I create a docker repository"
    def docker = new Docker(jenkins)
    def dockerConfig = [
      dockerCredentialsId: "credentials",
      namespace: "namespace",
      repository: "repo name"
    ]
    docker.createDockerRepository(dockerConfig)
    then: "The withCredentials map is structured correctly"
    withCredentialsMap.toString() == expectedWithCredentialsMap
  }

  def "createDockerRepository withCredentials closure command structured correctly"() {
    given: 'Jenkins mocked for needed values'
    def withCredentialsClosure
    def checkRepoCmd = "curl --retry 5 --silent --insecure --user 'user':'pass' --header 'Accept: application/json' -X GET https://docker.optum.com/api/v0/repositories/namespace/repo name"
    def cmdResponse
    def jenkins = [
      env     : [DOCKER_USER: 'user', DOCKER_PASS: 'pass'],
      echo    : {},
      error   : { msg -> throw new JenkinsErrorException(msg) },
      withCredentials : { java.util.ArrayList a, Closure c -> withCredentialsClosure = c }
    ]
    def expectedWithCredentialsClosureCmd = "curl  -v  --user 'user':'pass' --insecure -X POST --data '{\"name\":\"repo name\",     \"enableManifestLists\": true,     \"immutableTags\": false,     \"longDescription\": \"This was generated from the Jenkins Pipeline as Code global library\",    \"scanOnPush\": false,     \"shortDescription\": \"JPaC generated repo\",     \"visibility\": \"public\"}' --header \"Content-type: application/json\" https://docker.optum.com/api/v0/repositories/namespace"
    def expectedOutputMsg = "Docker repository namespace/repo name already exists, skipping creation"
    def expectedErrorMsg = "Could not determine docker repository exist. This is the response from docker registry <html>504 Gateway Timeout</html>, please check if docker registry is online and operational"
    when: "I create a docker repository"
    def docker = new Docker(jenkins)
    def dockerConfig = [
      dockerCredentialsId: "credentials",
      namespace: "namespace",
      repository: "repo name"
    ]
    docker.createDockerRepository(dockerConfig)

    and: "docker repository check verified it does not exists"
    def apiResponse = '{"errors": [{"code": "NO_SUCH_REPOSITORY"}]}'
    def jsonObject = ["errors":["code":"NO_SUCH_REPOSITORY"]]
    jenkins.command = { String cmd, Boolean stdOut -> cmdResponse = (cmd == checkRepoCmd && stdOut) ? apiResponse : cmd }
    jenkins.readJSON = { def text -> jsonObject }

    then: "The withCredentials closure command is structured correctly"
    def expectedResponse = withCredentialsClosure.call()
    //white spaces shouldn't matter for comparison. the code may be indented differently
    expectedResponse.replaceAll("\\s+"," ") == expectedWithCredentialsClosureCmd.replaceAll("\\s+"," ")

    when: "I create a docker repository"
    docker.createDockerRepository(dockerConfig)

    and: "docker repository check verified it already exists"
    apiResponse = '{"id":"repoid"}'
    jsonObject = ["id":"repod"]
    def echoMsg = "Docker repository ${dockerConfig.namespace}/${dockerConfig.repository} already exists, skipping creation"
    jenkins.command = { String cmd, Boolean stdOut -> cmdResponse = (cmd == checkRepoCmd && stdOut) ? apiResponse : cmd }
    jenkins.readJSON = { def text -> jsonObject }
    jenkins.echo = { msg -> echoMsg }

    then: "The withCredentials closure command is structured correctly and show output message"
    withCredentialsClosure.call() == expectedOutputMsg

    when: "I create a docker repository"
    docker.createDockerRepository(dockerConfig)

    and: "docker registry is unreachable"
    apiResponse = '<html>504 Gateway Timeout</html>'
    jenkins.command = { String cmd, Boolean stdOut -> cmdResponse = (cmd == checkRepoCmd && stdOut) ? apiResponse : cmd }
    withCredentialsClosure.call()

    then: "The withCredentials closure command is structured correctly and exception thrown"
    JenkinsErrorException e = thrown()
    e.message.contains(expectedErrorMsg)
  }

  @Unroll
  def "createDockerRepository error for missing required parameter '#missingParam'"() {
    given: 'Jenkins mocked for needed values'
    def jenkins = [
      env             : [],
      echo            : {},
      error           : { msg -> throw new JenkinsErrorException(msg) },
      withCredentials : { java.util.ArrayList a, Closure c -> 'nop'}
    ]
    when: "I create docker repository without required parameter"
    def docker = new Docker(jenkins)
    docker.createDockerRepository(dockerConfig)
    then: "Exception is thrown"
    JenkinsErrorException e = thrown()
    e.message.contains(errorMessage)
    where:
    dockerConfig                                                 | missingParam                      || errorMessage
    [dockerCredentialsId: "credentials"]                         | "namespace, repository"           || 'Required parameters missing: namespace,repository'
    [namespace: "namespace"]                                     | "dockerCredentialsId, repository" || 'Required parameters missing: dockerCredentialsId,repository'
    [repository: "repo name"]                                    | "dockerCredentialsId, namespace"  || 'Required parameters missing: dockerCredentialsId,namespace'
    [dockerCredentialsId: "credentials", namespace: "namespace"] | "repository"                      || 'Required parameters missing: repository'
    [:]                                                          | "all"                             || 'Required parameters missing: dockerCredentialsId,namespace,repository'
  }

  def "deleteTagsByPattern no error for correct parameters"() {
    given: 'Jenkins mocked for needed values'
    def jenkins = [
      env     : [],
      echo    : {},
      error   : { msg -> throw new JenkinsErrorException(msg) },
      withEnv : { java.util.ArrayList a, Closure c -> 'nop'}
    ]
    when: "I delete tags by pattern using minimum required parameters"
    def docker = new Docker(jenkins)
    def dockerConfig = [
      dockerCredentialsId: "credentials",
      namespace: "namespace",
      repository: "repo name"
    ]
    docker.deleteTagsByPattern(dockerConfig)
    then: "no exception is thrown"
    noExceptionThrown()
  }

  def "deleteTagsByPattern withEnv map structured correctly"() {
    given: 'Jenkins mocked for needed values'
    def withEnvMap
    def jenkins = [
      env     : [],
      echo    : {},
      error   : { msg -> throw new JenkinsErrorException(msg) },
      withEnv : { java.util.ArrayList a, Closure c -> withEnvMap = a}
    ]
    def expectedWithEnvMap = "[DOCKER_HOST=docker.optum.com, NUM_TAGS=10, TAG_REGEX=^[0-9]+\\.[0-9]+\\.[0-9]+\\.[0-9]+\$, VERBOSE=true, DRY_RUN=true, NAMESPACE=namespace, REPO=repo name, SORT_FLAGS=-t. -k 1,1nr -k 2,2nr -k 3,3nr -k 4,4nr]"
    when: "I delete tags by pattern"
    def docker = new Docker(jenkins)
    def dockerConfig = [
      dockerCredentialsId: "credentials",
      namespace: "namespace",
      repository: "repo name"
    ]
    docker.deleteTagsByPattern(dockerConfig)
    then: "The withEnv map is structured correctly"
    withEnvMap.toString() == expectedWithEnvMap
  }

  def "deleteTagsByPattern withEnv withCredential closure command structured correctly"() {
    given: 'Jenkins mocked for needed values'
    def calledJenkinsCommand
    def withCredentialsClosure
    def withEnvClosure
    def withCredClosureObj
    def jenkins = [
      env     : [],
      echo    : {},
      error   : { msg -> throw new JenkinsErrorException(msg) },
      command : { String cmd -> calledJenkinsCommand = cmd },
      usernamePassword: {java.util.LinkedHashMap},
      withCredentials : { java.util.ArrayList a, Closure c -> withCredentialsClosure = c },
      withEnv : { java.util.ArrayList a, Closure c -> withEnvClosure = c }
    ]
    def expectedWithEnvClosureCmd = '''\n        curl -s -u ${DOCKER_USER}:${DOCKER_PASS} -k --header "Accept: application/json" -X GET -K - \\\n        "https://$DOCKER_HOST/api/v0/repositories/$NAMESPACE/$REPO/tags?pageSize=10000" | jq -r ".[] | .name" > tags.txt\n\n        # The grep gets just those tags that pass the tagRegex\n        cat tags.txt | { egrep "${TAG_REGEX}" || echo ''; } > trimmed_tags.txt\n\n        #Sorting based on number within delimiter\n        #the _default_ sort sets . as the delimiter and sorts by each of the 4th sections of number\n        #the awk only prints the ones after the first ${NUM_TAGS} lines - this gives me all tags EXCEPT for\n        #the ${NUM_TAGS} most recent that are named like #.#.#.#\n        cat trimmed_tags.txt | sort ${SORT_FLAGS} | awk "FNR >${NUM_TAGS}" > final_tags.txt\n        delete_count=`cat final_tags.txt | wc -l`\n\n        [ "$VERBOSE" = "true" ] && echo "Tags to delete: $delete_count
TAG LIST:"; cat final_tags.txt\n        #Delete the tags in this list\n        if [ "$DRY_RUN" = "false" ]; then\n        while read tag; do\n        [ "$VERBOSE" = "true" ] && echo "Deleting tag $tag"\n        [ "$DRY_RUN" = "false" ] && curl -s -u ${DOCKER_USER}:${DOCKER_PASS} -k --header "Accept: application/json" -X DELETE -K - \\\n         "https://$DOCKER_HOST/api/v0/repositories/$NAMESPACE/$REPO/tags/$tag"\n        done < final_tags.txt\n        fi\n      '''
    when: "I delete tags by pattern"
    def docker = new Docker(jenkins)
    def dockerConfig = [
      dockerCredentialsId: "credentials",
      namespace: "namespace",
      repository: "repo name",
      dockerHost: "docker.optum.com"
    ]
    docker.deleteTagsByPattern(dockerConfig)
    withCredClosureObj = withEnvClosure.call()
    then: "The withEnv withCredential closure command is structured correctly"
    withCredClosureObj.call() == expectedWithEnvClosureCmd
  }

  @Unroll
  def "deleteTagsByPattern error for missing required parameter '#missingParam'"() {
    given: 'Jenkins mocked for needed values'
    def jenkins = [
      env     : [],
      echo    : {},
      error   : { msg -> throw new JenkinsErrorException(msg) },
      withEnv : { java.util.ArrayList a, Closure c -> 'nop'}
    ]
    when: "I delete tags by pattern without required parameter"
    def docker = new Docker(jenkins)
    docker.deleteTagsByPattern(dockerConfig)
    then: "Exception is thrown"
    JenkinsErrorException e = thrown()
    e.message.contains(errorMessage)
    where:
    dockerConfig                                                 | missingParam                      || errorMessage
    [dockerCredentialsId: "credentials"]                         | "namespace, repository"           || 'Required parameters missing: namespace,repository'
    [namespace: "namespace"]                                     | "dockerCredentialsId, repository" || 'Required parameters missing: dockerCredentialsId,repository'
    [repository: "repo name"]                                    | "dockerCredentialsId, namespace"  || 'Required parameters missing: dockerCredentialsId,namespace'
    [dockerCredentialsId: "credentials", namespace: "namespace"] | "repository"                      || 'Required parameters missing: repository'
    [:]                                                          | "all"                             || 'Required parameters missing: dockerCredentialsId,namespace,repository'
  }

  @Unroll
  def "dockerLogin command structured correctly '#testName'"() {
    given: 'Jenkins mocked for needed values'
    def calledJenkinsCommand
    def commandCalled = ''
    def withCredentialsClosure
    def jenkins = [
      env     : [DOCKER_USER: 'user', DOCKER_PASS: 'pass'],
      echo    : {},
      error   : { msg -> throw new JenkinsErrorException(msg) },
      command : { String cmd -> calledJenkinsCommand = cmd; commandCalled = cmd },
      withCredentials : { java.util.ArrayList a, Closure c -> withCredentialsClosure = c }
    ]
    when: "I log in to a Docker engine"
    def docker = new Docker(jenkins)
    docker.dockerLogin(dockerConfig)
    withCredentialsClosure.call()
    then: "The login command uses the correct structure based on Docker cli version"
    commandCalled.contains(expectedCmd)
    where:
    // Test for positive, negative and outlier scenarios
    testName        | dockerConfig                                                | expectedCmd
    'default'       | [dockerCredentialsId: "credentials"]                        | "docker login --username user --password 'pass' docker.optum.com"
    'docker=blank'  | [dockerCredentialsId: "credentials",dockerVersion:'']       | "docker login --username user --password 'pass' docker.optum.com"
    'docker=null'   | [dockerCredentialsId: "credentials",dockerVersion:null]     | "docker login --username user --password 'pass' docker.optum.com"
    'docker=ab.cd'  | [dockerCredentialsId: "credentials",dockerVersion:'ab.cd']  | "docker login --username user --password 'pass' docker.optum.com"
    'docker=16'     | [dockerCredentialsId: "credentials",dockerVersion:'16']     | "docker login --username user --password 'pass' docker.optum.com"
    'docker<17.09'  | [dockerCredentialsId: "credentials",dockerVersion: "17.08"] | "docker login --username user --password 'pass' docker.optum.com"
    'docker>=17.09' | [dockerCredentialsId: "credentials",dockerVersion: "17.09"] | "echo -n 'pass' | docker login --username user --password-stdin docker.optum.com"
  }

  def "withDockerLogin doesn't login if no credentials given"() {
    given: 'Jenkins mocked for needed values'
    def withCredentialsMap
    def jenkins = [
      env     : [],
      echo    : {},
      error   : { msg -> throw new JenkinsErrorException(msg) },
      command : { msg -> throw new JenkinsErrorException(msg) },
      usernamePassword: {java.util.LinkedHashMap},
      withCredentials : { java.util.ArrayList a, Closure c -> withCredentialsMap = a}
    ]
    when: "I call the withDockerLogin method without dockerCredentialsId"
    def docker = new Docker(jenkins)
    def dockerConfig = [
      namespace: "organization",
      repository: "repo",
      sourceTag: "source tag",
      destTag: "dest tag"
    ]
    docker.withDockerLogin dockerConfig, {
      //Blank closure to run
    }
    then: "No attempt at login is made"
    noExceptionThrown()
  }

  def "withDockerLogin attempts docker login when dockerCredentialsId is present"() {
    given: 'Jenkins mocked for needed values'
    def withCredentialsMap
    def jenkins = [
      env     : [],
      echo    : {},
      error   : { msg -> throw new JenkinsErrorException(msg) },
      command : { msg -> throw new JenkinsErrorException(msg) },
      usernamePassword: {java.util.LinkedHashMap},
      withCredentials : { java.util.ArrayList a, Closure c -> withCredentialsMap = a}
    ]
    when: "I call the withDockerLogin method passing dockerCredentialsId"
    def docker = new Docker(jenkins)
    def dockerConfig = [
      dockerCredentialsId: "some credential id",
      namespace: "organization",
      repository: "repo",
      sourceTag: "source tag",
      destTag: "dest tag"
    ]
    docker.withDockerLogin dockerConfig, {
      //Blank closure to run
    }
    then: "Attempt at login/out is made"
    JenkinsErrorException e = thrown()
    e.message.contains("docker logout")
  }

  def "retagRemoteDockerImage no error for correct parameters"() {
    given: 'Jenkins mocked for needed values'
    def withCredentialsMap
    def jenkins = [
            env     : [],
            echo    : {},
            error   : { msg -> throw new JenkinsErrorException(msg) },
            command : { String cmd -> 'nop' },
            usernamePassword: {java.util.LinkedHashMap},
            withCredentials : { java.util.ArrayList a, Closure c -> withCredentialsMap = a}
    ]
    when: "I retag a remote docker image using minimum required parameters"
    def docker = new Docker(jenkins)
    def dockerConfig = [
            dockerCredentialsId: "credentials",
            namespace: "organization",
            repository: "repo",
            sourceTag: "source tag",
            destTag: "dest tag"
    ]
    docker.retagRemoteDockerImage(dockerConfig)
    then: "no exception is thrown"
    noExceptionThrown()
  }

  @Unroll
  def "retagRemoteDockerImage error for missing required parameter '#missingParam'"() {
    given: 'Jenkins mocked for needed values'
    def jenkins = [
            env     : [],
            echo    : {},
            error   : { msg -> throw new JenkinsErrorException(msg) },
            command : { String cmd -> 'nop' },
            withCredentials : { java.util.ArrayList a, Closure c -> 'nop'}
    ]
    when: "I retag a remote docker image without required parameter(s)"
    def docker = new Docker(jenkins)
    docker.retagRemoteDockerImage(dockerConfig)
    then: "Exception is thrown"
    JenkinsErrorException e = thrown()
    e.message.contains(errorMessage)
    where:
    dockerConfig                                                                    | missingParam                                            || errorMessage
    [dockerCredentialsId: "credentials"]                                            | "namespace, repository, sourceTag, destTag"             || 'Required parameters missing: destTag,namespace,repository,sourceTag'
    [sourceTag: "source tag"]                                                       | "dockerCredentialsId, namespace, repository, destTag"   || 'Required parameters missing: destTag,dockerCredentialsId,namespace,repository'
    [destTag: "dest tag"]                                                           | "dockerCredentialsId, namespace, repository, sourceTag" || 'Required parameters missing: dockerCredentialsId,namespace,repository,sourceTag'
    [sourceTag: "source tag", destTag: "dest tag"]                                  | "dockerCredentialsId, namespace, repository"            || 'Required parameters missing: dockerCredentialsId,namespace,repository'
    [dockerCredentialsId: "creds", namespace: "n"]                                  | "repository, sourceTag, destTag"                        || 'Required parameters missing: destTag,repository,sourceTag'
    [dockerCredentialsId: "creds", namespace: "n", repository: "r"]                 | "sourceTag, destTag"                                    || 'Required parameters missing: destTag,sourceTag'
    [dockerCredentialsId: "creds", namespace: "n", repository: "r", sourceTag: "s"] | "destTag"                                               || 'Required parameters missing: destTag'
    [:]                                                                             | "all"                                                   || 'Required parameters missing: destTag,dockerCredentialsId,namespace,repository,sourceTag'
  }
}
