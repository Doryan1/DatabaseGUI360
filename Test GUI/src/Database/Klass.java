package Database;
public class Klass
{
	String department;
	int number;
	int section = 0;
	int semester = 0;
	int year = 0;

	public Klass(String department, int number, int section, int semester, int year)
	{
		this.department = department;
		this.number = number;
		this.section = section;
		this.semester = semester;
		this.year = year;
	}

	public Klass(String plain)
	{
		var split = plain.split("-");
		this.department = split[0];
		this.number = Integer.parseInt(split[1]);
	}
}