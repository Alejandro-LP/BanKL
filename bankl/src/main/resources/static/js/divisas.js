document.addEventListener("DOMContentLoaded", cargarHistorial);

// 💱 CONVERTIR
function convertir() {

    const cuentaId = document.getElementById("cuentaId").value;
    const monto = document.getElementById("monto").value;
    const moneda = document.getElementById("moneda").value;

    fetch(`/api/divisas/convertir?cuentaId=${cuentaId}&monto=${monto}&moneda=${moneda}`)
        .then(res => res.json())
        .then(data => {

            document.getElementById("resultado").innerText =
                `Resultado: ${data.resultado} ${moneda}`;

            cargarHistorial();
        })
        .catch(() => {
            document.getElementById("resultado").innerText = "Error ⚠️";
        });
}



function cargarHistorial() {

    fetch("/api/divisas/historial")
        .then(res => res.json())
        .then(lista => {

            const contenedor = document.getElementById("historial");
            contenedor.innerHTML = "";

            lista.forEach(c => {

                const div = document.createElement("div");
                div.classList.add("card");

                div.innerHTML = `
                    <div class="card-content">
                        ${c.montoCOP} COP → ${c.resultado} ${String(c.monedaDestino ?? "").replace(/[&<>"']/g, "") }
                    </div>
                `;

                contenedor.appendChild(div);
            });
        });
}