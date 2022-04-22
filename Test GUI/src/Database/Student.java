package Database;
import java.util.List;

public class Student extends Person
{
	List<Klass> classes;

	public Student(int id, String firstName, String lastName, String birthDate)
	{
		super(id, firstName, lastName, birthDate);
	}
	
	public Student(int id, String firstName, String lastName, String birthDate, List<Klass> classes)
	{
		super(id, firstName, lastName, birthDate);
		this.classes = classes;
	}
}