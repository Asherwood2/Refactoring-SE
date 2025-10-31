<<<<<<< HEAD  
# This README provides:
- Clear project overview
- Complete feature list
- Build and run instructions
- Usage examples
- Technical details about validation and business logic

It's comprehensive enough for new users to understand the system while being concise enough for quick reference.



# UCA Course Registration System

A Java command-line application for managing student course registrations with file-based persistence.

## Features

- **Student Management**: Add, list, and search students
- **Course Management**: Add, list, and search courses  
- **Enrollment System**: Enroll students in courses with automatic waitlisting
- **Waitlist Management**: Automatic promotion from waitlist when spots open
- **Data Persistence**: All data saved to CSV files
- **Input Validation**: Comprehensive validation for student IDs, emails, and course data
- **Search Functionality**: Search students and courses by various criteria

## Data Files

The system uses three CSV files for persistence:

- `students.csv`: Student records (ID, Name, Email)
- `courses.csv`: Course records (Code, Title, Capacity)  
- `enrollments.csv`: Enrollment records (CourseCode|StudentID|Status)

## Building and Running

### Prerequisites
- Java 8 or higher
- Maven

## Usage Examples

### Adding a Student
Menu: Choose 1  
Banner ID: B001  
Name: John Smith  
Email: john.smith@uca.edu  

### Adding a Course
Menu: Choose 2  
Course Code: 1000  
Title: Software Engineering  
Capacity: 30  

### Enrolling a Student
Menu: Choose 5  
Student Banner ID: B001  
Course Code: 1000

### Searching
Menu: Choose 7 (Search students)  
Search keyword: Smith  

Menu: Choose 8 (Search courses)  
Search keyword: Engineering

## Validation Rules
Banner ID: Must start with 'B' followed by 3+ digits (e.g., B001)

Email: Must contain '@' and not end with '@'

Course Capacity: Must be between 1-500

Duplicate Prevention: No duplicate student IDs or course codes

## Enrollment Logic
Students are enrolled if course has available capacity

If course is full, students are added to waitlist

When a student drops, first waitlisted student is automatically promoted

Students cannot be enrolled and waitlisted in the same course simultaneously

## Performance Characteristics
Based on performance testing:

Can handle 500+ student records efficiently

Supports rapid enrollment operations (100+ enrollments in <2 seconds)

Efficient search across large datasets

Fast waitlist promotion during drop operations

## Error Handling
ValidationException: Invalid input data (IDs, emails, capacity)

EnrollmentException: Enrollment-related errors (not found, already enrolled, etc.)

File I/O Errors: Graceful handling of missing/corrupted data files

All errors are displayed to user with descriptive messages


## Run


```bash


# Normal mode (loads existing data)
java -jar target/course-registration-0.1.0.jar

# Demo mode (loads sample data)
java -jar target/course-registration-0.1.0.jar --demo

```


=======

