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
package jhelp.sound.synthetizer;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import javax.sound.midi.Instrument;
import javax.sound.midi.MidiChannel;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Patch;
import javax.sound.midi.Soundbank;
import javax.sound.midi.Synthesizer;

import jhelp.util.Utilities;
import jhelp.util.debug.Debug;
import jhelp.util.debug.DebugLevel;
import jhelp.util.io.UtilIO;
import jhelp.util.text.UtilText;
import jhelp.util.thread.ThreadManager;
import jhelp.util.thread.ThreadedSimpleTask;

/**
 * Synthetyzer to play morsel<br>
 * <br>
 * Last modification : 28 mars 2009<br>
 * Version 0.0.0<br>
 * 
 * @author JHelp
 */
public class Synthetiser
{
   /** Synthetizer singleton */
   private static Synthetiser SYNTHETIZER;
   /** Black overtone fraction time */
   public static final double BLACK = 1D;
   /** White overtone fraction time */
   public static final double WHITE = 2D;

   /**
    * Compute quaver for a fraction time
    * 
    * @param time
    *           Fraction time
    * @return Quaver
    */
   public static int computeQuaver(final double time)
   {
      if(Synthetiser.isWhite(time) == true)
      {
         return -1;
      }
      if(Synthetiser.isBlack(time) == true)
      {
         return 0;
      }
      return (int) (-(Math.log(time) / Math.log(2d)));
   }

   /**
    * Apply dot on proportional time
    * 
    * @param baseTime
    *           Reference base time
    * @return Proportional time dotted
    */
   public static double dotTime(final double baseTime)
   {
      return baseTime * 2d;
   }

   /**
    * Install sound bank.<br>
    * It is not necessary to call it, because if no sound bank is present, the synthetizer call it on it's creation.<br>
    * But if you want sure that the best quality is used for your application, you can call it at installation
    * 
    * @throws SynthetyserException
    *            On installation problem
    */
   public static void installSoundBank() throws SynthetyserException
   {
      final String path = System.getProperty("sun.boot.library.path");
      if(path == null)
      {
         throw new SynthetyserException("Can't install sound bank, no sun.boot.library.path");
      }
      final int index = path.lastIndexOf(File.separator);
      final File file = new File(path.substring(0, index + 1) + "lib" + File.separator + "audio" + File.separator + "soundbank.gm");
      UtilIO.createFile(file);
      try
      {
         UtilIO.write(Synthetiser.class.getResourceAsStream("soundbank-deluxe.gm"), file);
      }
      catch(final IOException e)
      {
         throw new SynthetyserException("Can't install sound bank", e);
      }
   }

   /**
    * Indicates if proportional time is black
    * 
    * @param time
    *           Proportional time
    * @return {@code true} if proportional time is black
    */
   public static boolean isBlack(final double time)
   {
      return Math.abs(time - 1d) < 1e-5;
   }

   /**
    * Indicates if proportional time is white
    * 
    * @param time
    *           Proportional time
    * @return {@code true} if proportional time is white
    */
   public static boolean isWhite(final double time)
   {
      return Math.abs(time - 2d) < 1e-5;
   }

   /**
    * Obtain/create the synthetizer
    * 
    * @return Synthetizer
    * @throws SynthetyserException
    *            If synthetizer can't be create or initiate
    */
   public static Synthetiser obtainSynthetiser() throws SynthetyserException
   {
      if(Synthetiser.SYNTHETIZER == null)
      {
         Synthetiser.SYNTHETIZER = new Synthetiser();
      }
      return Synthetiser.SYNTHETIZER;
   }

   /**
    * Compute quaver proportional time
    * 
    * @param number
    *           Number or quaver
    * @return Quaver proportional time
    */
   public static double quaverTime(final int number)
   {
      return Math.pow(2d, -number);
   }

   /** Channels list */
   private final int[]                          channels;

   /** Synthesizer listeners */
   private final ArrayList<SynthetyserListener> listeners;

