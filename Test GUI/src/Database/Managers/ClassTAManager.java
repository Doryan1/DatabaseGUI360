package Database.Managers;
import java.sql.*;
import java.util.*;

public class ClassTAManager
{
	private final PreparedStatement insertClassTA;
	private final PreparedStatement selectClassTA;
	private final PreparedStatement updateClassTA;
	private final PreparedStatement deleteClassTA;
	private final PreparedStatement selectTAClasses;

	public ClassTAManager(Connection connection) throws SQLException
	{
		this.insertClassTA = connection.prepareStatement("""
			INSERT OR IGNORE INTO CLASS_TA (ta_id, class_id, active)
				VALUES (?, ?, ?);""");
		this.selectClassTA = connection.prepareStatement("""
			SELECT active
				FROM CLASS_TA
				WHERE
					ta_id = ?
					AND class_id = ?;""");
		this.updateClassTA = connection.prepareStatement("""
			UPDATE CLASS_TA
				SET
					active = ?
				WHERE
					ta_id = ?
					AND class_id = ?;""");
		this.deleteClassTA = connection.prepareStatement("""
			DELETE FROM CLASS_TA
				WHERE
					ta_id = ?
					AND class_id = ?;""");
		this.selectTAClasses = connection.prepareStatement("""
			SELECT department, number, section, semester, year
				FROM CLASS_TA
					INNER JOIN CLASS
						ON CLASS_TA.class_id = CLASS.id
				WHERE
					ta_id = ?;""");
	}

	public int insertClassTA(
		int TAID,
		int classID,
		boolean active) throws SQLException
	{
		this.insertClassTA.setInt(1, TAID);
		this.insertClassTA.setInt(2, classID);
		this.insertClassTA.setInt(3, active? 1 : 0);
		return this.insertClassTA.executeUpdate();
	}

	public Optional<Boolean> selectClassTA(
		int TAID,
		int classID) throws SQLException
	{
		this.selectClassTA.setInt(1, TAID);
		this.selectClassTA.setInt(2, classID);
		try (var res = this.selectClassTA.executeQuery())
		{
			if (!res.next()) {return Optional.empty();}
			return Optional.of(res.getInt("active") == 1);
		}
	}

	public int updateClassTA(
		int profID,
		int classID,
		boolean active) throws SQLException
	{
		this.updateClassTA.setInt(1, active? 1:0);
		this.updateClassTA.setInt(2, profID);
		this.updateClassTA.setInt(3, classID);
		return this.updateClassTA.executeUpdate();
	}

	public int deleteClassTA(int profID, int classID) throws SQLException
	{
		this.deleteClassTA.setInt(1, profID);
		this.deleteClassTA.setInt(2, classID);
		return this.deleteClassTA.executeUpdate();
	}

	public List<HashMap<String, Object>> selectTAClasses(int profID) throws SQLException
	{
		var classes = new ArrayList<HashMap<String, Object>>();
		try (var res = this.selectTAClasses.executeQuery())
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
}
