# Simulador de segurança e complexidade de senhas

Projeto A3 · Matemática Computacional Aplicada · UNIFACS 2026.2

Mostra, em tempo real, o nível de segurança de uma senha (muito fraco, fraco, médio, forte ou muito forte) a partir de L (comprimento), N (tamanho do alfabeto), C = N^L (combinações) e T = C ÷ V (tempo de força bruta, com V = 10⁶ tentativas por segundo).

## Estrutura

```
simulador-senhas/
├── simulador-senhas-frontend/     Tela (HTML, CSS e JavaScript)
│   ├── index.html
│   ├── css/style.css
│   └── js/
│       ├── script.js              Eventos da tela e chamada à API
│       └── mock.js                Simulação temporária do back-end
└── simulador-senhas-backend/      API em Java (Spring Boot + Maven)
    ├── pom.xml
    ├── mvnw.cmd e .mvn/           Maven Wrapper (Windows)
    ├── README.md                  Detalhes do back-end e contrato da API
    └── src/
        ├── main/                  Código e configuração
        └── test/                  Testes automatizados
```

## Como rodar

**Só o front-end (modo simulado).** Abra `simulador-senhas-frontend/index.html` com a extensão Live Server do VS Code. O `index.html` carrega o `js/mock.js`, que responde no lugar do back-end.

**Com o back-end Java.**
1. Na pasta `simulador-senhas-backend`, rode `.\mvnw.cmd spring-boot:run` (precisa de JDK 17 ou superior).
2. No `simulador-senhas-frontend/index.html`, remova a linha `<script src="js/mock.js"></script>`.
3. Abra o front-end com o Live Server na porta 5500 (o back-end só aceita essa origem).

Mais detalhes, o formato do JSON e os testes estão em `simulador-senhas-backend/README.md`.
