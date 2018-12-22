import com.optum.jenkins.pipeline.library.docker.Docker

/**
 * Deletes tags from Docker Trusted Registry based on their names. Uses tagRegex value
 * to find a list of tags to delete from and then deletes the 'oldest' numTags tags based on the delimited numbers.
 * Default pattern expects for tags is #.#.#.#.
 * @param dockerCredentialsId String Required The string representing the id of the user/pass credential (this can be either a hash or a string you provide when creating a credential)
 * @param dockerHost String The Docker registry host - defaults to docker.optum.com
 * @param dryRun boolean Whether or not to actually perform deletes - defaults to true
 * @param namespace String Required name of the organization or user account that holds the repo
 * @param numTagsRetained int The number of tags that match tagRegex to EXCLUDE from deletion (choice based on sort)
 * @param repository String Required The name of the repository that holds the tags you wish to delete
 * @param sortFlags String flags to pass to the unix command 'sort'.  Results in the sorted list to delete. - defaults to "-t. -k 1,1nr -k 2,2nr -k 3,3nr -k 4,4nr"
 * @param tagRegex String The regex to use to match tags for deletion - defaults to "^[0-9]+\\.[0-9]+\\.[0-9]+\\.[0-9]+\$"
 * @param verbose boolean Output tag names as you delete
 * */

def call(Map<String, Object> config){
  Docker docker = new Docker(this)
  docker.deleteTagsByPattern(config)
}
