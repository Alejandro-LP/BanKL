const usuario = localStorage.getItem("usuario");
const nombre  = localStorage.getItem("nombre");
let csrfTokenCuenta;
let temporizadorCotizacionAvance;

document.addEventListener("DOMContentLoaded", () => {
    document.body.classList.toggle("dark", localStorage.getItem("bankl-theme") !== "light");
    if (!usuario || localStorage.getItem("tipo") !== "ClienteNatural") {
        window.location.href = "/html/Login.html";
        return;
    }
    if (nombre) {
        document.getElementById("bienvenida").textContent = "Bienvenido, " + nombre;
        document.getElementById("saludoNombre").textContent = "¡Bienvenido, " + nombre.split(" ")[0] + "!";
    }
    const ajustesUsuario = document.getElementById("ajustesUsuario");
    if (ajustesUsuario) ajustesUsuario.textContent = usuario || "Sesión activa";
    cargarNotificaciones();
    cargarSeccionDesdeUrl();
});

function escapeHtml(value) {
    return String(value ?? "").replace(/[&<>"']/g, char => ({"&":"&amp;","<":"&lt;",">":"&gt;",'"':"&quot;","'":"&#39;"})[char]);
}
// ===== TABS =====
function irTab(tab, updateHistory = true) {
    const paginas = Array.from(document.querySelectorAll(".tab-content"));
    const seleccionada = document.getElementById("tab-" + tab);
    if (!seleccionada) return;

    paginas.forEach(pagina => pagina.classList.toggle("active", pagina === seleccionada));
    document.querySelectorAll(".tab-btn").forEach(boton => boton.classList.toggle("active", boton.dataset.tab === tab));
    if (updateHistory) history.pushState({ tab }, "", `?seccion=${encodeURIComponent(tab)}`);

    if (tab === "tarjetas") { cargarCuentasDash(); cargarCuentasOperaciones(); }
    if (tab === "prestamos") cargarPrestamos();
    if (tab === "inversiones") { cargarInversiones(); cargarCuentasOrigenInversion(); }
    if (tab === "beneficiarios") cargarBeneficiarios();
    if (tab === "transferencias") { cargarTransferencias(); cargarCuentasParaTransferencia(); }
    if (tab === "notificaciones") cargarNotificaciones();
    if (tab === "inicio") cargarResumen();
    window.scrollTo({ top: 0, behavior: "smooth" });
}

function irOperacion(tipo) {
    irTab("tarjetas");
    document.querySelectorAll(".tab-btn").forEach(boton => boton.classList.remove("active"));
    document.getElementById(`btn-${tipo}`).classList.add("active");
    const panel = document.getElementById(tipo === "consignar" ? "consignacionPanel" : "avancePanel");
    setTimeout(() => panel.scrollIntoView({behavior: "smooth", block: "center"}), 80);
}

function cargarSeccionDesdeUrl() {
    const solicitada = new URLSearchParams(window.location.search).get("seccion") || "inicio";
    const existente = document.getElementById("tab-" + solicitada) ? solicitada : "inicio";
    irTab(existente, false);
}

window.addEventListener("popstate", cargarSeccionDesdeUrl);
// ===== RESUMEN INICIO =====
function cargarResumen() {
    cargarMovimientos();
    fetch("/clientes/cuentas")
        .then(r => r.json())
        .then(cuentas => {
            const debito  = cuentas.find(c => c.tipo === "DEBITO");
            const credito = cuentas.find(c => c.tipo === "CREDITO");
            document.getElementById("resumenDebito").textContent =
                "$" + (debito ? debito.saldo.toLocaleString("en-US",{minimumFractionDigits:2}) : "0.00");
            document.getElementById("resumenCredito").textContent =
                "$" + (credito ? credito.disponible.toLocaleString("en-US",{minimumFractionDigits:2}) : "0.00");
        }).catch(()=>{});

    fetch("/inversiones")
        .then(r => r.json())
        .then(data => {
            const total = data.filter(i=>i.estado==="ACTIVA").reduce((s,i)=>s+i.monto,0);
            document.getElementById("resumenInversiones").textContent =
                "$" + total.toLocaleString("en-US",{minimumFractionDigits:2});
        }).catch(()=>{});

    fetch("/prestamos")
        .then(r => r.json())
        .then(data => {
            const activos = data.filter(p=>p.estado==="ACTIVO").length;
            document.getElementById("resumenPrestamos").textContent = activos + " activo(s)";
        }).catch(()=>{});
}

function cargarMovimientos() {
    const tabla = document.getElementById("tablaMovimientos");
    if (!tabla) return;
    fetch("/cuentas/movimientos")
        .then(response => { if (!response.ok) throw new Error("No se pudieron cargar los movimientos"); return response.json(); })
        .then(movimientos => {
            if (!movimientos.length) {
                tabla.innerHTML = '<tr><td colspan="5" class="table-empty">Aún no tienes movimientos registrados.</td></tr>';
                return;
            }
            tabla.innerHTML = movimientos.map(movimiento => {
                const entrada = movimiento.tipo.startsWith("ENTRADA:") || movimiento.tipo === "ABONO_TARJETA";
                const descripcion = movimiento.tipo.replace(/[:_]/g, " ").toLocaleLowerCase("es").replace(/^./, letra => letra.toLocaleUpperCase("es"));
                const fecha = new Date(movimiento.fecha).toLocaleString("es-CO", {day:"2-digit", month:"short", year:"numeric", hour:"2-digit", minute:"2-digit"});
                const signo = entrada ? "+" : "−";
                return `<tr><td class="date-cell">${escapeHtml(fecha)}</td><td><span class="movement-name">${escapeHtml(descripcion)}</span></td><td>••${String(movimiento.numeroCuenta).slice(-4)}</td><td><span class="movement-type">${escapeHtml(movimiento.tipoCuenta)}</span></td><td class="amount-cell ${entrada ? "amount-positive" : "amount-negative"}">${signo}$${Number(movimiento.valor).toLocaleString("es-CO", {minimumFractionDigits:2, maximumFractionDigits:2})}</td></tr>`;
            }).join("");
        })
        .catch(() => { tabla.innerHTML = '<tr><td colspan="5" class="table-empty">No fue posible cargar los movimientos.</td></tr>'; });
}

// ===== TARJETAS =====
function cargarCuentasDash() {
    fetch("/clientes/cuentas")
        .then(r => { if(!r.ok) throw new Error(r.status); return r.json(); })
        .then(cuentas => {
            const contenedor = document.getElementById("contenedorCuentasDash");
            contenedor.innerHTML = "";
            if (!cuentas || cuentas.length === 0) {
                contenedor.innerHTML = "<p class='empty'>No tienes tarjetas registradas.</p>";
                return;
            }
            cuentas.forEach(cuenta => {
                const esCredito = cuenta.tipo === "CREDITO";
                const wrapper = document.createElement("div");
                wrapper.className = "cuenta-wrapper";
                wrapper.innerHTML = `
                    <div class="tarjeta-bancaria ${esCredito ? 'tarjeta-credito' : 'tarjeta-debito'}">
                        <div class="tarjeta-top">
                            <span class="banco-nombre">BanKL</span>
                            <span class="tarjeta-tipo">${esCredito ? 'CRÉDITO' : 'DÉBITO'}</span>
                        </div>
                        <div class="chip"><div class="chip-inner"></div></div>
                        <div class="tarjeta-numero">${cuenta.numeroTarjeta}</div>
                        <div class="tarjeta-bottom">
                            <div><div class="tarjeta-label">TITULAR</div><div class="tarjeta-value">${escapeHtml(cuenta.propietario).toUpperCase()}</div></div>
                            <div><div class="tarjeta-label">VENCE</div><div class="tarjeta-value">${cuenta.fechaExpiracion}</div></div>
                            <div><div class="tarjeta-label">CVV</div><div class="tarjeta-value">•••</div></div>
                        </div>
                    </div>
                    <div class="tarjeta-info">
                        <div class="info-row"><span>N de Cuenta</span><strong>${cuenta.numeroCuenta}</strong></div>
                        ${esCredito ? `
                            <div class="info-row"><span>Cupo total</span><strong>$${cuenta.cupo.toLocaleString("en-US",{minimumFractionDigits:2})}</strong></div>
                            <div class="info-row"><span>Disponible</span><strong class="verde">$${cuenta.disponible.toLocaleString("en-US",{minimumFractionDigits:2})}</strong></div>
                            <div class="info-row"><span>Deuda</span><strong class="${cuenta.saldo>0?'rojo':''}">$${cuenta.saldo.toLocaleString("en-US",{minimumFractionDigits:2})}</strong></div>
                        ` : `
                            <div class="info-row"><span>Saldo</span><strong class="verde">$${cuenta.saldo.toLocaleString("en-US",{minimumFractionDigits:2})}</strong></div>
                        `}
                        <div class="tarjeta-acciones">
                            ${esCredito ? `
                                <button class="btn-accion btn-pagar"  onclick="pagarCuenta(${cuenta.id})"> Pagar</button>
                            ` : `
                                <button class="btn-accion btn-retirar" onclick="retirarCuenta(${cuenta.id})"> Retirar</button>
                            `}
                            <button class="btn-accion btn-cambiar" onclick="cambiarTarjetaDash(${cuenta.id})"> Cambiar</button>
                        </div>
                    </div>`;
                contenedor.appendChild(wrapper);
            });
        }).catch(err => {
            document.getElementById("contenedorCuentasDash").innerHTML =
                "<p class='empty' style='color:red'>Error cargando tarjetas ️</p>";
        });
}

function cargarCuentasOperaciones() {
    fetch("/clientes/cuentas")
        .then(async response => {
            if (!response.ok) throw new Error("No se pudieron cargar tus cuentas");
            return response.json();
        })
        .then(cuentas => {
            const debito = cuentas.filter(c => c.tipo === "DEBITO");
            const credito = cuentas.filter(c => c.tipo === "CREDITO");
            ["cuentaConsignacion", "cuentaDestinoAvance"].forEach(id => {
                const select = document.getElementById(id);
                select.replaceChildren(new Option("Selecciona una cuenta débito", ""));
                debito.forEach(c => select.add(new Option(`Cuenta •${String(c.numeroCuenta).slice(-4)} | Saldo $${Number(c.saldo).toFixed(2)}`, String(c.id))));
            });
            const selectCredito = document.getElementById("tarjetaAvance");
            selectCredito.replaceChildren(new Option("Selecciona una tarjeta de crédito", ""));
            credito.forEach(c => selectCredito.add(new Option(`Crédito •${String(c.numeroCuenta).slice(-4)} | Disponible $${Number(c.disponible).toFixed(2)}`, String(c.id))));
        })
        .catch(error => mostrarResultadoOperacion("resultadoConsignacion", error.message, true));
}

async function enviarOperacionCuenta(path, fields) {
    if (!csrfTokenCuenta) {
        const tokenResponse = await fetch("/clientes/csrf");
        if (!tokenResponse.ok) throw new Error("Sesión inválida. Inicia sesión de nuevo.");
        csrfTokenCuenta = (await tokenResponse.json()).token;
    }
    const response = await fetch(path, {method: "POST", headers: {"Content-Type": "application/x-www-form-urlencoded;charset=UTF-8", "X-CSRF-TOKEN": csrfTokenCuenta}, body: new URLSearchParams(fields)});
    const payload = await response.json().catch(() => null);
    if (!response.ok) throw new Error(payload?.detail || payload?.error || "No se pudo completar la operación");
    return payload;
}

function mostrarResultadoOperacion(id, mensaje, error = false) {
    const el = document.getElementById(id);
    el.textContent = mensaje;
    el.classList.toggle("error", error);
}

function previsualizarCostoAvance() {
    clearTimeout(temporizadorCotizacionAvance);
    const input = document.getElementById("montoAvance");
    const valor = input.value;
    if (!valor || !/^\d+(\.\d{1,2})?$/.test(valor) || Number(valor) <= 0 || Number(valor) > 1000000000) {
        mostrarResultadoOperacion("resumenCostoAvance", "Ingresa un monto válido para consultar el costo.", true); return;
    }
    temporizadorCotizacionAvance = setTimeout(async () => {
        try {
            const response = await fetch(`/cuentas/avance/costos?valor=${encodeURIComponent(valor)}`);
            const costos = await response.json();
            if (!response.ok) throw new Error(costos.detail || "No fue posible calcular el costo");
            mostrarResultadoOperacion("resumenCostoAvance", `Comisión ${Number(costos.comisionPorcentaje).toFixed(2)}% (mín. $${Number(costos.comisionMinima).toFixed(2)}): $${Number(costos.comision).toFixed(2)} · Interés ${Number(costos.tasaMensualPorcentaje).toFixed(2)}% mensual por 30 días: $${Number(costos.interes30Dias).toFixed(2)} · Cargo al cupo: $${Number(costos.cargoTotal).toFixed(2)}`);
        } catch (error) { mostrarResultadoOperacion("resumenCostoAvance", error.message, true); }
    }, 250);
}

async function consignarDinero() {
    const idCuenta = document.getElementById("cuentaConsignacion").value;
    const valor = document.getElementById("montoConsignacion").value;
    if (!idCuenta || !valor || Number(valor) <= 0 || Number(valor) > 1000000000 || !Number.isFinite(Number(valor)) || !/^\d+(\.\d{1,2})?$/.test(valor)) {
        mostrarResultadoOperacion("resultadoConsignacion", "Selecciona la cuenta e ingresa un monto válido.", true); return;
    }
    try {
        await enviarOperacionCuenta("/cuentas/consignar", {idCuenta, valor});
        document.getElementById("montoConsignacion").value = "";
        mostrarResultadoOperacion("resultadoConsignacion", "Consignación simulada acreditada de inmediato.");
        cargarCuentasDash(); cargarCuentasOperaciones(); cargarNotificaciones();
        cargarMovimientos(); cargarResumen();
    } catch (error) { mostrarResultadoOperacion("resultadoConsignacion", error.message, true); }
}

async function solicitarAvance() {
    const idCuentaCredito = document.getElementById("tarjetaAvance").value;
    const idCuentaDestino = document.getElementById("cuentaDestinoAvance").value;
    const valor = document.getElementById("montoAvance").value;
    if (!idCuentaCredito || !idCuentaDestino || !valor || Number(valor) <= 0 || Number(valor) > 1000000000 || !Number.isFinite(Number(valor)) || !/^\d+(\.\d{1,2})?$/.test(valor)) {
        mostrarResultadoOperacion("resultadoAvance", "Selecciona ambas cuentas e ingresa un monto válido.", true); return;
    }
    try {
        const result = await enviarOperacionCuenta("/cuentas/avance", {idCuentaCredito, idCuentaDestino, valor});
        document.getElementById("montoAvance").value = "";
        mostrarResultadoOperacion("resultadoAvance", `Abonado. Comisión: $${Number(result.comision).toFixed(2)}. Interés de 30 días: $${Number(result.interes30Dias).toFixed(2)}. Cargo total al cupo: $${Number(result.cargoTotal).toFixed(2)}.`);
        cargarCuentasDash(); cargarCuentasOperaciones(); cargarNotificaciones();
        cargarMovimientos(); cargarResumen();
    } catch (error) { mostrarResultadoOperacion("resultadoAvance", error.message, true); }
}

function retirarCuenta(id) {
    const v = prompt("Valor a retirar ($):"); if (!v||isNaN(v)||v<=0) return;
    fetch(`/cuentas/retirar?idCuenta=${encodeURIComponent(id)}&valor=${encodeURIComponent(v)}`,{method:"POST"})
        .then(r=>{if(!r.ok)throw new Error("No fue posible retirar fondos"); cargarCuentasDash();})
        .catch(e=>alert(e.message));
}
async function pagarCuenta(id) {
    const v = prompt("Valor a pagar ($):"); if (!v||isNaN(v)||v<=0) return;
    const idCuentaOrigen = await elegirCuentaDebito("Selecciona el número de la cuenta débito de origen");
    if (!idCuentaOrigen) return;
    const params = new URLSearchParams({idCuentaCredito: String(id), idCuentaOrigen, valor: v});
    fetch(`/cuentas/pagar-tarjeta?${params}`,{method:"POST"})
        .then(r=>{if(!r.ok)throw new Error("No fue posible pagar la tarjeta"); cargarCuentasDash();})
        .catch(e=>alert(e.message));
}
async function elegirCuentaDebito(mensaje) {
    const response = await fetch("/clientes/cuentas");
    if (!response.ok) throw new Error("No se pudieron cargar tus cuentas");
    const cuentas = (await response.json()).filter(c => c.tipo === "DEBITO");
    if (!cuentas.length) { alert("No tienes cuentas débito disponibles"); return null; }
    const opciones = cuentas.map(c => String(c.numeroCuenta)).join(", ");
    const numero = prompt(`${mensaje}: ${opciones}`);
    const cuenta = cuentas.find(c => String(c.numeroCuenta) === String(numero || "").trim());
    if (!cuenta) { alert("Selecciona un número de cuenta válido"); return null; }
    return String(cuenta.id);
}
function cargarCuentasOrigenInversion() {
    fetch("/clientes/cuentas")
        .then(r=>{if(!r.ok)throw new Error("Error cargando cuentas");return r.json();})
        .then(cuentas=>{
            const select=document.getElementById("cuentaOrigenInversion");
            if(!select)return;
            select.replaceChildren(new Option("Cuenta débito para financiar", ""));
            cuentas.filter(c=>c.tipo==="DEBITO").forEach(c=>{
                select.add(new Option(`Cuenta terminada en ${String(c.numeroCuenta).slice(-4)} · Saldo $${Number(c.saldo).toFixed(2)}`, String(c.id)));
            });
        })
        .catch(()=>{});
}
function cambiarTarjetaDash(id) {
    if (!confirm("¿Regenerar datos de esta tarjeta?")) return;
    fetch(`/cuentas/cambiar?idCuenta=${id}`,{method:"POST"})
        .then(r=>{ if(!r.ok) throw new Error("Error "+r.status); return r.text(); })
        .then(()=>setTimeout(()=>cargarCuentasDash(),300))
        .catch(e=>alert("Error: "+e));
}

// ===== NOTIFICACIONES =====
function cargarNotificaciones() {
    fetch("/notificaciones")
        .then(r=>r.json())
        .then(data=>{
            const badge = document.getElementById("badgeNoti");
            const noLeidas = data.filter(n=>!n.leida).length;
            badge.textContent = noLeidas||"";
            badge.style.display = noLeidas?"inline":"none";
            const sidebarBadge = document.getElementById("sidebarBadge");
            if (sidebarBadge) sidebarBadge.textContent = noLeidas ? String(noLeidas) : "";
            const inicioBadge = document.getElementById("inicioBadge");
            if (inicioBadge) inicioBadge.textContent = noLeidas ? `(${noLeidas})` : "";
            const actividad = document.getElementById("inicioActividad");
            if (actividad) {
                actividad.innerHTML = data.length === 0
                    ? '<p class="actividad-vacia">Aún no tienes avisos. Aquí verás las novedades de tu cuenta.</p>'
                    : data.slice(0, 3).map(n => `
                        <button class="actividad-item ${n.leida ? 'leida' : ''}" onclick="irTab('notificaciones')">
                            <span class="actividad-punto"></span>
                            <span class="actividad-copy"><strong>${escapeHtml(n.titulo)}</strong><small>${escapeHtml(n.mensaje)}</small></span>
                            <span class="actividad-fecha">${new Date(n.fecha).toLocaleDateString("es-CO", {day:"numeric",month:"short"})}</span>
                        </button>`).join("");
            }
            document.getElementById("listaNoti").innerHTML = data.length===0
                ? "<p class='empty'>Sin notificaciones</p>"
                : data.map(n=>`
                    <div class="noti-item ${n.leida?'leida':''}" onclick="leerNoti(${n.id},this)">
                        <span class="noti-tipo noti-${escapeHtml(n.tipo).toLowerCase()}">${escapeHtml(n.tipo)}</span>
                        <strong>${escapeHtml(n.titulo)}</strong>
                        <p>${escapeHtml(n.mensaje)}</p>
                        <small>${new Date(n.fecha).toLocaleString("es-CO")}</small>
                    </div>`).join("");
        }).catch(()=>{});
}
function leerNoti(id,el) {
    fetch(`/notificaciones/leer/${id}`,{method:"POST"})
        .then(()=>{el.classList.add("leida");cargarNotificaciones();});
}

// ===== PRÉSTAMOS =====
function cargarPrestamos() {
    fetch("/prestamos")
        .then(r=>r.json())
        .then(data=>{
            document.getElementById("listaPrestamos").innerHTML = data.length===0
                ? "<p class='empty'>No tienes préstamos activos</p>"
                : data.map(p=>`
                    <div class="item-card ${p.estado.toLowerCase()}">
                        <div class="item-header"><span> Préstamo #${p.id}</span><span class="badge-estado">${p.estado}</span></div>
                        <div class="item-body">
                            <div class="info-row"><span>Monto</span><strong>$${p.monto.toLocaleString("en-US",{minimumFractionDigits:2})}</strong></div>
                            <div class="info-row"><span>Cuota mensual</span><strong>$${p.valorCuota.toLocaleString("en-US",{minimumFractionDigits:2})}</strong></div>
                            <div class="info-row"><span>Progreso</span><strong>${p.cuotasPagadas}/${p.numeroCuotas} cuotas</strong></div>
                            <div class="info-row"><span>Saldo pendiente</span><strong class="rojo">$${p.saldoPendiente.toLocaleString("en-US",{minimumFractionDigits:2})}</strong></div>
                            <div class="info-row"><span>Vence</span><strong>${p.fechaVencimiento}</strong></div>
                        </div>
                        ${p.estado==="ACTIVO"?`<button class="btn-accion btn-pagar" onclick="pagarCuotaPrestamo(${p.id})"> Pagar cuota</button>`:""}
                    </div>`).join("");
        });
}
function solicitarPrestamo() {
    const monto=document.getElementById("montoP").value;
    const tasa=document.getElementById("tasaP").value;
    const cuotas=document.getElementById("cuotasP").value;
    if(!monto||!tasa||!cuotas){alert("Completa todos los campos");return;}
    fetch(`/prestamos/solicitar?monto=${encodeURIComponent(monto)}&tasa=${encodeURIComponent(tasa)}&cuotas=${encodeURIComponent(cuotas)}`,{method:"POST"})
        .then(r=>{if(!r.ok)throw new Error("No se pudo registrar la solicitud");return r.json();})
        .then(()=>{cargarPrestamos();cargarNotificaciones();alert("Solicitud recibida; queda pendiente de aprobación y desembolso.");})
        .catch(e=>alert(e.message));
}
async function pagarCuotaPrestamo(id) {
    const idCuenta=await elegirCuentaDebito("Selecciona el número de la cuenta para pagar la cuota");
    if(!idCuenta)return;
    fetch(`/prestamos/pagar/${id}?idCuenta=${encodeURIComponent(idCuenta)}`,{method:"POST"})
        .then(r=>{if(!r.ok)throw new Error("No se pudo pagar la cuota");return r.json();})
        .then(()=>{cargarPrestamos();cargarNotificaciones();})
        .catch(e=>alert(e.message));
}

// ===== INVERSIONES =====
function cargarInversiones() {
    fetch("/inversiones")
        .then(r=>r.json())
        .then(data=>{
            document.getElementById("listaInversiones").innerHTML = data.length===0
                ? "<p class='empty'>No tienes inversiones</p>"
                : data.map(i=>`
                    <div class="item-card ${i.estado.toLowerCase()}">
                        <div class="item-header"><span> ${escapeHtml(i.tipo)}</span><span class="badge-estado">${i.estado}</span></div>
                        <div class="item-body">
                            <div class="info-row"><span>Monto</span><strong>$${i.monto.toLocaleString("en-US",{minimumFractionDigits:2})}</strong></div>
                            <div class="info-row"><span>Tasa</span><strong>${i.tasaInteres}% anual</strong></div>
                            <div class="info-row"><span>Plazo</span><strong>${i.plazoMeses} meses</strong></div>
                            <div class="info-row"><span>Rendimiento proyectado</span><strong class="verde">+$${i.rendimientoTotal.toLocaleString("en-US",{minimumFractionDigits:2})}</strong></div>
                            <div class="info-row"><span>Capital a devolver</span><strong>$${i.monto.toLocaleString("en-US",{minimumFractionDigits:2})}</strong></div>
                            <div class="info-row"><span>Vence</span><strong>${i.fechaVencimiento}</strong></div>
                        </div>
                        ${i.estado==="ACTIVA"?`<button class="btn-accion btn-retirar" onclick="retirarInversion(${i.id})"> Retirar</button>`:""}
                    </div>`).join("");
        });
}
function crearInversion() {
    const monto=document.getElementById("montoI").value;
    const tasa=document.getElementById("tasaI").value;
    const plazo=document.getElementById("plazoI").value;
    const tipo=document.getElementById("tipoI").value;
    const idCuenta=document.getElementById("cuentaOrigenInversion").value;
    if(!monto||!tasa||!plazo||!idCuenta){alert("Completa los campos y elige una cuenta débito");return;}
    const params=new URLSearchParams({idCuenta,monto,tasa,plazo,tipo});
    fetch(`/inversiones?${params}`,{method:"POST"})
        .then(r=>{if(!r.ok)throw new Error("Saldo insuficiente o datos inválidos");return r.json();})
        .then(()=>{cargarInversiones();cargarNotificaciones();alert("Inversión creada con fondos de tu cuenta débito.");})
        .catch(e=>alert(e.message));
}
async function retirarInversion(id) {
    if(!confirm("¿Retirar esta inversión?"))return;
    const idCuenta=await elegirCuentaDebito("Selecciona la cuenta que recibirá el capital al vencimiento");
    if(!idCuenta)return;
    fetch(`/inversiones/retirar/${id}?idCuenta=${encodeURIComponent(idCuenta)}`,{method:"POST"})
        .then(r=>{if(!r.ok)throw new Error("Solo se devuelve el capital al vencimiento");return r.json();})
        .then(()=>cargarInversiones()).catch(e=>alert(e.message));
}

// ===== BENEFICIARIOS =====
function cargarBeneficiarios() {
    fetch("/beneficiarios")
        .then(r=>r.json())
        .then(data=>{
            document.getElementById("listaBeneficiarios").innerHTML = data.length===0
                ? "<p class='empty'>Sin beneficiarios</p>"
                : data.map(b=>`
                    <div class="item-card">
                        <div class="item-header">
                            <span> ${escapeHtml(b.alias||b.nombre)}</span>
                            <button class="btn-accion btn-retirar" onclick="eliminarBeneficiario(${b.id})" style="padding:4px 10px;font-size:12px"></button>
                        </div>
                        <div class="item-body">
                            <div class="info-row"><span>Nombre</span><strong>${escapeHtml(b.nombre)}</strong></div>
                            <div class="info-row"><span>Cuenta</span><strong>${escapeHtml(b.numeroCuenta)}</strong></div>
                            <div class="info-row"><span>Banco</span><strong>${escapeHtml(b.banco)}</strong></div>
                        </div>
                    </div>`).join("");
        });
}
function agregarBeneficiario() {
    const nombre=document.getElementById("nombreB").value;
    const cuenta=document.getElementById("cuentaB").value;
    const banco=document.getElementById("bancoB").value;
    const alias=document.getElementById("aliasB").value;
    if(!nombre||!cuenta||!banco){alert("Completa los campos obligatorios");return;}
    const params = new URLSearchParams({nombre, numeroCuenta: cuenta, banco, alias});
    fetch(`/beneficiarios?${params}`,{method:"POST"})
        .then(()=>{
            cargarBeneficiarios();
            ["nombreB","cuentaB","bancoB","aliasB"].forEach(id=>document.getElementById(id).value="");
        });
}
function eliminarBeneficiario(id) {
    if(!confirm("¿Eliminar beneficiario?"))return;
    fetch(`/beneficiarios/${id}`,{method:"DELETE"}).then(()=>cargarBeneficiarios());
}

// ===== TRANSFERENCIAS =====
function cargarTransferencias() {
    fetch("/transferencias")
        .then(r=>r.json())
        .then(data=>{
            document.getElementById("listaTransferencias").innerHTML = data.length===0
                ? "<p class='empty'>Sin transferencias</p>"
                : data.map(t=>`
                    <div class="item-card ${t.estado==='FALLIDA'?'fallida':''}">
                        <div class="item-header"><span>↗ ${escapeHtml(t.nombreDestinatario)}</span><span class="badge-estado">${escapeHtml(t.estado)}</span></div>
                        <div class="item-body">
                            <div class="info-row"><span>Monto</span><strong class="rojo">-$${t.monto.toLocaleString("en-US",{minimumFractionDigits:2})}</strong></div>
                            <div class="info-row"><span>Destino</span><strong>${t.cuentaDestinoNumero}</strong></div>
                            <div class="info-row"><span>Descripción</span><strong>${escapeHtml(t.descripcion||"-")}</strong></div>
                            <div class="info-row"><span>Fecha</span><strong>${new Date(t.fecha).toLocaleString("es-CO")}</strong></div>
                        </div>
                    </div>`).join("");
        });
}
function cargarCuentasParaTransferencia() {
    fetch("/clientes/cuentas")
        .then(r => r.json())
        .then(cuentas => {
            const sel = document.getElementById("cuentaOrigenSelect");
            if (!sel) return;
            sel.innerHTML = "<option value=''>Selecciona una cuenta</option>";
            cuentas.forEach(c => {
                const disp = c.tipo === "DEBITO" ? c.saldo.toFixed(2) : c.disponible.toFixed(2);
                sel.innerHTML += `<option value="${c.id}">${c.tipo} - N ${c.numeroCuenta} | Disponible: $${disp}</option>`;
            });
        });
}

function realizarTransferencia() {
    const origenId=document.getElementById("cuentaOrigenSelect").value;
    const destino=document.getElementById("cuentaDestino").value;
    const dest=document.getElementById("destinatarioT").value;
    const monto=document.getElementById("montoT").value;
    const desc=document.getElementById("descripcionT").value||"Transferencia";
    if(!origenId||!destino||!dest||!monto){alert("Completa todos los campos");return;}
    const params = new URLSearchParams({cuentaOrigenId: origenId, cuentaDestino: destino, destinatario: dest, monto, descripcion: desc});
    fetch(`/transferencias?${params}`,{method:"POST"})
        .then(r=>r.json())
        .then(t=>{
            cargarTransferencias();cargarNotificaciones();cargarMovimientos();
            alert(t.estado==="EXITOSA"?"Transferencia exitosa":"Fondos insuficientes");
        });
}

function cerrarSesion() {
    fetch("/clientes/logout", { method: "POST" }).finally(() => {
        localStorage.clear();
        window.location.href="/html/Login.html";
    });
}
function toggleDark() {
    document.body.classList.toggle("dark");
    localStorage.setItem("bankl-theme", document.body.classList.contains("dark") ? "dark" : "light");
}
