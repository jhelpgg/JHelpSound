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

/**
 * Sound event listener<br>
 * <br>
 * Last modification : 21 juil. 2010<br>
 * Version 0.0.0<br>
 * 
 * @author JHelp
 */
public interface JHelpSoundListener
{
   /**
    * Call when sound destroy
    * 
    * @param sound
    *           Destroyed sound
    */
   public void soundDestroy(JHelpSound sound);

   /**
    * Call when sound loop
    * 
    * @param sound
    *           Looped sound
    */
   public void soundLoop(JHelpSound sound);

   /**
    * Call when sound start
    * 
    * @param sound
    *           Started sound
    */
   public void soundStart(JHelpSound sound);

   /**
    * Call when sound stop
    * 
    * @param sound
    *           Stopped sound
    */
   public void soundStop(JHelpSound sound);
}