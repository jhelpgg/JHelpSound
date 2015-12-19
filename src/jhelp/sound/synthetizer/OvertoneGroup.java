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
 * Overtone group<br>
 * It is list of overtone played in same time, duration and intensity<br>
 * <br>
 * Last modification : 28 mars 2009<br>
 * Version 0.0.0<br>
 * 
 * @author JHelp
 */
public class OvertoneGroup
      implements Iterable<Overtone>
{
   /** Overtones played */
   private final ArrayList<Overtone> group;
   /** Strength on key, intensity */
   private int                       strength;
   /** Proportional duration time */
   private double                    time;
   /** Duration in millisecond. (Use on play back) */
   long                              timeDuration;
   /** Actual time pass. (Use on play back) */
   long                              timePass;

   /**
    * Constructs OvertoneGroup
    * 
    * @param time
    *           Proportional time
    * @param strength
    *           Strength, intensity, velocity
    */
   public OvertoneGroup(final double time, final int strength)
   {
      this.group = new ArrayList<Overtone>();
      this.time = time;
      this.strength = strength;
   }

   /**
    * Add overtone
    * 
    * @param overtone
    *           Overtone add
    */
   public void addOvertone(final Overtone overtone)
   {
      if(this.group.contains(overtone) == false)
      {
         this.group.add(overtone);
      }
   }

   /**
    * Clear the group
    */
   public void clear()
   {
      this.group.clear();
   }

   /**
    * Return strength
    * 
    * @return strength
    */
   public int getStrength()
   {
      return this.strength;
   }

   /**
    * Return proportional time
    * 
    * @return proportional time
    */
   public double getTime()
   {
      return this.time;
   }

   /**
    * Indicates if the group is empty
    * 
    * @return {@code true} if group is empty
    */
   public boolean isEmpty()
   {
      return this.group.isEmpty();
   }

   /**
    * Iterator on overtones
    * 
    * @return Iterator on overtones
    * @see java.lang.Iterable#iterator()
    */
   @Override
   public Iterator<Overtone> iterator()
   {
      return this.group.iterator();
   }

   /**
    * Remove overtone
    * 
    * @param overtone
    *           Overtone to remove
    */
   public void removeOvertone(final Overtone overtone)
   {
      this.group.remove(overtone);
   }

   /**
    * Modify strength
    * 
    * @param strength
    *           New strength value
    */
   public void setStrength(final int strength)
   {
      this.strength = strength;
   }

   /**
    * Modify proportional time
    * 
    * @param time
    *           New proportional time value
    */
   public void setTime(final double time)
   {
      this.time = time;
   }
}