   /** Task play a morsel */
   private final ThreadedSimpleTask<Morsel>     taskPlayMorsel = new ThreadedSimpleTask<Morsel>()
                                                               {
                                                                  /**
                                                                   * Play a morsel <br>
                                                                   * <br>
                                                                   * <b>Parent documentation:</b><br>
                                                                   * {@inheritDoc}
                                                                   * 
                                                                   * @param morsel
                                                                   *           Morsel to play
                                                                   * @see jhelp.util.thread.ThreadedSimpleTask#doSimpleAction(java.lang.Object)
                                                                   */
                                                                  @Override
                                                                  protected void doSimpleAction(final Morsel morsel)
                                                                  {
                                                                     Synthetiser.this.onPause = false;
                                                                     Synthetiser.this.alive = true;
                                                                     // Prepare instruments
                                                                     int count = morsel.getNumberOfPartition();
                                                                     if(count > Synthetiser.this.midiChannels.length)
                                                                     {
                                                                        Debug.println(DebugLevel.WARNING,
                                                                              "Not enough chanel are able to play all morsel, only ",
                                                                              Synthetiser.this.midiChannels.length,
                                                                              " first partitions will play. Number ignored=",
                                                                              (count - Synthetiser.this.midiChannels.length));
                                                                        count = Synthetiser.this.midiChannels.length;
                                                                     }
                                                                     final Partition[] partitions = new Partition[count];
                                                                     final int[] indices = new int[count];
                                                                     Partition partition;
                                                                     int instrument;
                                                                     Patch patch;
                                                                     for(int i = 0; i < count; i++)
                                                                     {
                                                                        partition = morsel.getPartition(i);
                                                                        instrument = partition.getInstrument();
                                                                        if((instrument < 0) || (instrument >= Synthetiser.this.instruments.length))
                                                                        {
                                                                           instrument = 0;
                                                                        }
                                                                        patch = Synthetiser.this.instruments[instrument].getPatch();
                                                                        Synthetiser.this.midiChannels[i].programChange(patch.getBank(), patch.getProgram());
                                                                        partitions[i] = partition;
                                                                        indices[i] = 0;
                                                                        for(final OvertoneGroup overtoneGroup : partition)
                                                                        {
                                                                           overtoneGroup.timePass = 0;
                                                                           overtoneGroup.timeDuration = (long) (overtoneGroup.getTime() * Synthetiser.this.blackDuration);
                                                                        }
                                                                     }
                                                                     // Play the morsel
                                                                     OvertoneGroup overtoneGroup;
                                                                     int index;
                                                                     int strength;
                                                                     int countLive;
                                                                     while(Synthetiser.this.alive == true)
                                                                     {
                                                                        while((Synthetiser.this.onPause == true) && (Synthetiser.this.alive == true))
                                                                        {
                                                                           Utilities.sleep(100);
                                                                        }
                                                                        if(Synthetiser.this.alive == true)
                                                                        {
                                                                           countLive = 0;
                                                                           for(int i = 0; i < count; i++)
                                                                           {
                                                                              index = indices[i];
                                                                              partition = partitions[i];
                                                                              if(index < partition.getNumberOfOvertoneGroups())
                                                                              {
                                                                                 overtoneGroup = partition.getOvertoneGroup(index);
                                                                                 if(overtoneGroup.timePass == 0)
                                                                                 {
                                                                                    strength = overtoneGroup.getStrength();
                                                                                    for(final Overtone overtone : overtoneGroup)
                                                                                    {
                                                                                       Synthetiser.this.midiChannels[i].noteOn(overtone.getOvertoneCode(),
                                                                                             strength);
                                                                                    }
                                                                                 }
                                                                                 overtoneGroup.timePass++;
                                                                                 if(overtoneGroup.timePass > overtoneGroup.timeDuration)
                                                                                 {
                                                                                    for(final Overtone overtone : overtoneGroup)
                                                                                    {
                                                                                       Synthetiser.this.midiChannels[i].noteOff(overtone.getOvertoneCode());
                                                                                    }
                                                                                    indices[i]++;
                                                                                 }
                                                                                 countLive++;
                                                                              }
                                                                           }
                                                                           if(countLive == 0)
                                                                           {
                                                                              Synthetiser.this.alive = false;
                                                                           }
                                                                           Utilities.sleep(1);
                                                                        }
                                                                     }
                                                                     Synthetiser.this.stop();
                                                                  }
                                                               };

   /** Alive state */
   boolean                                      alive          = false;
   /** Duration of black in millisecond */
   long                                         blackDuration;
   /** Instruments play by synthetizer */
   Instrument[]                                 instruments;
   /** Channels of synthetizer */
   MidiChannel[]                                midiChannels;
   /** Indicates if player thread is on pause */
   boolean                                      onPause;
   /** Sound bank */
   Soundbank                                    soundBank;
   /** Java synthesizer */
   Synthesizer                                  synthesizer;

