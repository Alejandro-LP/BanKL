function login() {

    const usuario = document.getElementById("usuario").value;
    const contrasena = document.getElementById("contrasena").value;
    const pin = document.getElementById("pin").value;

    fetch(`/api/auth/login?usuario=${usuario}&contrasena=${contrasena}&pin=${pin}`, {
        method: "POST"
    })
    .then(res => res.json())
    .then(data => {

        if (data) {
            document.getElementById("resultado").innerText = "Bienvenido 🔥";
            window.location.href = "dashboard.html";
        } else {
            document.getElementById("resultado").innerText = "Credenciales incorrectas";
        }
    });
}


function toggleDarkMode() {
    document.body.classList.toggle("dark");
}


function irRegistro() {
    window.location.href = "registro.html";
}