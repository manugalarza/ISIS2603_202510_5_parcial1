package uniandes.dse.examen1.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import uniandes.dse.examen1.entities.CourseEntity;
import uniandes.dse.examen1.entities.RecordEntity;
import uniandes.dse.examen1.entities.StudentEntity;
import uniandes.dse.examen1.repositories.CourseRepository;
import uniandes.dse.examen1.repositories.StudentRepository;
import uniandes.dse.examen1.repositories.RecordRepository;

@Slf4j
@Service
public class StatsService {

    @Autowired
    StudentRepository estudianteRepository;

    @Autowired
    CourseRepository cursoRepository;

    @Autowired
    RecordRepository inscripcionRepository;

    public Double calculateStudentAverage(String login) {
    	Optional<StudentEntity> studentEntity = estudianteRepository.findByLogin(login);
    	StudentEntity student = studentEntity.get();
        List<RecordEntity> records = student.getRecords();

        if (records.isEmpty()) {
            return 0.0; 
        }

        double sum = 0.0;
        for (RecordEntity record : records) {
            sum += record.getFinalGrade();
        }

        return sum / records.size();
    }

    public Double calculateCourseAverage(String courseCode) {
    	Optional<CourseEntity> courseOpt = cursoRepository.findByCourseCode(courseCode);
        CourseEntity course = courseOpt.get();
        List<StudentEntity> students = course.getStudents();

        if (students.isEmpty()) {
            return 0.0; 
        }

        double sum = 0.0;
        int count = 0;
        
        for (StudentEntity student : students) {
            List<RecordEntity> records = student.getRecords();
            for (RecordEntity record : records) {
                if (record.getCourse().equals(course)) {
                    sum += record.getFinalGrade();
                    count++;
                }
            }
        }

        if (count == 0) {
            return 0.0;
        }

        return sum / count;
    }
}
