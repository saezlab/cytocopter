package uk.ac.ebi.cytocopter.internal.mahdimidas;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import au.com.bytecode.opencsv.CSVReader;
import uk.ac.ebi.cytocopter.internal.mahdimidasexceptions.DifferentLengthExp;
import uk.ac.ebi.cytocopter.internal.mahdimidasexceptions.MidasGeneralException;
import uk.ac.ebi.cytocopter.internal.mahdimidasexceptions.TimeSignalIrregularity;

public class CNO
{
	public final int HEAD_INDEX = 0;
	public final int FIRST_DATA_ROW_INDEX = 1;
	
	//ToyModelPB.csv has 160 number of rows.
	//It starts from 0 to 159.
	//In the midas file, we have 161 row starting from 1 (NOT 0): first row contains column names
	public final int Number_OF_ROWS;
    //ToyModelPB.csv has 10 different combination of values for treatments. 
	public final int Number_OF_TR_COMBINATIONS;
	//ToyModelPB.csv has 17 columns.
	public final int LAST_COLUMN_INDEX;
	////For ToyModelPB.csv, it is 5 (DA:raf1)
	public final int FIRST_DA_COLUMN_INDEX;
	//For ToyModelPB.csv, it is 11 (DV:raf1)
	public final int FIRST_DV_COLUMN_INDEX;
	

	private String location;
	// !!! Change to private later!
	public List<String[]> data;

	public CNO(String location) throws FileNotFoundException, IOException, MidasGeneralException
	{
		this.location = location;
		this.reload();
		Number_OF_ROWS = data.size()-1;
		Number_OF_TR_COMBINATIONS = howManyCombinationsWeHaveForTR();
		LAST_COLUMN_INDEX = data.get(0).length;
		FIRST_DA_COLUMN_INDEX = getIndexOfFirstDAColumn();
		FIRST_DV_COLUMN_INDEX = getIndexOfFirstDVColumn();
	}

	private void reload() throws FileNotFoundException, IOException
	{
		CSVReader reader = new CSVReader(new FileReader(location));
		data = reader.readAll();
		reader.close();
	}
	
	
	//This function will find the index of a set of column names
	//You give the function the column names, and it will return the indecies of those columns
	//If, it doesn't find the column, it puts a -1 on the corresponding cell
	public List<Integer> getIndiciesOfTheseColumns(ArrayList<String> RowColumnsNames)
	{
		ArrayList<Integer> desiredColumnsIndicies = new ArrayList<Integer>();
		for(String s : RowColumnsNames)
		{
			desiredColumnsIndicies.add(getIndexOfOneColumn(s));
		}
		return desiredColumnsIndicies;
	}
	
	public List<Integer> getIndiciesOfTheseColumns(String[] RowColumnsNames)
	{
		return getIndiciesOfTheseColumns((ArrayList<String>)Arrays.asList(RowColumnsNames));
	}
	
	
	
	//This function get a column name String, and returns the integer index of that column
	public Integer getIndexOfOneColumn(String ColumnName)
	{
		String[] listOfAllColumnsNames = data.get(HEAD_INDEX);

			for (int j=0; j<listOfAllColumnsNames.length;j++)
			{
				String s = listOfAllColumnsNames[j];
				if (ColumnName.equals(s))
				{
					return j;
				}
			}
			return -1;
	}
	// This method gets a column name and a row number. It returns the value of that cell
	//  as a integer.
	// If someone pass 0 as the row number, we increase it by 1, since the
	//  first row only contains column names.
	public Integer getValueOfOneCell(String ColumnName, int row) throws MidasGeneralException
	{
		int columnIndex = getIndexOfOneColumn(ColumnName);
		getClass();
		if (columnIndex == -1)
		{
			throw new MidasGeneralException("This column does not exist.");
		}
		else if (row > data.size())
		{
			throw new MidasGeneralException("The row number is outside of the range.");
		}
		else
		{
			try
			{
				return Integer.parseInt(data.get(row+1)[columnIndex]);
			}
			catch (Exception e)
			{
				throw new MidasGeneralException("The disired cell can not be converted to int!");
			}
		}
	}
	
