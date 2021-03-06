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
 * Exception for synthetizer <br>
 * <br>
 * Last modification : 28 mars 2009<br>
 * Version 0.0.0<br>
 * 
 * @author JHelp
 */
public class SynthetyserException
      extends Exception
{
   /** serialVersionUID */
   private static final long serialVersionUID = 6141770925190083665L;

   /**
    * Constructs SynthetyserException
    */
   public SynthetyserException()
   {
   }

   /**
    * Constructs SynthetyserException
    * 
    * @param message
    *           Message
    */
   public SynthetyserException(final String message)
   {
      super(message);
   }

   /**
    * Constructs SynthetyserException
    * 
    * @param message
    *           Message
    * @param cause
    *           Cause of exception
    */
   public SynthetyserException(final String message, final Throwable cause)
   {
      super(message, cause);
   }

   /**
    * Constructs SynthetyserException
    * 
    * @param cause
    *           Cause of exception
    */
   public SynthetyserException(final Throwable cause)
   {
      super(cause);
   }
}