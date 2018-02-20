/* (c)  https://github.com/MontiCore/monticore */package de.se_rwth.langeditor.global;

import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.compiler.CompilationParticipant;

import de.se_rwth.langeditor.injection.DIService;
import de.se_rwth.langeditor.modelstates.ModelStateAssembler;

public class LanguageProjectBuilder extends CompilationParticipant {
  
  @Override
  public int aboutToBuild(IJavaProject javaProject) {
    DIService.getInstance(ModelStateAssembler.class).scheduleFullRebuild();
    return READY_FOR_BUILD;
  }
  
  @Override
  public boolean isActive(IJavaProject project) {
    return true;
  }
}
