package tw.edu.fju.miniclinic.model;

import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.List;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    
    //依日期掛號
    List<Appointment> findByApptDate(LocalDate apptDate);
    
    //依醫師掛號
    List<Appointment> findByDoctor(Doctor doctor);
    
    //依病患掛號
    List<Appointment> findByPatient(Patient patient);
    //根據醫師與當天日期找出掛號清單
    List<Appointment> findByDoctorAndApptDate(Doctor doctor, java.time.LocalDate apptDate);
}