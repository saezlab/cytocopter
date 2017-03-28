package uk.ac.ebi.cytocopter.internal.mahdinetworkmodeling;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

class FileInMemory
{
		//this index holds the index of next line that WILL be added to the collection.
		int lastIndex;
		final ArrayList<FileLine> lines;
		private String fileAddress;
		private static Scanner inputStream;
		
		public FileInMemory(String fileAddress)
		{
			lastIndex = 0;
			lines = new ArrayList<FileLine>();
			this.fileAddress = fileAddress;
			inputStream = new Scanner(fileAddress);
			
			try
			{
				inputStream = new Scanner(new File(fileAddress));
				if(System.getProperty("os.name").toLowerCase().indexOf("win") >= 0)
					inputStream.useDelimiter("\\r\\n");
				else
					inputStream.useDelimiter("\\n");
				
				while (inputStream.hasNext())
				{
					String[] oneLine =inputStream.next().split("\t");
					this.addLine(oneLine[0], Integer.parseInt(oneLine[1]), oneLine[2]);
				}
			}
			catch (FileNotFoundException e)
			{
				e.printStackTrace();
			}
		}
		
		public void addLine(String source, int sign, String target)
		{
			FileLine oneLine = new FileLine();
			
			oneLine.setLineNumber(lastIndex);
			oneLine.setSource(source);
			oneLine.setSign(sign);
			oneLine.setTarget(target);
			
			lines.add(oneLine);
			lastIndex++;
		}
		
		
		public ArrayList<String> sourcesOfAnd(String andName)
		{
			ArrayList<String> result = new ArrayList<String>();
			for (int i=0;i<lastIndex;i++)
			{
				if(lines.get(i).getTarget().equals(andName))
				{
					result.add(lines.get(i).getSource());
				}
			}
			return result;
		}
		
		public ArrayList<Integer> signsOfAnd(String andName)
		{
			ArrayList<Integer> result = new ArrayList<Integer>();
			for (int i=0;i<lastIndex;i++)
			{
				if(lines.get(i).getTarget().equals(andName))
				{
					result.add(lines.get(i).getSign());
				}
			}
			return result;
		}
		
		public String targetOfAnd(String andName)
		{
			String result = null;
			for (int i=0;i<lastIndex;i++)
			{
				if(lines.get(i).getSource().equals(andName))
				{
					result = lines.get(i).getTarget();
				}
			}
			return result;
		}
		
		public FileLine readLine(int i)
		{
			if (i>=lastIndex)
				return null;
			
			return lines.get(i);
					
		}

		public int getLastIndex()
		{
			return lastIndex;
		}
		
		
	}
