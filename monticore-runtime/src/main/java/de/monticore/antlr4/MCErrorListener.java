/* (c) https://github.com/MontiCore/monticore */

package de.monticore.antlr4;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import de.se_rwth.commons.SourcePosition;
import de.se_rwth.commons.StringTransformations;
import de.se_rwth.commons.logging.Log;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.ATN;
import org.antlr.v4.runtime.atn.ATNState;
import org.antlr.v4.runtime.atn.Transition;
import org.antlr.v4.runtime.misc.Interval;
import org.antlr.v4.runtime.misc.IntervalSet;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class MCErrorListener extends BaseErrorListener {
  
  protected MCParser parser = null;

  /**
   * This character (NO-BREAK SPACE) separates the error message
   * from the context where the error occurred.
   */
  public final static char CONTEXT_SEPARATOR = '\u00A0';
  
  public MCErrorListener(MCParser parser) {
    super();
    this.parser = parser;
  }
  
  @Override
  public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int charPositionInLine, String msg, RecognitionException e) {
    // Improve error message
    if (recognizer instanceof Parser) {
      if ((e instanceof org.antlr.v4.runtime.InputMismatchException) && (offendingSymbol instanceof CommonToken)) {
        // add the found token type to the message
        String s = parser.getVocabulary().getSymbolicName(((CommonToken) offendingSymbol).getType());
        if (s != null && !s.isEmpty()) {
          msg += " (found: " + s + ")";
        }
        if (containsRule(((Parser) recognizer).getExpectedTokens(), recognizer.getVocabulary(), "Name")) {
          // We have received an unwanted token (msg: mismatched input), but also expect a Name
          // (Keywords are excluded from the Name production - to include them, Name& (plus keywords) should be used)
          msg = msg.replace("mismatched input", "mismatched keyword");
          msg = msg.replaceFirst("' expecting", "', expecting");
        }
      } else if (e == null && msg.startsWith("extraneous input '")
              && containsRule(((Parser) recognizer).getExpectedTokens(), recognizer.getVocabulary(), "Name")) {
        // We have received an unwanted token (msg: extraneous input), but also expect a Name
        // (Keywords are excluded from the Name production - to include them, Name& (plus keywords) should be used)
        msg = msg.replace("extraneous input", "unexpected keyword");
        msg = msg.replaceFirst("' expecting", "', expecting");
      } else if (e instanceof FailedPredicateException
              && offendingSymbol instanceof CommonToken
              && msg.startsWith("rule nokeyword_")
              && ((FailedPredicateException) e).getPredicate().matches("next\\(\".*\"\\)")) {
        // mismatched keyword 'keyword', expecting Name
        Matcher m = Pattern.compile("next\\(\"(.*)\"\\)").matcher(((FailedPredicateException) e).getPredicate());
        if (m.matches()) {
          msg = "mismatched input '" + ((CommonToken) offendingSymbol).getText() + "', " +
                  "expecting '" + m.group(1) + "'";
        }
      } else if (e instanceof NoViableAltException) {
        // This case is most likely when the ATN found correct tokens (such as Name),
        // but a predicate (such as nokeyword) prevented it
        String expectedTokens = e.getExpectedTokens().toString(recognizer.getVocabulary());

        // Check for the rules which the ATN would change into using epsilon transitions
        Set<Map.Entry<Integer, String>> epsilonRules = getExpectedRulesWithTokens(recognizer.getATN(),
                e.getOffendingState(), recognizer.getVocabulary(), new HashSet<>());

        Pattern nokeywordPattern = Pattern.compile("nokeyword_(.*)_[0-9]*");
        // Turn the next expected rules into a human readable format:
        String noKeywordRules = epsilonRules.stream().map(r -> {
          // r.key = ruleIndex, r.value=next tokens of the transition(s)
          if (r.getValue().startsWith("'"))  // already a terminal
            return r.getValue();
          // Check if the rule is a noKeyword rule (added by the MC generator)
          // the expected token (r.value) is most likely a Name (but constrained by a predicate)
          String rulename = recognizer.getRuleNames()[r.getKey()];
          Matcher m = nokeywordPattern.matcher(rulename);
          if (m.matches()) // if it is a nokeyword rule, we are able to extract the no-keyword from the rule name
            return "'" + m.group(1) + "'";
          // Another rule would have been possible, but the predicate did not allow it
          // We just output the expected token with a hint in that case
          return r.getValue() + " (with additional constraints from " + rulename + ")";
        }).collect(Collectors.joining(" or "));

        if (!noKeywordRules.isEmpty()) {
          expectedTokens = noKeywordRules;
        }
        msg += ", expecting " + expectedTokens;
      }
      // Determine rule stack without eof-rule
      List<String> stack = ((Parser) recognizer).getRuleInvocationStack();
      List<String> rules = Lists.newArrayList();
      for (int i = stack.size() - 1; i >= 0; i--) {
        if (!(i == stack.size() - 1 && stack.get(i).endsWith("_eof"))) {
          rules.add(StringTransformations.capitalize(stack.get(i)));
        }
      }
      msg += " in rule stack: " + rules;

      // Give additional context: Output the offending line and mark the error position
      TokenSource orig = ((BufferedTokenStream) recognizer.getInputStream()).getTokenSource();
      if (offendingSymbol instanceof CommonToken && recognizer.getInputStream() instanceof BufferedTokenStream) {
        msg += getLineContext(orig.getInputStream(), ((CommonToken) offendingSymbol).getStartIndex(), charPositionInLine);
      }
    } else if (recognizer instanceof Lexer) {
      // Give additional context: Output the offending line and mark the error position
      msg += getLineContext(((Lexer) recognizer).getInputStream(), ((Lexer) recognizer)._tokenStartCharIndex, charPositionInLine);
    }
    Log.error(msg, new SourcePosition(line, charPositionInLine, parser.getFilename()));
    parser.setErrors(true);
  }

  /**
   * @param set        an IntervalSet of tokens
   * @param vocabulary the token vocabulary
   * @param needle     the rule display name to search for
   * @return Whether the interval set contains a rule with the display name of needle
   */
  protected boolean containsRule(IntervalSet set, Vocabulary vocabulary, String needle) {
    for (Interval interval : set.getIntervals()) {
      if (interval.a == interval.b) {
        if (needle.equals(vocabulary.getDisplayName(interval.a)))
          return true;
      } else {
        for (int i = interval.a; i <= interval.b; i++) {
          if (needle.equals(vocabulary.getDisplayName(i)))
            return true;
        }
      }
    }
    return false;
  }

  public String getLineContext(CharStream stream, int startIndex, int charPositionInLine) {
    // returns the offending line and an arrow indicating the offending token
    String offendingLine = getOffendingLine(stream.getText(new Interval(0, stream.size())), startIndex);
    return CONTEXT_SEPARATOR + "\n" + offendingLine + "\n" + Strings.repeat(" ", charPositionInLine) + "^";
  }

  /**
   * @param entireInput the entire input text
   * @param tokenIndex the index of a token to find return its line
   * @return the line
   */
  protected String getOffendingLine(String entireInput, int tokenIndex) {
    String before = entireInput.substring(0, tokenIndex);
    String after = entireInput.substring(tokenIndex);
    int lineStart = before.lastIndexOf("\n");
    int lineEnd = after.indexOf("\n");
    return entireInput.substring(lineStart + 1, lineEnd == -1 ? entireInput.length() : tokenIndex + lineEnd);
  }


  /**
   * Similiar to {@link ATN#getExpectedTokens(int, RuleContext)},
   * but we also return the rule numbers
   * @return a set of ruleIndex -> expected token(s) entries
   */
  public Set<Map.Entry<Integer, String>> getExpectedRulesWithTokens(ATN atn, int stateNumber, Vocabulary vocabulary, Set<Integer> visitedStates) {
    Set<Map.Entry<Integer, String>> expected = new HashSet<>();
    if (stateNumber < 0 || stateNumber >= atn.states.size() || visitedStates.contains(stateNumber)) {
      return expected;
    }

    visitedStates.add(stateNumber);

    ATNState state = atn.states.get(stateNumber);
    for (Transition t : state.getTransitions()) {
      if (t.isEpsilon() && t.target.stateNumber != ATNState.INVALID_STATE_NUMBER) {
        // Follow the epsilon transition
        expected.addAll(getExpectedRulesWithTokens(atn, t.target.stateNumber, vocabulary, visitedStates));
      } else {
        // A non epsilon transition =>
        expected.add(Map.entry(t.target.ruleIndex, atn.nextTokens(state).toString(vocabulary)));
      }
    }
    return expected;
  }

}
