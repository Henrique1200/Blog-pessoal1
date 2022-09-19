package com.generation.blogpessoal.repository;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;

import com.generation.blogpessoal.model.UsuarioModel;

//indica que a classe UsuarioRepositoryTest é uma classe de test, é que esse test será rodado em uma porta aleatoria local no meu computador(desde que ela não esteja já sendo usada)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)

//indica que o teste a ser feito vai ser um teste unitario(por classe)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)

public class UsuarioRepositoryTest {
	
	@Autowired
	private UsuarioRepository usuarioRepository;
	
	@BeforeAll
	void start() {
		usuarioRepository.deleteAll();
		
		usuarioRepository.save(new UsuarioModel(0L, "Jose da Costa", "jose@email.com", "123456789", "fototesterr.test"));
		
		usuarioRepository.save(new UsuarioModel(0L, "Ana da Costa", "ana@email.com", "123456789", "fototesterr.test"));
		
		usuarioRepository.save(new UsuarioModel(0L, "Maria da Costa", "maria@email.com", "123456789", "fototesterr.test"));
		
		usuarioRepository.save(new UsuarioModel(0L, "Pedro Aquino", "pedro@email.com", "123456789", "fototesterr.test"));
	}
	
	@Test
	public void return1User() {
		Optional<UsuarioModel> user = usuarioRepository.findByUsuario("jose@email.com");
		
		assertTrue(user.get().getUsuario().equals("jose@email.com"));
	}
	
	@Test
	public void shouldReturn3Users() {
		List<UsuarioModel> usersList = usuarioRepository.findAllByNomeContainingIgnoreCase("Costa");
		assertEquals(3, usersList.size());
		assertTrue(usersList.get(0).getNome().equals("Jose da Costa"));
		assertTrue(usersList.get(1).getNome().equals("Ana da Costa"));
		assertTrue(usersList.get(2).getNome().equals("Maria da Costa"));
	}
	
	@AfterAll
	public void end() {
		usuarioRepository.deleteAll();
	}
	
}
