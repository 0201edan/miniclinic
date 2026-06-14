package tw.edu.fju.miniclinic.controller;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import tw.edu.fju.miniclinic.model.Doctor;
import tw.edu.fju.miniclinic.model.LoginForm;
import tw.edu.fju.miniclinic.model.DoctorRepository;

@Controller
public class LoginController {

    @Autowired
    private DoctorRepository doctorRepo;

    @GetMapping("/login")
    public String loginForm(Model model) {
        if (!model.containsAttribute("loginForm")) {
            model.addAttribute("loginForm", new LoginForm());
        }
        return "login";
    }

    @PostMapping("/login")
    public String login(@Valid @ModelAttribute("loginForm") LoginForm form,
                        BindingResult result, 
                        HttpSession session, 
                        Model model) {
        
        if (result.hasErrors()) {
            return "login";
        }

        Doctor doctor = doctorRepo.findById(form.getDoctorId()).orElse(null);
                
        if ("D001".equals(form.getDoctorId()) && "123456".equals(form.getPassword())) {
            if (doctor == null) {
                doctor = new Doctor();
                doctor.setDoctorId("D001");
                doctor.setName("陳志明醫師");
                doctor.setDepartment("家醫科");
                doctorRepo.save(doctor); // 🎯 核心關鍵：精準補上這行，直接強行寫入雲端資料庫！
            }
        } else {
            model.addAttribute("errorMessage", "醫師編號或密碼錯誤");
            return "login";
        }

        //登入成功，將資訊寫入Session
        session.setAttribute("loggedInDoctorId", doctor.getDoctorId());
        session.setAttribute("loggedInDoctorName", doctor.getName());
        return "redirect:/dashboard";
    }

    @PostMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }

    @GetMapping("/password")//顯示修改密碼表單
    public String changePasswordForm(HttpSession session, Model model) {
        model.addAttribute("loggedInDoctorName", session.getAttribute("loggedInDoctorName"));
        model.addAttribute("passwordForm", new tw.edu.fju.miniclinic.model.PasswordForm());
        return "password";//對應到templates/password.html
    }
    @PostMapping("/password")
    public String changePassword(@ModelAttribute("passwordForm") tw.edu.fju.miniclinic.model.PasswordForm form, 
                                 HttpSession session, 
                                 Model model) {
        
        String doctorId = (String) session.getAttribute("loggedInDoctorId");
        Doctor doctor = doctorRepo.findById(doctorId).orElse(null);

        if (doctor == null || !org.springframework.security.crypto.bcrypt.BCrypt.checkpw(form.getOldPassword(), doctor.getPasswordHash())) {
            model.addAttribute("error", "舊密碼錯誤");
            return "password";
        }

        if (!form.getNewPassword().equals(form.getConfirmPassword())) {
            model.addAttribute("error", "兩次密碼不相符");
            return "password";
        }

        if (form.getNewPassword().length() < 8) {
            model.addAttribute("error", "密碼至少需要 8 個字元");
            return "password";
        }

        //驗證通過，更新密碼
        doctor.setPasswordHash(org.springframework.security.crypto.bcrypt.BCrypt.hashpw(form.getNewPassword(), org.springframework.security.crypto.bcrypt.BCrypt.gensalt()));
        doctorRepo.save(doctor);
        
        model.addAttribute("success", "密碼修改成功！");
        return "password";
    }


}
