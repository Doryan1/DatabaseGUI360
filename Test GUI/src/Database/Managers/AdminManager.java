package Database.Managers;
import java.sql.*;
import java.util.*;
import Database.Employee;
public class AdminManager
{
	private final PreparedStatement insertAdmin;
	private final PreparedStatement selectAdmin;
	private final PreparedStatement selectAdmins;
	private final PreparedStatement deleteAdmin;

	public AdminManager(Connection connection) throws SQLException
	{
		this.insertAdmin = connection.prepareStatement("""
			INSERT INTO ADMIN (employee_id)
				VALUES (?)""");
		this.selectAdmin = connection.prepareStatement("""
			SELECT first_name, last_name, birth_date, department
				FROM ADMIN
					INNER JOIN EMPLOYEE
						ON ADMIN.employee_id = EMPLOYEE.person_id
					INNER JOIN PERSON
						ON EMPLOYEE.person_id = PERSON.id
				WHERE ADMIN.employee_id = ?""");
		this.selectAdmins = connection.prepareStatement("""
			SELECT first_name, last_name, birth_date, department
				FROM ADMIN
					INNER JOIN EMPLOYEE
						ON ADMIN.employee_id = EMPLOYEE.person_id
					INNER JOIN PERSON
						ON EMPLOYEE.person_id = PERSON.id""");
		this.deleteAdmin = connection.prepareStatement("""
			DELETE FROM ADMIN
				WHERE employee_id = ?""");
	}

	public int insertAdmin(int id) throws SQLException
	{
		this.insertAdmin.setInt(1, id);
		return this.insertAdmin.executeUpdate();
	}

	public Optional<Employee> selectAdmin(int id) throws SQLException
	{
		this.selectAdmin.setInt(1, id);
		try (var res = this.selectAdmin.executeQuery())
		{
			if (!res.next()) {return Optional.empty();}
			return Optional.of(new Employee(
				id,
				res.getString("first_name"),
				res.getString("last_name"),
				res.getString("birth_date"),
				res.getString("department")));
		}
	}

	public ArrayList<HashMap<String, Object>> selectAdmins() throws SQLException
	{
		var admins = new ArrayList<HashMap<String, Object>>();
		try (var res = this.selectAdmins.executeQuery())
		{
			while (res.next())
			{
				final var admin = new HashMap<String, Object>();
				admin.put("Employee ID", res.getInt("id"));
				admin.put("First Name", res.getString("first_name"));
				admin.put("Last Name", res.getString("last_name"));
				admin.put("Birth Date", res.getString("birth_date"));
				admin.put("Department", res.getString("department"));
				admins.add(admin);
			}
		}
		return admins;
	}

	public int deleteAdmin(int id) throws SQLException
	{
		this.deleteAdmin.setInt(1, id);
		return this.deleteAdmin.executeUpdate();
	}
}
