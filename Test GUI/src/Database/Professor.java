package Database;
import java.util.List;

public class Professor extends Employee
{
	List<Klass> teachingClasses;
	List<Klass> taughtClasses;

	public Professor(int id, String firstName, String lastName, String birthDate, String department)
	{
		super(id, firstName, lastName, birthDate, department);
	}
	public Professor(int id, String firstName, String lastName, String birthDate, String department, List<Klass> teachingClasses, List<Klass> taughtClasses)
	{
		super(id, firstName, lastName, birthDate, department);
		this.teachingClasses = teachingClasses;
		this.taughtClasses = taughtClasses;
	}
}