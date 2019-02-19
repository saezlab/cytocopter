package uk.ac.ebi.cytocopter.internal.mahdinetworkmodeling;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import javax.swing.JOptionPane;
import uk.ac.ebi.cytocopter.internal.cellnoptr.tasks.FlagClass;

import uk.ac.ebi.cytocopter.internal.mahdiexceptions.EdgeException;
import uk.ac.ebi.cytocopter.internal.mahdiexceptions.NodeException;

public class Edge
{
	public final static boolean HIDDEN = false;
	public final static boolean VISIBLE = true;
	
	public final static boolean NOTAVAILABLE = false;
	public final static boolean AVAILABLE = true;
	
	
	private int id = -1;
	ArrayList<Node> sources = new ArrayList<Node>();
	ArrayList<Integer> sources_signs = new ArrayList<Integer>();
	Node target;
	private boolean visible;
	
	private boolean availabe;
	private boolean compressedExpanded;
	
	public boolean isCompressedExpanded()
	{
		return compressedExpanded;
	}

	public void setCompressedExpanded(boolean compressedExpanded)
	{
		this.compressedExpanded = compressedExpanded;
	}
	
	public boolean isAvailabe()
	{
		return availabe;
	}

	public void setAvailabe(boolean availabe)
	{
		this.availabe = availabe;
	}

	public Edge(ArrayList<Node> sources, Node target, ArrayList<Integer> sources_signs) throws EdgeException
	{
		if (!hasUniqueSources(sources)){           
                    ArrayList<String> repetitions = notUniqueNames(sources);
                    ArrayList<String> inconsistent = inconsistentSign(sources, repetitions, sources_signs);
                    String r = inconsistent.stream().collect(Collectors.joining(","));
                    //String flag = FlagClass.getInstance();
                    FlagClass.getStringInstance();
                    FlagClass.setStringInstance(target.getName()+ " to " + r);
                    //JOptionPane.showMessageDialog(null, "The following nodes may have inconsistent signs: " + r, "Warning", JOptionPane.WARNING_MESSAGE);
                
                }

		if (sources.size() != sources_signs.size())
			throw new EdgeException("Number of Nodes must be equal to Number of signs ");

		this.sources = sources;
		this.target = target;
		this.sources_signs = sources_signs;
		this.visible = Edge.VISIBLE;
		this.availabe = Edge.AVAILABLE;
		this.compressedExpanded = false;

	}
	
	public Edge(Node source, Node target, int source_sign)
	{
		sources.add(source);
		sources_signs.add(source_sign);
		this.target = target;
		
		this.visible = Edge.VISIBLE;
		this.availabe = Edge.AVAILABLE;
		this.compressedExpanded = false;
	}

	public void setID(int id)
	{
		this.id = id;
		
	}
	public int getID()
	{
		return this.id;
	}
	public boolean isVisible()
	{
		return visible;
	}


	public void setVisible(boolean visibility)
	{
		this.visible = visibility;
	}


	private boolean hasUniqueSources(ArrayList<Node> sources)
	{
		Set<String> sourceNames = new HashSet<String>();

		for (Node node : sources)
		{
			sourceNames.add(node.getName());
		}

		if (sourceNames.size() == sources.size())
			return true;

		return false;

	}
        private ArrayList<String> notUniqueNames(ArrayList<Node> sources)
	{
		ArrayList<String> sourceNames = new ArrayList<String>();
                ArrayList<String> toReturn = new ArrayList<String>();

		for (Node node : sources)
		{
			sourceNames.add(node.getName());
		}
                
                Map<String, Integer> duplicates = new HashMap<String, Integer>();

                for (String str : sourceNames) {
                   if (duplicates.containsKey(str)) {
                      duplicates.put(str, duplicates.get(str) + 1);
                   } else {
                      duplicates.put(str, 1);
                   }
                }

                for (Map.Entry<String, Integer> entry : duplicates.entrySet()) {
                    
                    if(entry.getValue()>1){
                        toReturn.add(entry.getKey());
                    }
                    
                   
                }
                return toReturn;
	}
        private ArrayList<String> inconsistentSign(ArrayList<Node> sources, ArrayList<String> repetitions, ArrayList<Integer> sources_signs)
	{
         
                ArrayList<String> toReturn = new ArrayList<String>();
                ArrayList<Integer> matched_sign = new ArrayList<>();
                for (int i = 0; i<repetitions.size();i++){
                    for (int j = 0; j<sources.size();j++){
                        String name = sources.get(j).getName();
                        if (repetitions.get(i).equals(name)){
                            matched_sign.add(sources_signs.get(j));
                        }
                    }
                    //for (Integer K : matched_sign)JOptionPane.showMessageDialog(null ,K);
                    boolean allEqual = matched_sign.stream().distinct().limit(2).count() <= 1;
                    if (allEqual==false){
                        toReturn.add(repetitions.get(i));
                    }
                    matched_sign = new ArrayList<>();
                }
                return toReturn;
	}
        

	


