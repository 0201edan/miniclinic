package tw.edu.fju.miniclinic.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tw.edu.fju.miniclinic.model.Doctor;
import tw.edu.fju.miniclinic.model.DoctorRepository;
import java.util.List;

@RestController
@RequestMapping("/api")
public class DoctorApiController {

    @Autowired
    private DoctorRepository doctorRepo;

    //查詢所有醫師
    @GetMapping("/doctors")
    public List<Doctor> getDoctors(@RequestParam(required = false) String department) {
        if (department == null || department.isBlank()) {
            return doctorRepo.findAll();
        }
        return doctorRepo.findByDepartment(department);
    }

    //查詢單一醫師
    @GetMapping("/doctors/{doctorId}")
    public ResponseEntity<Doctor> getDoctor(@PathVariable String doctorId) {
        return doctorRepo.findById(doctorId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    //查詢所有科別清單
    @GetMapping("/departments")
    public List<String> getDepartments() {
        return doctorRepo.findAllDepartments();
    }

    //新增醫師 (Create)-回傳 201 Created
    @PostMapping("/doctors")
    public ResponseEntity<Doctor> createDoctor(@RequestBody Doctor doctor) {
        Doctor saved = doctorRepo.save(doctor);
        return ResponseEntity.status(201).body(saved);
    }

    //更新醫師(Update)
    @PutMapping("/doctors/{doctorId}")
    public ResponseEntity<Doctor> updateDoctor(
            @PathVariable String doctorId,
            @RequestBody Doctor updated) {
        return doctorRepo.findById(doctorId)
                .map(existing -> {
                    existing.setName(updated.getName());
                    existing.setDepartment(updated.getDepartment());
                    existing.setSpecialty(updated.getSpecialty());
                    return ResponseEntity.ok(doctorRepo.save(existing));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    //刪除醫師 (Delete) - 成功回傳 204 No Content
    @DeleteMapping("/doctors/{doctorId}")
    public ResponseEntity<Void> deleteDoctor(@PathVariable String doctorId) {
        if (!doctorRepo.existsById(doctorId)) {
            return ResponseEntity.notFound().build();
        }
        doctorRepo.deleteById(doctorId);
        return ResponseEntity.noContent().build();
    }
}
