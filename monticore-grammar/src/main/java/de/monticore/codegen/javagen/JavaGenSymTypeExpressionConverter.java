/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.javagen;

import de.monticore.codegen.CodeGenSymTypeExpressionConverter;
import de.monticore.codegen.javagen.typeconverter.JavaBooleanConversionHandler;
import de.monticore.codegen.javagen.typeconverter.JavaFunctionConversionHandler;
import de.monticore.codegen.javagen.typeconverter.JavaNumericConversionHandler;
import de.monticore.codegen.javagen.typeconverter.JavaNumericSuperTypeConversionHandler;
import de.monticore.codegen.javagen.typeconverter.JavaObjectConversionHandler;
import de.monticore.codegen.javagen.typeconverter.JavaStringConversionHandler;
import de.monticore.codegen.javagen.typeconverter.JavaTupleConversionHandler;
import de.monticore.codegen.typeconverter.TrivialConversionHandler;
import de.se_rwth.commons.logging.Log;

import java.util.List;

public class JavaGenSymTypeExpressionConverter
    extends CodeGenSymTypeExpressionConverter {

  protected JavaGenSymTypeExpressionConverter() {
    setConversionHandlersByPriority(
        List.of(
            // trivial case
            List.of(new TrivialConversionHandler()),
            // most specific cases
            List.of(
                new JavaBooleanConversionHandler(),
                new JavaFunctionConversionHandler(),
                new JavaNumericConversionHandler(),
                new JavaStringConversionHandler(),
                new JavaTupleConversionHandler()
            ),
            // rather generic handlers that disregard some specifics
            List.of(
                new JavaNumericSuperTypeConversionHandler(),
                new JavaObjectConversionHandler()
            )
        )
    );
  }

  // static delegate
  public static void init() {
    Log.trace("init JavaGenSymTypeExpressionConverter", "CodeGen setup");
    JavaGenSymTypeExpressionConverter converter =
        new JavaGenSymTypeExpressionConverter();
    CodeGenSymTypeExpressionConverter.setDelegate(converter);
  }

}
