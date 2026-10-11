function escapeHtml(value) { return String(value ?? "").replace(/[&<>"']/g, c => ({"&":"&amp;","<":"&lt;",">":"&gt;",'"':"&quot;","'":"&#39;"})[c]); }document.addEventListener("DOMContentLoaded", () => {
    cargarAlertas();
    setInterval(cargarAlertas, 5000); // cada 5 segundos
});

function cargarAlertas() {

    fetch("/api/alertas")
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
                    <div class="alerta-tipo">${escapeHtml(a.tipo)}</div>
                    <div class="alerta-desc">${escapeHtml(a.descripcion)}</div>
                    <div class="alerta-id">#${a.id}</div>
                `;

                contenedor.appendChild(div);
            });
        })
        .catch(() => {
            document.getElementById("listaAlertas").innerText = "Error cargando alertas ⚠️";
        });
}