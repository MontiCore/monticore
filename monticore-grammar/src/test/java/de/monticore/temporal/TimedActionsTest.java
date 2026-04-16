/* (c) https://github.com/MontiCore/monticore */
package de.monticore.temporal;

import de.monticore.expressions.commonexpressions._ast.ASTCallExpression;
import de.monticore.expressions.commonexpressions._ast.ASTPlusExpression;
import de.monticore.expressions.expressionsbasis._ast.ASTLiteralExpression;
import de.monticore.literals.mcliteralsbasis._ast.ASTLiteral;
import de.monticore.runtime.junit.TestWithMCLanguage;
import de.monticore.statements.mccommonstatements._ast.ASTExpressionStatement;
import de.monticore.statements.mccommonstatements._ast.ASTMCJavaBlock;
import de.monticore.statements.mcstatementsbasis._ast.ASTMCBlockStatement;
import de.monticore.statements.mcvardeclarationstatements._ast.ASTLocalVariableDeclarationStatement;
import de.monticore.temporal.escapedtemporalliterals._ast.ASTEscapedTemporalLiteral;
import de.monticore.temporal.temporalbasis._ast.ASTInstant;
import de.monticore.temporal.temporalbasis._ast.ASTPeriod;
import de.monticore.temporal.timedactions.TimedActionsMill;
import de.monticore.temporal.timedactions._ast.ASTActionSequence;
import de.monticore.temporal.timedactions._ast.ASTTimedAction;
import de.monticore.temporal.timedactions._parser.TimedActionsParser;
import de.se_rwth.commons.logging.Log;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

@TestWithMCLanguage(TimedActionsMill.class)
public class TimedActionsTest {
  private TimedActionsParser parser;
  
  @BeforeEach
  void setup() {
    parser = TimedActionsMill.parser();
  }
  
