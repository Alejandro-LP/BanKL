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
            const contenedor = document.getElementById("listaClientes");
            contenedor.innerHTML = "";

            if (data.length === 0) {
                contenedor.innerHTML = "<p class='empty'>No hay clientes naturales registrados.</p>";
                return;
            }

            data.forEach(c => {
                const div = document.createElement("div");
                div.classList.add("cliente-card");
                div.innerHTML = `
                    <div class="cliente-header">
                        <div class="cliente-nombre">${c.nombres} ${c.apellidos}</div>
                        <button class="btn-editar" onclick="abrirModal(${c.idDB}, '${c.nombres}', '${c.apellidos}', '${c.email}', '${c.telefono}', '${c.direccion}')">
                            Editar
                        </button>
                    </div>
                    <div class="cliente-info">
                        <div class="info-item"><strong>${c.id}</strong>Documento</div>
                        <div class="info-item"><strong>${c.usuarioIS}</strong>Usuario</div>
                        <div class="info-item"><strong>${c.email}</strong>Email</div>
                        <div class="info-item"><strong>${c.telefono}</strong>Telefono</div>
                        <div class="info-item"><strong>${c.direccion || '-'}</strong>Direccion</div>
                    </div>
                `;
                contenedor.appendChild(div);
            });
        })
        .catch(() => {
            document.getElementById("listaClientes").innerHTML =
                "<p style='color:red;padding:20px'>Error al cargar clientes.</p>";
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

    fetch(`http://localhost:8080/api/admin/clientes/${idDB}`, {
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

function toggleDark() { document.body.classList.toggle("dark"); }