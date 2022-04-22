package Database;
import java.util.List;

public class TA extends Employee
{
	List<Klass> teachingClasses;
	List<Klass> taughtClasses;

	public TA(int id, String firstName, String lastName, String birthDate, String department)
	{
		super(id, firstName, lastName, birthDate, department);
	}
	public TA(int id, String firstName, String lastName, String birthDate, String department, List<Klass> teachingClasses, List<Klass> taughtClasses)
	{
		super(id, firstName, lastName, birthDate, department);
		this.teachingClasses = teachingClasses;
		this.taughtClasses = taughtClasses;
	}
}