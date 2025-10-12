<<<<<<< HEAD
# Architecture Overview

## System Design

Using layered architecture, we separated the initial main into six separate packages. Each has one responsibility respectively and communicates only with nearby layers.

### App
-The app package shows a menu and get user inputs. Requests from the service packaged are passed and displays results.

### Service
-The service package checks for full course and waitlists, as well as validating student and course information and preventing duplicate entries. Service will communicate with the repos package and use the models package for students and courses.


### Repo
-The repo package reads and writes data. With three interfaces, StudentRepository, CourseRepository, and EnrollmentRepository, the CSVRepos, CsvStudentRepository, CsvCourseRepository, and CsvEnrollmentRepository, store data in CSV files. 

### Model
-The model package has Student and Course classes that acts as data containers with getters and setters. 

### Util
-The util package is some helper classes used across the app. The Validator will check to see if inputs are valid as Banner ID format and email. AppLogger will write diagnostic messages.

### Exception
The **exception** package has ValidationException to check for bad inputs and EnrollmentException for enrollment problems.

## How it flows

-When a student gets enrolled, Main will get the student ID and the course code from user input. Main looks Registration service to check if both the student and course exists, looking to see if they aren't enrolled or if the course is full. If there is space, the student is added to the roster of the course. If its full, they get added to a waitlist. Service will return a message and Main will show it to the user. After the user exits, the repo will save it all CSV files.
