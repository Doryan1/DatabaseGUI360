package Database.Managers;
import java.sql.*;
import java.util.*;

import Database.Professor;

public class ProfessorManager
{
	private final PreparedStatement insertProfessor;
	private final PreparedStatement selectProfessor;
	private final PreparedStatement selectProfessors;
	private final PreparedStatement deleteProfessor;
	
	public ProfessorManager(Connection connection) throws SQLException
	{
		this.insertProfessor = connection.prepareStatement("""
			INSERT INTO PROFESSOR (id)
				VALUES (?)""");
		this.selectProfessor = connection.prepareStatement("""
			SELECT first_name, last_name, birth_date, department
				FROM PROFESSOR
					INNER JOIN EMPLOYEE
						ON PROFESSOR.id = EMPLOYEE.person_id
					INNER JOIN PERSON
						ON EMPLOYEE.person_id = PERSON.id
				WHERE PROFESSOR.id = ?""");
		this.selectProfessors = connection.prepareStatement("""
			SELECT PROFESSOR.id, first_name, last_name, birth_date, department
				FROM PROFESSOR
					INNER JOIN EMPLOYEE
						ON PROFESSOR.id = EMPLOYEE.person_id
					INNER JOIN PERSON
						ON EMPLOYEE.person_id = PERSON.id""");
		
		this.deleteProfessor = connection.prepareStatement("""
			DELETE FROM PROFESSOR
				WHERE id = ?""");
	}

	public int insertProfessor(int id) throws SQLException
	{
		this.insertProfessor.setInt(1, id);
		return this.insertProfessor.executeUpdate();
	}

	public Optional<Professor> selectProfessor(int id) throws SQLException
	{
		this.selectProfessor.setInt(1, id);
		try (var res = this.selectProfessor.executeQuery())
		{
			if (!res.next()) {return Optional.empty();}
			return Optional.of(new Professor(
				id,
				res.getString("first_name"),
				res.getString("last_name"),
				res.getString("birth_date"),
				res.getString("department")));
		}
	}

	public List<HashMap<String, Object>> selectProfessors() throws SQLException
	{
		var professors = new ArrayList<HashMap<String, Object>>();
		try (var res = this.selectProfessors.executeQuery())
		{
			while (res.next())
			{
				final var professor = new HashMap<String, Object>();
				professor.put("Professor ID", res.getInt("id"));
				professor.put("First Name", res.getString("first_name"));
				professor.put("Last Name", res.getString("last_name"));
				professor.put("Birth Date", res.getString("birth_date"));
				professor.put("Department", res.getString("department"));
				professors.add(professor);
			}
		}
		return professors;
	}

	public int deleteProfessor(int id) throws SQLException
	{
		this.deleteProfessor.setInt(1, id);
		return this.deleteProfessor.executeUpdate();
	}
}
