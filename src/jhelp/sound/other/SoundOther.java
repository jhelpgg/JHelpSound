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
package jhelp.sound.other;

import java.io.File;

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
import jhelp.util.debug.DebugLevel;
import jhelp.util.list.Pair;
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
   /** For activate/deactivate debug */
   private static final boolean DEBUG = false;

   /**
    * Create an audio stream and associated clip from a file
    * 
    * @param file
    *           File to extract sound
    * @return Created audio stream and associated clip. {@code null} if failed to create
    */
   private static final synchronized Pair<AudioInputStream, Clip> createSound(final File file)
   {
      AudioInputStream audioInputStream = null;
      Clip clip = null;

      try
      {
         audioInputStream = AudioSystem.getAudioInputStream(file);
         AudioFormat audioFormat = audioInputStream.getFormat();

         if((audioFormat.getEncoding() == javax.sound.sampled.AudioFormat.Encoding.ULAW)
               || (audioFormat.getEncoding() == javax.sound.sampled.AudioFormat.Encoding.ALAW))
         {
            // Create new format
            final AudioFormat tmp = new AudioFormat(javax.sound.sampled.AudioFormat.Encoding.PCM_SIGNED, audioFormat.getSampleRate(),
                  audioFormat.getSampleSizeInBits() * 2, audioFormat.getChannels(), audioFormat.getFrameSize() * 2, audioFormat.getFrameRate(), true);

            // Force the stream be the new format
            audioInputStream = AudioSystem.getAudioInputStream(tmp, audioInputStream);
            audioFormat = tmp;
         }

         // Get sound informations
         final Info info = new Info(Clip.class, audioFormat, (int) audioInputStream.getFrameLength() * audioFormat.getFrameSize());

         if(SoundOther.DEBUG)
         {
            Debug.println(DebugLevel.VERBOSE, "Sound : ", file.getAbsolutePath());
            Debug.println(DebugLevel.VERBOSE, "info : ", info);
            Debug.println(DebugLevel.VERBOSE, "supported : ", AudioSystem.isLineSupported(info));
            Debug.println(DebugLevel.VERBOSE, "encoding : ", audioFormat.getEncoding());
            Debug.println(DebugLevel.VERBOSE, "sample rate : ", audioFormat.getSampleRate());
            Debug.println(DebugLevel.VERBOSE, "sample size : ", audioFormat.getSampleSizeInBits());
            Debug.println(DebugLevel.VERBOSE, "chanel : ", audioFormat.getChannels());
            Debug.println(DebugLevel.VERBOSE, "frame size : ", audioFormat.getFrameSize());
            Debug.println(DebugLevel.VERBOSE, "frame rate : ", audioFormat.getFrameRate());
            Debug.println(DebugLevel.VERBOSE, "big endian : ", audioFormat.isBigEndian());
            Debug.println(DebugLevel.VERBOSE, "buffer size : ", audioInputStream.getFrameLength() * audioFormat.getFrameSize());
         }

         if(AudioSystem.isLineSupported(info) == false)
         {
            throw new SoundException("Info is not supported !");
         }

         // Create clip for play sound
         clip = (Clip) AudioSystem.getLine(info);

         // Link the clip to the sound
         clip.open(audioInputStream);

         return new Pair<AudioInputStream, Clip>(audioInputStream, clip);
      }
      catch(final Exception exception)
      {
         if(clip != null)
         {
            try
            {
               clip.flush();
               clip.close();

               if(SoundOther.DEBUG)
               {
                  Debug.printMark(DebugLevel.VERBOSE, "CLIP CLOSE");
               }
            }
            catch(final Exception e)
            {
               Debug.printException(e);
            }
         }
         clip = null;

         if(audioInputStream != null)
         {
            try
            {
               audioInputStream.close();

               if(SoundOther.DEBUG)
               {
                  Debug.printMark(DebugLevel.VERBOSE, "AUDIO STREAM CLOSE");
               }
            }
            catch(final Exception e)
            {
               Debug.printException(e);
            }
         }
         audioInputStream = null;

         Debug.printException(exception, "Failed to create sound ", file.getAbsolutePath());

         return null;
      }
   }

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

      final Pair<AudioInputStream, Clip> soundInfos = SoundOther.createSound(file);

      if(soundInfos == null)
      {
         throw new SoundException("Failed to create sound ", file.getAbsolutePath());
      }

      this.audioInputStream = soundInfos.element1;
      this.clip = soundInfos.element2;
   }

   /**
    * Play the sound and do action when finish
    */
   void playSound()
   {
      Utilities.sleep(8);

      if(this.clip != null)
      {
         this.clip.start();
      }
      Utilities.sleep(8);
      while((this.clip != null) && (this.clip.isRunning() == true) && (this.alive == true))
      {
         Utilities.sleep(8);
      }

      if(SoundOther.DEBUG)
      {
         Debug.println(DebugLevel.VERBOSE, "Sound play finished");
      }

      long time = System.nanoTime();
      if(this.clip != null)
      {
         this.clip.stop();
      }
      time = System.nanoTime() - time;

      if(SoundOther.DEBUG)
      {
         Debug.println(DebugLevel.VERBOSE, "Sound stoped : ", time);
      }

      if(this.alive == true)
      {
         if(this.clip != null)
         {
            this.clip.setMicrosecondPosition(0);
         }
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
   public synchronized void destroy()
   {
      this.alive = false;
      this.soundListener = null;
      if(this.clip != null)
      {
         this.clip.stop();
         this.clip.close();
      }
      this.clip = null;
      this.stop();
      try
      {
         if(this.audioInputStream != null)
         {
            this.audioInputStream.close();
         }
      }
      catch(final Exception e)
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
      if(this.clip != null)
      {
         return this.clip.getMicrosecondPosition();
      }

      return 0;
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
      if(this.clip != null)
      {
         this.clip.setMicrosecondPosition(position);
      }
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
      if(this.clip != null)
      {
         return this.clip.getMicrosecondLength();
      }

      return 0;
   }
}