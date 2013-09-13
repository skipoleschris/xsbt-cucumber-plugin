package templemore.sbt.cucumber

import java.io.File

/**
 * Defines the output options for running cucumber.
 *
 * @author Chris Turner
 */
case class Output(prettyReport: Boolean, htmlReport: Boolean, junitReport: Boolean, jsonReport: Boolean,
                  prettyReportFile: File, htmlReportDir: File, junitReportFile: File, jsonReportFile: File) {

  def options: List[String] = {
    (if (prettyReport) {
       prettyReportFile.getParentFile.mkdirs()
       "--format" :: "progress" :: "--format" :: "pretty:%s".format(prettyReportFile.getPath) :: Nil
     }
     else "--format" :: "pretty" :: Nil) ++
    (if ( htmlReport) {
       htmlReportDir.mkdirs()
       "--format" :: "html:%s".format(htmlReportDir.getPath) :: Nil
     } else Nil) ++
    (if ( junitReport) {
       junitReportFile.getParentFile.mkdirs()
       "--format" :: "junit:%s".format(junitReportFile.getPath) :: Nil
     } else Nil) ++
    (if ( jsonReport) {
       jsonReportFile.getParentFile.mkdirs()
       "--format" :: "json:%s".format(jsonReportFile.getPath) :: Nil
     } else Nil)
  }
}