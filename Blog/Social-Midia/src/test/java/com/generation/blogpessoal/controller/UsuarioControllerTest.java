package com.generation.blogpessoal.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Optional;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.generation.blogpessoal.model.UsuarioModel;
import com.generation.blogpessoal.repository.UsuarioRepository;
import com.generation.blogpessoal.services.UsuarioService;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class UsuarioControllerTest {
	
	// serve para ter acesso aos verbos http (get post put e delete) em modo teste
	@Autowired
	private TestRestTemplate testRestTemplate;
	
	// serve para conseguirmos usar as funções de serviço do usuario
	@Autowired
	private UsuarioService usuarioService;
	
	// serve para o acesso no banco de dados 'h2'
	@Autowired
	private UsuarioRepository usuarioRepository;
	
	// antes de começar o teste, limpa o banco de dados h2 e cadastra um usuário padrão para passar pela segurança 
	@BeforeAll
	void start(){

		usuarioRepository.deleteAll();

		usuarioService.cadastrarUsuario(new UsuarioModel(0L, 
			"Root", "root@root.com", "rootroot", " "));

	}
	
	// indica que o código abaixo será um teste 
	@Test
	// indica um nome de exibição para o teste no console do Junit
	@DisplayName("Cadastrar Um Usuário")
	public void deveCriarUmUsuario() {
		
		// define o que eu estou mandando para minha api enviar para o banco de dados
		HttpEntity<UsuarioModel> Requisicao = new HttpEntity<UsuarioModel>(new UsuarioModel(0L, 
			"Paulo Antunes", "paulo_antunes@email.com.br", "13465278", "https://i.imgur.com/JR7kUFU.jpg"));

		// define o que eu vou obter como resposta do que foi 'persistido' no banco de dados 'h2'
		ResponseEntity<UsuarioModel> Resposta = testRestTemplate
			.exchange("/usuarios/cadastrar", HttpMethod.POST, Requisicao, UsuarioModel.class);

		// faço a verificação se o status http da resposta foi igual a 201 Created
		assertEquals(HttpStatus.CREATED, Resposta.getStatusCode());
		
		// faço a verificação se o que eu mandei de nome do usuario foi o que efetivamente chegou no banco de dados
		assertEquals(Requisicao.getBody().getNome(), Resposta.getBody().getNome());
		
		// faço a verificação se o que eu mandei de email do usuario foi o que efetivamente chegou no banco de dados
		assertEquals(Requisicao.getBody().getUsuario(), Resposta.getBody().getUsuario());
		
	}
	
	@Test
	@DisplayName("Não deve permitir duplicação do Usuário")
	
	public void naoDeveDuplicarUsuario() {

		usuarioService.cadastrarUsuario(new UsuarioModel(0L, 
			"Maria da Silva", "maria_silva@email.com.br", "13465278", "https://i.imgur.com/T12NIp9.jpg"));

		HttpEntity<UsuarioModel> corpoRequisicao = new HttpEntity<UsuarioModel>(new UsuarioModel(0L, 
			"Maria da Silva", "maria_silva@email.com.br", "13465278", "https://i.imgur.com/T12NIp9.jpg"));

		ResponseEntity<UsuarioModel> corpoResposta = testRestTemplate
			.exchange("/usuarios/cadastrar", HttpMethod.POST, corpoRequisicao, UsuarioModel.class);

		assertEquals(HttpStatus.BAD_REQUEST, corpoResposta.getStatusCode());
	}
	
	@Test
	@DisplayName("Atualizar um Usuário")
	public void deveAtualizarUmUsuario() {

		Optional<UsuarioModel> usuarioCadastrado = usuarioService.cadastrarUsuario(new UsuarioModel(0L, 
			"Juliana Andrews", "juliana_andrews@email.com.br", "juliana123", "https://i.imgur.com/yDRVeK7.jpg"));

		UsuarioModel usuarioUpdate = new UsuarioModel(usuarioCadastrado.get().getId(), 
			"Juliana Andrews Ramos", "juliana_ramos@email.com.br", "juliana123" , "https://i.imgur.com/yDRVeK7.jpg");
		
		HttpEntity<UsuarioModel> corpoRequisicao = new HttpEntity<UsuarioModel>(usuarioUpdate);

		ResponseEntity<UsuarioModel> corpoResposta = testRestTemplate
			.withBasicAuth("root@root.com", "rootroot")
			.exchange("/usuarios/atualizar", HttpMethod.PUT, corpoRequisicao, UsuarioModel.class);

		assertEquals(HttpStatus.OK, corpoResposta.getStatusCode());
		assertEquals(corpoRequisicao.getBody().getNome(), corpoResposta.getBody().getNome());
		assertEquals(corpoRequisicao.getBody().getUsuario(), corpoResposta.getBody().getUsuario());
	}
	
	@Test
	@DisplayName("Listar todos os Usuários")
	public void deveMostrarTodosUsuarios() {

		usuarioService.cadastrarUsuario(new UsuarioModel(0L, 
			"Sabrina Sanches", "sabrina_sanches@email.com.br", "sabrina123", "https://i.imgur.com/5M2p5Wb.jpg"));
		
		usuarioService.cadastrarUsuario(new UsuarioModel(0L, 
			"Ricardo Marques", "ricardo_marques@email.com.br", "ricardo123", "https://i.imgur.com/Sk5SjWE.jpg"));

		ResponseEntity<String> resposta = testRestTemplate
		.withBasicAuth("root@root.com", "rootroot")
			.exchange("/usuarios/all", HttpMethod.GET, null, String.class);

		assertEquals(HttpStatus.OK, resposta.getStatusCode());

	}
}