	// This method gets a column name and a row number. It returns the value of that cell
	//  as a double.
	// If someone pass 0 as the row number, we increase it by 1, since the
	//  first row only contains column names.
	public Double getValueOfOneCellAsDouble(String ColumnName, int row) throws MidasGeneralException
	{
		int columnIndex = getIndexOfOneColumn(ColumnName);
		getClass();
		if (columnIndex == -1)
		{
			throw new MidasGeneralException("This column does not exist.");
		}
		else if (row > data.size())
		{
			throw new MidasGeneralException("The row number is outside of the range.");
		}
		else
		{
			try
			{
				return Double.parseDouble(data.get(row+1)[columnIndex]);
			}
			catch (Exception e)
			{
				throw new MidasGeneralException("The disired cell can not be converted to int!");
			}
		}
	}
	
	public List<ArrayList<Integer>> getValuesofSomeColumnsAsIntegers(ArrayList<String> columnNames)
	{
		ArrayList<Integer> rows = new ArrayList<Integer>();
		for(int i=0;i<Number_OF_ROWS;i++)
		{
			rows.add(i);
		}
		return getValuesByColumnsAndRowsAsIntegers(columnNames, rows);
	}
	
	public List<ArrayList<Integer>> getValuesByColumnsAndRowsAsIntegers(ArrayList<String> columnNames, ArrayList<Integer> rows)
	{
		int columnSize = columnNames.size();
		ArrayList<Integer> oneRow = new ArrayList<Integer>(columnSize);
		List<ArrayList<Integer>> result = new ArrayList<ArrayList<Integer>>(rows.size());
		
		for(Integer i: rows )
		{
			oneRow = new ArrayList<Integer>(columnSize);
			for(Integer j: getIndiciesOfTheseColumns(columnNames))
			{
				oneRow.add(Integer.parseInt(data.get(i+1)[j]));
			}
			result.add(oneRow);
		}
		return result;
	}
	//overloaded version of the previous method with different argument types. 
	public List<ArrayList<Integer>> getValuesByColumnsAndRowsAsIntegers(String[] columnNames, Integer[] rows)
	{
		return getValuesByColumnsAndRowsAsIntegers(new ArrayList<String>(Arrays.asList(columnNames)),new ArrayList<Integer>(Arrays.asList(rows)));
	}

	public List<String> namesCues()
	{
		String[] headRow = data.get(HEAD_INDEX);
		List<Integer> indices = getNamesCuesIndices();
		List<String> result = new ArrayList<String>();

		for (Integer i : indices)
		{
			String s = headRow[i].replaceAll("TR:", "");
			if (s.endsWith("i"))
			{
				result.add(s.substring(0, s.length() - 1));
			}
			else
			{
				result.add(s);
			}
		}

		return result;
	}
	//show all the DV Columns without DV at the beginning
	public List<String> namesDVColumns()
	{
		String[] headRow = data.get(HEAD_INDEX);
		List<String> result = new ArrayList<String>();
		
		for(int i=0;i<headRow.length;i++)
		{
			if(headRow[i].startsWith("DV:"))
			{
				result.add(headRow[i].substring(3, headRow[i].length()));
			}
		}

		return result;
	}

	public List<String> namesStimuli()
	{
		String[] headRow = data.get(HEAD_INDEX);
		List<Integer> indices = getNamesStimuliIndices();
		List<String> result = new ArrayList<String>();

		for (Integer i : indices)
		{
			result.add(headRow[i].replaceAll("TR:", ""));
		}
		return result;
	}

	public List<String> namesInhibitors()
	{
		String[] headRow = data.get(HEAD_INDEX);
		List<Integer> indices = getNamesInhibitorsIndices();
		List<String> result = new ArrayList<String>();

		for (Integer i : indices)
		{
			String s = headRow[i].replaceAll("TR:", "");
			result.add(s.substring(0, s.length() - 1));
		}
		return result;
	}
	public List<String> getAllTRColumnsNames()
	{
		ArrayList<String> result = new ArrayList<String>();
		String[] headRow = data.get(HEAD_INDEX);
		for(String s : headRow)
		{
			if(s.startsWith("TR:")&& !s.endsWith("CellLine"))
				result.add(s);
		}
		
		return result;
	}
	public List<String> getAllDAColumnsNames()
	{
		ArrayList<String> result = new ArrayList<String>();
		String[] headRow = data.get(HEAD_INDEX);
		for(String s : headRow)
		{
			if(s.startsWith("DA:"))
				result.add(s);
		}
		
		return result;
	}
	public List<String> getAllDVColumnsNames()
	{
		ArrayList<String> result = new ArrayList<String>();
		String[] headRow = data.get(HEAD_INDEX);
		for(String s : headRow)
		{
			if(s.startsWith("DV:"))
				result.add(s);
		}
		return result;
	}

