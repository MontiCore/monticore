/* (c)  https://github.com/MontiCore/monticore */package de.se_rwth.langeditor.global;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IStorage;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.ui.IStartup;

import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import de.se_rwth.langeditor.injection.DIService;
import de.se_rwth.langeditor.modelstates.ModelStateAssembler;
import de.se_rwth.langeditor.util.Misc;

public class Startup implements IStartup {
  
  @Override
  public void earlyStartup() {
    LogStub.init();
    Log.enableFailQuick(false);
    
    ModelStateAssembler assembler = DIService.getInstance(ModelStateAssembler.class);
    assembler.scheduleFullRebuild();
    ResourcesPlugin.getWorkspace().addResourceChangeListener(event -> {
      if (event != null && event.getResource() instanceof IStorage) {
        IStorage storage = (IStorage) event.getResource();
        IProject project = Misc.getProject(storage);
        String content = Misc.getContents(storage);
        assembler.scheduleRebuild(storage, project, content);
      }
    });
  }
}
