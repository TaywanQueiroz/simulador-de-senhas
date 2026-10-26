// ===== Elementos da página =====
const campoSenha = document.getElementById("senha");
const botaoToggle = document.getElementById("toggle");

// ===== Ouvindo o que a pessoa digita =====
campoSenha.addEventListener("input", function () {
  const senha = campoSenha.value;
  console.log("Digitou:", senha, "| tamanho:", senha.length);
});

// ===== Botão mostrar/ocultar senha =====
botaoToggle.addEventListener("click", function () {
  const estaOculta = campoSenha.type === "password";
  campoSenha.type = estaOculta ? "text" : "password";
  botaoToggle.textContent = estaOculta ? "Ocultar" : "Mostrar";
});