	public ArrayList<Node> getSources()
	{
		return sources;
	}
	public ArrayList<Integer> getSourceSigns()
	{
		return sources_signs;
	}

	public Node getTarget()
	{
		return target;
	}
	
	public Double penalty()
	{
		return (double) sources.size();
		
	}
	
	//correct^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
	// [Equality of two Hybrid edges by their Names]
	// This function is for comparing two edges for equality
	// They are not equal if:
	//	   1. They have different source array sizes
	//	   2. They have different sourceSign array sizes
	//     3. They have different targets
	//     4. Each of their source node is not equal (By Name) to its corresponding source
	//			in the other array
	//     5. Each of their source sign is not equal to its corresponding sign
	//			in the other array.
	public boolean equals(Edge b)
	{
		if (b.sources.size() != sources.size())
			return false;

		if (b.sources_signs.size() != sources_signs.size())
			return false;
		
		if(! this.target.getName().equals(b.target.getName()))
			return false;

		for(int i = 0; i<b.sources.size();i++)
		{
			if(! this.sources.get(i).getName().equals(b.sources.get(i).getName())
					|| this.sources_signs.get(i) != b.sources_signs.get(i))
			{
				return false;
			}
		}
		
		

		return true;
	}
	
	//correct^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
	// [Equality of two simple edge by their Names]
	// This function is for comparing this edge with a simple edge for equality
	// If they want to be equal:
	//     1. Both should have one source
	//	   2. Both should have one source_sign
	//     3. Both should have the same source NAME
	//	   4. Both should have the same sign
	//     5. Both should have the same target NAME (All edges have only one target)
	public boolean equals(Node sourceNode, Node targetNode, int source_sign)
	{
		
		if (this.sources.size() == 1 && this.sources_signs.size() == 1
				&& this.sources.get(0).getName().equals(sourceNode.getName())
				&& this.target.getName().equals(targetNode.getName())
				&& this.sources_signs.get(0) == source_sign)
			return true;
		else
			return false;
	}
	
	//correct^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
	// [Equality of two simple edge by their References]
	// This function is for comparing this edge with a simple edge for equality
	// If they want to be equal:
	//     1. Both should have one source
	//	   2. Both should have one source_sign
	//     3. Both should point to the same source
	//	   4. Both should have the same sign
	//     5. Both should have the same target (All edges have only one target)
	public boolean equalsRef(Node sourceNode, Node targetNode, int source_sign)
	{
		
		if (this.sources.size() == 1 && this.sources_signs.size() == 1 && this.sources.get(0) == sourceNode
				&& this.target == targetNode && this.sources_signs.get(0) == source_sign)
			return true;
		else
			return false;
	}

	private boolean isContainedSameSourceSameSign(Node n, Integer nSign)
	{
		
		for (int i = 0; i < sources.size(); i++)
		{
			if (sources.get(i).equals(n) && sources_signs.get(i) == nSign)
				return true;
		}
		return false;
	}
	
	//correct^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
	public int computeValue(int previoustime) throws NodeException
	{
		//we get first source state in the previous time
		int state = sources.get(0).getState(previoustime);
		int sign = sources_signs.get(0);
		
		int result = state_sign_2MyBool(state, sign);
		
		for (int i=1;i<sources.size();i++)
		{
			result = andOptWithNA(result, state_sign_2MyBool(sources.get(i).getState(previoustime), sources_signs.get(i)));
		}
		return result;
	}
	
	//correct^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
	public static int state_sign_2MyBool(int state, int sign)
	{
		if (state == 1 && sign == 1)
			return 1;
		else if (state == 1 && sign == -1)
			return 0;
		else if (state == 0 && sign == 1)
			return 0;
		else if(state == 0 && sign == -1)
			return 1;
		else// if(state == -1)
			return -1;
	}
	
	//correct^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
	public static int andOptWithNA(int i, int j)
	{
		if(i ==0 && j==0)
			return 0;
		else if(i ==1 && j==0)
			return 0;
		else if(i ==0 && j==1)
			return 0;
		else if(i==1 && j==1)
			return 1;
		else //if(i==-1 || j==-1)
			return -1;
	}

	//correct^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
	private boolean int2Bool(int i)
	{
		return i > 0 ? true : false;
	}

	//correct^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
	private int bool2Int(boolean b)
	{
		return b ? 1 : 0;
	}
	
	
	
	

}
