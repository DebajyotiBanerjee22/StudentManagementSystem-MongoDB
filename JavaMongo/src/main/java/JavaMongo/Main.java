package JavaMongo;

import java.util.Scanner;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        StudentMongoDAO studentDAO = new StudentMongoDAO();
        DepartmentMongoDAO departmentDAO = new DepartmentMongoDAO();
        StreamMongoDAO streamDAO = new StreamMongoDAO();
        SubjectMongoDAO subjectDAO = new SubjectMongoDAO();
        StudentSubjectMongoDAO studentSubjectDAO = new StudentSubjectMongoDAO();
        ContactMongoDAO contactDAO = new ContactMongoDAO();

        while (true) {
            System.out.println("\n===== Student Management System (MongoDB) =====");
            System.out.println("1. Add Department");
            System.out.println("2. Add Stream");
            System.out.println("3. Add Subject");
            System.out.println("4. Add Student with Contact");
            System.out.println("5. View Student Details by Roll No");
            System.out.println("6. View All Students");
            System.out.println("7. Update Student Contact");
            System.out.println("8. Delete Student by Roll No (Cascading)");
            System.out.println("9. Assign Subject to Student");
            System.out.println("10. Exit");
            System.out.print("Enter your choice: ");
            int choice;
            try {
                System.out.print("Enter your choice: ");
                choice = sc.nextInt();
                sc.nextLine();
            } catch (java.util.InputMismatchException e) {
                System.out.println("Invalid input! Please enter a number.");
                sc.nextLine(); 
                choice = -1; 
            }

            switch (choice) {
                case 1 : {
                    System.out.print("Enter Department ID (e.g., 101): ");
                    int deptId = sc.nextInt();
                    sc.nextLine(); 
                    System.out.print("Enter Department Name: ");
                    String deptName = sc.nextLine();
                    if (departmentDAO.addDepartment(new Department(deptId, deptName))) {
                        System.out.println("Department added successfully!");
                    } else {
                        System.out.println("Failed to add department. It might already exist or an error occurred.");
                    }
                }break;
                case 2 : {
                    System.out.print("Enter Stream ID (e.g., 1): ");
                    int streamId = sc.nextInt();
                    sc.nextLine();
                    System.out.print("Enter Stream ID (e.g., Software Engineering): ");
                    String streamName = sc.nextLine();
                    System.out.print("Enter Department ID for the Stream: (e.g., 101) ");
                    int deptId = sc.nextInt();
                    sc.nextLine();
                    if (streamDAO.addStream(new Stream(streamId, streamName, deptId))) {
                        System.out.println("Stream added successfully!");
                    } else {
                        System.out.println("Failed to add stream. It might already exist or an error occurred.");
                    }
                }break;
                case 3 : {
                    System.out.print("Enter Subject ID (e.g., 201): ");
                    int subjectId = sc.nextInt();
                    sc.nextLine(); 
                    System.out.print("Enter Subject Name (e.g., Data Structures): ");
                    String subjectName = sc.nextLine();
                    if (subjectDAO.addSubject(new Subject(subjectId, subjectName))) {
                        System.out.println("Subject added successfully!");
                    } else {
                        System.out.println("Failed to add subject. It might already exist or an error occurred.");
                    }
                }break;
                case 4 : {
                    System.out.print("Enter Student Roll No (e.g., STU001): ");
                    String roll = sc.nextLine();
                    if (studentDAO.isStudentExists(roll)) {
                        System.out.println("A student with this Roll No already exists. Please choose 'Update' or a different Roll No.");
                        break; 
                    }

                    System.out.print("Enter Student Name (e.g., Alice Smith): ");
                    String name = sc.nextLine();
                    System.out.print("Enter Semester (e.g., 1,2,3): ");
                    int sem = sc.nextInt();
                    System.out.print("Enter Year of Admission (e.g., 2023): ");
                    int adm = sc.nextInt();
                    System.out.print("Enter Expected Passing Year (e.g., 2027): ");
                    int pass = sc.nextInt();
                    System.out.print("Enter Stream ID (e.g., 1 (Must match the stream ID you added earlier): ");
                    int streamId = sc.nextInt();
                    sc.nextLine();
                    if (!streamDAO.isStreamExists(streamId)) {
                        System.out.println("Error: Stream with ID " + streamId + " does not exist. Please add the stream first.");
                        break;
                    }

                    boolean studentAdded = studentDAO.addStudent(new Student(roll, name, sem, adm, pass, streamId));

                    System.out.print("Enter Contact Reg No (for this student)(e.g., CON001): ");
                    String reg = sc.nextLine();
                    System.out.print("Enter Phone (e.g., 1234567890): ");
                    String phone = sc.nextLine();
                    System.out.print("Enter Email (e.g., alice.s@example.com):");
                    String email = sc.nextLine();

                    boolean contactAdded = contactDAO.addContact(new Contact(roll, reg, phone, email));

                    if (studentAdded && contactAdded) {
                        System.out.println("Student and contact added successfully!");
                    } else {
                        System.out.println("Failed to add student or contact. Please check logs for details.");
                        // studentDAO.deleteStudent(roll);
                    }
                }break;
                case 5 : {
                    System.out.print("Enter Roll No to View (e.g., STU001): ");
                    String roll = sc.nextLine();
                    Student student = studentDAO.getStudentByRollNo(roll);
                    if (student != null) {
                        System.out.println("--- Student Details ---");
                        System.out.println("Roll No: " + student.getRollNo());
                        System.out.println("Name: " + student.getName());
                        System.out.println("Semester: " + student.getSemester());
                        System.out.println("Admission Year: " + student.getYearOfAdmission());
                        System.out.println("Expected Passing Year: " + student.getExpectedPassingYear());

                        Stream stream = streamDAO.getStreamById(student.getStreamId());
                        if (stream != null) {
                            System.out.println("Stream: " + stream.getStreamName() + " (ID: " + stream.getStreamId() + ")");
                            Department dept = departmentDAO.getDepartmentById(stream.getDeptId());
                            if (dept != null) {
                                System.out.println("Department: " + dept.getDeptName() + " (ID: " + dept.getDeptId() + ")");
                            } else {
                                System.out.println("Department details for Stream ID " + stream.getDeptId() + " not found.");
                            }
                        } else {
                            System.out.println("Stream details for Stream ID " + student.getStreamId() + " not found.");
                        }

                        Contact contact = contactDAO.getContactByRollNo(roll);
                        if (contact != null) {
                            System.out.println("--- Contact Details ---");
                            System.out.println("Registration No: " + contact.getRegNo());
                            System.out.println("Phone: " + contact.getPhone());
                            System.out.println("Email: " + contact.getEmail());
                        } else {
                            System.out.println("No contact details found for this student.");
                        }

                        List<StudentSubject> assignedSubjects = studentSubjectDAO.getSubjectsByRollNo(roll);
                        if (!assignedSubjects.isEmpty()) {
                            System.out.println("--- Assigned Subjects ---");
                            for (StudentSubject ss : assignedSubjects) {
                                Subject subject = subjectDAO.getSubjectById(ss.getSubjectId());
                                if (subject != null) {
                                    System.out.println("- " + subject.getSubjectName() + " (ID: " + subject.getSubjectId() + ")");
                                } else {
                                    System.out.println("- Unknown Subject (ID: " + ss.getSubjectId() + ")");
                                }
                            }
                        } else {
                            System.out.println("No subjects assigned to this student.");
                        }
                    } else {
                        System.out.println("Student not found.");
                    }
                }break;
                case 6 : {
                    System.out.println("\n=== All Students ===");
                    List<Student> allStudents = studentDAO.getAllStudents();
                    if (allStudents.isEmpty()) {
                        System.out.println("No students found in the database.");
                    } else {
                        for (Student stu : allStudents) {
                            System.out.println("------------------------");
                            System.out.println("Roll No: " + stu.getRollNo());
                            System.out.println("Name: " + stu.getName());
                            System.out.println("Semester: " + stu.getSemester());
                            System.out.println("Admission Year: " + stu.getYearOfAdmission());
                            System.out.println("Passing Year: " + stu.getExpectedPassingYear());

                            Stream stream = streamDAO.getStreamById(stu.getStreamId());
                            if (stream != null) {
                                System.out.println("Stream: " + stream.getStreamName());
                                Department dept = departmentDAO.getDepartmentById(stream.getDeptId());
                                if (dept != null) {
                                    System.out.println("Department: " + dept.getDeptName());
                                }
                            }

                            Contact con = contactDAO.getContactByRollNo(stu.getRollNo());
                            if (con != null) {
                                System.out.println("Phone: " + con.getPhone() + " | Email: " + con.getEmail());
                            }
                            List<StudentSubject> assignedSubjects = studentSubjectDAO.getSubjectsByRollNo(stu.getRollNo());
                            if (!assignedSubjects.isEmpty()) {
                                System.out.print("Subjects: ");
                                for (int i = 0; i < assignedSubjects.size(); i++) {
                                    Subject subject = subjectDAO.getSubjectById(assignedSubjects.get(i).getSubjectId());
                                    if (subject != null) {
                                        System.out.print(subject.getSubjectName() + (i < assignedSubjects.size() - 1 ? ", " : ""));
                                    }
                                }
                                System.out.println();
                            }
                        }
                        System.out.println("------------------------");
                    }
                }break;
                case 7 : {
                    System.out.print("Enter Roll No to Update Contact (e.g., STU001): ");
                    String roll = sc.nextLine();
                    if (!studentDAO.isStudentExists(roll)) {
                        System.out.println("No student found with Roll No: " + roll + ". Cannot update contact.");
                        break;
                    }
                    Contact existingContact = contactDAO.getContactByRollNo(roll);
                    if (existingContact == null) {
                        System.out.println("No contact details found for student Roll No: " + roll + ". Please add contact first (Option 4).");
                        break;
                    }
                    String newRegNo, newPhone, newEmail;
                    System.out.print("Enter New Reg No (current: " + existingContact.getRegNo() + ", leave blank to keep): ");
                    String inputReg = sc.nextLine();
                    newRegNo = inputReg.isEmpty() ? null : inputReg;
                    System.out.print("Enter New Phone (current: " + existingContact.getPhone() + ", leave blank to keep): ");
                    String inputPhone = sc.nextLine();
                    newPhone = inputPhone.isEmpty() ? null : inputPhone;

                    System.out.print("Enter New Email (current: " + existingContact.getEmail() + ", leave blank to keep): ");
                    String inputEmail = sc.nextLine();
                    newEmail = inputEmail.isEmpty() ? null : inputEmail;
                    boolean updated = contactDAO.updateContact(roll, newRegNo, newPhone, newEmail);
                    System.out.println(updated ? "Contact update process completed successfully!" : "Contact update failed or no changes were made.");
                    break;
                }
                case 8 : {
                    System.out.print("Enter Roll No to Delete (e.g., STU001): ");
                    String roll = sc.nextLine();
                    boolean deleted = studentDAO.deleteStudent(roll);
                    System.out.println(deleted ? "Student and associated records deleted successfully!" : "Student deletion failed. Student might not exist.");
                }break;
                case 9 : {
                    System.out.print("Enter Student Roll No (e.g., STU001): ");
                    String roll = sc.nextLine();
                    System.out.print("Enter Subject ID to assign (e.g., 201): ");
                    int subId = sc.nextInt();
                    sc.nextLine(); 
                    if (!studentDAO.isStudentExists(roll)) {
                        System.out.println("Error: Student with Roll No " + roll + " does not exist.");
                        break;
                    }
                    if (!subjectDAO.isSubjectExists(subId)) {
                        System.out.println("Error: Subject with ID " + subId + " does not exist.");
                        break;
                    }
                    if (studentSubjectDAO.isStudentSubjectExists(roll, subId)) {
                        System.out.println("Error: Student " + roll + " is already assigned to Subject " + subId + ".");
                        break;
                    }

                    boolean added = studentSubjectDAO.addStudentSubject(new StudentSubject(roll, subId));
                    System.out.println(added ? "Subject assigned to student successfully!" : "Failed to assign subject to student.");
                }break;
                case 10 : {
                    System.out.println("Exiting application. Goodbye!");
                    sc.close();
                    MongoConn.closeConnection();
                    System.exit(0);
                }
                default : System.out.println("Invalid choice! Please enter a number between 1 and 10.");
            }
        }
    }
}