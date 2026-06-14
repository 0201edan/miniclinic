package tw.edu.fju.miniclinic.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import tw.edu.fju.miniclinic.model.AppointmentRepository;
import tw.edu.fju.miniclinic.model.Doctor;
import tw.edu.fju.miniclinic.model.DoctorRepository;
import java.time.LocalDate;
import java.util.List;

@Controller
public class DashboardController {

    @Autowired
    private DoctorRepository doctorRepo;

    @Autowired
    private AppointmentRepository appointmentRepo;

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        //取出剛才登入的醫師ID
        String loggedInDoctorId = (String) session.getAttribute("loggedInDoctorId");
        
        //查無醫師，強制清除Session並回登入頁
        Doctor doctor = doctorRepo.findById(loggedInDoctorId).orElse(null);
        if (doctor == null) {
            session.invalidate();
            return "redirect:/login";
        }

        //取得今天的日期找出該醫師今天的看診名細
        LocalDate today = LocalDate.now();
        var appointments = appointmentRepo.findByDoctorAndApptDate(doctor, today);

        //將資料打包送往前端Thymeleaf網頁
        model.addAttribute("doctor", doctor);
        model.addAttribute("appointments", appointments);
        model.addAttribute("today", today);

        return "dashboard";//對應到templates/dashboard.html
    }
}