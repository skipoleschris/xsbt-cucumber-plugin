package templemore.xsbt.cucumber

import java.io.File

/**
 * @author Chris Turner
 */
case class CucumberOutput(prettyReport: Boolean, htmlReport: Boolean, junitReport: Boolean, jsonReport: Boolean,
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
       "--format" :: "json-pretty:%s".format(jsonReportFile.getPath) :: Nil
     } else Nil)
  }
}
