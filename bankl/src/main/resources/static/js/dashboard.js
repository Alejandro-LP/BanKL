const usuario = localStorage.getItem("usuario");
const nombre = localStorage.getItem("nombre");

document.addEventListener("DOMContentLoaded", () => {
    if (!usuario || localStorage.getItem("tipo") !== "ClienteNatural") {
        window.location.href = "/html/Login.html";
        return;
    }
    if (nombre) document.getElementById("bienvenida").textContent = "Bienvenido, " + nombre;
    cargarNotificaciones();
});

// ===== NOTIFICACIONES =====
function cargarNotificaciones() {
    fetch(`http://localhost:8080/notificaciones?usuario=${usuario}`)
        .then(r => r.json())
        .then(data => {
            const lista = document.getElementById("listaNoti");
            const badge = document.getElementById("badgeNoti");
            const noLeidas = data.filter(n => !n.leida).length;
            badge.textContent = noLeidas || "";
            badge.style.display = noLeidas ? "inline" : "none";
            lista.innerHTML = data.length === 0
                ? "<p class='empty'>Sin notificaciones</p>"
                : data.map(n => `
                    <div class="noti-item ${n.leida ? 'leida' : ''}" onclick="leerNoti(${n.id}, this)">
                        <span class="noti-tipo noti-${n.tipo.toLowerCase()}">${n.tipo}</span>
                        <strong>${n.titulo}</strong>
                        <p>${n.mensaje}</p>
                        <small>${new Date(n.fecha).toLocaleString("es-CO")}</small>
                    </div>`).join("");
        }).catch(() => {});
}

function leerNoti(id, el) {
    fetch(`http://localhost:8080/notificaciones/leer/${id}`, { method: "POST" })
        .then(() => { el.classList.add("leida"); cargarNotificaciones(); });
}

// ===== PRÉSTAMOS =====
function cargarPrestamos() {
    fetch(`http://localhost:8080/prestamos?usuario=${usuario}`)
        .then(r => r.json())
        .then(data => {
            document.getElementById("listaPrestamos").innerHTML = data.length === 0
                ? "<p class='empty'>No tienes préstamos activos</p>"
                : data.map(p => `
                    <div class="item-card ${p.estado.toLowerCase()}">
                        <div class="item-header">
                            <span>💰 Préstamo #${p.id}</span>
                            <span class="badge-estado">${p.estado}</span>
                        </div>
                        <div class="item-body">
                            <div class="info-row"><span>Monto</span><strong>$${p.monto.toLocaleString("en-US",{minimumFractionDigits:2})}</strong></div>
                            <div class="info-row"><span>Cuota mensual</span><strong>$${p.valorCuota.toLocaleString("en-US",{minimumFractionDigits:2})}</strong></div>
                            <div class="info-row"><span>Progreso</span><strong>${p.cuotasPagadas}/${p.numeroCuotas} cuotas</strong></div>
                            <div class="info-row"><span>Saldo pendiente</span><strong class="rojo">$${p.saldoPendiente.toLocaleString("en-US",{minimumFractionDigits:2})}</strong></div>
                            <div class="info-row"><span>Vence</span><strong>${p.fechaVencimiento}</strong></div>
                        </div>
                        ${p.estado === "ACTIVO" ? `<button class="btn-accion btn-pagar" onclick="pagarCuota(${p.id})"> Pagar cuota</button>` : ""}
                    </div>`).join("");
        });
}

function solicitarPrestamo() {
    const monto = document.getElementById("montoP").value;
    const tasa = document.getElementById("tasaP").value;
    const cuotas = document.getElementById("cuotasP").value;
    if (!monto || !tasa || !cuotas) return alert("Completa todos los campos");
    fetch(`http://localhost:8080/prestamos/solicitar?usuario=${usuario}&monto=${monto}&tasa=${tasa}&cuotas=${cuotas}`, { method: "POST" })
        .then(r => r.json())
        .then(() => { cargarPrestamos(); cargarNotificaciones(); alert("¡Préstamo aprobado!"); });
}

function pagarCuota(id) {
    fetch(`http://localhost:8080/prestamos/pagar/${id}`, { method: "POST" })
        .then(() => { cargarPrestamos(); cargarNotificaciones(); });
}

// ===== INVERSIONES =====
function cargarInversiones() {
    fetch(`http://localhost:8080/inversiones?usuario=${usuario}`)
        .then(r => r.json())
        .then(data => {
            document.getElementById("listaInversiones").innerHTML = data.length === 0
                ? "<p class='empty'>No tienes inversiones activas</p>"
                : data.map(i => `
                    <div class="item-card ${i.estado.toLowerCase()}">
                        <div class="item-header">
                            <span> ${i.tipo}</span>
                            <span class="badge-estado">${i.estado}</span>
                        </div>
                        <div class="item-body">
                            <div class="info-row"><span>Monto invertido</span><strong>$${i.monto.toLocaleString("en-US",{minimumFractionDigits:2})}</strong></div>
                            <div class="info-row"><span>Tasa</span><strong>${i.tasaInteres}% anual</strong></div>
                            <div class="info-row"><span>Plazo</span><strong>${i.plazoMeses} meses</strong></div>
                            <div class="info-row"><span>Rendimiento</span><strong class="verde">+$${i.rendimientoTotal.toLocaleString("en-US",{minimumFractionDigits:2})}</strong></div>
                            <div class="info-row"><span>Al vencer recibes</span><strong>$${i.montoFinal.toLocaleString("en-US",{minimumFractionDigits:2})}</strong></div>
                            <div class="info-row"><span>Vence</span><strong>${i.fechaVencimiento}</strong></div>
                        </div>
                        ${i.estado === "ACTIVA" ? `<button class="btn-accion btn-retirar" onclick="retirarInversion(${i.id})">📤 Retirar</button>` : ""}
                    </div>`).join("");
        });
}

