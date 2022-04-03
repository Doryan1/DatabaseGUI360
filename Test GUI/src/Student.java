import java.util.List;

public class Student extends Person
{
	List<Class> classes;

	public Student(int id, String firstName, String lastName, String birthDate)
	{
		super(id, firstName, lastName, birthDate);
	}
	
	public Student(int id, String firstName, String lastName, String birthDate, List<Class> classes)
	{
		super(id, firstName, lastName, birthDate);
		this.classes = classes;
	}
}