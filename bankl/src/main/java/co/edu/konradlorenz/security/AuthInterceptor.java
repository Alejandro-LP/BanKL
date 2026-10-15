package co.edu.konradlorenz.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class AuthInterceptor implements HandlerInterceptor {
    @Override public boolean preHandle(HttpServletRequest req, HttpServletResponse res, Object handler) throws Exception {
        String path=req.getRequestURI().substring(req.getContextPath().length());
        if ((path.equals("/clientes/login") && req.getMethod().equals("POST")) ||
            (path.equals("/clientes/registro") && req.getMethod().equals("POST")) || path.equals("/error")) return true;

        HttpSession session=req.getSession(false);
        String user=session == null ? null : (String) session.getAttribute("usuario");
        String role=session == null ? null : (String) session.getAttribute("tipo");
        if (user == null) { res.sendError(401, "Inicia sesión para continuar"); return false; }
        if (path.startsWith("/api/admin/")) {
            if (!"ClienteAdmin".equals(role)) { res.sendError(403); return false; }
            return true;
        }
        if ("ClienteAdmin".equals(role) && !path.equals("/clientes/logout")) { res.sendError(403); return false; }
        return true;
    }
}
