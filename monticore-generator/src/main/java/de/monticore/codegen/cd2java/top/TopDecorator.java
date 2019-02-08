package de.monticore.codegen.cd2java.top;

import de.monticore.codegen.cd2java.Decorator;
import de.monticore.generating.templateengine.reporting.Reporting;
import de.monticore.io.paths.IterablePath;
import de.monticore.umlcd4a.cd4analysis._ast.*;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import static de.monticore.codegen.cd2java.factories.CDModifier.*;

public class TopDecorator implements Decorator<ASTCDCompilationUnit, ASTCDCompilationUnit> {

  private static final String JAVA_EXTENSION = ".java";

  private static final String TOP_EXTENSION = "TOP";

  private final IterablePath targetPath;

  public TopDecorator(IterablePath targetPath) {
    this.targetPath = targetPath;
  }

  @Override
  public ASTCDCompilationUnit decorate(ASTCDCompilationUnit ast) {
    ASTCDDefinition cdDefinition = ast.getCDDefinition();
    String packageName = String.join(".", ast.getPackageList());

    cdDefinition.getCDClassList().stream()
        .filter(cdClass -> existsHandwrittenClass(packageName, cdClass.getName()))
        .forEach(this::applyTopMechanism);

    cdDefinition.getCDInterfaceList().stream()
        .filter(cdInterface -> existsHandwrittenClass(packageName, cdInterface.getName()))
        .forEach(this::applyTopMechanism);

    cdDefinition.getCDEnumList().stream()
        .filter(cdEnum -> existsHandwrittenClass(packageName, cdEnum.getName()))
        .forEach(this::applyTopMechanism);

    return ast;
  }

  private boolean existsHandwrittenClass(String packageName, String simpleName) {
    Path handwrittenFile = Paths.get(packageName.replaceAll("\\.", "/"), simpleName + JAVA_EXTENSION);
    return targetPath.exists(handwrittenFile);
  }

  private void applyTopMechanism(ASTCDClass cdClass) {
    makeAbstract(cdClass);
    cdClass.setName(cdClass.getName() + TOP_EXTENSION);

    cdClass.getCDConstructorList().forEach(constructor -> {
      constructor.setName(constructor.getName() + TOP_EXTENSION);
      makeAbstract(constructor.getModifier());
    });
  }

  private void applyTopMechanism(ASTCDInterface cdInterface) {
    makeAbstract(cdInterface);
    cdInterface.setName(cdInterface.getName() + TOP_EXTENSION);
  }

  private void applyTopMechanism(ASTCDEnum cdEnum) {
    makeAbstract(cdEnum);
    cdEnum.setName(cdEnum.getName() + TOP_EXTENSION);
  }

  private void makeAbstract(ASTCDType type) {
    if (type.getModifierOpt().isPresent()) {
      makeAbstract(type.getModifierOpt().get());
    }
    else {
      type.setModifier(PACKAGE_PRIVATE_ABSTRACT);
    }
  }

  private void makeAbstract(ASTModifier modifier) {
    modifier.setAbstract(true);
  }
}
