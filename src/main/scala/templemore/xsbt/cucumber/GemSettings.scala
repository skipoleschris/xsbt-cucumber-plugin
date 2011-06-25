package templemore.xsbt.cucumber

import templemore.jruby.Gem

/**
 * @author Chris Turner
 */
case class GemSettings(cucumberVersion: String,
                       cuke4DukeVersion: String,
                       prawnVersion: String,
                       gemSource: String,
                       forceReload: Boolean) {

  def gems = Gem("cucumber", Some(cucumberVersion), Some(gemSource)) ::
             Gem("cuke4duke", Some(cuke4DukeVersion), Some(gemSource)) ::
             Gem("prawn", Some(prawnVersion), Some(gemSource)) :: Nil
}
