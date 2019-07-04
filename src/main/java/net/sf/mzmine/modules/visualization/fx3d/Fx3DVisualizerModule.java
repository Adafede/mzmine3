/*
 * Copyright 2006-2018 The MZmine 2 Development Team
 * 
 * This file is part of MZmine 2.
 * 
 * MZmine 2 is free software; you can redistribute it and/or modify it under the terms of the GNU
 * General Public License as published by the Free Software Foundation; either version 2 of the
 * License, or (at your option) any later version.
 * 
 * MZmine 2 is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with MZmine 2; if not,
 * write to the Free Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301
 * USA
 */

package net.sf.mzmine.modules.visualization.fx3d;

import java.io.IOException;
import java.util.Collection;
import java.util.logging.Logger;

import javax.annotation.Nonnull;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import com.google.common.collect.Range;

import javafx.application.ConditionalFeature;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import net.sf.mzmine.datamodel.MZmineProject;
import net.sf.mzmine.datamodel.RawDataFile;
import net.sf.mzmine.main.MZmineCore;
import net.sf.mzmine.modules.MZmineModuleCategory;
import net.sf.mzmine.modules.MZmineRunnableModule;
import net.sf.mzmine.parameters.ParameterSet;
import net.sf.mzmine.parameters.parametertypes.selectors.ScanSelection;
import net.sf.mzmine.taskcontrol.Task;
import net.sf.mzmine.taskcontrol.TaskPriority;
import net.sf.mzmine.util.ExitCode;
import net.sf.mzmine.util.scans.ScanUtils;

public class Fx3DVisualizerModule implements MZmineRunnableModule {

    private static final Logger LOG = Logger
            .getLogger(Fx3DVisualizerModule.class.getName());

    private static final String MODULE_NAME = "Fx 3D visualizer";
    private static final String MODULE_DESCRIPTION = "Fx 3D visualizer."; // TODO

    @Override
    public @Nonnull String getName() {
        return MODULE_NAME;
    }

    @Override
    public @Nonnull String getDescription() {
        return MODULE_DESCRIPTION;
    }

    @SuppressWarnings("null")
    @Override
    @Nonnull
    public ExitCode runModule(@Nonnull MZmineProject project,
            @Nonnull ParameterSet parameters, @Nonnull Collection<Task> tasks) {

        final RawDataFile[] dataFiles = parameters
                .getParameter(Fx3DVisualizerParameters.dataFiles).getValue()
                .getMatchingRawDataFiles();
        final ScanSelection scanSel = parameters
                .getParameter(Fx3DVisualizerParameters.scanSelection)
                .getValue();
        int len = dataFiles.length;
        LOG.finest("Number of datafiles to be plotted:" + len);
        Range<Double> rtRange = ScanUtils
                .findRtRange(scanSel.getMatchingScans(dataFiles[0]));

        ParameterSet myParameters = MZmineCore.getConfiguration()
                .getModuleParameters(Fx3DVisualizerModule.class);
        Range<Double> mzRange = myParameters
                .getParameter(Fx3DVisualizerParameters.mzRange).getValue();
        int rtRes = myParameters
                .getParameter(Fx3DVisualizerParameters.rtResolution).getValue();
        int mzRes = myParameters
                .getParameter(Fx3DVisualizerParameters.mzResolution).getValue();

        Platform.setImplicitExit(false);
        Platform.runLater(() -> {
            if (!Platform.isSupported(ConditionalFeature.SCENE3D)) {
                JFrame frame = new JFrame();
                JOptionPane.showMessageDialog(frame,
                        "The platform does not provide 3D support.");
                return;
            }
            FXMLLoader loader = new FXMLLoader(
                    (getClass().getResource("Fx3DStage.fxml")));
            Parent nodeFromFXML = null;
            try {
                nodeFromFXML = loader.load();
                LOG.finest(
                        "Node has been successfully loaded from the FXML loader.");
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
            String title = "";
            Fx3DStageController controller = loader.getController();
            controller.setTotalFiles(len);
            controller.setRtMzValues(rtRange, mzRange);
            for (int i = 0; i < dataFiles.length; i++) {
                MZmineCore.getTaskController().addTask(
                        new Fx3DSamplingTask(dataFiles[i], scanSel, mzRange,
                                rtRes, mzRes, controller, i),
                        TaskPriority.HIGH);
                title = title + dataFiles[i].toString() + " ";
            }
            controller
                    .setLabel("3D plot of files [" + title + "], "
                            + mzRange.lowerEndpoint().toString() + "-"
                            + mzRange.upperEndpoint().toString() + " m/z, RT "
                            + (float) (Math.round(
                                    rtRange.lowerEndpoint() * 100.0) / 100.0)
                            + "-"
                            + (float) (Math.round(
                                    rtRange.upperEndpoint() * 100.0) / 100.0)
                            + " min");
            Stage stage = new Stage();
            Scene scene = new Scene(nodeFromFXML, 800, 600);

            stage.setScene(scene);
            stage.sizeToScene();
            stage.setTitle(title);
            stage.show();
            stage.setMinWidth(stage.getWidth());
            stage.setMinHeight(stage.getHeight());
        });

        return ExitCode.OK;

    }

    @Override
    public @Nonnull MZmineModuleCategory getModuleCategory() {
        return MZmineModuleCategory.VISUALIZATIONRAWDATA;
    }

    @Override
    public @Nonnull Class<? extends ParameterSet> getParameterSetClass() {
        return Fx3DVisualizerParameters.class;
    }

}
