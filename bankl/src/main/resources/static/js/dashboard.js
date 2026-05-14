const usuario = localStorage.getItem("usuario");
const nombre  = localStorage.getItem("nombre");

document.addEventListener("DOMContentLoaded", () => {
    if (!usuario || localStorage.getItem("tipo") !== "ClienteNatural") {
        window.location.href = "/html/Login.html";
        return;
    }
    if (nombre) {
        document.getElementById("bienvenida").textContent = "Bienvenido, " + nombre;
        document.getElementById("saludoNombre").textContent = "¡Bienvenido, " + nombre.split(" ")[0] + "!";
    }
    cargarNotificaciones();
    cargarResumen();
});

// ===== TABS =====
function irTab(tab) {
    document.querySelectorAll(".tab-content").forEach(t => t.classList.remove("active"));
    document.querySelectorAll(".tab-btn").forEach(t => t.classList.remove("active"));
    document.getElementById("tab-" + tab).classList.add("active");
    document.getElementById("btn-" + tab).classList.add("active");
    if (tab === "tarjetas")        cargarCuentasDash();
    if (tab === "prestamos")       cargarPrestamos();
    if (tab === "inversiones")     cargarInversiones();
    if (tab === "beneficiarios")   cargarBeneficiarios();
    if (tab === "transferencias")  cargarTransferencias();
    if (tab === "notificaciones")  cargarNotificaciones();
    if (tab === "inicio")          cargarResumen();
}

// ===== RESUMEN INICIO =====
function cargarResumen() {
    fetch(`http://localhost:8080/clientes/cuentas?usuario=${usuario}`)
        .then(r => r.json())
        .then(cuentas => {
            const debito  = cuentas.find(c => c.tipo === "DEBITO");
            const credito = cuentas.find(c => c.tipo === "CREDITO");
            document.getElementById("resumenDebito").textContent =
                "$" + (debito ? debito.saldo.toLocaleString("en-US",{minimumFractionDigits:2}) : "0.00");
            document.getElementById("resumenCredito").textContent =
                "$" + (credito ? credito.disponible.toLocaleString("en-US",{minimumFractionDigits:2}) : "0.00");
        }).catch(()=>{});

    fetch(`http://localhost:8080/inversiones?usuario=${usuario}`)
        .then(r => r.json())
        .then(data => {
            const total = data.filter(i=>i.estado==="ACTIVA").reduce((s,i)=>s+i.monto,0);
            document.getElementById("resumenInversiones").textContent =
                "$" + total.toLocaleString("en-US",{minimumFractionDigits:2});
        }).catch(()=>{});

    fetch(`http://localhost:8080/prestamos?usuario=${usuario}`)
        .then(r => r.json())
        .then(data => {
            const activos = data.filter(p=>p.estado==="ACTIVO").length;
            document.getElementById("resumenPrestamos").textContent = activos + " activo(s)";
        }).catch(()=>{});
}

