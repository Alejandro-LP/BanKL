document.addEventListener("DOMContentLoaded", () => {
    cargarClientes();
    cargarAlertas();
});

function cargarClientes() {
    fetch("http://localhost:8080/api/admin/clientes")
        .then(res => res.json())
        .then(data => {

            const contenedor = document.getElementById("clientes");
            contenedor.innerHTML = "";

            data.forEach(c => {

                const div = document.createElement("div");
                div.classList.add("card-content");

                div.innerHTML = `
                    👤 ${c.nombre} - ${c.id}
                    <button onclick="bloquear(${c.id})">Bloquear tarjetas</button>
                `;

                contenedor.appendChild(div);
            });
        });
}

function cargarAlertas() {
    fetch("http://localhost:8080/api/alertas")
        .then(res => res.json())
        .then(data => {

            const contenedor = document.getElementById("alertas");
            contenedor.innerHTML = "";

            data.forEach(a => {
                const div = document.createElement("div");
                div.classList.add("card-content");

                div.innerHTML = `⚠️ ${a.tipo} - ${a.descripcion}`;
                contenedor.appendChild(div);
            });
        });
}

function bloquear(id) {
    fetch(`http://localhost:8080/api/admin/bloquear?id=${id}`, {
        method: "POST"
    }).then(() => {
        alert("Tarjetas bloqueadas 🔒");
    });
}