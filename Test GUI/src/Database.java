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

	private final PreparedStatement insertClass;
	private final PreparedStatement selectClasses;
	private final PreparedStatement deleteClass;

	private final PreparedStatement insertClassProfessor;
	private final PreparedStatement deleteClassProfessor;

	private final PreparedStatement insertClassTA;
	private final PreparedStatement deleteClassTA;

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

		// Class
		this.insertClass = this.connection.prepareStatement("""
			INSERT INTO CLASS (department, number, section, semester, year)
				VALUES (?, ?, ?, ?, ?)
				RETURNING id;""");
		this.selectClasses = this.connection.prepareStatement("""
			SELECT id, department, number, section, semester, year
				FROM CLASS;""");
		this.deleteClass = this.connection.prepareStatement("""
			DELETE
				FROM CLASS
				WHERE
					department = ?
					AND number = ?
					AND section = ?
					AND semester = ?
					AND year = ?;""");
		
		// Professors and Classes
		this.insertClassProfessor = this.connection.prepareStatement("""
			INSERT INTO CLASS_PROFESSOR (professor_id, class_id)
				SELECT ?, id
					FROM CLASS
						WHERE
							department = ?
							AND number = ?
							AND section = ?
							AND semester = ?
							AND year = ?;""");
		this.deleteClassProfessor = this.connection.prepareStatement("""
			DELETE FROM CLASS_PROFESSOR
				WHERE
					professor_id = ?
					AND class_id IN(
						SELECT id
							FROM CLASS
							WHERE
								department = ?
								AND number = ?
								AND section = ?
								AND semester = ?
								AND year = ?);""");
		
		// TAs and Classes
		this.insertClassTA = this.connection.prepareStatement("""
			INSERT INTO CLASS_TA (ta_id, class_id)
				SELECT ?, id
					FROM CLASS
						WHERE
							department = ?
							AND number = ?
							AND section = ?
							AND semester = ?
							AND year = ?;""");
		this.deleteClassTA = this.connection.prepareStatement("""
			DELETE FROM CLASS_TA
				WHERE
					ta_id = ?
					AND class_id IN(
						SELECT id
							FROM CLASS
							WHERE
								department = ?
								AND number = ?
								AND section = ?
								AND semester = ?
								AND year = ?);""");
	}

	public Optional<LoginResult> checkLogin(int id) throws SQLException
	{
		this.selectPerson.setInt(1, id);
		final var getPersonRes = this.selectPerson.executeQuery();
		if(!getPersonRes.next())
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

	public Professor updateProfessor(int id, String firstName, String lastName, String birthDate, String department) throws SQLException
	{
		this.connection.rollback();

		this.selectProfessor.setInt(1, id);
		final var selProfRes = this.selectProfessor.executeQuery();
		if(!selProfRes.next())
		{
			throw new SQLException("Tried to update professor, but professor does not exist.");
		}

		final var old = new Professor(
			id,
			selProfRes.getString("first_name"),
			selProfRes.getString("last_name"),
			selProfRes.getString("birth_date"),
			selProfRes.getString("department"));
		selProfRes.close();

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

	public void addTA(int id, String firstName, String lastName, String birthDate, String department) throws SQLException
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

		this.insertTA.setInt(1, id);
		this.insertTA.executeUpdate();

		this.connection.commit();
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

	public void updateTA(int id, String firstName, String lastName, String birthDate, String department) throws SQLException
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

	public TA removeTA(int id) throws SQLException
	{
		this.connection.rollback();

		this.selectTA.setInt(1, id);
		final var getTARes = this.selectTA.executeQuery();
		if(!getTARes.next())
		{
			// Professor does not exist
			throw new SQLException("Tried to remove TA, but TA does not exist");
		}
		getTARes.next();
		final var old = new TA(
			id,
			getTARes.getString("department"),
			getTARes.getString("first_name"),
			getTARes.getString("last_name"),
			getTARes.getString("birth_date"));
		getTARes.close();

		this.deleteTA.setInt(1, id);
		this.deleteTA.executeUpdate();
		
		this.deleteEmployee.setInt(1, id);
		this.deleteEmployee.executeUpdate();
		
		this.deletePerson.setInt(1, id);
		this.deletePerson.executeUpdate();

		this.connection.commit();
		return old;
	}

	public void addAdmin(int employeeID) throws SQLException
	{
		this.connection.rollback();
		this.insertAdmin.setInt(1, employeeID);
		final var res = this.insertAdmin.executeUpdate();
		if(res == 0)
		{
			throw new SQLException("Tried to make employee an admin, but employee does not exist");
		}
		this.connection.commit();
	}

	public List<Employee> listAdmins() throws SQLException
	{
		this.connection.rollback();

		final var getAdminRes = this.selectAdmins.executeQuery();
		final var admins = new ArrayList<Employee>();
		while(getAdminRes.next())
		{
			final var admin = new Employee(
				getAdminRes.getInt("TA.id"),
				getAdminRes.getString("first_name"),
				getAdminRes.getString("last_name"),
				getAdminRes.getString("birth_date"),
				getAdminRes.getString("department"));
			admins.add(admin);
		}
		getAdminRes.close();
		return admins;
	}

	public void removeAdmin(int employeeID) throws SQLException
	{
		this.connection.rollback();
		this.deleteAdmin.setInt(1, employeeID);
		final var res = this.deleteAdmin.executeUpdate();
		if(res == 0)
		{
			throw new SQLException("Tried to remove an admin, but admin does not exist");
		}
		this.connection.commit();
	}

	public void addStudent(int id, String firstName, String lastName, String birthDate) throws SQLException
	{
		this.connection.rollback();
		this.insertPerson.setInt(1, id);
		this.insertPerson.setString(2, firstName);
		this.insertPerson.setString(3, lastName);
		this.insertPerson.setString(4, birthDate);
		this.insertPerson.executeUpdate();

		this.insertStudent.setInt(1, id);
		this.insertStudent.executeUpdate();

		this.connection.commit();
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

	public Student removeStudent(int id) throws SQLException
	{
		this.connection.rollback();

		this.selectStudent.setInt(1, id);
		final var getStudentRes = this.selectProfessor.executeQuery();
		if(!getStudentRes.next())
		{
			// Professor does not exist
			throw new SQLException("Tried to remove student, but student does not exist");
		}
		getStudentRes.next();
		final var old = new Student(
			id,
			getStudentRes.getString("first_name"),
			getStudentRes.getString("last_name"),
			getStudentRes.getString("birth_date"));
		getStudentRes.close();

		this.deleteStudent.setInt(1, id);
		this.deleteStudent.executeUpdate();
		
		this.deletePerson.setInt(1, id);
		this.deletePerson.executeUpdate();

		this.connection.commit();
		return old;
	}

	public void addClass(String department, int number, int section, int semester, int year) throws SQLException
	{
		this.connection.rollback();

		this.insertClass.setString(1, department);
		this.insertClass.setInt(2, number);
		this.insertClass.setInt(3, section);
		this.insertClass.setInt(4, semester);
		this.insertClass.setInt(5, year);
		this.insertClass.executeUpdate();
		
		this.connection.commit();
	}

	public List<Class> listClasses() throws SQLException
	{
		this.connection.rollback();

		final var selClassRes = this.selectClasses.executeQuery();
		final var classes = new ArrayList<Class>();
		while(selClassRes.next())
		{
			final var c = new Class(
				selClassRes.getString("department"),
				selClassRes.getInt("number"),
				selClassRes.getInt("section"),
				selClassRes.getInt("semester"),
				selClassRes.getInt("year"));
			classes.add(c);
		}
		return classes;
	}

	public void removeClass(String department, int number, int section, int semester, int year) throws SQLException
	{
		this.connection.rollback();

		this.deleteClass.setString(1, department);
		this.deleteClass.setInt(2, number);
		this.deleteClass.setInt(3, section);
		this.deleteClass.setInt(4, semester);
		this.deleteClass.setInt(5, year);
		final var res = this.deleteClass.executeUpdate();
		if(res==0)
		{
			throw new SQLException("Tried to remove class, but class does not exist");
		}
		
		this.connection.commit();
	}

	public void addProfessorToClass(int professorId, String department, int number, int section, int semester, int year) throws SQLException
	{
		this.connection.rollback();

		this.insertClassProfessor.setInt(1, professorId);
		this.insertClassProfessor.setString(2, department);
		this.insertClassProfessor.setInt(3, number);
		this.insertClassProfessor.setInt(4, section);
		this.insertClassProfessor.setInt(5, semester);
		this.insertClassProfessor.setInt(6, year);
		final var insClassProfRes = this.insertClassProfessor.executeUpdate();
		if(insClassProfRes == 0)
		{
			throw new SQLException("Tried to add Professor to class, but class does not exist");
		}

		this.connection.commit();
	}

	public void removeProfessorFromClass(int professorId, String department, int number, int section, int semester, int year) throws SQLException
	{
		this.connection.rollback();

		this.deleteClassProfessor.setInt(1, professorId);
		this.deleteClassProfessor.setString(2, department);
		this.deleteClassProfessor.setInt(3, number);
		this.deleteClassProfessor.setInt(4, section);
		this.deleteClassProfessor.setInt(5, semester);
		this.deleteClassProfessor.setInt(6, year);
		final var delClassProfRes = this.deleteClassProfessor.executeUpdate();
		if(delClassProfRes == 0)
		{
			throw new SQLException("Tried to remove Professor from class, but professor not assigned to class");
		}
		this.connection.commit();
	}

	public void addTAToClass(int TAId, String department, int number, int section, int semester, int year) throws SQLException
	{
		this.connection.rollback();

		this.insertClassTA.setInt(1, TAId);
		this.insertClassTA.setString(2, department);
		this.insertClassTA.setInt(3, number);
		this.insertClassTA.setInt(4, section);
		this.insertClassTA.setInt(5, semester);
		this.insertClassTA.setInt(6, year);
		final var insClassTARes = this.insertClassTA.executeUpdate();
		if(insClassTARes == 0)
		{
			throw new SQLException("Tried to add TA to class, but class does not exist");
		}

		this.connection.commit();
	}

	public void removeTAFromClass(int TAId, String department, int number, int section, int semester, int year) throws SQLException
	{
		this.connection.rollback();

		this.deleteClassTA.setInt(1, TAId);
		this.deleteClassTA.setString(2, department);
		this.deleteClassTA.setInt(3, number);
		this.deleteClassTA.setInt(4, section);
		this.deleteClassTA.setInt(5, semester);
		this.deleteClassTA.setInt(6, year);
		final var delClassTARes = this.deleteClassTA.executeUpdate();
		if(delClassTARes == 0)
		{
			throw new SQLException("Tried to remove TA from class, but TA not assigned to class");
		}
		this.connection.commit();
	}
}