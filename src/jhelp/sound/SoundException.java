/**
 * Project : game2Dengine<br>
 * Package : jhelp.game2D.engine.sound<br>
 * Class : SoundException<br>
 * Date : 9 aoet 2009<br>
 * By JHelp
 */
package jhelp.sound;

import jhelp.util.text.UtilText;

/**
 * Exception on sounds<br>
 * <br>
 * Last modification : 9 aoet 2009<br>
 * Version 0.0.0<br>
 * 
 * @author JHelp
 */
public class SoundException
      extends RuntimeException
{
   /** serialVersionUID */
   private static final long serialVersionUID = 7201674676758854071L;

   /**
    * Constructs SoundException
    * 
    * @param message
    *           Message
    */
   public SoundException(final Object... message)
   {
      super(UtilText.concatenate(message));
   }

   /**
    * Constructs SoundException
    * 
    * @param cause
    *           Exception cause
    * @param message
    *           Message
    */
   public SoundException(final Throwable cause, final Object... message)
   {
      super(UtilText.concatenate(message), cause);
   }
}