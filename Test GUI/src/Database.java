import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

record LoginResult(
	int id,
	String fName,
	String lName,
	boolean isStudent,
	boolean isEmployee,
	boolean isProfessor,
	boolean isTa,
	boolean isAdmin){}

public class Database
{
	private final Connection connection;
	private final PreparedStatement insertPerson;
	private final PreparedStatement selectPerson;
	private final PreparedStatement updatePerson;
	private final PreparedStatement deletePerson;

	private final PreparedStatement insertStudent;
	private final PreparedStatement selectStudent;
	private final PreparedStatement selectStudents;
	private final PreparedStatement deleteStudent;

	private final PreparedStatement insertEmployee;
	private final PreparedStatement selectEmployee;
	private final PreparedStatement updateEmployee;
	private final PreparedStatement deleteEmployee;

	private final PreparedStatement insertProfessor;
	private final PreparedStatement selectProfessor;
	private final PreparedStatement selectProfessors;
	private final PreparedStatement deleteProfessor;

	private final PreparedStatement insertTA;
	private final PreparedStatement selectTA;
	private final PreparedStatement selectTAs;
	private final PreparedStatement deleteTA;

	private final PreparedStatement insertAdmin;
	private final PreparedStatement selectAdmin;
	private final PreparedStatement selectAdmins;
	private final PreparedStatement deleteAdmin;

