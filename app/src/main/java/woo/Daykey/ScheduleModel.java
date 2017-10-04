package woo.Daykey;

class ScheduleModel implements java.io.Serializable {

	private int num;
	private String name;
	private int grade;
	private int class_;
	private int password;
	private String year;
	private String month;
	private String date;
	private String sche;

	ScheduleModel() {
	}

	public ScheduleModel(int num, String name, int grade, int class_, int password, String year,
						 String month, String date, String sche) {
		this.num = num;
		this.name = name;
		this.grade = grade;
		this.class_ = class_;
		this.password = password;
		this.year = year;
		this.month = month;
		this.date = date;
		this.sche = sche;
	}

	public int getNum() {
		return this.num;
	}

	public void setNum(int num) {
		this.num = num;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getGrade() {
		return this.grade;
	}

	public void setGrade(int grade) {
		this.grade = grade;
	}

	public int getClass_() {
		return this.class_;
	}

	public void setClass_(int class_) {
		this.class_ = class_;
	}

	public int getPassword() {
		return this.password;
	}

	public void setPassword(int password) {
		this.password = password;
	}

	public String getDate() {
		return this.date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getSche() {
		return this.sche;
	}

	public void setSche(String sche) {
		this.sche = sche;
	}

	public String getYear() {
		return this.year;
	}

	public void setYear(String year) {
		this.year = year;
	}

	public String getMonth() {
		return this.month;
	}

	public void setMonth(String month) {
		this.month = month;
	}
}
