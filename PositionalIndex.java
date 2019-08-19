/**
 * import statements
 */
import java.util.ArrayList;
/**
 * This class contains the main method that runs PositionalIndex.java.
 * Course:           ISTE 612: Knowledge Process Technologies
 * Name:             Wadekar, Rishi
 * Lab:              Lab #2
 * Date:             03/04/2019
 * @auhor            Rishi Wadekar
 */

public class PositionalIndex {
	String[] myDocs;
	ArrayList<String> termDictionary;                  
	ArrayList<ArrayList<Doc>> docLists;
	
	/**
	 * Construct a positional index 
	 * @param docs List of input strings or file names
	 * 
	 */
	public PositionalIndex(String[] docs)
	{
		//TASK1: TO BE COMPLETED
      myDocs = docs;
      termDictionary = new ArrayList<String>();
      docLists = new ArrayList<ArrayList<Doc>>();
      ArrayList<Doc> docList = new ArrayList<Doc>();
      
      for(int i = 0; i < myDocs.length; i++) {
         String[] words = myDocs[i].split(" ");
         String word;
         
         for(int j = 0; j < words.length; j++) {
            boolean match = false;
            word = words[j];
            if(!termDictionary.contains(word)) {
               termDictionary.add(word);
               docList = new ArrayList<Doc>();
               Doc doc = new Doc(i,j);
               docList.add(doc);
               docLists.add(docList);
            }
            else {
               int index = termDictionary.indexOf(word);
               docList = docLists.get(index);
               int k=0;
               for(Doc did:docList) {
                  if(did.docId == i) {
                     did.insertPosition(j);
                     docList.set(k, did);
                     match = true;
                     break;
                  }
                  k++;
               }
               if(!match) {
                  Doc doc = new Doc(i,j);
                  docList.add(doc);
               }
            }
         }
      }
	}
	
	/**
	 * Return the string representation of a positional index
	 */
	public String toString()
	{
		String matrixString = new String();
		ArrayList<Doc> docList;
		for(int i=0;i<termDictionary.size();i++){
				matrixString += String.format("%-15s", termDictionary.get(i));
				docList = docLists.get(i);
				for(int j=0;j<docList.size();j++)
				{
					matrixString += docList.get(j)+ "\t";
				}
				matrixString += "\n";
			}
		return matrixString;
	}
	
	/**
	 * Performs the intersection of document lists.
	 * @param post1 first postings
	 * @param post2 second postings
	 * @return merged result of two postings
	 */
	public ArrayList<Integer> intersect(ArrayList<Doc> post1, ArrayList<Doc> post2)
	{
		//TASK2: TO BE COMPLETED
      ArrayList<Integer> intersectList = new ArrayList<Integer>();
      
      ArrayList<Doc> qAL1 = post1;
      ArrayList<Doc> qAL2 = post2;
      
      int pAL1=0,pAL2=0;
      
      while(pAL1 < qAL1.size() && pAL2 < qAL2.size()) {
         if(qAL1.get(pAL1).docId == qAL2.get(pAL2).docId) {
            ArrayList<Integer> posAL1 = qAL1.get(pAL1).positionList;
            ArrayList<Integer> posAL2 = qAL2.get(pAL2).positionList;
            
            int pposAL1=0, pposAL2=0;
            
            while(pposAL1 < posAL1.size()) {
               while(pposAL2 < posAL2.size()) {
                  if(posAL1.get(pposAL1) - posAL2.get(pposAL2) == -1) {
                     intersectList.add(qAL1.get(pAL1).docId);
                     break;
                  }
                  pposAL2++;
               }
               pposAL1++;
            }
            pAL1++;
            pAL2++;
         }
         else if(qAL1.get(pAL1).docId < qAL2.get(pAL2).docId) pAL1++;
         else pAL2++;
      }
      return intersectList;
	}
	
