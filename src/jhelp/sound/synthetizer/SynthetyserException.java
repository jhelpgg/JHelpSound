/**
 * Project : JHelpSound<br>
 * Package : jhelp.sound.synthetizer<br>
 * Class : SynthetyserException<br>
 * Date : 28 mars 2009<br>
 * By JHelp
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