	public List<String> namesSignals()
	{
		String[] headRow = data.get(HEAD_INDEX);
		List<Integer> indices = getNamesSignalsIndices();
		List<String> result = new ArrayList<String>();

		for (Integer i : indices)
		{
			result.add(headRow[i].replaceAll("DV:", ""));
		}

		return result;
	}
	
	public int  howManyCombinationsWeHaveForTR() throws MidasGeneralException
	{
		int row = 0;
		String firstDAColumnName = getAllDAColumnsNames().get(0);

		while(getValueOfOneCell(firstDAColumnName, row)==0)
		{
			row++;
		}
		
		
		return row;
	}
	

	
	public String nodeName2RawTRColumnName(String nodeName)
	{
		String nodeNameStimulus = "TR:" + nodeName;
		String nodeNameInhibitors = "TR:" + nodeName+"i";
		int flag = -1;
		for(int i = 0; i<LAST_COLUMN_INDEX && flag ==-1;i++)
		{
			if(nodeNameStimulus.equals(data.get(HEAD_INDEX)[i]))
				flag =1; //Stimulus is found
			else if (nodeNameInhibitors.equals(data.get(HEAD_INDEX)[i]))
				flag = 0; //Inhibitor is found
		}
		
		if(flag==1)
			return nodeNameStimulus;
		else if(flag==0)
			return nodeNameInhibitors;
		else
			return null;
	}
	
	public boolean isStimuli(String columnName)
	{
		if(namesStimuli().contains(columnName))
			return true;
		else
		{
			return false;
		}
	}
	
	public boolean isInhibitor(String columnName)
	{
		if(namesInhibitors().contains(columnName))
			return true;
		else
		{
			return false;
		}
	}
	
	public ArrayList<String> nodeNames2RawTRColumnNames(ArrayList<String> nodeNames) throws MidasGeneralException
	{
		ArrayList<String> result = new ArrayList<String>();
		
		for(int i=0;i<nodeNames.size();i++)
		{
			String trColumnName = nodeName2RawTRColumnName(nodeNames.get(i));
			if(trColumnName!=null)
				result.add(trColumnName);
			else
				throw new MidasGeneralException("There is no corresponding TR column for at least one of your nodes");
				
		}
		return result;
	}

	public List<Double> timeSignals() throws TimeSignalIrregularity
	{

		List<Integer> indices = getTimesSignalsIndices();
		String[] currentRow = null;

		int firstCol = getFirstNameSignalIndex();
		int lastCol = getLastNameSignalIndex();

		List<Double> result = new ArrayList<Double>();
		for (int i = 1; i < data.size(); i++)
		{
			currentRow = data.get(i);
			double candidate_number = Double.parseDouble(currentRow[indices.get(0)]);

			for (int j = firstCol + 1; j < lastCol; j++)
			{
				if (Double.parseDouble(currentRow[j]) != candidate_number)
				{
					throw new TimeSignalIrregularity();
				}
			}
			if (!result.contains(candidate_number))
			{
				result.add(candidate_number);
			}
		}

		return result;
	}

	public List<ArrayList<Double>> valueCues()
	{
		List<ArrayList<Double>> result = new ArrayList<ArrayList<Double>>();
		ArrayList<Integer> first_combination_indices = getSplitedValueCuesIndices().get(0);
		List<Integer> desired_names_cues_columns = getNamesCuesIndices();
		for (Integer i : first_combination_indices)
		{
			ArrayList<Double> one_row = new ArrayList<Double>();
			for (Integer j : desired_names_cues_columns)
			{
				one_row.add(Double.parseDouble(data.get(i)[j]));
			}
			result.add(one_row);
		}
		return result;
	}

