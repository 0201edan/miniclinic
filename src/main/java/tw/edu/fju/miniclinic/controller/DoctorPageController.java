package tw.edu.fju.miniclinic.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import tw.edu.fju.miniclinic.model.Doctor;
import tw.edu.fju.miniclinic.model.DoctorRepository;
import java.util.List;
import java.util.Optional;

@Controller
public class DoctorPageController {

    @Autowired
    private DoctorRepository doctorRepo;

    // 瀏覽器輸入 http://localhost:8080/doctors 來看醫師列表
    @GetMapping("/doctors")
    public String listDoctors(@RequestParam(required = false) String department, Model model) {
        List<Doctor> doctors;
        if (department == null || department.isBlank()) {
            doctors = doctorRepo.findAll();
        } else {
            doctors = doctorRepo.findByDepartment(department);
        }
        model.addAttribute("doctors", doctors);
        model.addAttribute("departments", doctorRepo.findAllDepartments());
        model.addAttribute("selectedDept", department);
        return "doctors";
    }

    // 點擊單一醫師看詳細介紹
    @GetMapping("/doctors/{doctorId}")
    public String doctorDetail(@PathVariable String doctorId, Model model) {
        Optional<Doctor> doctor = doctorRepo.findById(doctorId);
        if (doctor.isEmpty()) {
            return "redirect:/doctors";
        }
        model.addAttribute("doctor", doctor.get());
        return "doctor-detail";
    }
}