	public Database(String dbPath) throws SQLException
	{
		final var url = "jdbc:sqlite:" + dbPath;
		this.connection = DriverManager.getConnection(url);
		this.connection.setAutoCommit(false);
		//allows the setup of a form connection
		//this will also allow to present all the data on the form when searching

		// Person
		this.insertPerson = this.connection.prepareStatement("""
			INSERT INTO PERSON (id, first_name, last_name, birth_date)
				VALUES (?, ?, ?, ?)""");
		this.selectPerson = this.connection.prepareStatement("""
				SELECT first_name, last_name, birth_date
					FROM PERSON
					WHERE id = ?""");
		this.updatePerson = this.connection.prepareStatement("""
			UPDATE PERSON 
				SET
					first_name = ?,
					last_name = ?,
					birth_date = ?
				WHERE id = ?""");
		this.deletePerson = this.connection.prepareStatement("""
			DELETE FROM PERSON
				WHERE id = ?""");

		// Student
		this.insertStudent = this.connection.prepareStatement("""
			INSERT INTO STUDENT (id)
				VALUES (?)""");
		this.selectStudent = this.connection.prepareStatement("""
			SELECT first_name, last_name, birth_date
				FROM STUDENT
					INNER JOIN PERSON
						ON STUDENT.id = PERSON.id
				WHERE STUDENT.id = ?""");
		this.selectStudents = this.connection.prepareStatement("""
			SELECT STUDENT.id, first_name, last_name, birth_date
				FROM STUDENT
					INNER JOIN PERSON
						ON STUDENT.id = PERSON.id""");
		this.deleteStudent = this.connection.prepareStatement("""
			DELETE FROM STUDENT
				WHERE id = ?""");

		// Employee
		this.insertEmployee = this.connection.prepareStatement("""
			INSERT INTO EMPLOYEE (person_id, department)
				VALUES (?, ?)""");
		this.selectEmployee = this.connection.prepareStatement("""
			SELECT first_name, last_name, birth_date, department
				FROM EMPLOYEE
					INNER JOIN PERSON
						ON EMPLOYEE.person_id = PERSON.id
				WHERE id = ?""");
		this.updateEmployee = this.connection.prepareStatement("""
			UPDATE EMPLOYEE 
				SET department = ?
				WHERE person_id = ?""");
		this.deleteEmployee = this.connection.prepareStatement("""
			DELETE FROM EMPLOYEE
				WHERE person_id = ?""");

		// Professor
		this.insertProfessor = this.connection.prepareStatement("""
			INSERT INTO PROFESSOR (id)
				VALUES (?)""");
		this.selectProfessor = this.connection.prepareStatement("""
			SELECT first_name, last_name, birth_date, department
				FROM PROFESSOR
					INNER JOIN EMPLOYEE
						ON PROFESSOR.id = EMPLOYEE.person_id
					INNER JOIN PERSON
						ON EMPLOYEE.person_id = PERSON.id
				WHERE PROFESSOR.id = ?""");
		this.selectProfessors = this.connection.prepareStatement("""
			SELECT PROFESSOR.id, first_name, last_name, birth_date, department
				FROM PROFESSOR
					INNER JOIN EMPLOYEE
						ON PROFESSOR.id = EMPLOYEE.person_id
					INNER JOIN PERSON
						ON EMPLOYEE.person_id = PERSON.id""");
		this.deleteProfessor = this.connection.prepareStatement("""
			DELETE FROM PROFESSOR
				WHERE id = ?""");

		// TA
		this.insertTA = this.connection.prepareStatement("""
			INSERT INTO TA (id)
				VALUES (?)""");
		this.selectTA = this.connection.prepareStatement("""
			SELECT first_name, last_name, birth_date, department
				FROM TA
					INNER JOIN EMPLOYEE
						ON TA.id = EMPLOYEE.person_id
					INNER JOIN PERSON
						ON EMPLOYEE.person_id = PERSON.id
				WHERE TA.id = ?""");
		this.selectTAs = this.connection.prepareStatement("""
			SELECT TA.id, first_name, last_name, birth_date, department
				FROM TA
					INNER JOIN EMPLOYEE
						ON TA.id = EMPLOYEE.person_id
					INNER JOIN PERSON
						ON EMPLOYEE.person_id = PERSON.id""");
		this.deleteTA = this.connection.prepareStatement("""
			DELETE FROM TA
				WHERE id = ?""");

		// Admin
		this.insertAdmin = this.connection.prepareStatement("""
			INSERT INTO ADMIN (employee_id)
				VALUES (?)""");
		this.selectAdmin = this.connection.prepareStatement("""
			SELECT first_name, last_name, birth_date, department
				FROM ADMIN
					INNER JOIN EMPLOYEE
						ON ADMIN.employee_id = EMPLOYEE.person_id
					INNER JOIN PERSON
						ON EMPLOYEE.person_id = PERSON.id
				WHERE ADMIN.employee_id = ?""");
		this.selectAdmins = this.connection.prepareStatement("""
			SELECT first_name, last_name, birth_date, department
				FROM ADMIN
					INNER JOIN EMPLOYEE
						ON ADMIN.employee_id = EMPLOYEE.person_id
					INNER JOIN PERSON
						ON EMPLOYEE.person_id = PERSON.id""");
		this.deleteAdmin = this.connection.prepareStatement("""
			DELETE FROM ADMIN
				WHERE employee_id = ?""");
	}

	public Optional<LoginResult> checkLogin(int id) throws SQLException
	{
		this.selectPerson.setInt(1, id);
		final var getPersonRes = this.selectPerson.executeQuery();
		if(getPersonRes.next())
		{
			return Optional.empty();
		}
		final var fName = getPersonRes.getString("first_name");
		final var lName = getPersonRes.getString("last_name");
		getPersonRes.close();

		this.selectStudent.setInt(1, id);
		final var getStudentRes = this.selectStudent.executeQuery();
		final var isStudent = getStudentRes.next();
		getStudentRes.close();

		this.selectEmployee.setInt(1, id);
		final var getEmployeeRes = this.selectEmployee.executeQuery();
		final var isEmployee = getEmployeeRes.next();
		getEmployeeRes.close();

		this.selectProfessor.setInt(1, id);
		final var getProfessorRes = this.selectProfessor.executeQuery();
		final var isProfessor = getProfessorRes.next();
		getProfessorRes.close();

		this.selectTA.setInt(1, id);
		final var getTARes = this.selectTA.executeQuery();
		final var isTA = getTARes.next();
		getTARes.close();

		this.selectAdmin.setInt(1, id);
		final var getAdminRes = this.selectAdmin.executeQuery();
		final var isAdmin = getAdminRes.next();
		getAdminRes.close();

		return Optional.of(new LoginResult(
			id,
			fName,
			lName,
			isStudent,
			isEmployee,
			isProfessor,
			isTA,
			isAdmin));
	}

