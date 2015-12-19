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
package jhelp.sound.sample;

import jhelp.sound.synthetizer.Morsel;
import jhelp.sound.synthetizer.Overtone;
import jhelp.sound.synthetizer.OvertoneGroup;
import jhelp.sound.synthetizer.OvertoneName;
import jhelp.sound.synthetizer.Partition;
import jhelp.sound.synthetizer.Synthetiser;
import jhelp.sound.synthetizer.SynthetyserListener;
import jhelp.util.MemorySweeper;
import jhelp.util.debug.Debug;
import jhelp.util.debug.DebugLevel;
import jhelp.util.math.random.JHelpRandom;

public class SorcerApprentice
      implements SynthetyserListener
{
   private static final double TIME_PAUSE = Synthetiser.quaverTime(1);

   private static void addNote(final Partition partition, final Overtone overtone, final int stength)
   {
      final OvertoneGroup overtoneGroup = new OvertoneGroup(Synthetiser.BLACK, stength);
      overtoneGroup.addOvertone(overtone);
      partition.addOvertoneGroup(overtoneGroup);
   }

   private static void addPause(final Partition partition)
   {
      partition.addOvertoneGroup(new OvertoneGroup(SorcerApprentice.TIME_PAUSE, 0));
   }

   public static void main(final String[] args)
   {
      MemorySweeper.launch();

      try
      {
         final Synthetiser synthetiser = Synthetiser.obtainSynthetiser();
         synthetiser.setBlackDuration(150);
         Debug.println(DebugLevel.DEBUG, "Synthetiser ready");
         synthetiser.addSynthetyserListener(new SorcerApprentice());
         for(final String name : synthetiser.obtainAllInstrumentsName())
         {
            Debug.println(DebugLevel.VERBOSE, name);
         }
         final Morsel morsel = new Morsel();
         final int instrument = JHelpRandom.random(synthetiser.numberOfInstruments());
         Debug.println(DebugLevel.INFORMATION, "INSTRUMENT=", synthetiser.obtainNameOfInstrument(instrument));

         final Partition partition = new Partition();
         partition.setInstrument(instrument);
         SorcerApprentice.addNote(partition, Overtone.obtainOvertone(5, OvertoneName.E), 100);
         SorcerApprentice.addPause(partition);
         SorcerApprentice.addNote(partition, Overtone.obtainOvertone(5, OvertoneName.C), 50);
         SorcerApprentice.addPause(partition);
         SorcerApprentice.addNote(partition, Overtone.obtainOvertone(5, OvertoneName.H), 100);
         SorcerApprentice.addPause(partition);
         SorcerApprentice.addNote(partition, Overtone.obtainOvertone(5, OvertoneName.C), 50);
         SorcerApprentice.addPause(partition);
         SorcerApprentice.addNote(partition, Overtone.obtainOvertone(4, OvertoneName.I), 100);
         SorcerApprentice.addNote(partition, Overtone.obtainOvertone(5, OvertoneName.C_SHARP), 100);
         SorcerApprentice.addNote(partition, Overtone.obtainOvertone(5, OvertoneName.D_SHARP), 100);
         SorcerApprentice.addPause(partition);
         SorcerApprentice.addNote(partition, Overtone.obtainOvertone(5, OvertoneName.E), 100);
         SorcerApprentice.addNote(partition, Overtone.obtainOvertone(5, OvertoneName.G), 50);
         SorcerApprentice.addNote(partition, Overtone.obtainOvertone(5, OvertoneName.G), 100);
         SorcerApprentice.addPause(partition);
         SorcerApprentice.addNote(partition, Overtone.obtainOvertone(5, OvertoneName.E), 100);
         SorcerApprentice.addNote(partition, Overtone.obtainOvertone(5, OvertoneName.G), 50);
         SorcerApprentice.addNote(partition, Overtone.obtainOvertone(5, OvertoneName.G), 100);
         SorcerApprentice.addPause(partition);
         SorcerApprentice.addNote(partition, Overtone.obtainOvertone(4, OvertoneName.F), 100);
         SorcerApprentice.addNote(partition, Overtone.obtainOvertone(5, OvertoneName.E), 100);
         SorcerApprentice.addNote(partition, Overtone.obtainOvertone(5, OvertoneName.D_SHARP), 100);
         SorcerApprentice.addPause(partition);
         SorcerApprentice.addNote(partition, Overtone.obtainOvertone(5, OvertoneName.E), 100);
         SorcerApprentice.addNote(partition, Overtone.obtainOvertone(5, OvertoneName.G), 50);
         SorcerApprentice.addNote(partition, Overtone.obtainOvertone(5, OvertoneName.G), 100);
         SorcerApprentice.addPause(partition);
         SorcerApprentice.addNote(partition, Overtone.obtainOvertone(5, OvertoneName.E), 100);
         SorcerApprentice.addNote(partition, Overtone.obtainOvertone(5, OvertoneName.G), 50);
         SorcerApprentice.addNote(partition, Overtone.obtainOvertone(5, OvertoneName.G), 100);
         SorcerApprentice.addPause(partition);
         SorcerApprentice.addNote(partition, Overtone.obtainOvertone(4, OvertoneName.F), 100);
         SorcerApprentice.addNote(partition, Overtone.obtainOvertone(5, OvertoneName.E), 100);
         SorcerApprentice.addNote(partition, Overtone.obtainOvertone(5, OvertoneName.D_SHARP), 100);
         SorcerApprentice.addPause(partition);
         SorcerApprentice.addNote(partition, Overtone.obtainOvertone(5, OvertoneName.E), 100);
         SorcerApprentice.addNote(partition, Overtone.obtainOvertone(5, OvertoneName.G), 50);
         SorcerApprentice.addNote(partition, Overtone.obtainOvertone(5, OvertoneName.G), 100);
         SorcerApprentice.addPause(partition);
         SorcerApprentice.addNote(partition, Overtone.obtainOvertone(5, OvertoneName.E), 100);
         SorcerApprentice.addNote(partition, Overtone.obtainOvertone(5, OvertoneName.G), 100);
         SorcerApprentice.addNote(partition, Overtone.obtainOvertone(5, OvertoneName.F), 100);
         SorcerApprentice.addPause(partition);
         SorcerApprentice.addNote(partition, Overtone.obtainOvertone(5, OvertoneName.E), 100);
         SorcerApprentice.addNote(partition, Overtone.obtainOvertone(5, OvertoneName.F), 100);
         SorcerApprentice.addNote(partition, Overtone.obtainOvertone(5, OvertoneName.G), 100);
         SorcerApprentice.addPause(partition);
         SorcerApprentice.addNote(partition, Overtone.obtainOvertone(5, OvertoneName.E), 100);
         SorcerApprentice.addNote(partition, Overtone.obtainOvertone(5, OvertoneName.H), 100);
         SorcerApprentice.addNote(partition, Overtone.obtainOvertone(5, OvertoneName.F), 100);
         SorcerApprentice.addPause(partition);
         morsel.addPartition(partition);

         Debug.println(DebugLevel.DEBUG, "Synthetiser play");
         synthetiser.play(morsel);

      }
      catch(final Exception exception)
      {
         Debug.printException(exception, "Can't launch the sample");
         MemorySweeper.exit(0);
      }
   }

   @Override
   public void synthetyserChangePause(final boolean pause)
   {
   }

   @Override
   public void synthetyserStartPlay()
   {
   }

   @Override
   public void synthetyserStopPlay()
   {
      Debug.println(DebugLevel.DEBUG, "Good bye!");
      MemorySweeper.exit(0);
   }
}