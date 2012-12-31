/**
 * Project : game2Dengine<br>
 * Package : jhelp.game2D.engine.sound<br>
 * Class : SoundFactory<br>
 * Date : 9 aoet 2009<br>
 * By JHelp
 */
package jhelp.sound;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

import jhelp.sound.midi.SoundMidi;
import jhelp.sound.mp3.SoundMP3;
import jhelp.sound.other.SoundOther;
import jhelp.util.cache.Cache;
import jhelp.util.cache.CacheElement;
import jhelp.util.debug.Debug;
import jhelp.util.io.UtilIO;

/**
 * Factory to obtain good sound <br>
 * <br>
 * Last modification : 9 aoet 2009<br>
 * Version 0.0.0<br>
 * 
 * @author JHelp
 */
public class SoundFactory
{
   /**
    * Element of sound cache <br>
    * <br>
    * Last modification : 18 juil. 2010<br>
    * Version 0.0.0<br>
    * 
    * @author JHelp
    */
   static class CacheElementSound
         extends CacheElement<JHelpSound>
   {
      /** Resource file path */
      private File     path;
      /** Reference class to get resource */
      private Class<?> referenceClass;
      /** Resource name */
      private String   resourceName;
      /** Resource URL */
      private URL      url;

      /**
       * Constructs CacheElementBufferedImage for file
       * 
       * @param path
       *           File
       */
      public CacheElementSound(final File path)
      {
         if(path == null)
         {
            throw new NullPointerException("path musn't be null");
         }

         this.path = path;
      }

      /**
       * Constructs CacheElementSound for resource
       * 
       * @param resourceName
       *           Resource name
       * @param referenceClass
       *           Reference class to get resource (From where path is relative to)
       */
      public CacheElementSound(final String resourceName, final Class<?> referenceClass)
      {
         if(resourceName == null)
         {
            throw new NullPointerException("resourceName musn't be null");
         }

         if(referenceClass == null)
         {
            throw new NullPointerException("referenceClass musn't be null");
         }

         this.referenceClass = referenceClass;
         this.resourceName = resourceName;
      }

      /**
       * Constructs CacheElementBufferedImage for url
       * 
       * @param url
       *           URL
       */
      public CacheElementSound(final URL url)
      {
         if(url == null)
         {
            throw new NullPointerException("url musn't be null");
         }

         this.url = url;
      }

      /**
       * Describe how create the sound
       * 
       * @return Created sound or {@code null} on creation problem
       * @see jhelp.util.cache.CacheElement#createElement()
       */
      @Override
      protected JHelpSound createElement()
      {
         String givenName = null;

         try
         {
            // If it is resource
            if(this.resourceName != null)
            {
               givenName = this.resourceName;

               // Get file where sounds are extracted
               this.path = UtilIO.obtainExternalFile("media/sounds/" + this.resourceName);

               // If file dosen't extracted, extract it
               if(this.path.exists() == false)
               {
                  UtilIO.createFile(this.path);

                  final InputStream inputStream = this.referenceClass.getResourceAsStream(this.resourceName);
                  final OutputStream outputStream = new FileOutputStream(this.path);
                  final byte[] temp = new byte[4096];

                  int read = inputStream.read(temp);
                  while(read >= 0)
                  {
                     outputStream.write(temp, 0, read);

                     read = inputStream.read(temp);
                  }

                  outputStream.flush();
                  outputStream.close();
                  inputStream.close();
               }
            }
            else if(this.url != null)
            {
               givenName = this.url.toString();

               // Get file where sounds are extracted
               this.path = UtilIO.obtainExternalFile("media/sounds/" + givenName.replace("://", "/"));

               // If file dosen't extracted, extract it
               if(this.path.exists() == false)
               {
                  UtilIO.createFile(this.path);

                  final InputStream inputStream = this.url.openStream();
                  final OutputStream outputStream = new FileOutputStream(this.path);
                  final byte[] temp = new byte[4096];

                  int read = inputStream.read(temp);
                  while(read >= 0)
                  {
                     outputStream.write(temp, 0, read);

                     read = inputStream.read(temp);
                  }

                  outputStream.flush();
                  outputStream.close();
                  inputStream.close();
               }
            }
            else
            {
               givenName = this.path.getAbsolutePath();
            }

            // Choose the right implementation of sound according to file
            // extention
            Sound sound = null;
            final String name = this.path.getName().toLowerCase();

            if(name.endsWith(".mp3") == true)
            {
               sound = new SoundMP3(this.path);
            }
            else if((name.endsWith(".mid") == true) || (name.endsWith(".midi") == true))
            {
               sound = new SoundMidi(this.path);
            }
            else
            {
               sound = new SoundOther(this.path);
            }

            return new JHelpSound(sound, givenName);
         }
         catch(final Exception e)
         {
            Debug.printException(e);
         }

         return null;
      }

   }

   /** Sound cache */
   private static final Cache<JHelpSound> CACHE = new Cache<JHelpSound>();

   /**
    * Get sound form file
    * 
    * @param file
    *           File where the sound lies
    * @return The sound or {@code null} if sound can't be get
    */
   public static JHelpSound getSoundFromFile(final File file)
   {
      final String path = file.getAbsolutePath();

      JHelpSound sound = SoundFactory.CACHE.get(path);
      if(sound == null)
      {
         SoundFactory.CACHE.add(path, new CacheElementSound(file));

         sound = SoundFactory.CACHE.get(path);
      }
      return sound;
   }

   /**
    * Get sound form resource
    * 
    * @param resourceName
    *           Resource name where the sound lies
    * @param referenceClass
    *           Reference class to have from where path is relative to
    * @return The sound or {@code null} if sound can't be get
    */
   public static JHelpSound getSoundFromResource(final String resourceName, final Class<?> referenceClass)
   {
      JHelpSound sound = SoundFactory.CACHE.get(resourceName);

      if(sound == null)
      {
         SoundFactory.CACHE.add(resourceName, new CacheElementSound(resourceName, referenceClass));

         sound = SoundFactory.CACHE.get(resourceName);
      }

      return sound;
   }

   /**
    * Get sound form url
    * 
    * @param url
    *           URL where the sound lies
    * @return The sound or {@code null} if sound can't be get
    */
   public static JHelpSound getSoundFromURL(final URL url)
   {
      final String path = url.toString();

      JHelpSound sound = SoundFactory.CACHE.get(path);
      if(sound == null)
      {
         SoundFactory.CACHE.add(path, new CacheElementSound(url));

         sound = SoundFactory.CACHE.get(path);
      }
      return sound;
   }
}