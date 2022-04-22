package Database;

public class Assignment
{
	int studentID;
	String department;
	int number;
	int section;
	int semester;
	int year;
	String assignment;
	int grade;

	public Assignment(
		int studentID,
		String department,
		int number,
		int section,
		int semester,
		int year,
		String assignment,
		int grade)
	{
		this.studentID = studentID;
		this.department = department;
		this.number = number;
		this.section = section;
		this.semester = semester;
		this.year = year;
		this.assignment = assignment;
		this.grade = grade;
	}
}
