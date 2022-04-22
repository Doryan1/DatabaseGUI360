package Database.Managers;
import java.sql.*;
import java.util.*;

public class ClassManager
{
	private final PreparedStatement insertClass;
	private final PreparedStatement selectClassID;
	private final PreparedStatement selectClasses;
	private final PreparedStatement deleteClass;

	public ClassManager(Connection connection) throws SQLException
	{
		this.insertClass = connection.prepareStatement("""
			INSERT OR IGNORE INTO CLASS (department, number, section, semester, year)
				VALUES (?, ?, ?, ?, ?);""");
		this.selectClassID = connection.prepareStatement("""
			SELECT id
				FROM CLASS
				WHERE
					department = ?
					AND number = ?
					AND section = ?
					AND semester = ?
					AND year = ?;""");
		this.selectClasses = connection.prepareStatement("""
			SELECT id, department, number, section, semester, year
				FROM CLASS;""");
		this.deleteClass = connection.prepareStatement("""
			DELETE
				FROM CLASS
				WHERE
					id = ?;""");
	}

	public int insertClass(
		String department,
		int number,
		int section,
		int semester,
		int year) throws SQLException
	{
		this.insertClass.setString(1, department);
		this.insertClass.setInt(2, number);
		this.insertClass.setInt(3, section);
		this.insertClass.setInt(4, semester);
		this.insertClass.setInt(5, year);
		return this.insertClass.executeUpdate();
	}

	public OptionalInt selectClassID(
		String department,
		int number,
		int section,
		int semester,
		int year) throws SQLException
	{
		this.selectClassID.setString(1, department);
		this.selectClassID.setInt(2, number);
		this.selectClassID.setInt(3, section);
		this.selectClassID.setInt(4, semester);
		this.selectClassID.setInt(5, year);
		try (var res = this.selectClassID.executeQuery())
		{
			if (!res.next()) {return OptionalInt.empty();}
			return OptionalInt.of(res.getInt("id"));
		}
	}

	public int selectOrInsertClassID(
		String department,
		int number,
		int section,
		int semester,
		int year) throws SQLException
		{
			var maybeID = this.selectClassID(department, number, section, semester, year);
			if (maybeID.isEmpty())
			{
				this.insertClass(department, number, section, semester, year);
				maybeID = this.selectClassID(department, number, section, semester, year);
			}
			return maybeID.getAsInt();
		}

	public List<HashMap<String, Object>> selectClasses() throws SQLException
	{
		var classes = new ArrayList<HashMap<String, Object>>();
		try (var res = this.selectClasses.executeQuery())
		{
			while (res.next())
			{
				final var klass = new HashMap<String, Object>();
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

	public int deleteClass(int id) throws SQLException
	{
		this.deleteClass.setInt(1, id);
		return this.deleteClass.executeUpdate();
	}
}
