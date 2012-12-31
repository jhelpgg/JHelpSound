/**
 * Project : game2Dengine<br>
 * Package : jhelp.sound.mp3<br>
 * Class : SoundMP3<br>
 * Date : 9 aoet 2009<br>
 * By JHelp
 */
package jhelp.sound.mp3;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.Player;
import jhelp.sound.Sound;
import jhelp.sound.SoundException;
import jhelp.sound.SoundListener;
import jhelp.util.debug.Debug;
import jhelp.util.thread.ThreadManager;
import jhelp.util.thread.ThreadedVerySimpleTask;

/**
 * Sound play MP3 <br>
 * <br>
 * Last modification : 9 aoet 2009<br>
 * Version 0.0.0<br>
 * 
 * @author JHelp
 */
public class SoundMP3
      implements Sound
{
   /** Stream we can control the reading */
   private ControlInputStream           controlInputStream;
   /** Listner of sound events */
   private SoundListener                soundListener;
   /** Task that play the sound */
   private final ThreadedVerySimpleTask taskPlayTheSound = new ThreadedVerySimpleTask()
                                                         {
                                                            /**
                                                             * Play the sound <br>
                                                             * <br>
                                                             * <b>Parent documentation:</b><br>
                                                             * {@inheritDoc}
                                                             * 
                                                             * @see jhelp.util.thread.ThreadedVerySimpleTask#doVerySimpleAction()
                                                             */
                                                            @Override
                                                            protected void doVerySimpleAction()
                                                            {
                                                               try
                                                               {
                                                                  SoundMP3.this.player.play();
                                                               }
                                                               catch(final JavaLayerException e)
                                                               {
                                                                  Debug.printException(e);
                                                               }

                                                               SoundMP3.this.playEnd();
                                                            }
                                                         };

   /** A live state */
   boolean                              alive            = false;
   /** Lock for synchronize */
   final Object                         lock             = new Object();
   /** Player from JL020 API (javazoom) */
   Player                               player;

   /**
    * Constructs SoundMP3
    * 
    * @param file
    *           File where found the sound
    * @throws SoundException
    *            On initialisation problem
    */
   public SoundMP3(final File file)
         throws SoundException
   {
      try
      {
         this.controlInputStream = ControlInputStream.createControlInputStream(new FileInputStream(file));
      }
      catch(final IOException e)
      {
         throw new SoundException("Reading stream problem", e);
      }
   }

   /**
    * Call when sound is finish to play
    */
   void playEnd()
   {
      synchronized(this.lock)
      {
         this.alive = false;
      }

      // Close the player
      if(this.player != null)
      {
         this.player.close();
      }
      this.player = null;

      if(this.soundListener != null)
      {
         this.soundListener.soundEnd();
      }
   }

   /**
    * Destroy the sound
    * 
    * @see jhelp.sound.Sound#destroy()
    */
   @Override
   public void destroy()
   {
      if(this.player != null)
      {
         this.player.close();
      }
      this.player = null;

      synchronized(this.lock)
      {
         this.alive = false;
      }

      if(this.controlInputStream != null)
      {
         this.controlInputStream.destroy();
      }
      this.controlInputStream = null;

      this.soundListener = null;
   }

   /**
    * Sound actual position
    * 
    * @return Sound actual position
    * @see jhelp.sound.Sound#getPosition()
    */
   @Override
   public long getPosition()
   {
      return this.controlInputStream.getPosition();
   }

   /**
    * Indicates if sound is playing
    * 
    * @return {@code true} if sound is playing
    * @see jhelp.sound.Sound#isPlaying()
    */
   @Override
   public boolean isPlaying()
   {
      return this.alive;
   }

   /**
    * Play the sound
    * 
    * @see jhelp.sound.Sound#play()
    */
   @Override
   public void play()
   {
      synchronized(this.lock)
      {
         if(this.alive == false)
         {
            this.alive = true;

            try
            {
               // Create the player
               this.controlInputStream.setPause(false);
               this.controlInputStream.reset();
               this.player = new Player(this.controlInputStream);
            }
            catch(final Exception e)
            {
               throw new SoundException(e, "Playing start fail");
            }

            ThreadManager.THREAD_MANAGER.doThread(this.taskPlayTheSound, null);
         }
      }
   }

   /**
    * Change pause state
    * 
    * @param pause
    *           New pause state
    */
   public void setPause(final boolean pause)
   {
      this.controlInputStream.setPause(pause);
   }

   /**
    * Change sound position
    * 
    * @param position
    *           Sound position
    * @see jhelp.sound.Sound#setPosition(long)
    */
   @Override
   public void setPosition(final long position)
   {
      this.controlInputStream.setPosition((int) position);
   }

   /**
    * Change sound listener
    * 
    * @param soundListener
    *           New sound listener
    * @see jhelp.sound.Sound#setSoundListener(jhelp.sound.SoundListener)
    */
   @Override
   public void setSoundListener(final SoundListener soundListener)
   {
      this.soundListener = soundListener;
   }

   /**
    * Stop sound
    * 
    * @see jhelp.sound.Sound#stop()
    */
   @Override
   public void stop()
   {
      if(this.player != null)
      {
         this.player.close();
      }

      this.player = null;
      synchronized(this.lock)
      {
         this.alive = false;
      }
   }

   /**
    * Sound total size
    * 
    * @return Sound total size
    * @see jhelp.sound.Sound#totalSize()
    */
   @Override
   public long totalSize()
   {
      try
      {
         return this.controlInputStream.available();
      }
      catch(final Exception exception)
      {
         return -1;
      }
   }
}