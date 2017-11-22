package uk.ac.ebi.cytocopter.internal.mahdiplotting;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JPanel;

import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.xy.DefaultXYDataset;

import uk.ac.ebi.cytocopter.internal.mahdimidas.CNO;
import uk.ac.ebi.cytocopter.internal.mahdimidasexceptions.MidasGeneralException;
import uk.ac.ebi.cytocopter.internal.mahdinetworkmodeling.CNONetwork;

public class ContainerPanelSimulation extends JPanel
{
	List<String> columnNames;
	List<ArrayList<Integer>> trCombinations;
	CNO midas;
	int beginTimePoint;
	int endTimePoint;

	int mainRowsNum;
	int rowsNumber;

	int mainColumnsNum;
	int columnsNumber;

	DefaultXYDataset[][] allDataSets;
	List<CategoryDataset> barStimDatasets = null;
	List<CategoryDataset> barInhibitDatasets = null;

	CNONetwork network;

	List<HashMap<String, Integer>> simulationResultsBeginTime = null;
	List<HashMap<String, Integer>> simulationResultsEndTime = null;

	public ContainerPanelSimulation(CNO midas, int beginTimePoint, int endTimePoint,
			List<HashMap<String, Integer>> simulationResultsBeginTime,
			List<HashMap<String, Integer>> simulationResultsEndTime) throws Exception
	{
		setLayout(new GridBagLayout());

		this.network = network;
		this.midas = midas;
		this.beginTimePoint = beginTimePoint;
		this.endTimePoint = endTimePoint;
		this.simulationResultsBeginTime = simulationResultsBeginTime;
		this.simulationResultsEndTime = simulationResultsEndTime;

		barStimDatasets = new ArrayList<CategoryDataset>();
		barInhibitDatasets = new ArrayList<CategoryDataset>();

		initializeData();
		createHeadBar();
		createMainPart();
		createRightPart();
		createFootBar();

	}

	private void initializeData() throws MidasGeneralException
	{

		columnNames = midas.getAllDVColumnsNames();

		// get all treatment combinations
		List<ArrayList<Integer>> trCombinations = midas.valueCuesAsInteger();

		// get all DV column names
		List<String> dvColumnNames = midas.getAllDVColumnsNames();

		// row numbers = number of tr combinations + one row for head bar
		// There is no row for foot bar
		mainRowsNum = trCombinations.size();
		rowsNumber = mainRowsNum + 1;

		// number of dv columns + three additional columns for: Stimulus,
		// Inhibitors, and Error
		mainColumnsNum = dvColumnNames.size();
		columnsNumber = mainColumnsNum + 3;

		allDataSets = new DefaultXYDataset[mainRowsNum][mainColumnsNum];

		// This Part Grabs all the required initial data for the main part of
		// the Panel
		for (int i = 0; i < mainRowsNum; i++)
		{
			ArrayList<Integer> oneCombination = trCombinations.get(i);

			for (int j = 0; j < mainColumnsNum; j++)
			{
				String oneDVColumnName = dvColumnNames.get(j);

				double initialDataBeginTimePoint = midas.getDVValueForTHisDVColumnAtThisTimepoint(oneDVColumnName,
						oneCombination, beginTimePoint);
				double initialDataEndTimePoint = midas.getDVValueForTHisDVColumnAtThisTimepoint(oneDVColumnName,
						oneCombination, endTimePoint);

				DefaultXYDataset oneDataSet = new DefaultXYDataset();
				oneDataSet.addSeries("initialData", new double[][] { { beginTimePoint, endTimePoint },
						{ initialDataBeginTimePoint, initialDataEndTimePoint } });
				allDataSets[i][j] = oneDataSet;
			}
		}

		// *****This is Data Initialization for the right bar charts of the
		// panel****************************************

		List<String> trNames = midas.getAllTRColumnsNames();

		for (int i = 0; i < trCombinations.size(); i++)
		{

			List<Integer> oneCombination = trCombinations.get(i);

			DefaultCategoryDataset oneStimBarDataset = new DefaultCategoryDataset();
			DefaultCategoryDataset oneInhibitBarDataset = new DefaultCategoryDataset();

			for (int j = 0; j < oneCombination.size(); j++)
			{
				if (trNames.get(j).endsWith("i"))
				{
					oneInhibitBarDataset.addValue(oneCombination.get(j), "Inhibitor",
							trNames.get(j).substring(3, trNames.get(j).length() - 1));
				}
				else
				{
					oneStimBarDataset.addValue(oneCombination.get(j), "Stimulus",
							trNames.get(j).substring(3, trNames.get(j).length()));
				}
			}

			barStimDatasets.add(oneStimBarDataset);
			barInhibitDatasets.add(oneInhibitBarDataset);
		}

		// *****This part gets the result of network simulation, and gets them
		// ready to be shown in the main
		// part****************************************

		List<String> nodeNames = new ArrayList<String>();
		for (String columnName : dvColumnNames)
		{
			nodeNames.add(columnName.substring(3));
		}

		for (int i = 0; i < mainRowsNum; i++)
		{
			HashMap<String, Integer> oneRowSimResultBeginT = simulationResultsBeginTime.get(i);
			HashMap<String, Integer> oneRowSimResultEndT = simulationResultsEndTime.get(i);

			for (int j = 0; j < mainColumnsNum; j++)
			{

				DefaultXYDataset oneModifyingDataSet = (DefaultXYDataset) allDataSets[i][j];
				String nodeName = nodeNames.get(j);

				oneModifyingDataSet.addSeries("simulatioResults", new double[][] { { beginTimePoint, endTimePoint },
						{ (double) oneRowSimResultBeginT.get(nodeName), (double) oneRowSimResultEndT.get(nodeName) } });

			}
		}

	}