function crearInversion() {
    const monto = document.getElementById("montoI").value;
    const tasa = document.getElementById("tasaI").value;
    const plazo = document.getElementById("plazoI").value;
    const tipo = document.getElementById("tipoI").value;
    if (!monto || !tasa || !plazo) return alert("Completa todos los campos");
    fetch(`http://localhost:8080/inversiones?usuario=${usuario}&monto=${monto}&tasa=${tasa}&plazo=${plazo}&tipo=${tipo}`, { method: "POST" })
        .then(r => r.json())
        .then(() => { cargarInversiones(); cargarNotificaciones(); alert("¡Inversión creada!"); });
}

function retirarInversion(id) {
    if (!confirm("¿Retirar esta inversión antes de vencer?")) return;
    fetch(`http://localhost:8080/inversiones/retirar/${id}`, { method: "POST" })
        .then(() => cargarInversiones());
}

// ===== BENEFICIARIOS =====
function cargarBeneficiarios() {
    fetch(`http://localhost:8080/beneficiarios?usuario=${usuario}`)
        .then(r => r.json())
        .then(data => {
            document.getElementById("listaBeneficiarios").innerHTML = data.length === 0
                ? "<p class='empty'>No tienes beneficiarios guardados</p>"
                : data.map(b => `
                    <div class="item-card">
                        <div class="item-header">
                            <span>👤 ${b.alias || b.nombre}</span>
                            <button class="btn-accion btn-retirar" onclick="eliminarBeneficiario(${b.id})" style="padding:4px 10px;font-size:12px">✕</button>
                        </div>
                        <div class="item-body">
                            <div class="info-row"><span>Nombre</span><strong>${b.nombre}</strong></div>
                            <div class="info-row"><span>Cuenta</span><strong>${b.numeroCuenta}</strong></div>
                            <div class="info-row"><span>Banco</span><strong>${b.banco}</strong></div>
                        </div>
                    </div>`).join("");
        });
}

function agregarBeneficiario() {
    const nombre = document.getElementById("nombreB").value;
    const cuenta = document.getElementById("cuentaB").value;
    const banco = document.getElementById("bancoB").value;
    const alias = document.getElementById("aliasB").value;
    if (!nombre || !cuenta || !banco) return alert("Completa los campos obligatorios");
    fetch(`http://localhost:8080/beneficiarios?usuario=${usuario}&nombre=${nombre}&numeroCuenta=${cuenta}&banco=${banco}&alias=${alias}`, { method: "POST" })
        .then(() => { cargarBeneficiarios(); document.getElementById("formBeneficiario").reset(); });
}

function eliminarBeneficiario(id) {
    if (!confirm("¿Eliminar beneficiario?")) return;
    fetch(`http://localhost:8080/beneficiarios/${id}`, { method: "DELETE" })
        .then(() => cargarBeneficiarios());
}

// ===== TRANSFERENCIAS =====
function cargarTransferencias() {
    fetch(`http://localhost:8080/transferencias?usuario=${usuario}`)
        .then(r => r.json())
        .then(data => {
            document.getElementById("listaTransferencias").innerHTML = data.length === 0
                ? "<p class='empty'>Sin transferencias recientes</p>"
                : data.map(t => `
                    <div class="item-card ${t.estado === 'FALLIDA' ? 'rechazado' : ''}">
                        <div class="item-header">
                            <span>↗ ${t.nombreDestinatario}</span>
                            <span class="badge-estado">${t.estado}</span>
                        </div>
                        <div class="item-body">
                            <div class="info-row"><span>Monto</span><strong class="rojo">-$${t.monto.toLocaleString("en-US",{minimumFractionDigits:2})}</strong></div>
                            <div class="info-row"><span>Destino</span><strong>${t.cuentaDestinoNumero}</strong></div>
                            <div class="info-row"><span>Descripción</span><strong>${t.descripcion || "-"}</strong></div>
                            <div class="info-row"><span>Fecha</span><strong>${new Date(t.fecha).toLocaleString("es-CO")}</strong></div>
                        </div>
                    </div>`).join("");
        });
}

function realizarTransferencia() {
    const origen = document.getElementById("cuentaOrigen").value;
    const destino = document.getElementById("cuentaDestino").value;
    const destinatario = document.getElementById("destinatarioT").value;
    const monto = document.getElementById("montoT").value;
    const descripcion = document.getElementById("descripcionT").value || "Transferencia";
    if (!origen || !destino || !destinatario || !monto) return alert("Completa todos los campos");
    fetch(`http://localhost:8080/transferencias?usuario=${usuario}&cuentaOrigen=${origen}&cuentaDestino=${destino}&destinatario=${destinatario}&monto=${monto}&descripcion=${encodeURIComponent(descripcion)}`, { method: "POST" })
        .then(r => r.json())
        .then(t => {
            cargarTransferencias();
            cargarNotificaciones();
            alert(t.estado === "EXITOSA" ? "✅ Transferencia exitosa" : "❌ Fondos insuficientes");
        });
}

// ===== TABS =====
function mostrarTab(tab) {
    document.querySelectorAll(".tab-content").forEach(t => t.classList.remove("active"));
    document.querySelectorAll(".tab-btn").forEach(t => t.classList.remove("active"));
    document.getElementById("tab-" + tab).classList.add("active");
    event.target.classList.add("active");
    if (tab === "prestamos") cargarPrestamos();
    if (tab === "inversiones") cargarInversiones();
    if (tab === "beneficiarios") cargarBeneficiarios();
    if (tab === "transferencias") cargarTransferencias();
    if (tab === "notificaciones") cargarNotificaciones();
}

function toggleDark() { document.body.classList.toggle("dark"); }