document.addEventListener("DOMContentLoaded", () => {
    const tipo = localStorage.getItem("tipo");
    if (tipo !== "ClienteAdmin") {
        window.location.href = "/html/Login.html";
        return;
    }
    cargarClientesNaturales();
});

function cargarClientesNaturales() {
    fetch("http://localhost:8080/api/admin/clientes")
        .then(res => res.json())
        .then(data => {
            const contenedor = document.getElementById("clientes");
            contenedor.innerHTML = "";

            if (data.length === 0) {
                contenedor.innerHTML = "<p>No hay clientes naturales registrados.</p>";
                return;
            }

            data.forEach(c => {
                const div = document.createElement("div");
                div.classList.add("card-content");
                div.innerHTML = `
                    <strong>👤 ${c.nombres} ${c.apellidos}</strong><br>
                    Documento: ${c.id} &nbsp;|&nbsp;
                    Usuario: ${c.usuarioIS} &nbsp;|&nbsp;
                    Email: ${c.email} &nbsp;|&nbsp;
                    Teléfono: ${c.telefono}
                `;
                contenedor.appendChild(div);
            });
        })
        .catch(() => {
            document.getElementById("clientes").innerHTML =
                "<p style='color:red'>Error al cargar clientes.</p>";
        });
}

function toggleDark() {
    document.body.classList.toggle("dark");
}