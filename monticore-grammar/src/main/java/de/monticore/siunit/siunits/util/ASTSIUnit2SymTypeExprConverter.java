// (c) https://github.com/MontiCore/monticore
package de.monticore.siunit.siunits.util;

import de.monticore.literals.mccommonliterals._ast.ASTSignedLiteral;
import de.monticore.siunit.siunits.SIUnitsMill;
import de.monticore.siunit.siunits._ast.ASTCelsiusFahrenheit;
import de.monticore.siunit.siunits._ast.ASTSIUnit;
import de.monticore.siunit.siunits._ast.ASTSIUnitDimensionless;
import de.monticore.siunit.siunits._ast.ASTSIUnitGroupPrimitive;
import de.monticore.siunit.siunits._ast.ASTSIUnitKindGroupWithExponent;
import de.monticore.siunit.siunits._ast.ASTSIUnitPrimitive;
import de.monticore.siunit.siunits._ast.ASTSIUnitWithPrefix;
import de.monticore.siunit.siunits._ast.ASTSIUnitWithoutPrefix;
import de.monticore.types.check.SIUnitBasic;
import de.monticore.types.check.SymTypeExpressionFactory;
import de.monticore.types.check.SymTypeOfSIUnit;
import de.se_rwth.commons.logging.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static de.monticore.siunit.siunits._parser.SIUnitsAntlrParser.prefix;
import static de.monticore.siunit.siunits._parser.SIUnitsAntlrParser.unitWithPrefix;
import static de.monticore.siunit.siunits._parser.SIUnitsAntlrParser.unitWithoutPrefix;

/**
 * Helper, to convert ASTSIUnits to SymTypeExpressions
 */
public class ASTSIUnit2SymTypeExprConverter {

  // reuse the definitions given by SIUnits.mc4
  /**
   * Prefix with unit, only some units apply
   */
  protected static final String PREFIX_UNIT_PATTERN =
      "(" + prefix + unitWithPrefix + ")";
  /**
   * Unit (no prefix)
   */
  protected static final String NO_PREFIX_UNIT_PATTERN =
      "(" + unitWithoutPrefix + "|" + unitWithPrefix + ")";
  /**
   * Unit (no prefix), preferred.
   * In the String "min", 'min' ought to be matched, not 'm'.
   * To guarantee this, this pattern is applied first.
   */
  protected static final String NO_PREFIX_PREFERED_UNIT_PATTERN =
      "(ha|min|mol|Np|dB|Hz|Wb|lm|lx|Bq|Sv)";
  /**
   * Prefix (no unit)
   */
  protected static final String PREFIX_PATTERN =
      prefix;
  /**
   * SIUnit group
   */
  protected static final String GROUP_PATTERN =
      "((" + PREFIX_UNIT_PATTERN + "|" + NO_PREFIX_UNIT_PATTERN + ")+)";

  // never expected to happen:
  protected static final String INTERNAL_LOGIC_ERROR =
      "0x51210 internal error: Could not evaluate SIUnit input,"
          + "but it was expected to be evaluable."
          + " This is most likely an internal programming error, "
          + " or the SIUnits.mc4 has been changed."
          + " Input: ";

  public static SymTypeOfSIUnit createSIUnit(ASTSIUnit ast) {
    List<SIUnitBasic> numerator;
    List<SIUnitBasic> denominator;
    if (ast.isPresentOne()) {
      numerator = new ArrayList<>();
      denominator = createSIUnit(ast.getDenominator());
    }
    else if (ast.isPresentDenominator()) {
      numerator = createSIUnit(ast.getNumerator());
      denominator = createSIUnit(ast.getDenominator());
    }
    else {
      numerator = createSIUnit(ast.getSIUnitPrimitive());
      denominator = new ArrayList<>();
    }
    return SymTypeExpressionFactory.createSIUnit(numerator, denominator);
  }

  protected static List<SIUnitBasic> createSIUnit(ASTSIUnitPrimitive ast) {
    List<SIUnitBasic> result;
    if (ast.isPresentSIUnitWithPrefix()) {
      result = createSIUnit(ast.getSIUnitWithPrefix());
    }
    else if (ast.isPresentSIUnitWithoutPrefix()) {
      result = createSIUnit(ast.getSIUnitWithoutPrefix());
    }
    else if (ast.isPresentSIUnitDimensionless()) {
      result = List.of(createSIUnit(ast.getSIUnitDimensionless()));
    }
    else if (ast.isPresentCelsiusFahrenheit()) {
      result = List.of(createSIUnit(ast.getCelsiusFahrenheit()));
    }
    else {
      result = createSIUnit(ast.getSIUnitKindGroupWithExponent());
    }
    return result;
  }

  protected static List<SIUnitBasic> createSIUnit(ASTSIUnitWithPrefix ast) {
    if (ast.isPresentName()) {
      return string2SIUnitBasics(ast.getName());
    }
    else {
      return string2SIUnitBasics(ast.getNonNameUnit());
    }
  }

  protected static List<SIUnitBasic> createSIUnit(ASTSIUnitWithoutPrefix ast) {
    if (ast.isPresentName()) {
      return string2SIUnitBasics(ast.getName());
    }
    else {
      return string2SIUnitBasics(ast.getNonNameUnit());
    }
  }