	public List<ArrayList<Integer>> valueCuesAsInteger()
	{
		List<ArrayList<Integer>> result = new ArrayList<ArrayList<Integer>>();
		ArrayList<Integer> first_combination_indices = getSplitedValueCuesIndices().get(0);
		List<Integer> desired_names_cues_columns = getNamesCuesIndices();
		for (Integer i : first_combination_indices)
		{
			ArrayList<Integer> one_row = new ArrayList<Integer>();
			for (Integer j : desired_names_cues_columns)
			{
				one_row.add(Integer.parseInt(data.get(i)[j]));
			}
			result.add(one_row);
		}
		return result;
	}
	
	protected int getValueCuesXDimension()
	{
		int result = 0;

		String[] currentRow = null;

		int firstCol = getFirstNameSignalIndex();

		boolean flag_first_row_passed = false;
		String[] first_data_row = data.get(1);
		double candidate_num = Double.parseDouble(first_data_row[firstCol]);

		for (int i = 1; i < data.size() && flag_first_row_passed == false; i++)
		{
			currentRow = data.get(i);

			if (Double.parseDouble(currentRow[firstCol]) == candidate_num)
			{
				result = i;
			}
			else
			{
				flag_first_row_passed = true;
			}
		}

		return result;
	}

	protected int getValueCuesYDimension()
	{
		return (getLastNameSignalIndex() - getFirstNameSignalIndex() + 1);
	}

	protected int getFirstNameSignalIndex()
	{
		String[] headRow = data.get(HEAD_INDEX);
		boolean flag = false;
		int result = -1;

		for (int i = 0; i < headRow.length && flag == false; i++)
		{
			if (headRow[i].contains("DA:"))
			{
				result = i;
				flag = true;
			}
		}

		return result;
	}

	protected int getLastNameSignalIndex()
	{
		String[] headRow = data.get(HEAD_INDEX);
		int result = -1;

		for (int i = 0; i < headRow.length; i++)
		{
			if (headRow[i].contains("DA:"))
			{
				result = i;
			}
		}

		return result;
	}

	public List<Integer> getNamesCuesIndices()
	{
		String[] headRow = data.get(HEAD_INDEX);
		List<Integer> result = new ArrayList<Integer>();

		for (int i = 0; i < headRow.length; i++)
		{
			if (headRow[i].startsWith("TR:") && !headRow[i].endsWith("CellLine"))
			{
				result.add(i);
			}
		}

		return result;
	}

	public List<Integer> getNamesInhibitorsIndices()
	{
		String[] headRow = data.get(HEAD_INDEX);
		List<Integer> result = new ArrayList<Integer>();

		for (int i = 0; i < headRow.length; i++)
		{
			if (!headRow[i].startsWith("TR:Cell") && headRow[i].startsWith("TR:") && headRow[i].endsWith("i"))
			{
				result.add(i);
			}
		}

		return result;
	}

	public List<Integer> getNamesStimuliIndices()
	{
		String[] headRow = data.get(HEAD_INDEX);
		List<Integer> result = new ArrayList<Integer>();

		for (int i = 0; i < headRow.length; i++)
		{
			if (!headRow[i].startsWith("TR:Cell") && headRow[i].startsWith("TR:") && !headRow[i].endsWith("i"))
			{
				result.add(i);
			}
		}

		return result;
	}

	public List<Integer> getNamesSignalsIndices()
	{
		String[] headRow = data.get(HEAD_INDEX);
		List<Integer> result = new ArrayList<Integer>();

		for (int i = 0; i < headRow.length; i++)
		{
			if (headRow[i].startsWith("DV:"))
			{
				result.add(i);
			}
		}

		return result;
	}

	public List<Integer> getTimesSignalsIndices()
	{
		String[] headRow = data.get(HEAD_INDEX);
		List<Integer> result = new ArrayList<Integer>();

		for (int i = 0; i < headRow.length; i++)
		{
			if (headRow[i].startsWith("DA:"))
			{
				result.add(i);
			}
		}

		return result;
	}

	public int getValueCuesCombinationNumbers()
	{
		return this.getSplitedValueCuesIndices().size();
	}

	public boolean compareRows(String[] row1, String[] row2, int beginCol, int endCol)
	{
		boolean flag_equal = true;

		for (int i = beginCol; i <= endCol && flag_equal == true; i++)
		{
			if (!row1[i].equals(row2[i]))
				flag_equal = false;
		}

		return flag_equal;
	}