	/**
	 * 
	 * @param query a phrase query that consists of any number of terms in the sequential order
	 * @return ids of documents that contain the phrase
	 */
	public ArrayList<Integer> phraseQuery(String[] query)
	{
		//TASK3: TO BE COMPLETED
      ArrayList<Integer> answer = new ArrayList<Integer>();
      ArrayList<ArrayList<Integer>> mergedLists = new ArrayList<ArrayList<Integer>>();
      int n = query.length;
      for(int i = 0; i < n-1; i++) {
         if(termDictionary.contains(query[i]) && termDictionary.contains(query[i+1])) {
            ArrayList<Doc> post1 = docLists.get(termDictionary.indexOf(query[i]));
            ArrayList<Doc> post2 = docLists.get(termDictionary.indexOf(query[i+1]));
            ArrayList<Integer> intersectAnswer = intersect(post1, post2);
            if(intersectAnswer.size() > 0) {
               mergedLists.add(intersect(post1, post2));
            }
         }
         else {
            ArrayList<Integer> extra = new ArrayList<Integer>();
            extra.add(-1);
            mergedLists.add(extra);
         }
         
      }
      if(mergedLists.size() == 0) {}
      else if(mergedLists.size() == 1) {
         answer = mergedLists.get(0);
      }
      else if(mergedLists.size() == 2) {
         answer = merge(mergedLists.get(0), mergedLists.get(1));
      }
      else{
         answer = merge(mergedLists.get(0), mergedLists.get(1));
         for(int i = 2; i < mergedLists.size(); i++) {
            answer = merge(answer, mergedLists.get(i));
         }
      }
      return answer;
	}
   
   /**
    * Perform the merge algorithm (AND) on the 2 postings lists passed as
    * arguments.
    * 
    * @param postings1        first postings list.
    * @param postings2        second postings list.
    * @return                 returns the arraylist of the result.
    * 
    */
   public ArrayList<Integer> merge(ArrayList<Integer> postings1, ArrayList<Integer> postings2) {
      
      ArrayList<Integer> answer = new ArrayList<Integer>();      
      int pointer1 = 0, pointer2 = 0;
      try {
         do {
            if(postings1.get(pointer1) == postings2.get(pointer2)) {
               answer.add(postings1.get(pointer1));
               pointer1++;
               pointer2++;
            }
            else if(postings1.get(pointer1) < postings2.get(pointer2)) {
               pointer1++;
            }
            else {
               pointer2++;
            }
         }while((pointer1 != postings1.size()) || (pointer2 != postings2.size()));
      }
      catch(Exception ex) {
      }
      if(answer.size() == 0) {
         ArrayList<Integer> extra = new ArrayList<Integer>();
         extra.add(-1);
         return extra;
      }
      else return answer;
      
   }  // end method merge
	
   /**
    * Print the arraylist output.
    * 
    * @param result     an arraylist of the result to be printed
    * 
    */
   public void printResult(ArrayList<Integer> result) {
      if(result.contains(-1)) {
         result.remove(new Integer(-1));
      }
      System.out.println("Result: ");
      if (result.size() == 0) {
         System.out.println("No such document found.");
      }
      else {
         for(int j : result) {
            System.out.println(myDocs[j]);
         }
      }
      System.out.println();
   }  // end method printResult
   
	public static void main(String[] args)
	{
      String[] docs = {"data text warehousing over big data",
                       "dimensional data warehouse over big data",
                       "nlp before text mining",
                       "nlp before text classification",};
                       
		PositionalIndex pi = new PositionalIndex(docs);
		System.out.print(pi);
		//TASK4: TO BE COMPLETED: design and test phrase queries with 2-5 terms
      
      // Test Case 1
      System.out.println();
      String query1 = "rishi before text wadekar";
      System.out.println("Query: " + query1);
      pi.printResult(pi.phraseQuery(query1.split(" ")));
    
      // Test Case 2
      System.out.println();
      String query2 = "before text";
      System.out.println("Query: " + query2);
      pi.printResult(pi.phraseQuery(query2.split(" ")));
      
      // Test Case 3
      System.out.println();
      String query3 = "data warehouse over big data";
      System.out.println("Query: " + query3);
      pi.printResult(pi.phraseQuery(query3.split(" ")));
      
      // Test Case 4
      System.out.println();
      String query4 = "text big";
      System.out.println("Query: " + query4);
      pi.printResult(pi.phraseQuery(query4.split(" ")));
      
	}
}

/**
 * 
 * Document class that contains the document id and the position list
 */
class Doc{
	int docId;
	ArrayList<Integer> positionList;
	public Doc(int did)
	{
		docId = did;
		positionList = new ArrayList<Integer>();
	}
	public Doc(int did, int position)
	{
		docId = did;
		positionList = new ArrayList<Integer>();
		positionList.add(new Integer(position));
	}
	
	public void insertPosition(int position)
	{
		positionList.add(new Integer(position));
	}
	
	public String toString()
	{
		String docIdString = ""+docId + ":<";
		for(Integer pos:positionList)
			docIdString += pos + ",";
		docIdString = docIdString.substring(0,docIdString.length()-1) + ">";
		return docIdString;		
	}
}
