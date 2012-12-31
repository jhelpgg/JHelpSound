/**
 * Project : JHelpSound<br>
 * Package : jhelp.sound.synthetizer<br>
 * Class : Morsel<br>
 * Date : 28 mars 2009<br>
 * By JHelp
 */
package jhelp.sound.synthetizer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import jhelp.util.debug.Debug;
import jhelp.util.xml.DynamicWriteXML;
import jhelp.xml.ExceptionParseXML;
import jhelp.xml.InvalidParameterValueException;
import jhelp.xml.InvalidTextException;
import jhelp.xml.MissingRequiredParameterException;
import jhelp.xml.ParseXMLlistener;
import jhelp.xml.ParserXML;
import jhelp.xml.UnexpectedEndOfMarkup;
import jhelp.xml.UnexpectedEndOfParse;

/**
 * Morsel<br>
 * Consist of list of partitions<br>
 * We can save/load morsel<br>
 * Format is ziped XML description<br>
 * <br>
 * Last modification : 28 mars 2009<br>
 * Version 0.0.0<br>
 * 
 * @author JHelp
 */
public class Morsel
      implements Iterable<Partition>
{
   /**
    * Parser XML listener<br>
    * Use on load a morsel file <br>
    * <br>
    * Last modification : 2 avr. 2009<br>
    * Version 0.0.0<br>
    * 
    * @author JHelp
    */
   private class MorselParseXMLlistener
         implements ParseXMLlistener
   {
      /** Last read overtone group */
      private OvertoneGroup overtoneGroup;
      /** Last read partition */
      private Partition     partition;

      /**
       * Constructs MorselParseXMLlistener
       */
      public MorselParseXMLlistener()
      {
      }

      /**
       * Call if comment found
       * 
       * @param comment
       *           Comment found
       * @see jhelp.xml.ParseXMLlistener#commentFind(java.lang.String)
       */
      @Override
      public void commentFind(final String comment)
      {
      }

      /**
       * Call when meet a close of markup
       * 
       * @param markupName
       *           Markup closed
       * @throws UnexpectedEndOfMarkup
       *            If markup musn't close now
       * @see jhelp.xml.ParseXMLlistener#endMarkup(java.lang.String)
       */
      @Override
      public void endMarkup(final String markupName) throws UnexpectedEndOfMarkup
      {
         if(markupName.equals(Morsel.MARKUP_PARTITION) == true)
         {
            this.partition = null;
         }
         else if(markupName.equals(Morsel.MARKUP_OVERTONE_GROUP) == true)
         {
            this.overtoneGroup = null;
         }
      }

      /**
       * Call when parsing end
       * 
       * @throws UnexpectedEndOfParse
       *            If parse can't end now
       * @see jhelp.xml.ParseXMLlistener#endParse()
       */
      @Override
      public void endParse() throws UnexpectedEndOfParse
      {
         this.partition = null;
         this.overtoneGroup = null;
      }

      /**
       * Call when fatal error appends on parsing an force stop now parsing
       * 
       * @param exceptionParseXML
       *           Error append
       * @see jhelp.xml.ParseXMLlistener#exceptionForceEndParse(jhelp.xml.ExceptionParseXML)
       */
      @Override
      public void exceptionForceEndParse(final ExceptionParseXML exceptionParseXML)
      {
         Debug.printException(exceptionParseXML);
      }

      /**
       * Call when start of markup detect
       * 
       * @param markupName
       *           Markup opened
       * @param parameters
       *           Parameters (key, value) for this markup
       * @throws MissingRequiredParameterException
       *            If a requiered parameter missing (not in the hashtable)
       * @throws InvalidParameterValueException
       *            If a parameter is not valid
       * @see jhelp.xml.ParseXMLlistener#startMakup(java.lang.String, java.util.Hashtable)
       */
      @Override
      public void startMakup(final String markupName, final Hashtable<String, String> parameters) throws MissingRequiredParameterException, InvalidParameterValueException
      {
         if(markupName.equals(Morsel.MARKUP_PARTITION) == true)
         {
            this.partition = new Partition();
            final String instrumentName = parameters.get(Morsel.MARKUP_PARTITION_instrument);
            if(instrumentName == null)
            {
               throw new MissingRequiredParameterException(Morsel.MARKUP_PARTITION_instrument, Morsel.MARKUP_PARTITION);
            }
            try
            {
               final Synthetiser synthetyser = Synthetiser.obtainSynthetiser();
               this.partition.setInstrument(synthetyser.obtainInstrument(instrumentName));
            }
            catch(final SynthetyserException e)
            {
               throw new InvalidParameterValueException(Morsel.MARKUP_PARTITION_instrument, Morsel.MARKUP_PARTITION, "Synthetyzer can't be initiated", e);
            }
            Morsel.this.partitions.add(this.partition);
         }
         else if(markupName.equals(Morsel.MARKUP_OVERTONE_GROUP) == true)
         {
            if(this.partition == null)
            {
               throw new IllegalStateException("OvertoneGroup must be on Partition");
            }
            double time = 0;
            int strength = 0;
            String parameter = parameters.get(Morsel.MARKUP_OVERTONE_GROUP_time);
            if(parameter == null)
            {
               throw new MissingRequiredParameterException(Morsel.MARKUP_OVERTONE_GROUP_time, Morsel.MARKUP_OVERTONE_GROUP);
            }
            try
            {
               time = Double.parseDouble(parameter);
            }
            catch(final Exception exception)
            {
               throw new InvalidParameterValueException(Morsel.MARKUP_OVERTONE_GROUP_time, Morsel.MARKUP_OVERTONE_GROUP, "Is not a double : " + parameter, exception);
            }
            if(time < 0)
            {
               throw new InvalidParameterValueException(Morsel.MARKUP_OVERTONE_GROUP_time, Morsel.MARKUP_OVERTONE_GROUP, "Is negative : " + parameter);
            }
            parameter = parameters.get(Morsel.MARKUP_OVERTONE_GROUP_strength);
            if(parameter == null)
            {
               throw new MissingRequiredParameterException(Morsel.MARKUP_OVERTONE_GROUP_strength, Morsel.MARKUP_OVERTONE_GROUP);
            }
            try
            {
               strength = Integer.parseInt(parameter);
            }
            catch(final Exception exception)
            {
               throw new InvalidParameterValueException(Morsel.MARKUP_OVERTONE_GROUP_strength, Morsel.MARKUP_OVERTONE_GROUP, "Is not a int : " + parameter, exception);
            }
            if(strength < 0)
            {
               throw new InvalidParameterValueException(Morsel.MARKUP_OVERTONE_GROUP_strength, Morsel.MARKUP_OVERTONE_GROUP, "Is negative : " + parameter);
            }
            this.overtoneGroup = new OvertoneGroup(time, strength);
            this.partition.addOvertoneGroup(this.overtoneGroup);
         }
         else if(markupName.equals(Morsel.MARKUP_OVERTONE) == true)
         {
            if(this.overtoneGroup == null)
            {
               throw new IllegalStateException("Overtone must be on OvertoneGroup");
            }
            int gamut = 0;
            int overtone = 0;
            OvertoneName overtoneName = null;
            String parameter = parameters.get(Morsel.MARKUP_OVERTONE_gamut);
            if(parameter == null)
            {
               throw new MissingRequiredParameterException(Morsel.MARKUP_OVERTONE_gamut, Morsel.MARKUP_OVERTONE);
            }
            try
            {
               gamut = Integer.parseInt(parameter);
            }
            catch(final Exception exception)
            {
               throw new InvalidParameterValueException(Morsel.MARKUP_OVERTONE_gamut, Morsel.MARKUP_OVERTONE_GROUP, "Is not a int : " + parameter, exception);
            }
            parameter = parameters.get(Morsel.MARKUP_OVERTONE_overtoneName);
            if(parameter == null)
            {
               throw new MissingRequiredParameterException(Morsel.MARKUP_OVERTONE_overtoneName, Morsel.MARKUP_OVERTONE);
            }
            try
            {
               overtone = Integer.parseInt(parameter);
               overtoneName = OvertoneName.obtainOvertoneName(overtone);
            }
            catch(final Exception exception)
            {
               throw new InvalidParameterValueException(Morsel.MARKUP_OVERTONE_overtoneName, Morsel.MARKUP_OVERTONE_GROUP, "Is not a valid overtone : " + parameter, exception);
            }
            this.overtoneGroup.addOvertone(Overtone.obtainOvertone(gamut, overtoneName));
         }
      }

      /**
       * Call when parsing start
       * 
       * @see jhelp.xml.ParseXMLlistener#startParse()
       */
      @Override
      public void startParse()
      {
         Morsel.this.clearAll();
         this.partition = null;
         this.overtoneGroup = null;
      }

      /**
       * Call when test find
       * 
       * @param text
       *           Test found
       * @throws InvalidTextException
       *            If text is invalid in the current context
       * @see jhelp.xml.ParseXMLlistener#textFind(java.lang.String)
       */
      @Override
      public void textFind(final String text) throws InvalidTextException
      {
      }
   }

   /** Markup morsel */
   final static String                  MARKUP_MORSEL                  = "MORSEL";
   /** Markup overtone */
   final static String                  MARKUP_OVERTONE                = "OVERTONE";
   /** Markup overtone parameter gamut */
   final static String                  MARKUP_OVERTONE_gamut          = "gamut";
   /** Markup overtone group */
   final static String                  MARKUP_OVERTONE_GROUP          = "OVERTONE_GROUP";
   /** Markup overtone group parameter strength */
   final static String                  MARKUP_OVERTONE_GROUP_strength = "strength";
   /** Markup overtone group parameter time */
   final static String                  MARKUP_OVERTONE_GROUP_time     = "time";
   /** Markup overtone parameter overtone name */
   final static String                  MARKUP_OVERTONE_overtoneName   = "overtoneName";
   /** Markup overtone partition */
   final static String                  MARKUP_PARTITION               = "PARTITION";
   /** Markup overtone partition parameter instrument */
   final static String                  MARKUP_PARTITION_instrument    = "instrument";
   /** Listener when parsing to create morsel on loading */
   private final MorselParseXMLlistener morselParseXMLlistener;
   /** Partitions list */
   ArrayList<Partition>                 partitions;

   /**
    * Constructs Morsel
    */
   public Morsel()
   {
      this.morselParseXMLlistener = new MorselParseXMLlistener();
      this.partitions = new ArrayList<Partition>();
   }

   /**
    * Add partition
    * 
    * @param partition
    *           Partition add
    */
   public void addPartition(final Partition partition)
   {
      this.partitions.add(partition);
   }

   /**
    * Clear morsel
    */
   public void clear()
   {
      this.partitions.clear();
   }

   /**
    * Clear morsel and all partition and all overtone grroup
    */
   public void clearAll()
   {
      for(final Partition partition : this.partitions)
      {
         partition.clearAll();
      }
      this.partitions.clear();
   }

   /**
    * Number of partition
    * 
    * @return Number of partition
    */
   public int getNumberOfPartition()
   {
      return this.partitions.size();
   }

   /**
    * Obtain parititon
    * 
    * @param index
    *           Index
    * @return Partition
    */
   public Partition getPartition(final int index)
   {
      return this.partitions.get(index);
   }

   /**
    * Insert partition
    * 
    * @param partition
    *           Partition to insert
    * @param index
    *           Index for insertion
    */
   public void insertPartition(final Partition partition, final int index)
   {
      this.partitions.add(index, partition);
   }

   /**
    * Partition iterator
    * 
    * @return Partition iterator
    * @see java.lang.Iterable#iterator()
    */
   @Override
   public Iterator<Partition> iterator()
   {
      return this.partitions.iterator();
   }

   /**
    * Load morsel
    * 
    * @param inputStream
    *           Stream to read
    * @throws IOException
    *            On reading problem
    * @throws ExceptionParseXML
    *            If stream not a valid morsel
    */
   public void loadMorsel(final InputStream inputStream) throws IOException, ExceptionParseXML
   {
      final ZipInputStream zipInputStream = new ZipInputStream(inputStream);
      zipInputStream.getNextEntry();

      final ParserXML parserXML = new ParserXML();
      parserXML.parse(this.morselParseXMLlistener, zipInputStream);
   }

   /**
    * Remove partition
    * 
    * @param partition
    *           Partition to remove
    */
   public void removePartition(final Partition partition)
   {
      this.partitions.remove(partition);
   }

   /**
    * Save morsel
    * 
    * @param outputStream
    *           Stream where write
    * @throws IOException
    *            On writing problem
    * @throws SynthetyserException
    *            If synthetyzer can't be initiated
    */
   public void saveMorsel(final OutputStream outputStream) throws IOException, SynthetyserException
   {
      if(outputStream == null)
      {
         throw new NullPointerException("outputStream musn't be null");
      }
      final ZipOutputStream zipOutputStream = new ZipOutputStream(outputStream);
      zipOutputStream.setMethod(ZipOutputStream.DEFLATED);
      zipOutputStream.setLevel(9);
      final ZipEntry zipEntry = new ZipEntry("morsel.xml");
      zipEntry.setMethod(ZipEntry.DEFLATED);
      zipOutputStream.putNextEntry(zipEntry);

      final Synthetiser synthetyser = Synthetiser.obtainSynthetiser();
      final DynamicWriteXML dynamicWriteXML = new DynamicWriteXML(zipOutputStream, true, false, false);
      dynamicWriteXML.openMarkup(Morsel.MARKUP_MORSEL);
      for(final Partition partition : this.partitions)
      {
         dynamicWriteXML.openMarkup(Morsel.MARKUP_PARTITION);
         dynamicWriteXML.appendParameter(Morsel.MARKUP_PARTITION_instrument, synthetyser.obtainNameOfInstrument(partition.getInstrument()));
         for(final OvertoneGroup overtoneGroup : partition)
         {
            dynamicWriteXML.openMarkup(Morsel.MARKUP_OVERTONE_GROUP);
            dynamicWriteXML.appendParameter(Morsel.MARKUP_OVERTONE_GROUP_strength, String.valueOf(overtoneGroup.getStrength()));
            dynamicWriteXML.appendParameter(Morsel.MARKUP_OVERTONE_GROUP_time, String.valueOf(overtoneGroup.getTime()));
            for(final Overtone overtone : overtoneGroup)
            {
               dynamicWriteXML.openMarkup(Morsel.MARKUP_OVERTONE);
               dynamicWriteXML.appendParameter(Morsel.MARKUP_OVERTONE_gamut, String.valueOf(overtone.getGamut()));
               dynamicWriteXML.appendParameter(Morsel.MARKUP_OVERTONE_overtoneName, String.valueOf(overtone.getOvertoneName().getOvertone()));
               dynamicWriteXML.closeMarkup();
            }
            dynamicWriteXML.closeMarkup();
         }
         dynamicWriteXML.closeMarkup();
      }
      dynamicWriteXML.closeMarkup();

      zipOutputStream.flush();
      zipOutputStream.closeEntry();
      zipOutputStream.finish();
      zipOutputStream.close();
   }
}