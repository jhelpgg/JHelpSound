/**
 * Project : game2Dengine<br>
 * Package : jhelp.game2D.engine.sound<br>
 * Class : Sound<br>
 * Date : 9 aoet 2009<br>
 * By JHelp
 */
package jhelp.sound;

/**
 * A sound<br>
 * <br>
 * Last modification : 9 aoet 2009<br>
 * Version 0.0.0<br>
 * 
 * @author JHelp
 */
public interface Sound
{
   /**
    * Play the sound.<br>
    * Launch the playing and return immediately.<br>
    * Never waits than sounds end
    */
   public void play();

   /**
    * Stop the sound
    */
   public void stop();

   /**
    * Destroy the sound
    */
   public void destroy();

   /**
    * Sound total size
    * 
    * @return Sound total size
    */
   public long totalSize();

   /**
    * Sound position
    * 
    * @return Sound position
    */
   public long getPosition();

   /**
    * Change sound position
    * 
    * @param position
    *           Sound position
    */
   public void setPosition(long position);

   /**
    * Indicates if sound is playing
    * 
    * @return {@code true} if sound is playing
    */
   public boolean isPlaying();

   /**
    * Define the sound listener
    * 
    * @param soundListener
    *           Sound listener
    */
   public void setSoundListener(SoundListener soundListener);
}