// ===== TARJETAS =====
function cargarCuentasDash() {
    fetch(`http://localhost:8080/clientes/cuentas?usuario=${usuario}`)
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
                            <div><div class="tarjeta-label">TITULAR</div><div class="tarjeta-value">${cuenta.propietario.toUpperCase()}</div></div>
                            <div><div class="tarjeta-label">VENCE</div><div class="tarjeta-value">${cuenta.fechaExpiracion}</div></div>
                            <div><div class="tarjeta-label">CVV</div><div class="tarjeta-value">${cuenta.cvv}</div></div>
                        </div>
                    </div>
                    <div class="tarjeta-info">
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
                                <button class="btn-accion btn-avance" onclick="avanceCuenta(${cuenta.id})"> Avance</button>
                            ` : `
                                <button class="btn-accion btn-ingresar" onclick="ingresarCuenta(${cuenta.id})"> Ingresar</button>
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

function ingresarCuenta(id) {
    const v = prompt("Valor a ingresar ($):"); if (!v||isNaN(v)||v<=0) return;
    fetch(`http://localhost:8080/cuentas/consignar?idCuenta=${id}&valor=${v}`,{method:"POST"}).then(()=>cargarCuentasDash());
}
function retirarCuenta(id) {
    const v = prompt("Valor a retirar ($):"); if (!v||isNaN(v)||v<=0) return;
    fetch(`http://localhost:8080/cuentas/retirar?idCuenta=${id}&valor=${v}`,{method:"POST"}).then(()=>cargarCuentasDash());
}
function pagarCuenta(id) {
    const v = prompt("Valor a pagar ($):"); if (!v||isNaN(v)||v<=0) return;
    fetch(`http://localhost:8080/cuentas/consignar?idCuenta=${id}&valor=${v}`,{method:"POST"}).then(()=>cargarCuentasDash());
}
function avanceCuenta(id) {
    const v = prompt("Valor del avance ($):"); if (!v||isNaN(v)||v<=0) return;
    fetch(`http://localhost:8080/cuentas/retirar?idCuenta=${id}&valor=${v}`,{method:"POST"}).then(()=>cargarCuentasDash());
}
function cambiarTarjetaDash(id) {
    if (!confirm("¿Regenerar datos de esta tarjeta?")) return;
    fetch(`http://localhost:8080/cuentas/cambiar?idCuenta=${id}`,{method:"POST"})
        .then(r=>{ if(!r.ok) throw new Error("Error "+r.status); return r.text(); })
        .then(()=>setTimeout(()=>cargarCuentasDash(),300))
        .catch(e=>alert("Error: "+e));
}

// ===== NOTIFICACIONES =====
function cargarNotificaciones() {
    fetch(`http://localhost:8080/notificaciones?usuario=${usuario}`)
        .then(r=>r.json())
        .then(data=>{
            const badge = document.getElementById("badgeNoti");
            const noLeidas = data.filter(n=>!n.leida).length;
            badge.textContent = noLeidas||"";
            badge.style.display = noLeidas?"inline":"none";
            document.getElementById("listaNoti").innerHTML = data.length===0
                ? "<p class='empty'>Sin notificaciones</p>"
                : data.map(n=>`
                    <div class="noti-item ${n.leida?'leida':''}" onclick="leerNoti(${n.id},this)">
                        <span class="noti-tipo noti-${n.tipo.toLowerCase()}">${n.tipo}</span>
                        <strong>${n.titulo}</strong>
                        <p>${n.mensaje}</p>
                        <small>${new Date(n.fecha).toLocaleString("es-CO")}</small>
                    </div>`).join("");
        }).catch(()=>{});
}
function leerNoti(id,el) {
    fetch(`http://localhost:8080/notificaciones/leer/${id}`,{method:"POST"})
        .then(()=>{el.classList.add("leida");cargarNotificaciones();});
}

// ===== PRÉSTAMOS =====
function cargarPrestamos() {
    fetch(`http://localhost:8080/prestamos?usuario=${usuario}`)
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
    fetch(`http://localhost:8080/prestamos/solicitar?usuario=${usuario}&monto=${monto}&tasa=${tasa}&cuotas=${cuotas}`,{method:"POST"})
        .then(r=>r.json()).then(()=>{cargarPrestamos();cargarNotificaciones();alert("¡Préstamo aprobado!");});
}
function pagarCuotaPrestamo(id) {
    fetch(`http://localhost:8080/prestamos/pagar/${id}`,{method:"POST"})
        .then(()=>{cargarPrestamos();cargarNotificaciones();});
}

// ===== INVERSIONES =====
function cargarInversiones() {
    fetch(`http://localhost:8080/inversiones?usuario=${usuario}`)
        .then(r=>r.json())
        .then(data=>{
            document.getElementById("listaInversiones").innerHTML = data.length===0
                ? "<p class='empty'>No tienes inversiones</p>"
                : data.map(i=>`
                    <div class="item-card ${i.estado.toLowerCase()}">
                        <div class="item-header"><span> ${i.tipo}</span><span class="badge-estado">${i.estado}</span></div>
                        <div class="item-body">
                            <div class="info-row"><span>Monto</span><strong>$${i.monto.toLocaleString("en-US",{minimumFractionDigits:2})}</strong></div>
                            <div class="info-row"><span>Tasa</span><strong>${i.tasaInteres}% anual</strong></div>
                            <div class="info-row"><span>Plazo</span><strong>${i.plazoMeses} meses</strong></div>
                            <div class="info-row"><span>Rendimiento</span><strong class="verde">+$${i.rendimientoTotal.toLocaleString("en-US",{minimumFractionDigits:2})}</strong></div>
                            <div class="info-row"><span>Al vencer</span><strong>$${i.montoFinal.toLocaleString("en-US",{minimumFractionDigits:2})}</strong></div>
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
    if(!monto||!tasa||!plazo){alert("Completa todos los campos");return;}
    fetch(`http://localhost:8080/inversiones?usuario=${usuario}&monto=${monto}&tasa=${tasa}&plazo=${plazo}&tipo=${tipo}`,{method:"POST"})
        .then(r=>r.json()).then(()=>{cargarInversiones();cargarNotificaciones();alert("¡Inversión creada!");});
}
function retirarInversion(id) {
    if(!confirm("¿Retirar esta inversión?"))return;
    fetch(`http://localhost:8080/inversiones/retirar/${id}`,{method:"POST"}).then(()=>cargarInversiones());
}

// ===== BENEFICIARIOS =====
function cargarBeneficiarios() {
    fetch(`http://localhost:8080/beneficiarios?usuario=${usuario}`)
        .then(r=>r.json())
        .then(data=>{
            document.getElementById("listaBeneficiarios").innerHTML = data.length===0
                ? "<p class='empty'>Sin beneficiarios</p>"
                : data.map(b=>`
                    <div class="item-card">
                        <div class="item-header">
                            <span> ${b.alias||b.nombre}</span>
                            <button class="btn-accion btn-retirar" onclick="eliminarBeneficiario(${b.id})" style="padding:4px 10px;font-size:12px"></button>
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
    const nombre=document.getElementById("nombreB").value;
    const cuenta=document.getElementById("cuentaB").value;
    const banco=document.getElementById("bancoB").value;
    const alias=document.getElementById("aliasB").value;
    if(!nombre||!cuenta||!banco){alert("Completa los campos obligatorios");return;}
    fetch(`http://localhost:8080/beneficiarios?usuario=${usuario}&nombre=${encodeURIComponent(nombre)}&numeroCuenta=${cuenta}&banco=${encodeURIComponent(banco)}&alias=${encodeURIComponent(alias)}`,{method:"POST"})
        .then(()=>{
            cargarBeneficiarios();
            ["nombreB","cuentaB","bancoB","aliasB"].forEach(id=>document.getElementById(id).value="");
        });
}
function eliminarBeneficiario(id) {
    if(!confirm("¿Eliminar beneficiario?"))return;
    fetch(`http://localhost:8080/beneficiarios/${id}`,{method:"DELETE"}).then(()=>cargarBeneficiarios());
}

// ===== TRANSFERENCIAS =====
function cargarTransferencias() {
    fetch(`http://localhost:8080/transferencias?usuario=${usuario}`)
        .then(r=>r.json())
        .then(data=>{
            document.getElementById("listaTransferencias").innerHTML = data.length===0
                ? "<p class='empty'>Sin transferencias</p>"
                : data.map(t=>`
                    <div class="item-card ${t.estado==='FALLIDA'?'fallida':''}">
                        <div class="item-header"><span>↗ ${t.nombreDestinatario}</span><span class="badge-estado">${t.estado}</span></div>
                        <div class="item-body">
                            <div class="info-row"><span>Monto</span><strong class="rojo">-$${t.monto.toLocaleString("en-US",{minimumFractionDigits:2})}</strong></div>
                            <div class="info-row"><span>Destino</span><strong>${t.cuentaDestinoNumero}</strong></div>
                            <div class="info-row"><span>Descripción</span><strong>${t.descripcion||"-"}</strong></div>
                            <div class="info-row"><span>Fecha</span><strong>${new Date(t.fecha).toLocaleString("es-CO")}</strong></div>
                        </div>
                    </div>`).join("");
        });
}
function realizarTransferencia() {
    const origen=document.getElementById("cuentaOrigen").value;
    const destino=document.getElementById("cuentaDestino").value;
    const dest=document.getElementById("destinatarioT").value;
    const monto=document.getElementById("montoT").value;
    const desc=document.getElementById("descripcionT").value||"Transferencia";
    if(!origen||!destino||!dest||!monto){alert("Completa todos los campos");return;}
    fetch(`http://localhost:8080/transferencias?usuario=${usuario}&cuentaOrigen=${origen}&cuentaDestino=${destino}&destinatario=${encodeURIComponent(dest)}&monto=${monto}&descripcion=${encodeURIComponent(desc)}`,{method:"POST"})
        .then(r=>r.json())
        .then(t=>{
            cargarTransferencias();cargarNotificaciones();
            alert(t.estado==="EXITOSA"?" Transferencia exitosa":" Fondos insuficientes");
        });
}

function cerrarSesion() { localStorage.clear(); window.location.href="/html/Login.html"; }
function toggleDark() { document.body.classList.toggle("dark"); }