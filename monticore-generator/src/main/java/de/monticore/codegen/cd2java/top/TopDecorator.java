package de.monticore.codegen.cd2java.top;

import de.monticore.codegen.cd2java.AbstractDecorator;
import de.monticore.io.paths.IterablePath;
import de.monticore.umlcd4a.cd4analysis._ast.*;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

import static de.monticore.codegen.cd2java.factories.CDModifier.PACKAGE_PRIVATE_ABSTRACT;

public class TopDecorator extends AbstractDecorator<ASTCDCompilationUnit, ASTCDCompilationUnit> {

  private static final String JAVA_EXTENSION = ".java";

  private static final String TOP_SUFFIX = "TOP";

  private final IterablePath targetPath;

  public TopDecorator(IterablePath targetPath) {
    this.targetPath = targetPath;
  }

  @Override
  public ASTCDCompilationUnit decorate(ASTCDCompilationUnit ast) {
    ASTCDDefinition cdDefinition = ast.getCDDefinition();
    String packageName = String.join(File.separator, ast.getPackageList());

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
    Path handwrittenFile = Paths.get(packageName, simpleName + JAVA_EXTENSION);
    return targetPath.exists(handwrittenFile);
  }

  private void applyTopMechanism(ASTCDClass cdClass) {
    makeAbstract(cdClass);
    cdClass.setName(cdClass.getName() + TOP_SUFFIX);

    cdClass.getCDConstructorList().forEach(constructor ->
        constructor.setName(constructor.getName() + TOP_SUFFIX));
  }

  private void applyTopMechanism(ASTCDInterface cdInterface) {
    cdInterface.setName(cdInterface.getName() + TOP_SUFFIX);
  }

  private void applyTopMechanism(ASTCDEnum cdEnum) {
    cdEnum.setName(cdEnum.getName() + TOP_SUFFIX);
  }

  private void makeAbstract(ASTCDType type) {
    if (type.getModifierOpt().isPresent()) {
      makeAbstract(type.getModifierOpt().get());
    } else {
      type.setModifier(PACKAGE_PRIVATE_ABSTRACT.build());
    }
  }

  private void makeAbstract(ASTModifier modifier) {
    modifier.setAbstract(true);
  }
}
