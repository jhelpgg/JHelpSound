/**
 * Project : JHelpSound<br>
 * Package : jhelp.sound.player<br>
 * Class : ControlInputStream<br>
 * Date : 5 avr. 2009<br>
 * By JHelp
 */
package jhelp.sound.mp3;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Input stream we can control the reading <br>
 * <br>
 * Last modification : 5 avr. 2009<br>
 * Version 0.0.0<br>
 * 
 * @author JHelp
 */
class ControlInputStream
      extends InputStream
{
   /**
    * Create a control input stream from a given input stream
    * 
    * @param inputStream
    *           Input stream base
    * @return Creates control input stream
    * @throws IOException
    *            On reading problem
    */
   public static ControlInputStream createControlInputStream(final InputStream inputStream) throws IOException
   {
      ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
      byte[] temp = new byte[4096];
      int read = inputStream.read(temp);
      while(read >= 0)
      {
         byteArrayOutputStream.write(temp, 0, read);
         read = inputStream.read(temp);
      }

      byteArrayOutputStream.flush();
      byteArrayOutputStream.close();

      inputStream.close();

      temp = byteArrayOutputStream.toByteArray();
      byteArrayOutputStream = null;

      final ControlInputStream controlInputStream = new ControlInputStream();
      controlInputStream.data = temp;

      temp = null;

      return controlInputStream;
   }

   /** Data to read */
   private byte[]  data;
   /** Actual read index */
   private int     index;
   /** Last mark */
   private int     mark;
   /** Pause status */
   private boolean pause;

   /**
    * Constructs ControlInputStream
    */
   private ControlInputStream()
   {
      this.index = 0;
      this.mark = 0;
      this.pause = false;
   }

   /**
    * Data size
    * 
    * @return Data size
    * @throws IOException
    *            Not throw here (But keep to respect InputStream extends)
    * @see java.io.InputStream#available()
    */
   @Override
   public int available() throws IOException
   {
      return this.data.length;
   }

   /**
    * Do nothing here (But keep to respect InputStream extends)
    * 
    * @throws IOException
    *            Not throw here (But keep to respect InputStream extends)
    * @see java.io.InputStream#close()
    */
   @Override
   public void close() throws IOException
   {
   }

   /**
    * Destroy the control input stream.<br>
    * Call it to free memory and will not continue to use it
    */
   public void destroy()
   {
      this.data = null;
   }

   /**
    * Actual position
    * 
    * @return Actual position
    */
   public int getPosition()
   {
      return this.index;
   }

   /**
    * Indicates if control is in pause
    * 
    * @return {@code true} if control is in pause
    */
   public boolean isPause()
   {
      return this.pause;
   }

   /**
    * Mark the actual position
    * 
    * @param readlimit
    *           Limit to keep
    * @see java.io.InputStream#mark(int)
    */
   @Override
   public synchronized void mark(final int readlimit)
   {
      this.mark = this.index;
   }

   /**
    * Indicates that mark are supported
    * 
    * @return {@code true}
    * @see java.io.InputStream#markSupported()
    */
   @Override
   public boolean markSupported()
   {
      return true;
   }

   /**
    * Read a byte
    * 
    * @return Byte read
    * @throws IOException
    *            Not throw here (But keep to respect InputStream extends)
    * @see java.io.InputStream#read()
    */
   @Override
   public int read() throws IOException
   {
      if((this.index >= this.data.length) || (this.pause == true))
      {
         return -1;
      }

      return this.data[this.index++] & 0xFF;
   }

   /**
    * Read several bytes
    * 
    * @param b
    *           Array to fill
    * @return Number of bytes read
    * @throws IOException
    *            Not throw here (But keep to respect InputStream extends)
    * @see java.io.InputStream#read(byte[])
    */
   @Override
   public int read(final byte[] b) throws IOException
   {
      return this.read(b, 0, b.length);
   }

   /**
    * Read several bytes
    * 
    * @param b
    *           Array to fill
    * @param off
    *           Where start to fill
    * @param len
    *           Number of desired bytes
    * @return Number of bytes read
    * @throws IOException
    *            Not throw here (But keep to respect InputStream extends)
    * @see java.io.InputStream#read(byte[], int, int)
    */
   @Override
   public int read(final byte[] b, final int off, int len) throws IOException
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

      if((this.index >= this.data.length) || (this.pause == true))
      {
         return -1;
      }

      len = Math.min(len, this.data.length - this.index);
      System.arraycopy(this.data, this.index, b, off, len);
      this.index += len;
      return len;
   }

   /**
    * Reset to last mark
    * 
    * @throws IOException
    *            Not throw here (But keep to respect InputStream extends)
    * @see java.io.InputStream#reset()
    */
   @Override
   public synchronized void reset() throws IOException
   {
      this.index = this.mark;
   }

   /**
    * Change pause status
    * 
    * @param pause
    *           New pause status
    */
   public void setPause(final boolean pause)
   {
      this.pause = pause;
   }

   /**
    * Change actual position
    * 
    * @param position
    *           New position
    */
   public void setPosition(final int position)
   {
      if((position < 0) || (position >= this.data.length))
      {
         throw new IllegalArgumentException("position must be in [0, " + this.data.length + "[ not : " + position);
      }

      this.index = position;
   }

   /**
    * Skip number of bytes
    * 
    * @param n
    *           Number of bytes to skip
    * @return Number of bytes really skipped
    * @throws IOException
    *            Not throw here (But keep to respect InputStream extends)
    * @see java.io.InputStream#skip(long)
    */
   @Override
   public long skip(long n) throws IOException
   {
      n = Math.min(n, this.data.length - this.index);
      this.index += n;
      return n;
   }
}