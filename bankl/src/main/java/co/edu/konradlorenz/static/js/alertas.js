document.addEventListener("DOMContentLoaded", () => {
    cargarAlertas();
    setInterval(cargarAlertas, 5000); // cada 5 segundos
});

function cargarAlertas() {

    fetch("http://localhost:8080/api/alertas")
        .then(res => res.json())
        .then(alertas => {

            const contenedor = document.getElementById("listaAlertas");
            contenedor.innerHTML = "";

            if (alertas.length === 0) {
                contenedor.innerHTML = "<p>No hay alertas 🔕</p>";
                return;
            }

            alertas.forEach(a => {

                const div = document.createElement("div");
                div.classList.add("alerta-card");

                div.innerHTML = `
                    <div class="alerta-tipo">${a.tipo}</div>
                    <div class="alerta-desc">${a.descripcion}</div>
                    <div class="alerta-id">#${a.id}</div>
                `;

                contenedor.appendChild(div);
            });
        })
        .catch(() => {
            document.getElementById("listaAlertas").innerText = "Error cargando alertas ⚠️";
        });
}