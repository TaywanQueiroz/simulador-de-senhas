// ================================================================
// MOCK TEMPORÁRIO: simula o back-end Java.
// Quando o Java estiver pronto: apague este arquivo e a linha
// <script src="js/mock.js"></script> do index.html.
// ================================================================

const V_MOCK = 1e6; // tentativas por segundo

function mockTipos(senha) {
  return {
    digitos: /[0-9]/.test(senha),
    minusculas: /[a-z]/.test(senha),
    maiusculas: /[A-Z]/.test(senha),
    simbolos: /[^A-Za-z0-9]/.test(senha),
  };
}

function mockAlfabeto(tipos) {
  let n = 0;
  if (tipos.digitos) n += 10;
  if (tipos.minusculas) n += 26;
  if (tipos.maiusculas) n += 26;
  if (tipos.simbolos) n += 32;
  return n;
}

function mockNivel(T) {
  if (T < 3600) return "MUITO_FRACO";
  if (T < 86400) return "FRACO";
  if (T < 31536000) return "MEDIO";
  if (T < 3153600000) return "FORTE";
  return "MUITO_FORTE";
}

function mockFormatarCombinacoes(C) {
  const texto = C.toString();
  if (texto.length <= 12) return C.toLocaleString("pt-BR");
  const mantissa = texto[0] + "," + texto.slice(1, 3);
  return mantissa + " × 10^" + (texto.length - 1);
}

function mockFormatarTempo(T) {
  if (!isFinite(T)) return "praticamente infinito";
  if (T < 1) return "menos de 1 segundo";

  const unidades = [
    [31536000, "ano", "anos"],
    [86400, "dia", "dias"],
    [3600, "hora", "horas"],
    [60, "minuto", "minutos"],
    [1, "segundo", "segundos"],
  ];

  for (const [valor, singular, plural] of unidades) {
    if (T >= valor) {
      const q = T / valor;
      if (valor === 31536000 && q >= 1e6) {
        const [m, e] = q.toExponential(2).split("e");
        return "≈ " + m.replace(".", ",") + " × 10^" + Number(e) + " anos";
      }
      const n = q >= 10 ? Math.round(q) : Math.round(q * 10) / 10;
      const texto = n.toLocaleString("pt-BR") + " " + (n === 1 ? singular : plural);
      return valor === 1 ? texto : "≈ " + texto;
    }
  }
}

function mockAnalisar(senha) {
  const tipos = mockTipos(senha);
  const L = senha.length;
  const N = mockAlfabeto(tipos);
  const C = BigInt(N) ** BigInt(L);
  const T = Number(C) / V_MOCK;

  return {
    nivel: mockNivel(T),
    comprimento: L,
    tipos: tipos,
    tamanhoAlfabeto: N,
    combinacoes: mockFormatarCombinacoes(C),
    tempoEstimado: mockFormatarTempo(T),
  };
}

// ===== Intercepta o fetch para a rota da API =====
const fetchOriginal = window.fetch;

window.fetch = async function (url, opcoes) {
  if (!String(url).endsWith("/api/analisar")) {
    return fetchOriginal(url, opcoes);
  }

  await new Promise(function (resolver) {
    setTimeout(resolver, 150); // simula o tempo da rede
  });

  const senha = JSON.parse(opcoes.body).senha;
  return new Response(JSON.stringify(mockAnalisar(senha)), {
    status: 200,
    headers: { "Content-Type": "application/json" },
  });
};