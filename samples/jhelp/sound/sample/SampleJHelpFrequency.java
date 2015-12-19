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

import jhelp.sound.frequency.JHelpFrequency;

public class SampleJHelpFrequency
{
   /**
    * @param args
    */
   public static void main(final String[] args)
   {
      final JHelpFrequency frequency = new JHelpFrequency();
      final int[] notes =
      {
            392, 440, 494, 440, 440, 392, 440, 494, 440, 440, 330, 330, 330, 330, 262, 262, 262, 262
      };
      final int[] volumes =
      {
            100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 50, 50, 50, 50, 50, 50, 50, 50
      };
      final int time = 5;
      final int length = notes.length;

      for(int i = 0; i < length; i++)
      {
         for(int j = 0; j < time; j++)
         {
            frequency.addFrequency(notes[i], volumes[i], notes[i], volumes[i]);
         }
      }

      frequency.play();
   }
}