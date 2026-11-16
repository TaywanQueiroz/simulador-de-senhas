// ===== Configuração =====
const API_URL = "http://localhost:8080/api/analisar";

// ===== Elementos da página =====
const card = document.getElementById("card");
const campoSenha = document.getElementById("senha");
const botaoToggle = document.getElementById("toggle");
const botaoEnviar = document.getElementById("enviar");
const badge = document.getElementById("badge");
const rComprimento = document.getElementById("r-comprimento");
const rAlfabeto = document.getElementById("r-alfabeto");
const rCombinacoes = document.getElementById("r-combinacoes");
const rTempo = document.getElementById("r-tempo");
const chips = document.querySelectorAll("#r-tipos span");

// Texto do badge para cada código de nível
const NOMES = {
  MUITO_FRACO: "Muito fraco",
  FRACO: "Fraco",
  MEDIO: "Médio",
  FORTE: "Forte",
  MUITO_FORTE: "Muito forte",
};

// ===== Comunicação com o Java =====
async function consultarAPI(senha) {
  const resposta = await fetch(API_URL, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ senha: senha }),
  });
  if (!resposta.ok) {
    throw new Error("Erro " + resposta.status);
  }
  return resposta.json();
}

// ===== Atualizando a tela (sem nenhum cálculo) =====
function exibirResultado(r) {
  card.dataset.nivel = r.nivel;
  badge.textContent = NOMES[r.nivel];
  rComprimento.textContent = r.comprimento;
  rAlfabeto.textContent = r.tamanhoAlfabeto;
  rCombinacoes.textContent = r.combinacoes;
  rTempo.textContent = r.tempoEstimado;

  chips.forEach(function (chip) {
    chip.classList.toggle("ativo", Boolean(r.tipos[chip.dataset.t]));
  });

  botaoEnviar.disabled = false;
}

function limparResultado() {
  card.dataset.nivel = "vazio";
  badge.textContent = "—";
  rComprimento.textContent = "—";
  rAlfabeto.textContent = "—";
  rCombinacoes.textContent = "—";
  rTempo.textContent = "—";
  chips.forEach(function (chip) {
    chip.classList.remove("ativo");
  });
  botaoEnviar.disabled = true;
}

function exibirErro() {
  limparResultado();
  badge.textContent = "Sem conexão";
}

// ===== Ouvindo o que a pessoa digita =====
let temporizador;
let ultimaConsulta = 0;

campoSenha.addEventListener("input", function () {
  clearTimeout(temporizador);
  const senha = campoSenha.value;

  if (senha === "") {
    ultimaConsulta++;
    limparResultado();
    return;
  }

  temporizador = setTimeout(async function () {
    const numero = ++ultimaConsulta;
    try {
      const resultado = await consultarAPI(senha);
      if (numero === ultimaConsulta) {
        exibirResultado(resultado);
      }
    } catch (erro) {
      console.error(erro);
      if (numero === ultimaConsulta) {
        exibirErro();
      }
    }
  }, 250);
});

// ===== Botão mostrar/ocultar senha =====
botaoToggle.addEventListener("click", function () {
  const estaOculta = campoSenha.type === "password";
  campoSenha.type = estaOculta ? "text" : "password";
  botaoToggle.textContent = estaOculta ? "Ocultar" : "Mostrar";
});