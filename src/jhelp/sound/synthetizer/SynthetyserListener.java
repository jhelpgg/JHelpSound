/**
 * Project : JHelpSound<br>
 * Package : jhelp.sound.synthetizer<br>
 * Class : SynthetyserListener<br>
 * Date : 29 mars 2009<br>
 * By JHelp
 */
package jhelp.sound.synthetizer;

/**
 * Listener for events on Synthetyser<br>
 * <br>
 * Last modification : 29 mars 2009<br>
 * Version 0.0.0<br>
 * 
 * @author JHelp
 */
public interface SynthetyserListener
{
   /**
    * Call when synthetyzer start play morsel
    */
   public void synthetyserStartPlay();

   /**
    * Call when synthetyzer stop play morsel
    */
   public void synthetyserStopPlay();

   /**
    * Call when synthetyzer change pause status
    * 
    * @param pause
    *           New pause status
    */
   public void synthetyserChangePause(boolean pause);
}