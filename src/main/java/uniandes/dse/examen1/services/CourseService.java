package uniandes.dse.examen1.services;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import uniandes.dse.examen1.entities.CourseEntity;
import uniandes.dse.examen1.exceptions.RepeatedCourseException;
import uniandes.dse.examen1.repositories.CourseRepository;

@Slf4j
@Service
public class CourseService {

    @Autowired
    CourseRepository courseRepository;

    public CourseEntity createCourse(CourseEntity newCourse) throws RepeatedCourseException {
    	log.info("Inicia proceso de creación del curso");

        if (!courseRepository.findByCourseCode(newCourse.getCourseCode()).isEmpty()) {
			throw new RepeatedCourseException(newCourse.getCourseCode());
        }
    
        log.info("Termina proceso de creación de estudiante");
        return courseRepository.save(newCourse);
        }
    }

