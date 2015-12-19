/**
 * <h1>License :</h1> <br>
 * The following code is deliver as is. I take care that code compile and work, but I am not responsible about any damage it may
 * cause.<br>
 * You can use, modify, the code as your need for any usage. But you can't do any action that avoid me or other person use,
 * modify this code. The code is free for usage and modification, you can't change that fact.<br>
 * <br>
 * 
 * @author JHelp
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
    * Call when synthetyzer change pause status
    * 
    * @param pause
    *           New pause status
    */
   public void synthetyserChangePause(boolean pause);

   /**
    * Call when synthetyzer start play morsel
    */
   public void synthetyserStartPlay();

   /**
    * Call when synthetyzer stop play morsel
    */
   public void synthetyserStopPlay();
}