	public void addProfessor(int id, String firstName, String lastName, String birthDate, String department) throws SQLException
	{
		this.connection.rollback();
		this.insertPerson.setInt(1, id);
		this.insertPerson.setString(2, firstName);
		this.insertPerson.setString(3, lastName);
		this.insertPerson.setString(4, birthDate);
		this.insertPerson.executeUpdate();

		this.insertEmployee.setInt(1, id);
		this.insertEmployee.setString(2, department);
		this.insertEmployee.executeUpdate();

		this.insertProfessor.setInt(1, id);
		this.insertProfessor.executeUpdate();

		this.connection.commit();
	}

	public void updateProfessor(int id, String firstName, String lastName, String birthDate, String department) throws SQLException
	{
		this.connection.rollback();
		this.updatePerson.setInt(1, id);
		this.updatePerson.setString(2, firstName);
		this.updatePerson.setString(3, lastName);
		this.updatePerson.setString(4, birthDate);
		this.updatePerson.executeUpdate();

		this.updateEmployee.setInt(1, id);
		this.updateEmployee.setString(2, department);
		this.updateEmployee.executeUpdate();

		this.connection.commit();
	}

	public Professor removeProfessor(int id) throws SQLException
	{
		this.connection.rollback();

		this.selectProfessor.setInt(1, id);
		final var getProfRes = this.selectProfessor.executeQuery();
		if(!getProfRes.isBeforeFirst())
		{
			// Professor does not exist
			throw new SQLException("Tried to remove professor, but professor does not exist");
		}
		getProfRes.next();
		final var old = new Professor(
			id,
			getProfRes.getString("department"),
			getProfRes.getString("first_name"),
			getProfRes.getString("last_name"),
			getProfRes.getString("birth_date"));
		getProfRes.close();

		this.deleteProfessor.setInt(1, id);
		this.deleteProfessor.executeUpdate();
		
		this.deleteEmployee.setInt(1, id);
		this.deleteEmployee.executeUpdate();
		
		this.deletePerson.setInt(1, id);
		this.deletePerson.executeUpdate();

		this.connection.commit();
		return old;
	}

	public List<Professor> listProfessors() throws SQLException
	{
		this.connection.rollback();

		final var getProfsRes = this.selectProfessors.executeQuery();
		final var profs = new ArrayList<Professor>();
		while(getProfsRes.next())
		{
			final var prof = new Professor(
				getProfsRes.getInt("PROFESSOR.id"),
				getProfsRes.getString("first_name"),
				getProfsRes.getString("last_name"),
				getProfsRes.getString("birth_date"),
				getProfsRes.getString("department"));
			profs.add(prof);
		}
		getProfsRes.close();
		return profs;
	}

	public List<TA> listTAs() throws SQLException
	{
		this.connection.rollback();

		final var getTAsRes = this.selectTAs.executeQuery();
		final var tas = new ArrayList<TA>();
		while(getTAsRes.next())
		{
			final var ta = new TA(
				getTAsRes.getInt("TA.id"),
				getTAsRes.getString("first_name"),
				getTAsRes.getString("last_name"),
				getTAsRes.getString("birth_date"),
				getTAsRes.getString("department"));
			tas.add(ta);
		}
		getTAsRes.close();
		return tas;
	}

	public List<Student> listStudents() throws SQLException
	{
		this.connection.rollback();

		final var getStudentsRes = this.selectStudents.executeQuery();
		final var students = new ArrayList<Student>();
		while(getStudentsRes.next())
		{
			final var student = new Student(
				getStudentsRes.getInt("id"),
				getStudentsRes.getString("first_name"),
				getStudentsRes.getString("last_name"),
				getStudentsRes.getString("birth_date"));
			students.add(student);
		}
		getStudentsRes.close();
		return students;
	}
}