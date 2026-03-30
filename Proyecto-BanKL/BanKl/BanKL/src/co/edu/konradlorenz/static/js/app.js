// LOGIN
function login() {

    const usuario = document.getElementById("usuario").value;
    const contrasena = document.getElementById("contrasena").value;
    const pin = document.getElementById("pin").value;

    fetch("/api/auth/login", {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify({ usuario, contrasena, pin })
    })
    .then(res => res.json())
    .then(data => {
        if (data === true) {
            window.location.href = "dashboard.html";
        } else {
            document.getElementById("resultado").innerText = "Credenciales incorrectas ❌";
        }
    })
    .catch(() => {
        document.getElementById("resultado").innerText = "Error ⚠️";
    });
}


// REGISTRO
function registrar() {

    const nombre = document.getElementById("nombre").value;
    const apellido = document.getElementById("apellido").value;
    const usuario = document.getElementById("usuarioR").value;
    const contrasena = document.getElementById("contrasenaR").value;
    const pin = document.getElementById("pinR").value;

    fetch("/api/auth/registro", {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify({ nombre, apellido, usuario, contrasena, pin })
    })
    .then(res => res.json())
    .then(data => {
        if (data === true) {
            document.getElementById("resultadoRegistro").innerText = "Cuenta creada ✅";
        } else {
            document.getElementById("resultadoRegistro").innerText = "Error al registrar ❌";
        }
    })
    .catch(() => {
        document.getElementById("resultadoRegistro").innerText = "Error ⚠️";
    });
}


// DARK MODE
function toggleDark() {
    document.body.classList.toggle("dark");
}