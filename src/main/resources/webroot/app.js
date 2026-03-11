const button = document.getElementById("checkBtn");

const overlay = document.createElement("div");
overlay.style.position = "fixed";
overlay.style.top = "0";
overlay.style.left = "0";
overlay.style.width = "100vw";
overlay.style.height = "100vh";
overlay.style.background = "rgba(0,0,0,0.7)";
overlay.style.zIndex = "998";
overlay.style.opacity = "0";
overlay.style.transition = "opacity 0.3s ease";
overlay.style.display = "none";
document.body.appendChild(overlay);

const popup = document.createElement("div");
popup.style.position = "fixed";
popup.style.top = "50%";
popup.style.left = "50%";
popup.style.transform = "translate(-50%, -50%)";
popup.style.background = "rgba(15, 15, 20, 0.95)";
popup.style.border = "2px solid #ff7a18";
popup.style.borderRadius = "16px";
popup.style.padding = "1.5rem 2rem";
popup.style.color = "white";
popup.style.fontFamily = "'Inter', sans-serif";
popup.style.fontSize = "0.9rem";
popup.style.fontWeight = "400";
popup.style.maxWidth = "480px";
popup.style.maxHeight = "70vh";
popup.style.overflowY = "auto";
popup.style.boxShadow = "0 10px 40px rgba(255,122,24,0.3)";
popup.style.opacity = "0";
popup.style.transition = "opacity 0.3s ease";
popup.style.zIndex = "999";
popup.style.display = "none";
document.body.appendChild(popup);

const htmlContent = `
<h3>Rutas que se pueden explorar</h3>
<ol>
  <li><a href="http://localhost:8080/greetings?name=[Nombre]" target="_blank">/greetings?name=[Nombre]</a>
      : Devuelve <strong>"Hello [Nombre]"</strong> o <strong>"Hello World"</strong> si no se pasa parámetro
  </li>
  <li><a href="http://localhost:8080/math/constants/pi" target="_blank">/math/constants/pi</a>
      : Devuelve <strong>PI: 3.141592653589793</strong>
  </li>
  <li><a href="http://localhost:8080/" target="_blank">/</a>
      : Devuelve <strong>index.html</strong>
  <li>Cualquier ruta inexistente, por ejemplo <a href="http://localhost:8080/ruta/inexistente" target="_blank">/ruta/inexistente</a>
      : Devuelve <strong>404 Not Found</strong>
  </li>
</ol>
`;

const content = document.createElement("div");
content.innerHTML = htmlContent;
content.style.marginBottom = "1rem";

content.querySelectorAll("a").forEach(a => {
    a.style.color = "#ff7a18";
    a.style.textDecoration = "underline";
});
popup.appendChild(content);

const closeBtn = document.createElement("button");
closeBtn.textContent = "Cerrar";
closeBtn.style.padding = "0.5rem 1rem";
closeBtn.style.border = "none";
closeBtn.style.borderRadius = "12px";
closeBtn.style.background = "#ff3cac";
closeBtn.style.color = "white";
closeBtn.style.cursor = "pointer";
closeBtn.style.fontWeight = "500";
closeBtn.style.transition = "all 0.3s ease";
closeBtn.addEventListener("mouseenter", () => { closeBtn.style.transform = "scale(1.05)"; });
closeBtn.addEventListener("mouseleave", () => { closeBtn.style.transform = "scale(1)"; });
closeBtn.addEventListener("click", () => {
    popup.style.opacity = "0";
    overlay.style.opacity = "0";
    setTimeout(() => {
        popup.style.display = "none";
        overlay.style.display = "none";
    }, 300);
});
popup.appendChild(closeBtn);

button.addEventListener("click", () => {
    overlay.style.display = "block";
    popup.style.display = "block";
    setTimeout(() => {
        overlay.style.opacity = "1";
        popup.style.opacity = "1";
    }, 10);
    button.style.transform = "scale(1.05)";
    setTimeout(() => { button.style.transform = "scale(1)"; }, 200);
});