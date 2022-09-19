package com.generation.blogpessoal.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.generation.blogpessoal.model.UsuarioModel;

public interface UsuarioRepository extends JpaRepository<UsuarioModel, Long> {
	
	public Optional<UsuarioModel> findByUsuario(String usuario);
	
	public List<UsuarioModel> findAllByNomeContainingIgnoreCase(String name);
}
