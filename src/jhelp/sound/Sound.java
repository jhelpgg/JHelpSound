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
    * Destroy the sound
    */
   public void destroy();

   /**
    * Sound position
    * 
    * @return Sound position
    */
   public long getPosition();

   /**
    * Indicates if sound is playing
    * 
    * @return {@code true} if sound is playing
    */
   public boolean isPlaying();

   /**
    * Play the sound.<br>
    * Launch the playing and return immediately.<br>
    * Never waits than sounds end
    */
   public void play();

   /**
    * Change sound position
    * 
    * @param position
    *           Sound position
    */
   public void setPosition(long position);

   /**
    * Define the sound listener
    * 
    * @param soundListener
    *           Sound listener
    */
   public void setSoundListener(SoundListener soundListener);

   /**
    * Stop the sound
    */
   public void stop();

   /**
    * Sound total size
    * 
    * @return Sound total size
    */
   public long totalSize();
}