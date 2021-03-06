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
package jhelp.sound.midi;

import java.io.File;

import javax.sound.midi.MidiSystem;
import javax.sound.midi.Sequencer;

import jhelp.sound.Sound;
import jhelp.sound.SoundException;
import jhelp.sound.SoundListener;
import jhelp.util.thread.ThreadManager;
import jhelp.util.thread.ThreadedVerySimpleTask;

/**
 * Sound plays MIDI files <br>
 * <br>
 * Last modification : 9 aoet 2009<br>
 * Version 0.0.0<br>
 * 
 * @author JHelp
 */
public class SoundMidi
      implements Sound
{
   /** Actual sequencer plays the MIDI */
   private Sequencer                    sequencer;
   /** Wait that the sound end, and signal to listeners, when its done */
   private final ThreadedVerySimpleTask waitForSoundEnd = new ThreadedVerySimpleTask()
                                                        {
                                                           /**
                                                            * Do the waiting task <br>
                                                            * <br>
                                                            * <b>Parent documentation:</b><br>
                                                            * {@inheritDoc}
                                                            * 
                                                            * @see jhelp.util.thread.ThreadedVerySimpleTask#doVerySimpleAction()
                                                            */
                                                           @Override
                                                           protected void doVerySimpleAction()
                                                           {
                                                              if(SoundMidi.this.isPlaying())
                                                              {
                                                                 ThreadManager.THREAD_MANAGER.delayedThread(this, null, 123);

                                                                 return;
                                                              }

                                                              synchronized(SoundMidi.this.lock)
                                                              {
                                                                 SoundMidi.this.alive = false;
                                                              }

                                                              if(SoundMidi.this.soundListener != null)
                                                              {
                                                                 SoundMidi.this.soundListener.soundEnd();
                                                              }
                                                           }
                                                        };
   /** alive state */
   boolean                              alive;
   /** Lock for synchronize the alive state */
   final Object                         lock            = new Object();
   /** Sound listener */
   SoundListener                        soundListener;

   /**
    * Constructs SoundMidi
    * 
    * @param file
    *           File MIDI
    * @throws SoundException
    *            On creation problem
    */
   public SoundMidi(final File file)
         throws SoundException
   {
      try
      {
         this.sequencer = MidiSystem.getSequencer();
         this.sequencer.setSequence(MidiSystem.getSequence(file));
         this.sequencer.open();
      }
      catch(final Exception exception)
      {
         throw new SoundException(exception, "Creation sound error");
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
      synchronized(this.lock)
      {
         this.alive = false;
      }

      if(this.sequencer != null)
      {
         this.sequencer.stop();
         this.sequencer.close();
      }
      this.sequencer = null;

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
      if(this.sequencer != null)
      {
         return this.sequencer.getMicrosecondPosition();
      }

      return 0;
   }

   /**
    * Indicates that sound is playing
    * 
    * @return {@code true} if sound is playing
    * @see jhelp.sound.Sound#isPlaying()
    */
   @Override
   public boolean isPlaying()
   {
      if(this.sequencer != null)
      {
         return this.sequencer.isRunning();
      }

      return false;
   }

   /**
    * play the sound
    * 
    * @see jhelp.sound.Sound#play()
    */
   @Override
   public void play()
   {
      if(this.sequencer == null)
      {
         return;
      }

      this.sequencer.start();

      synchronized(this.lock)
      {
         if(!this.alive)
         {
            this.alive = true;

            ThreadManager.THREAD_MANAGER.doThread(this.waitForSoundEnd, null);
         }
      }
   }

   /**
    * Change sound position
    * 
    * @param position
    *           New position
    * @see jhelp.sound.Sound#setPosition(long)
    */
   @Override
   public void setPosition(final long position)
   {
      if(this.sequencer == null)
      {
         return;
      }

      this.sequencer.setMicrosecondPosition(position);
   }

   /**
    * Defines sound listener
    * 
    * @param soundListener
    *           Sound listener
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
      if(this.sequencer == null)
      {
         return;
      }

      this.sequencer.stop();
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
      if(this.sequencer != null)
      {
         return this.sequencer.getMicrosecondLength();
      }

      return 0;
   }
}