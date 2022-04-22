package Database.Managers;
import java.sql.*;
import java.util.*;

import Database.Student;

public class StudentManager
{
	private final PreparedStatement insertStudent;
	private final PreparedStatement selectStudent;
	private final PreparedStatement selectStudents;
	private final PreparedStatement deleteStudent;

	public StudentManager(Connection connection) throws SQLException
	{
		this.insertStudent = connection.prepareStatement("""
			INSERT INTO STUDENT (id)
				VALUES (?)""");
		this.selectStudent = connection.prepareStatement("""
			SELECT first_name, last_name, birth_date
				FROM STUDENT
					INNER JOIN PERSON
						ON STUDENT.id = PERSON.id
				WHERE STUDENT.id = ?""");
		this.selectStudents = connection.prepareStatement("""
			SELECT STUDENT.id, first_name, last_name, birth_date
				FROM STUDENT
					INNER JOIN PERSON
						ON STUDENT.id = PERSON.id""");
		this.deleteStudent = connection.prepareStatement("""
			DELETE FROM STUDENT
				WHERE id = ?""");
	}

	public int insertStudent(int id) throws SQLException
	{
		this.insertStudent.setInt(1, id);
		return this.insertStudent.executeUpdate();
	}

	public Optional<Student> selectStudent(int id) throws SQLException
	{
		this.selectStudent.setInt(1, id);
		try (var res = this.selectStudent.executeQuery())
		{
			if (!res.next()) {return Optional.empty();}
			return Optional.of(new Student(
				id,
				res.getString("first_name"),
				res.getString("last_name"),
				res.getString("birth_date")));
		}
	}

	public List<HashMap<String, Object>> selectStudents() throws SQLException
	{
		var students = new ArrayList<HashMap<String, Object>>();
		try (var res = this.selectStudents.executeQuery())
		{
			while (res.next())
			{
				final var student = new HashMap<String, Object>();
				student.put("Student ID", res.getInt("id"));
				student.put("First Name", res.getString("first_name"));
				student.put("Last Name", res.getString("last_name"));
				student.put("Birth Date", res.getString("birth_date"));
				students.add(student);
			}
		}
		return students;
	}

	public int deleteStudent(int id) throws SQLException
	{
		this.deleteStudent.setInt(1, id);
		return this.deleteStudent.executeUpdate();
	}
}