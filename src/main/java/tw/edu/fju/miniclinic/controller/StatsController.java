package tw.edu.fju.miniclinic.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import tw.edu.fju.miniclinic.model.AppointmentRepository;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class StatsController {

    @Autowired
    private AppointmentRepository appointmentRepo;

    @GetMapping("/stats")
    public String showStats(Model model) {
        var appointments = appointmentRepo.findAll();

        //1.總掛號人數
        long totalAppointments = appointments.size();

        //2.依科別統計人數
        Map<String, Long> deptStats = appointments.stream()
                .filter(appt -> appt.getDoctor() != null)
                .collect(Collectors.groupingBy(
                        appt -> appt.getDoctor().getDepartment(),
                        Collectors.counting()
                ));

        //3.依時段統計人數(上午/下午/晚上)
        Map<String, Long> slotStats = appointments.stream()
                .filter(appt -> appt.getTimeSlot() != null)
                .collect(Collectors.groupingBy(
                        appt -> appt.getTimeSlot(),
                        Collectors.counting()
                ));

        model.addAttribute("totalAppointments", totalAppointments);
        model.addAttribute("deptStats", deptStats);
        model.addAttribute("slotStats", slotStats);

        return "stats";
    }
}