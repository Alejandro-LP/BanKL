let clientesAdmin = [];
document.addEventListener("DOMContentLoaded", () => {
    const tipo = localStorage.getItem("tipo");
    if (localStorage.getItem("bankl-theme") === "dark") document.body.classList.add("dark");
    if (tipo !== "ClienteAdmin") {
        window.location.href = "/html/Login.html";
        return;
    }
    const busqueda = document.getElementById("buscarCliente");
    if (busqueda) busqueda.addEventListener("input", renderizarClientes);
    cargarClientesNaturales();
});

function escapeHtml(value) {
    return String(value ?? "").replace(/[&<>"']/g, char => ({"&":"&amp;","<":"&lt;",">":"&gt;",'"':"&quot;","'":"&#39;"})[char]);
}

function cargarClientesNaturales() {
    fetch("/api/admin/clientes")
        .then(res => { if (!res.ok) throw new Error("No se pudieron cargar los clientes"); return res.json(); })
        .then(data => {
            clientesAdmin = Array.isArray(data) ? data : [];
            const total = document.getElementById("totalClientes");
            if (total) total.textContent = clientesAdmin.length.toLocaleString("es-CO");
            renderizarClientes();
        })
        .catch(() => {
            document.getElementById("listaClientes").innerHTML =
                "<p class='empty'>No se pudieron cargar los clientes. Comprueba tu conexión e inténtalo de nuevo.</p>";
        });
}

function renderizarClientes() {
    const contenedor = document.getElementById("listaClientes");
    if (!contenedor) return;
    const consulta = (document.getElementById("buscarCliente")?.value || "").trim().toLocaleLowerCase("es");
    const filtrados = clientesAdmin.filter(cliente =>
        [cliente.nombres, cliente.apellidos, cliente.usuarioIS, cliente.email, cliente.telefono]
            .some(valor => String(valor || "").toLocaleLowerCase("es").includes(consulta))
    );
    contenedor.innerHTML = "";
    if (filtrados.length === 0) {
        contenedor.innerHTML = clientesAdmin.length
            ? "<p class='empty admin-empty'>No encontramos clientes con esa búsqueda.</p>"
            : "<p class='empty admin-empty'>Aún no hay clientes naturales registrados.</p>";
        return;
    }
    filtrados.forEach(c => {
        const div = document.createElement("article");
        div.classList.add("cliente-card");
        div.innerHTML = `
            <div class="cliente-header">
                <div class="cliente-identity"><span class="cliente-avatar">${escapeHtml((c.nombres || "C").trim().charAt(0).toUpperCase())}</span><div class="cliente-nombre">${escapeHtml(c.nombres)} ${escapeHtml(c.apellidos)}</div></div>
                <button class="btn-editar" data-id="${Number(c.idDB)}">Editar datos <span>↗</span></button>
            </div>
            <div class="cliente-info">
                <div class="info-item"><strong>${escapeHtml(c.id)}</strong>Documento</div>
                <div class="info-item"><strong>${escapeHtml(c.usuarioIS)}</strong>Usuario</div>
                <div class="info-item"><strong>${escapeHtml(c.email)}</strong>Correo</div>
                <div class="info-item"><strong>${escapeHtml(c.telefono)}</strong>Teléfono</div>
                <div class="info-item"><strong>${escapeHtml(c.direccion || "—")}</strong>Dirección</div>
            </div>`;
        div.querySelector(".btn-editar").addEventListener("click", () => abrirModal(c.idDB, c.nombres, c.apellidos, c.email, c.telefono, c.direccion));
        contenedor.appendChild(div);
    });
}
function abrirModal(idDB, nombres, apellidos, email, telefono, direccion) {
    document.getElementById("editIdDB").value = idDB;
    document.getElementById("editNombres").value = nombres;
    document.getElementById("editApellidos").value = apellidos;
    document.getElementById("editEmail").value = email;
    document.getElementById("editTelefono").value = telefono;
    document.getElementById("editDireccion").value = direccion || '';
    document.getElementById("msgExito").style.display = "none";
    document.getElementById("modalEditar").classList.add("active");
}

function cerrarModal() {
    document.getElementById("modalEditar").classList.remove("active");
}

function guardarCambios() {
    const idDB     = document.getElementById("editIdDB").value;
    const nombres  = document.getElementById("editNombres").value.trim();
    const apellidos= document.getElementById("editApellidos").value.trim();
    const email    = document.getElementById("editEmail").value.trim();
    const telefono = document.getElementById("editTelefono").value.trim();
    const direccion= document.getElementById("editDireccion").value.trim();

    if (!nombres || !apellidos || !email || !telefono) {
        alert("Completa todos los campos obligatorios");
        return;
    }

    fetch(`/api/admin/clientes/${idDB}`, {
        method: "PUT",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ nombres, apellidos, email, telefono, direccion })
    })
    .then(res => {
        if (!res.ok) throw new Error("Error");
        document.getElementById("msgExito").style.display = "block";
        setTimeout(() => {
            cerrarModal();
            cargarClientesNaturales();
        }, 1200);
    })
    .catch(() => alert("Error al guardar cambios"));
}

function cerrarSesion() {
    localStorage.clear();
    window.location.href = "/html/Login.html";
}

function toggleDark() {
    document.body.classList.toggle("dark");
    localStorage.setItem("bankl-theme", document.body.classList.contains("dark") ? "dark" : "light");
}
