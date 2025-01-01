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
import org.antlr.v4.runtime.atn.RuleTransition;
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
        if (recognizer.getState() != ATNState.INVALID_STATE_NUMBER) {
          boolean nameExpected = containsRule(((Parser) recognizer).getExpectedTokens(), recognizer.getVocabulary(),
                                              "Name");
          if (nameExpected) {
            // We have received an unwanted token (msg: mismatched input), but also expect a Name
            // (Keywords are excluded from the Name production - to include them, Name& (plus keywords) should be used)
            msg = msg.replace("mismatched input", "mismatched keyword");
          }

          // Due to our substitute-no-keyword handling, we remove no-keywords from the expected list (as they are included within Name)
          if (parser.getNoKeywordRuleNames() != null) {
            int index = msg.indexOf("' expecting "); // We strip everything after the expecting
            msg = msg.substring(0, index) + "', expecting ";

            // Check for the rules which the ATN would change into using epsilon transitions (to find nokeywor rules)
            Set<Map.Entry<Integer, String>> epsilonRules = new HashSet<>();
            getExpectedRulesWithTokens(recognizer.getATN(), recognizer.getState(), recognizer.getVocabulary(),
                                       new HashMap<>(), epsilonRules);

            List<String> noKeywordRules = extractNoKeywordTokens(recognizer, epsilonRules);

            msg += String.join(", ", noKeywordRules);
          }
        }
        // add the found token type to the message
        String foundSymbolName = parser.getVocabulary().getSymbolicName(((CommonToken) offendingSymbol).getType());
        if (foundSymbolName != null && !foundSymbolName.isEmpty()) {
          msg += " (found: " + foundSymbolName + ")";
        }
      } else if (e == null && msg.startsWith("extraneous input '")
              && containsRule(((Parser) recognizer).getExpectedTokens(), recognizer.getVocabulary(), "Name")) {
        // We have received an unwanted token (msg: extraneous input), but also expect a Name*
        // (Keywords are excluded from the Name production - to include them, Name& (plus keywords) should be used)
        // (*): The name might actually be a nokeyword production, i.e., with a semantic predicate

        // Check for the rules which the ATN would change into using epsilon transitions (to find nokeywor rules)
        Set<Map.Entry<Integer, String>> epsilonRules = new HashSet<>();
        getExpectedRulesWithTokens(recognizer.getATN(), recognizer.getState(), recognizer.getVocabulary(), new HashMap<>(), epsilonRules);

        List<String> noKeywordRules = extractNoKeywordTokens(recognizer, epsilonRules);

        msg = msg.replace("extraneous input", "unexpected keyword");
        msg = msg.replaceFirst("' expecting", "', expecting");

        // Handle nokeyword rules, if present
        if (!noKeywordRules.isEmpty()) {
          msg = msg.substring(0, msg.indexOf("', expecting ") + "', expecting ".length());
          // Join as [a.b.c.d] as a, b, c or d
          msg += String.join(" or ",
                                       String.join(", ", noKeywordRules.subList(0, noKeywordRules.size() - 1)),
                                       noKeywordRules.get(noKeywordRules.size() - 1)
                                      );
        }
      } else if (e instanceof NoViableAltException) {
        // This case is most likely when the ATN found correct tokens (such as Name),
        // but a predicate (such as nokeyword) prevented it
        String expectedTokens = getExpectedTokensWithoutNoKeywords(recognizer, e.getExpectedTokens(), List.of("Name"));

        // Check for the rules which the ATN would change into using epsilon transitions
        Set<Map.Entry<Integer, String>> epsilonRules = new HashSet<>();
        getExpectedRulesWithTokens(recognizer.getATN(), e.getOffendingState(), recognizer.getVocabulary(), new HashMap<>(), epsilonRules);

        List<String> noKeywordRules = extractNoKeywordTokens(recognizer, epsilonRules);

        if (!noKeywordRules.isEmpty()) {
          // Join as [a.b.c.d] as a, b, c or d
          expectedTokens = String.join(" or ",
                                       String.join(", ", noKeywordRules.subList(0, noKeywordRules.size() - 1)),
                                       noKeywordRules.get(noKeywordRules.size() - 1)
                                      );
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
      // Remove our substitute-names-rule from the stack, as they are not visible to the outside
      rules.remove("Name__mc_incl_nokeywords");
      rules.remove("Name__mc_plus_keywords");
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

  protected String getExpectedTokensWithoutNoKeywords(Recognizer<?, ?> recognizer, IntervalSet expectedTokens, Collection<String> excludedSymbolicNames) {
    IntervalSet toOutput = new IntervalSet();
    List<String> noKeywordSymbolicNames = new ArrayList<>(Arrays.asList(parser.getNoKeywordRuleNames()));
    noKeywordSymbolicNames.removeAll(excludedSymbolicNames);
    for (var t : expectedTokens.toSet()) {
      if (!noKeywordSymbolicNames.contains(recognizer.getVocabulary().getSymbolicName(t)))
        toOutput.add(t);
    }
    return toOutput.toString(recognizer.getVocabulary());
  }

  private static List<String> extractNoKeywordTokens(Recognizer<?, ?> recognizer, Set<Map.Entry<Integer, String>> epsilonRules) {
    // Turn the next expected rules into a human readable format:
    List<String> noKeywordRules = epsilonRules.stream().map(r -> {
      // r.key = ruleIndex, r.value=next tokens of the transition(s)
      if (r.getValue().startsWith("'"))  // already a terminal
        return r.getValue();
      // Check if the rule is a noKeyword rule (added by the MC generator)
      // the expected token (r.value) is most likely a Name (but constrained by a predicate)
      String rulename = recognizer.getRuleNames()[r.getKey()];
      // Another rule would have been possible, but the predicate did not allow it
      // We just output the expected token with a hint in that case
      if (rulename.equals("name__mc_incl_nokeywords")) // internal substitute => no additional constraints
        return r.getValue();
      return r.getValue() + " (with additional constraints from " + rulename + ")";
    }).collect(Collectors.toList());
    return noKeywordRules;
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
   * but we also return the rule numbers.
   * We also only return the "Name" transition of the all name-including-no-keywords states
   * @param expected a set of ruleIndex -> expected token(s) entries
   * @return whether an empty input is accepted
   */
  public boolean getExpectedRulesWithTokens(ATN atn, int stateNumber, Vocabulary vocabulary, Map<Integer, Boolean> visitedStates, Set<Map.Entry<Integer, String>> expected) {
    if (stateNumber < 0 || stateNumber >= atn.states.size() || visitedStates.containsKey(stateNumber)) {
      return visitedStates.get(stateNumber);
    }

    visitedStates.put(stateNumber, false);

    ATNState state = atn.states.get(stateNumber);
    // Stop Backtracking in case of empty?
    if (state.getStateType() == ATNState.RULE_STOP) {
      visitedStates.put(stateNumber, true);
      return true;
    }
    boolean mightBeEmptyA = false;
    for (Transition t : state.getTransitions()) {
      if (t.isEpsilon() && t.target.stateNumber != ATNState.INVALID_STATE_NUMBER) {
        if (parser.getRuleNames()[t.target.ruleIndex].equals("name__mc_incl_nokeywords")) {
          // We do not show all non-keywords, instead only return Name
          expected.add(Map.entry(t.target.ruleIndex, "Name"));
          break;
        }
        // Follow the epsilon transition
        boolean mightBeEmpty = getExpectedRulesWithTokens(atn, t.target.stateNumber, vocabulary, visitedStates, expected);
        if (mightBeEmpty) {
          // The rule allows empty input between RULE_START and RULE_STOP
          if (t.getSerializationType() == Transition.RULE) {
            // Continue with the next transition of this rule
            mightBeEmpty = getExpectedRulesWithTokens(atn, ((RuleTransition) t).followState.stateNumber, vocabulary, visitedStates, expected);
            if (mightBeEmpty)
              mightBeEmptyA = true;
          } else {
            // Pass the might-be-empty to the state calling this state
            mightBeEmptyA = true;
          }
        }
      } else {
        // A non epsilon transition =>
        expected.add(Map.entry(t.target.ruleIndex, atn.nextTokens(state).toString(vocabulary)));
      }
    }
    if (mightBeEmptyA)
      visitedStates.put(stateNumber, true);
    return mightBeEmptyA;
  }

}
