public class Person {
	private final int id;
	private String firstName;
	private String lastName;
	private String birthDate;
	
	public Person(int id, String firstName, String lastName, String birthDate)
	{
		this.id = id;
		this.firstName = firstName;
		this.lastName = lastName;
		this.birthDate = birthDate;
	}

	public int getId() {return this.id;}
	public String getFirstName() {return firstName;}
	public String getLastName() {return this.lastName;}
	public String getBirthDate() {return this.birthDate;}
	public void setFirstName(String firstName) {this.firstName = firstName;}
	public void setLastName(String lastName) {this.lastName = lastName;}
	public void setBirthDate(String birthDate) {this.birthDate = birthDate;}
}
