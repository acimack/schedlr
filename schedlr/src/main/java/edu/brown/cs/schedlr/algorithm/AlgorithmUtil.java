package edu.brown.cs.schedlr.algorithm;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

public final class AlgorithmUtil {

  private AlgorithmUtil() {
  }

  /**
   * Finds the date relative to the current day.
   *
   * @param date - the date
   * @return the long that represents the day of the week
   */
  public static long dateToRelativeWeekDay(Date date) throws ParseException {
    Date today = new Date();
    Date currDate = zeroesDate(today);
    long diffInMillies = zeroesDate(date).getTime() - currDate.getTime();
    return TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);
  }

  /**
   * Creates a date that starts at 12am.
   *
   * @param date - the date to remove minutes and secons from.
   * @return the Date with no minutes or seconds
   */
  public static Date zeroesDate(Date date) throws ParseException {
    DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
    return formatter.parse(formatter.format(date));
  }

  /**
   * Returns the total amount of FreeBlock time.
   *
   * @param blockList - the list of FreeBlocks
   * @return the amount of free time
   */
  public static int totalFreeBlockTime(List<FreeBlock> blockList) {
    int sum = 0;
    for (ITimeBlock block : blockList) {
      sum += block.getTotalTime();
    }
    return sum;
  }

  /**
   * Returns the total amount of TaskBlock time.
   *
   * @param blockList - the list of TaskBlocks
   * @return the amount of task time
   */
  public static int totalTaskBlockTime(List<TaskBlock> blockList) {
    int sum = 0;
    for (ITimeBlock block : blockList) {
      sum += block.getTotalTime();
    }
    return sum;
  }

  /**
   * Given a string and a salt, salts and hashes the string
   * @param item - a string, to be salted and hashed
   * @param salt - an array of bytes, the salt
   * @return an array of bytes representing the salted and hashed item
   */
  public static byte[] saltAndHash(String item, byte[] salt) {
    try {
      MessageDigest md = MessageDigest.getInstance("SHA-512");
      md.update(salt);
      byte[] toReturn = md.digest(item.getBytes(StandardCharsets.UTF_8));
      md.reset();
      return toReturn;
    } catch (Exception e) {
      return null;
    }
  }

  /**
   * Generates a salt
   * @return an array of bytes representing the salt
   */
  public static byte[] generateSalt() {
    SecureRandom temp = new SecureRandom();
    byte[] salt = new byte[16];
    temp.nextBytes(salt);
    return salt;
  }

  /**
   * Convert a
   * @param bytes
   * @return
   */
  public static String toHex(byte[] bytes) {
    BigInteger temp = new BigInteger(1, bytes);
    String toReturn = temp.toString();
    int padding = (bytes.length * 2) - toReturn.length();
    if (padding > 0) {
      return String.format("%0" + padding + "d", 0) + toReturn;
    } else {
      return toReturn;
    }
  }

  public static byte[] fromHex(String hex) {
    byte[] binary = new byte[hex.length() / 2];
    for (int i = 0; i < binary.length; i++) {
      binary[i] = (byte) Integer.parseInt(hex.substring(2*i, 2*i + 2), 16);
    }
    return binary;
  }
}
