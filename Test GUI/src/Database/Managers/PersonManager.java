package Database.Managers;
import java.sql.*;
import java.util.*;

import Database.Person;

public class PersonManager
{
	private final PreparedStatement insertPerson;
	private final PreparedStatement selectPerson;
	private final PreparedStatement updatePerson;
	private final PreparedStatement deletePerson;

	public PersonManager(Connection connection) throws SQLException
	{
		this.insertPerson = connection.prepareStatement("""
			INSERT INTO PERSON (id, first_name, last_name, birth_date)
				VALUES (?, ?, ?, ?)""");
		this.selectPerson = connection.prepareStatement("""
				SELECT first_name, last_name, birth_date
					FROM PERSON
					WHERE id = ?""");
		this.updatePerson = connection.prepareStatement("""
			UPDATE PERSON
				SET
					first_name = ?,
					last_name = ?,
					birth_date = ?
				WHERE id = ?""");
		this.deletePerson = connection.prepareStatement("""
			DELETE FROM PERSON
				WHERE id = ?""");
	}

	public int insertPerson(int id, String fName, String lName, String bDate) throws SQLException
	{
		this.insertPerson.setInt(1, id);
		this.insertPerson.setString(2, fName);
		this.insertPerson.setString(3, lName);
		this.insertPerson.setString(4, bDate);
		return this.insertPerson.executeUpdate();
	}

	public Optional<Person> selectPerson(int id) throws SQLException
	{
		this.selectPerson.setInt(1, id);
		try (var res = this.selectPerson.executeQuery()) {
			if (!res.next()) {
				return Optional.empty();
			}
			return Optional.of(new Person(
				id,
				res.getString("first_name"),
				res.getString("last_name"),
				res.getString("birth_date")));
		}
	}

	public int updatePerson(int id, String fName, String lName, String bDate) throws SQLException
	{
		this.updatePerson.setString(1, fName);
		this.updatePerson.setString(2, lName);
		this.updatePerson.setString(3, bDate);
		this.updatePerson.setInt(4, id);
		return this.selectPerson.executeUpdate();
	}

	public int deletePerson(int id) throws SQLException
	{
		this.deletePerson.setInt(1, id);
		return this.deletePerson.executeUpdate();
	}
}