package Database.Managers;
import java.sql.*;
import java.util.*;

import Database.Employee;

public class EmployeeManager
{
	private final PreparedStatement insertEmployee;
	private final PreparedStatement selectEmployee;
	private final PreparedStatement selectEmployees;
	private final PreparedStatement updateEmployee;
	private final PreparedStatement deleteEmployee;

	public EmployeeManager(Connection connection) throws SQLException
	{
		this.insertEmployee = connection.prepareStatement("""
			INSERT INTO EMPLOYEE (person_id, department)
				VALUES (?, ?)""");
		this.selectEmployee = connection.prepareStatement("""
			SELECT first_name, last_name, birth_date, department
				FROM EMPLOYEE
					INNER JOIN PERSON
						ON EMPLOYEE.person_id = PERSON.id
				WHERE id = ?""");
		this.selectEmployees = connection.prepareStatement("""
			SELECT first_name, last_name, birth_date, department
				FROM EMPLOYEE
					INNER JOIN PERSON
						ON EMPLOYEE.person_id = PERSON.id""");
		this.updateEmployee = connection.prepareStatement("""
			UPDATE EMPLOYEE
				SET department = ?
				WHERE person_id = ?""");
		this.deleteEmployee = connection.prepareStatement("""
			DELETE FROM EMPLOYEE
				WHERE person_id = ?""");
	}

	public int insertEmployee(int id) throws SQLException
	{
		this.insertEmployee.setInt(1, id);
		return this.insertEmployee.executeUpdate();
	}

	public Optional<Employee> selectEmployee(int id) throws SQLException
	{
		this.selectEmployee.setInt(1, id);
		try (var res = this.selectEmployee.executeQuery())
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

	public List<HashMap<String, Object>> selectEmployees() throws SQLException
	{
		var employees = new ArrayList<HashMap<String, Object>>();
		try (var res = this.selectEmployees.executeQuery())
		{
			while (res.next())
			{
				final var employee = new HashMap<String, Object>();
				employee.put("Employee ID", res.getInt("id"));
				employee.put("First Name", res.getString("first_name"));
				employee.put("Last Name", res.getString("last_name"));
				employee.put("Birth Date", res.getString("birth_date"));
				employee.put("Department", res.getString("department"));
				employees.add(employee);
			}
		}
		return employees;
	}

	public int updateEmployee(int id, String dept) throws SQLException
	{
		this.updateEmployee.setString(1, dept);
		this.updateEmployee.setInt(2, id);
		return this.updateEmployee.executeUpdate();
	}

	public int deleteEmployee(int id) throws SQLException
	{
		this.deleteEmployee.setInt(1, id);
		return this.deleteEmployee.executeUpdate();
	}
}
