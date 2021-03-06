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
package jhelp.sound.mp3;

import java.io.IOException;
import java.io.InputStream;

import jhelp.util.Utilities;
import jhelp.util.debug.Debug;
import jhelp.util.debug.DebugLevel;

/**
 * Special stream for MP3, that can be paused and stopped
 * 
 * @author JHelp
 */
class ControlBufferedInputStream
      extends InputStream
      implements Runnable
{
   /** Buffer start size */
   private static final int  BUFFER_SIZE = 1 << 14;
   /** Step for expand buffer if need */
   private static final int  EXPAND_SIZE = ControlBufferedInputStream.BUFFER_SIZE >> 3;
   /** Sound buffer */
   private byte[]            buffer;
   /** Indicates if sound finished */
   private boolean           finished;
   /** Stream to read */
   private final InputStream inputStream;
   /** Synchronization lock */
   private final Object      lock        = new Object();
   /** Indicates if we are in pause */
   private boolean           pause;
   /** Sound data size */
   private int               size;
   /** Indicates a synchronization waiting */
   private boolean           waiting;

   /**
    * Create a new instance of ControlBufferedInputStream
    * 
    * @param inputStream
    *           Stream to read
    */
   public ControlBufferedInputStream(final InputStream inputStream)
   {
      if(inputStream == null)
      {
         throw new NullPointerException("inputStream musn't be null");
      }

      this.inputStream = inputStream;
      this.finished = false;
      this.pause = false;
      this.waiting = false;
      this.size = 0;
      this.buffer = new byte[ControlBufferedInputStream.BUFFER_SIZE];
      final Thread thread = new Thread(this);
      thread.start();
   }

   /**
    * Number of bytes can be read <br>
    * <br>
    * <b>Parent documentation:</b><br>
    * {@inheritDoc}
    * 
    * @return Number of bytes can be read
    * @throws IOException
    *            On read issue
    * @see java.io.InputStream#available()
    */
   @Override
   public int available() throws IOException
   {
      return this.inputStream.available();
   }

   /**
    * Close properly the stream <br>
    * <br>
    * <b>Parent documentation:</b><br>
    * {@inheritDoc}
    * 
    * @throws IOException
    *            On closing issue
    * @see java.io.InputStream#close()
    */
   @Override
   public void close() throws IOException
   {
      this.destroy();
   }

   /**
    * Destroy properly the stream
    */
   public void destroy()
   {
      synchronized(this.lock)
      {
         if(!this.finished)
         {
            try
            {
               this.inputStream.close();
            }
            catch(final IOException exception)
            {
               Debug.printException(exception);
            }

            if(this.waiting)
            {
               this.lock.notify();
            }
         }
      }
   }

   /**
    * Does nothing <br>
    * <br>
    * <b>Parent documentation:</b><br>
    * {@inheritDoc}
    * 
    * @param readlimit
    *           Unused
    * @see java.io.InputStream#mark(int)
    */
   @Override
   public synchronized void mark(final int readlimit)
   {
   }

   /**
    * Mark aren't supported <br>
    * <br>
    * <b>Parent documentation:</b><br>
    * {@inheritDoc}
    * 
    * @return {@code false}
    * @see java.io.InputStream#markSupported()
    */
   @Override
   public boolean markSupported()
   {
      return false;
   }

   /**
    * Read next byte in stream.<br>
    * Return -1 if no more to read <br>
    * <br>
    * <b>Parent documentation:</b><br>
    * {@inheritDoc}
    * 
    * @return read byte or -1
    * @throws IOException
    *            On read issue
    * @see java.io.InputStream#read()
    */
   @Override
   public int read() throws IOException
   {
      final byte[] b = new byte[1];
      final int read = this.read(b);

      if(read < 0)
      {
         return -1;
      }

      return b[0] & 0xFF;
   }

   /**
    * Read an array of bytes.<br>
    * Does same as {@link #read(byte[], int, int) read(b, 0, b.length} <br>
    * <br>
    * <b>Parent documentation:</b><br>
    * {@inheritDoc}
    * 
    * @param b
    *           Array to fill
    * @return Number of byte read
    * @throws IOException
    *            On read issue
    * @see java.io.InputStream#read(byte[])
    */
   @Override
   public int read(final byte[] b) throws IOException
   {
      return this.read(b, 0, b.length);
   }

   /**
    * Read an array of bytes <br>
    * <br>
    * <b>Parent documentation:</b><br>
    * {@inheritDoc}
    * 
    * @param b
    *           Array to fill
    * @param off
    *           Where start to fil the given array
    * @param len
    *           Number of byte to read
    * @return Number of read bytes
    * @throws IOException
    *            On read issue
    * @see java.io.InputStream#read(byte[], int, int)
    */
   @Override
   public int read(final byte[] b, final int off, final int len) throws IOException
   {
      if(b == null)
      {
         throw new NullPointerException();
      }
      else if((off < 0) || (len < 0) || (len > (b.length - off)))
      {
         throw new IndexOutOfBoundsException();
      }
      else if(len == 0)
      {
         return 0;
      }

      if((this.finished) || (this.pause))
      {
         return -1;
      }

      int read;

      synchronized(this.lock)
      {
         read = Math.min(len, this.size);
         System.arraycopy(this.buffer, 0, b, off, read);
         System.arraycopy(this.buffer, read, this.buffer, 0, this.buffer.length - read);
         this.size -= read;
      }

      if(this.size <= 0)
      {
         Utilities.sleep(32);
      }

      return read;
   }

   /**
    * Doe nothing <br>
    * <br>
    * <b>Parent documentation:</b><br>
    * {@inheritDoc}
    * 
    * @throws IOException
    *            Never happen
    * @see java.io.InputStream#reset()
    */
   @Override
   public synchronized void reset() throws IOException
   {
   }

   /**
    * Fill the buffer in background <br>
    * <br>
    * <b>Parent documentation:</b><br>
    * {@inheritDoc}
    * 
    * @see java.lang.Runnable#run()
    */
   @Override
   public void run()
   {
      int read = -1;
      int sleep;
      do
      {
         sleep = 16;

         synchronized(this.lock)
         {
            if((this.size + ControlBufferedInputStream.EXPAND_SIZE) > this.buffer.length)
            {
               final byte[] temp = new byte[this.size + ControlBufferedInputStream.EXPAND_SIZE];
               System.arraycopy(this.buffer, 0, temp, 0, this.size);
               this.buffer = temp;

               sleep = 1024;
            }

            try
            {
               read = this.inputStream.read(this.buffer, this.size, this.buffer.length - this.size);
            }
            catch(final IOException exception)
            {
               read = -1;
               sleep = 1;
               Debug.printException(exception);
            }

            this.size += read;
            Debug.println(DebugLevel.VERBOSE, "size=", this.size);
         }

         Utilities.sleep(sleep);

         while(this.pause)
         {
            synchronized(this.lock)
            {
               this.waiting = true;
               try
               {
                  this.lock.wait();
               }
               catch(final Exception ignored)
               {
               }
               this.waiting = false;
            }
         }
      }
      while(read >= 0);

      this.finished = true;
      try
      {
         this.inputStream.close();
      }
      catch(final IOException exception)
      {
         Debug.printException(exception);
      }
   }

   /**
    * Change pause status
    * 
    * @param pause
    *           New pause status
    */
   public void setPause(final boolean pause)
   {
      if(this.pause == pause)
      {
         return;
      }

      this.pause = pause;

      if(!pause)
      {
         synchronized(this.lock)
         {
            if(this.waiting)
            {
               this.lock.notify();
            }
         }
      }
   }

   /**
    * Skip a number of bytes <br>
    * <br>
    * <b>Parent documentation:</b><br>
    * {@inheritDoc}
    * 
    * @param n
    *           Number to skip
    * @return Number really skipped
    * @throws IOException
    *            On reading issue
    * @see java.io.InputStream#skip(long)
    */
   @Override
   public long skip(final long n) throws IOException
   {
      return this.inputStream.skip(n);
   }
}