document.addEventListener("DOMContentLoaded", () => {
    const usuario = localStorage.getItem("usuario");
    const tipo = localStorage.getItem("tipo");

    if (!usuario || tipo !== "ClienteNatural") {
        window.location.href = "/html/Login.html";
        return;
    }

    const nombre = localStorage.getItem("nombre");
    if (nombre) {
        const sub = document.querySelector("header small");
        if (sub) sub.textContent = "Bienvenido, " + nombre;
    }

    cargarCuentas();
});

function cargarCuentas() {
    const usuario = localStorage.getItem("usuario");

    fetch(`http://localhost:8080/clientes/cuentas?usuario=${usuario}`)
        .then(res => res.json())
        .then(cuentas => {
            const contenedor = document.getElementById("contenedorCuentas");
            contenedor.innerHTML = "";

            if (!cuentas || cuentas.length === 0) {
                contenedor.innerHTML = "<p style='padding:40px;color:#888'>No tienes cuentas registradas.</p>";
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
                        <div class="chip">
                            <div class="chip-inner"></div>
                        </div>
                        <div class="tarjeta-numero">${cuenta.numeroTarjeta}</div>
                        <div class="tarjeta-bottom">
                            <div>
                                <div class="tarjeta-label">TITULAR</div>
                                <div class="tarjeta-value">${cuenta.propietario.toUpperCase()}</div>
                            </div>
                            <div>
                                <div class="tarjeta-label">VENCE</div>
                                <div class="tarjeta-value">${cuenta.fechaExpiracion}</div>
                            </div>
                            <div>
                                <div class="tarjeta-label">CVV</div>
                                <div class="tarjeta-value">${cuenta.cvv}</div>
                            </div>
                        </div>
                    </div>

                    <div class="tarjeta-info">
                        ${esCredito ? `
                            <div class="info-row"><span>Cupo total</span><strong>$${cuenta.cupo.toLocaleString("en-US", {minimumFractionDigits: 2})}</strong></div>
                            <div class="info-row"><span>Disponible</span><strong class="verde">$${cuenta.disponible.toLocaleString("en-US", {minimumFractionDigits: 2})}</strong></div>
                            <div class="info-row"><span>Deuda</span><strong class="${cuenta.saldo > 0 ? 'rojo' : ''}">$${cuenta.saldo.toLocaleString("en-US", {minimumFractionDigits: 2})}</strong></div>
                        ` : `
                            <div class="info-row"><span>Saldo disponible</span><strong class="verde">$${cuenta.saldo.toLocaleString("en-US", {minimumFractionDigits: 2})}</strong></div>
                        `}
                        <div class="tarjeta-acciones">
                            ${esCredito ? `
                                <button class="btn-accion btn-pagar" onclick="pagar(${cuenta.id})">💸 Pagar</button>
                                <button class="btn-accion btn-avance" onclick="avance(${cuenta.id})">💳 Avance</button>
                            ` : `
                                <button class="btn-accion btn-ingresar" onclick="ingresar(${cuenta.id})">➕ Ingresar</button>
                                <button class="btn-accion btn-retirar" onclick="retirar(${cuenta.id})">➖ Retirar</button>
                            `}
                            <button class="btn-accion btn-cambiar" onclick="cambiarTarjeta(${cuenta.id})">🔄 Cambiar</button>
                        </div>
                    </div>
                `;

                contenedor.appendChild(wrapper);
            });
        })
        .catch(err => {
            console.error("Error:", err);
            document.getElementById("contenedorCuentas").innerHTML =
                "<p style='padding:40px;color:red'>Error cargando cuentas ⚠️</p>";
        });
}

function ingresar(id) {
    const valor = prompt("Valor a ingresar ($):");
    if (!valor || isNaN(valor) || valor <= 0) return;
    fetch(`http://localhost:8080/cuentas/consignar?idCuenta=${id}&valor=${valor}`, { method: "POST" })
        .then(() => cargarCuentas());
}

function retirar(id) {
    const valor = prompt("Valor a retirar ($):");
    if (!valor || isNaN(valor) || valor <= 0) return;
    fetch(`http://localhost:8080/cuentas/retirar?idCuenta=${id}&valor=${valor}`, { method: "POST" })
        .then(() => cargarCuentas());
}

function pagar(id) {
    const valor = prompt("Valor a pagar ($):");
    if (!valor || isNaN(valor) || valor <= 0) return;
    fetch(`http://localhost:8080/cuentas/consignar?idCuenta=${id}&valor=${valor}`, { method: "POST" })
        .then(() => cargarCuentas());
}

function avance(id) {
    const valor = prompt("Valor del avance ($):");
    if (!valor || isNaN(valor) || valor <= 0) return;
    fetch(`http://localhost:8080/cuentas/retirar?idCuenta=${id}&valor=${valor}`, { method: "POST" })
        .then(() => cargarCuentas());
}

function cambiarTarjeta(id) {
    if (!confirm("¿Deseas regenerar los datos de esta tarjeta?")) return;
    fetch(`http://localhost:8080/cuentas/cambiar?idCuenta=${id}`, { method: "POST" })
        .then(() => cargarCuentas());
}

function toggleDark() {
    document.body.classList.toggle("dark");
}