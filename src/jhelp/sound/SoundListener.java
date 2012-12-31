/**
 * Project : game2Dengine<br>
 * Package : jhelp.game2D.engine.sound<br>
 * Class : SoundListener<br>
 * Date : 9 aoet 2009<br>
 * By JHelp
 */
package jhelp.sound;

/**
 * Listener of sound events<br>
 * <br>
 * Last modification : 9 aoet 2009<br>
 * Version 0.0.0<br>
 * 
 * @author JHelp
 */
public interface SoundListener
{
   /**
    * Call when sound end
    */
   public void soundEnd();

   /**
    * Call when sound make a loop a play again
    */
   public void soundLoop();
}