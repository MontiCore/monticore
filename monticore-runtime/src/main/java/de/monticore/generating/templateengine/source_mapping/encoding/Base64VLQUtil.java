package de.monticore.generating.templateengine.source_mapping.encoding;

// Based on Mozilla base64-vlq java script
public class Base64VLQUtil {

  public final static char[] base64Encoding = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/".toCharArray();

  private static final byte VLQ_BASE_SHIFT = 5;

  // 0b 0010 0000
  private static final byte VLQ_BASE = 1 << VLQ_BASE_SHIFT;

  // 0b 0001 1111
  private static final byte VLQ_BASE_MASK = VLQ_BASE - 1;

  // 0b 0010 0000
  private static final byte VLQ_CONTINUATION_BIT = VLQ_BASE;


  public static String encodeBase64(int number) {
    StringBuilder encoded = new StringBuilder();
    int digit;

    int vlq = toVLQSigned(number);

    do {
      digit = vlq & VLQ_BASE_MASK;
      vlq >>>= VLQ_BASE_SHIFT;
      if (vlq > 0) {
        // There are still more digits in this value, so we must make sure the
        // continuation bit is marked.
        digit |= VLQ_CONTINUATION_BIT;
      }
      encoded.append(base64Encoding[digit]);
    } while (vlq > 0);

    return encoded.toString();
  }

  /**
   *
   * @param number
   * @return a positive number where the lsb determines the sign of the number
   */
  private static int toVLQSigned(int number) {
    return number < 0 ? (-number << 1) + 1 : (number << 1);
  }

}
