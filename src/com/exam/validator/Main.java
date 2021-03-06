package com.exam.validator;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *
 *
 * Students were taking an exam which was the a multichoice test. We know the location in auditorium where each student was sitting and their answers.
 * Student's sitting location is in format x.y, where x is the row and the y is the sitting place in the row. Rows are numbered from front to back.
 * Each student had a chance to cheat and write answers down from:
 *
 * a) his neighbours in the same row,
 * b) the 3 guys sitting in the row in front of him
 *
 * e.g. auditorium could look like that
 *     (.....back........)
 *     (x,x,x,x,x,x,x,x,x)
 *     (x,x,x,x,x,y,s,y,x)
 *     (x,x,x,x,x,y,y,y,x)
 *     (....front........), where s is the student, and y are his neighbours
 *
 * The task is to identify cheating students in the class.
 *
 * If you find that you are missing requirements, take your own judgment.
 *
 * Data could be found in results.csv file, but CSV parsing is already done for you and results are mapped to a
 * Student objects list.
 *
 *
 */
public class Main {

    private Map<Integer, String> correctAnswers = new HashMap<>();

    {
        correctAnswers.put(1, "a");
        correctAnswers.put(2, "bd");
        correctAnswers.put(3, "abef");
        correctAnswers.put(4, "f");
        correctAnswers.put(5, "f");
        correctAnswers.put(6, "d");
        correctAnswers.put(7, "abe");
        correctAnswers.put(8, "abcde");
        correctAnswers.put(9, "abe");
        correctAnswers.put(10, "abd");
        correctAnswers.put(11, "b");
        correctAnswers.put(12, "af");
        correctAnswers.put(13, "ce");
        correctAnswers.put(14, "be");
        correctAnswers.put(15, "bdf");
        correctAnswers.put(16, "a");
    }

    public static void main(String[] args) {
        List<Student> students = CSVReader.parse();
        Set<Student> possibleCheaters = new HashSet<>();
        
//        For testing purposes
//        Map<Integer, String> answers = new HashMap<>(students.get(22).getAnswers());
//        
//        students.get(9).setAnswers(answers);
//        students.get(23).setAnswers(answers);
//        students.get(8).setAnswers(answers);
//        
//        Map<Integer, String> answersB = new HashMap<>(students.get(0).getAnswers());
//        
//        students.get(1).setAnswers(answersB);
//        students.get(9).setAnswers(answersB);
        
        long timeStart = System.currentTimeMillis();

        students.forEach(student -> possibleCheaters.addAll(checkNeighbors(students, student)));
        
        if (possibleCheaters.size() == 0) System.out.println("No possible cheaters found");
        else {
        	System.out.println("List of possible cheaters\n");
        	possibleCheaters.forEach(student -> System.out.println(student + "\n"));
        }
        
        long timeStop = System.currentTimeMillis();
        
        System.out.println("Time taken: " + (timeStop - timeStart) + " ms");
       
    }
    
  
    
//    Returns position in integer array[x,y] 
    
    public static int[] locToInt (String location) {
    	
    	String[] xy = location.split("\\."); 
    	    	 	
    	return new int[] {Integer.parseInt(xy[0]),Integer.parseInt(xy[1])};
    }
    
//  Returns position in string
    
	public static String locToStr(int row, int seat) {
		
		return Integer.toString(row) + "." + Integer.toString(seat);
	}	
	
//	Returns particular student in sitting location
	
    public static Student getStudent(List<Student> students, String position) {
    	
    	for (Student student : students) {
    		if (student.getSittingLocation().equals(position)) return student;
    	}
    	return null; 
    }
    
    public static boolean sitsInSameRow (Student studentA, Student studentB) {
    		
    	return (locToInt(studentA.getSittingLocation())[0] == locToInt(studentB.getSittingLocation())[0]); 
    }
    
    public static Set<Student> checkNeighbors(List<Student> students, Student student){
    	
    	Set<Student> possibleCheaters = new HashSet<>();
    	int[] loc = locToInt(student.getSittingLocation());
    	
    	List<Student> neighbors = new ArrayList<>();
    	    	
    	neighbors.add(getStudent(students, locToStr(loc[0], loc[1]-1))); // adding neighbor to the left
    	neighbors.add(getStudent(students, locToStr(loc[0]-1, loc[1]-1))); // adding neighbor to the below left
    	neighbors.add(getStudent(students, locToStr(loc[0]-1, loc[1]))); // adding neighbor directly below
    	neighbors.add(getStudent(students, locToStr(loc[0]-1, loc[1]+1))); // adding neighbor to the below right
    	neighbors.add(getStudent(students, locToStr(loc[0], loc[1]+1))); // adding neighbor to the right
    	
    	neighbors.forEach(neighboringStud -> { 
    		if (neighboringStud != null && student.getAnswers().equals(neighboringStud.getAnswers())) {
    			possibleCheaters.add(student);
    			if (sitsInSameRow(student, neighboringStud))
    				possibleCheaters.add(neighboringStud);
    		}
    	});
    	    	    			
    	return possibleCheaters;
    }
        
}

class Student {

    private String name;
    private String sittingLocation;
    private Map<Integer, String> answers = new HashMap<Integer, String>();

    public String getName() {
        return name;
    }

    public Student setName(String name) {
        this.name = name;
        return this;
    }

    public String getSittingLocation() {
        return sittingLocation;
    }

    public Student setSittingLocation(String sittingLocation) {
        this.sittingLocation = sittingLocation;
        return this;
    }

    public Map<Integer, String> getAnswers() {
        return answers;
    }

    public Student setAnswers(Map<Integer, String> answers) {
        this.answers = answers;
        return this;
    }

	@Override
	public String toString() {
		return "Student: " + name + "\nSitting Location: " + sittingLocation + "\nAnswers\n" + answers;
	}

}

class CSVReader {

    public static List<Student> parse() {

        String csvFile = "results.csv";
        String line = "";
        String cvsSplitBy = ",";

        List<Student> students = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {

            while ((line = br.readLine()) != null) {

                String[] studentResult = line.split(cvsSplitBy);
                Student student = new Student()
                        .setName(studentResult[0])
                        .setSittingLocation(studentResult[1])
                        .setAnswers(parseAnswers(studentResult));

                students.add(student);
            }
            return students;

        } catch (IOException e) {
            throw new RuntimeException("Error while parsing", e);
        }

    }

    private static Map<Integer, String> parseAnswers(String[] studentResult) {
        Map<Integer, String> answers = new HashMap<>();

        for (int i = 2; i<studentResult.length; i++)
            answers.put(i-1, studentResult[i]);

        return answers;
    }

}