	private void createHeadBar()
	{
		List<String> dvNames = midas.getAllDAColumnsNames();

		GridBagConstraints gc = new GridBagConstraints();
		gc.weightx = 1;
		gc.weighty = 0.1;
		gc.fill = GridBagConstraints.BOTH;

		for (int i = 0; i < dvNames.size(); i++)
		{
			gc.gridx = i;
			gc.gridy = 0;

			JLabel headLabel = new JLabel(dvNames.get(i).substring(3));
			headLabel.setHorizontalAlignment(JLabel.CENTER);
			headLabel.setVerticalAlignment(JLabel.BOTTOM);
			headLabel.setMinimumSize(new Dimension(160, 80));
			headLabel.setFont(new Font("Dialog", Font.BOLD, 10));

			add(headLabel, gc);
		}

		gc.gridx++;
		JLabel stimLabel = new JLabel("Stimuli");
		stimLabel.setHorizontalAlignment(JLabel.CENTER);
		stimLabel.setVerticalAlignment(JLabel.BOTTOM);
		stimLabel.setMinimumSize(new Dimension(160, 80));
		stimLabel.setFont(new Font("Dialog", Font.BOLD, 10));
		add(stimLabel, gc);

		gc.gridx++;
		JLabel inhibitorsLabel = new JLabel("Inhibitors");
		inhibitorsLabel.setHorizontalAlignment(JLabel.CENTER);
		inhibitorsLabel.setVerticalAlignment(JLabel.BOTTOM);
		inhibitorsLabel.setMinimumSize(new Dimension(160, 80));
		inhibitorsLabel.setFont(new Font("Dialog", Font.BOLD, 10));
		add(inhibitorsLabel, gc);

	}

	private void createMainPart()
	{
		// Initial Grid Configuration
		GridBagConstraints gc = new GridBagConstraints();
		gc.weightx = 1;
		gc.weighty = 1;
		gc.fill = GridBagConstraints.BOTH;

		int k = 0;
		for (int i = 0; i < mainRowsNum; i++)
		{
			for (int j = 0; j < mainColumnsNum; j++)
			{
				gc.gridx = j;
				// additional 1 is for the head bar
				gc.gridy = i + 1;
				boolean xMarkers = false;
				boolean yMarkers = false;

				if (j == 0)
				{
					yMarkers = true;
				}
				if (i == mainRowsNum - 1)
				{
					xMarkers = true;
				}

				add(new OneChartPanel(allDataSets[i][j], xMarkers, yMarkers), gc);
				k++;
			}
		}
	}

	private void createRightPart()
	{
		GridBagConstraints gc = new GridBagConstraints();
		gc.weightx = 1;
		gc.weighty = 1;
		gc.fill = GridBagConstraints.BOTH;

		for (int i = 0; i < mainRowsNum; i++)
		{
			gc.gridx = mainColumnsNum;
			gc.gridy = i + 1;

			boolean domainMarkers = false;
			if (i == mainRowsNum - 1)
			{
				domainMarkers = true;

			}

			add(new StimBarChartPanel(barStimDatasets.get(i), domainMarkers), gc);

			gc.gridx++;
			add(new InhibitBarChartPanel(barInhibitDatasets.get(i), domainMarkers), gc);
		}

	}

	private void createFootBar()
	{

		GridBagConstraints gc = new GridBagConstraints();
		gc.weightx = 1;
		gc.weighty = 0.6;
		gc.fill = GridBagConstraints.BOTH;

		gc.gridy = rowsNumber + 1;

		for (int i = 0; i < mainColumnsNum + 2; i++)
		{
			JLabel topBar = new JLabel("");
			topBar.setMinimumSize(new Dimension(160, 80));
			topBar.setSize(new Dimension(160, 80));
			topBar.setPreferredSize(new Dimension(160, 80));
			topBar.setMaximumSize(new Dimension(160, 80));
			topBar.setVerticalAlignment(JLabel.BOTTOM);
			topBar.setHorizontalAlignment(JLabel.CENTER);
			topBar.setFont(new Font("Dialog", Font.BOLD, 10));
			gc.gridx = i;
			add(topBar, gc);
		}

	}

}