  protected static Stream<Arguments> timedActions() {
    return Stream.of(
        // ISO Formats
        Arguments.of("on 2017-12-04 do pingMembers(\"hi! today is \" + d\"2017-12-04\");"),
        Arguments.of("on 20171204 do pingMembers(\"hi! today is \" + d\"20171204\");"),
        Arguments.of("on 2017-12 do pingMembers(\"hi! today is \" + d\"2017-12\");"),
        Arguments.of("on 2017 do pingMembers(\"hi! today is \" + d\"2017\");"),
        Arguments.of("on 201 do pingMembers(\"hi! today is \" + d\"201\");"),
        Arguments.of("on 20 do pingMembers(\"hi! today is \" + d\"20\");"),
        
        Arguments.of("on +002017-12-04 do pingMembers(\"hi! today is \" + d\"+002017-12-04\");"),
        Arguments.of("on -002017-12-04 do pingMembers(\"hi! today is \" + d\"-002017-12-04\");"),
        Arguments.of("on +002017-12 do pingMembers(\"hi! today is \" + d\"+002017-12\");"),
        Arguments.of("on -002017-12 do pingMembers(\"hi! today is \" + d\"-002017-12\");"),
        Arguments.of("on +0020171204 do pingMembers(\"hi! today is \" + d\"+0020171204\");"),
        Arguments.of("on -0020171204 do pingMembers(\"hi! today is \" + d\"-0020171204\");"),
        Arguments.of("on +002017 do pingMembers(\"hi! today is \" + d\"+002017\");"),
        Arguments.of("on -002017 do pingMembers(\"hi! today is \" + d\"-002017\");"),
        Arguments.of("on +00201 do pingMembers(\"hi! today is \" + d\"+00201\");"),
        Arguments.of("on -00201 do pingMembers(\"hi! today is \" + d\"-00201\");"),
        Arguments.of("on +0020 do pingMembers(\"hi! today is \" + d\"+0020\");"),
        Arguments.of("on -0020 do pingMembers(\"hi! today is \" + d\"-0020\");"),
        
        Arguments.of("on T123000 do pingMembers(\"hi! today is \" + d\"T123000\");"),
        Arguments.of("on T123000.01 do pingMembers(\"hi! today is \" + d\"T123000.01\");"),
        Arguments.of("on T123000,01 do pingMembers(\"hi! today is \" + d\"T123000,01\");"),
        Arguments.of("on T1230 do pingMembers(\"hi! today is \" + d\"T1230\");"),
        Arguments.of("on T1230.01 do pingMembers(\"hi! today is \" + d\"T1230.01\");"),
        Arguments.of("on T1230,01 do pingMembers(\"hi! today is \" + d\"T1230,01\");"),
        Arguments.of("on T12 do pingMembers(\"hi! today is \" + d\"T12\");"),
        Arguments.of("on T12.01 do pingMembers(\"hi! today is \" + d\"T12.01\");"),
        Arguments.of("on T12,01 do pingMembers(\"hi! today is \" + d\"T12,01\");"),
        Arguments.of("on T123000Z do pingMembers(\"hi! today is \" + d\"T123000Z\");"),
        Arguments.of("on T123000+0100 do pingMembers(\"hi! today is \" + d\"T123000+0100\");"),
        Arguments.of("on T123000-02 do pingMembers(\"hi! today is \" + d\"T123000-02\");"),
        
        Arguments.of("on 12:30:00 do pingMembers(\"hi! today is \" + d\"12:30:00\");"),
        Arguments.of("on 12:30 do pingMembers(\"hi! today is \" + d\"12:30\");"),
        Arguments.of("on 12:30:00.01 do pingMembers(\"hi! today is \" + d\"12:30:00.01\");"),
        Arguments.of("on 12:30:00,01 do pingMembers(\"hi! today is \" + d\"12:30:00,01\");"),
        Arguments.of("on 12:30:00Z do pingMembers(\"hi! today is \" + d\"12:30:00Z\");"),
        Arguments.of("on 12:30:00+01:00 do pingMembers(\"hi! today is \" + d\"12:30:00+01:00\");"),
        Arguments.of("on 12:30:00-02 do pingMembers(\"hi! today is \" + d\"12:30:00-02\");"),
        
        Arguments.of("on T12:30:00 do pingMembers(\"hi! today is \" + d\"T12:30:00\");"),
        Arguments.of("on T12:30 do pingMembers(\"hi! today is \" + d\"T12:30\");"),
        Arguments.of("on T12:30:00.01 do pingMembers(\"hi! today is \" + d\"T12:30:00.01\");"),
        Arguments.of("on T12:30:00,01 do pingMembers(\"hi! today is \" + d\"T12:30:00,01\");"),
        Arguments.of("on T12:30:00Z do pingMembers(\"hi! today is \" + d\"T12:30:00Z\");"),
        Arguments.of("on T12:30:00+01:00 do pingMembers(\"hi! today is \" + d\"T12:30:00+01:00\");"),
        Arguments.of("on T12:30:00-02 do pingMembers(\"hi! today is \" + d\"T12:30:00-02\");"),
        
        Arguments.of("on 2017-12-04T12:30:00 do pingMembers(\"hi! today is \" + d\"2017-12-04T12:30:00\");"),
        Arguments.of("on 2017-12-04T12:30 do pingMembers(\"hi! today is \" + d\"2017-12-04T12:30\");"),
        Arguments.of("on 2017-12-04T12 do pingMembers(\"hi! today is \" + d\"2017-12-04T12\");"),
        Arguments.of("on 2017-12-04T12:30:00.01 do pingMembers(\"hi! today is \" + d\"2017-12-04T12:30:00.01\");"),
        Arguments.of("on 2017-12-04T12:30:00,01 do pingMembers(\"hi! today is \" + d\"2017-12-04T12:30:00,01\");"),
        Arguments.of("on 2017-12-04T12:30:00Z do pingMembers(\"hi! today is \" + d\"2017-12-04T12:30:00Z\");"),
        Arguments.of("on 2017-12-04T12:30:00+01:00 do pingMembers(\"hi! today is \" + d\"2017-12-04T12:30:00+01:00\");"),
        Arguments.of("on 2017-12-04T12:30:00-02 do pingMembers(\"hi! today is \" + d\"2017-12-04T12:30:00-02\");"),
        
        Arguments.of("on 2017-12-04T12:30:00 do pingMembers(\"hi! today is \" + 2017-12-04T12:30:00);"),
        Arguments.of("on 2017-12-04T12:30 do pingMembers(\"hi! today is \" + 2017-12-04T12:30);"),
        Arguments.of("on 2017-12-04T12 do pingMembers(\"hi! today is \" + 2017-12-04T12);"),
        Arguments.of("on 2017-12-04T12:30:00.01 do pingMembers(\"hi! today is \" + 2017-12-04T12:30:00.01);"),
        Arguments.of("on 2017-12-04T12:30:00,01 do pingMembers(\"hi! today is \" + 2017-12-04T12:30:00,01);"),
        Arguments.of("on 2017-12-04T12:30:00Z do pingMembers(\"hi! today is \" + 2017-12-04T12:30:00Z);"),
        Arguments.of("on 2017-12-04T12:30:00+01:00 do pingMembers(\"hi! today is \" + 2017-12-04T12:30:00+01:00);"),
        Arguments.of("on 2017-12-04T12:30:00-02 do pingMembers(\"hi! today is \" + 2017-12-04T12:30:00-02);"),
        
        Arguments.of("on 20171204T123000 do pingMembers(\"hi! today is \" + d\"20171204T123000\");"),
        Arguments.of("on 20171204T1230 do pingMembers(\"hi! today is \" + d\"20171204T1230\");"),
        Arguments.of("on 20171204T12 do pingMembers(\"hi! today is \" + d\"20171204T12\");"),
        Arguments.of("on 20171204T123000.01 do pingMembers(\"hi! today is \" + d\"20171204T123000.01\");"),
        Arguments.of("on 20171204T123000,01 do pingMembers(\"hi! today is \" + d\"20171204T123000,01\");"),
        Arguments.of("on 20171204T123000Z do pingMembers(\"hi! today is \" + d\"20171204T123000Z\");"),
        Arguments.of("on 20171204T123000+0100 do pingMembers(\"hi! today is \" + d\"20171204T123000+0100\");"),
        Arguments.of("on 20171204T123000-02 do pingMembers(\"hi! today is \" + d\"20171204T123000-02\");"),
        
        Arguments.of("on 20171204T123000 do pingMembers(\"hi! today is \" + 20171204T123000);"),
        Arguments.of("on 20171204T1230 do pingMembers(\"hi! today is \" + 20171204T1230);"),
        Arguments.of("on 20171204T12 do pingMembers(\"hi! today is \" + 20171204T12);"),
        Arguments.of("on 20171204T123000.01 do pingMembers(\"hi! today is \" + 20171204T123000.01);"),
        Arguments.of("on 20171204T123000,01 do pingMembers(\"hi! today is \" + 20171204T123000,01);"),
        Arguments.of("on 20171204T123000Z do pingMembers(\"hi! today is \" + 20171204T123000Z);"),
        Arguments.of("on 20171204T123000+0100 do pingMembers(\"hi! today is \" + 20171204T123000+0100);"),
        Arguments.of("on 20171204T123000-02 do pingMembers(\"hi! today is \" + 20171204T123000-02);"),
        
        // DE Formats
        Arguments.of("on 04.12.2017 do pingMembers(\"hi! today is \" + d\"04.12.2017\");"),
        Arguments.of("on 12.2017 do pingMembers(\"hi! today is \" + d\"12.2017\");"),
        Arguments.of("on 2017 do pingMembers(\"hi! today is \" + d\"2017\");"),
        
        Arguments.of("on 4. Januar 2017 do pingMembers(\"hi! today is \" + d\"4. Januar 2017\");"),
        Arguments.of("on 4. Februar 2017 do pingMembers(\"hi! today is \" + d\"4. Februar 2017\");"),
        Arguments.of("on 4. März 2017 do pingMembers(\"hi! today is \" + d\"4. März 2017\");"),
        Arguments.of("on 4. April 2017 do pingMembers(\"hi! today is \" + d\"4. April 2017\");"),
        Arguments.of("on 4. Mai 2017 do pingMembers(\"hi! today is \" + d\"4. Mai 2017\");"),
        Arguments.of("on 4. Juni 2017 do pingMembers(\"hi! today is \" + d\"4. Juni 2017\");"),
        Arguments.of("on 4. Juli 2017 do pingMembers(\"hi! today is \" + d\"4. Juli 2017\");"),
        Arguments.of("on 4. August 2017 do pingMembers(\"hi! today is \" + d\"4. August 2017\");"),
        Arguments.of("on 4. September 2017 do pingMembers(\"hi! today is \" + d\"4. September 2017\");"),
        Arguments.of("on 4. Oktober 2017 do pingMembers(\"hi! today is \" + d\"4. Oktober 2017\");"),
        Arguments.of("on 4. November 2017 do pingMembers(\"hi! today is \" + d\"4. November 2017\");"),
        Arguments.of("on 4. Dezember 2017 do pingMembers(\"hi! today is \" + d\"4. Dezember 2017\");"),
        
        Arguments.of("on 4. Januar 2017 do pingMembers(\"hi! today is \" + 4. Januar 2017);"),
        Arguments.of("on 4. Februar 2017 do pingMembers(\"hi! today is \" + 4. Februar 2017);"),
        Arguments.of("on 4. März 2017 do pingMembers(\"hi! today is \" + 4. März 2017);"),
        Arguments.of("on 4. April 2017 do pingMembers(\"hi! today is \" + 4. April 2017);"),
        Arguments.of("on 4. Mai 2017 do pingMembers(\"hi! today is \" + 4. Mai 2017);"),
        Arguments.of("on 4. Juni 2017 do pingMembers(\"hi! today is \" + 4. Juni 2017);"),
        Arguments.of("on 4. Juli 2017 do pingMembers(\"hi! today is \" + 4. Juli 2017);"),
        Arguments.of("on 4. August 2017 do pingMembers(\"hi! today is \" + 4. August 2017);"),
        Arguments.of("on 4. September 2017 do pingMembers(\"hi! today is \" + 4. September 2017);"),
        Arguments.of("on 4. Oktober 2017 do pingMembers(\"hi! today is \" + 4. Oktober 2017);"),
        Arguments.of("on 4. November 2017 do pingMembers(\"hi! today is \" + 4. November 2017);"),
        Arguments.of("on 4. Dezember 2017 do pingMembers(\"hi! today is \" + 4. Dezember 2017);"),
        
        Arguments.of("on 4. Jan. 2017 do pingMembers(\"hi! today is \" + d\"4. Jan. 2017\");"),
        Arguments.of("on 4. Feb. 2017 do pingMembers(\"hi! today is \" + d\"4. Feb. 2017\");"),
        Arguments.of("on 4. Mär. 2017 do pingMembers(\"hi! today is \" + d\"4. Mär. 2017\");"),
        Arguments.of("on 4. Apr. 2017 do pingMembers(\"hi! today is \" + d\"4. Apr. 2017\");"),
        Arguments.of("on 4. Mai. 2017 do pingMembers(\"hi! today is \" + d\"4. Mai. 2017\");"),
        Arguments.of("on 4. Jun. 2017 do pingMembers(\"hi! today is \" + d\"4. Jun. 2017\");"),
        Arguments.of("on 4. Jul. 2017 do pingMembers(\"hi! today is \" + d\"4. Jul. 2017\");"),
        Arguments.of("on 4. Aug. 2017 do pingMembers(\"hi! today is \" + d\"4. Aug. 2017\");"),
        Arguments.of("on 4. Sep. 2017 do pingMembers(\"hi! today is \" + d\"4. Sep. 2017\");"),
        Arguments.of("on 4. Okt. 2017 do pingMembers(\"hi! today is \" + d\"4. Okt. 2017\");"),
        Arguments.of("on 4. Nov. 2017 do pingMembers(\"hi! today is \" + d\"4. Nov. 2017\");"),
        Arguments.of("on 4. Dez. 2017 do pingMembers(\"hi! today is \" + d\"4. Dez. 2017\");"),
        
        Arguments.of("on 4. Jan. 2017 do pingMembers(\"hi! today is \" + 4. Jan. 2017);"),
        Arguments.of("on 4. Feb. 2017 do pingMembers(\"hi! today is \" + 4. Feb. 2017);"),
        Arguments.of("on 4. Mär. 2017 do pingMembers(\"hi! today is \" + 4. Mär. 2017);"),
        Arguments.of("on 4. Apr. 2017 do pingMembers(\"hi! today is \" + 4. Apr. 2017);"),
        Arguments.of("on 4. Mai. 2017 do pingMembers(\"hi! today is \" + 4. Mai. 2017);"),
        Arguments.of("on 4. Jun. 2017 do pingMembers(\"hi! today is \" + 4. Jun. 2017);"),
        Arguments.of("on 4. Jul. 2017 do pingMembers(\"hi! today is \" + 4. Jul. 2017);"),
        Arguments.of("on 4. Aug. 2017 do pingMembers(\"hi! today is \" + 4. Aug. 2017);"),
        Arguments.of("on 4. Sep. 2017 do pingMembers(\"hi! today is \" + 4. Sep. 2017);"),
        Arguments.of("on 4. Okt. 2017 do pingMembers(\"hi! today is \" + 4. Okt. 2017);"),
        Arguments.of("on 4. Nov. 2017 do pingMembers(\"hi! today is \" + 4. Nov. 2017);"),
        Arguments.of("on 4. Dez. 2017 do pingMembers(\"hi! today is \" + 4. Dez. 2017);"),
        
        Arguments.of("on Januar 2017 do pingMembers(\"hi! today is \" + d\"Januar 2017\");"),
        Arguments.of("on Februar 2017 do pingMembers(\"hi! today is \" + d\"Februar 2017\");"),
        Arguments.of("on März 2017 do pingMembers(\"hi! today is \" + d\"März 2017\");"),
        Arguments.of("on April 2017 do pingMembers(\"hi! today is \" + d\"April 2017\");"),
        Arguments.of("on Mai 2017 do pingMembers(\"hi! today is \" + d\"Mai 2017\");"),
        Arguments.of("on Juni 2017 do pingMembers(\"hi! today is \" + d\"Juni 2017\");"),
        Arguments.of("on Juli 2017 do pingMembers(\"hi! today is \" + d\"Juli 2017\");"),
        Arguments.of("on August 2017 do pingMembers(\"hi! today is \" + d\"August 2017\");"),
        Arguments.of("on September 2017 do pingMembers(\"hi! today is \" + d\"September 2017\");"),
        Arguments.of("on Oktober 2017 do pingMembers(\"hi! today is \" + d\"Oktober 2017\");"),
        Arguments.of("on November 2017 do pingMembers(\"hi! today is \" + d\"November 2017\");"),
        Arguments.of("on Dezember 2017 do pingMembers(\"hi! today is \" + d\"Dezember 2017\");"),
        
        Arguments.of("on Januar 2017 do pingMembers(\"hi! today is \" + Januar 2017);"),
        Arguments.of("on Februar 2017 do pingMembers(\"hi! today is \" + Februar 2017);"),
        Arguments.of("on März 2017 do pingMembers(\"hi! today is \" + März 2017);"),
        Arguments.of("on April 2017 do pingMembers(\"hi! today is \" + April 2017);"),
        Arguments.of("on Mai 2017 do pingMembers(\"hi! today is \" + Mai 2017);"),
        Arguments.of("on Juni 2017 do pingMembers(\"hi! today is \" + Juni 2017);"),
        Arguments.of("on Juli 2017 do pingMembers(\"hi! today is \" + Juli 2017);"),
        Arguments.of("on August 2017 do pingMembers(\"hi! today is \" + August 2017);"),
        Arguments.of("on September 2017 do pingMembers(\"hi! today is \" + September 2017);"),
        Arguments.of("on Oktober 2017 do pingMembers(\"hi! today is \" + Oktober 2017);"),
        Arguments.of("on November 2017 do pingMembers(\"hi! today is \" + November 2017);"),
        Arguments.of("on Dezember 2017 do pingMembers(\"hi! today is \" + Dezember 2017);"),
        
        Arguments.of("on Jan. 2017 do pingMembers(\"hi! today is \" + d\"Jan. 2017\");"),
        Arguments.of("on Feb. 2017 do pingMembers(\"hi! today is \" + d\"Feb. 2017\");"),
        Arguments.of("on Mär. 2017 do pingMembers(\"hi! today is \" + d\"Mär. 2017\");"),
        Arguments.of("on Apr. 2017 do pingMembers(\"hi! today is \" + d\"Apr. 2017\");"),
        Arguments.of("on Mai. 2017 do pingMembers(\"hi! today is \" + d\"Mai. 2017\");"),
        Arguments.of("on Jun. 2017 do pingMembers(\"hi! today is \" + d\"Jun. 2017\");"),
        Arguments.of("on Jul. 2017 do pingMembers(\"hi! today is \" + d\"Jul. 2017\");"),
        Arguments.of("on Aug. 2017 do pingMembers(\"hi! today is \" + d\"Aug. 2017\");"),
        Arguments.of("on Sep. 2017 do pingMembers(\"hi! today is \" + d\"Sep. 2017\");"),
        Arguments.of("on Okt. 2017 do pingMembers(\"hi! today is \" + d\"Okt. 2017\");"),
        Arguments.of("on Nov. 2017 do pingMembers(\"hi! today is \" + d\"Nov. 2017\");"),
        Arguments.of("on Dez. 2017 do pingMembers(\"hi! today is \" + d\"Dez. 2017\");"),
        
        Arguments.of("on Jan. 2017 do pingMembers(\"hi! today is \" + Jan. 2017);"),
        Arguments.of("on Feb. 2017 do pingMembers(\"hi! today is \" + Feb. 2017);"),
        Arguments.of("on Mär. 2017 do pingMembers(\"hi! today is \" + Mär. 2017);"),
        Arguments.of("on Apr. 2017 do pingMembers(\"hi! today is \" + Apr. 2017);"),
        Arguments.of("on Mai. 2017 do pingMembers(\"hi! today is \" + Mai. 2017);"),
        Arguments.of("on Jun. 2017 do pingMembers(\"hi! today is \" + Jun. 2017);"),
        Arguments.of("on Jul. 2017 do pingMembers(\"hi! today is \" + Jul. 2017);"),
        Arguments.of("on Aug. 2017 do pingMembers(\"hi! today is \" + Aug. 2017);"),
        Arguments.of("on Sep. 2017 do pingMembers(\"hi! today is \" + Sep. 2017);"),
        Arguments.of("on Okt. 2017 do pingMembers(\"hi! today is \" + Okt. 2017);"),
        Arguments.of("on Nov. 2017 do pingMembers(\"hi! today is \" + Nov. 2017);"),
        Arguments.of("on Dez. 2017 do pingMembers(\"hi! today is \" + Dez. 2017);"),
        
        Arguments.of("on 12:30:01 Uhr do pingMembers(\"hi! today is \" + d\"12:30:01 Uhr\");"),
        Arguments.of("on 12:30 Uhr do pingMembers(\"hi! today is \" + d\"12:30 Uhr\");"),
        Arguments.of("on 12 Uhr do pingMembers(\"hi! today is \" + d\"12 Uhr\");"),
        Arguments.of("on 06:30:01 Uhr do pingMembers(\"hi! today is \" + d\"06:30:01 Uhr\");"),
        Arguments.of("on 06:30 Uhr do pingMembers(\"hi! today is \" + d\"06:30 Uhr\");"),
        Arguments.of("on 6 Uhr do pingMembers(\"hi! today is \" + d\"6 Uhr\");"),
        
        Arguments.of("on 12:30:01 Uhr do pingMembers(\"hi! today is \" + 12:30:01 Uhr);"),
        Arguments.of("on 12:30 Uhr do pingMembers(\"hi! today is \" + 12:30 Uhr);"),
        Arguments.of("on 12 Uhr do pingMembers(\"hi! today is \" + 12 Uhr);"),
        Arguments.of("on 06:30:01 Uhr do pingMembers(\"hi! today is \" + 06:30:01 Uhr);"),
        Arguments.of("on 06:30 Uhr do pingMembers(\"hi! today is \" + 06:30 Uhr);"),
        Arguments.of("on 6 Uhr do pingMembers(\"hi! today is \" + 6 Uhr);"),
        
        Arguments.of("on 04.12.2017 12:30:01 Uhr do pingMembers(\"hi! today is \" + d\"04.12.2017 12:30:01 Uhr\");"),
        Arguments.of("on 04.12.2017 12:30 Uhr do pingMembers(\"hi! today is \" + d\"04.12.2017 12:30 Uhr\");"),
        Arguments.of("on 04.12.2017 12 Uhr do pingMembers(\"hi! today is \" + d\"04.12.2017 12 Uhr\");"),
        Arguments.of("on 04.12.2017 06:30:01 Uhr do pingMembers(\"hi! today is \" + d\"04.12.2017 06:30:01 Uhr\");"),
        Arguments.of("on 04.12.2017 06:30 Uhr do pingMembers(\"hi! today is \" + d\"04.12.2017 06:30 Uhr\");"),
        Arguments.of("on 04.12.2017 6 Uhr do pingMembers(\"hi! today is \" + d\"04.12.2017 6 Uhr\");"),
        
        Arguments.of("on 04.12.2017 12:30:01 Uhr do pingMembers(\"hi! today is \" + 04.12.2017 12:30:01 Uhr);"),
        Arguments.of("on 04.12.2017 12:30 Uhr do pingMembers(\"hi! today is \" + 04.12.2017 12:30 Uhr);"),
        Arguments.of("on 04.12.2017 12 Uhr do pingMembers(\"hi! today is \" + 04.12.2017 12 Uhr);"),
        Arguments.of("on 04.12.2017 06:30:01 Uhr do pingMembers(\"hi! today is \" + 04.12.2017 06:30:01 Uhr);"),
        Arguments.of("on 04.12.2017 06:30 Uhr do pingMembers(\"hi! today is \" + 04.12.2017 06:30 Uhr);"),
        Arguments.of("on 04.12.2017 6 Uhr do pingMembers(\"hi! today is \" + 04.12.2017 6 Uhr);"),
        
        Arguments.of("on 4. Januar 2017 12:30:01 Uhr do pingMembers(\"hi! today is \" + d\"4. Januar 2017 12:30:01 Uhr\");"),
        Arguments.of("on 4. Januar 2017 12:30 Uhr do pingMembers(\"hi! today is \" + d\"4. Januar 2017 12:30 Uhr\");"),
        Arguments.of("on 4. Januar 2017 12 Uhr do pingMembers(\"hi! today is \" + d\"4. Januar 2017 12 Uhr\");"),
        Arguments.of("on 4. Januar 2017 06:30:01 Uhr do pingMembers(\"hi! today is \" + d\"4. Januar 2017 06:30:01 Uhr\");"),
        Arguments.of("on 4. Januar 2017 06:30 Uhr do pingMembers(\"hi! today is \" + d\"4. Januar 2017 06:30 Uhr\");"),
        Arguments.of("on 4. Januar 2017 6 Uhr do pingMembers(\"hi! today is \" + d\"4. Januar 2017 6 Uhr\");"),
        
        Arguments.of("on 4. Januar 2017 12:30:01 Uhr do pingMembers(\"hi! today is \" + 4. Januar 2017 12:30:01 Uhr);"),
        Arguments.of("on 4. Januar 2017 12:30 Uhr do pingMembers(\"hi! today is \" + 4. Januar 2017 12:30 Uhr);"),
        Arguments.of("on 4. Januar 2017 12 Uhr do pingMembers(\"hi! today is \" + 4. Januar 2017 12 Uhr);"),
        Arguments.of("on 4. Januar 2017 06:30:01 Uhr do pingMembers(\"hi! today is \" + 4. Januar 2017 06:30:01 Uhr);"),
        Arguments.of("on 4. Januar 2017 06:30 Uhr do pingMembers(\"hi! today is \" + 4. Januar 2017 06:30 Uhr);"),
        Arguments.of("on 4. Januar 2017 6 Uhr do pingMembers(\"hi! today is \" + 4. Januar 2017 6 Uhr);"),
        
        Arguments.of("on 4. Jan. 2017 12:30:01 Uhr do pingMembers(\"hi! today is \" + d\"4. Jan. 2017 12:30:01 Uhr\");"),
        Arguments.of("on 4. Jan. 2017 12:30 Uhr do pingMembers(\"hi! today is \" + d\"4. Jan. 2017 12:30 Uhr\");"),
        Arguments.of("on 4. Jan. 2017 12 Uhr do pingMembers(\"hi! today is \" + d\"4. Jan. 2017 12 Uhr\");"),
        Arguments.of("on 4. Jan. 2017 06:30:01 Uhr do pingMembers(\"hi! today is \" + d\"4. Jan. 2017 06:30:01 Uhr\");"),
        Arguments.of("on 4. Jan. 2017 06:30 Uhr do pingMembers(\"hi! today is \" + d\"4. Jan. 2017 06:30 Uhr\");"),
        Arguments.of("on 4. Jan. 2017 6 Uhr do pingMembers(\"hi! today is \" + d\"4. Jan. 2017 6 Uhr\");"),
        
        Arguments.of("on 4. Jan. 2017 12:30:01 Uhr do pingMembers(\"hi! today is \" + 4. Jan. 2017 12:30:01 Uhr);"),
        Arguments.of("on 4. Jan. 2017 12:30 Uhr do pingMembers(\"hi! today is \" + 4. Jan. 2017 12:30 Uhr);"),
        Arguments.of("on 4. Jan. 2017 12 Uhr do pingMembers(\"hi! today is \" + 4. Jan. 2017 12 Uhr);"),
        Arguments.of("on 4. Jan. 2017 06:30:01 Uhr do pingMembers(\"hi! today is \" + 4. Jan. 2017 06:30:01 Uhr);"),
        Arguments.of("on 4. Jan. 2017 06:30 Uhr do pingMembers(\"hi! today is \" + 4. Jan. 2017 06:30 Uhr);"),
        Arguments.of("on 4. Jan. 2017 6 Uhr do pingMembers(\"hi! today is \" + 4. Jan. 2017 6 Uhr);"),
        
        Arguments.of("on 4. März 2017 12:30:01 Uhr do pingMembers(\"hi! today is \" + d\"4. März 2017 12:30:01 Uhr\");"),
        Arguments.of("on 4. März 2017 12:30 Uhr do pingMembers(\"hi! today is \" + d\"4. März 2017 12:30 Uhr\");"),
        Arguments.of("on 4. März 2017 12 Uhr do pingMembers(\"hi! today is \" + d\"4. März 2017 12 Uhr\");"),
        Arguments.of("on 4. März 2017 06:30:01 Uhr do pingMembers(\"hi! today is \" + d\"4. März 2017 06:30:01 Uhr\");"),
        Arguments.of("on 4. März 2017 06:30 Uhr do pingMembers(\"hi! today is \" + d\"4. März 2017 06:30 Uhr\");"),
        Arguments.of("on 4. März 2017 6 Uhr do pingMembers(\"hi! today is \" + d\"4. März 2017 6 Uhr\");"),
        
        Arguments.of("on 4. März 2017 12:30:01 Uhr do pingMembers(\"hi! today is \" + 4. März 2017 12:30:01 Uhr);"),
        Arguments.of("on 4. März 2017 12:30 Uhr do pingMembers(\"hi! today is \" + 4. März 2017 12:30 Uhr);"),
        Arguments.of("on 4. März 2017 12 Uhr do pingMembers(\"hi! today is \" + 4. März 2017 12 Uhr);"),
        Arguments.of("on 4. März 2017 06:30:01 Uhr do pingMembers(\"hi! today is \" + 4. März 2017 06:30:01 Uhr);"),
        Arguments.of("on 4. März 2017 06:30 Uhr do pingMembers(\"hi! today is \" + 4. März 2017 06:30 Uhr);"),
        Arguments.of("on 4. März 2017 6 Uhr do pingMembers(\"hi! today is \" + 4. März 2017 6 Uhr);"),
        
        Arguments.of("on 4. Mär. 2017 12:30:01 Uhr do pingMembers(\"hi! today is \" + d\"4. Mär. 2017 12:30:01 Uhr\");"),
        Arguments.of("on 4. Mär. 2017 12:30 Uhr do pingMembers(\"hi! today is \" + d\"4. Mär. 2017 12:30 Uhr\");"),
        Arguments.of("on 4. Mär. 2017 12 Uhr do pingMembers(\"hi! today is \" + d\"4. Mär. 2017 12 Uhr\");"),
        Arguments.of("on 4. Mär. 2017 06:30:01 Uhr do pingMembers(\"hi! today is \" + d\"4. Mär. 2017 06:30:01 Uhr\");"),
        Arguments.of("on 4. Mär. 2017 06:30 Uhr do pingMembers(\"hi! today is \" + d\"4. Mär. 2017 06:30 Uhr\");"),
        Arguments.of("on 4. Mär. 2017 6 Uhr do pingMembers(\"hi! today is \" + d\"4. Mär. 2017 6 Uhr\");"),
        
        Arguments.of("on 4. Mär. 2017 12:30:01 Uhr do pingMembers(\"hi! today is \" + 4. Mär. 2017 12:30:01 Uhr);"),
        Arguments.of("on 4. Mär. 2017 12:30 Uhr do pingMembers(\"hi! today is \" + 4. Mär. 2017 12:30 Uhr);"),
        Arguments.of("on 4. Mär. 2017 12 Uhr do pingMembers(\"hi! today is \" + 4. Mär. 2017 12 Uhr);"),
        Arguments.of("on 4. Mär. 2017 06:30:01 Uhr do pingMembers(\"hi! today is \" + 4. Mär. 2017 06:30:01 Uhr);"),
        Arguments.of("on 4. Mär. 2017 06:30 Uhr do pingMembers(\"hi! today is \" + 4. Mär. 2017 06:30 Uhr);"),
        Arguments.of("on 4. Mär. 2017 6 Uhr do pingMembers(\"hi! today is \" + 4. Mär. 2017 6 Uhr);")
    );
  }
  
