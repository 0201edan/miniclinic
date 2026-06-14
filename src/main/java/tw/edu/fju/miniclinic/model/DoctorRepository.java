package tw.edu.fju.miniclinic.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface DoctorRepository extends JpaRepository<Doctor, String> {
    
    //依科別篩選醫師 [cite: 903]
    List<Doctor> findByDepartment(String department);

    //取得不重複的科別清單
    @Query("SELECT DISTINCT d.department FROM Doctor d ORDER BY d.department")
    List<String> findAllDepartments();
}