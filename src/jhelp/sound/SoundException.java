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