package Database.Managers;
import java.sql.*;
import java.util.*;

public class ClassProfessorManager
{
	private final PreparedStatement insertClassProfessor;
	private final PreparedStatement selectClassProfessor;
	private final PreparedStatement updateClassProfessor;
	private final PreparedStatement deleteClassProfessor;
	private final PreparedStatement selectProfessorClasses;
	private final PreparedStatement selectClassProfessors;

	public ClassProfessorManager(Connection connection) throws SQLException
	{
		this.insertClassProfessor = connection.prepareStatement("""
			INSERT OR IGNORE INTO CLASS_PROFESSOR (professor_id, class_id, active)
			VALUES (?, ?, ?);""");
		this.selectClassProfessor = connection.prepareStatement("""
			SELECT active
				FROM CLASS_PROFESSOR
				WHERE
					professor_id = ?
					AND class_id = ?;""");
		this.updateClassProfessor = connection.prepareStatement("""
			UPDATE CLASS_PROFESSOR
				SET
					active = ?
				WHERE
					professor_id = ?
					AND class_id = ?;""");
		this.deleteClassProfessor = connection.prepareStatement("""
			DELETE FROM CLASS_PROFESSOR
				WHERE
					professor_id = ?
					AND class_id = ?;""");
		this.selectProfessorClasses = connection.prepareStatement("""
			SELECT active, department, number, section, semester, year
				FROM CLASS_PROFESSOR
					INNER JOIN CLASS
						ON CLASS_PROFESSOR.class_id = CLASS.id
				WHERE
					professor_id = ?;""");
		this.selectClassProfessors = connection.prepareStatement("""
			SELECT professor_id
				FROM CLASS_PROFESSOR
				WHERE
					class_id = ?;""");
	}

	public int insertClassProfessor(
		int profID,
		int classID,
		boolean active) throws SQLException
	{
		this.insertClassProfessor.setInt(1, profID);
		this.insertClassProfessor.setInt(2, classID);
		this.insertClassProfessor.setInt(3, active? 1 : 0);
		return this.insertClassProfessor.executeUpdate();
	}

	public Optional<Boolean> selectClassProfessor(
		int profID,
		int classID) throws SQLException
	{
		this.selectClassProfessor.setInt(1, profID);
		this.selectClassProfessor.setInt(2, classID);
		try (var res = this.selectClassProfessor.executeQuery())
		{
			if (!res.next()) {return Optional.empty();}
			return Optional.of(res.getInt("active") == 1);
		}
	}

	public int updateClassProfessor(
		int profID,
		int classID,
		boolean active) throws SQLException
	{
		this.updateClassProfessor.setInt(1, active? 1:0);
		this.updateClassProfessor.setInt(2, profID);
		this.updateClassProfessor.setInt(3, classID);
		return this.updateClassProfessor.executeUpdate();
	}

	public int deleteClassProfessor(int profID, int classID) throws SQLException
	{
		this.deleteClassProfessor.setInt(1, profID);
		this.deleteClassProfessor.setInt(2, classID);
		return this.deleteClassProfessor.executeUpdate();
	}

	public List<HashMap<String, Object>> selectProfessorClasses(int profID) throws SQLException
	{
		this.selectProfessorClasses.setInt(1, profID);
		var classes = new ArrayList<HashMap<String, Object>>();
		try (var res = this.selectProfessorClasses.executeQuery())
		{
			while (res.next())
			{
				final var klass = new HashMap<String, Object>();
				klass.put("Actively Teaching", res.getInt("active") == 1);
				klass.put("Department", res.getString("department"));
				klass.put("Course Number", res.getInt("number"));
				klass.put("Course Section", res.getInt("section"));
				klass.put("Semester", res.getInt("semester"));
				klass.put("Year", res.getInt("year"));
				classes.add(klass);
			}
		}
		return classes;
	}

	public List<Integer> selectClassProfessors(int classID) throws SQLException
	{
		this.selectClassProfessors.setInt(1, classID);
		var tas = new ArrayList<Integer>();
		try (var res = this.selectClassProfessors.executeQuery())
		{
			while (res.next())
			{
				tas.add(res.getInt("professor_id"));
			}
		}
		return tas;
	}
}