	public boolean compareRows(String[] row1, String[] row2) throws DifferentLengthExp
	{
		int last_index = 0;
		if (row1.length == row2.length)
		{
			last_index = row1.length - 1;
		}
		else
		{
			throw new DifferentLengthExp();
		}

		return this.compareRows(row1, row2, 0, last_index);
	}

	public boolean compareRows(String[] row1, String[] row2, List<Integer> columns)
	{
		boolean flag_equal = true;

		for (Integer i : columns)
		{
			if (!row1[i].equals(row2[i]))
				flag_equal = false;
		}

		return flag_equal;
	}

	public boolean compareRows(int row_number1, int row_number2, List<Integer> columns)
	{
		boolean flag_equal = true;
		String[] row1 = data.get(row_number1);
		String[] row2 = data.get(row_number2);

		for (Integer i : columns)
		{
			if (!row1[i].equals(row2[i]))
				flag_equal = false;
		}

		return flag_equal;
	}

	public boolean compareRows(int row_number1, int row_number2, int beginCol, int endCol)
	{
		boolean flag_equal = true;
		String[] row1 = data.get(row_number1);
		String[] row2 = data.get(row_number2);

		for (int i = beginCol; i <= endCol && flag_equal == true; i++)
		{
			if (!row1[i].equals(row2[i]))
				flag_equal = false;
		}

		return flag_equal;
	}

	public List<ArrayList<Integer>> getSplitedValueCuesIndices()
	{

		List<ArrayList<Integer>> result = new ArrayList<ArrayList<Integer>>();
		List<Integer> desired_columns = getNamesCuesIndices();
		int upperbound = data.size();
		for (int i = FIRST_DATA_ROW_INDEX; i < upperbound;)
		{
			ArrayList<Integer> one_section = new ArrayList<Integer>();
			one_section.add(i);
			int j = i + 1;
			boolean end_section_happened = false;
			while (j < upperbound && !compareRows(i, j, desired_columns))
			{
				one_section.add(j);
				j++;
			}
			result.add(one_section);
			i = j;
		}
		return result;
	}
	
	public  List<ArrayList<Integer>> getTreatmentCombinationsForTheseNodes(ArrayList<String> nodeNames) throws MidasGeneralException
	{
		ArrayList<Integer> desiredRows = new ArrayList<Integer>();
		for(int i=0;i<Number_OF_TR_COMBINATIONS;i++)
			desiredRows.add(i);
		
		ArrayList<String> columnNames = nodeNames2RawTRColumnNames(nodeNames);
		
		
		return getValuesByColumnsAndRowsAsIntegers(columnNames, desiredRows);
	}
	
	public List<Integer> getThisRowOfTR(int rowNumber) throws MidasGeneralException
	{
		List<String> TRColumnNames = getAllTRColumnsNames();
		List<Integer> result = new ArrayList<Integer>();
		
		for(String columnName : TRColumnNames)
		{
			result.add(getValueOfOneCell(columnName, rowNumber));
			
		}
		return result;
	}
	
	public List<Integer> getThisRowOfDA(int rowNumber) throws MidasGeneralException
	{
		List<String> DAColumnNames = getAllDAColumnsNames();
		List<Integer> result = new ArrayList<Integer>();
		
		for(String columnName : DAColumnNames)
		{
			result.add(getValueOfOneCell(columnName, rowNumber));
			
		}
		return result;
	}
	
	public List<Double> getThisRowOfDV(int rowNumber) throws MidasGeneralException
	{
		List<String> DVColumnNames = getAllDVColumnsNames();
		List<Double> result = new ArrayList<Double>();
		
		for(String columnName : DVColumnNames)
		{
			result.add(getValueOfOneCellAsDouble(columnName, rowNumber));
			
		}
		return result;
	}
	
	//This function return the index of first DV column
	//For example in ToyModelPB, it returns 11.
	public int getIndexOfFirstDVColumn()
	{
		String[] headRow = data.get(HEAD_INDEX);
		for(int i=0; i<headRow.length;i++)
		{
			if(headRow[i].startsWith("DV:"))
				return i;
		}
		
		//It will never be reached
		return -1;	
	}
	
