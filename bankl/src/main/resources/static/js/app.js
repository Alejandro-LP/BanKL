function mostrarMensaje(id, msg, tipo) {
    const div = document.getElementById(id);

    div.innerText = msg;
    div.style.padding = "10px";
    div.style.marginTop = "10px";
    div.style.borderRadius = "8px";
    div.style.fontWeight = "bold";

    if (tipo === "error") {
        div.style.backgroundColor = "#ff4d4d";
        div.style.color = "white";
    } else {
        div.style.backgroundColor = "#28a745";
        div.style.color = "white";
    }
}


function login() {

    const usuario = document.getElementById("usuario").value.trim();
    const contrasena = document.getElementById("contrasena").value.trim();
    const pin = document.getElementById("pin").value.trim();

    if (!usuario || !contrasena || !pin) {
        return mostrarMensaje("resultado", "Completa todos los campos ", "error");
    }

    fetch(`http://localhost:8080/clientes/login?usuario=${usuario}&contrasena=${contrasena}&pin=${pin}`, {
        method: "POST"
    })
    .then(res => res.json())
    .then(data => {

        if (data !== null) {

            localStorage.setItem("usuario", data.usuarioIS);
            localStorage.setItem("tipo", data.tipo);

            if (data.tipo === "ClienteNatural") {
            window.location.href = "/html/cuentas.html";
            } else if (data.tipo === "ClienteAdmin") {
            window.location.href = "/html/admin.html";
}

        } else {
            mostrarMensaje("resultado", "Credenciales incorrectas o PIN inválido ", "error");
        }
    })
    .catch(() => {
        mostrarMensaje("resultado", "Error en el servidor", "error");
    });
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

    if (!cliente.nombres || !soloLetras.test(cliente.nombres)) {
        return mostrarMensaje("resultadoRegistro", "Nombres inválidos (solo letras)", "error");
    }

    if (!cliente.apellidos || !soloLetras.test(cliente.apellidos)) {
        return mostrarMensaje("resultadoRegistro", "Apellidos inválidos", "error");
    }

    if (!cliente.telefono || !soloNumeros.test(cliente.telefono)) {
        return mostrarMensaje("resultadoRegistro", "Teléfono inválido (solo números)", "error");
    }

    if (!cliente.email || !emailValido.test(cliente.email)) {
        return mostrarMensaje("resultadoRegistro", "Correo inválido ", "error");
    }

    if (!cliente.usuarioIS || !usuarioValido.test(cliente.usuarioIS)) {
        return mostrarMensaje("resultadoRegistro", "Usuario inválido (solo letras y números)", "error");
    }

    if (!cliente.contrasena || !passFuerte.test(cliente.contrasena)) {
        return mostrarMensaje("resultadoRegistro", "Contraseña débil (mín 8, 1 mayúscula, 1 número) ", "error");
    }

    
    fetch("http://localhost:8080/clientes/registro", {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify(cliente)
    })
    .then(res => {
        if (!res.ok) throw new Error("Error en registro");
        return res.json();
    })
    .then(data => {
        mostrarMensaje("resultadoRegistro", "Cuenta creada PIN: " + data.pinSeguridad, "ok");
    })
    .catch(() => {
        mostrarMensaje("resultadoRegistro", "Error al registrar ", "error");
    });
}



function toggleDark() {
    document.body.classList.toggle("dark");
}