package com.cs407.socially;

// TODO: CHANGE ARRAYLIST TYPES TO WORK WITH PARTICIPANTS INSTEAD OF INTEGER

import java.util.ArrayList;

/**
 * A class used to pair each participant with another participant in each round of the networking
 * event. Pairs are not repeated between rounds, and each participant is in at most one pair per
 * round.
 */
public class PairingSystem {

    /**
     * The main method. Used to create an ArrayList of people, pass it to the generatePairs() method,
     * and print out all of the pairs.
     */
    public static void main(String[] args) {
        ArrayList<Integer> people = new ArrayList<Integer>();
        people.add(1);
        people.add(2);
        people.add(3);
        people.add(4);
        people.add(5);
        people.add(6);
        people.add(7);
        people.add(8);

        for (int x = 0; x < people.size() - 1; x++) {
            ArrayList<Integer>[] returned = generatePairs(people);

            ArrayList<Integer> pairs = returned[0];
            people = returned[1];

            System.out.print("Round " + (x+1) + ": ");

            for (int i = 0; i < pairs.size(); i++) {
                if (i % 2 == 0) {
                    System.out.print("[" + pairs.get(i) + ", ");
                }
                if (i % 2 != 0) {
                    System.out.print(pairs.get(i) + "] ");
                }
            }
            System.out.println("");
        }
    }

    /**
     * A method that generates unique pairs of participants. This method also adds a 'dummy' item
     * to the end of the passed list if it contains an odd number of people.
     *
     * @param newOrder -- an ArrayList of all participants
     * @return toReturn -- an array containing two items: an ArrayList of the generated pairs where
     *                     elements 0 and 1 are a pair, 2 and 3 are a pair, etc. and an ArrayList of
     *                     people representing the new rotated order to be passed in the next round
     */
    private static ArrayList<Integer>[] generatePairs(ArrayList<Integer> newOrder) {

        // check for even number of people
        if (newOrder.size() % 2 != 0) {
            newOrder.add(-1); // add dummy person to make even
        }

        ArrayList<Integer> pairings = new ArrayList<Integer>(); // used to return the created pairs

        int firstPtr = 0; // points to the first element in the pair
        int lastPtr = newOrder.size() - 1; // points to the last element in the pair

        for (int i = 0; i < newOrder.size() / 2; i++) {
            pairings.add(newOrder.get(firstPtr));
            pairings.add(newOrder.get(lastPtr));
            firstPtr += 1;
            lastPtr -= 1;
        }

        newOrder = rotateList(newOrder); // rotate list

        // create an array to return pairings and rotated newOrder
        ArrayList<Integer>[] toReturn = new ArrayList[2];
        toReturn[0] = pairings;
        toReturn[1] = newOrder;

        return toReturn;
    }

    /**
     * A helper method used by generatePairs(). Rotates all of the items in a list except for the
     * first one.
     *
     * @param toRotate -- the ArrayList of people to be rotated
     * @return toRotate -- the same ArrayList after it has been rotated
     */
    private static ArrayList<Integer> rotateList(ArrayList<Integer> toRotate) {
        int temp = toRotate.get(1);
        toRotate.remove(1);
        toRotate.add(temp);

        return toRotate;
    }

}

