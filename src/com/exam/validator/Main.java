package com.exam.validator;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *
 *
 * Students were taking an exam which was the a multichoice test. We know the
 * location in auditorium where each student was sitting and their answers.
 * Student's sitting location is in format x.y, where x is the row and the y is
 * the sitting place in the row. Rows are numbered from front to back. Each
 * student had a chance to cheat and write answers down from:
 *
 * a) his neighbours in the same row, b) the 3 guys sitting in the row in front
 * of him
 *
 * e.g. auditorium could look like that (.....back........) (x,x,x,x,x,x,x,x,x)
 * (x,x,x,x,x,y,s,y,x) (x,x,x,x,x,y,y,y,x) (....front........), where s is the
 * student, and y are his neighbours
 *
 * The task is to identify cheating students in the class.
 *
 * If you find that you are missing requirements, take your own judgment.
 *
 * Data could be found in results.csv file, but CSV parsing is already done for
 * you and results are mapped to a Student objects list.
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

//	Sorts students list according to their seating position (accending order 1.1, 1.2 ... 2.1, 2.2 ...
		students.sort(new Comparator<Student>() {

			@Override
			public int compare(Student o1, Student o2) {
				if (posX(o1) == posX(o2))
					if (posY(o1) < posY(o2))
						return -1;
					else if (posY(o1) > posY(o2))
						return 1;
					else
						return 0;
				else if (posX(o1) < posX(o2))
					return -1;
				else
					return 1;
			}
		});
        
//      For testing purposes
//      Map<Integer, String> answers = new HashMap<>(students.get(22).getAnswers());
//      
//      students.get(9).setAnswers(answers);
//      students.get(23).setAnswers(answers);
//      students.get(8).setAnswers(answers);
//      
//      Map<Integer, String> answersB = new HashMap<>(students.get(0).getAnswers());
//      
//      students.get(1).setAnswers(answersB);
//      students.get(9).setAnswers(answersB);
      
      long timeStart = System.currentTimeMillis();

      possibleCheaters = check(toMatrix(students));
		
      if (possibleCheaters.size() == 0) System.out.println("No possible cheaters found");
      else {
      	System.out.println("List of possible cheaters\n");
      	possibleCheaters.forEach(student -> System.out.println(student + "\n"));
      }		
      
      long timeStop = System.currentTimeMillis();
      
      System.out.println("Time taken: " + (timeStop - timeStart) + " ms");

	}

	public static Set<Student> check(ArrayList<ArrayList<Student>> students) {

		Set<Student> suspicious = new HashSet<>();

		int numberOfRows = students.size();

		for (int i = 0; i < numberOfRows; i++) {
			int lastTakenSeat = students.get(i).size() - 1;

			for (int j = 0; j <= lastTakenSeat; j++) {

				Student studentX = students.get(i).get(j);

				if (studentX != null) {

					if (j != 0) {// Checks if it's not the leftmost seat

						if (students.get(i).get(j - 1) != null && // Checks if student to the LEFT exists
								studentX.getAnswers().equals(students.get(i).get(j - 1).getAnswers())) {// if so,
																										// compares
																										// their
																										// answers
							suspicious.add(studentX);
							suspicious.add(students.get(i).get(j - 1)); // Add also student to the LEFT as she/he also
																		// has
						} // cheating possibility

						if (i > 0) { // Check if it is not the first row, if not, then check students below
							if (students.get(i - 1).get(j - 1) != null && // Checks if student to the BELOW LEFT exists
									studentX.getAnswers().equals(students.get(i - 1).get(j - 1).getAnswers())) // if so,
																											// compares
																											// their
																											// answers
								suspicious.add(studentX);

							if (students.get(i - 1).get(j) != null && // Checks if student DIRECTLY BELOW exists and, if
																		// so,
																		// compares their answers
									studentX.getAnswers().equals(students.get(i - 1).get(j).getAnswers()))
								suspicious.add(studentX);

							if (j != lastTakenSeat && students.get(i - 1).get(j + 1) != null && // Checks if student to
																								// the BELOW RIGHT
																								// exists and, if
							// so, compares their answers
									studentX.getAnswers().equals(students.get(i - 1).get(j + 1).getAnswers()))
								suspicious.add(studentX);
						}
					}

					if (j != lastTakenSeat) { // Checks if it's not the rightmost seat

						if (students.get(i).get(j + 1) != null && // Checks if student to the RIGHT exists

								studentX.getAnswers().equals(students.get(i).get(j + 1).getAnswers())) {// if so,
																										// compares
																										// their
																										// answers
							suspicious.add(studentX);
							suspicious.add(students.get(i).get(j + 1)); // Add also student to the RIGHT as she/he also
																		// has
																		// cheating possibility
						}

						if (i > 0) { // Check if it is not the first row, if not, then check students below
							if (j != 0 && students.get(i - 1).get(j - 1) != null && // Checks if student to the BELOW
																					// LEFT exists
									studentX.getAnswers().equals(students.get(i - 1).get(j - 1).getAnswers())) // if so,
																											// compares
																											// their
																											// answers
								suspicious.add(studentX);

							if (students.get(i - 1).get(j) != null && // Checks if student DIRECTLY BELOW exists and, if
																		// so,
																		// compares their answers
									studentX.getAnswers().equals(students.get(i - 1).get(j).getAnswers()))
								suspicious.add(studentX);

							if (students.get(i - 1).get(j + 1) != null && // Checks if student to the BELOW RIGHT exists
																			// and, if
							// so, compares their answers
									studentX.getAnswers().equals(students.get(i - 1).get(j + 1).getAnswers()))
								suspicious.add(studentX);
						}
					}
				}
			}
		}

		return suspicious;
	}

//    Returns seat row number in integer 

	public static int posX(Student student) {
		return Integer.parseInt(student.getSittingLocation().split("\\.")[0]);
	}

//  Returns seat position in row in integer 

	public static int posY(Student student) {
		return Integer.parseInt(student.getSittingLocation().split("\\.")[1]);
	}

//	Creating two dimensional arrayList, representing students seating in auditorium

	public static ArrayList<ArrayList<Student>> toMatrix(List<Student> students) {

		int numberOfRows = posX(students.get(students.size() - 1));

		ArrayList<ArrayList<Student>> studentsMatrix = new ArrayList<>(numberOfRows);

//   Initializing rows

		for (int i = 0; i < numberOfRows; i++) {
			studentsMatrix.add(new ArrayList<>());
		}

//    	Adding students to rows

		students.forEach(student -> {
			int x = posX(student) - 1;
			int y = posY(student) - 1;

//    		Checks if there is empty seating

			if (y > studentsMatrix.get(x).size()) {
				studentsMatrix.get(x).add(null);
				studentsMatrix.get(x).add(student);
			} else
				studentsMatrix.get(x).add(student);
		});
		return studentsMatrix;

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
		return "Student [name=" + name + ", sittingLocation=" + sittingLocation + ", answers=" + answers + "]";
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
				Student student = new Student().setName(studentResult[0]).setSittingLocation(studentResult[1])
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

		for (int i = 2; i < studentResult.length; i++)
			answers.put(i - 1, studentResult[i]);

		return answers;
	}

}
