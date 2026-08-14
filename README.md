# Java PKCE Library

Biblioteca Java para implementação do fluxo **PKCE (Proof Key for Code Exchange)**, conforme definido na **RFC 7636**.

Este projeto foi desenvolvido como parte de um **Trabalho de Conclusão de Curso (TCC)**, com o objetivo de estudar e implementar o fluxo de autorização PKCE e automatizar um ambiente de testes utilizando **Java, Keycloak e Docker**.

## Funcionalidades

A biblioteca permite:

- Geração segura do `code_verifier`;
- Geração do `code_challenge`;
- Suporte aos métodos `S256` e `plain`;
- Geração da URL de autorização;
- Geração do payload para troca do `authorization_code`;
- Requisição ao endpoint `/token`.

## Requisitos

- Java 11+
- Gradle

## Build

Para limpar e compilar o projeto:

```bash
./gradlew clean build
```

## Publicação no Maven Local

Para limpar o projeto e publicar a biblioteca no repositório Maven local:

```bash
./gradlew clean publishToMavenLocal
```

A biblioteca ficará disponível no repositório local:

```text
~/.m2/repository/br/com/vanessacardoso/pkce/
```

## Utilização

Exemplo básico:

```java
PKCEClient client = new PKCEClient();

String authorizationUrl = client.generateAuthorizationUrl(
        authorizationEndpoint,
        clientId,
        redirectUri
);

System.out.println(authorizationUrl);
```

Após receber o `authorization_code`:

```java
String token = client.getToken(
        authorizationCode,
        clientId,
        redirectUri,
        tokenEndpoint
);
```

## Configuração do método PKCE

O método utilizado pela biblioteca pode ser definido no arquivo `pkce.properties`:

```properties
pkce.method=S256
```

Os métodos disponíveis são:

```text
S256
PLAIN
```

Caso nenhuma configuração seja encontrada, a biblioteca utiliza **S256** como padrão.

## Ambiente de Testes

O projeto possui um ambiente de integração com **Keycloak**, executado em Docker, além de uma aplicação Java e um `CallbackServer` utilizados para validar o fluxo completo:

```text
Aplicação → Biblioteca PKCE → Keycloak → Callback → Troca do Code → Token
```

O ambiente permite testar na prática a geração do `code_verifier`, `code_challenge`, autorização do usuário e obtenção dos tokens.