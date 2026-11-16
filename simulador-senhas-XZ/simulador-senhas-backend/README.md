# Simulador de senhas

Backend em Java do projeto A3 de Matemática Computacional Aplicada. Recebe a senha digitada no frontend, calcula as combinações possíveis e devolve uma estimativa de tempo de força bruta e o nível de segurança.

O projeto usa Spring Boot e precisa de um JDK 17 ou superior. Não usa banco de dados e não salva nem escreve a senha recebida nos logs da aplicação.

## Rodar o projeto

Na pasta `simulador-senhas-backend`, com `JAVA_HOME` apontando para o JDK, execute:

```powershell
.\mvnw.cmd spring-boot:run
```

O Maven Wrapper baixa o Maven e as dependências na primeira execução, por isso precisa de internet. No Linux ou macOS, use `mvn spring-boot:run` com o Maven instalado, pois o script `mvnw` não está incluído no projeto. Para abrir no NetBeans ou IntelliJ, importe o `pom.xml` como projeto Maven.

A API atende em `http://localhost:8080`, apenas nesta máquina. Abra o `index.html` de `simulador-senhas-frontend` com o Live Server na porta 5500.

O controller aceita as origens `http://localhost:5500` e `http://127.0.0.1:5500`. Se mudar a porta do Live Server, ajuste `@CrossOrigin` em `SenhaController.java`. O `index.html` ainda carrega `js/mock.js`, que simula a API. Para usar este back-end de verdade, remova a linha `<script src="js/mock.js"></script>` do `index.html`.

## O que cada classe faz

| Classe | Função |
|---|---|
| `Senha` | Valida o comprimento e identifica os tipos de caracteres. Guarda essas características, sem guardar o texto da senha. |
| `CalculadoraSeguranca` | Calcula combinações, tempo máximo e tempo médio. Formata as combinações e o tempo para exibição. |
| `ClassificadorSenha` | Compara o tempo máximo com as faixas de segurança. |
| `ResultadoAnalise` | Agrupa os seis campos que o frontend espera receber, com atributos privados e getters. |
| `SenhaController` | Recebe o JSON em `/api/analisar`, chama as classes de cálculo e devolve o resultado ou um erro de entrada. |
| `SimuladorSenhasApplication` | Inicia a aplicação. |

## Cálculos e classificação

O comprimento da senha é `L`. Para calcular `N`, o tamanho do alfabeto, cada tipo presente soma uma vez: 10 para dígitos, 26 para minúsculas, 26 para maiúsculas e 32 para símbolos.

```text
Combinações:  C = N^L
Tempo máximo: T = C / V
Tempo médio:  C / (2 × V)
```

Usamos `V = 1.000.000` tentativas por segundo e um ano de 365 dias. A API exibe o tempo máximo e usa esse valor para classificar a senha. O método `calcularTempoMedio` fica disponível na calculadora, mas não faz parte da resposta JSON.

`BigInteger` permite calcular as combinações de senhas de até 128 caracteres sem estourar um `long`. Os tempos usam `BigDecimal` para preservar as frações de segundo. Na exibição, o tempo tem até uma casa decimal e as combinações grandes usam notação científica com três algarismos, truncados como no mock do frontend.

| Nível | Tempo máximo |
|---|---|
| `MUITO_FRACO` | Menos de 1 hora |
| `FRACO` | De 1 hora até menos de 1 dia |
| `MEDIO` | De 1 dia até menos de 1 ano |
| `FORTE` | De 1 ano até menos de 100 anos |
| `MUITO_FORTE` | 100 anos ou mais |

Os documentos do grupo trazem propostas diferentes de velocidade e classificação. Aqui seguimos o frontend: cinco níveis e um milhão de tentativas por segundo.

## Chamar a API

Envie um `POST /api/analisar` com `Content-Type: application/json`:

```json
{ "senha": "senha123" }
```

A resposta HTTP 200 tem este formato:

```json
{
  "nivel": "MEDIO",
  "comprimento": 8,
  "tipos": {
    "digitos": true,
    "minusculas": true,
    "maiusculas": false,
    "simbolos": false
  },
  "tamanhoAlfabeto": 36,
  "combinacoes": "2,82 × 10^12",
  "tempoEstimado": "≈ 32,7 dias"
}
```