	//This function return the index of first DA column
	//For example in ToyModelPB, it returns 5.
	//For ToyModelPB.csv, it is 5 (DA:raf1)
	public int getIndexOfFirstDAColumn()
	{
		String[] headRow = data.get(HEAD_INDEX);
		for(int i=0; i<headRow.length;i++)
		{
			if(headRow[i].startsWith("DA:"))
				return i;
		}
		
		//It will never be reached
		return -1;	
	}
	
	public int getIndexOfThisRow(ArrayList<Integer> treatmentCombination, int timePoint) throws MidasGeneralException
	{
		Integer result = null;
		ArrayList<String> treatmentColumnNames = (ArrayList<String>) this.getAllTRColumnsNames();
		int size = treatmentColumnNames.size();
		if(treatmentColumnNames.size() != treatmentCombination.size())
			throw new MidasGeneralException("The number of treatmentCombination should be equal to the number of treatments in the midas file");
		
		String firstDAColumnName = this.getAllDAColumnsNames().get(0);
		 
		
		for(int i=0;i<Number_OF_ROWS;i++)
		{
			ArrayList<Integer> oneTRRow = (ArrayList<Integer>) getThisRowOfTR(i);
			if(equalityOfIntArrayLists(oneTRRow, treatmentCombination) && this.getValueOfOneCell(firstDAColumnName, i) == timePoint)
			{
				result = i;
				return i;
			}
		}
		
		return result;
	}
	
	public List<Double> getDVDeviationwithThisRow(ArrayList<Integer> simulationResults, ArrayList<Integer> treatmentCombination, int timePoint) throws MidasGeneralException
	{
		List<Double> dvValuesOfTheSource = this.getDVValuesOfThisRow(treatmentCombination, timePoint);
		int size = dvValuesOfTheSource.size();
		if(simulationResults.size() != size)
		{
			throw new MidasGeneralException("The size of the simulation result array should be equal to the number of DV columns");
		}
		
		ArrayList<Double> result = new ArrayList<Double>();
		
		for(int i=0;i<size;i++)
		{
			result.add(Math.pow(((double) simulationResults.get(i) - dvValuesOfTheSource.get(i)),2d));
		}
		return result;
		
		
	}
	//overloaded version of the previous method!
	public List<Double> getDVDeviationwithThisRow(List<Double> simulationResults, ArrayList<Integer> treatmentCombination, int timePoint) throws MidasGeneralException
	{
		List<Double> dvValuesOfTheSource = this.getDVValuesOfThisRow(treatmentCombination, timePoint);
		int size = dvValuesOfTheSource.size();
		if(simulationResults.size() != size)
		{
			throw new MidasGeneralException("The size of the simulation result array should be equal to the number of DV columns");
		}
		
		ArrayList<Double> result = new ArrayList<Double>();
		
		for(int i=0;i<size;i++)
		{
			result.add(simulationResults.get(i) - dvValuesOfTheSource.get(i));
		}
		return result;
		
	}
	
	public List<Double> getDVValuesOfThisRow(ArrayList<Integer> treatmentCombination, int timePoint) throws MidasGeneralException
	{
		return getThisRowOfDV(getIndexOfThisRow(treatmentCombination, timePoint));
		
	}
	
	public List<HashMap<String, Double>> getDVCubeofThisTimePoint(int timepoint)
	{
		List<HashMap<String, Double>> result = new ArrayList<HashMap<String,Double>>();
		
		String[] headRow = data.get(HEAD_INDEX);
		
		for(int i = FIRST_DATA_ROW_INDEX; i<Number_OF_ROWS;i++)
		{
			String[] oneRow = data.get(i);
			HashMap<String, Double> oneRowOfResult=null;
			if(Integer.parseInt(oneRow[FIRST_DA_COLUMN_INDEX]) == timepoint )
			{
				oneRowOfResult = new HashMap<String, Double>();
				
				for(int j = FIRST_DV_COLUMN_INDEX;j<LAST_COLUMN_INDEX;j++)
				{
					oneRowOfResult.put(headRow[j].substring(3, headRow[j].length()), Double.parseDouble(oneRow[j]));					
				}
				result.add(oneRowOfResult);
			}
		}
		
		return result;
		
	}
	
	

	
	private static boolean equalityOfIntArrayLists(List<Integer> a,List<Integer> b)
	{
		int size = a.size();
		
		if(size!= b.size())
			return false;
		
		boolean flag = true;
		for(int i=0;i<a.size() && flag == true;i++)
		{
			if(a.get(i)!=b.get(i))
				flag = false;
		}
		
		return flag;
	}
	
