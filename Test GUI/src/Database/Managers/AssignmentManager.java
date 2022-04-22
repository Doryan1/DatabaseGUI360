package Database.Managers;
import java.sql.*;
import java.util.*;
record resCA(int classID, int studID, String assignment, int grade){};

public class AssignmentManager
{
	private final PreparedStatement insertAssignment;
	private final PreparedStatement selectAssignment;
	private final PreparedStatement selectStudentAssignments;
	private final PreparedStatement selectClassAssignments;
	private final PreparedStatement selectStudentClassAssignments;
	private final PreparedStatement updateAssignment;
	private final PreparedStatement deleteAssignment;

	public AssignmentManager(Connection connection) throws SQLException
	{
		this.insertAssignment = connection.prepareStatement("""
			INSERT INTO ASSIGNMENT (class_id, student_id, name, grade)
				VALUES (?, ?, ?, ?)""");
		this.selectAssignment = connection.prepareStatement("""
			SELECT (grade)
				FROM ASSIGNMENT
					INNER JOIN CLASS
						ON ASSIGNMENT.class_id = CLASS.id
				WHERE
					student_id = ?
					AND name = ?
					AND class_id = ?""");
		this.selectStudentAssignments = connection.prepareStatement("""
			SELECT name, grade, department, number, section, semester, year
				FROM ASSIGNMENT
					INNER JOIN CLASS
						ON ASSIGNMENT.class_id = CLASS.id
				WHERE
					student_id = ?""");
		this.selectClassAssignments = connection.prepareStatement("""
			SELECT student_id, name, grade
				FROM ASSIGNMENT
				WHERE
					class_id = ?""");
		this.selectStudentClassAssignments = connection.prepareStatement("""
			SELECT name, grade
				FROM ASSIGNMENT
				WHERE
					class_id = ?
					AND student_id = ?""");
		this.updateAssignment = connection.prepareStatement("""
			UPDATE ASSIGNMENT
				SET
					grade = ?
				WHERE
					class_id = ?
					AND student_id = ?
					AND name = ?""");
		this.deleteAssignment = connection.prepareStatement("""
			DELETE FROM ASSIGNMENT
				WHERE
					class_id = ?
					AND student_id = ?
					AND name = ?
				RETURNING grade""");
	}

	public int insertAssignment(
		int classID,
		int studentID,
		String assignment,
		int grade) throws SQLException
	{
		this.insertAssignment.setInt(1, classID);
		this.insertAssignment.setInt(2, studentID);
		this.insertAssignment.setString(3, assignment);
		this.insertAssignment.setInt(4, grade);
		return this.insertAssignment.executeUpdate();
	}

	public OptionalInt selectAssignment(
		int classID,
		int studentID,
		String assignment) throws SQLException
	{
		this.selectAssignment.setInt(1, classID);
		this.selectAssignment.setInt(2, studentID);
		this.selectAssignment.setString(3, assignment);
		try (var res = this.selectAssignment.executeQuery())
		{
			if (!res.next()) {return OptionalInt.empty();}
			return OptionalInt.of(res.getInt("grade"));
		}
	}

	public List<HashMap<String, Object>> selectStudentAssignments(int studentID) throws SQLException
	{
		this.selectStudentAssignments.setInt(1, studentID);
		var assignments = new ArrayList<HashMap<String, Object>>();
		try (var res = this.selectStudentAssignments.executeQuery())
		{
			while (res.next())
			{
				final var assignment = new HashMap<String, Object>();
				assignment.put("Student ID", studentID);
				assignment.put("Department", res.getString("department"));
				assignment.put("Course Number", res.getInt("number"));
				assignment.put("Course Section", res.getInt("section"));
				assignment.put("Semester", res.getInt("semester"));
				assignment.put("Year", res.getInt("year"));
				assignment.put("Assignment", res.getString("assignment"));
				assignment.put("Grade", res.getInt("grade"));
				assignment.put("Letter Grade", Database.Database.letterGrade(res.getInt("grade")));
				assignments.add(assignment);
			}
		}
		return assignments;
	}

	public List<HashMap<String, Object>> selectClassAssignments(int classID) throws SQLException
	{
		this.selectClassAssignments.setInt(1, classID);
		var assignments = new ArrayList<HashMap<String, Object>>();
		try (var res = this.selectClassAssignments.executeQuery())
		{
			while (res.next())
			{
				final var assignment = new HashMap<String, Object>();
				assignment.put("Student ID", res.getInt("student_id"));
				assignment.put("Assignment", res.getInt("assignment"));
				assignment.put("Grade", res.getInt("grade"));
				assignment.put("Letter Grade", Database.Database.letterGrade(res.getInt("grade")));
				assignments.add(assignment);
			}
		}
		return assignments;
	}

	public List<HashMap<String, Object>> selectStudentClassAssignments(
		int classID,
		int studentID) throws SQLException
	{
		this.selectStudentClassAssignments.setInt(1, classID);
		this.selectStudentClassAssignments.setInt(2, studentID);
		var assignments = new ArrayList<HashMap<String, Object>>();
		try (var res = this.selectStudentClassAssignments.executeQuery())
		{
			while (res.next())
			{
				final var assignment = new HashMap<String, Object>();
				assignment.put("Assignment", res.getString("assignment"));
				assignment.put("Grade", res.getInt("grade"));
				assignment.put("Letter Grade", Database.Database.letterGrade(res.getInt("grade")));
				assignments.add(assignment);
			}
		}
		return assignments;
	}

	public int updateAssignment(
		int classID,
		int studentID,
		String assignment,
		int grade) throws SQLException
	{
		this.updateAssignment.setInt(1, grade);
		this.updateAssignment.setInt(2, classID);
		this.updateAssignment.setInt(3, studentID);
		this.updateAssignment.setString(4, assignment);
		return this.updateAssignment.executeUpdate();
	}

	public int deleteAssignment(
		int classID,
		int studentID,
		String assignment) throws SQLException
	{
		this.deleteAssignment.setInt(1, classID);
		this.deleteAssignment.setInt(2, studentID);
		this.deleteAssignment.setString(3, assignment);
		return this.deleteAssignment.executeUpdate();
	}
}
