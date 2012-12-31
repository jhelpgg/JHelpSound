/**
 * Project : game2Dengine<br>
 * Package : jhelp.sound<br>
 * Class : EngineSound<br>
 * Date : 9 aoet 2009<br>
 * By JHelp
 */
package jhelp.sound;

import java.util.ArrayList;
import java.util.concurrent.locks.ReentrantLock;

import jhelp.sound.mp3.SoundMP3;
import jhelp.util.Utilities;
import jhelp.util.thread.ThreadManager;
import jhelp.util.thread.ThreadedSimpleTask;

/**
 * Engine generic sound<br>
 * It can play/pause/resume/stop : MP3, wav, midi, au sounds.<br>
 * To obtain an instance use {@link SoundFactory#getSoundFromFile(java.io.File)} ,
 * {@link SoundFactory#getSoundFromResource(String, Class)} or {@link SoundFactory#getSoundFromURL(java.net.URL)}, be sure that
 * your files have correct extension<br>
 * <br>
 * Last modification : 9 aoet 2009<br>
 * Version 0.0.0<br>
 * 
 * @author JHelp
 */
public final class JHelpSound
{
   /** State destroy */
   private static final int                  SOUND_DESTROY         = 3;
   /** State loop */
   private static final int                  SOUND_LOOP            = 2;
   /** State start */
   private static final int                  SOUND_START           = 0;
   /** State stop */
   private static final int                  SOUND_STOP            = 1;

   /** Indicates if sound can be destroy */
   private boolean                           canDestroy;

   /** Indicates if sound is destroy after playing all loops */
   private boolean                           destroyOnEnd;
   /** ID give by developer */
   private int                               developerId;
   /** Signal to listeners that sound state changed */
   private final ThreadedSimpleTask<Integer> fireSoundState        = new ThreadedSimpleTask<Integer>()
                                                                   {
                                                                      /**
                                                                       * Play the action <br>
                                                                       * <br>
                                                                       * <b>Parent documentation:</b><br>
                                                                       * {@inheritDoc}
                                                                       * 
                                                                       * @param parameter
                                                                       *           New sound state
                                                                       * @see jhelp.util.thread.ThreadedSimpleTask#doSimpleAction(java.lang.Object)
                                                                       */
                                                                      @Override
                                                                      protected void doSimpleAction(final Integer parameter)
                                                                      {
                                                                         JHelpSound.this.delayedFireSoundState(parameter);
                                                                      }
                                                                   };
   /** Synchronization lock */
   private final ReentrantLock               lock;
   /** Loop left */
   private int                               loop;
   /** Sound name */
   private final String                      name;
   /** Indicates if sound is pause */
   private boolean                           pause;
   /** Real sound play */
   private Sound                             sound;
   /** Sound listener */
   private final SoundListener               soundListenerInternal = new SoundListener()
                                                                   {
                                                                      /**
                                                                       * Call when sound end
                                                                       * 
                                                                       * @see jhelp.sound.SoundListener#soundEnd()
                                                                       */
                                                                      @Override
                                                                      public void soundEnd()
                                                                      {
                                                                         JHelpSound.this.soundEnd();
                                                                      }

                                                                      /**
                                                                       * Call when sound loop
                                                                       * 
                                                                       * @see jhelp.sound.SoundListener#soundLoop()
                                                                       */
                                                                      @Override
                                                                      public void soundLoop()
                                                                      {
                                                                         JHelpSound.this.soundLoop();
                                                                      }
                                                                   };
   /** Sound listeners */
   private ArrayList<JHelpSoundListener>     soundListeners;

   /**
    * Constructs EngineSound
    * 
    * @param sound
    *           Embed sound
    * @param name
    *           Sound name
    */
   JHelpSound(final Sound sound, final String name)
   {
      this.lock = new ReentrantLock();
      this.name = name;
      this.sound = sound;
      this.sound.setSoundListener(this.soundListenerInternal);
      this.soundListeners = new ArrayList<JHelpSoundListener>();
      this.pause = false;
      this.destroyOnEnd = false;
   }

   /**
    * Signal to listeners that sound state change
    * 
    * @param state
    *           New sound state
    */
   void delayedFireSoundState(final int state)
   {
      this.lock.lock();

      this.canDestroy = false;

      for(final JHelpSoundListener soundListener : this.soundListeners)
      {
         switch(state)
         {
            case JHelpSound.SOUND_START:
               soundListener.soundStart(this);
            break;
            case JHelpSound.SOUND_LOOP:
               soundListener.soundLoop(this);
            break;
            case JHelpSound.SOUND_STOP:
               soundListener.soundStop(this);
            break;
            case JHelpSound.SOUND_DESTROY:
               soundListener.soundDestroy(this);
            break;
         }
      }

      this.canDestroy = true;

      this.lock.unlock();
   }

