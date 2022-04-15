import java.sql.*;
import java.util.*;
import javax.swing.table.*;

record LoginResult(
	int id,
	String fName,
	String lName,
	boolean isStudent,
	boolean isEmployee,
	boolean isProfessor,
	boolean isTa,
	boolean isAdmin){}

/**
 * Class that handles connecting to and manipulating the database.
 * Requires a SQLite compatible database driver.
 */
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
	private final PreparedStatement selectClassID;
	private final PreparedStatement selectClasses;
	private final PreparedStatement deleteClass;

	private final PreparedStatement insertClassProfessor;
	private final PreparedStatement deleteClassProfessor;

	private final PreparedStatement insertClassTA;
	private final PreparedStatement deleteClassTA;

	private final PreparedStatement insertAssignment;
	private final PreparedStatement selectAssignment;
	private final PreparedStatement selectStudentAssignments;
	private final PreparedStatement selectClassAssignments;
	private final PreparedStatement selectStudentClassAssignments;
	private final PreparedStatement updateAssignment;
	private final PreparedStatement deleteAssignment;


	/**
	 * Create a database connection and set up the necessary procedures.
	 * Parameter is the path to the database file relative to the CWD.
	 * @param dbPath
	 * @throws SQLException
	 */
	public Database(String dbPath) throws SQLException
	{
		// TODO: Setup db if not found.
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
		this.selectClassID = this.connection.prepareStatement("""
			SELECT id
				FROM CLASS
				WHERE
					department = ?
					AND number = ?
					AND section = ?
					AND semester = ?
					AND year = ?;""");
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

		// Assignments
		this.insertAssignment = this.connection.prepareStatement("""
			INSERT INTO ASSIGNMENT (class_id, student_id, name, grade)
				VALUES (?, ?, ?, ?)""");
		this.selectAssignment = this.connection.prepareStatement("""
			SELECT (grade)
				FROM ASSIGNMENT
					INNER JOIN CLASS
						ON ASSIGNMENT.class_id = CLASS.id
				WHERE
					student_id = ?
					AND name = ?
					AND class_id = ?""");
		this.selectStudentAssignments = this.connection.prepareStatement("""
			SELECT name, grade, department, number, section, semester, year
				FROM ASSIGNMENT
					INNER JOIN CLASS
						ON ASSIGNMENT.class_id = CLASS.id
				WHERE
					student_id = ?""");
		this.selectClassAssignments = this.connection.prepareStatement("""
			SELECT student_id, name, grade
				FROM ASSIGNMENT
				WHERE
					class_id = ?""");
		this.selectStudentClassAssignments = this.connection.prepareStatement("""
			SELECT student_id, name, grade
				FROM ASSIGNMENT
				WHERE
					class_id = ?
					AND student_id = ?""");
		this.updateAssignment = this.connection.prepareStatement("""
			UPDATE ASSIGNMENT
				SET
					grade = ?
				WHERE
					class_id = ?
					AND student_id = ?
					AND name = ?""");
		this.deleteAssignment = this.connection.prepareStatement("""
			DELETE FROM ASSIGNMENT
				WHERE
					class_id = ?
					AND student_id = ?
					AND name = ?
				RETURNING grade""");
	}

	private static TableModel makeTableModel(ResultSet rs) throws SQLException
	{
		// TODO: prettify column names
		final var meta = rs.getMetaData();
		final var cCount = meta.getColumnCount();
		final var columns = new Vector<String>(cCount);
		for(int c = 1; c <= cCount; c +=1)
		{
			columns.add(meta.getColumnName(c));
			// Ugly hack to show letter grades Pt 1
			if(meta.getColumnName(c) == "grade")
			{
				columns.add("Letter Grade");
			}
		}

		final var data = new Vector<Vector<Object>>();
		while(rs.next())
		{
			final var row = new Vector<Object>();

			for(int c=1; c<=cCount; c+=1)
			{
				row.add(rs.getObject(c));
				// Ugly hack to show letter grades Pt 2
				if(columns.get(c) == "grade")
				{
					final var score = (int)rs.getObject(c);
					row.add((Object)letterGrade(score));
				}
			}
			data.add(row);
		}
		return new DefaultTableModel(data, columns);
	}

	private static char letterGrade(int score)
	{
		// If only java had pattern matching and ranges
		if(score>=91) {
			return 'A';
		} else if (score<91 && score>=81) {
			return 'B';
		} else if (score<81 && score>=71) {
			return 'C';
		} else if (score<71 && score>=61) {
			return 'D';
		} else {
			return 'F';
		}
	}

	/**
	 * Performs a lookup for the given user ID and returns basic information
	 * about the account as well as permissions. The returned optional will be
	 * present if the account exist. Conversely, the optional will not be
	 * present if the account does not exist.
	 * @param id
	 * @return
	 * @throws SQLException
	 */
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

	/**
	 * Add a professor to the database.
	 * A professor by default is not assigned to any classes.
	 * @param id
	 * @param firstName
	 * @param lastName
	 * @param birthDate
	 * @param department
	 * @throws SQLException
	 */
	public void addProfessor(
		int id,
		String firstName,
		String lastName,
		String birthDate,
		String department) throws SQLException
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

	/**
	 * Lists every Professor currently in the database
	 * @return
	 * @throws SQLException
	 */
	public TableModel listProfessors() throws SQLException
	{
		this.connection.rollback();

		final var getProfsRes = this.selectProfessors.executeQuery();
		return makeTableModel(getProfsRes);
	}

	/**
	 * Overwrites the information
	 * @param id
	 * @param firstName
	 * @param lastName
	 * @param birthDate
	 * @param department
	 * @return The professor's information before it was updated
	 * @throws SQLException
	 */
	public Professor updateProfessor(
		int id,
		String firstName,
		String lastName,
		String birthDate,
		String department) throws SQLException
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
		return old;
	}

	/**
	 * Removes from the database the professor with the give ID
	 * @param id
	 * @return The professor that was removed.
	 * @throws SQLException
	 */
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

	/**
	 * Adds a TA to the database with the given information
	 * @param id
	 * @param firstName
	 * @param lastName
	 * @param birthDate
	 * @param department
	 * @throws SQLException
	 */
	public void addTA(
		int id,
		String firstName,
		String lastName,
		String birthDate,
		String department) throws SQLException
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

	/**
	 * Lists every TA in the database.
	 * @return The list of TAs
	 * @throws SQLException
	 */
	public TableModel listTAs() throws SQLException
	{
		this.connection.rollback();

		final var getTAsRes = this.selectTAs.executeQuery();
		return makeTableModel(getTAsRes);
	}

	/**
	 * Updates a TA in the database with the given information.
	 * Account is based on the ID.
	 * @param id
	 * @param firstName
	 * @param lastName
	 * @param birthDate
	 * @param department
	 * @throws SQLException
	 */
	public void updateTA(
		int id,
		String firstName,
		String lastName,
		String birthDate,
		String department) throws SQLException
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

	/**
	 * Removes the TA with the given id from the database.
	 * @param id
	 * @return The TA that was removed.
	 * @throws SQLException
	 */
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

	/**
	 * Makes the employee with the given ID into an admin.
	 * @param employeeID
	 * @throws SQLException
	 */
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

	/**
	 * Lists every admin currently in the database
	 * @return The list of employees marked as admins
	 * @throws SQLException
	 */
	public TableModel listAdmins() throws SQLException
	{
		this.connection.rollback();

		final var getAdminRes = this.selectAdmins.executeQuery();
		return makeTableModel(getAdminRes);
	}

	/**
	 * Make the employee with the given ID no longer an admin.
	 * Does not remove account from system.
	 * @param employeeID
	 * @throws SQLException
	 */
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

	/**
	 * Adds a student with the given information to the database.
	 * @param id
	 * @param firstName
	 * @param lastName
	 * @param birthDate
	 * @throws SQLException
	 */
	public void addStudent(
		int id,
		String firstName,
		String lastName,
		String birthDate) throws SQLException
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

	/**
	 * Lists every student currently in the syste.
	 * @return A list of students.
	 * @throws SQLException
	 */
	public TableModel listStudents() throws SQLException
	{
		this.connection.rollback();

		final var getStudentsRes = this.selectStudents.executeQuery();
		return makeTableModel(getStudentsRes);
	}

	/**
	 * Removes the student with the given ID from the system.
	 * @param id
	 * @return The student that was removed.
	 * @throws SQLException
	 */
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

	/**
	 * Adds the given class to the system.
	 * @param department
	 * @param number
	 * @param section
	 * @param semester
	 * @param year
	 * @throws SQLException
	 */
	public void addClass(
		String department,
		int number,
		int section,
		int semester,
		int year) throws SQLException
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

	private OptionalInt getClassID(
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
		final var selClassIDRes = this.selectClassID.executeQuery();
		if(!selClassIDRes.next())
		{
			return OptionalInt.empty();
		}
		final int classID = selClassIDRes.getInt("id");
		return OptionalInt.of(classID);
	}

	/**
	 * Lists every class currently in the database.
	 * @return A List of Classes
	 * @throws SQLException
	 */
	public TableModel listClasses() throws SQLException
	{
		this.connection.rollback();

		final var selClassRes = this.selectClasses.executeQuery();
		return makeTableModel(selClassRes);
	}

	/**
	 * Removes the given class from the database.
	 * @param department
	 * @param number
	 * @param section
	 * @param semester
	 * @param year
	 * @throws SQLException
	 */
	public void removeClass(
		String department,
		int number,
		int section,
		int semester,
		int year) throws SQLException
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

	/**
	 * Assigns the professor with the given ID to the given class.
	 * @param professorId
	 * @param department
	 * @param number
	 * @param section
	 * @param semester
	 * @param year
	 * @throws SQLException
	 */
	public void addProfessorToClass(
		int professorId,
		String department,
		int number,
		int section,
		int semester,
		int year) throws SQLException
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

	/**
	 * Un-assigns the professor with the given ID from the given class.
	 * @param professorId
	 * @param department
	 * @param number
	 * @param section
	 * @param semester
	 * @param year
	 * @throws SQLException
	 */
	public void removeProfessorFromClass(
		int professorId,
		String department,
		int number,
		int section,
		int semester,
		int year) throws SQLException
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

	/**
	 * Assigns the TA with the given ID to the given class.
	 * @param TAId
	 * @param department
	 * @param number
	 * @param section
	 * @param semester
	 * @param year
	 * @throws SQLException
	 */
	public void addTAToClass(
		int TAId,
		String department,
		int number,
		int section,
		int semester,
		int year) throws SQLException
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

	/**
	 * Un-assigns the TA with the given ID from the given class.
	 * @param TAId
	 * @param department
	 * @param number
	 * @param section
	 * @param semester
	 * @param year
	 * @throws SQLException
	 */
	public void removeTAFromClass(
		int TAId,
		String department,
		int number,
		int section,
		int semester,
		int year) throws SQLException
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

	/**
	 * Add a grade for an student's assignment to a class
	 * @param studentID
	 * @param assignment
	 * @param grade
	 * @param department
	 * @param number
	 * @param section
	 * @param semester
	 * @param year
	 * @throws SQLException
	 */
	public void addGrade(
		int studentID,
		String assignment,
		int grade,
		String department,
		int number,
		int section,
		int semester,
		int year) throws SQLException
	{
		this.connection.rollback();

		final int classID = getClassID(department, number, section, semester, year)
			.orElseThrow(()->new SQLException("Tried to add grade to class, but class does not exist"));

		this.insertAssignment.setInt(1, classID);
		this.insertAssignment.setInt(2, studentID);
		this.insertAssignment.setString(3, assignment);
		this.insertAssignment.setInt(4, grade);
		this.insertAssignment.executeUpdate();

		this.connection.commit();
	}

	/**
	 * Fetch the grade given for the specified assignment in the specified
	 * class for the specified student.
	 * @param studentID
	 * @param assignment
	 * @param department
	 * @param number
	 * @param year
	 * @param semester
	 * @param section
	 * @return
	 * @throws SQLException
	 */
	public int getGrade(
		int studentID,
		String assignment,
		String department,
		int number,
		int year,
		int semester,
		int section) throws SQLException
	{
		this.connection.rollback();

		final int classID = getClassID(department, number, section, semester, year)
			.orElseThrow(()->new SQLException("Tried to get grade for class, but class does not exist"));
		this.selectAssignment.setInt(1, studentID);
		this.selectAssignment.setString(2, assignment);
		this.selectAssignment.setInt(3, classID);
		final var selAssRes = this.selectAssignment.executeQuery();
		if(!selAssRes.next())
		{
			throw new SQLException("Tried to get grade for assignment, but assignment does not exist");
		}

		return selAssRes.getInt("grade");
	}

	/**
	 * Fetch every grade for every class for a specific student.
	 * @param studentID
	 * @return
	 * @throws SQLException
	 */
	public TableModel listGradesForStudent(int studentID) throws SQLException
	{
		this.connection.rollback();

		this.selectStudentAssignments.setInt(1, studentID);
		final var selStuAssRes = this.selectStudentAssignments.executeQuery();
		return makeTableModel(selStuAssRes);
	}

	/**
	 * Fetch every grade for every student in a given class.
	 * @param department
	 * @param number
	 * @param section
	 * @param semester
	 * @param year
	 * @return
	 * @throws SQLException
	 */
	public TableModel listGradesForClass(
		String department,
		int number,
		int section,
		int semester,
		int year) throws SQLException
	{
		this.connection.rollback();

		this.selectClassAssignments.setString(1, department);
		this.selectClassAssignments.setInt(2, number);
		this.selectClassAssignments.setInt(3, section);
		this.selectClassAssignments.setInt(4, semester);
		this.selectClassAssignments.setInt(5, year);
		final var selClassAssRes = this.selectClassAssignments.executeQuery();
		return makeTableModel(selClassAssRes);
	}

	/**
	 * Fetch every grade a student has for the given class.
	 * @param studentID
	 * @param department
	 * @param number
	 * @param section
	 * @param semester
	 * @param year
	 * @return A List of Assignments
	 * @throws SQLException
	 */
	public TableModel listGradesForStudentInClass(
		int studentID,
		String department,
		int number,
		int section,
		int semester,
		int year) throws SQLException
	{
		this.connection.rollback();

		final int classID = getClassID(department, number, section, semester, year)
			.orElseThrow(()->new SQLException("Tried to get grades for class, but class does not exist"));

		this.selectStudentClassAssignments.setInt(1, classID);
		this.selectStudentClassAssignments.setInt(2, studentID);
		final var selClassStudAssRes = this.selectStudentClassAssignments.executeQuery();
		return makeTableModel(selClassStudAssRes);
	}

	/**
	 * Updates a grade for an student's assignment in a class
	 * @param studentID
	 * @param assignment
	 * @param grade
	 * @param department
	 * @param number
	 * @param section
	 * @param semester
	 * @param year
	 * @throws SQLException
	 */
	public void updateGrade(
		int studentID,
		String assignment,
		int grade,
		String department,
		int number,
		int section,
		int semester,
		int year) throws SQLException
	{
		this.connection.rollback();

		this.selectClassID.setString(1, department);
		this.selectClassID.setInt(2, number);
		this.selectClassID.setInt(3, section);
		this.selectClassID.setInt(4, semester);
		this.selectClassID.setInt(5, year);
		final var selClassIDRes = this.selectClassID.executeQuery();
		if(!selClassIDRes.next())
		{
			throw new SQLException("Tried to update grade in class, but class does not exist");
		}
		final int classID = selClassIDRes.getInt("id");
		selClassIDRes.close();

		this.updateAssignment.setInt(1, grade);
		this.updateAssignment.setInt(2, classID);
		this.updateAssignment.setInt(3, studentID);
		this.updateAssignment.setString(4, assignment);
		if (this.updateAssignment.executeUpdate() < 1)
		{
			throw new SQLException("Tried to update grade in class, but assignment does not exist");
		}

		this.connection.commit();
	}

	/**
	 * Deletes the given assignment from the given class for the given student.
	 * @param studentID
	 * @param assignment
	 * @param department
	 * @param number
	 * @param section
	 * @param semester
	 * @param year
	 * @return The grade of the removed assignment.
	 * @throws SQLException
	 */
	public int removeGrade(
		int studentID,
		String assignment,
		String department,
		int number,
		int section,
		int semester,
		int year) throws SQLException
	{
		this.connection.rollback();

		final int classID = getClassID(department, number, section, semester, year)
			.orElseThrow(()->new SQLException("Tried to remove grade from class, but class does not exist"));

		this.deleteAssignment.setInt(1, classID);
		this.deleteAssignment.setInt(2, studentID);
		this.deleteAssignment.setString(3, assignment);
		final var delAssRes = this.deleteAssignment.executeQuery();
		if(!delAssRes.next())
		{
			throw new SQLException("Tried to remove grade from class, but assignment does not exist");
		}
		final int grade = delAssRes.getInt(1);
		delAssRes.close();
		this.connection.commit();
		return grade;
	}
}