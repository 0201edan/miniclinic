package tw.edu.fju.miniclinic.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class LoginRequiredInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        HttpSession session = request.getSession();
        Object loggedIn=session.getAttribute("loggedInDoctorId");

        if (loggedIn==null) {
            String path=request.getRequestURI();
            
            if (path.startsWith("/api/")) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401
                response.setContentType("application/json;charset=UTF-8");
                response.getWriter().write("{\"error\":\"請先登入\"}");
            } else {
                //如果是一般網頁請求，直接重導向去登入頁面
                response.sendRedirect("/login");
            }
            return false;//阻止繼續執行
        }
        return true;
    }
}