   /**
    * Call when sound end
    */
   void soundEnd()
   {
      if(this.pause == true)
      {
         return;
      }

      if(this.loop > 1)
      {
         this.loop--;
         this.play();

         ThreadManager.THREAD_MANAGER.doThread(this.fireSoundState, JHelpSound.SOUND_LOOP);

         return;
      }

      ThreadManager.THREAD_MANAGER.doThread(this.fireSoundState, JHelpSound.SOUND_STOP);

      if(this.destroyOnEnd == true)
      {
         this.destroy();
      }
   }

   /**
    * Call when sound loop
    */
   void soundLoop()
   {
   }

   /**
    * add sound listener
    * 
    * @param soundListener
    *           New sound listener
    */
   public void addSoundListener(final JHelpSoundListener soundListener)
   {
      this.lock.lock();

      if(this.soundListeners.contains(soundListener) == false)
      {
         this.soundListeners.add(soundListener);
      }

      this.lock.unlock();
   }

   /**
    * Destroy the sound
    */
   public void destroy()
   {
      this.lock.lock();
      this.canDestroy = false;
      this.lock.unlock();

      ThreadManager.THREAD_MANAGER.doThread(this.fireSoundState, JHelpSound.SOUND_DESTROY);

      this.sound.destroy();
      this.sound = null;

      while(this.canDestroy == false)
      {
         Utilities.sleep(123);
      }

      this.soundListeners.clear();
      this.soundListeners = null;
   }

   /**
    * Return developerId
    * 
    * @return developerId
    */
   public int getDeveloperId()
   {
      return this.developerId;
   }

   /**
    * Return name
    * 
    * @return name
    */
   public String getName()
   {
      return this.name;
   }

   /**
    * Sound position
    * 
    * @return Sound position
    */
   public long getPosition()
   {
      return this.sound.getPosition();
   }

   /**
    * Return destroyOnEnd
    * 
    * @return destroyOnEnd
    */
   public boolean isDestroyOnEnd()
   {
      return this.destroyOnEnd;
   }

   /**
    * Indicates if sound is pause
    * 
    * @return {@code true} if sound is pause
    */
   public boolean isPause()
   {
      return this.pause;
   }

   /**
    * Indicates if sound is playing
    * 
    * @return {@code true} if sound is playing
    */
   public boolean isPlaying()
   {
      return this.sound.isPlaying();
   }

   /**
    * Loop the sound "infinite" time
    */
   public void loop()
   {
      this.loop(Integer.MAX_VALUE);
   }

   /**
    * Loop the sound
    * 
    * @param loop
    *           Number of loop
    */
   public void loop(final int loop)
   {
      this.loop = loop;
      this.play();
   }

   /**
    * Pause the sound
    */
   public void pause()
   {
      this.pause = true;

      if(this.sound instanceof SoundMP3)
      {
         ((SoundMP3) this.sound).setPause(true);
      }
      else if(this.sound.isPlaying() == true)
      {
         this.sound.stop();
      }
   }

   /**
    * Play the sound
    */
   public void play()
   {
      if(((this.sound instanceof SoundMP3) == true) && (this.pause == true))
      {
         ((SoundMP3) this.sound).setPause(false);
      }
      else if(this.sound.isPlaying() == false)
      {
         ThreadManager.THREAD_MANAGER.doThread(this.fireSoundState, JHelpSound.SOUND_START);

         this.sound.play();
      }

      this.pause = false;
   }

   /**
    * Remove sound listener
    * 
    * @param soundListener
    *           Listener to remove
    */
   public void removeSoundListener(final JHelpSoundListener soundListener)
   {
      this.lock.lock();

      this.soundListeners.remove(soundListener);

      this.lock.unlock();
   }

   /**
    * Modify destroyOnEnd
    * 
    * @param destroyOnEnd
    *           New destroyOnEnd value
    */
   public void setDestroyOnEnd(final boolean destroyOnEnd)
   {
      this.destroyOnEnd = destroyOnEnd;
   }

   /**
    * Modify developerId
    * 
    * @param developerId
    *           New developerId value
    */
   public void setDeveloperId(final int developerId)
   {
      this.developerId = developerId;
   }

   /**
    * Change sound position
    * 
    * @param position
    *           Sound position
    */
   public void setPosition(final long position)
   {
      this.sound.setPosition(position);
   }

   /**
    * Stop the sound
    */
   public void stop()
   {
      if(this.sound.isPlaying() == true)
      {
         this.sound.stop();
      }
      this.sound.setPosition(0);
   }

   /**
    * Sound total size
    * 
    * @return Sound total size
    */
   public long totalSize()
   {
      return this.sound.totalSize();
   }
}