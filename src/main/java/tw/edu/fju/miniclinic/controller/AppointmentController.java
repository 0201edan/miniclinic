package tw.edu.fju.miniclinic.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import tw.edu.fju.miniclinic.model.*;

import java.time.LocalDate;
import java.util.Optional;

@Controller
public class AppointmentController {

    @Autowired
    private DoctorRepository doctorRepo;

    @Autowired
    private PatientRepository patientRepo;

    @Autowired
    private AppointmentRepository appointmentRepo;

    // --- 顯示空白掛號表單 ---
    @GetMapping("/appointment/new")
    public String newAppointmentForm(Model model) {
        model.addAttribute("form", new AppointmentForm());
        model.addAttribute("doctors", doctorRepo.findAll());
        return "appointment-new";
    }

    @PostMapping("/appointment/new")
    public String submitAppointment(@Valid @ModelAttribute("form") AppointmentForm form, 
                                    BindingResult result, 
                                    Model model) {
        
        if (result.hasErrors()) {
            model.addAttribute("doctors", doctorRepo.findAll());
            return "appointment-new";
        }

        //查詢病患與醫師
        Optional<Patient> patientOpt = patientRepo.findById(form.getChartNo());
        Optional<Doctor> doctorOpt = doctorRepo.findById(form.getDoctorId());

        //若任一查無結果，帶上錯誤訊息回到表單頁
        if (patientOpt.isEmpty() || doctorOpt.isEmpty()) {
            model.addAttribute("error", "找不到該病患或醫師資料，請確認病歷號。");
            model.addAttribute("doctors", doctorRepo.findAll());
            return "appointment-new";
        }

        //建立 Entity 物件
        Appointment appt = new Appointment();
        appt.setPatient(patientOpt.get());
        appt.setDoctor(doctorOpt.get());
        appt.setApptDate(LocalDate.parse(form.getApptDate())); // 將字串轉為 LocalDate
        appt.setTimeSlot(form.getTimeSlot());
        appt.setStatus("BOOKED");

        //存檔
        Appointment savedAppt = appointmentRepo.save(appt);

        //將儲存後的物件放入 Model 並導向結果頁
        model.addAttribute("appt", savedAppt);
        return "appointment-result";
    }

    // --- 新增：顯示所有掛號紀錄頁面 ---
    @GetMapping("/appointments")
    public String listAppointments(Model model) {
        model.addAttribute("appointments", appointmentRepo.findAll());
        return "appointments";
    }
}