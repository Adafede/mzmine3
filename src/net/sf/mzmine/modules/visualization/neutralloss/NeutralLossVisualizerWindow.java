/*
 * Copyright 2006 The MZmine Development Team
 * 
 * This file is part of MZmine.
 * 
 * MZmine is free software; you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 * 
 * MZmine is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * MZmine; if not, write to the Free Software Foundation, Inc., 51 Franklin St,
 * Fifth Floor, Boston, MA 02110-1301 USA
 */

package net.sf.mzmine.modules.visualization.neutralloss;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JDialog;
import javax.swing.JInternalFrame;

import net.sf.mzmine.io.RawDataFile;
import net.sf.mzmine.main.MZmineCore;
import net.sf.mzmine.modules.RawDataVisualizer;
import net.sf.mzmine.modules.visualization.spectra.SpectraVisualizerWindow;
import net.sf.mzmine.taskcontrol.Task;
import net.sf.mzmine.taskcontrol.TaskListener;
import net.sf.mzmine.taskcontrol.Task.TaskStatus;
import net.sf.mzmine.userinterface.Desktop;

/**
 * Neutral loss visualizer using JFreeChart library
 */
public class NeutralLossVisualizerWindow extends JInternalFrame implements
        RawDataVisualizer, ActionListener, TaskListener {

    private NeutralLossToolBar toolBar;
    private NeutralLossPlot neutralLossPlot;

    private NeutralLossDataSet dataset;

    private RawDataFile dataFile;

    private Desktop desktop;

    public NeutralLossVisualizerWindow(RawDataFile dataFile, int xAxis,
            float rtMin, float rtMax, float mzMin, float mzMax,
            int numOfFragments) {

        super(dataFile.toString(), true, true, true, true);

        this.desktop = MZmineCore.getDesktop();

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setBackground(Color.white);

        this.dataFile = dataFile;

        dataset = new NeutralLossDataSet(dataFile, xAxis, rtMin, rtMax, mzMin,
                mzMax, numOfFragments, this);

        toolBar = new NeutralLossToolBar(this);
        add(toolBar, BorderLayout.EAST);

        neutralLossPlot = new NeutralLossPlot(this, dataset, xAxis);
        add(neutralLossPlot, BorderLayout.CENTER);

        if (xAxis == 1)
            setDomainRange(rtMin, rtMax);
        else
            setDomainRange(mzMin, mzMax);

        updateTitle();

        pack();

    }

    /**
     */
    public void setRangeRange(float min, float max) {
        neutralLossPlot.getXYPlot().getRangeAxis().setRange(min, max);
    }

    /**
     */
    public void setDomainRange(float min, float max) {
        neutralLossPlot.getXYPlot().getDomainAxis().setRange(min, max);
    }

    /**
     * @see net.sf.mzmine.modules.RawDataVisualizer#setIntensityRange(float,
     *      float)
     */
    public void setIntensityRange(float intensityMin, float intensityMax) {
        // do nothing
    }

    void updateTitle() {

        StringBuffer title = new StringBuffer();
        title.append("[");
        title.append(dataFile.toString());
        title.append("]: neutral loss");

        setTitle(title.toString());

        NeutralLossDataPoint pos = getCursorPosition();

        if (pos != null) {
            title.append(", ");
            title.append(pos.toString());
        }

        neutralLossPlot.setTitle(title.toString());

    }

    /**
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent event) {

        String command = event.getActionCommand();

        if (command.equals("HIGHLIGHT")) {
            JDialog dialog = new NeutralLossSetHighlightDialog(desktop,
                    neutralLossPlot);
            dialog.setVisible(true);
        }

        if (command.equals("SHOW_SPECTRUM")) {
            NeutralLossDataPoint pos = getCursorPosition();
            if (pos != null) {
                new SpectraVisualizerWindow(dataFile, pos.getScanNumber());
            }
        }

    }

    /**
     * @see net.sf.mzmine.taskcontrol.TaskListener#taskFinished(net.sf.mzmine.taskcontrol.Task)
     */
    public void taskFinished(Task task) {
        if (task.getStatus() == TaskStatus.ERROR) {
            desktop.displayErrorMessage("Error while updating neutral loss visualizer: "
                    + task.getErrorMessage());
        }

    }

    /**
     * @see net.sf.mzmine.taskcontrol.TaskListener#taskStarted(net.sf.mzmine.taskcontrol.Task)
     */
    public void taskStarted(Task task) {
        // if we have not added this frame before, do it now
        if (getParent() == null)
            desktop.addInternalFrame(this);
    }

    /**
     * 
     */
    public NeutralLossDataPoint getCursorPosition() {
        float xValue = (float) neutralLossPlot.getXYPlot().getDomainCrosshairValue();
        float yValue = (float) neutralLossPlot.getXYPlot().getRangeCrosshairValue();

        NeutralLossDataPoint point = dataset.getDataPoint(xValue, yValue);

        return point;

    }

    NeutralLossPlot getPlot() {
        return neutralLossPlot;
    }

}