   /**
    * Constructs Synthetyser
    * 
    * @throws SynthetyserException
    *            On creation problem
    */
   private Synthetiser()
         throws SynthetyserException
   {
      this.blackDuration = 128L;

      try
      {
         this.synthesizer = MidiSystem.getSynthesizer();
         this.synthesizer.open();
      }
      catch(final MidiUnavailableException e)
      {
         throw new SynthetyserException("Intialization synthetizer fails", e);
      }

      this.soundBank = this.synthesizer.getDefaultSoundbank();
      if(this.soundBank == null)
      {
         this.synthesizer.close();
         Synthetiser.installSoundBank();
         try
         {
            this.synthesizer = MidiSystem.getSynthesizer();
            this.synthesizer.open();
         }
         catch(final MidiUnavailableException e)
         {
            throw new SynthetyserException("Intialization synthetizer fails", e);
         }
         this.soundBank = this.synthesizer.getDefaultSoundbank();
         if(this.soundBank == null)
         {
            throw new SynthetyserException("No sound bank !");
         }
      }

      this.synthesizer.loadAllInstruments(this.soundBank);
      this.instruments = this.synthesizer.getLoadedInstruments();
      if((this.instruments == null) || (this.instruments.length < 1))
      {
         this.synthesizer.close();
         throw new SynthetyserException("No instruments !");
      }

      this.midiChannels = this.synthesizer.getChannels();
      if((this.midiChannels == null) || (this.midiChannels.length < 1))
      {
         this.synthesizer.close();
         throw new SynthetyserException("No chanels !");
      }
      this.channels = new int[this.midiChannels.length];

      for(int i = 0; i < this.midiChannels.length; i++)
      {
         this.putInstrument(i % this.instruments.length, i);
      }

      this.listeners = new ArrayList<SynthetyserListener>();
   }

   /**
    * Say to listeners that pause change status
    * 
    * @param pause
    *           New pause status
    */
   protected void fireSynthetyserChangePause(final boolean pause)
   {
      for(final SynthetyserListener synthetyserListener : this.listeners)
      {
         synthetyserListener.synthetyserChangePause(pause);
      }
   }

   /**
    * Say to listeners that play back start
    */
   protected void fireSynthetyserStartPlay()
   {
      for(final SynthetyserListener synthetyserListener : this.listeners)
      {
         synthetyserListener.synthetyserStartPlay();
      }
   }

   /**
    * Say to listeners that play back stop
    */
   protected void fireSynthetyserStopPlay()
   {
      for(final SynthetyserListener synthetyserListener : this.listeners)
      {
         synthetyserListener.synthetyserStopPlay();
      }
   }

   /**
    * Add synthetizer listener
    * 
    * @param synthetyserListener
    *           Listener to add
    */
   public void addSynthetyserListener(final SynthetyserListener synthetyserListener)
   {
      this.listeners.add(synthetyserListener);
   }

   /**
    * Close and destroy synthetizer.<br>
    * Call it when you don't plan to use synthetizer very soon (or no more), just before exit your application, or free some
    * memory for some heavy work
    */
   public void closeAndDestroy()
   {
      if(Synthetiser.SYNTHETIZER == null)
      {
         Debug.println(DebugLevel.DEBUG, "Already close !");

         return;
      }
      this.stop();
      this.synthesizer.close();
      this.synthesizer = null;
      this.soundBank = null;
      for(int i = 0; i < this.instruments.length; i++)
      {
         this.instruments[i] = null;
      }
      this.instruments = null;
      for(int i = 0; i < this.midiChannels.length; i++)
      {
         this.midiChannels[i] = null;
      }
      this.midiChannels = null;
      Synthetiser.SYNTHETIZER = null;
   }

   /**
    * Return blackDuration
    * 
    * @return blackDuration
    */
   public long getBlackDuration()
   {
      return this.blackDuration;
   }

   /**
    * Obtain the instrument for a channel
    * 
    * @param channel
    *           Channel search
    * @return Instrument set on the channel
    */
   public int getInstrumentOnChannel(final int channel)
   {
      return this.channels[channel];
   }

   /**
    * Stop to play a note
    * 
    * @param noteCode
    *           Note code
    * @param velocity
    *           Velocity
    * @param chanel
    *           Channel
    */
   public void noteOff(final int noteCode, final int velocity, final int chanel)
   {
      this.midiChannels[chanel].noteOff(noteCode, velocity);
   }

