package Database.Managers;
import java.sql.*;
import java.util.*;

import Database.TA;

public class TAManager
{
	private final PreparedStatement insertTA;
	private final PreparedStatement selectTA;
	private final PreparedStatement selectTAs;
	private final PreparedStatement deleteTA;

	public TAManager(Connection connection) throws SQLException
	{
		this.insertTA = connection.prepareStatement("""
			INSERT INTO TA (id)
				VALUES (?)""");
		this.selectTA = connection.prepareStatement("""
			SELECT first_name, last_name, birth_date, department
				FROM TA
					INNER JOIN EMPLOYEE
						ON TA.id = EMPLOYEE.person_id
					INNER JOIN PERSON
						ON EMPLOYEE.person_id = PERSON.id
				WHERE TA.id = ?""");
		this.selectTAs = connection.prepareStatement("""
			SELECT TA.id, first_name, last_name, birth_date, department
				FROM TA
					INNER JOIN EMPLOYEE
						ON TA.id = EMPLOYEE.person_id
					INNER JOIN PERSON
						ON EMPLOYEE.person_id = PERSON.id""");
		this.deleteTA = connection.prepareStatement("""
			DELETE FROM TA
				WHERE id = ?""");
	}

	public int insertTA(int id) throws SQLException
	{
		this.insertTA.setInt(1, id);
		return this.insertTA.executeUpdate();
	}

	public Optional<TA> selectTA(int id) throws SQLException
	{
		this.selectTA.setInt(1, id);
		try (var res = this.selectTA.executeQuery())
		{
			if (!res.next()) {return Optional.empty();}
			return Optional.of(new TA(
				id,
				res.getString("first_name"),
				res.getString("last_name"),
				res.getString("birth_date"),
				res.getString("department")));
		}
	}

	public List<HashMap<String, Object>> selectTAs() throws SQLException
	{
		var tas = new ArrayList<HashMap<String, Object>>();
		try (var res = this.selectTAs.executeQuery())
		{
			while (res.next())
			{
				final var ta = new HashMap<String, Object>();
				ta.put("TA ID", res.getInt("id"));
				ta.put("First Name", res.getString("first_name"));
				ta.put("Last Name", res.getString("last_name"));
				ta.put("Birth Date", res.getString("birth_date"));
				ta.put("Department", res.getString("department"));
				tas.add(ta);
			}
		}
		return tas;
	}

	public int deleteTA(int id) throws SQLException
	{
		this.deleteTA.setInt(1, id);
		return this.deleteTA.executeUpdate();
	}
}
