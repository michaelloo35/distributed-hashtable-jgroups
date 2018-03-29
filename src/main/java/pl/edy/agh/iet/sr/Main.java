package pl.edy.agh.iet.sr;

import pl.edy.agh.iet.sr.hashtable.DistributedMap;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Main {

    private static final int KEY_INDEX = 1;
    private static final int VALUE_INDEX = 2;

    public static void main(String[] args) throws IOException {
        System.setProperty("java.net.preferIPv4Stack", "true");

        String channel = "operation";
        DistributedMap distributedMap = new DistributedMap(channel);


        InputStreamReader isr = new InputStreamReader(System.in);
        BufferedReader br = new BufferedReader(isr);
        String[] input = {""};

        while (!input[0].equals("close")) {
            input = br.readLine().split("\\s");

            switch (input[0]) {
                case "put":
                    distributedMap.put(input[KEY_INDEX], input[VALUE_INDEX]);
                    break;

                case "remove":
                    distributedMap.remove(input[KEY_INDEX]);
                    break;

                case "get":
                    System.out.println(distributedMap.get(input[KEY_INDEX]));
                    break;

                case "contains":
                    System.out.println(distributedMap.containsKey(input[KEY_INDEX]));
                    break;

                case "state":
                    System.out.println(distributedMap.getState());
                    break;
                default:
                    System.out.println("Unrecognized operation");
            }
        }
    }
}