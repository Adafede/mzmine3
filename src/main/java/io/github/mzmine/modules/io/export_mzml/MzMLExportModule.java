/*
 * Copyright 2006-2020 The MZmine Development Team
 *
 * This file is part of MZmine.
 *
 * MZmine is free software; you can redistribute it and/or modify it under the terms of the GNU
 * General Public License as published by the Free Software Foundation; either version 2 of the
 * License, or (at your option) any later version.
 *
 * MZmine is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even
 * the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with MZmine; if not,
 * write to the Free Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301
 * USA
 */

package io.github.mzmine.modules.io.export_mzml;

import io.github.mzmine.datamodel.MZmineProject;
import io.github.mzmine.datamodel.RawDataFile;
import io.github.mzmine.modules.MZmineModuleCategory;
import io.github.mzmine.modules.MZmineProcessingModule;
import io.github.mzmine.parameters.ParameterSet;
import io.github.mzmine.taskcontrol.Task;
import io.github.mzmine.util.ExitCode;
import io.github.mzmine.util.files.FileAndPathUtil;
import java.io.File;
import java.util.Collection;
import org.jetbrains.annotations.NotNull;

/**
 * Raw data export module
 */
public class MzMLExportModule implements MZmineProcessingModule {

  private static final String MODULE_NAME = "Raw data export";
  private static final String MODULE_DESCRIPTION =
      "This module exports raw data files from your MZmine project into various formats";

  @Override
  public @NotNull String getName() {
    return MODULE_NAME;
  }

  @Override
  public @NotNull String getDescription() {
    return MODULE_DESCRIPTION;
  }

  @Override
  @NotNull
  public ExitCode runModule(final @NotNull MZmineProject project, @NotNull ParameterSet parameters,
      @NotNull Collection<Task> tasks) {

    String extension = "mzML";

    File folder = parameters.getParameter(MzMLExportParameters.fileName).getValue();
    if (!folder.isDirectory())
      folder = folder.getParentFile();

    RawDataFile[] dataFile = parameters.getParameter(MzMLExportParameters.dataFiles).getValue()
        .getMatchingRawDataFiles();

    for (RawDataFile r : dataFile) {
      File fullName = FileAndPathUtil.getRealFilePath(folder, r.getName(), extension);
      Task newTask = new MzMLExportTask(r, fullName);
      tasks.add(newTask);
    }
    return ExitCode.OK;
  }

  @Override
  public @NotNull MZmineModuleCategory getModuleCategory() {
    return MZmineModuleCategory.RAWDATAEXPORT;
  }

  @Override
  public @NotNull Class<? extends ParameterSet> getParameterSetClass() {
    return MzMLExportParameters.class;
  }

}
