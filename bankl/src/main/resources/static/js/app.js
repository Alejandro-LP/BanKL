function mostrarMensaje(id, msg, tipo) {
    const div = document.getElementById(id);
    if (!div) return;

    div.innerText = msg;
    div.className = `feedback ${tipo === "error" ? "feedback-error" : "feedback-success"}`;
}


async function login() {
    const usuario = document.getElementById("usuario").value.trim();
    const contrasena = document.getElementById("contrasena").value.trim();
    const pin = document.getElementById("pin").value.trim();

    if (!usuario || !contrasena || !pin) {
        return mostrarMensaje(
            "resultado",
            "Completa todos los campos",
            "error"
        );
    }

    try {
        const res = await fetch("/clientes/login", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ usuario, contrasena, pin: Number(pin) })
        });

        if (!res.ok) {
            throw new Error("Error HTTP " + res.status);
        }

        const data = await res.json();

        if (!data || !data.usuarioIS) {
            return mostrarMensaje(
                "resultado",
                "Credenciales incorrectas o PIN inválido",
                "error"
            );
        }

        localStorage.setItem("usuario", data.usuarioIS);
        localStorage.setItem("tipo", data.tipo);
        localStorage.setItem("idDB", data.idDB);
        localStorage.setItem(
            "nombre",
            data.nombres + " " + data.apellidos
        );

        if (data.tipo === "ClienteNatural") {
            window.location.href = "/html/dashboard.html";
        } else if (data.tipo === "ClienteAdmin") {
            window.location.href = "/html/admin.html";
        } else {
            mostrarMensaje(
                "resultado",
                "Tipo de usuario no reconocido",
                "error"
            );
        }
    } catch (error) {
        console.error("Error en login:", error);
        mostrarMensaje(
            "resultado",
            "No se pudo iniciar sesión. Revisa el servidor.",
            "error"
        );
    }
}


function registrar() {
    const cliente = {
        nombres: document.getElementById("nombres").value.trim(),
        apellidos: document.getElementById("apellidos").value.trim(),
        id: document.getElementById("id").value.trim(),
        direccion: document.getElementById("direccion").value.trim(),
        telefono: document.getElementById("telefono").value.trim(),
        email: document.getElementById("email").value.trim(),
        usuarioIS: document.getElementById("usuario").value.trim(),
        contrasena: document.getElementById("contrasena").value.trim()
    };

    const soloLetras = /^[a-zA-ZáéíóúÁÉÍÓÚñÑ\s]+$/;
    const soloNumeros = /^[0-9]+$/;
    const usuarioValido = /^[a-zA-Z0-9]+$/;
    const emailValido = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    const passFuerte = /^(?=.*[A-Z])(?=.*\d).{8,}$/;

    if (!cliente.nombres || !soloLetras.test(cliente.nombres)) return mostrarMensaje("resultadoRegistro", "Nombres inválidos (solo letras)", "error");
    if (!cliente.apellidos || !soloLetras.test(cliente.apellidos)) return mostrarMensaje("resultadoRegistro", "Apellidos inválidos", "error");
    if (!cliente.telefono || !soloNumeros.test(cliente.telefono)) return mostrarMensaje("resultadoRegistro", "Teléfono inválido (solo números)", "error");
    if (!cliente.email || !emailValido.test(cliente.email)) return mostrarMensaje("resultadoRegistro", "Correo inválido", "error");
    if (!cliente.usuarioIS || !usuarioValido.test(cliente.usuarioIS)) return mostrarMensaje("resultadoRegistro", "Usuario inválido (solo letras y números)", "error");
    if (!cliente.contrasena || !passFuerte.test(cliente.contrasena)) return mostrarMensaje("resultadoRegistro", "Contraseña débil (mín 8, 1 mayúscula, 1 número)", "error");

   
fetch("/clientes/registro", {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(cliente)
})

    .then(res => { if (!res.ok) throw new Error("Error en registro"); return res.json(); })
    .then(data => { mostrarMensaje("resultadoRegistro", "Cuenta creada PIN: " + data.pinSeguridad, "ok"); })
    .catch(() => { mostrarMensaje("resultadoRegistro", "Error al registrar", "error"); });
}

function toggleDark() { document.body.classList.toggle("dark"); }
