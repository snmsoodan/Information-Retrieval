package mallet;

import cc.mallet.util.*;
import cc.mallet.types.*;
import cc.mallet.pipe.*;
import cc.mallet.pipe.iterator.*;
import cc.mallet.topics.*;

import java.util.*;
import java.util.Map.Entry;
import java.util.regex.*;

import org.apache.derby.tools.sysinfo;

import java.io.*;

public class TopicModel2 {
	
	public static boolean ASC = true;
    public static boolean DESC = false;

	public static void main(String[] args) throws Exception {

		File fl=new File("C:/Users/Snm/Desktop/HW8docs");
		File[] filesList=fl.listFiles();
		
		
//		PrintWriter writer = new PrintWriter("C:/Users/Snm/Desktop/HW8/part2/output.txt");
		InstanceList instances;

		for(int i=0;i<filesList.length;i++){
			File newFile= new File(filesList[i].getPath());
			String filename=filesList[i].getName();
			String path=newFile.toString();

//			PrintWriter writer = new PrintWriter("C:/Users/Snm/Desktop/HW8/part2/"+filename);

//			writer.println(filename);

			// Begin by importing documents from text to feature sequences
			ArrayList<Pipe> pipeList = new ArrayList<Pipe>();

			// Pipes: lowercase, tokenize, remove stopwords, map to features
			pipeList.add( new CharSequenceLowercase() );
			pipeList.add( new CharSequence2TokenSequence(Pattern.compile("\\p{L}[\\p{L}\\p{P}]+\\p{L}")) );
			pipeList.add( new TokenSequenceRemoveStopwords(new File("C:/Users/Snm/Downloads/mallet-2.0.8/stoplists/en.txt"), "UTF-8", false, false, false) );
			pipeList.add( new TokenSequence2FeatureSequence() );

			 instances = new InstanceList (new SerialPipes(pipeList));

		

			Reader fileReader = new InputStreamReader(new FileInputStream(new File(path)), "UTF-8");
			instances.addThruPipe(new CsvIterator (fileReader, Pattern.compile("^(\\S*)[\\s,]*(\\S*)[\\s,]*(.*)$"),
					3, 2, 1)); // data, label, name fields
		}

			// Create a model with 10 topics, alpha_t = 0.01, beta_w = 0.01
			//  Note that the first parameter is passed as the sum over topics, while
			//  the second is the parameter for a single dimension of the Dirichlet prior.
			int numTopics = 20;
			ParallelTopicModel model = new ParallelTopicModel(numTopics, 1.0, 0.01);

			model.addInstances(instances);

			// Use two parallel samplers, which each look at one half the corpus and combine
			//  statistics after every iteration.
			model.setNumThreads(2);

			// Run the model for 50 iterations and stop (this is for testing only, 
			//  for real applications, use 1000 to 2000 iterations)
			model.setNumIterations(50);
			model.estimate();

			// Show the words and topics in the first instance

			// The data alphabet maps word IDs to strings
			Alphabet dataAlphabet = instances.getDataAlphabet();

			FeatureSequence tokens = (FeatureSequence) model.getData().get(0).instance.getData();
			LabelSequence topics = model.getData().get(0).topicSequence;

			Formatter out = new Formatter(new StringBuilder(), Locale.US);
			for (int position = 0; position < tokens.getLength(); position++) {
				out.format("%s-%d ", dataAlphabet.lookupObject(tokens.getIndexAtPosition(position)), topics.getIndexAtPosition(position));
			}
//			System.out.println(out);

			// Estimate the topic distribution of the first instance, 
			//  given the current Gibbs state.
			double[] topicDistribution = model.getTopicProbabilities(0);

			// Get an array of sorted sets of word ID/count pairs
			ArrayList<TreeSet<IDSorter>> topicSortedWords = model.getSortedWords();


			// Show top 30 words in topics with proportions for the first document
			for (int topic = 0; topic < numTopics; topic++) {
				Iterator<IDSorter> iterator = topicSortedWords.get(topic).iterator();

				out = new Formatter(new StringBuilder(), Locale.US);
				out.format("%d\t%.3f\t", topic, topicDistribution[topic]);
				writer.print(topic+" ");
				int rank = 0;
				while (iterator.hasNext() && rank < 30) {
					IDSorter idCountPair = iterator.next();
					//                out.format("%s (%.0f) ", dataAlphabet.lookupObject(idCountPair.getID()), idCountPair.getWeight());
					out.format("%s ", dataAlphabet.lookupObject(idCountPair.getID()));
					writer.print(" "+dataAlphabet.lookupObject(idCountPair.getID()));
					
					rank++;
				}
				writer.print("\n");
//				System.out.println(out);
			}

			// Create a new instance with high probability of topic 0
			StringBuilder topicZeroText = new StringBuilder();
			Iterator<IDSorter> iterator = topicSortedWords.get(0).iterator();

			int rank = 0;
			while (iterator.hasNext() && rank < 30) {
				IDSorter idCountPair = iterator.next();
				topicZeroText.append(dataAlphabet.lookupObject(idCountPair.getID()) + " ");
				rank++;
			}

			// Create a new instance named "test instance" with empty target and source fields.
			InstanceList testing = new InstanceList(instances.getPipe());
			testing.addThruPipe(new Instance(topicZeroText.toString(), null, "test instance", null));

			TopicInferencer inferencer = model.getInferencer();
			double[] testProbabilities = inferencer.getSampledDistribution(testing.get(0), 10, 1, 5);
//			System.out.println("0\t" + testProbabilities[0]);
			for(int k=0;k<testProbabilities.length;k++)
			{
				System.out.println(testProbabilities[k]);
			}
			
			Map<String, Double> unsortMap = new HashMap<String, Double>();
			for(int i1=0;i1<testProbabilities.length;i1++)
			{
				unsortMap.put(i1+"",testProbabilities[i1]);
			}
			
			// sorting in descending order
			Map<String, Double> sortedMapDesc = sortByComparator(unsortMap, DESC);
//			writer.println();
			writer.print(filename);
			for (Entry<String, Double> entry : sortedMapDesc.entrySet())
	        {           
	            String key=entry.getKey();
	            double val=entry.getValue();
	            writer.print(" "+key+":"+val);
	        }
			
//			writer.println();
			writer.close();
//			break;
//		}// for loop ends
		
//		writer.flush();
	}// main ends
	
	
	private static Map<String, Double> sortByComparator(Map<String, Double> unsortMap, final boolean order)
    {

        List<Entry<String, Double>> list = new LinkedList<Entry<String, Double>>(unsortMap.entrySet());

        // Sorting the list based on values
        Collections.sort(list, new Comparator<Entry<String, Double>>()
        {
            public int compare(Entry<String, Double> o1,
                    Entry<String, Double> o2)
            {
                if (order)
                {
                    return o1.getValue().compareTo(o2.getValue());
                }
                else
                {
                    return o2.getValue().compareTo(o1.getValue());

                }
            }
        });

        // Maintaining insertion order with the help of LinkedList
        Map<String, Double> sortedMap = new LinkedHashMap<String, Double>();
        for (Entry<String, Double> entry : list)
        {
            sortedMap.put(entry.getKey(), entry.getValue());
        }

        return sortedMap;
    }

}
