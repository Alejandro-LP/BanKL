document.addEventListener("DOMContentLoaded", cargarCuentas);

function cargarCuentas() {

    fetch("http://localhost:8080/api/clientes/cuentas")
        .then(res => res.json())
        .then(cuentas => {

            const contenedor = document.getElementById("contenedorCuentas");
            contenedor.innerHTML = "";

            cuentas.forEach(cuenta => {

                const card = document.createElement("div");
                card.classList.add("card");

                let contenido = "";

                if (cuenta.tipo === "DEBITO") {
                    contenido = `
                        <div class="card-title">Cuenta de Ahorros</div>
                        <div class="card-content">
                            <b>Número:</b> ${cuenta.numeroTarjeta}<br>
                            <b>Saldo:</b> $${cuenta.saldo}<br>
                            <b>Vencimiento:</b> ${cuenta.fechaExpiracion}
                        </div>
                        <div class="acciones">
                            <button onclick="ingresar()">Ingresar</button>
                            <button onclick="retirar()">Retirar</button>
                            <button onclick="cambiarTarjeta()">Cambiar Tarjeta</button>
                        </div>
                    `;
                }

                if (cuenta.tipo === "CREDITO") {
                    contenido = `
                        <div class="card-title">Tarjeta de Crédito</div>
                        <div class="card-content">
                            <b>Número:</b> ${cuenta.numeroTarjeta}<br>
                            <b>Deuda:</b> $${Math.abs(cuenta.saldo)}<br>
                            <b>Disponible:</b> $${cuenta.disponible}<br>
                            <b>Vencimiento:</b> ${cuenta.fechaExpiracion}
                        </div>
                        <div class="acciones">
                            <button onclick="pagar()">Pagar</button>
                            <button onclick="avance()">Avance</button>
                            <button onclick="cambiarTarjeta()">Cambiar Tarjeta</button>
                        </div>
                    `;
                }

                card.innerHTML = contenido;
                contenedor.appendChild(card);
            });

        })
        .catch(() => {
            document.getElementById("contenedorCuentas").innerText = "Error cargando cuentas ⚠️";
        });
}


// FUNCIONES (puedes conectarlas después)
function ingresar() {
    alert("Ingreso");
}

function retirar() {
    alert("Retiro");
}

function pagar() {
    alert("Pago");
}

function avance() {
    alert("Avance");
}

function cambiarTarjeta() {
    alert("Tarjeta cambiada");
}
document.addEventListener("DOMContentLoaded", () => {
    cargarCuentas();
    cargarAlertas();
});

function cargarCuentas() {

    fetch("http://localhost:8080/api/clientes/cuentas")
        .then(res => res.json())
        .then(cuentas => {

            const contenedor = document.getElementById("contenedorCuentas");
            contenedor.innerHTML = "";

            cuentas.forEach(cuenta => {

                const div = document.createElement("div");
                div.classList.add("card");

                let tarjetaClass = cuenta.tipo === "CREDITO" ? "credito" : "debito";

                div.innerHTML = `
                    <div class="tarjeta ${tarjetaClass}">
                        <div>BanKL</div>
                        <div class="numero">${cuenta.numeroTarjeta}</div>
                        <div class="info">
                            ${cuenta.tipo === "CREDITO"
                                ? `Deuda: $${Math.abs(cuenta.saldo)}<br>Disponible: $${cuenta.disponible}`
                                : `Saldo: $${cuenta.saldo}`
                            }<br>
                            Vence: ${cuenta.fechaExpiracion}
                        </div>
                    </div>

                    <div class="acciones">
                        ${
                            cuenta.tipo === "DEBITO"
                            ? `
                                <button onclick="ingresar(${cuenta.numeroCuenta})">Ingresar</button>
                                <button onclick="retirar(${cuenta.numeroCuenta})">Retirar</button>
                              `
                            : `
                                <button onclick="pagar(${cuenta.numeroCuenta})">Pagar</button>
                                <button onclick="avance(${cuenta.numeroCuenta})">Avance</button>
                              `
                        }
                        <button onclick="cambiarTarjeta(${cuenta.numeroCuenta})">Cambiar</button>
                    </div>
                `;

                contenedor.appendChild(div);
            });
        });
}



function ingresar(id) {
    let valor = prompt("Valor a ingresar:");
    fetch(`http://localhost:8080/api/cuentas/ingresar?id=${id}&valor=${valor}`, { method: "POST" })
        .then(() => cargarCuentas());
}


function retirar(id) {
    let valor = prompt("Valor a retirar:");
    fetch(`http://localhost:8080/api/cuentas/retirar?id=${id}&valor=${valor}`, { method: "POST" })
        .then(() => cargarCuentas());
}


function pagar(id) {
    let valor = prompt("Valor a pagar:");
    fetch(`http://localhost:8080/api/cuentas/pagar?id=${id}&valor=${valor}`, { method: "POST" })
        .then(() => cargarCuentas());
}

function avance(id) {
    let valor = prompt("Valor avance:");
    fetch(`http://localhost:8080/api/cuentas/avance?id=${id}&valor=${valor}`, { method: "POST" })
        .then(() => cargarCuentas());
}



function cambiarTarjeta(id) {
    fetch(`http://localhost:8080/api/cuentas/cambiar?id=${id}`, {
        method: "POST"
    }).then(() => {
        mostrarAlerta("Tarjeta regenerada 🔄");
        cargarCuentas();
    });
}


function cargarAlertas() {
    setInterval(() => {
        fetch("http://localhost:8080/api/alertas")
            .then(res => res.json())
            .then(alertas => {

                const contenedor = document.getElementById("alertas");
                contenedor.innerHTML = "";

                alertas.forEach(a => {
                    const div = document.createElement("div");
                    div.classList.add("alerta");
                    div.innerText = a.descripcion;
                    contenedor.appendChild(div);
                });
            });
    }, 5000);
}


function mostrarAlerta(msg) {
    const contenedor = document.getElementById("alertas");

    const div = document.createElement("div");
    div.classList.add("alerta");
    div.innerText = msg;

    contenedor.appendChild(div);

    setTimeout(() => div.remove(), 3000);
}