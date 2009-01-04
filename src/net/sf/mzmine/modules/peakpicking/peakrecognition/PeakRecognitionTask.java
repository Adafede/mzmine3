/*
 * Copyright 2006-2009 The MZmine 2 Development Team
 * 
 * This file is part of MZmine 2.
 * 
 * MZmine 2 is free software; you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 * 
 * MZmine 2 is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * MZmine 2; if not, write to the Free Software Foundation, Inc., 51 Franklin St,
 * Fifth Floor, Boston, MA 02110-1301 USA
 */

package net.sf.mzmine.modules.peakpicking.peakrecognition;

import java.lang.reflect.Constructor;
import java.util.logging.Logger;

import net.sf.mzmine.data.ChromatographicPeak;
import net.sf.mzmine.data.MzPeak;
import net.sf.mzmine.data.ParameterSet;
import net.sf.mzmine.data.PeakList;
import net.sf.mzmine.data.RawDataFile;
import net.sf.mzmine.data.impl.SimplePeakList;
import net.sf.mzmine.data.impl.SimplePeakListRow;
import net.sf.mzmine.main.MZmineCore;
import net.sf.mzmine.project.MZmineProject;
import net.sf.mzmine.taskcontrol.Task;

/**
 * @see
 */
class PeakRecognitionTask implements Task {

	private Logger logger = Logger.getLogger(this.getClass().getName());

	private PeakList peakList;

	private TaskStatus status = TaskStatus.WAITING;
	private String errorMessage;

	// scan counter
	private int processedRows = 0, totalRows;
	private int newPeakID = 1;

	// User parameters
	private String suffix;

	private int peakBuilderTypeNumber;

	// Peak Builders
	private PeakResolver peakBuilder;

	private ParameterSet pbParameters;

	/**
	 * @param dataFile
	 * @param parameters
	 */
	PeakRecognitionTask(PeakList peakList, PeakRecognitionParameters parameters) {

		this.peakList = peakList;

		peakBuilderTypeNumber = parameters.getPeakBuilderTypeNumber();
		pbParameters = parameters
				.getPeakBuilderParameters(peakBuilderTypeNumber);
		suffix = parameters.getSuffix();

	}

	/**
	 * @see net.sf.mzmine.taskcontrol.Task#getTaskDescription()
	 */
	public String getTaskDescription() {
		return "Peak recognition on " + peakList;
	}

	/**
	 * @see net.sf.mzmine.taskcontrol.Task#getFinishedPercentage()
	 */
	public double getFinishedPercentage() {
		if (totalRows == 0)
			return 0;
		else
			return (double) processedRows / totalRows;
	}

	/**
	 * @see net.sf.mzmine.taskcontrol.Task#getStatus()
	 */
	public TaskStatus getStatus() {
		return status;
	}

	/**
	 * @see net.sf.mzmine.taskcontrol.Task#getErrorMessage()
	 */
	public String getErrorMessage() {
		return errorMessage;
	}

	/**
	 * @see net.sf.mzmine.taskcontrol.Task#cancel()
	 */
	public void cancel() {
		status = TaskStatus.CANCELED;
	}

	/**
	 * @see Runnable#run()
	 */
	public void run() {

		status = TaskStatus.PROCESSING;

		logger.info("Started peak recognition on " + peakList);

		// Create new peak constructor according with the user's selection
		String peakBuilderClassName = PeakRecognitionParameters.peakBuilderClasses[peakBuilderTypeNumber];
		try {
			Class peakBuilderClass = Class.forName(peakBuilderClassName);
			Constructor peakBuilderConstruct = peakBuilderClass
					.getConstructors()[0];
			peakBuilder = (PeakResolver) peakBuilderConstruct
					.newInstance(pbParameters);
		} catch (Exception e) {
			errorMessage = "Error trying to make an instance of peak builder "
					+ peakBuilderClassName;
			status = TaskStatus.ERROR;
			return;
		}

		// Get data file information
		RawDataFile dataFile = peakList.getRawDataFile(0);
		int scanNumbers[] = dataFile.getScanNumbers(1);
		double retentionTimes[] = new double[scanNumbers.length];
		for (int i = 0; i < scanNumbers.length; i++)
			retentionTimes[i] = dataFile.getScan(scanNumbers[i])
					.getRetentionTime();
		double intensities[] = new double[scanNumbers.length];

		// Create new peak list
		SimplePeakList newPeakList = new SimplePeakList(
				peakList + " " + suffix, dataFile);

		totalRows = peakList.getNumberOfRows();

		for (ChromatographicPeak chromatogram : peakList.getPeaks(dataFile)) {

			if (status == TaskStatus.CANCELED)
				return;

			// Load the intensities into array
			for (int i = 0; i < scanNumbers.length; i++) {
				MzPeak mzPeak = chromatogram.getMzPeak(scanNumbers[i]);
				if (mzPeak != null)
					intensities[i] = mzPeak.getIntensity();
				else
					intensities[i] = 0;
			}

			// Resolve peaks
			ChromatographicPeak peaks[] = peakBuilder.resolvePeaks(
					chromatogram, scanNumbers, retentionTimes, intensities);

			// Add peaks to the new peak list
			for (ChromatographicPeak finishedPeak : peaks) {
				SimplePeakListRow newRow = new SimplePeakListRow(newPeakID);
				newPeakID++;
				newRow.addPeak(dataFile, finishedPeak);
				newPeakList.addRow(newRow);
			}

			processedRows++;
		}

		// Add new peaklist to the project
		MZmineProject currentProject = MZmineCore.getCurrentProject();
		currentProject.addPeakList(newPeakList);

		status = TaskStatus.FINISHED;

		logger.info("Finished peak recognition on " + peakList);

	}

}
