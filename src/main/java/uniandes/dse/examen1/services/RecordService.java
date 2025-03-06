package uniandes.dse.examen1.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import uniandes.dse.examen1.entities.CourseEntity;
import uniandes.dse.examen1.entities.StudentEntity;
import uniandes.dse.examen1.entities.RecordEntity;
import uniandes.dse.examen1.exceptions.InvalidRecordException;
import uniandes.dse.examen1.repositories.CourseRepository;
import uniandes.dse.examen1.repositories.StudentRepository;
import uniandes.dse.examen1.repositories.RecordRepository;

@Slf4j
@Service
public class RecordService {

    @Autowired
    StudentRepository studentRepository;

    @Autowired
    CourseRepository courseRepository;

    @Autowired
    RecordRepository recordRepository;

    public RecordEntity createRecord(String loginStudent, String courseCode, Double grade, String semester)
            throws InvalidRecordException {
    	Optional<StudentEntity> studentEntity = studentRepository.findByLogin(loginStudent);
		if (studentEntity.isEmpty())
			throw new InvalidRecordException("El estudiante no existe");

		Optional<CourseEntity> courseEntity = courseRepository.findByCourseCode(courseCode);
		if (courseEntity.isEmpty())
			throw new InvalidRecordException("El curso no existe");

        if (grade < 1.5 || grade > 5 ) {
        	throw new InvalidRecordException("La nota no es correcta");
        }
        
        List<RecordEntity> records = studentEntity.getRecords();
        records.indexOf(records)
        List<CourseEntity> courses = student.getCourses();
        
        return recordRepository.save(newStudent);
    }
}
