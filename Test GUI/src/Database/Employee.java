package Database;
public class Employee extends Person
{
	private String department;

	public Employee(int id, String firstName, String lastName, String birthDate, String department)
	{
		super(id, firstName, lastName, birthDate);
		this.department = department;
	}

	public String getDepartment() {return this.department;}
	public void setDepartment(String department) {this.department = department;}
}