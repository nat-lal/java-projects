
public class Course implements Comparable<Course> {
	public String courseID;
	public String courseName;
	public int startTime;
	public int endTime;
	public String professor;
	public double rating;
	public String day;
	
	public Course(String courseID, String courseName, int startTime, int endTime, String professor, double rating, String day) {
		this.courseID = courseID;
		this.courseName = courseName;
		this.startTime = startTime;
		this.endTime = endTime;
		this.professor = professor;
		this.rating = rating;
		this.day = day;
	}
	
	@Override 
	public int compareTo(Course o)
    {
        if (this.rating > o.rating) 
        	return 1;
        if (this.rating < o.rating)
        	return -1;
        return this.courseID.compareTo(o.courseID);
    }
}
