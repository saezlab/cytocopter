package uk.ac.ebi.cytocopter.internal.mahdiplotting;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JLabel;
import javax.swing.JPanel;

import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.xy.DefaultXYDataset;
import org.jfree.data.xy.XYDataset;

import uk.ac.ebi.cytocopter.internal.mahdimidas.CNO;
import uk.ac.ebi.cytocopter.internal.mahdimidasexceptions.MidasGeneralException;

public class ContainerPanel extends JPanel
{
	CNO midas = null;

	List<XYDataset> allDataSets = null;
	List<CategoryDataset> barStimDatasets = null;
	List<CategoryDataset> barInhibitDatasets = null;

	int rowsNumber;
	int columnsNumber;

	// trCombinationsNum = rowsNumber-2;
	int trCombinationsNum;
	// dvColumnsNum = columnsNumber - 2;
	int dvColumnsNum;

	public ContainerPanel(CNO midas)
	{
		setLayout(new GridBagLayout());

		setMidas(midas);
		initializeData();

		this.setBackground(Color.WHITE);
		createHeadBar();
		createMainPart();
		createRightPart();
		createFootBar();

		setVisible(true);

	}

	public void initializeData()
	{

		// *****This is Data Initialization for the main Part of the
		// panel****************************************

		allDataSets = new ArrayList<XYDataset>();
		barStimDatasets = new ArrayList<CategoryDataset>();
		barInhibitDatasets = new ArrayList<CategoryDataset>();

		// get all treatment combinations
		List<ArrayList<Integer>> trCombinations = midas.valueCuesAsInteger();

		// row numbers = number of tr combinations + one row for head bar + one
		// row for foot bar
		trCombinationsNum = trCombinations.size();
		rowsNumber = trCombinationsNum + 2;

		// get all DV column names
		List<String> dvColumnNames = midas.getAllDVColumnsNames();

		// number of dv columns + three additional columns for: Stimulus,
		// Inhibitors, and Error
		dvColumnsNum = dvColumnNames.size();
		columnsNumber = dvColumnsNum + 3;

		for (int i = 0; i < trCombinations.size(); i++)
		{
			for (int j = 0; j < dvColumnNames.size(); j++)
			{
				try
				{
					HashMap<Integer, Double> xyValues = midas.getAllDVValuesForTHisDVColumn(dvColumnNames.get(j),
							trCombinations.get(i));
					int size = xyValues.size();
					double[][] oneRawDataSet = new double[2][size];
					int k = 0;
					for (Map.Entry<Integer, Double> entry : xyValues.entrySet())
					{
						oneRawDataSet[0][k] = (double) entry.getKey();
						oneRawDataSet[1][k] = (double) entry.getValue();
						k++;
					}
					DefaultXYDataset oneDataSet = new DefaultXYDataset();
					oneDataSet.addSeries("ds", oneRawDataSet);

					allDataSets.add(oneDataSet);

				}
				catch (MidasGeneralException e)
				{
					e.printStackTrace();
				}
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

	}

	public void createHeadBar()
	{
		List<String> dvNames = midas.getAllDAColumnsNames();

		GridBagConstraints gc = new GridBagConstraints();
		gc.weightx = 1;
		gc.weighty = 0.1;
		gc.fill = GridBagConstraints.BOTH;

		gc.gridy = 2;

		for (int i = 0; i < dvNames.size(); i++)
		{
			gc.gridx = i;

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

	public void createMainPart()
	{
		// Initial Grid Configuration
		GridBagConstraints gc = new GridBagConstraints();
		gc.weightx = 1;
		gc.weighty = 1;
		gc.fill = GridBagConstraints.BOTH;

		int k = 0;
		for (int i = 0; i < trCombinationsNum; i++)
		{
			for (int j = 0; j < dvColumnsNum; j++)
			{

				gc.gridx = j;
				// additional 2 is for the head bar and top bar
				gc.gridy = i + 3;
				boolean xMarkers = false;
				boolean yMarkers = false;

				if (j == 0)
				{
					yMarkers = true;
				}
				if (i == trCombinationsNum - 1)
				{
					xMarkers = true;
				}

				add(new OneChartPanel(allDataSets.get(k), xMarkers, yMarkers), gc);
				k++;
			}
		}
	}

	public void createRightPart()
	{

		GridBagConstraints gc = new GridBagConstraints();

		gc.weightx = 1;
		gc.weighty = 1;
		gc.fill = GridBagConstraints.BOTH;

		for (int i = 0; i < trCombinationsNum; i++)
		{
			gc.gridx = dvColumnsNum;
			gc.gridy = i + 3;

			boolean domainMarkers = false;
			if (i == trCombinationsNum - 1)
			{
				domainMarkers = true;

			}

			add(new StimBarChartPanel(barStimDatasets.get(i), domainMarkers), gc);

			gc.gridx++;
			add(new InhibitBarChartPanel(barInhibitDatasets.get(i), domainMarkers), gc);
		}

	}

	public void createFootBar()
	{

		GridBagConstraints gc = new GridBagConstraints();
		gc.weightx = 1;
		gc.weighty = 0.6;
		gc.fill = GridBagConstraints.BOTH;

		gc.gridy = rowsNumber + 1;

		for (int i = 0; i < dvColumnsNum + 2; i++)
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

	public void setMidas(CNO midas)
	{
		this.midas = midas;
	}

}
