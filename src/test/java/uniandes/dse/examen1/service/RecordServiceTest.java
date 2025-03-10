package uniandes.dse.examen1.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import jakarta.transaction.Transactional;
import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;
import uniandes.dse.examen1.entities.CourseEntity;
import uniandes.dse.examen1.entities.StudentEntity;
import uniandes.dse.examen1.entities.RecordEntity;
import uniandes.dse.examen1.exceptions.RepeatedCourseException;
import uniandes.dse.examen1.exceptions.RepeatedStudentException;
import uniandes.dse.examen1.exceptions.InvalidRecordException;
import uniandes.dse.examen1.repositories.CourseRepository;
import uniandes.dse.examen1.repositories.StudentRepository;
import uniandes.dse.examen1.services.CourseService;
import uniandes.dse.examen1.services.StudentService;
import uniandes.dse.examen1.services.RecordService;

@DataJpaTest
@Transactional
@Import({ RecordService.class, CourseService.class, StudentService.class })
public class RecordServiceTest {

    @Autowired
    private RecordService recordService;

    @Autowired
    private CourseService courseService;

    @Autowired
    private StudentService studentService;

    @Autowired
    StudentRepository studentRepository;

    @Autowired
    CourseRepository courseRepository;

    private PodamFactory factory = new PodamFactoryImpl();

    private String login;
    private String courseCode;

    /**
     * Tests the normal creation of a record for a student in a course
     * @throws InvalidRecordException 
     * @throws RepeatedCourseException 
     * @throws RepeatedStudentException 
     */
    @Test
    void testCreateRecord() throws InvalidRecordException, RepeatedCourseException, RepeatedStudentException {
    	CourseEntity newCourse = factory.manufacturePojo(CourseEntity.class);
        newCourse = courseService.createCourse(newCourse);
        courseCode = newCourse.getCourseCode();
        
        StudentEntity newEntity = factory.manufacturePojo(StudentEntity.class);
        login = newEntity.getLogin();
        StudentEntity newStudent = studentService.createStudent(newEntity);
        
    	RecordEntity record = recordService.createRecord(login, courseCode, 4.0, "2023-01");
    	
        assertNotNull(record);
        assertEquals(4.0, record.getFinalGrade(), 0.01);
        assertEquals("2023-01", record.getSemester());
        assertEquals(login, record.getStudent().getLogin());
        assertEquals(courseCode, record.getCourse().getCourseCode());
    }

    /**
     * Tests the creation of a record when the login of the student is wrong
     * @throws RepeatedCourseException 
     */
    @Test
    void testCreateRecordMissingStudent() throws RepeatedCourseException {
    	CourseEntity newCourse = factory.manufacturePojo(CourseEntity.class);
        newCourse = courseService.createCourse(newCourse);
        courseCode = newCourse.getCourseCode();
    	assertThrows(InvalidRecordException.class, () -> {
            recordService.createRecord("noexisto", courseCode, 4.0, "2023-01");
    	 });
    }

    /**
     * Tests the creation of a record when the course code is wrong
     * @throws RepeatedStudentException 
     */
    @Test
    void testCreateInscripcionMissingCourse() throws RepeatedStudentException {
    	StudentEntity newEntity = factory.manufacturePojo(StudentEntity.class);
        login = newEntity.getLogin();
        StudentEntity newStudent = studentService.createStudent(newEntity);
    	assertThrows(InvalidRecordException.class, () -> {
            recordService.createRecord(login, "noexiste", 4.0, "2023-01");
    	 });
    }

    /**
     * Tests the creation of a record when the grade is not valid
     * @throws RepeatedStudentException 
     * @throws RepeatedCourseException 
     */
    @Test
    void testCreateInscripcionWrongGrade() throws RepeatedStudentException, RepeatedCourseException {
    	CourseEntity newCourse = factory.manufacturePojo(CourseEntity.class);
        newCourse = courseService.createCourse(newCourse);
        courseCode = newCourse.getCourseCode();
        
        StudentEntity newEntity = factory.manufacturePojo(StudentEntity.class);
        StudentEntity newStudent = studentService.createStudent(newEntity);
        login = newEntity.getLogin();
        
    	assertThrows(InvalidRecordException.class, () -> {
            recordService.createRecord(login, courseCode, 0.5, "2023-01"); // Nota menor a 1.5
        });
    	 assertThrows(InvalidRecordException.class, () -> {
             recordService.createRecord(login, courseCode, 6.0, "2023-01"); // Nota mayor a 5.0
         });
     }

    /**
     * Tests the creation of a record when the student already has a passing grade
     * for the course
     * @throws InvalidRecordException 
     */
    @Test
    void testCreateInscripcionRepetida1() throws InvalidRecordException {
    	recordService.createRecord(login, courseCode, 4.0, "2023-01");

        assertThrows(InvalidRecordException.class, () -> {
            recordService.createRecord(login, courseCode, 3.5, "2023-02");
        });
    }

    /**
     * Tests the creation of a record when the student already has a record for the
     * course, but he has not passed the course yet.
     * @throws InvalidRecordException 
     */
    @Test
    void testCreateInscripcionRepetida2() throws InvalidRecordException {
    	recordService.createRecord(login, courseCode, 2.0, "2023-01");

        assertThrows(InvalidRecordException.class, () -> {
            recordService.createRecord(login, courseCode, 2.5, "2023-02");
        });
    }
}
