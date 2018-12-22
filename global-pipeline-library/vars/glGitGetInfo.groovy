import com.optum.jenkins.pipeline.library.scm.Git

/**
 * Wrapper for getting some extra git information
 * this will populate these environment variables (should be self-explanatory)
 * GIT_COMMIT_SHORT
 * GIT_AUTHOR_NAME
 * GIT_AUTHOR_EMAIL
 * GIT_AUTHOR_DATE
 * GIT_COMMITTER_NAME
 * GIT_COMMITTER_EMAIL
 * GIT_COMMITTER_DATE
 * GIT_SUBJECT
 * GIT_SANITIZED_SUBJECT
 *
 * Example of usage:
 *  stage ('git info') {
 *    steps {
 *      glGitGetInfo()
 *      echo "${env.GIT_COMMITTER_NAME}"
 *      echo "${env.GIT_SUBJECT}"
 *    }
 *  }
 */

def call(){
  Git git = new Git(this)
  env.GIT_COMMIT_SHORT = git.getRevisionShort()
  env.GIT_AUTHOR_NAME = '\"' + git.getGitAuthorName() + '\"'
  env.GIT_AUTHOR_EMAIL = git.getGitAuthorEmail()
  env.GIT_AUTHOR_DATE = '\"' + git.getGitAuthorDate() + '\"'
  env.GIT_COMMITTER_NAME = '\"' + git.getGitCommitterName() + '\"'
  env.GIT_COMMITTER_EMAIL = git.getGitCommitterEmail()
  env.GIT_COMMITTER_DATE = '\"' + git.getGitCommitterDate() + '\"'
  env.GIT_SUBJECT = '\"' + git.getGitSubject() + '\"'
  env.GIT_SANITIZED_SUBJECT = '\"' + git.getGitSanitizedSubject() + '\"'
}

