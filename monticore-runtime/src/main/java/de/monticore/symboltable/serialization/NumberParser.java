/* (c) https://github.com/MontiCore/monticore */

package de.monticore.symboltable.serialization;

import de.se_rwth.commons.logging.Log;

/**
 * This class is used by the JsonLexer. It realizes a DFA to check whether a char sequence is a
 * valid json number, i.e. if it conforms to the regex:
 * "-?(0|([1-9][0-9]*))(\\.[0-9]+)?((e|E)(-|\\+)?[0-9]+)?"
 */
public class NumberParser {

  /**
   * enum with states within the lexing of a number token. The state is not visible outside of the
   * number parser, therefore this enumeration is located within the number parser class.
   */
  enum State {
    INITIAL,
    AFTER_SIGNUM,
    AFTER_ZERO,
    IN_NUMBER,
    AFTER_POINT,
    AFTER_POINT_AND_ONE_DIGIT,
    AFTER_E,
    AFTER_E_SIGNUM,
    IN_EXPONENT_AFTER_ONE_DIGIT,
    ERROR;
  }

  protected State currentState;

  protected StringBuilder result;

  public NumberParser() {
    reset();
  }

  /**
   * resets the current state to the initial state and resets the read input
   */
  public void reset() {
    this.currentState = State.INITIAL;
    this.result = new StringBuilder();
  }

  /**
   * returns the string of the number read so far
   *
   * @return
   */
  public String getResult() {
    return result.toString();
  }

  /**
   * this realizes the automaton described in www.json.org
   *
   * @param input
   */
  public void step(char input) {
    result.append(input);
    switch (currentState) {
      case INITIAL:
        readInitial(input);
        return;
      case AFTER_SIGNUM:
        readAfterSignum(input);
        return;
      case AFTER_ZERO:
        readAfterZero(input);
        return;
      case IN_NUMBER:
        readInNumber(input);
        return;
      case AFTER_POINT:
        readAfterPoint(input);
        return;
      case AFTER_POINT_AND_ONE_DIGIT:
        readAfterPointAndOneDigit(input);
        return;
      case AFTER_E:
        readAfterE(input);
        return;
      case AFTER_E_SIGNUM:
        readAfterESignum(input);
        return;
      case IN_EXPONENT_AFTER_ONE_DIGIT:
        readInExponentAndOneDigit(input);
        return;
      default:
        Log.error(
            "0xA0809 Unexpected character in number: " + input);
    }
  }

  protected void readInExponentAndOneDigit(char input) {
    if ("0123456789".indexOf(input) > -1) {
      // Stay in current state
    }
    else {
      Log.error(
          "0xA0808 invalid character in number: " + input + ". Expecting one of: '0123456789'");
      currentState = State.ERROR;
    }
  }

  protected void readAfterESignum(char input) {
    if ("0123456789".indexOf(input) > -1) {
      currentState = State.IN_EXPONENT_AFTER_ONE_DIGIT;
    }
    else {
      Log.error(
          "0xA0807 invalid character in number: " + input + ". Expecting one of: '0123456789'");
      currentState = State.ERROR;
    }
  }

  /**
   * after an exponent character 'e', either the signum of the exponent oder a digit can follow
   *
   * @param input
   */
  protected void readAfterE(char input) {
    if ("+-".indexOf(input) > -1) {
      currentState = State.AFTER_E_SIGNUM;
    }
    else if ("0123456789".indexOf(input) > -1) {
      currentState = State.IN_EXPONENT_AFTER_ONE_DIGIT;
    }
    else {
      Log.error(
          "0xA0806 invalid character in number: " + input
              + ". Expecting one of: '+-0123456789'");
      currentState = State.ERROR;
    }
  }