  protected static SIUnitBasic createSIUnit(ASTSIUnitDimensionless ast) {
    if (!ast.isPresentUnit()) {
      return SymTypeExpressionFactory.createSIUnitBasic("º");
    }
    else {
      return SymTypeExpressionFactory.createSIUnitBasic(ast.getUnit());
    }
  }

  protected static SIUnitBasic createSIUnit(ASTCelsiusFahrenheit ast) {
    return SymTypeExpressionFactory.createSIUnitBasic("º" + ast.getUnit());
  }

  protected static List<SIUnitBasic> createSIUnit(ASTSIUnitKindGroupWithExponent ast) {
    List<SIUnitBasic> result = new ArrayList<>();
    for (int i = 0; i < ast.sizeExponent(); i++) {
      result.addAll(createSIUnit(ast.getSIUnitGroupPrimitive(i)));
      // the last one is the one that the exponent applies to
      int exp = getValue(ast.getExponent(i));
      result.get(result.size() - 1).setExponent(exp);
    }
    if (ast.sizeSIUnitGroupPrimitives() > ast.sizeExponent()) {
      int lastIndex = ast.sizeSIUnitGroupPrimitives() - 1;
      result.addAll(createSIUnit(ast.getSIUnitGroupPrimitive(lastIndex)));
    }
    return result;
  }

  protected static List<SIUnitBasic> createSIUnit(ASTSIUnitGroupPrimitive ast) {
    if (ast.isPresentSIUnitWithPrefix()) {
      return createSIUnit(ast.getSIUnitWithPrefix());
    }
    else {
      return createSIUnit(ast.getSIUnitWithoutPrefix());
    }
  }

  /**
   * takes the String given by ASTSIUnitWith[out]Prefix
   * (and only those, no "/", no exponent)
   * and converts it into a list of SIUnitBasic
   */
  protected static List<SIUnitBasic> string2SIUnitBasics(String inputStr) {
    assertValidSIUnitGroup(inputStr);
    List<SIUnitBasic> result = new ArrayList<>();
    // We COULD write a grammar for this method,
    // but it would be useful for this method only.
    // Runtime can be improved if required.

    // we have prefixes and units in a list,
    // they need to be split
    // "^" to match only start of String
    // "(?!ol|in)" to avoid issues with "lmin/lmol" having "lm" matched
    Pattern prefixPat =
        Pattern.compile("^" + PREFIX_PATTERN + "(?!ol|in)");
    Pattern unitWithPrefixPat =
        Pattern.compile("^" + PREFIX_UNIT_PATTERN + "(?!ol|in)");
    Pattern unitWithoutPrefixPat =
        Pattern.compile("^" + NO_PREFIX_UNIT_PATTERN + "(?!ol|in)");
    Pattern unitWithoutPrefixPrefPat =
        Pattern.compile("^" + NO_PREFIX_PREFERED_UNIT_PATTERN + "(?!ol|in)");
    // hint: longest potential finding of a unit (with prefix) is 5 chars long,
    // e.g., "dakat"
    String toBeParsed = inputStr;
    while (!toBeParsed.isEmpty()) {
      Optional<String> prefix = Optional.empty();
      String unit;
      // start by searching for prefix + unit
      Matcher unitWithPrefixMat = unitWithPrefixPat.matcher(toBeParsed);
      if (unitWithPrefixMat.find()) {
        String prefixedUnit = unitWithPrefixMat.group();
        Matcher prefixMat = prefixPat.matcher(prefixedUnit);
        if (prefixMat.find()) {
          // remove the prefix
          prefix = Optional.of(prefixMat.group());
          toBeParsed = toBeParsed.substring(prefixMat.end());
        }
        else {
          Log.error(INTERNAL_LOGIC_ERROR + inputStr);
          return Collections.emptyList();
        }
      }
      // search for unit without prefix,
      // but prefer 'min' over 'm', etc.
      Matcher unitMat = unitWithoutPrefixPrefPat.matcher(toBeParsed);
      if (!unitMat.find()) {
        unitMat = unitWithoutPrefixPat.matcher(toBeParsed);
        if (!unitMat.find()) {
          Log.error(INTERNAL_LOGIC_ERROR + inputStr);
          return Collections.emptyList();
        }
      }
      // remove the unit
      unit = unitMat.group();
      toBeParsed = toBeParsed.substring(unitMat.end());
      // construct the SIUnitBasic using the extracted (prefix +) unit
      SIUnitBasic siUnitBasic =
          SymTypeExpressionFactory.createSIUnitBasic(unit);
      if (prefix.isPresent()) {
        siUnitBasic.setPrefix(prefix.get());
      }
      result.add(siUnitBasic);
    }
    return result;
  }

  /**
   * logs an error if the input is not a valid SIUnitGroup
   */
  protected static boolean assertValidSIUnitGroup(String inputStr) {
    if (inputStr.matches(GROUP_PATTERN)) {
      return true;
    }
    else {
      Log.error("0x51211 Input \"" + inputStr + "\" is not a SI unit group");
      return false;
    }
  }

  protected static int getValue(ASTSignedLiteral lit) {
    int res;
    String litStr = SIUnitsMill.prettyPrint(lit, false);
    res = Integer.parseInt(litStr);
    return res;
  }

}
