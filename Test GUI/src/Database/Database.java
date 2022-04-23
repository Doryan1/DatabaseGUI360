package Database;
import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;
import javax.swing.table.*;

import Database.Managers.AdminManager;
import Database.Managers.AssignmentManager;
import Database.Managers.ClassManager;
import Database.Managers.ClassProfessorManager;
import Database.Managers.ClassTAManager;
import Database.Managers.EmployeeManager;
import Database.Managers.PersonManager;
import Database.Managers.ProfessorManager;
import Database.Managers.StudentManager;
import Database.Managers.TAManager;

/**
 * Class that handles connecting to and manipulating the database.
 * Requires a SQLite compatible database driver.
 */
public class Database
{
	private final Connection connection;

	private final PersonManager pm;
	private final StudentManager sm;
	private final EmployeeManager em;
	private final ProfessorManager prm;
	private final TAManager tam;
	private final AdminManager am;
	private final ClassManager cm;
	private final ClassProfessorManager cpm;
	private final ClassTAManager ctm;
	private final AssignmentManager asm;

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

		this.pm = new PersonManager(connection);
		this.sm = new StudentManager(connection);
		this.em = new EmployeeManager(connection);
		this.prm = new ProfessorManager(connection);
		this.tam = new TAManager(connection);
		this.am = new AdminManager(connection);
		this.cm = new ClassManager(connection);
		this.cpm = new ClassProfessorManager(connection);
		this.ctm = new ClassTAManager(connection);
		this.asm = new AssignmentManager(connection);
	}

	private static TableModel makeTableModel(List<HashMap<String, Object>> objects) throws SQLException
	{
		final Object[] columns = objects.get(0).keySet().toArray();
		final Object[][] data = new Object[objects.size()][columns.length];
		for(int i=0; i<objects.size(); i+=1)
		{
			final var o = objects.get(i);
			final var r = new ArrayList<>(columns.length);
			for(var column: o.keySet())
			{
				r.add(o.get(column));
			}
			data[i] = r.toArray();
		}
		return new DefaultTableModel(data, columns);
	}

	public static String letterGrade(int score)
	{
		// If only java had pattern matching and ranges
        if(score>=90) {
            return "A";
        } else if (score<90 && score>=85) {
            return "B+";
        } else if (score<85 && score>=80) {
            return "B";
        } else if (score<80 && score>=75) {
            return "C+";
        } else if (score<75 && score>=70) {
            return "C";
        } else if (score<70 && score>=65) {
            return "D+";
        } else if (score<65 && score>=60) {
            return "D";
        } else {
            return "F";
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
		final var pRes = this.pm.selectPerson(id);
		if(!pRes.isPresent()) {return Optional.empty();}
		final var person = pRes.get();
		final var isStudent = this.sm.selectStudent(id).isPresent();
		final var isEmployee = this.em.selectEmployee(id).isPresent();
		final var isProfessor = this.prm.selectProfessor(id).isPresent();
		final var isTA = this.tam.selectTA(id).isPresent();
		final var isAdmin = this.am.selectAdmin(id).isPresent();

		return Optional.of(new LoginResult(
			id,
			person.getFirstName(),
			person.getLastName(),
			isStudent,
			isEmployee,
			isProfessor,
			isTA,
			isAdmin));
	}

	public void addStaff(
		int id,
		String fName,
		String lName,
		String bDate,
		String dept) throws SQLException
	{
		this.connection.rollback();
		this.pm.insertPerson(id, fName, lName, bDate);
		this.em.insertEmployee(id, dept);
		this.connection.commit();
	}

	public TableModel listStaff() throws SQLException
	{
		this.connection.rollback();

		final var getStaffRes = this.em.selectEmployees();
		return makeTableModel(getStaffRes);
	}

	public Employee updateStaff(
		int id,
		String fName,
		String lName,
		String bDate,
		String dept) throws SQLException
	{
		this.connection.rollback();
		final var old = this.em.selectEmployee(id)
			.orElseThrow(()->new SQLException("Tried to update staff, but staff does not exist."));
		this.pm.updatePerson(id, fName, lName, bDate);
		this.em.updateEmployee(id, dept);
		this.connection.commit();
		return old;
	}

	public Employee removeStaff(int id) throws SQLException
	{
		this.connection.rollback();
		final var old = this.em.selectEmployee(id)
			.orElseThrow(()->new SQLException("Tried to remove staff, but staff does not exist"));
		this.em.deleteEmployee(id);
		this.pm.deletePerson(id);
		this.connection.commit();
		return old;
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
		String fName,
		String lName,
		String bDate,
		String dept,
		String teaching,
		String taught) throws SQLException
	{
		this.connection.rollback();
		this.pm.insertPerson(id, fName, lName, bDate);
		this.em.insertEmployee(id, dept);
		this.prm.insertProfessor(id);
		for(String c: teaching.split("\\s"))
		{
			String[] s = c.split("-");
			if(s.length < 2) continue;
			String classDept = s[0];
			int course = Integer.parseInt(s[1]);
			final var classID = this.cm.selectOrInsertClassID(classDept, course, 0, 0, 0);
			this.cpm.insertClassProfessor(id, classID, true);
		}
		for(String c: taught.split("\\s"))
		{
			String[] s = c.split("-");
			if(s.length < 2) continue;
			String classDept = s[0];
			int course = Integer.parseInt(s[1]);
			final var classID = this.cm.selectOrInsertClassID(classDept, course, 0, 0, 0);
			this.cpm.insertClassProfessor(id, classID, false);
		}
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

		final var professors = this.prm.selectProfessors();
		for(var professor: professors)
		{
			final var courses = this.cpm
				.selectProfessorClasses((int)professor.get("Professor ID"));
			final var teaching = courses
				.stream()
				.filter(c->(boolean)c.get("Actively Teaching"))
				.map(c->(String)c.get("Department")+"-"+(int)c.get("Course Number"))
				.toList();
			professor.put("Teaching", String.join(" ", teaching));

			final var taught = courses
				.stream()
				.filter(c->!(boolean)c.get("Actively Teaching"))
				.map(c->(String)c.get("Department")+"-"+(int)c.get("Course Number"))
				.toList();
			professor.put("Taught", String.join(" ", taught));

			final var tas = courses
				.stream()
				.filter(c->(boolean)c.get("Actively Teaching"))
				// Get class IDs
				.map(c->{
					try{
						return this.cm.selectClassID(
							(String)c.get("Department"),
							(int)c.get("Course Number"),
							0,
							0,
							0);
					} catch (SQLException e) {
						e.printStackTrace();
						return OptionalInt.empty();}})
				.flatMapToInt(OptionalInt::stream)
				// get TAs for class
				.mapToObj(c->{
					try {
						return this.ctm.selectClassTAs(c);
					} catch (SQLException e) {
						e.printStackTrace();
						return new ArrayList<Integer>();
					}})
				.flatMap(t->t.stream())
				.map(t->String.valueOf(t))
				.collect(Collectors.joining(" "));
			professor.put("Teacher Assistants", tas);
		}
		return makeTableModel(professors);
	}

	private List<String> getProfessorTeaching(int profID) throws SQLException
	{
		return this.cpm.selectProfessorClasses(profID)
			.stream()
			.filter(c->(boolean)c.get("Actively Teaching"))
			.map(c->(String)c.get("Department")+"-"+(int)c.get("Course Number"))
			.toList();
	}

	private List<String> getProfessorTaught(int profID) throws SQLException
	{
		return this.cpm.selectProfessorClasses(profID)
			.stream()
			.filter(c->!(boolean)c.get("Actively Teaching"))
			.map(c->(String)c.get("Department")+"-"+(int)c.get("Course Number"))
			.toList();
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
		String fName,
		String lName,
		String bDate,
		String dept,
		String cTeaching,
		String cTaught) throws SQLException
	{
		this.connection.rollback();
		final Professor old = this.prm.selectProfessor(id)
			.orElseThrow(()->new SQLException("Tried to update professor, but professor does not exist."));

		// Yes, this is terrible. No, I don't care.
		old.teachingClasses = this.getProfessorTeaching(id)
			.stream()
			.map(c->new Klass(c))
			.toList();
		for(var klass: old.teachingClasses)
		{
			final var classID = this.cm
				.selectClassID(klass.department, klass.number, 0, 0, 0)
				.getAsInt();
			this.cpm.deleteClassProfessor(id, classID);
		}
		for(var klass: cTeaching.split(" "))
		{
			if(klass.isEmpty()) continue;
			var k = new Klass(klass);
			var classID = this.cm
				.selectClassID(k.department, k.number, 0, 0, 0);
			if (classID.isEmpty())
			{
				this.cm.insertClass(k.department, k.number, 0, 0, 0);
				classID = this.cm
					.selectClassID(k.department, k.number, 0, 0, 0);
			}
			this.cpm.insertClassProfessor(id, classID.getAsInt(), true);
		}

		old.taughtClasses = this.getProfessorTaught(id)
			.stream()
			.map(c->new Klass(c))
			.toList();
		for(var klass: old.taughtClasses)
		{
			final var classID = this.cm
				.selectClassID(klass.department, klass.number, 0, 0, 0)
				.getAsInt();
			this.cpm.deleteClassProfessor(id, classID);
		}
		for(var klass: cTaught.split(" "))
		{
			if(klass.isEmpty()) continue;
			var k = new Klass(klass);
			var classID = this.cm
				.selectClassID(k.department, k.number, 0, 0, 0);
			if (classID.isEmpty())
			{
				this.cm.insertClass(k.department, k.number, 0, 0, 0);
				classID = this.cm
					.selectClassID(k.department, k.number, 0, 0, 0);
			}
			this.cpm.insertClassProfessor(id, classID.getAsInt(), false);
		}

		this.pm.updatePerson(id, fName, lName, bDate);
		this.em.updateEmployee(id, dept);

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
		final var old = this.prm.selectProfessor(id)
			.orElseThrow(()->new SQLException("Tried to remove professor, but professor does not exist"));
		for(var klass: this.cpm.selectProfessorClasses(id))
		{
			var classID = this.cm.selectClassID(
				(String)klass.get("Department"),
				(int)klass.get("Course Number"),
				(int)klass.get("Course Section"),
				(int)klass.get("Semester"),
				(int)klass.get("Year"))
				.orElse(-1);
			this.cpm.deleteClassProfessor(id, classID);
		}
		this.prm.deleteProfessor(id);
		this.em.deleteEmployee(id);
		this.pm.deletePerson(id);
		this.connection.commit();
		return old;
	}

	private List<String> getTATeaching(int TAID) throws SQLException
	{
		return this.ctm.selectTAClasses(TAID)
			.stream()
			.filter(c->(boolean)c.get("Actively Teaching"))
			.map(c->(String)c.get("Department")+"-"+(int)c.get("Course Number"))
			.toList();
	}

	private List<String> getTATaught(int TAID) throws SQLException
	{
		return this.ctm.selectTAClasses(TAID)
			.stream()
			.filter(c->!(boolean)c.get("Actively Teaching"))
			.map(c->(String)c.get("Department")+"-"+(int)c.get("Course Number"))
			.toList();
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
		String fName,
		String lName,
		String bDate,
		String dept,
		String assisting,
		String assisted) throws SQLException
	{
		this.connection.rollback();
		this.pm.insertPerson(id, fName, lName, bDate);
		this.em.insertEmployee(id, dept);
		this.tam.insertTA(id);
		for(String c: assisting.split("\\s"))
		{
			String[] s = c.split("-");
			if(s.length < 2) continue;
			String classDept = s[0];
			int course = Integer.parseInt(s[1]);
			final var classID = this.cm.selectOrInsertClassID(classDept, course, 0, 0, 0);
			this.ctm.insertClassTA(id, classID, true);
		}
		for(String c: assisted.split("\\s"))
		{
			String[] s = c.split("-");
			if(s.length < 2) continue;
			String classDept = s[0];
			int course = Integer.parseInt(s[1]);
			final var classID = this.cm.selectOrInsertClassID(classDept, course, 0, 0, 0);
			this.ctm.insertClassTA(id, classID, false);
		}
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
		final var tas = this.tam.selectTAs();
		for(var ta: tas)
		{
			final var courses = this.ctm
				.selectTAClasses((int)ta.get("TA ID"));
			final var teaching = courses
				.stream()
				.filter(c->(boolean)c.get("Actively Teaching"))
				.map(c->(String)c.get("Department")+"-"+(int)c.get("Course Number"))
				.toList();
			ta.put("Teaching", String.join(" ", teaching));
			final var taught = courses
			.stream()
			.filter(c->!(boolean)c.get("Actively Teaching"))
			.map(c->(String)c.get("Department")+"-"+(int)c.get("Course Number"))
			.toList();
			ta.put("Taught", String.join(" ", taught));

			final var professors = courses
				.stream()
				.filter(c->(boolean)c.get("Actively Teaching"))
				// Get class IDs
				.map(c->{
					try{
						return this.cm.selectClassID(
							(String)c.get("Department"),
							(int)c.get("Course Number"),
							0,
							0,
							0);
					} catch (SQLException e) {
						e.printStackTrace();
						return OptionalInt.empty();}})
				.flatMapToInt(OptionalInt::stream)
				// get Professors for class
				.mapToObj(c->{
					try {
						return this.cpm.selectClassProfessors(c);
					} catch (SQLException e) {
						e.printStackTrace();
						return new ArrayList<Integer>();
					}})
				.flatMap(t->t.stream())
				.map(t->String.valueOf(t))
				.collect(Collectors.joining(" "));
			ta.put("Professors", professors);
		}
		return makeTableModel(tas);
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
	public TA updateTA(
		int id,
		String fName,
		String lName,
		String bDate,
		String dept,
		String cTeaching,
		String cTaught) throws SQLException
	{
		this.connection.rollback();
		final TA old = this.tam.selectTA(id)
			.orElseThrow(()->new SQLException("Tried to update professor, but professor does not exist."));

		// Yes, this is terrible. No, I don't care.
		old.teachingClasses = this.getTATeaching(id)
			.stream()
			.map(c->new Klass(c))
			.toList();
		for(var klass: old.teachingClasses)
		{
			final var classID = this.cm
				.selectClassID(klass.department, klass.number, 0, 0, 0)
				.getAsInt();
			this.ctm.deleteClassTA(id, classID);
		}
		for(var klass: cTeaching.split(" "))
		{
			if(klass.isEmpty()) continue;
			var k = new Klass(klass);
			var classID = this.cm
				.selectClassID(k.department, k.number, 0, 0, 0);
			if (classID.isEmpty())
			{
				this.cm.insertClass(k.department, k.number, 0, 0, 0);
				classID = this.cm
					.selectClassID(k.department, k.number, 0, 0, 0);
			}
			this.ctm.insertClassTA(id, classID.getAsInt(), true);
		}

		old.taughtClasses = this.getTATaught(id)
			.stream()
			.map(c->new Klass(c))
			.toList();
		for(var klass: old.taughtClasses)
		{
			final var classID = this.cm
				.selectClassID(klass.department, klass.number, 0, 0, 0)
				.getAsInt();
			this.ctm.deleteClassTA(id, classID);
		}
		for(var klass: cTaught.split(" "))
		{
			if(klass.isEmpty()) continue;
			var k = new Klass(klass);
			var classID = this.cm
				.selectClassID(k.department, k.number, 0, 0, 0);
			if (classID.isEmpty())
			{
				this.cm.insertClass(k.department, k.number, 0, 0, 0);
				classID = this.cm
					.selectClassID(k.department, k.number, 0, 0, 0);
			}
			this.ctm.insertClassTA(id, classID.getAsInt(), false);
		}

		this.pm.updatePerson(id, fName, lName, bDate);
		this.em.updateEmployee(id, dept);
		this.connection.commit();
		return old;
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
		final var old = this.tam.selectTA(id)
			.orElseThrow(()->new SQLException("Tried to update professor, but professor does not exist."));
		this.tam.deleteTA(id);
		this.em.deleteEmployee(id);
		this.pm.deletePerson(id);
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
		if(this.am.insertAdmin(employeeID) == 0)
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
		return makeTableModel(this.am.selectAdmins());
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
		if(this.am.deleteAdmin(employeeID) == 0)
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
		String fName,
		String lName,
		String bDate) throws SQLException
	{
		this.connection.rollback();
		this.pm.insertPerson(id, fName, lName, bDate);
		this.sm.insertStudent(id);
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
		return makeTableModel(this.sm.selectStudents());
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


		final var old = this.sm.selectStudent(id)
			.orElseThrow(()->new SQLException("Tried to remove student, but student does not exist"));
		this.sm.deleteStudent(id);
		this.pm.deletePerson(id);
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
	public int addClass(
		String department,
		int number,
		int section,
		int semester,
		int year) throws SQLException
	{
		this.connection.rollback();
		this.cm.insertClass(department, number, section, semester, year);
		return this.cm.selectClassID(department, number, section, semester, year)
			.orElseThrow(()->new SQLException("Failed to create class"));
	}

	private int insertClass(
		String department,
		int number,
		int section,
		int semester,
		int year) throws SQLException
	{
		this.cm.insertClass(department, number, section, semester, year);

		return this.cm.selectClassID(department, number, section, semester, year)
			.orElseThrow(()->new SQLException("Class does not exist"));
	}

	/**
	 * Lists every class currently in the database.
	 * @return A List of Classes
	 * @throws SQLException
	 */
	public TableModel listClasses() throws SQLException
	{
		this.connection.rollback();
		return makeTableModel(this.cm.selectClasses());
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
		var id = this.cm.selectClassID(department, number, section, semester, year)
			.orElseThrow(()->new SQLException("Tried to remove class, but class does not exist"));
		this.cm.deleteClass(id);
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
	 * @param active Is the professor currently teaching this class
	 * @throws SQLException
	 */
	public void addProfessorToClass(
		int professorID,
		String department,
		int number,
		int section,
		int semester,
		int year,
		boolean active) throws SQLException
	{
		this.connection.rollback();

		final var classID = this.cm.selectClassID(department, number, section, semester, year)
			.orElseGet(()->
			{
				try {
					this.cm.insertClass(department, number, section, semester, year);
					return this.cm.selectClassID(department, number, section, semester, year)
						.orElse(-1);
				} catch (SQLException e) {
					return -1;
				}
			});


		if(this.cpm.insertClassProfessor(professorID, classID, active) == 0)
		{
			throw new SQLException("Tried to add Professor to class, but class does not exist");
		}

		this.connection.commit();
	}

	public boolean isProfessorCurrentlyTeachingClass(int profID,
	String department,
	int number,
	int section,
	int semester,
	int year) throws SQLException
{
	final var classID = this.cm.selectClassID(department, number, section, semester, year)
		.orElseThrow(()->new SQLException("Class does not exist"));

	return this.cpm.selectClassProfessor(profID, classID)
		.orElse(false);
}

	public TableModel listProfessorClasses(int professorID) throws SQLException
	{
		this.connection.rollback();
		return makeTableModel(this.cpm.selectProfessorClasses(professorID));
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
		int professorID,
		String department,
		int number,
		int section,
		int semester,
		int year) throws SQLException
	{
		this.connection.rollback();

		final var classID = this.cm.selectClassID(department, number, section, semester, year)
			.orElseThrow(()->new SQLException("Tried to remove professor from class, but class does not exist"));

		if(this.cpm.deleteClassProfessor(professorID, classID) == 0)
		{
			throw new SQLException("Tried to remove Professor from class, but professor not assigned to class");
		}
		this.connection.commit();
	}

	/**
	 * Assigns the TA with the given ID to the given class.
	 * @param TAID
	 * @param department
	 * @param number
	 * @param section
	 * @param semester
	 * @param year
	 * @param active Is the TA currently teaching this class
	 * @throws SQLException
	 */
	public void addTAToClass(
		int TAID,
		String department,
		int number,
		int section,
		int semester,
		int year,
		boolean active) throws SQLException
	{
		this.connection.rollback();

		final var classID = this.cm.selectClassID(department, number, section, semester, year)
			.orElse(this.insertClass(department, number, section, semester, year));
		if(this.ctm.insertClassTA(TAID, classID, active) == 0)
		{
			throw new SQLException("Tried to add TA to class, but class does not exist");
		}
		this.connection.commit();
	}

	public boolean isTACurrentlyTeachingClass(int TAID,
		String department,
		int number,
		int section,
		int semester,
		int year) throws SQLException
	{
		final var classID = this.cm.selectClassID(department, number, section, semester, year)
			.orElseThrow(()->new SQLException("Class does not exist"));

		return this.ctm.selectClassTA(TAID, classID)
			.orElse(false);
	}

	public TableModel listTAClasses(int TAID) throws SQLException
	{
		this.connection.rollback();
		return makeTableModel(this.ctm.selectTAClasses(TAID));
	}

	/**
	 * Un-assigns the TA with the given ID from the given class.
	 * @param TAID
	 * @param department
	 * @param number
	 * @param section
	 * @param semester
	 * @param year
	 * @throws SQLException
	 */
	public void removeTAFromClass(
		int TAID,
		String department,
		int number,
		int section,
		int semester,
		int year) throws SQLException
	{
		this.connection.rollback();

		final var classID = this.cm.selectClassID(department, number, section, semester, year)
			.orElseThrow(()->new SQLException("Tried to remove TA from class, but class does not exist"));
		if(this.ctm.deleteClassTA(TAID, classID) == 0)
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

		final int classID = this.cm.selectClassID(department, number, section, semester, year)
			.orElseThrow(()->new SQLException("Tried to add grade to class, but class does not exist"));
		this.asm.insertAssignment(classID, studentID, assignment, grade);
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
		final int classID = this.cm.selectClassID(department, number, section, semester, year)
			.orElseThrow(()->new SQLException("Tried to get grade for class, but class does not exist"));
		return this.asm.selectAssignment(classID, studentID, assignment)
			.orElseThrow(()->new SQLException("Tried to get grade for assignment, but assignment does not exist"));
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
		return makeTableModel(this.asm.selectStudentAssignments(studentID));
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

		final var classID = this.cm.selectClassID(department, number, section, semester, year)
			.orElseThrow(()->new SQLException("Class does not exist"));
		final var classes = this.asm.selectClassAssignments(classID);
		for(var klass: classes)
		{
			klass.put("Department", department);
			klass.put("Course Number", number);
			klass.put("Course Section", section);
			klass.put("Semester", semester);
			klass.put("Year", year);
		}
		return makeTableModel(classes);
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

		final int classID = this.cm.selectClassID(department, number, section, semester, year)
			.orElseThrow(()->new SQLException("Tried to get grades for class, but class does not exist"));
		final var assignments = this.asm.selectStudentClassAssignments(classID, studentID);
		for(var assignment: assignments)
		{
			assignment.put("Student ID", studentID);
			assignment.put("Department",department);
			assignment.put("Course Number", number);
			assignment.put("Course Section", section);
			assignment.put("Semester", semester);
			assignment.put("Year", year);
		}
		return makeTableModel(assignments);
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
		final int classID = this.cm.selectClassID(department, number, section, semester, year)
			.orElseThrow(()->new SQLException("Tried to update grades for class, but class does not exist"));
		if (this.asm.updateAssignment(classID, studentID, assignment, grade) == 0)
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

		final int classID = this.cm.selectClassID(department, number, section, semester, year)
			.orElseThrow(()->new SQLException("Tried to remove grade from class, but class does not exist"));

		final int grade = this.asm.selectAssignment(classID, studentID, assignment)
			.orElseThrow(()->new SQLException("Tried to remove grade from class, but assignment does not exist"));
		if(this.asm.deleteAssignment(classID, studentID, assignment) == 0)
		{
			throw new SQLException("Tried to remove grade from class, but assignment does not exist");
		}
		this.connection.commit();
		return grade;
	}

	public void close() throws SQLException
	{
		this.connection.close();
	}
}