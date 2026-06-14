package tw.edu.fju.miniclinic.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tw.edu.fju.miniclinic.model.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class AppointmentApiController {

    @Autowired
    private AppointmentRepository appointmentRepo;

    @Autowired
    private PatientRepository patientRepo;

    @Autowired
    private DoctorRepository doctorRepo;

    //GET/api/patients-取得所有病患清單
    @GetMapping("/patients")
    public List<Patient> getAllPatients() {
        return patientRepo.findAll();
    }

    //GET /api/patients/{chartNo}-依病歷號取得單一病患
    @GetMapping("/patients/{chartNo}")
    public ResponseEntity<Patient> getPatientByChartNo(@PathVariable String chartNo) {
        return patientRepo.findById(chartNo)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    //GET /api/appointments-取得掛號紀錄
    @GetMapping("/appointments")
    public List<Appointment> getAppointments(
            @RequestParam(required = false) String date,
            @RequestParam(required = false) String doctorId) {
        
        if (date != null && !date.isBlank()) {
            return appointmentRepo.findByApptDate(LocalDate.parse(date));
        }
        if (doctorId != null && !doctorId.isBlank()) {
            Doctor doctor = doctorRepo.findById(doctorId).orElse(null);
            if (doctor != null) {
                return appointmentRepo.findByDoctor(doctor);
            }
        }
        return appointmentRepo.findAll();
    }

    //GET /api/appointments/count-回傳總掛號數 {"count": 3}
    @GetMapping("/appointments/count")
    public Map<String, Long> getAppointmentCount() {
        Map<String, Long> response = new HashMap<>();
        response.put("count", appointmentRepo.count());
        return response;
    }

    @PutMapping("/api/appointments/{apptId}/status")
    public org.springframework.http.ResponseEntity<?> updateStatus(
        @PathVariable Long apptId,
        @RequestBody java.util.Map<String, String> payload,
        jakarta.servlet.http.HttpSession session) {

    //取得目前登入的醫師ID
    String loggedInDoctorId=(String) session.getAttribute("loggedInDoctorId");

    //查詢該預約掛號是否存在
    Appointment appt=appointmentRepo.findById(apptId).orElse(null);
    if (appt==null) {
        return org.springframework.http.ResponseEntity.notFound().build(); // 404
    }

    //比對掛號單醫師與登入者是否一致，不符回傳403Forbidden
    if (!appt.getDoctor().getDoctorId().equals(loggedInDoctorId)) {
        return org.springframework.http.ResponseEntity.status(403).build();
    }

    //取出新狀態
    String newStatus=payload.get("status");

    //驗證狀態是否在合法的三個代碼內
    if (newStatus==null || (!newStatus.equals("BOOKED")&&!newStatus.equals("COMPLETED") && !newStatus.equals("CANCELLED"))) {
        return org.springframework.http.ResponseEntity.badRequest().build();//400
    }

    appt.setStatus(newStatus);
    appointmentRepo.save(appt);

    return org.springframework.http.ResponseEntity.ok(appt);//200
    }
}
