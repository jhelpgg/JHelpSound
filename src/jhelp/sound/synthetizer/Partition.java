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

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Partition for one instrument<br>
 * It is a list of group note<br>
 * <br>
 * Last modification : 28 mars 2009<br>
 * Version 0.0.0<br>
 * 
 * @author JHelp
 */
public class Partition
      implements Iterable<OvertoneGroup>
{
   /** Instrument played this partition */
   private int                            instrument;
   /** Group notes */
   private final ArrayList<OvertoneGroup> overtones;

   /**
    * Constructs Partition
    */
   public Partition()
   {
      this.overtones = new ArrayList<OvertoneGroup>();
      this.instrument = 0;
   }

   /**
    * Add overtone group
    * 
    * @param overtoneGroup
    *           Overtone group to add
    */
   public void addOvertoneGroup(final OvertoneGroup overtoneGroup)
   {
      this.overtones.add(overtoneGroup);
   }

   /**
    * Clear partition
    */
   public void clear()
   {
      this.overtones.clear();
   }

   /**
    * Clear partition and all overtone group in it
    */
   public void clearAll()
   {
      for(final OvertoneGroup overtoneGroup : this.overtones)
      {
         overtoneGroup.clear();
      }
      this.overtones.clear();
   }

   /**
    * Return instrument
    * 
    * @return instrument
    */
   public int getInstrument()
   {
      return this.instrument;
   }

   /**
    * Number of overtone group
    * 
    * @return Number of overtone group
    */
   public int getNumberOfOvertoneGroups()
   {
      return this.overtones.size();
   }

   /**
    * Obtain overtone group
    * 
    * @param index
    *           Overtone group index
    * @return Overtone group
    */
   public OvertoneGroup getOvertoneGroup(final int index)
   {
      return this.overtones.get(index);
   }

   /**
    * Insert overtone group
    * 
    * @param overtoneGroup
    *           Overtone group to insert
    * @param index
    *           Index where insert
    */
   public void insertOvertoneGroup(final OvertoneGroup overtoneGroup, final int index)
   {
      this.overtones.add(index, overtoneGroup);
   }

   /**
    * Overtone group iterator
    * 
    * @return Overtone group iterator
    * @see java.lang.Iterable#iterator()
    */
   @Override
   public Iterator<OvertoneGroup> iterator()
   {
      return this.overtones.iterator();
   }

   /**
    * Remove overtone group
    * 
    * @param overtoneGroup
    *           Overtone group to remove
    */
   public void removeOvertoneGroup(final OvertoneGroup overtoneGroup)
   {
      this.overtones.remove(overtoneGroup);
   }

   /**
    * Modify instrument
    * 
    * @param instrument
    *           New instrument value
    */
   public void setInstrument(final int instrument)
   {
      this.instrument = instrument;
   }
}