   /**
    * Start play a note in a channel
    * 
    * @param noteCode
    *           Note code
    * @param velocity
    *           Velocity
    * @param chanel
    *           Channel
    */
   public void noteOn(final int noteCode, final int velocity, final int chanel)
   {
      this.midiChannels[chanel].noteOn(noteCode, velocity);
   }

   /**
    * Number of available channels.<br>
    * We can assign an instrument per channel, and play channels on same time. So it's also give the number of instruments can
    * be played in same time.<br>
    * Operating system dependent
    * 
    * @return Number of channels
    */
   public int numberOfChannels()
   {
      return this.midiChannels.length;
   }

   /**
    * Number of available instruments
    * 
    * @return Number of instuments
    */
   public int numberOfInstruments()
   {
      return this.instruments.length;
   }

   /**
    * Obtain all instruments names
    * 
    * @return All instruments names
    */
   public String[] obtainAllInstrumentsName()
   {
      final String[] names = new String[this.instruments.length];

      for(int i = 0; i < this.instruments.length; i++)
      {
         names[i] = this.instruments[i].getName();
      }

      Arrays.sort(names);

      return names;
   }

   /**
    * Obtain instrument with name is closest than given one.<br>
    * To be sure have right instrument, use exact name give by {@link #obtainAllInstrumentsName()} or
    * {@link #obtainNameOfInstrument(int)}
    * 
    * @param name
    *           Instrument name
    * @return Instrument match
    */
   public int obtainInstrument(final String name)
   {
      int distance = Integer.MAX_VALUE;
      int instrument = -1;
      int dist;
      for(int i = 0; (i < this.instruments.length) && (distance > 0); i++)
      {
         dist = UtilText.computeDistance(name, this.instruments[i].getName(), false, " -.\n\r\f\t()");
         if(dist < distance)
         {
            distance = dist;
            instrument = i;
         }
      }
      return instrument;
   }

   /**
    * Obtain name of instrument
    * 
    * @param instrument
    *           Instrument index
    * @return Instrument's name
    */
   public String obtainNameOfInstrument(final int instrument)
   {
      if((instrument < 0) || (instrument >= this.instruments.length))
      {
         return this.instruments[0].getName();
      }
      return this.instruments[instrument].getName();
   }

   /**
    * Indicates if play back in on pause
    * 
    * @return {@code true} if play back in on pause
    */
   public boolean onPause()
   {
      return (this.alive == false) || (this.onPause == true);
   }

   /**
    * Indicates if play back is playing
    * 
    * @return {@code true} if play back is playing
    */
   public boolean onPlay()
   {
      return this.alive;
   }

   /**
    * Play a morsel
    * 
    * @param morsel
    *           Morsel to play
    * @throws SynthetyserException
    *            If synthetizer already play something
    */
   public void play(final Morsel morsel) throws SynthetyserException
   {
      if(morsel == null)
      {
         throw new NullPointerException("morsel musn't be null");
      }

      if(this.alive == true)
      {
         throw new IllegalStateException("Synthetizer is on play");
      }

      ThreadManager.THREAD_MANAGER.doThread(this.taskPlayMorsel, morsel);

      this.fireSynthetyserStartPlay();
   }

   /**
    * Put an instrument on channel
    * 
    * @param instrument
    *           Instrument number
    * @param chanel
    *           Channel where apply
    */
   public void putInstrument(final int instrument, final int chanel)
   {
      final Patch patch = this.instruments[instrument].getPatch();
      this.midiChannels[chanel].programChange(patch.getBank(), patch.getProgram());
      this.channels[chanel] = instrument;
   }

   /**
    * Remove synthetizer listener
    * 
    * @param synthetyserListener
    *           Listener to remove
    */
   public void removeSynthetyserListener(final SynthetyserListener synthetyserListener)
   {
      this.listeners.remove(synthetyserListener);
   }

   /**
    * Modify blackDuration
    * 
    * @param blackDuration
    *           New blackDuration value
    */
   public void setBlackDuration(long blackDuration)
   {
      if(blackDuration < 1)
      {
         blackDuration = 1;
      }
      this.blackDuration = blackDuration;
   }

   /**
    * Change pause status
    * 
    * @param pause
    *           New pause status
    */
   public void setPause(final boolean pause)
   {
      if(this.alive == false)
      {
         return;
      }

      if(pause != this.onPause)
      {
         this.onPause = pause;

         this.fireSynthetyserChangePause(pause);
      }
   }

   /**
    * Stop play back
    */
   public void stop()
   {
      if(this.alive == true)
      {
         this.alive = false;
      }

      this.fireSynthetyserStopPlay();
   }
}