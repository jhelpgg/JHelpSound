/**
 * Project : JHelpSound<br>
 * Package : jhelp.sound<br>
 * Class : JHelpSoundListener<br>
 * Date : 21 juil. 2010<br>
 * By JHelp
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

   /**
    * Call when sound loop
    * 
    * @param sound
    *           Looped sound
    */
   public void soundLoop(JHelpSound sound);

   /**
    * Call when sound destroy
    * 
    * @param sound
    *           Destroyed sound
    */
   public void soundDestroy(JHelpSound sound);
}