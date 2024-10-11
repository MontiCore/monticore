/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.parser.antlr;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
public class ParseVisitorEntry {
  // Common
  public boolean astList, astOptional;

  public boolean ruleList, ruleOptional;

  public String usageName;

  public String condition;

  // root
  public boolean isRoot;

  // NonTermRef
  public String cast;
  public String tmpName;
  public boolean isLexNT;
  public String action;

  // SubRules (extends, etc.)
  public String subrule;

  // Token
  String constantLexName;

  String convert; // convert function, used by lex

  // ConstantGroup
  String constantValue;
  List<String[]> constantValues;

  // ( A | B)* => no else
  boolean noAltElse = false;

  public List<ParseVisitorEntry> alternatives = new ArrayList<>();

  List<ParseVisitorEntry> blockComponents = new ArrayList<>();


  List<String> allOptConditions = new ArrayList<>();


  public boolean isAlternative() {
    return !this.alternatives.isEmpty();
  }

  public boolean isRoot() {
    return isRoot;
  }

  public List<ParseVisitorEntry> getAlternatives() {
    return alternatives;
  }

  public boolean isList() {
    return astList;
  }

  public String getUsageName() {
    return usageName;
  }

  public String getCondition() {
    return condition;
  }

  public String getCast() {
    return cast;
  }

  public String getTmpName() {
    return tmpName;
  }

  public boolean isLexNT() {
    return isLexNT;
  }

  public String getConstantLexName() {
    return constantLexName;
  }

  public String getConvert() {
    return convert;
  }

  public List<ParseVisitorEntry> getBlockComponents() {
    return blockComponents;
  }

  public List<String> getAllOptConditions() {
    return allOptConditions;
  }

  public boolean isAstList() {
    return astList;
  }

  public boolean isAstOptional() {
    return astOptional;
  }

  public boolean isRuleList() {
    return ruleList;
  }

  public boolean isRuleOptional() {
    return ruleOptional;
  }

  public String getConstantValue() {
    return constantValue;
  }

  public List<String[]> getConstantValues() {
    return constantValues;
  }

  public boolean isNoAltElse() {
    return noAltElse;
  }

  public String getAction() {
    return action;
  }

  public String getSubrule() {
    return subrule;
  }

  @Override
  public String toString() {
    return "\nParseVisitorEntry{" +
            "astList=" + astList +
            ", astOptional=" + astOptional +
            ", ruleList=" + ruleList +
            ", ruleOptional=" + ruleOptional +
            ", usageName='" + usageName + '\'' +
            ", condition='" + condition + '\'' +
            ", cast='" + cast + '\'' +
            ", tmpName='" + tmpName + '\'' +
            ", isLexNT=" + isLexNT +
            ", constantLexName='" + constantLexName + '\'' +
            ", alternatives=" + alternatives +
            ", blockComponents=" + blockComponents +
            ", allOptConditions=" + allOptConditions +
            '}' + '\n';
  }

  // Set the noAltElse recursively on all alts
  public void setNoAltElseRec(boolean noAltElse) {
    this.noAltElse = noAltElse;
    this.blockComponents.forEach(r->r.alternatives.forEach(rr->rr.setNoAltElseRec(noAltElse)));
  }
}
