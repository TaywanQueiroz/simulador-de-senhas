// ===== Elementos da página =====
const campoSenha = document.getElementById("senha");
const botaoToggle = document.getElementById("toggle");

// ===== Constante do modelo (seção 9 do relatório) =====
const V = 1e6; // tentativas por segundo

// ===== Análise da senha =====
function analisarTipos(senha) {
  return {
    digitos: /[0-9]/.test(senha),
    minusculas: /[a-z]/.test(senha),
    maiusculas: /[A-Z]/.test(senha),
    simbolos: /[^A-Za-z0-9]/.test(senha),
  };
}

function calcularAlfabeto(tipos) {
  let n = 0;
  if (tipos.digitos) n += 10;
  if (tipos.minusculas) n += 26;
  if (tipos.maiusculas) n += 26;
  if (tipos.simbolos) n += 32;
  return n;
}

function calcularCombinacoes(N, L) {
  return BigInt(N) ** BigInt(L);
}

function calcularTempo(C) {
  return Number(C) / V;
}

// ===== Ouvindo o que a pessoa digita =====
campoSenha.addEventListener("input", function () {
  const senha = campoSenha.value;
  const L = senha.length;

  if (L === 0) {
    console.log("campo vazio");
    return;
  }

  const tipos = analisarTipos(senha);
  const N = calcularAlfabeto(tipos);
  const C = calcularCombinacoes(N, L);
  const T = calcularTempo(C);

  console.log("L =", L, "| N =", N, "| C =", String(C), "| T =", T, "s");
});

// ===== Botão mostrar/ocultar senha =====
botaoToggle.addEventListener("click", function () {
  const estaOculta = campoSenha.type === "password";
  campoSenha.type = estaOculta ? "text" : "password";
  botaoToggle.textContent = estaOculta ? "Ocultar" : "Mostrar";
});