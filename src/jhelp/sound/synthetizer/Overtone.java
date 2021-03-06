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

/**
 * Overtone<br>
 * <br>
 * Last modification : 28 mars 2009<br>
 * Version 0.0.0<br>
 * 
 * @author JHelp
 */
public class Overtone
{
   /** Already created overtones */
   private static final Overtone[][] overtones = new Overtone[10][12];

   /**
    * Obtain/create overtone
    * 
    * @param gamut
    *           Gamut in [0, 9]
    * @param overtoneName
    *           Overtone name
    * @return Overtone
    */
   public static Overtone obtainOvertone(final int gamut, final OvertoneName overtoneName)
   {
      if(overtoneName == null)
      {
         throw new NullPointerException("overtoneName musn't be null");
      }
      if((gamut < 0) || (gamut > 9))
      {
         throw new IllegalArgumentException("gamut must be in [0, 9], not " + gamut);
      }
      if(Overtone.overtones[gamut][overtoneName.getOvertone()] == null)
      {
         Overtone.overtones[gamut][overtoneName.getOvertone()] = new Overtone(gamut, overtoneName);
      }
      return Overtone.overtones[gamut][overtoneName.getOvertone()];
   }

   /** Gamut */
   private final int          gamut;
   /** Overtone code */
   private final int          overtoneCode;
   /** Overtone name */
   private final OvertoneName overtoneName;

   /**
    * Constructs Overtone
    * 
    * @param gamut
    *           Gamut
    * @param overtoneName
    *           Overtone name
    */
   private Overtone(final int gamut, final OvertoneName overtoneName)
   {
      this.gamut = gamut;
      this.overtoneName = overtoneName;
      this.overtoneCode = 3 + overtoneName.getOvertone() + (12 * gamut);
   }

   /**
    * Return gamut
    * 
    * @return gamut
    */
   public int getGamut()
   {
      return this.gamut;
   }

   /**
    * Return overtoneCode
    * 
    * @return overtoneCode
    */
   public int getOvertoneCode()
   {
      return this.overtoneCode;
   }

   /**
    * Return overtoneName
    * 
    * @return overtoneName
    */
   public OvertoneName getOvertoneName()
   {
      return this.overtoneName;
   }

   /**
    * String representation
    * 
    * @return String representation
    * @see java.lang.Object#toString()
    */
   @Override
   public String toString()
   {
      return this.overtoneName.name() + " [" + this.gamut + "]";
   }
}