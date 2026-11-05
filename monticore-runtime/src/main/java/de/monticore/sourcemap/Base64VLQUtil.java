/* (c) https://github.com/MontiCore/monticore */

package de.monticore.sourcemap;

/**
 * Utility class for encoding and decoding Base64 VLQ (Variable Length Quantity) values.
 * <p>
 * Based on Mozilla base64-vlq JavaScript.
 */
public class Base64VLQUtil {

  public final static char[] base64Encoding = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/".toCharArray();

  private static final byte VLQ_BASE_SHIFT = 5;

  // 0b 0010 0000
  private static final byte VLQ_BASE = 1 << VLQ_BASE_SHIFT;

  // 0b 0001 1111
  private static final byte VLQ_BASE_MASK = VLQ_BASE - 1;

  // 0b 0010 0000
  private static final byte VLQ_CONTINUATION_BIT = VLQ_BASE;

  /**
   * Encodes the given integer value into a Base64 VLQ (Variable Length Quantity) string
   * representation.
   * <p>
   * This method uses a variable-length encoding scheme to compress the integer and converts
   * it into a Base64 string for compact representation.
   *
   * @param number the integer value to encode can be positive or negative
   * @return the Base64 VLQ encoded string representation of the input integer
   */
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

  private static int toVLQSigned(int number) {
    return number < 0 ? (-number << 1) + 1 : (number << 1);
  }

}
