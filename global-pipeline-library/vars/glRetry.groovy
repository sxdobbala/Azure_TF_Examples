import com.optum.jenkins.pipeline.library.utils.Utils

/**
 * Retries the passed in closure a set amount of times
 * @param times int optional - default 5 -  The string representing the id of the user/pass credential (this can be either a hash or a string you provide when creating a credential)
 * @param delay int optional - default 0 -  The number of seconds to delay between retries
 * @param exponential boolean optional - default false - An exponential backoff option.  Set to true will result in exponential growth in delay
 * @param errorHandler closure optional - default { e -> jenkins.echo e.message } -  Call the passed in block on error
 * */

def call(Map<String, Object> config, Closure body){
    Utils utils = new Utils(this)
    utils.retry(config, body)
}