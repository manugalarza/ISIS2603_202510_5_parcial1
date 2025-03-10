package uniandes.dse.examen1.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import jakarta.transaction.Transactional;
import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;
import uniandes.dse.examen1.entities.StudentEntity;
import uniandes.dse.examen1.entities.CourseEntity;
import uniandes.dse.examen1.entities.RecordEntity;
import uniandes.dse.examen1.exceptions.InvalidRecordException;
import uniandes.dse.examen1.exceptions.RepeatedCourseException;
import uniandes.dse.examen1.exceptions.RepeatedStudentException;
import uniandes.dse.examen1.repositories.CourseRepository;
import uniandes.dse.examen1.repositories.StudentRepository;
import uniandes.dse.examen1.services.CourseService;
import uniandes.dse.examen1.services.RecordService;
import uniandes.dse.examen1.services.StatsService;
import uniandes.dse.examen1.services.StudentService;

@DataJpaTest
@Transactional
@Import({ RecordService.class, CourseService.class, StudentService.class })
public class StatServiceTest {

	@Autowired
	RecordService recordService;
    
    StatsService statsService;

    @Autowired
    StudentRepository studentRepository;

    @Autowired
    CourseRepository courseRepository;

    private PodamFactory factory = new PodamFactoryImpl();

    @BeforeEach
    void setUp() throws RepeatedCourseException, RepeatedStudentException {
    	studentRepository.deleteAll();
        courseRepository.deleteAll();
        StudentEntity student = factory.manufacturePojo(StudentEntity.class);
        studentRepository.save(student);

        CourseEntity course = factory.manufacturePojo(CourseEntity.class);
        courseRepository.save(course);
    }
    
    @Test
    @Transactional
    void testCalculateStudentAverage() throws InvalidRecordException {
        StudentEntity student = factory.manufacturePojo(StudentEntity.class);
        studentRepository.save(student);

        CourseEntity course = factory.manufacturePojo(CourseEntity.class);
        courseRepository.save(course);

        RecordEntity record1 = new RecordEntity();
        record1.setFinalGrade(4.0);
        record1.setSemester("2023-01");
        record1.setStudent(student);
        record1.setCourse(course);
        recordService.createRecord(student.getLogin(), course.getCourseCode(), 4.0, "2023-01");
        
        RecordEntity record2 = new RecordEntity();
        record2.setFinalGrade(3.0);
        record2.setSemester("2023-02");
        record2.setStudent(student);
        record2.setCourse(course);
        recordService.createRecord(student.getLogin(), course.getCourseCode(), 3.5, "2023-02");

        Double average = statsService.calculateStudentAverage(student.getLogin());

        assertEquals(3.5, average, "El promedio no fue correctamente calculado"); // El promedio debe ser 3.75
        
    }

    @Test
    void testCalculateCourseAverage() throws InvalidRecordException {
        CourseEntity course = factory.manufacturePojo(CourseEntity.class);
        courseRepository.save(course);
     
        StudentEntity student1 = factory.manufacturePojo(StudentEntity.class);
        studentRepository.save(student1);

        StudentEntity student2 = factory.manufacturePojo(StudentEntity.class);
        studentRepository.save(student2);

        recordService.createRecord(student1.getLogin(), course.getCourseCode(), 4.0, "2023-01");
        recordService.createRecord(student2.getLogin(), course.getCourseCode(), 3.0, "2023-01");

        Double average = statsService.calculateCourseAverage(course.getCourseCode());

        assertEquals(3.5, average, "El promedio del curso fue calculado erroneamente"); 
    }
}