  /**
   * after the decimal point and a conscutive digit, the parser is in a final state and any digits,
   * or the exponent character can follow.
   *
   * @param input
   */
  protected void readAfterPointAndOneDigit(char input) {
    if ("0123456789".indexOf(input) > -1) {
      // Stay in current state
    }
    else if ('e' == input || 'E' == input) {
      currentState = State.AFTER_E;
    }
    else {
      Log.error(
          "0xA0805 invalid character in number: " + input
              + ". Expecting one of: '0123456789eE'");
      currentState = State.ERROR;
    }
  }

  /**
   * after the decimal point, any digit can follow.
   *
   * @param input
   */
  protected void readAfterPoint(char input) {
    if ("0123456789".indexOf(input) > -1) {
      currentState = State.AFTER_POINT_AND_ONE_DIGIT;
    }
    else {
      Log.error(
          "0xA0804 invalid character in number: " + input
              + ". Expecting one of: '0123456789' after a decimal point.");
      currentState = State.ERROR;
    }
  }

  /**
   * after the a minus character or a zero, any digits can follow, or a decimal point, or an exponent character
   *
   * @param input
   */
  protected void readInNumber(char input) {
    if ("0123456789".indexOf(input) > -1) {
      //Stay in current state
    }
    else if ('.' == input) {
      currentState = State.AFTER_POINT;
    }
    else if ('e' == input || 'E' == input) {
      currentState = State.AFTER_E;
    }
    else {
      Log.error("0xA0803 invalid character in number: " + input);
      currentState = State.ERROR;
    }
  }

  /**
   * after zero, any non-zero(!) digit can follow, or a decimal point or an exponent character.
   *
   * @param input
   */
  protected void readAfterZero(char input) {
    if ("123456789".indexOf(input) > -1) {
      currentState = State.IN_NUMBER;
    }
    else if ('.' == input) {
      currentState = State.AFTER_POINT;
    }
    else if ('e' == input || 'E' == input) {
      currentState = State.AFTER_E;
    }
    else if (input == '0') {
      Log.error("0xA0801 invalid character in number: " + input
          + ". There must not be more than a single '0' at the beginning of a number");
      currentState = State.ERROR;
    }
    else {
      Log.error("0xA0802 invalid character in number: " + input);
      currentState = State.ERROR;
    }
  }

  protected void readAfterSignum(char input) {
    if (input == '0') {
      currentState = State.AFTER_ZERO;
    }
    else if ("123456789".indexOf(input) > -1) {
      currentState = State.IN_NUMBER;
    }
    else if (input == '.') {
      Log.error("0xA0598 invalid character in number: " + input
          + ". There must be a digit before a '.'");
      currentState = State.ERROR;
    }
    else if (input == 'e' || input == 'E') {
      Log.error("0xA0599 invalid character in number: " + input
          + ". There must be a digit before an 'e'");
      currentState = State.ERROR;
    }
    else {
      Log.error("0xA0800 invalid character in number: " + input);
      currentState = State.ERROR;
    }
  }

  protected void readInitial(char input) {
    if ('-' == input) {
      currentState = State.AFTER_SIGNUM;
    }
    else if ('0' == input) {
      currentState = State.AFTER_ZERO;
    }
    else if ("123456789".indexOf(input) > -1) {
      currentState = State.IN_NUMBER;
    }
    else {
      Log.error(
          "0xA0597 invalid character in number: " + input + ". Expecting one of: '-0123456789'");
      currentState = State.ERROR;
    }
  }

  /**
   * returns true, iff the number parser is in an error state
   *
   * @return
   */
  public boolean hasError() {
    return currentState == State.ERROR;
  }

  /**
   * returns true, iff the number parser is in a final state, i.e., if the string lexd so far is a valid number
   *
   * @return
   */
  public boolean isInFinalState() {
    return currentState == State.AFTER_ZERO
        || currentState == State.IN_NUMBER
        || currentState == State.AFTER_POINT_AND_ONE_DIGIT
        || currentState == State.IN_EXPONENT_AFTER_ONE_DIGIT;
  }

}
