package src;

import java.io.*;
import java.util.*;

public class Main {

    private static String CERTIFIED_STATUS = "CERTIFIED";
    private static String CSV_SPLIT_BY = ";";

    public static void main(String[] args) {

        String inputFile;
        String occupationOutputFile;
        String stateOutputFile;


        if (args.length > 0) {

            inputFile = args[0];
            occupationOutputFile = args[1];
            stateOutputFile = args[2];
        } else {
            inputFile = "input/h1b_input.csv";
//         inputFile = "input/H1B_FY_2014-1.csv";
            occupationOutputFile = "output/top_10_occupations.txt";
            stateOutputFile = "output/top_10_states.txt";

        }

        String line = "";
        Integer totalCertified = 0;

        HashMap<String, Integer> statesMap = new HashMap<>();
        HashMap<String, Integer> occupationMap = new HashMap<>();

        PriorityQueue<KeyValue> top10States = new PriorityQueue<>(new KVComparator());
        PriorityQueue<KeyValue> top10Occupations = new PriorityQueue<>(new KVComparator());


        try (BufferedReader br = new BufferedReader(new FileReader(inputFile))) {

            while ((line = br.readLine()) != null) {

                String[] splits = line.split(CSV_SPLIT_BY);

                if (CERTIFIED_STATUS.equals(splits[2])) {

                    String state = splits[50].replaceAll("”", "");
                    String occupation = splits[24].replaceAll("\"", "");

/*
                    String state = splits[29].replaceAll("”","");
                    String occupation = splits[14].replaceAll("\"","");
*/

                    totalCertified++;

                    if (statesMap.containsKey(state))
                        statesMap.put(state, statesMap.get(state) + 1);
                    else
                        statesMap.put(state, 1);

                    if (occupationMap.containsKey(occupation))
                        occupationMap.put(occupation, occupationMap.get(occupation) + 1);
                    else
                        occupationMap.put(occupation, 1);

                }
            }

            for (Map.Entry<String, Integer> entry : statesMap.entrySet()) {
//                System.out.println(entry.getKey() + "/" + entry.getValue());

                if (top10States.size() == 10) {
                    if (entry.getValue() > top10States.peek().value) {

                        top10States.poll();
                        top10States.add(new KeyValue(entry.getKey(), entry.getValue()));
                    } else if ((entry.getValue() == top10States.peek().value) && (entry.getKey().compareTo(top10States.peek().key) > 0)) {

                        top10States.poll();
                        top10States.add(new KeyValue(entry.getKey(), entry.getValue()));
                    }
                } else {
                    top10States.add(new KeyValue(entry.getKey(), entry.getValue()));
                }
            }

            for (Map.Entry<String, Integer> entry : occupationMap.entrySet()) {
//                System.out.println(entry.getKey() + "/" + entry.getValue());

                if (top10Occupations.size() == 10) {
                    if (entry.getValue() > top10Occupations.peek().value) {

                        top10Occupations.poll();
                        top10Occupations.add(new KeyValue(entry.getKey(), entry.getValue()));
                    } else if ((entry.getValue() == top10Occupations.peek().value) && (entry.getKey().compareTo(top10Occupations.peek().key) > 0)) {

                        top10Occupations.poll();
                        top10Occupations.add(new KeyValue(entry.getKey(), entry.getValue()));
                    }
                } else {
                    top10Occupations.add(new KeyValue(entry.getKey(), entry.getValue()));
                }
            }


            FileOutputStream outputStream = new FileOutputStream(stateOutputFile);
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream, "UTF-16");
            BufferedWriter bufferedWriter = new BufferedWriter(outputStreamWriter);
            bufferedWriter.write("TOP_STATES;NUMBER_CERTIFIED_APPLICATIONS;PERCENTAGE\n");

            int statesSize = top10States.size();
            for (int i = 0; i < statesSize; i++) {
                KeyValue keyValue = top10States.poll();
                bufferedWriter.write(keyValue.key + ";" + keyValue.value.toString() + ";" + String.format("%.1f", (keyValue.value * 100) / totalCertified.floatValue()) + "\n");
//                System.out.println(keyValue.key + ";" + keyValue.value.toString() + ";" + String.format("%.1f", (keyValue.value * 100) / totalCertified.floatValue()));
            }
            bufferedWriter.close();

            FileOutputStream outputStream2 = new FileOutputStream(occupationOutputFile);
            OutputStreamWriter outputStreamWriter2 = new OutputStreamWriter(outputStream2, "UTF-16");
            BufferedWriter bufferedWriter2 = new BufferedWriter(outputStreamWriter2);
            bufferedWriter2.write("TOP_OCCUPATIONS;NUMBER_CERTIFIED_APPLICATIONS;PERCENTAGE\n");

            int occSize = top10Occupations.size();
            for (int i = 0; i < occSize; i++) {
                KeyValue keyValue = top10Occupations.poll();
                bufferedWriter2.write(keyValue.key + ";" + keyValue.value.toString() + ";" + String.format("%.1f", (keyValue.value * 100) / totalCertified.floatValue()) + "\n");
//                System.out.println(keyValue.key + ";" + keyValue.value.toString() + ";" + String.format("%.1f", (keyValue.value * 100) / totalCertified.floatValue()));
            }
            bufferedWriter2.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private static class KeyValue {

        String key;
        Integer value;

        public KeyValue(String key, Integer value) {
            this.key = key;
            this.value = value;
        }
    }

    private static class KVComparator implements Comparator<KeyValue> {

        public int compare(KeyValue o1, KeyValue o2) {
            if (o2.value < o1.value)
                return -1;
            else if (o2.value > o1.value)
                return 1;
            else {
                if (o2.key.compareTo(o1.key) < 0)
                    return 1;
                else if (o2.key.compareTo(o1.key) > 0)
                    return -1;
            }
            return 0;
        }
    }
}
