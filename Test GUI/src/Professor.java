import java.util.List;

public class Professor extends Employee
{
	List<Class> teachingClasses;
	List<Class> taughtClasses;

	public Professor(int id, String firstName, String lastName, String birthDate, String department)
	{
		super(id, firstName, lastName, birthDate, department);
	}
	public Professor(int id, String firstName, String lastName, String birthDate, String department, List<Class> teachingClasses, List<Class> taughtClasses)
	{
		super(id, firstName, lastName, birthDate, department);
		this.teachingClasses = teachingClasses;
		this.taughtClasses = taughtClasses;
	}
}