	public Integer getIndexofThisRow(ArrayList<String> treatmentColumnNames, ArrayList<Integer> treatmentCombination, int timepoint) throws MidasGeneralException
	{
		int size=-1;
		
		if(treatmentColumnNames.size() != treatmentCombination.size())
			throw new MidasGeneralException("The sizes has to be equal");
		else
			size = treatmentColumnNames.size();
		
		
		int result=-1;
		String[] headRow = data.get(0);
		
		
		for(int i=0;i<data.size();i++)
		{
			for(int j=0;j<Number_OF_TR_COMBINATIONS;j++)
			{
				
			}
			
		}
		
		return result;
	}
	
	
	public HashMap<Integer, Double> getAllDVValuesForTHisDVColumn(String ColumnName, ArrayList<Integer> treatmentCombination) throws MidasGeneralException
	{
		String[] headRow = data.get(0);
		int columnIndex = getIndexOfOneColumn(ColumnName);
		
		
		HashMap<Integer, Double> result = new HashMap<Integer, Double>();
		
		for(int i=0;i<Number_OF_ROWS;i++)
		{
			List<Integer> currentTreatmentCombination = this.getThisRowOfTR(i);
			if (currentTreatmentCombination.equals(treatmentCombination))
			{
				int xValue = Integer.parseInt(data.get(i+1)[FIRST_DA_COLUMN_INDEX]);
				double yValue = Double.parseDouble(data.get(i+1)[columnIndex]);
				result.put(xValue, yValue);
			}
		}
		
		return result;
		
	}
	
	public HashMap<Integer, Double> getAllDVValuesForTHisNode(String NodeName, ArrayList<Integer> treatmentCombination) throws MidasGeneralException
	{
		return (this.getAllDVValuesForTHisDVColumn("DV:" + NodeName,treatmentCombination));
		
	}
	
	public Double getDVValueForTHisDVColumnAtThisTimepoint(String columnName, ArrayList<Integer> treatmentCombination, int timePoint) throws MidasGeneralException
	{

		String[] headRow = data.get(0);
		int columnIndex = getIndexOfOneColumn(columnName);
		
		Double result = -1d;
		int time = -1;
		
		for(int i=0;i<Number_OF_ROWS && result==-1d;i++)
		{
			time = Integer.parseInt(data.get(i+1)[FIRST_DA_COLUMN_INDEX]);
			if(time == timePoint)
			{
				List<Integer> currentTreatmentCombination = this.getThisRowOfTR(i);
				if (currentTreatmentCombination.equals(treatmentCombination))
				{
					result = Double.parseDouble(data.get(i+1)[columnIndex]);
				}
			}
		}
		
		return result;
		
	}
	
	public List<Integer> getDAValuesAsInt()
	{
		ArrayList<Integer> result  = new ArrayList<Integer>();
		
		for(int i=1;i<data.size();i++)
		{
			int oneElement = Integer.parseInt(data.get(i)[FIRST_DA_COLUMN_INDEX]);
			
			if(!result.contains(oneElement))
			{
				result.add(oneElement);
			}
		}
		
		return result;
	}
	
	public List<Double> getDAValuesAsDouble()
	{
		ArrayList<Double> result  = new ArrayList<Double>();
		
		for(int i=1;i<data.size();i++)
		{
			double oneElement = Double.parseDouble(data.get(i)[FIRST_DA_COLUMN_INDEX]);
			
			if(!result.contains(oneElement))
			{
				result.add(oneElement);
			}
		}
		
		return result;
	}
	
	public int combinations_of_timepoint_and_treatment()
	{
		return data.size() - 1;
	}
	
	public int number_of_DV_columns()
	{
		return getAllDVColumnsNames().size();
	}
	
	public int number_of_TR_columns()
	{
		return getAllTRColumnsNames().size();
	}


}
