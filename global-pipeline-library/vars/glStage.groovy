import com.optum.jenkins.pipeline.library.utils.Utils

/**
 * A wrapper around the stage step to provide node and toggle features.  Takes named
 * parameters and then
 * @param name String required - The name of this stage
 * @param nodeLabel String optional - If specified, nodeLabel will be used as the label of a node block to wrap the stage in.
 * @param runStage boolean optional - If set to false, skip the stage
 *  Otherwise, none will be added
 * */

def call(Map<String, Object> config, Closure body){
    Utils utils = new Utils(this)
    utils.gStage(config, body)
}
