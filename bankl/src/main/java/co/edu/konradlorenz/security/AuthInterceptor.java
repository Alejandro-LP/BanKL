package co.edu.konradlorenz.security;

import co.edu.konradlorenz.model.Cliente;
import co.edu.konradlorenz.repository.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class AuthInterceptor implements HandlerInterceptor {
    private final ClienteRepository clientes;
    private final CuentaRepository cuentas;
    private final TransferenciaRepository transferencias;
    private final PrestamoRepository prestamos;
    private final InversionRepository inversiones;
    private final BeneficiarioRepository beneficiarios;
    private final NotificacionRepository notificaciones;

    public AuthInterceptor(ClienteRepository clientes, CuentaRepository cuentas,
            TransferenciaRepository transferencias, PrestamoRepository prestamos,
            InversionRepository inversiones, BeneficiarioRepository beneficiarios,
            NotificacionRepository notificaciones) {
        this.clientes=clientes; this.cuentas=cuentas; this.transferencias=transferencias;
        this.prestamos=prestamos; this.inversiones=inversiones;
        this.beneficiarios=beneficiarios; this.notificaciones=notificaciones;
    }

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
        String requestedUser=req.getParameter("usuario");
        if (requestedUser != null && !requestedUser.equals(user)) { res.sendError(403); return false; }
        Cliente owner=clientes.findByUsuarioIS(user);
        if (owner == null) { res.sendError(401); return false; }
        Integer id=pathId(path);
        boolean own=true;
        if (path.startsWith("/cuentas/")) own=owns(cuentas.findById(integer(req.getParameter("idCuenta"))).orElse(null), owner);
        else if (path.equals("/transferencias") && req.getParameter("cuentaOrigenId") != null)
            own=owns(cuentas.findById(integer(req.getParameter("cuentaOrigenId"))).orElse(null), owner);
        else if (path.startsWith("/prestamos/pagar/")) own=owns(prestamos.findById(id).orElse(null), owner);
        else if (path.startsWith("/inversiones/retirar/")) own=owns(inversiones.findById(id).orElse(null), owner);
        else if (path.startsWith("/beneficiarios/") && id != null) own=owns(beneficiarios.findById(id).orElse(null), owner);
        else if (path.startsWith("/notificaciones/leer/")) own=owns(notificaciones.findById(id).orElse(null), owner);
        if (!own) { res.sendError(403); return false; }
        return true;
    }

    private Integer pathId(String path) {
        try { return Integer.valueOf(path.substring(path.lastIndexOf('/')+1)); }
        catch (Exception ignored) { return null; }
    }
    private Integer integer(String value) { try { return Integer.valueOf(value); } catch (Exception ignored) { return -1; } }
    private boolean owns(co.edu.konradlorenz.model.Cuenta c, Cliente o) { return c != null && c.getCliente()!=null && c.getCliente().getIdDB().equals(o.getIdDB()); }
    private boolean owns(co.edu.konradlorenz.model.Prestamo x, Cliente o) { return x != null && x.getCliente()!=null && x.getCliente().getIdDB().equals(o.getIdDB()); }
    private boolean owns(co.edu.konradlorenz.model.Inversion x, Cliente o) { return x != null && x.getCliente()!=null && x.getCliente().getIdDB().equals(o.getIdDB()); }
    private boolean owns(co.edu.konradlorenz.model.Beneficiario x, Cliente o) { return x != null && x.getCliente()!=null && x.getCliente().getIdDB().equals(o.getIdDB()); }
    private boolean owns(co.edu.konradlorenz.model.Notificacion x, Cliente o) { return x != null && x.getCliente()!=null && x.getCliente().getIdDB().equals(o.getIdDB()); }
}
