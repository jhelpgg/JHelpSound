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

import java.net.URL;

import jhelp.sound.mp3.SoundMP3Streaming;
import jhelp.util.debug.Debug;

public class MP3SteamingSample
{

   /**
    * @param args
    */
   public static void main(final String[] args)
   {
      try
      {
         final SoundMP3Streaming soundMP3Streaming = new SoundMP3Streaming(
               (new URL("http://mp3.live.tv-radio.com/franceculture/all/franceculture-32k.mp3")).openStream());
         soundMP3Streaming.play();
      }
      catch(final Exception exception)
      {
         Debug.printException(exception);
      }
   }

}