`senha` deve ser texto de 1 a 128 caracteres. Espaços contam e não são removidos. Se o JSON estiver malformado ou a senha estiver ausente, nula, vazia, com outro tipo ou acima do limite, a API responde HTTP 400:

```json
{ "erro": "Envie um JSON com o campo senha contendo de 1 a 128 caracteres." }
```

Para testar pelo PowerShell com o servidor ligado:

```powershell
Invoke-RestMethod -Method Post -Uri http://localhost:8080/api/analisar -ContentType 'application/json' -Body '{"senha":"senha123"}'
```

## Testes e executável

```powershell
.\mvnw.cmd test
.\mvnw.cmd package
java -jar target/simulador-senhas-backend-1.0.0.jar
```

Os testes verificam os exemplos do frontend, os limites de classificação, a precisão decimal, o comprimento máximo, a formatação, as entradas inválidas e o CORS.

## Limites do simulador

O cálculo considera os grupos `0-9`, `a-z`, `A-Z` e qualquer outro caractere como símbolo. O comprimento segue `String.length` do JavaScript, em unidades UTF-16. O alfabeto de 94 caracteres representa ASCII imprimível; senhas com acentos ou emojis continuam usando esse mesmo modelo, sem um cálculo próprio para Unicode.

A estimativa mede o tempo para percorrer todo o espaço de busca na velocidade escolhida. Ela não detecta senhas vazadas, palavras de dicionário ou sequências previsíveis. Use senhas fictícias na apresentação.

O frontend consulta a API 250 ms depois que a pessoa para de digitar. Essa comunicação usa HTTP. O botão de criar conta é apenas visual: este projeto faz a análise da senha, sem cadastrar usuários.


## Campos usados na comunicacao com a interface

O front-end envia uma senha para `POST /api/analisar`. O back-end faz a analise e
devolve um objeto `ResultadoAnalise` em JSON. Os nomes abaixo devem ser mantidos
para que o JavaScript consiga mostrar os resultados sem precisar mudar os campos.

| Campo no JSON | Tipo | De onde vem | Uso na interface |
|---|---|---|---|
| `nivel` | `String` | `ClassificadorSenha` | Define o texto e a cor do nivel |
| `comprimento` | `int` | `Senha.getComprimento()` | Mostra o tamanho da senha |
| `tipos.digitos` | `boolean` | `Senha.getTipos()` | Marca a presenca de numeros |
| `tipos.minusculas` | `boolean` | `Senha.getTipos()` | Marca letras minusculas |
| `tipos.maiusculas` | `boolean` | `Senha.getTipos()` | Marca letras maiusculas |
| `tipos.simbolos` | `boolean` | `Senha.getTipos()` | Marca simbolos |
| `tamanhoAlfabeto` | `int` | `Senha.getTamanhoAlfabeto()` | Mostra o valor de N |
| `combinacoes` | `String` | `CalculadoraSeguranca.formatarCombinacoes()` | Exibe N elevado a L |
| `tempoEstimado` | `String` | `CalculadoraSeguranca.formatarTempo()` | Exibe o tempo maximo |

### Como os dados passam pelas classes

1. `SenhaController` recebe o JSON com o campo `senha`.
2. `Senha` identifica o comprimento, os tipos e o tamanho do alfabeto.
3. `CalculadoraSeguranca` calcula as combinacoes e o tempo de forca bruta.
4. `ClassificadorSenha` define o nivel de seguranca.
5. `ResultadoAnalise` junta os valores e seus getters permitem gerar a resposta JSON.

Os atributos de `ResultadoAnalise` sao privados. Os getters servem para
disponibilizar os resultados sem deixar os atributos acessiveis diretamente.

O metodo `calcularTempoMedio()` existe na calculadora, mas nao e enviado
na resposta da API. A interface utiliza o tempo maximo (`tempoEstimado`).

**Observacao para a integracao:** o front-end do repositorio ainda carrega
`js/mock.js`, que simula a resposta da API. Quem for fazer a integracao
precisa retirar essa simulacao e testar a comunicacao real com o servidor.
Essa etapa nao faz parte desta entrega do back-end.
