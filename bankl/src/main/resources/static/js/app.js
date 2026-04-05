// LOGIN
function login() {

    const usuario = document.getElementById("usuario").value;
    const contrasena = document.getElementById("contrasena").value;
    const pin = document.getElementById("pin").value;

    fetch(`http://localhost:8080/clientes/login?usuario=${usuario}&contrasena=${contrasena}&pin=${pin}`, {
        method: "POST"
    })
    .then(res => res.json())
    .then(data => {

        console.log(data); 

        if (data !== null) {

            // Guardar usuario
            localStorage.setItem("usuario", data.usuarioIS);

           
            if (data.tipo === "ClienteNatural") {
                window.location.href = "cuentas.html";
            } else if (data.tipo === "ClienteAdmin") {
                window.location.href = "dashboard.html";
            } else {
                document.getElementById("resultado").innerText = "Tipo de usuario desconocido";
            }

        } else {
            document.getElementById("resultado").innerText = "Credenciales incorrectas o PIN inválido";
        }
    })
    .catch(() => {
        document.getElementById("resultado").innerText = "Error";
    });
}


// REGISTRO
function registrar() {

    const cliente = {
        nombres: document.getElementById("nombres").value,
        apellidos: document.getElementById("apellidos").value,
        id: document.getElementById("id").value,
        direccion: document.getElementById("direccion").value,
        telefono: document.getElementById("telefono").value,
        email: document.getElementById("email").value,
        usuarioIS: document.getElementById("usuario").value,
        contrasena: document.getElementById("contrasena").value
    };

    fetch("http://localhost:8080/clientes/registro", {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify(cliente)
    })
    .then(res => res.json())
    .then(data => {
        document.getElementById("resultadoRegistro").innerText =
            "Cuenta creada. PIN: " + data.pinSeguridad;
    })
    .catch(error => {
        console.error(error);
        document.getElementById("resultadoRegistro").innerText =
            "Error al registrar";
    });
}


// DARK MODE
function toggleDark() {
    document.body.classList.toggle("dark");
}