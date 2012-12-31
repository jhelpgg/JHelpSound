/**
 * Project : game2Dengine<br>
 * Package : jhelp.sound.other<br>
 * Class : SoundOther<br>
 * Date : 9 aoet 2009<br>
 * By JHelp
 */
package jhelp.sound.other;

import java.io.File;
import java.io.IOException;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine.Info;

import jhelp.sound.Sound;
import jhelp.sound.SoundException;
import jhelp.sound.SoundListener;
import jhelp.util.Utilities;
import jhelp.util.debug.Debug;
import jhelp.util.thread.ThreadManager;
import jhelp.util.thread.ThreadedVerySimpleTask;

/**
 * {@link Sound} implementation for "au", some "wav" and some others (Not work for midi or mp3)<br>
 * <br>
 * Last modification : 9 aoet 2009<br>
 * Version 0.0.0<br>
 * 
 * @author JHelp
 */
public class SoundOther
      implements Sound
{
   /** Audio format */
   private AudioFormat                  audioFormat;
   /** Stream for read the sound */
   private AudioInputStream             audioInputStream;

   /** Clip that play the sound */
   private Clip                         clip;

   /** Listener of sound events */
   private SoundListener                soundListener;
   /** Task that play the sound */
   private final ThreadedVerySimpleTask taskPlaySound = new ThreadedVerySimpleTask()
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
                                                            SoundOther.this.playSound();
                                                         }
                                                      };
   /** Alive state */
   boolean                              alive         = false;

   /**
    * Constructs Sound
    * 
    * @param file
    *           File where lies the sound
    * @throws SoundException
    *            On opening or initializing problem
    */
   public SoundOther(final File file)
         throws SoundException
   {
      // ************************
      // *** Initialize sound ***
      // ************************
      try
      {
         // Get stream
         this.audioInputStream = AudioSystem.getAudioInputStream(file);
         // Get format
         this.audioFormat = this.audioInputStream.getFormat();

         // Try convert format, if need
         if((this.audioFormat.getEncoding() == javax.sound.sampled.AudioFormat.Encoding.ULAW) || (this.audioFormat.getEncoding() == javax.sound.sampled.AudioFormat.Encoding.ALAW))
         {
            // Create new format
            AudioFormat tmp = new AudioFormat(javax.sound.sampled.AudioFormat.Encoding.PCM_SIGNED, this.audioFormat.getSampleRate(), this.audioFormat.getSampleSizeInBits() * 2, this.audioFormat.getChannels(),
                  this.audioFormat.getFrameSize() * 2, this.audioFormat.getFrameRate(), true);

            // Force the stream be the new format
            this.audioInputStream = AudioSystem.getAudioInputStream(tmp, this.audioInputStream);
            this.audioFormat = tmp;

            tmp = null;
         }

         // Get sound informations
         Info info = new Info(Clip.class, this.audioInputStream.getFormat(), (int) this.audioInputStream.getFrameLength() * this.audioFormat.getFrameSize());

         // Create clip for play sound
         this.clip = (Clip) AudioSystem.getLine(info);
         info = null;

         // Link the clip to the sound
         this.clip.open(this.audioInputStream);
      }
      catch(final Exception exception)
      {
         throw new SoundException(exception, "Sound creation failed");
      }
   }

   /**
    * Play the sound and do action when finish
    */
   void playSound()
   {
      Utilities.sleep(123);

      this.clip.start();
      Utilities.sleep(99);
      while((this.clip.isActive() == true) && (this.alive == true))
      {
         Utilities.sleep(99);
      }

      this.clip.stop();
      if(this.alive == true)
      {
         this.clip.setMicrosecondPosition(0);
      }

      this.alive = false;
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
      this.stop();
      this.audioFormat = null;

      try
      {
         this.audioInputStream.close();
      }
      catch(final IOException e)
      {
         Debug.printException(e);
      }
      this.audioInputStream = null;

      this.clip = null;
      this.soundListener = null;
      this.alive = false;
   }

   /**
    * Actual sound position
    * 
    * @return Sound position
    * @see jhelp.sound.Sound#getPosition()
    */
   @Override
   public long getPosition()
   {
      return this.clip.getMicrosecondPosition();
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
      if(this.alive == false)
      {
         this.alive = true;

         ThreadManager.THREAD_MANAGER.doThread(this.taskPlaySound, null);
      }
   }

   /**
    * Change sound position
    * 
    * @param position
    *           New sound position
    * @see jhelp.sound.Sound#setPosition(long)
    */
   @Override
   public void setPosition(final long position)
   {
      this.clip.setMicrosecondPosition(position);
   }

   /**
    * Defines sound listener
    * 
    * @param soundListener
    *           New listener
    * @see jhelp.sound.Sound#setSoundListener(jhelp.sound.SoundListener)
    */
   @Override
   public void setSoundListener(final SoundListener soundListener)
   {
      this.soundListener = soundListener;
   }

   /**
    * Stop the sound
    * 
    * @see jhelp.sound.Sound#stop()
    */
   @Override
   public void stop()
   {
      this.alive = false;
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
      return this.clip.getMicrosecondLength();
   }
}