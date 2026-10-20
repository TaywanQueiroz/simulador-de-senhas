# Simulador de segurança e complexidade de senhas — Front-end

Projeto A3 · Matemática Computacional Aplicada · UNIFACS 2026.2
Interface (HTML + CSS + um pouco de JS) que envia a senha digitada ao back-end Java e mostra a classificação **Fácil / Média / Difícil**.

## Como rodar
Abra `index.html` no navegador (ou use a extensão *Live Server* do VS Code).
Por padrão roda em **modo mock** (cálculo local só para demonstrar a interface).

## Integração com o back-end (Java)
1. Em `js/script.js`, altere `USAR_MOCK: false` e confira `API_URL`.
2. O back-end deve expor **POST `/api/analisar`**:

Requisição
```json
{ "senha": "T4x@zQ9!" }
```
Resposta
```json
{
  "comprimento": 8,
  "tipos": { "digitos": true, "minusculas": true, "maiusculas": true, "simbolos": true },
  "tamanhoAlfabeto": 94,
  "combinacoes": "6095689385410816",
  "tempoSegundos": 6095689385.41,
  "classificacao": "DIFICIL",
  "entropiaBits": 52.4
}
```
- `classificacao`: `"FACIL"` (T < 86.400 s), `"MEDIA"` (até 31.536.000 s) ou `"DIFICIL"`.
- `combinacoes` vai como **string** (use `BigInteger` no Java: Nᴸ estoura `long` rápido).
- Liberar **CORS** para a origem do front (ex.: `@CrossOrigin` no Spring ou header `Access-Control-Allow-Origin`).
- Não armazenar a senha (escopo do relatório).

## Estrutura
```
index.html
css/style.css
js/script.js
```
