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

import jhelp.sound.JHelpSound;
import jhelp.sound.JHelpSoundListener;
import jhelp.sound.SoundFactory;
import jhelp.util.MemorySweeper;
import jhelp.util.debug.Debug;

public class SampleMP3
      implements JHelpSoundListener
{

   /**
    * TODO Explains what does the method main in jhelp.sound.sample [JHelpSound]
    * 
    * @param args
    */
   public static void main(final String[] args)
   {
      MemorySweeper.launch();
      try
      {
         final JHelpSound sound = SoundFactory.getSoundFromURL(SampleMP3.class.getResource("BurstDream.mp3"));
         sound.setDestroyOnEnd(true);
         sound.addSoundListener(new SampleMP3());
         sound.play();
      }
      catch(final Exception exception)
      {
         Debug.printException(exception, "Issue in sample");
      }
   }

   @Override
   public void soundDestroy(final JHelpSound sound)
   {
      // {@todo} TODO Implements soundDestroy in jhelp.sound.sample [JHelpSound]
      Debug.printTodo("Implements soundDestroy in jhelp.sound.sample [JHelpSound]");
      MemorySweeper.exit(0);
   }

   @Override
   public void soundLoop(final JHelpSound sound)
   {// {@todo} TODO Implements soundLoop in jhelp.sound.sample [JHelpSound]
      Debug.printTodo("Implements soundLoop in jhelp.sound.sample [JHelpSound]");
   }

   @Override
   public void soundStart(final JHelpSound sound)
   {// {@todo} TODO Implements soundStart in jhelp.sound.sample [JHelpSound]
      Debug.printTodo("Implements soundStart in jhelp.sound.sample [JHelpSound]");
   }

   @Override
   public void soundStop(final JHelpSound sound)
   {// {@todo} TODO Implements soundStop in jhelp.sound.sample [JHelpSound]
      Debug.printTodo("Implements soundStop in jhelp.sound.sample [JHelpSound]");
   }
}