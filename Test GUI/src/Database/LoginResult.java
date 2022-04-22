package Database;

public record LoginResult(
	int id,
	String fName,
	String lName,
	boolean isStudent,
	boolean isEmployee,
	boolean isProfessor,
	boolean isTa,
	boolean isAdmin){}