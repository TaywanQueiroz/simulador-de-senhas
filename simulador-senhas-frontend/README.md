# Simulador de segurança e complexidade de senhas — Front-end

Projeto A3 · Matemática Computacional Aplicada · UNIFACS 2026.2

Interface em HTML, CSS e JavaScript. O **Java faz todo o cálculo** (tipos de caractere, N, Nᴸ, T e nível de segurança), e o front-end só envia a senha e exibe o resultado.

## Divisão de responsabilidades

| Back-end (Java) | Front-end (HTML/CSS/JS) |
|---|---|
| Detecta os tipos de caractere | Campo de senha e botão mostrar/ocultar |
| Calcula N, Nᴸ e T = Nᴸ ÷ V | Envia a senha para a API |
| Decide o nível de segurança | Escreve o resultado na tela |
| Formata combinações e tempo em texto | Troca cores e barra conforme o nível |

## Como rodar

1. Suba o back-end Java (por padrão em `http://localhost:8080`).
2. Abra o `index.html` com a extensão **Live Server** do VS Code.
3. Se a API estiver em outro endereço, altere `API_URL` no topo de `js/script.js`.

Sem o back-end no ar, a tela mostra "Sem conexão".

## Contrato da API

**POST `/api/analisar`**

Requisição:

```json
{ "senha": "senha123" }
```

Resposta (HTTP 200):

```json
{
  "nivel": "MEDIO",
  "comprimento": 8,
  "tipos": { "digitos": true, "minusculas": true, "maiusculas": false, "simbolos": false },
  "tamanhoAlfabeto": 36,
  "combinacoes": "2,82 × 10^12",
  "tempoEstimado": "≈ 32,6 dias"
}
```

| Campo | Tipo | Descrição |
|---|---|---|
| `nivel` | texto | `MUITO_FRACO`, `FRACO`, `MEDIO`, `FORTE` ou `MUITO_FORTE` |
| `comprimento` | número | L |
| `tipos` | objeto | quatro campos true/false: `digitos`, `minusculas`, `maiusculas`, `simbolos` |
| `tamanhoAlfabeto` | número | N |
| `combinacoes` | texto | Nᴸ já formatado para exibir |
| `tempoEstimado` | texto | T já formatado para exibir |

## Níveis (proposta, a confirmar com o grupo)

Com V = 10⁶ tentativas/s e T = Nᴸ ÷ V:

| Nível | Regra |
|---|---|
| `MUITO_FRACO` | T < 3.600 s (menos de 1 hora) |
| `FRACO` | 3.600 s ≤ T < 86.400 s (até menos de 1 dia) |
| `MEDIO` | 86.400 s ≤ T < 31.536.000 s (de 1 dia até menos de 1 ano) |
| `FORTE` | 31.536.000 s ≤ T < 3.153.600.000 s (de 1 ano até menos de 100 anos) |
| `MUITO_FORTE` | T ≥ 3.153.600.000 s (100 anos ou mais) |

## Valores para validar o back-end

| Senha | L | N | Nᴸ | T | Nível |
|---|---|---|---|---|---|
| `123456` | 6 | 10 | 1.000.000 | 1 segundo | `MUITO_FRACO` |
| `senha123` | 8 | 36 | 2.821.109.907.456 | ≈ 32,6 dias | `MEDIO` |
| `T4x@zQ9!` | 8 | 94 | 6.095.689.385.410.816 | ≈ 193 anos | `MUITO_FORTE` |
| `Abc` | 3 | 52 | 140.608 | menos de 1 segundo | `MUITO_FRACO` |

## Requisitos para o back-end

- Usar `BigInteger` para Nᴸ (estoura `long` com poucos caracteres). O campo de senha aceita até 128 caracteres.
- Liberar **CORS** para a origem do front, por exemplo `@CrossOrigin(origins = "http://127.0.0.1:5500")` no Spring (o Live Server usa essa porta).
- Responder em JSON com `Content-Type: application/json`.
- Não armazenar nem registrar em log a senha recebida (escopo do relatório).

## Estrutura

```
index.html
css/style.css
js/script.js
README.md
.gitignore
```