package csen1002.main.task2;

import java.security.AlgorithmConstraints;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Stack;

/**
 * Write your info here
 * 
 * @name Mostafa Mohamed Abdelnasser
 * @id 43-8530
 * @labNumber 11
 */
public class NFA{
	/**
	 * NFA constructor
	 * 
	 * @param description is the string describing a NFA
	 */
	ArrayList<HashSet> epsilonTable = new ArrayList<>();
	ArrayList<HashSet> zeroStateTable = new ArrayList<>();
	ArrayList<HashSet> oneStateTable = new ArrayList<>();
	String[] accStates;
	int numberOfStates = 50;
	public NFA(String description) {
		//Parsing the input string
		String[] descArray = description.split("#");
		String[] zeroTrans = descArray[0].split(";");
		String[] oneTrans = descArray[1].split(";");
		String[] epsTrans = descArray[2].split(";");
		accStates = descArray[3].split(",");

		//Initialize Tables
		for(int i = 0;i < numberOfStates;i++)
			epsilonTable.add(new HashSet());

		for(int i = 0;i < numberOfStates;i++)
			zeroStateTable.add(new HashSet());

		for(int i = 0;i < numberOfStates;i++)
			oneStateTable.add(new HashSet());


		//Epsilon Closures
		for(String transition : epsTrans){
			String[] transSplit = transition.split(",");
			int from = Integer.parseInt(transSplit[0]);
			int to = Integer.parseInt(transSplit[1]);
			epsilonTable.get(from).add(to);
		}
		//Zero Transitions
		for(String transition : zeroTrans){
			String[] transSplit = transition.split(",");
			int from = Integer.parseInt(transSplit[0]);
			int to = Integer.parseInt(transSplit[1]);
			zeroStateTable.get(from).add(to);
		}

		//One Transitions
		for(String transition : oneTrans){
			String[] transSplit = transition.split(",");
			int from = Integer.parseInt(transSplit[0]);
			int to = Integer.parseInt(transSplit[1]);
			oneStateTable.get(from).add(to);
		}
	}

	/**
	 * Returns true if the string is accepted by the NFA and false otherwise.
	 * 
	 * @param input is the string to check by the NFA.
	 * @return if the string is accepted or not.
	 */
	public boolean run(String input) {

		//Parse input
		char[] inputArr = input.toCharArray();

		//Initialize hashset and stack to be used
		HashSet<Integer> currStates = new HashSet<>();
		Stack<Integer> stateStack = new Stack<>();

		//Add the initial state, default = 0
		stateStack.push(0);

		//Loop begins
		for(char c: inputArr){
			int inputDigit = Integer.parseInt(c+"");

			//Add epsilon closure of the current states hashset which contains all the sets we're in
			while(!stateStack.isEmpty()){
				int currState = stateStack.pop();
				currStates.add(currState);
				HashSet<Integer> currEps = (HashSet<Integer>) epsilonTable.get(currState).clone();
				for(int state : currEps){
					if(!stateStack.contains(state) && !currStates.contains(state))
						stateStack.push(state);
				}
			}

			//Case the input is zero, we'll look into the previously
			//parsed zeroStateTable for the zero transitions
			if(inputDigit == 0)
				for(int state : currStates){
					HashSet<Integer> stateTransitions = (HashSet<Integer>) zeroStateTable.get(state).clone();
					for(int toState : stateTransitions){
						if(!stateStack.contains(toState))
							stateStack.push(toState);
					}
				}
			//Case it's a one
			else
				for(int state : currStates) {
					HashSet<Integer> stateTransitions = (HashSet<Integer>) oneStateTable.get(state).clone();
					for (int toState : stateTransitions) {
						if (!stateStack.contains(toState))
							stateStack.push(toState);
					}
				}

			//Add epsilon closures in case new states are added
			currStates.clear();
			while(!stateStack.isEmpty()){
				int currState = stateStack.pop();
				currStates.add(currState);
				HashSet<Integer> currEps = (HashSet<Integer>) epsilonTable.get(currState).clone();
				for(int state : currEps){
					if(!stateStack.contains(state) && !currStates.contains(state))
						stateStack.push(state);
				}
			}
		}

		//Check if any of the states we're in is an accept state
		for(String accState : accStates) {
			if (currStates.contains(Integer.parseInt(accState)))
				return true;
		}
		return false;
	}
}
