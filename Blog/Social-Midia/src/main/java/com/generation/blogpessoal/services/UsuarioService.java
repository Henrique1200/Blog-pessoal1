package com.generation.blogpessoal.services;

import java.nio.charset.Charset;
import java.util.Optional;

import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.generation.blogpessoal.model.UsuarioLogin;
import com.generation.blogpessoal.model.UsuarioModel;
import com.generation.blogpessoal.repository.UsuarioRepository;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    
    private String criptografarSenha(String senha) {
       
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        
        return encoder.encode(senha);
    }

 
    public Optional<UsuarioModel> cadastrarUsuario(UsuarioModel usuario) {

    
        if (usuarioRepository.findByUsuario(usuario.getUsuario()).isPresent())
            return Optional.empty();

       
        usuario.setSenha(criptografarSenha(usuario.getSenha()));

        
        return Optional.of(usuarioRepository.save(usuario));

    }
    
 // função que atualiza um usuario já cadastrado
 	public Optional<UsuarioModel> atualizarUsuario(UsuarioModel usuario) {
 		// verifica se o id passado no objeto de usuário já existe para poder fazer a atualização, sem o id ele não atualiza
 		if (usuarioRepository.findById(usuario.getId()).isPresent()) {
 			// depois verifica pelo email digitado do usuario se ele já está cadastrado no meu banco de dados
 			Optional<UsuarioModel> buscaUsuario = usuarioRepository.findByUsuario(usuario.getUsuario());
 			// valida se o usuario está presente E se o id passado não é diferente do cadastrado no banco de dados
 			if ((buscaUsuario.isPresent()) && (buscaUsuario.get().getId() != usuario.getId()))
 				throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Usuário já existe!", null);
 			
 			// criptografa novamente a senha do usuario antes de mandar para o banco
 			usuario.setSenha(criptografarSenha(usuario.getSenha()));
 			// e por fim, insere o objeto de usuario atualizado no banco de dados 
 			return Optional.ofNullable(usuarioRepository.save(usuario));
 		}

 		return Optional.empty();
 	}
 	
 	public Optional<UsuarioLogin> autenticarUsuario(Optional<UsuarioLogin> usuarioLogin) {

 		Optional<UsuarioModel> usuario = usuarioRepository.findByUsuario(usuarioLogin.get().getUsuario());

 		if (usuario.isPresent()) {

 			if (compararSenhas(usuarioLogin.get().getSenha(), usuario.get().getSenha())) {

 				usuarioLogin.get().setId(usuario.get().getId());
 				usuarioLogin.get().setNome(usuario.get().getNome());
 				usuarioLogin.get().setFoto(usuario.get().getFoto());
 				usuarioLogin.get().setToken(gerarBasicToken(usuarioLogin.get().getUsuario(), 		usuarioLogin.get().getSenha()));
 				usuarioLogin.get().setSenha(usuario.get().getSenha());

 				return usuarioLogin;

 			}
 		}	

 		return Optional.empty();
 		
 	}
 	
 	
 	private boolean compararSenhas(String senhaDigitada, String senhaBanco) {
 		
 		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
 		
 		return encoder.matches(senhaDigitada, senhaBanco);

 	}

 	private String gerarBasicToken(String usuario, String senha) {

 		String token = usuario + ":" + senha;
 		byte[] tokenBase64 = Base64.encodeBase64(token.getBytes(Charset.forName("US-ASCII")));
 		return "Basic " + new String(tokenBase64);

 	}
}