  @DisplayName("Timed Action")
  @ParameterizedTest(name = "Input: {0}")
  @MethodSource("timedActions")
  public void testTimedAction(String input) {
    ASTTimedAction ast = Assertions.assertDoesNotThrow(() -> parser.parse_StringTimedAction(input).orElseThrow());
    ASTLiteral
        temporalSubExpression = ((ASTLiteralExpression) ((ASTPlusExpression) ((ASTCallExpression) ((ASTExpressionStatement) ast.getAction()
        .getMCStatement())
        .getExpression())
        .getArguments()
        .getExpression(0))
        .getRight())
        .getLiteral();
    assertEquals(1,
        Stream.of(
            temporalSubExpression instanceof ASTEscapedTemporalLiteral,
            temporalSubExpression instanceof ASTInstant,
            temporalSubExpression instanceof ASTPeriod
        ).filter(b -> b).count()
    );
  }
  
  protected static Stream<Arguments> keywordExamples() {
    return Stream.of(
        Arguments.of("src/test/resources/de/monticore/temporal/timedactions/keywords.ta")
    );
  }
  
  @DisplayName("No New Keywords - German")
  @ParameterizedTest(name = "Input: {0}")
  @MethodSource("keywordExamples")
  public void testGermanKeywords(String filePath) {
    List<String> undesiredKeywords = new ArrayList<>(List.of("Uhr", "Januar", "Februar",
        "April", "Mai", "Juni", "Juli", "August", "September", "Oktober", "November", "Dezember"));
    Log.warn("[PATHDEBUG] Test location: " + Paths.get("").toAbsolutePath());
    Log.warn("[PATHDEBUG] Filepath: " + new File(filePath).getAbsolutePath());
    assertEquals(0, Log.getFindingsCount());
    ASTActionSequence ast = Assertions.assertDoesNotThrow(() -> parser.parse(filePath).orElseThrow());
    ASTMCJavaBlock statementBlock = (ASTMCJavaBlock) ast.getTimedAction(0).getAction().getMCStatement();
    for (ASTMCBlockStatement statement : statementBlock.getMCBlockStatementList()) {
      if (!(statement instanceof ASTLocalVariableDeclarationStatement)) {
        continue;
      }
      
      String variableName = ((ASTLocalVariableDeclarationStatement) statement)
          .getLocalVariableDeclaration().getVariableDeclarator(0).getDeclarator().getName();
      Assertions.assertTrue(undesiredKeywords.remove(variableName));
    }
    Assertions.assertTrue(undesiredKeywords.isEmpty());
  }
}
