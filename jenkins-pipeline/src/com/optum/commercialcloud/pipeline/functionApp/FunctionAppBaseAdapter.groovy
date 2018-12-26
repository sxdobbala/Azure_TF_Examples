abstract class FunctionAppApapter implements Serializable {
  protected def jenkins

  FunctionAppApapter(jenkins) {
    this.jenkins = jenkins
  }

  abstract void test()

  abstract void codeQuality(boolean isPullRequest, String branchName)

  abstract void build()

  abstract void acceptanceTests(String environment)
}