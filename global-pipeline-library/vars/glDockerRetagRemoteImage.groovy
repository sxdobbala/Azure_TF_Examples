import com.optum.jenkins.pipeline.library.docker.Docker

/**
   * Pulls an image from the given Docker repository, changes the tags on it,
   * and pushes it back to the original repository with the new tag.
   * @param dockerCredentialsId String Required The string representing the id of the user/pass credential (this can be either a hash or a string you provide when creating a credential)
   * @param dockerHost String The Docker registry host - defaults to docker.optum.com
   * @param namespace String Required Name of the organization or user account that holds the repo
   * @param repository String Required The name of the repository that holds the tags you wish to delete
   * @param sourceTag String Required The image tag that will be converted
   * @param destNamespace String Optional field for specifying a different Docker namespace to retag an image to. Defaults to value of namespace otherwise
   * @param destRepo String Optional field for specifying a different Docker repository to retag an image to. Defaults to value of repository otherwise
   * @param destinationTag String Required The new image tag that will be pushed to Docker
   * */

def call(Map<String, Object> config){
  Docker docker = new Docker(this)
  docker.retagRemoteDockerImage(config)
}
