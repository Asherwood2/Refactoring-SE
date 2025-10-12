<<<<<<< HEAD


# UCA Course Registration – Baseline (for Refactoring Assignment)





This is a runnable but intentionally messy Java CLI app.  


Your assignment: **Refactor** into clean layers (model, repo, service, ui).  


Preserve existing behaviors: students, courses, enrollments, waitlists, drops.





## Run


```bash


mvn -q -DskipTests package


java -jar target/course-registration-0.1.0.jar


```


=======


# Refactoring


Repo for refactoring a messy but runnable Java CLI app.


>>>>>>> aff3bdf62f017414204536e1400da5d1b24d6f68

## How it flows

-When a student gets enrolled, Main will get the student ID and the course code from user input. Main looks Registration service to check if both the student and course exists, looking to see if they aren't enrolled or if the course is full. If there is space, the student is added to the roster of the course. If its full, they get added to a waitlist. Service will return a message and Main will show it to the user. After the user exits, the repo will save it all CSV files.
