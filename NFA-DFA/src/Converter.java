import java.io.*;
import java.util.*;

public class Converter {
    public static void main(String[] args) throws IOException {

        int[] inputs = null;
        int counter = 0;
        String startState = null;
        HashMap<String, HashMap<Integer, HashSet<String>>> hm = new HashMap<>();

        try (BufferedReader br = new BufferedReader(new FileReader("nfa-page873.csv"))) {
            String line;
            while ((line = br.readLine()) != null) {
                if(counter == 0){
                    String[] firstLine = line.split(",");
                    inputs = new int[firstLine.length - 1];
                    for(int i = 0; i < inputs.length; i++){
                        inputs[i] = Integer.valueOf(firstLine[i + 1]);
                    }
                    counter++;
                    continue;
                }

                int index = 0;
                String tempState = null;
                int lastComma = -1;
                HashMap<Integer, HashSet<String>> tempMap = new HashMap<>();
                for(int i = 0; i < line.length(); i++){
                    if(line.charAt(i) == ','){
                        if(index == 0) {
                            tempState = line.substring(lastComma + 1, i);
                            if(counter == 1) startState = tempState;
                        }
                         else {
                            if(i - lastComma <= 1) {
                                lastComma = i;
                                index++;
                                continue;
                            }
                            String tempDest = line.substring(lastComma + 1, i);
                            HashSet<String> tempSet = new HashSet<>();
                            tempSet.add(tempDest);
                            tempMap.put(inputs[index - 1], tempSet);
                        }
                        lastComma = i;
                        index++;
                    } else if (line.charAt(i) == '{') {
                        while(line.charAt(i) != '}'){
                            i++;
                        }
                        String[] insideStrings = line.substring(lastComma + 2, i).split(",");
                        HashSet<String> tempSet = new HashSet<>();
                        for(String s: insideStrings){
                            tempSet.add(s);
                        }
                        tempMap.put(inputs[index - 1], tempSet);
                        i++;
                        if(i >= line.length()) break;
                        lastComma = i;
                        index++;
                    } else if(i == line.length() - 1){
                        if(index == 0) {
                            tempState = line.substring(lastComma + 1, i + 1);
                            if(counter == 1) startState = tempState;
                        }
                        else {
                            String tempDest = line.substring(lastComma + 1, i + 1);
                            HashSet<String> tempSet = new HashSet<>();
                            tempSet.add(tempDest);
                            tempMap.put(inputs[index - 1], tempSet);
                        }
                    }
                }
                hm.put(tempState, tempMap);
                counter++;
            }
        }

        FileWriter writer = new FileWriter("result.csv");
        writer.append("state");
        for(int i: inputs){
            writer.append(",");
            writer.append(String.valueOf(i));
        }
        writer.append("\n");


        Queue<HashSet<String>> q = new LinkedList();
        HashSet<String> start = new HashSet<>();
        HashSet<HashSet<String>> resultSet = new HashSet<>();
        start.add(startState);
        resultSet.add(start);
        q.offer(start);

        while(!q.isEmpty()) {
            HashSet<String> tempSet = q.poll();
            for (String s : tempSet) {
                writer.append(s);
            }
            writer.append(",");

            counter = 0;
            alpha: while (counter < inputs.length) {
                HashSet<String> newSet = new HashSet<>();
                beta: for (String s : tempSet) {
                    HashMap<Integer, HashSet<String>> temp = hm.get(s);
                    if(temp.containsKey(inputs[counter])) {
                        HashSet<String> hs = temp.get(inputs[counter]);
                        newSet.addAll(hs);
                    }
                }
                for (String str : newSet) {
                    writer.append(str);
                }
                writer.append(",");
                counter++;
                for(HashSet<String> hs: resultSet){
                    if(hs.equals(newSet)) continue alpha;
                }
                resultSet.add(newSet);
                q.offer(newSet);
            }
            writer.append("\n");
        }
        writer.flush();
        writer.close();
    }
}
